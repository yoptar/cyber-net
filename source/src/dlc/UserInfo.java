package dlc;

/**
 * $Id: UserInfo.java,v 1 2007/02/20
 * <br/>
 * Author: ��������� �.�.
 * <br/>
 * �����, ���������� ���������� � ������������,����������� ������� �������.
 */

public class UserInfo {

    private String m_login = "";
    private String m_password = "";

	/**
	 * ����� ��� ��������� ������ ������������
	 */
    public void setLogin(String login) {
        m_login = login;
    }

	/**
	 * ����� ��� ��������� ������ ������������
	 */
    public void setPassword(String password) {
        m_password = password;
    }

	/**
	 * ����� ��� ��������� ������ ������������
	 */
    public String getLogin() {
        return m_login;
    }

	/**
	 * ����� ��� ��������� ������ ������������
	 */
    public String getPassword() {
        return m_password;
    }
}
