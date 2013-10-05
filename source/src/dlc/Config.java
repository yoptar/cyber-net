package dlc;

import java.util.Vector;
import java.util.Hashtable;

/**
 * ����� ��� �������� ������ � ������������.
 */
public class Config {
    private Vector m_userInfo = new Vector();
    private int m_Port;
    private String m_redirHost;
    private String m_redirPort;
	private long   m_debugTimeout = 60000;

	/**
	 * ��������� ���������� � �������������, ������� �������� ������ � ������� ������������ �������
	 */
    public void setUserInfo(Vector userInfo) {
        m_userInfo = userInfo;
    }

	/**
	 * ��������� ���������� � �������������, ������� �������� ������ � ������� ������������ �������
	 */
    public Vector getUserInfo() {
        return m_userInfo;
    }

	/**
	 * ��������� ������� �� �������
	 */
	public long getDebugTimeout(){
		return m_debugTimeout;
	}

	/**
	 * ��������� ������� �� �������
	 */
	public void setDebugTimeout( long timeOut ){
		m_debugTimeout = timeOut;
	}

	/**
	 * ��������� �����, �� ������� ������ ����� ������������ �����������
	 */
    public void setPort(int port) {
        m_Port = port;
    }

	/**
	 * ��������� �����, �� ������� ������ ������������ �����������
	 */
    public int getPort() {
        return m_Port;
    }
    
    /**
     * ��������� ���������� ���������
     */
    public void setRedirect( String host, String port ){
        m_redirHost = host;
        m_redirPort = port;
    }
    
    /**
     * ��������� ��������� ��������� (���/����� �������)
     */
    public String getRedirectHost(){ return m_redirHost; }

    /**
     * ��������� ��������� ��������� (���� �������)
     */
    public String getRedirectPort(){ return m_redirPort; }
}
