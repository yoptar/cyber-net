package dlc;

/**
 * �����, ����������� ������ � ������ ������
 * (��� ������ ������ ����������� ���� �������)
 */
public class Logger {
    
	/**
	 * ����� ��� ������ Exception � ������ ������
	 */
    public static void log( Exception exc ){
        System.out.println( "ERROR at " + new java.util.Date() + ": " );
        exc.printStackTrace();
    }

	/**
	 * ����� ��� ���������� ������ � ������ ������
	 */
    public static void log( String msg ){
        System.out.println( "LOG: " + new java.util.Date() + ": " + msg );
    }
}
