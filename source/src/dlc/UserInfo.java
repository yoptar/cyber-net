package dlc;

/**
 * $Id: UserInfo.java,v 1 2007/02/20
 * <br/>
 * Author: Бердников Е.В.
 * <br/>
 * Класс, содержащий информацию о пользователе,запросившем ресурсы сервера.
 */

public class UserInfo {

    private String m_login = "";
    private String m_password = "";

	/**
	 * Метод для установки логина пользователя
	 */
    public void setLogin(String login) {
        m_login = login;
    }

	/**
	 * Метод для установки пароля пользователя
	 */
    public void setPassword(String password) {
        m_password = password;
    }

	/**
	 * Метод для получения логина пользователя
	 */
    public String getLogin() {
        return m_login;
    }

	/**
	 * Метод для получения пароля пользователя
	 */
    public String getPassword() {
        return m_password;
    }
}
