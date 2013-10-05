package prglabserver;

import dlc.*;
import org.dom4j.*;
import org.dom4j.io.*;
import java.io.*;
import java.util.*;

/**
 * Класс реализует основной обработчик запросов лабораторного стенда
 */
public class CommandProcessor implements Processor{
    
    String   m_code = "";
    String   m_langType = "";
    String   m_input;
    String   m_output;
    String   m_userOutput = "";
    HashMap  m_config;

    Main     m_interpreter;
    boolean  m_bCompileError = false;
    

	/**
	 * Конструктор - создает экземпляр с пустой конфигурацией
	 */
    public CommandProcessor(){
        m_config = new HashMap();
    }
	/**
	 * Конструктор
	 * @param src экземпляр CommandProcessor для копирования конфигурации
	 */
    public CommandProcessor( CommandProcessor src ){
        m_config = new HashMap( src.getConfig() );
    }
	/**
	 * Метод для получения конфигурации CommandProcessor
	 */
    public HashMap getConfig(){
        return m_config;
    }

	/**
	 * Реализация метода init из интерфейса Processor
	 * @param code данные, полученные с лабораторной установки (через Console.getResults())
	 */
    public void init( String code ){
        
        m_bCompileError = false;
        
        try{

            code = code.replace( "%26", "&" );
            code = code.replace( "%3B", ";" );
            code = code.replace( "%3F", "?" );
            code = code.replace( "%20", " " );
            code = code.replace( "%23", "#" );
            code = code.replace( "%2F", "/" );
            code = code.replace( "%5B", "[" );
            code = code.replace( "%5D", "]" );
            code = code.replace( "%7B", "{" );
            code = code.replace( "%7D", "}" );
            code = code.replace( "%3D", "=" );

            code = dlc.util.HtmlParamEscaper.unescapeParam( code );

			if( code.indexOf("<!--") < 0 )
	            code = dlc.util.HtmlParamEscaper.unescapeParam( code );

//			code = code.replaceAll( "--", ";mmminusezz;" );

			
System.out.println("==== CommandProcessor.init, code: =============== ");
System.out.println( code );
System.out.println("=============== CommandProcessor.init, code: ==== ");


            Document doc = new SAXReader().read( new StringReader(code.trim()) );
            if( doc.selectSingleNode( "//Program/@langType" ) != null )
                m_langType = doc.selectSingleNode( "//Program/@langType" ).getText();
            
            if( doc.selectSingleNode( "//Program/comment()" ) != null ){
                m_code = doc.selectSingleNode( "//Program/comment()" ).getText();
//				m_code = m_code.replaceAll( ";mmminusezz;", "--" );
			}
            m_code = dlc.util.HtmlParamEscaper.unescapeParam( m_code );
            m_code += System.getProperty( "line.separator" );
/*
System.out.println("==== CommandProcessor.init, program code: =============== ");
System.out.println( m_code );
System.out.println("=============== CommandProcessor.init, program code: ==== ");
*/
            m_interpreter = new Main( m_config, m_langType, m_code );
            m_interpreter.compileProgram();

        }catch( Exception exc ){
            exc.printStackTrace();
            m_bCompileError = true;
        }
    }
    
	/**
	 * Реализация метода run из интерфейса Processor
	 * @param input входной набор данных
	 * @param output выходной набор данных
	 * @return признак успешного выполнения
	 */    
    public boolean run( String input, String output ) throws Exception{
        
        if( m_bCompileError ){
            m_userOutput = m_interpreter.getResult();
            return false;
        }
        
        m_input = input;
        m_output = output;
        
        if( m_config.get(m_langType) == null ){
            m_userOutput = "Unsupported language: [" + m_langType + "]";
            return false;
        }
        
        try{
/*
            Main m = new Main( m_config, m_langType, m_code, m_input, m_output );
            try{
                m.startWork();
            }catch( Exception workExc ){
                Logger.log( workExc );
            }
*/
            m_interpreter.runProgram( m_input, m_output );
            m_userOutput = m_interpreter.getResult();

        }catch( Exception exc ){
            exc.printStackTrace();
        }
        
        if( m_output == null ) m_output = "";

		m_output = m_output.trim();
		m_userOutput = m_userOutput.trim();
/*
Logger.log( "COMPARE RESULTS: \n" +
	"\tOUTPUT : " + m_userOutput + "\n" +
	"\tUSEROUT: " + m_output );
*/
        boolean bRes = compareStringsIgnoreNL( m_output, m_userOutput );
        
        return bRes;
    }

	/**
	 * Метод, вызываемый по завершнии работы потока обработки запроса
	 * (используется, например, для удаления временных файлов)
	 */
	public void release() throws Exception{
		m_interpreter.stopExecution();
		m_interpreter.clearDirs();
	}

   	//TODO: pass it to Processor interface ?
   	//TODO: pass it to Processor interface ?
   	//TODO: pass it to Processor interface ?
	/**
	 * Метод, вызываемый по завершнии работы потока обработки отдельного тест-набора
	 */
	public void stopExecution() throws Exception{
		m_interpreter.stopExecution();
	}

   	//TODO: pass it to Processor interface ?
   	//TODO: pass it to Processor interface ?
   	//TODO: pass it to Processor interface ?
	/**
	 * Метод, вызываемый по завершнии работы для получение реального времени выполнения программы
	 */
	public long getExecTime(){
		if( m_bCompileError )
			return 0;

		if( m_interpreter == null ){
			Logger.log( "prglabserver.CommandProcessor.getExecTime() - STRANGE BEHAVIOR" );
			return 0;
		}

		return m_interpreter.getExecTime();
	}

   	//TODO: pass it to Processor interface ?
   	//TODO: pass it to Processor interface ?
   	//TODO: pass it to Processor interface ?
	/**
	 * Метод возвращает флаг - запущен ли процесс
	 */
	public boolean isStarted(){
		if( !m_bCompileError && m_interpreter == null )
			return false;
		else if( m_bCompileError )
			return true;
		else if( m_interpreter == null ){
			Logger.log( "prglabserver.CommandProcessor.isStarted() - STRANGE BEHAVIOR!" );
			return false;
		}

		return m_interpreter.isStarted();
	}
    
    private boolean compareStringsIgnoreNL( String s1, String s2 ){
        String _s1 = s1.replace("\r\n", "\n").replace( "\r", "\n" ).trim();
        String _s2 = s2.replace( "\r\n", "\n" ).replace( "\r", "\n" ).trim();
        return _s1.equals( _s2 );
    }
    
	/**
	 * Метод для получения состояния CommandProcessor (после выполнения задачи, возвращает данные со стандартного потока вывода)
	 */
    public String getOutput(){
        return m_userOutput;
    }
    
	/**
	 * Реализация метода из интерфейса Processor
	 * @param configPath путь к файлу конфигурации
	 */
    public void processConfig( String configPath ) throws Exception{
        SAXReader reader = new SAXReader( false );
/*
        reader.setIncludeInternalDTDDeclarations( false );
        reader.setIncludeExternalDTDDeclarations( false );
        reader.setValidation( false );
        reader.setFeature( "http://apache.org/xml/features/validation/dynamic", false );
        reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false );
        reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
        reader.setFeature( "http://xml.org/sax/features/validation", false );
 */

        BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(configPath) ) );
        String tmp = "";
        StringBuffer xDoc = new StringBuffer();
        while( (tmp = br.readLine()) != null ){
            xDoc.append( tmp + System.getProperty( "line.separator" ) );
        }
        br.close();
        
        tmp = xDoc.toString();
        if( tmp.indexOf( "<!DOCTYPE" ) >= 0 ){
            String lex = "<!DOCTYPE ";
            int    p = tmp.indexOf(lex);
            String res = tmp.substring( 0, p );
            res += tmp.substring( tmp.indexOf(">", p) + 1 );
            tmp = res;
        }
        
        Document doc = reader.read( new StringReader( tmp ) );
        m_config = ExecuteConfigParser.readConfig( doc );
    }
    
	/**
	 * Реализация метода из интерфейса Processor - создает новый экземпляр CommandProcessor с текущей конфигурацией
	 * (используется для клонирования CommmandProcessor в многопоточном сервере)
	 * @return новый экземпляр CommandProcessor с текущей конфигурацией
	 */
    public Processor newInstance(){
        CommandProcessor cmdProc = new CommandProcessor( this );
        return cmdProc;
    }

	/**
	 * Метод для запуска проверяющего сервера
	 */
    public static void main( String []args ){
        Thread t = new Thread(){
            public void run(){
                new Server( "resources/Config.xml" ).startServer( new CommandProcessor() );
            }
        };
        t.start();

        /**
         * Запуск модуля тестирования. Его задача - передача запроса RLCP на сервер
         */
        //test.Tester.main( new String[]{ "resources/input", "resources/output" } );

        /**
         * Останов потока проверяющего сервера и выход
         */
        //try{ t.interrupt(); }catch( Exception intExc ){}
        //System.exit(0);
    }

}
