package dlc;

import org.xml.sax.*;
import org.xml.sax.helpers.ParserAdapter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Класс, разбирающий XML-файл конфигурации.
 */

public class ConfigParser {

    private MyHandler handler;
    private ParserAdapter saxParser;
    private boolean ErrorWhenParse;

	/**
	 * Внутренний класс для разбора XML-документа
	 */
    class MyHandler implements ContentHandler, ErrorHandler {

        private PrintWriter out;

        private Vector userInfo = new Vector();
        private Vector tmp1 = new Vector();
        private String CurrentElement = "";
        private Config config = new Config();

        public void endPrefixMapping(java.lang.String prefix) {
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void skippedEntity(java.lang.String name) {
        }

        public void startPrefixMapping(java.lang.String prefix, java.lang.String uri) {
        }


//======================================================
// Вспомогательные методы
//======================================================
        //возвращает объект-программу
        public Config getConfig() {
            return config;
        }


//=======================================================
// Обработчики событий. Методы интерфейса DocumentHandler
//========================


        // Начало документа
        public void startDocument() {
            Logger.log("In StartDocument ");
        }

        // Конец документа
        public void endDocument() {
            Logger.log("In endDocument ");
        }

        // Встретился открывающий тэг элемента //

        public void startElement(String a, String name, String qName, Attributes attrs) {
//      Logger.log( name );
            CurrentElement = name;

            if (name.equals("UserInfo")) {

                int len = attrs.getLength();
                UserInfo info = new UserInfo();
                for (int i = 0; i < len; i++) {
                    if (attrs.getLocalName(i).equals("login")) {
                        info.setLogin(attrs.getValue(i).trim());
                    } else {
                        info.setPassword(attrs.getValue(i).trim());
                    }
                }
                userInfo.add(info);
            }

            else if (name.equals("Port")) {
                config.setPort(Integer.parseInt(attrs.getValue(0)));
            }
            
            else if(name.equals("Redirect") ){
                config.setRedirect( attrs.getValue("host"), attrs.getValue("port") );
            }
			else if( name.equals("DebugRunTimeout") ){
				config.setDebugTimeout( Integer.parseInt( attrs.getValue("timeOut") )*1000 );
			}
        }


        // Встретился закрывающий тэг элемента

        public void endElement(String URLname, String name, String qName) {
            if (name.equals("Config")) {
                config.setUserInfo(userInfo);
            }

        }

// Текстовые символы

        public void characters(char ch[], int start, int length) {
//	Logger.log( ch[3] );


        }

        // Необрабатываемые символы(например, содержимое секции CDATA)
        public void ignorableWhitespace(char ch[], int start, int length) {
            characters(ch, start, length);
        }

        // Инструкции XML-процессору
        public void processingInstruction(String target, String data) {
        }

//===================================================
// Методы интерфейса ErrorHandler
//===============================

        // Последнее предупреждение
        public void warning(SAXParseException ex) {
            Logger.log("In warning");
            Logger.log("Warning at " +
                    ex.getLineNumber() + " . " +
                    ex.getColumnNumber() + "  -  " +
                    ex.getMessage());
        }

        // Произошла ошибка
        public void error(SAXParseException ex) {
            Logger.log("In error");
            Logger.log("Error at {" +
                    ex.getLineNumber() + " . " +
                    ex.getColumnNumber() + "  -  " +
                    ex.getMessage());
            ErrorWhenParse = true;
        }

        // Такие ошибки исправить уже нельзя
        public void fatalError(SAXParseException ex) throws SAXException {
            Logger.log("fatal");
            Logger.log("Fatal error at {" +
                    ex.getLineNumber() + " . " +
                    ex.getColumnNumber() + "  -  " +
                    ex.getMessage());
            ErrorWhenParse = true;
            throw ex;
        }
    }//end of MyHandler class

//==========================================================
//Методы класса MyParser
//==========================================================
    /**
	 * Конструктор класса
	 */
    public ConfigParser() throws InstantiationException {


        try {
//      System.setProperty("org.xml.sax.parser", "javax.xml.parsers.SAXParser");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(true);
            SAXParser sp = spf.newSAXParser();

            saxParser = new ParserAdapter(sp.getParser());


            handler = new MyHandler();
            saxParser.setContentHandler(handler);
            saxParser.setErrorHandler(handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * Метод для разбора конфигурации проверяющего сервера (формат XML)
	 * @param is путь к файлу конфигурации
	 * @return экземпляр класса Config
	 */
    public Config parse(String is) throws SAXException {
        try {
            saxParser.parse(is);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (ErrorWhenParse) {
            return null;
        }
        return handler.getConfig();
    }
}