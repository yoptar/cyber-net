package prglabserver;

import java.util.*;
import java.io.*;

import org.dom4j.*;
import org.dom4j.io.*;

import dlc.Logger;

/**
 * ����� ��������� ���������� � ���������� ��������� � �������� ������������
 */
public class Main {
    
    ExecuteConfig m_config;
    String        m_type;
    String        m_program;
    String        m_input;
    String        m_output;
    Random        m_randomGen = new Random();
    
    String        m_workDir;
    
    String        m_lastResult = "";

	CommandExecutor m_programExecutor;
    
    /**
     * �����������
     * @param configRecs ������������� ������ (���� - �������� ������������, �������� - ��������� ExecuteConfig)
     * @param type ��� ������������ (������� � �������)
     * @param program �������� ��� ��������� ��� ��������
     */
    public Main( HashMap configRecs, String type, String program ) throws Exception{

        m_type = type;
        m_program = program;
        m_config = (ExecuteConfig)configRecs.get( type.toLowerCase() );

//2009.04.27
		try{
			program = program.replaceAll( "%25", "%" );
			program = program.replaceAll( "%2B", "+" );
			program = program.replaceAll( "\\%5C", "\\\\" );
			m_program = program;

System.out.println( "=======[PROGRAM.begin]========" );
System.out.println( m_program );
System.out.println( "=======[PROGRAM.end]==========" );
		}catch( Exception exc ){
			exc.printStackTrace();
		}
    }
    
    /**
     * ����� �����������, ������� � ��������� ��������� �� ����������
     */
/*
    public void startWork() throws Exception{

        prepareToRun();
        runProgram();
       
    }
*/
    /**
     * ����� ���������, ������������� �� ��������� �������� �����, ����������� ����� ������ ���������
     * @return ������� ������ �������� ������
     */
    public boolean isCorrectResult(){
        return m_lastResult.trim().equals( m_output.trim() );
    }

    /**
     * @return ��������� ������ ���������
     */
    public String getResult(){

System.out.println( "LAST_RESULT: " + m_lastResult );
System.out.println( "REF_OUTPUT : " + m_output );

        return m_lastResult;
    }
    
    /**
     * ����� ��������� ������� ��������� ������
     */
    private void setResult( String result ){
        m_lastResult = result;
    }
    
    /**
     * ���������� ���� ������, �������� ��������� (���������� � ��������)
     */
    public void compileProgram() throws Exception{
        
        String tmpPath = generateRandomPath( m_randomGen );
        String workDir = m_config.m_baseDir + File.separator + tmpPath + File.separator;
        m_workDir = workDir;
        
        if( !new File( workDir + File.separator + "Release" ).mkdirs() ){
            throw new Exception( "Main.prepareToRun - FAILED to create [" + workDir + File.separator + "Release" );
        }
        
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream( workDir + m_config.m_programFile );
            fos.write( m_program.getBytes() );
            fos.flush();
        }catch( Exception exc ){ throw exc; }
        finally{ try{ fos.close(); }catch( Exception closeExc ){} }
        
        CommandExecutor cmdExec = null;
        for( int i = 0; i < m_config.m_commands.size()-1; i++ ){
            cmdExec = new CommandExecutor();
            CommandArguments ca = m_config.m_commands.get(i);

            Logger.log( "Execute: [" + ca.m_name + "]" );
            if( !cmdExec.execCommand( ca.m_cmd, ca.m_args, workDir, ca.m_timeOut, m_config.m_env ) ){
				if( cmdExec.isTimeout() )
					setResult( "Time out at [" + ca.m_name + "]" );
				else
	                setResult( cmdExec.getResultOutput() );
                throw new Exception( "Main.prepareToRun() - FAILED to execute [" + ca.m_name + "]" );
            }else{

//System.out.println( "ResultCode: " + cmdExec.getResultCode() );

                if( cmdExec.getResultCode() != 0 ){
                    setResult( cmdExec.getResultOutput() );

System.out.println( "=======[FAILED TO EXECUTE [" + ca.m_name + "]=====" );
System.out.println( m_program );
System.out.println( "===================" );

                    throw new Exception( "Main.prepareToRun() - FAILED to execute [" + ca.m_name + "]" );
                }
            }
        }
    }

    /**
     * ������ ��������� ������� �� ����� ������������, ��� ������ ����
     * ���������������� ���������
     * @param input ��������� ������� ����� ������ (���������� ��������� ����� ����������� ����� �����)
     * @param output ��������� �������� ����� ������ (��������� � �������, ����������� �� ������������ ������ ������)
     */
    public void runProgram( String input, String output ) throws Exception{

		m_output = output;
		m_input = input;

        CommandExecutor cmdExec = new CommandExecutor();
        CommandArguments ca = m_config.m_commands.get( m_config.m_commands.size()-1 );
        Logger.log( "Execute: [" + ca.m_name + "]" );

		m_programExecutor = cmdExec;
        
        String cmdName = ca.m_cmd;
        String dir = m_workDir;
        if( ca.m_bPrefixWithDir ){
            cmdName = m_workDir + cmdName;
            dir = ".";
        }
        
        if( !cmdExec.execCommand( cmdName, ca.m_args, dir, /* timeOut */-1, new HashMap(), m_input ) ){//m_config.m_env ) ){
            setResult( cmdExec.getResultOutput() );
            throw new Exception( "Main.runProgram() - FAILED to execute [" + ca.m_name + "]" );
        }else{
            if( cmdExec.getResultCode() < 0 ){
                setResult( cmdExec.getResultOutput() );
                throw new Exception( "Main.runProgram() - FAILED to execute [" + ca.m_name + "]" );
            }
        }
        setResult( cmdExec.getResultOutput() );
    }

	private void clearDir( File f ){
		if( f.isDirectory() ){

			File []files = f.listFiles();

			for( File file : files ){
				if( file.getName().equals(".") || file.getName().equals("..") ) continue;
				clearDir( file );
			}
		}
		f.delete();
	}

	/**
	 * ����� ��� �������� ����������� ����������
	 */
	public void clearDirs(){

		File []files = new File( m_workDir ).listFiles();
		for( File f : files ){
			if( f.getName().equals(".") || f.getName().equals("..") ) continue;
			clearDir( f );
		}

		new File( m_workDir ).delete();
	}
    
    /**
     * ����� ���������� ��������� ��� ��� �������� ��������
     * @param rnd ��������� ���������� ��������� �����
     * @return ��� ��������
     */
    public static String generateRandomPath( Random rnd ){
        StringBuffer res = new StringBuffer();
        res.append( System.currentTimeMillis() + "_" );
        for( int i = 0; i < 5; i++ ){
            res.append( "" + rnd.nextInt(9) );
        }
        return res.toString();
    }
    
	/**
	 * ����� ��� ��������������� �������� ������ (�� ��������, ��������� � ����������� ������
	 */
	public void stopExecution(){
		try{ m_programExecutor.stopExecution(); }catch( Exception exc ){
			//Logger.log( "WARNING: prglabserver.Main.stopExecution FAILED" );
			//Logger.log( exc );
		}
		m_programExecutor = null;
	}

/*    
    public static void main( String []args ){
        try{
            //Its from VLServer:
            Document configDoc = new SAXReader().read( "resources/Config.xml" );
            HashMap config = ExecuteConfigParser.readConfig( configDoc );
            
            String sType = "msvc";
            String sProgram = "";
            {
                FileInputStream fis = new FileInputStream( "main.cpp" );
                byte []b = new byte[ (int)fis.available() ];
                fis.read( b );
                fis.close();
                sProgram = new String( b );
            }
            String sInput = "line1\r\nline2\r\nline3\r\n";
            String sOutput = "line1\r\nline2\r\nline3\r\n";
            
            Main m = new Main( config, sType, sProgram, sInput, sOutput );
            m.startWork();
        }catch( Exception exc ){
            exc.printStackTrace();
        }
    }
*/

	/**
	 * ����� ���������� ������� ����� ���������� ���������
	 */
	public long getExecTime(){
		return m_programExecutor.getExecTime();
	}

	/**
	 * ����� ���������� ���� - ������� �� �������
	 */
	public boolean isStarted(){

		if( m_programExecutor == null )
			return false;

		return m_programExecutor.isStarted();
	}

}
