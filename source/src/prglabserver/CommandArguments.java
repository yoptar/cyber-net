package prglabserver;

/**
 * ����� ��� �������� ������� ������������ ������� � �����������
 */
public class CommandArguments {
	/**
	 * �������� �������
	 */
    public String  m_name;
	/**
	 * ����������� ����
	 */
    public String  m_cmd;
	/**
	 * ��������� ������� (���������� �� ������ � ������� ������ CmdLineParser)
	 */
    public String  []m_args;
	/**
	 * ������� ����, ��� m_cmd ������� �� ���� � ����� �����
	 */
    public boolean m_bPrefixWithDir;

	/**
	 * ������� ���������� ������� (��� target=run �� �����������)
	 */
	public long m_timeOut = ExecuteConfig.m_defTimeOut;

	/**
	 * ����������� ������
	 */
    public CommandArguments( String name, String cmd, String []args, boolean bPrefixDir, int timeOut ){
        m_name = name;
        m_cmd = cmd;
        m_args = args;
        m_bPrefixWithDir = bPrefixDir;
		m_timeOut = (long)timeOut;
    }
}
