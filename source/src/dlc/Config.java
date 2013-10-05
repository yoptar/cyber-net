package dlc;

import java.util.Vector;
import java.util.Hashtable;

/**
 *  ласс дл€ хранени€ данных о конфигурации.
 */
public class Config {
    private Vector m_userInfo = new Vector();
    private int m_Port;
    private String m_redirHost;
    private String m_redirPort;
	private long   m_debugTimeout = 60000;

	/**
	 * ”становка информации о пользовател€х, которым разрешен доступ к данному провер€ющему серверу
	 */
    public void setUserInfo(Vector userInfo) {
        m_userInfo = userInfo;
    }

	/**
	 * ѕолучение информации о пользовател€х, которым разрешен доступ к данному провер€ющему серверу
	 */
    public Vector getUserInfo() {
        return m_userInfo;
    }

	/**
	 * ѕолучение времени на отладку
	 */
	public long getDebugTimeout(){
		return m_debugTimeout;
	}

	/**
	 * ”становка времени на отладку
	 */
	public void setDebugTimeout( long timeOut ){
		m_debugTimeout = timeOut;
	}

	/**
	 * ”становка порта, на котором сервер будет обрабатывать подключени€
	 */
    public void setPort(int port) {
        m_Port = port;
    }

	/**
	 * ѕолучение порта, на котором сервер обрабатывает подключени€
	 */
    public int getPort() {
        return m_Port;
    }
    
    /**
     * ”становка пареметров редиректа
     */
    public void setRedirect( String host, String port ){
        m_redirHost = host;
        m_redirPort = port;
    }
    
    /**
     * ѕолучение параметра редиректа (им€/адрес сервера)
     */
    public String getRedirectHost(){ return m_redirHost; }

    /**
     * ѕолучение параметра редиректа (порт сервера)
     */
    public String getRedirectPort(){ return m_redirPort; }
}
