package dlc;


/**
 * �����, ����������� ��������� ��� ������ ������� ���������� ���������.
 */
public class ConditionForChecking {

    /**
     * ������������ ����� ����������
     */
    private long m_nTime;
    /**
     * ������������� �������
     */
    private int  m_ID;

    /**
     * ������� ����� ������
     */
    private String m_Input;
    /**
     * ��������� �������� ����� ������
     */
    private String m_Output;

    /**
	 * ����� ��������� ������� ������ ( � ���� ������ ����� )
	 */
    public void setInput(String input) {
        m_Input = input;
    }

    /**
	 * ����� ��� ��������� �������� ������ ( � ���� ������ ����� )
	 */
    public void setOutput(String output) {
        m_Output = output;
    }

    /**
	 * ����� ��� ��������� ������������� ������� ����������
	 */
    public void setTime(long time) {
        m_nTime = time;
    }

    /**
	 * ����� ��� ��������� ������������ ����� ����������
	 */
    public long getTime() {
        return m_nTime;
    }

    /**
	 * ����� ��� ��������� �������������� �������
	 */
    public void setID(int id) {
        m_ID = id;
    }

    /**
	 * ����� ��� ��������� �������������� �������
	 */
    public String getInput() {
        return m_Input;
    }

    /**
	 * ����� ��� ��������� �������� ������ �������
	 */
    public String getOutput() {
        return m_Output;
    }

    /**
	 * ����� ��� ��������� �������������� �������
	 */
    public String getID() {
        return Integer.toString(m_ID);
    }

    /**
     * ����� ���������� ����������
     */
    public void dumpInputOutput() {
        Logger.log(" ID: " + Integer.toString(m_ID));
        Logger.log(" Time: " + Long.toString(m_nTime));
        Logger.log(" Input: " + m_Input);
        Logger.log(" Output: " + m_Output);

    }
}