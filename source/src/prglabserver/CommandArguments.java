package prglabserver;

/**
 * Класс для описания команды операционной системы с аргументами
 */
public class CommandArguments {
	/**
	 * Название команды
	 */
    public String  m_name;
	/**
	 * Исполняемый файл
	 */
    public String  m_cmd;
	/**
	 * Аргументы запуска (полученные из строки с помощью класса CmdLineParser)
	 */
    public String  []m_args;
	/**
	 * Признак того, что m_cmd состоит из пути и имени файла
	 */
    public boolean m_bPrefixWithDir;

	/**
	 * Таймаут выполнения команды (для target=run не учитывается)
	 */
	public long m_timeOut = ExecuteConfig.m_defTimeOut;

	/**
	 * Конструктор класса
	 */
    public CommandArguments( String name, String cmd, String []args, boolean bPrefixDir, int timeOut ){
        m_name = name;
        m_cmd = cmd;
        m_args = args;
        m_bPrefixWithDir = bPrefixDir;
		m_timeOut = (long)timeOut;
    }
}
