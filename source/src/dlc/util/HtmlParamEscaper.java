package dlc.util;

/**
 * Класс, реализующий экранирование/разэкранирование специальных символов
 * при передаче состояния ВЛР на сторону сервера и обратно.
 */
public class HtmlParamEscaper {

	/**
	 * метод для экранирования строки
	 * @param param исходная строка
	 * @return экранированная строка
	 */
    public static String escapeParam( String param ){
        String res = param.replaceAll( "&", "&amp;" );

        res = res.replaceAll( "\r\n", "<br/>" );
	res = res.replaceAll( "\r", "<br/>" );
	res = res.replaceAll( "\n", "<br/>" );

        res = res.replaceAll( "<", "&lt;" );
        res = res.replaceAll(">", "&gt;");
        res = res.replaceAll("-", "&#0045;");
        res = res.replaceAll( "\"", "&quot;" );

        return res;
    }

	/**
	 * метод для разэкранирования строки
	 * @param param исходная экранированная строка
	 * @return разэкранированная строка
	 */
    public static String unescapeParam( String param ){
        String res = param.replaceAll( "&quot;", "\"" );
        res = res.replaceAll( "&lt;br/&gt;", "\r\n");
        res = res.replaceAll( "&lt;", "<" );
        res = res.replaceAll( "&gt;", ">" );
        res = res.replaceAll( "&#0045;", "-" );
        res = res.replaceAll( "&amp;", "&" );

        res = res.replaceAll( "<br/>", System.getProperty("line.separator") );

        return res;
    }
}
