package dlc;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Класс, реализующий сервер проверки виртуальной лабораторной работы.
 */
public class Server {

    Processor m_proc;
    String m_configPath = "Config.xml";

    /**
     * Конструктор класса
     * @param confPath путь к файлу конфигурации
     */
    public Server(String confPath) {
        m_configPath = confPath;
    }

    /**
     * Метод для запуска сервера
     * @param proc экземпляр класса проверяющего сервера, реализующего интерфейс Processor
     */
    public void startServer(Processor proc) {
        m_proc = proc;

        try {
            //Парсим файл конфигурации
            ConfigParser parser = new ConfigParser();
            Config config = parser.parse(m_configPath);
            if (config == null) {
                Logger.log("Server.startServer() - FAILED to read config at: " + m_configPath);
                System.exit(0);
            }
            ServerSocket ss = null;
            Socket sock = null;

            int Port = config.getPort();
            Logger.log("Port:" + Integer.toString(Port));

            ss = new ServerSocket(Port);
            while (true) {
                //Слушаем порт
                sock = ss.accept();

                try {
                    m_proc.processConfig(m_configPath);
                } catch (Exception exc) {
                    Logger.log(exc);
                }

                //Запускаем поток, непосредственно производящий все основные действия
                new ThreadForRequest(sock, config, m_proc).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    public static void main(String args[]) {
    Logger.log("In main");
    Server server = new Server( "Config.xml" );
    try {
    //Парсим файл конфигурации
    ConfigParser parser = new ConfigParser();
    Config config = parser.parse("Config.xml");
    if (config == null) {
    System.exit(0);
    }
    ServerSocket ss = null;
    Socket sock = null;
    int Port = config.getPort();
    Logger.log("Port:" + Integer.toString(Port));
    ss = new ServerSocket(Port);
    while (true) {
    //Слушаем порт
    sock = ss.accept();
    //Запускаем поток, непосредственно производящий все основные действия
    new ThreadForRequest(sock, config, null).start();
    }
    } catch (Exception e) {
    e.printStackTrace();
    }
    }
     */
}

/**
 * Класс, реализующий поток обработки клиентских запросов
 */
class ThreadForRequest extends Thread {

    /**
     * Идентификатор метода CHECK
     */
    private final int CHECK = 0;
    /**
     * Идентификатор метода CALCULATE
     */
    private final int CALCULATE = 1;
    
    /**
     * Признак ошибки в запросе
     */
    private boolean bErrorInRequest = false;
    /**
     * Код ошибки
     */
    private int ErrorCode;
    /**
     * Socket соединения с клиентом (в нашем случае, сокет с сервлетом)
     */
    private Socket m_socket;
    /**
     * Режим работы сервера (CHECK или CALCULATE)
     */
    private int m_Method;
    /**
     * Имя клиента
     */
    private String Login = "";
    /**
     * Пароль клиента
     */
    private String Password = "";
    /**
     * Экземпляр класса Config с информацией о конфигурации, полученной из файла конфигурации
     */
    private Config m_config;
    /**
     * Экземпляр класса, реализующего интерфейс Processor
     */
    Processor m_proc;

    /**
     * Конструктор класса
     */
    public ThreadForRequest(Socket sock, Config config, Processor proc) {
        m_socket = sock;
        m_config = config;
        m_proc = proc;
    }

//Следующие 2 метода пока пустые(для запроса типа 
//Check все действия пока описаны далее по коду)
    private boolean check(Program prg) {
        return true;
    }

    private boolean calculate(Vector msg) {
        Logger.log("In Calculate ");
        return true;
    }

    private String readStringFromIS(InputStream is, int size) throws Exception {
        int ch = 0;
        int byteCount = 0;

        byte[] buffer = new byte[size];

        try {
            while (byteCount < size && (ch = is.read()) >= 0) {
                if (ch == '\b') {
                    break;
                }
//                buffer[byteCount++] = (byte) ch;
                buffer[byteCount++] = (byte) ch;
            }
        } catch (Exception exc) {
            System.out.println("We block at is.read. Total bytes read: " + byteCount + "/" + size );
            throw exc;
        }

        String res = new String(buffer, 0, byteCount );

//		System.out.println( "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=" );
//		System.out.println( res );

		return res;
    }


    /*
    String toSend = "check" + NL +
    "url:rlcp://ove:ove@" + Ip_Port + ":" + srv_port + NL +
    "content-length:" + req.length() + NL + NL +
    req + NL;
     */

    /*
    while (true) {
    m_socket.setSoTimeout(10000);
    c = in.read();
    if (c == -1) {
    Logger.log("ThreadForRequest::run - ERROR: Connection is closed ");
    in.close();
    out.close();
    m_socket.close();
    return;
    }
    if (c == 13) {
    c = in.read();
    nFieldCount++;
    if (nFieldCount == 1) {
    tmp = tmp.trim();
    Logger.log("METOD: " + tmp);
    if (tmp.toUpperCase().equals("CHECK")) {
    m_Method = CHECK;
    }
    //Пока что метод CALCULATE не поддерживается
    //поэтому возвращается код ответа 501(времено неподдерживаемый метод в запросе)
    else if (tmp.toUpperCase().equals("CALCULATE")) {
    m_Method = CALCULATE;
    out.write(("501" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    return;
    } else {
    out.write(("403" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    return;
    }
    }
    if (nFieldCount > 1) {
    StringTokenizer strTok = null;
    if (tmp.equals("")) {
    String szURL = (String) htFields.get("url");
    if (szURL == null) {
    out.write(("404" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    return;
    }
    strTok = new StringTokenizer(szURL, "@");
    if (strTok.countTokens() != 2) {
    out.write(("402" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    Logger.log("ERROR: error parse URL, cant find '@'");
    return;
    } else {
    String a1 = strTok.nextToken();
    strTok = new StringTokenizer( a1, "//" );
    String a2 = strTok.nextToken();
    //TODO: nexttoken - nexttoken BUG!!!
    strTok = new StringTokenizer(strTok.nextToken(), ":");
    //Logger.log("a1: " + a1 + ", a2: " + a2 );
    if (strTok.countTokens() != 2) {
    out.write(("402" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    Logger.log("ERROR: error at token URL parse" );
    return;
    } else {
    String szLogin = strTok.nextToken();
    String szPassword = strTok.nextToken();
    for (int i = 0; i < m_config.getUserInfo().size(); i++) {
    if (szLogin.equals(((UserInfo) m_config.getUserInfo().elementAt(i)).getLogin())) {
    if (szPassword.equals(((UserInfo) m_config.getUserInfo().elementAt(i)).getPassword())) {
    break;
    }
    }
    if (i == (m_config.getUserInfo().size() - 1)) {
    out.write(("402" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    Logger.log("ERROR: No user accounts specified in Config.xml");
    m_socket.close();
    return;
    }
    }
    }
    }
    String szLength = (String) htFields.get("content-length");
    if (szLength == null) {
    out.write(("404" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    return;
    }
    //Возвращаем в переменную str XML-документ из запроса
    //str = getXML(in, Integer.parseInt(szLength));
    str = readStringFromIS( in, Integer.parseInt( szLength ) );
    //Logger.log(str);
    break;
    }
    //Logger.log("Find : at\n\t" + tmp);
    strTok = new StringTokenizer(tmp, ":", true);
    if (strTok.countTokens() == 1 || strTok.countTokens() == 2) {
    out.write(("405" + "\r\n").getBytes());
    out.flush();
    in.close();
    out.close();
    m_socket.close();
    return;
    } else {
    String szKey = strTok.nextToken().trim().toLowerCase();
    strTok.nextToken();
    String szValue = "";
    while (strTok.hasMoreTokens()) {
    szValue += strTok.nextToken().trim();
    }
    htFields.put(szKey, szValue);
    }
    }
    tmp = "";
    continue;
    } else if (c == '\b') {
    if (tmp.length() == 0) {
    continue;
    }
    tmp = tmp.substring(0, tmp.length() - 1);
    } else {
    tmp += (char) c;
    }
    }
     */
    
    private boolean checkURLAccess( String rlcpURL ){
        StringTokenizer st = new StringTokenizer( rlcpURL, ":@/" );
        int tokenCounter = 0;
        
        final int TOKEN_LOGIN = 2;
        final int TOKEN_PASSWD = 3;
        
        String sLogin = "", sPasswd = "";
        
        while( st.hasMoreTokens() ){
            String token = st.nextToken();
//url:rlcp://ove:ove@" + Ip_Port + ":" + srv_port + NL
            if( tokenCounter == TOKEN_LOGIN )
                sLogin = token;
            else if( tokenCounter == TOKEN_PASSWD ){
                sPasswd = token;
                break;
            }
            tokenCounter++;
        }
        
        for( int i = 0; i < m_config.getUserInfo().size(); i++ ){
            UserInfo ui = (UserInfo)m_config.getUserInfo().elementAt(i);
            if( ui.getLogin().equals(sLogin) && ui.getPassword().equals(sPasswd) )
                return true;
        }
        return false;
    }

	public static long m_connID = 0;
	private void logConnection( Socket sock ){
        Logger.log( "Connection[" + m_connID + "] from: " + sock.getInetAddress().getHostAddress() );
		m_connID++;
	}
	private void logDisconnect(){
        Logger.log( "Connection[" + m_connID + "] CLOSED!" );
	}

    public void run(){
        OutputStream os = null;
        InputStream  is = null;

		logConnection( m_socket );
       
        try{
            os = m_socket.getOutputStream();
            is = m_socket.getInputStream();
            
            final int FIELD_METHOD = 1;
            final int FIELD_URL = 2;
            final int FIELD_LEN = 3;
            final int FIELD_EMPTY = 4;
            
            int  ch, prevCh = 0;
            StringBuffer strline = new StringBuffer();
            int  fieldID = 0;
            int  iContentLen = 0;
            String sRequest = "";
            HashMap<String,String> htFields = new HashMap<String,String>();
            
            //m_socket.setSoTimeout( 10000 );
            boolean bUse0xd_0xa = false;
            
            while( (ch = is.read()) >= 0 ){
                if( ch == 0xd || ch == 0xa ){
                    
                    if( prevCh == 0xd && ch == 0xa ){
                        bUse0xd_0xa = true;
                        continue;
                    }
                    fieldID++;
                    
                    String line = strline.toString().trim();
                    
                    System.out.println( "STROKE: [" + line + "]" );
                    
                    switch( fieldID ){
                        case FIELD_METHOD:
                            if( line.equalsIgnoreCase("CHECK") )
                                m_Method = CHECK;
                            else
                                throw new Exception( "403" );
                            break;
                        case FIELD_URL:
                            if( !checkURLAccess(line) )
                                throw new Exception( "402" );
                            htFields.put( line.substring( 0, line.indexOf(":") ).trim(),
                                    line.substring( line.indexOf(":")+1 ).trim() );
                            break;
                        case FIELD_LEN:
                            iContentLen = Integer.parseInt( line.substring( line.indexOf(":")+1 ) );
                            htFields.put( line.substring( 0, line.indexOf(":") ).trim(),
                                    line.substring( line.indexOf(":")+1 ).trim() );
                            break;
                        case FIELD_EMPTY:
                            ch = is.read();
                            while( ch == 0xd || ch == 0xa )
                                ch = is.read();
                        default:
                            
                            System.out.println( "======>> REQUEST >>======" );

							int byteCount = 0;
                            
                            try{

								//+ (iContentLen>>1) - to cover garbage \r or \r\n
								byte[] buffer = new byte[ iContentLen + (iContentLen>>1) ];
								buffer[ byteCount++ ] = (byte)ch;

                                while( byteCount < iContentLen && (ch = is.read()) >= 0 ){
//                                    System.out.print( (char)ch );
//                                    reqSB.append( (char)ch );
									buffer[ byteCount ] = (byte)ch;
                                    byteCount++;
                                }

								sRequest = new String( buffer, 0, byteCount );//, "Windows-1251" );

//System.out.println( sRequest );

                                //sRequest = reqSB.toString();
                            }catch( Exception readReqExc ){
                                Logger.log( "WARNING: Read/block request exception: " + byteCount + " of " + iContentLen + " read." );
//                                Logger.log( readReqExc );
                            }
                            
                            System.out.println( "======<< REQUEST.END <<======" );
                            
                            //sRequest = readStringFromIS( is, iContentLen );
                            break;
                    }
                    
                    if( sRequest.length() > 0 )
                        break;
                    
                    strline = new StringBuffer();
                }else{
                    strline.append( (char)ch );
                }
                
                prevCh = ch;
            }
            
            //Start working with sRequest
            //////////////////////////////
            
            //m_socket.setSoTimeout( 0 );
            
            if( m_config.getRedirectHost() != null && m_config.getRedirectPort() != null ){
                
                /**
                 * Что выведем в запрос при редиректе:
                 */
/*
                System.out.println( "CHECK" );
                System.out.println( "url:" + htFields.get("url") );
                System.out.println( "content-length:" + htFields.get("content-length") );
                System.out.println();
                System.out.println(str);
*/
                
                Logger.log( "REDIRECT TO: " + m_config.getRedirectHost() + ":" + m_config.getRedirectPort() );
                String NL = "\r\n"; //System.getProperty( "line.separator" );
                String req = "CHECK" + NL;
                req += "url:" + htFields.get("url") + NL;
                req += "content-length:" + htFields.get("content-length") + NL;
                req += NL;
                req += NL;
                req += sRequest + NL;
                
/*                
    String toSend = "check" + NL +
    "url:rlcp://ove:ove@" + Ip_Port + ":" + srv_port + NL +
    "content-length:" + req.length() + NL + NL +
    req + NL;
*/
                /**
                 * Ошибка соединения с удаленным сервером - выдавать как ошибку HTTP ? (под номером ???)
                 */
                
                Socket redirectSocket = new Socket( m_config.getRedirectHost(), Integer.parseInt( m_config.getRedirectPort() ) );
                OutputStream redirOS = redirectSocket.getOutputStream();
                InputStream  redirIS = redirectSocket.getInputStream();
                
System.out.println( "req.len=" + req.length() + ", content-length=" + htFields.get("content-length") );
                
                redirOS.write( req.getBytes() );
//                redirOS.flush();
                
                //StringBuffer answer = new StringBuffer();

				byte []buffer = new byte[255];
				int    byteCnt = 0;
                ch = 0;
                try{


                    while( (ch = redirIS.read()) >= 0 ){
						buffer[byteCnt++] = (byte)ch;

						if( byteCnt >= buffer.length ){
							byte []newBuffer = new byte[ buffer.length<<1 ];
							System.arraycopy( buffer, 0, newBuffer, 0, buffer.length );
							buffer = newBuffer;
						}
                    }

                }catch( SocketException sockExc ){
                    //ConnectionReset ?
					//sockExc.printStackTrace();
                }finally{
                    try{ redirOS.close(); }catch( Exception cExc ){}
                    try{ redirIS.close(); }catch( Exception cExc ){}
                }
                
//System.out.println("ANSWER: " + answer );
                
                os.write( buffer, 0, byteCnt ); //answer.toString().getBytes() );

//Logger.log( "RESPONSE: " + new String(buffer, 0, byteCnt) + ", len: " + new String(buffer, 0, byteCnt) );

                return;
            }
            else if (m_Method == CHECK) {
/*                
                System.out.println( "===========Request===>>>====" );
                System.out.println( sRequest );
                System.out.println( "===<<<====Request===========" );
*/                
                // Весь нижеидущий код со временем переместиться в метод Check
//                InputStream is = new ByteArrayInputStream(str.getBytes());

                //ReqParser parser = new ReqParser();

                /*
                Разбор XML-документа
                 */

                //Парсим ответ пользователя
//Logger.log("Input UserAnswer:\n" + str + "\n==================");
                Program prg = new ReqParser2().parse( sRequest );
                if (prg == null) 
                    throw new Exception( "400" );

//    Запуск программы (ответа студента) на выполнение

                Vector Res = new Vector();
                try {
                    Res = prg.runForCheck(m_config, m_proc.newInstance());
                } catch (Exception e) {
                    //e.printStackTrace();
                    Logger.log("ERROR: ThreadForRequest processing FAILED (at runForCheck): " + e.getMessage());
                }

                if (Res == null) 
                    throw new Exception( "401" );

                //Составляем XML-ответ сервлету
                Document document = DocumentHelper.createDocument();
//                document.addDocType("Response", null, "http://de.ifmo.ru/--DTD/Response.dtd");

                Element response = document.addElement("Response");
                for (int i = 0; i < Res.size(); i++) {
                    Element ChRes = response.addElement("CheckingResult");
                    ChRes.addAttribute("id", ((CheckingResult) Res.elementAt(i)).getID());
                    ChRes.addAttribute("Time", ((CheckingResult) Res.elementAt(i)).getTime());
                    ChRes.addAttribute("Result", ((CheckingResult) Res.elementAt(i)).getResult());
                    String output = ((CheckingResult) Res.elementAt(i)).getOutput();
                    output = dlc.util.HtmlParamEscaper.escapeParam( output );
                    ChRes.addComment( output );
                }

                int iLength = document.asXML().getBytes().length;
                String XMLResponse = "200" + "\r\n";
                XMLResponse += "Content-Length:" + Integer.toString(iLength) + "\r\n\r\n";
                XMLResponse += document.asXML();

//                Logger.log("Response:\n" + XMLResponse);

				byte []byteXmlResp = XMLResponse.getBytes();

Logger.log( "Result size: " + byteXmlResp.length + " bytes" );

                os.write( byteXmlResp );

				try{ Thread.sleep(1); Thread.yield(); }catch( Exception intExc ){}
                os.flush();
				try{ Thread.sleep(1); Thread.yield(); }catch( Exception intExc ){}
				os.close();
				try{ Thread.sleep(1); Thread.yield(); }catch( Exception intExc ){}

//Logger.log( "RESPONSE: " + XMLResponse );

            /*
            FileOutputStream fos = new FileOutputStream( "c:\\temp\\cohlab_server.out" );
            fos.write( XMLResponse.getBytes() );
            fos.close();
             */

            } else if (m_Method == CALCULATE) {
            //calculate( message );
            } else {
                throw new Exception( "501" );
/*
                out.write("Error in Request".getBytes());
                Logger.log("Error in request");
                bErrorInRequest = true;
*/
            }

            //////////////////////////////
            //////////////////////////////
            //////////////////////////////
        
        }catch( Exception exc ){
            
            String result = "401\r\n";
            try{ 
                result = "" + Integer.parseInt( exc.getMessage() ) + "\r\n";
            }catch( Exception codeExc ){
                Logger.log( "WARNING: server exception: " + exc.getMessage() );
                Logger.log( exc );
            }

//Logger.log( "RESPONSE: " + result );

            try{
                os.write( result.getBytes() );
            }catch( Exception innExc ){
                Logger.log( innExc );
            }

            Logger.log( exc );
        }finally{
            try{ is.close(); }catch( Exception cExc ){}
            try{ os.flush(); }catch( Exception cExc ){}
            try{ os.close(); }catch( Exception cExc ){}
            try{ m_socket.close(); }catch( Exception cExc ){}

			logDisconnect();
        }
    }

    /**
     * Основной цикл потока обработки запросов.
     * После прочтения текста RLCP-запроса, вызывает <br>
     * <ol>
     * <li>ReqParser2.parse</li>
     * <li>Program.runForCheck</li>
     * <li>Создает XML-документ с результатом проверки</li>
     * </ol>
     */
    public void run2() {

        OutputStream out = null;
        InputStream in = null;

        try {
            out = m_socket.getOutputStream();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)), true);
            in = m_socket.getInputStream();

            //Временная строка для всевозможной обработки данных
            String tmp = new String();

            int c;

            int nFieldCount = 0;   //Номер строки в запросе

            Hashtable htFields = new Hashtable();

            /*
            Разбор заголовков
             */

            String str = "";

            m_socket.setSoTimeout(10000);
            
            boolean bPrevNL = false;
            
            while (true) {

                c = in.read();
/*                
                System.out.print( (char)c );
                
                if( c == 13 || c == 10 ){
                    System.out.println( "\nNL: " + (int)c + "\n" );
                }
*/                
                if (c == -1) {
                    Logger.log("ThreadForRequest::run - ERROR: Connection is closed ");
                    in.close();
                    out.close();
                    m_socket.close();
                    return;
                }
                
                if (c == 13 || c == 10) {
                    
                    if( bPrevNL )
                        continue;
                    
                    System.out.println( "STROKE: [" + tmp.trim() + "]" );
                    
                    if( c == 13 )
                        c = in.read();
                    
                    nFieldCount++;

                    if (nFieldCount == 1) {
                        tmp = tmp.trim();
                        Logger.log("METOD: " + tmp);
                        if (tmp.toUpperCase().equals("CHECK")) {
                            m_Method = CHECK;
                        }
                        //Пока что метод CALCULATE не поддерживается
                        //поэтому возвращается код ответа 501(времено неподдерживаемый метод в запросе)
                        else if (tmp.toUpperCase().equals("CALCULATE")) {
                            m_Method = CALCULATE;
                            out.write(("501" + "\r\n").getBytes());
                            out.flush();
                            in.close();
                            out.close();
                            m_socket.close();
                            return;
                        } else {
                            out.write(("403" + "\r\n").getBytes());
                            out.flush();
                            in.close();
                            out.close();
                            m_socket.close();
                            return;
                        }
                    }
                    if (nFieldCount > 1) {
                        StringTokenizer strTok = null;
                        if (tmp.equals("")) {
                            String szURL = (String) htFields.get("url");
                            if (szURL == null) {
                                out.write(("404" + "\r\n").getBytes());
                                out.flush();
                                in.close();
                                out.close();
                                m_socket.close();
                                return;
                            }
                            strTok = new StringTokenizer(szURL, "@");

                            if (strTok.countTokens() != 2) {
                                out.write(("402" + "\r\n").getBytes());
                                out.flush();
                                in.close();
                                out.close();
                                m_socket.close();
                                Logger.log("ERROR: error parse URL, cant find '@'");
                                return;
                            } else {
                                String a1 = strTok.nextToken();
                                strTok = new StringTokenizer(a1, "//");
                                String a2 = strTok.nextToken();
                                //TODO: nexttoken - nexttoken BUG!!!
                                strTok = new StringTokenizer(strTok.nextToken(), ":");

//Logger.log("a1: " + a1 + ", a2: " + a2 );

                                if (strTok.countTokens() != 2) {
                                    out.write(("402" + "\r\n").getBytes());
                                    out.flush();
                                    in.close();
                                    out.close();
                                    m_socket.close();
                                    Logger.log("ERROR: error at token URL parse");
                                    return;
                                } else {
                                    String szLogin = strTok.nextToken();
                                    String szPassword = strTok.nextToken();

                                    for (int i = 0; i < m_config.getUserInfo().size(); i++) {
                                        if (szLogin.equals(((UserInfo) m_config.getUserInfo().elementAt(i)).getLogin())) {
                                            if (szPassword.equals(((UserInfo) m_config.getUserInfo().elementAt(i)).getPassword())) {
                                                break;
                                            }
                                        }

                                        if (i == (m_config.getUserInfo().size() - 1)) {
                                            out.write(("402" + "\r\n").getBytes());
                                            out.flush();
                                            in.close();
                                            out.close();
                                            Logger.log("ERROR: No user accounts specified in Config.xml");
                                            m_socket.close();
                                            return;
                                        }
                                    }
                                }
                            }

                            String szLength = (String) htFields.get("content-length");
                            if (szLength == null) {
                                out.write(("404" + "\r\n").getBytes());
                                out.flush();
                                in.close();
                                out.close();
                                m_socket.close();
                                return;
                            }
                            //Возвращаем в переменную str XML-документ из запроса
                            
System.out.println( "Reading request(XML) from InputStream...");

                            //str = getXML(in, Integer.parseInt(szLength));
                            str = readStringFromIS(in, Integer.parseInt(szLength));
                            //Logger.log(str);
                            break;
                        }

//Logger.log("Find : at\n\t" + tmp);

                        strTok = new StringTokenizer(tmp, ":", true);
                        if (strTok.countTokens() == 1 || strTok.countTokens() == 2) {
                            out.write(("405" + "\r\n").getBytes());
                            out.flush();
                            in.close();
                            out.close();
                            m_socket.close();
                            return;
                        } else {

                            String szKey = strTok.nextToken().trim().toLowerCase();
                            strTok.nextToken();
                            String szValue = "";
                            while (strTok.hasMoreTokens()) {
                                szValue += strTok.nextToken().trim();
                            }
                            
System.out.println( szKey + "=" + szValue );
                            
                            htFields.put(szKey, szValue);
                        }
                    }

                    tmp = "";
                    continue;
                } else if (c == '\b') {
                    if (tmp.length() == 0) {
                        continue;
                    }
                    tmp = tmp.substring(0, tmp.length() - 1);
                } else {
                    tmp += (char) c;
                }
                
                if( c == 13 || c == 10 )
                    bPrevNL = true;
                else
                    bPrevNL = false;

            }

            /*
            String str = "";
            try{
            str = readRequest( in );
            }catch( Exception readExc ){
            out.write( readExc.getMessage().getBytes() );
            try{
            out.flush();
            out.close();
            }catch( Exception outCloseEx ){}
            try{ m_socket.close(); }catch( Exception sockCloseExc ){}
            return;
            }finally{
            try{ in.close(); }catch( Exception closeExc ){}
            }
             */

            m_socket.setSoTimeout(0);

            /**
             * Если сервер сконфигурирован на перенаправление запросов
             */
            if( m_config.getRedirectHost() != null && m_config.getRedirectPort() != null ){
                
                /**
                 * Что выведем в запрос при редиректе:
                 */
/*
                System.out.println( "CHECK" );
                System.out.println( "url:" + htFields.get("url") );
                System.out.println( "content-length:" + htFields.get("content-length") );
                System.out.println();
                System.out.println(str);
*/
                
                Logger.log( "REDIRECT TO: " + m_config.getRedirectHost() + ":" + m_config.getRedirectPort() );
                String NL = "\r\n"; //System.getProperty( "line.separator" );
                String req = "CHECK" + NL;
                req += "url:" + htFields.get("url") + NL;
                req += "content-length:" + htFields.get("content-length") + NL;
                req += NL;
                req += NL;
                req += str + NL;
                
/*                
    String toSend = "check" + NL +
    "url:rlcp://ove:ove@" + Ip_Port + ":" + srv_port + NL +
    "content-length:" + req.length() + NL + NL +
    req + NL;
*/
                Socket redirectSocket = new Socket( m_config.getRedirectHost(), Integer.parseInt( m_config.getRedirectPort() ) );
                OutputStream os = redirectSocket.getOutputStream();
                InputStream  is = redirectSocket.getInputStream();
                
System.out.println( "req.len=" + req.length() + ", content-length=" + htFields.get("content-length") );
                
                os.write( req.getBytes() );
                os.flush();
                
                StringBuffer answer = new StringBuffer();
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                while( (req = br.readLine()) != null )
                    answer.append( req + NL );
                br.close();
                os.close();
                
                out.write( answer.toString().getBytes() );
                out.flush();
                out.close();
                m_socket.close();
                return;
            }
            else if (m_Method == CHECK) {

                // Весь нижеидущий код со временем переместиться в метод Check
//                InputStream is = new ByteArrayInputStream(str.getBytes());

                //ReqParser parser = new ReqParser();

                /*
                Разбор XML-документа
                 */

                //Парсим ответ пользователя
//Logger.log("Input UserAnswer:\n" + str + "\n==================");
                Program prg = new ReqParser2().parse(str);
                if (prg == null) {
                    out.write(("400" + "\r\n").getBytes());
                    out.flush();
                    in.close();
                    out.close();
                    m_socket.close();
                    return;
                }

//    Запуск программы (ответа студента) на выполнение

                Vector Res = new Vector();
                try {
                    Res = prg.runForCheck(m_config, m_proc.newInstance());
                } catch (Exception e) {
                    //e.printStackTrace();
                    Logger.log("ERROR: ThreadForRequest processing FAILED (at runForCheck): " + e.getMessage());
                }

                if (Res == null) {
                    out.write(("401" + "\r\n").getBytes());
                    out.flush();
                    in.close();
                    out.close();
                    m_socket.close();
                    return;
                }

                //Составляем XML-ответ сервлету
                Document document = DocumentHelper.createDocument();
//                document.addDocType("Response", null, "http://de.ifmo.ru/--DTD/Response.dtd");

                Element response = document.addElement("Response");
                for (int i = 0; i < Res.size(); i++) {
                    Element ChRes = response.addElement("CheckingResult");
                    ChRes.addAttribute("id", ((CheckingResult) Res.elementAt(i)).getID());
                    ChRes.addAttribute("Time", ((CheckingResult) Res.elementAt(i)).getTime());
                    ChRes.addAttribute("Result", ((CheckingResult) Res.elementAt(i)).getResult());
                    ChRes.addComment((String) ((CheckingResult) Res.elementAt(i)).getOutput());
                }

                int iLength = document.asXML().getBytes().length;
                String XMLResponse = "200" + "\r\n";
                XMLResponse += "Content-Length:" + Integer.toString(iLength) + "\r\n\r\n";
                XMLResponse += document.asXML();

//                Logger.log("Response:\n" + XMLResponse);
                out.write(XMLResponse.getBytes());

            /*
            FileOutputStream fos = new FileOutputStream( "c:\\temp\\cohlab_server.out" );
            fos.write( XMLResponse.getBytes() );
            fos.close();
             */

            } else if (m_Method == CALCULATE) {
            //calculate( message );
            } else {
                out.write("Error in Request".getBytes());
                Logger.log("Error in request");
                bErrorInRequest = true;
            }

            out.flush();
            in.close();
            out.close();

            m_socket.close();

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                m_socket.close();
            } catch (Exception ex) {
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
