package prglabserver;

import java.util.Arrays;
import java.util.*;
import java.io.*;

/**
 * Класс реализует поток для выполнения команды
 */
public class CommandThread extends Thread{
    
    String   m_sCommand;
    String   []m_args;
    String   m_sDir;
    Process  m_proc;
    boolean  m_bStopped = false;
    HashMap  m_env;
    
    BufferedReader m_stdErr;
    BufferedReader m_stdOut;

	//2008_05_25 vvv
	String         m_stdErrStr = "";
	String         m_stdOutStr = "";
	//2008_05_25 ^^^
    
    String         m_input;

	private long   m_startTime = -1;
	private long   m_execTime = -1;

	private boolean m_bExecutionStarted = false;
    
	/**
	 * Конструктор
	 * @param cmd имя исполняемого файла
	 * @param args аргументы запуска
	 * @param dir домашний каталог для запуска
	 * @param env переменные среды
	 * @param input входной набор для записи во входной поток
	 */
    public CommandThread( String cmd, String []args, String dir, HashMap env, String input ){
        m_sCommand = cmd;
        m_args = Arrays.copyOf(args, args.length);
        m_sDir = dir;
        m_env = env;
        m_input = input;
    }

	/**
	 * Метод для получения реального времени выполнения программы
	 */
	public long getExecTime(){

		if( m_startTime <= 0 ){

			try{ throw new Exception("prglabserver.ProgramThread.getExecTime() - STANGE BEHAVIOR"); }catch( Exception exc ){
				dlc.Logger.log( exc );
			}

			return 0;
		}

		if( m_execTime <= 0 )
			//Hang programs
			return System.currentTimeMillis() - m_startTime;

		return m_execTime;
	}

	/**
	 * Основной метод потока: добавляет переменные среды, перенаправляет потоки ввода/вывода/ошибок и запускает программу,
	 * ожидая завершения
	 */
    public void run(){
        try{
            List<String> commandArgs = new ArrayList();
            commandArgs.add( m_sCommand );
            for( int i = 0; i < m_args.length; i++ )
                commandArgs.add( m_args[i] );
            
            ProcessBuilder pBuilder = new ProcessBuilder( commandArgs );
            pBuilder.directory( new File(m_sDir) );

            HashMap _env = new HashMap( m_env );
            Map map = pBuilder.environment();
            for( Object o : map.keySet() ){
                String name  = ((String)o).toUpperCase();
                String value = (String)map.get( o );
                if( _env.get(name) != null ){
                    map.put( o, _env.get(name) );
                    _env.remove( name );
                }
            }
            map.putAll( _env );

            pBuilder.redirectErrorStream( true );

            m_proc   = pBuilder.start();

//2008_05_25
//            m_stdErr = new BufferedReader( new InputStreamReader( m_proc.getErrorStream() ) );
//            m_stdOut = new BufferedReader( new InputStreamReader( m_proc.getInputStream() ) );

            if( m_input != null ){
                PrintStream ps = new PrintStream( m_proc.getOutputStream() );
                ps.println( m_input );
                ps.flush();
//2008_05_25
				ps.close();
            }

			m_startTime = System.currentTimeMillis();

			m_bExecutionStarted = true;

System.out.println( "Waiting for process " + m_sCommand );

			//2008_05_25 vvv
			final ByteArrayOutputStream prgOut = new ByteArrayOutputStream();
			final InputStream prgOutStream = m_proc.getInputStream();

			final ByteArrayOutputStream prgErr = new ByteArrayOutputStream();
			final InputStream prgErrStream = m_proc.getErrorStream();

			Thread prgOutMonitor = new Thread(){
				public void run(){
					try{
						int ch;
						while( (ch = prgOutStream.read()) >= 0 ){
							prgOut.write( (int)((byte)ch) );
						}
					}catch( Exception exc ){
						exc.printStackTrace();
					}
				}
			};
			prgOutMonitor.start();

			Thread prgErrMonitor = new Thread(){
				public void run(){
					try{
						int ch;
						while( (ch = prgErrStream.read()) >= 0 ){
							prgErr.write( (int)((byte)ch) );
						}
					}catch( Exception exc ){
						exc.printStackTrace();
					}
				}
			};
			prgErrMonitor.start();

			//2008_05_25 ^^^

            m_proc.waitFor();


			m_execTime = System.currentTimeMillis() - m_startTime;
System.out.println( " - exec time: " + m_execTime );

			//2008_05_25 vvv
			m_stdOutStr = new String( prgOut.toByteArray() );
			m_stdErrStr = new String( prgErr.toByteArray() );
			//2008_05_25 ^^^

            m_bStopped = true;
        }catch( Exception exc ){
            exc.printStackTrace();
        }finally{

			m_bExecutionStarted = true;
			m_bStopped = true;

			try{ 
//System.out.println( "CommandThread.run.finally : m_proc.destroy()!" );
				m_proc.destroy();
			}catch( Exception proc_exc ){
				proc_exc.printStackTrace();
			}
		}
    }

	/**
	 * Метод возвращает флаг - запущен ли процесс
	 */
	public boolean isStarted(){
		return m_bExecutionStarted;
	}
    
	/**
	 * Метод для принудительного останова выполнения программы
	 */
    public void stopExecution(){

System.out.println( "CommandThread.stopExecution()" );

		m_bExecutionStarted = true;

        try{ m_proc.destroy(); }catch( Exception exc ){
			exc.printStackTrace();
		}

		m_execTime = System.currentTimeMillis() - m_startTime;

        m_bStopped = true;
    }

	/**
	 * Метод для получения кода завершения
	 */
    public int getResultCode(){
		if( m_proc == null )
			return -1;

		int res = 0;
		try{
			res = m_proc.exitValue();
		}catch( Exception exc ){
			return 0;
		}
		return res;
    }

    private String getFromBufferedReader( BufferedReader reader ) throws Exception{
        
        if( reader == null ) return "";
        
        if( !reader.ready() )
            return "";
        
        StringBuffer res = new StringBuffer();

        String tmp = "";
        while( (tmp = reader.readLine()) != null ){
            res.append( tmp + System.getProperty("line.separator") );
        }

        return res.toString();
    }

	/**
	 * Метод для получения данных со стандартного потока ошибок
	 */    
    public String getStdError(){

//2008_05_25
		return m_stdErrStr;
/*

        String res = "";
        try{
			if( m_stdErr == null )
				res = "Error starting program";
			else
	            res = getFromBufferedReader( m_stdErr );

        }catch( Exception exc ){
            exc.printStackTrace();
            return "";
        }
        return res;
*/

    }
	/**
	 * Метод для получения данных со стандартного потока вывода
	 */    
    public String getStdOut(){

//2008_05_25
		return m_stdOutStr;
/*
        String res = "";
        try{
			if( m_stdOut == null )
				res = "Error starting program";
			else
	            res = getFromBufferedReader( m_stdOut );

        }catch( Exception exc ){
            exc.printStackTrace();
            return "";
        }
        return res;
*/
    }
    
	/**
	 * Признак останова программы
	 */    
    public boolean isStopped(){
        return m_bStopped;
    }
}
