package dlc.util;

/**
 * �����, ����������� �������������/���������������� ����������� ��������
 * ��� �������� ��������� ��� �� ������� ������� � �������.
 */
public class HtmlParamEscaper {

	/**
	 * ����� ��� ������������� ������
	 * @param param �������� ������
	 * @return �������������� ������
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
	 * ����� ��� ���������������� ������
	 * @param param �������� �������������� ������
	 * @return ����������������� ������
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
