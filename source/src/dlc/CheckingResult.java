package dlc;

/**
 * �����, ����������� ��������� ���������� ��� ����������� �������� ������ ������
 */
public class CheckingResult {

    /**
     * ������, ���������� �� ������ ���������
     */
    private String m_sOutput = "";
    /**
     * ������������� ���� ��������/��������� �������
     */
    private String m_sID = "";
    /**
     * ����� ���������� ���������
     */
    private String m_sTime = "";
    /**
     * ��������� ���������� ��������� (������)
     */
    private String m_sResult = "";

	/**
	 * ��������� �������������� ������
	 */
    public void setID(String ID) {
        m_sID = ID;
    }

	/**
	 * ��������� �������������� ������
	 */
    public String getID() {
        return m_sID;
    }

	/**
	 * ��������� ����������� ������� ���������� ������
	 */
    public void setTime(long Time) {
        m_sTime = Long.toString(Time);
    }

	/**
	 * ��������� ����������� ������� ���������� ������
	 */
    public String getTime() {
        return m_sTime;
    }

	/**
	 * ��������� ���������� ���������� ������
	 */
    public void setResult(String Result) {
        m_sResult = Result;
    }

	/**
	 * ��������� ���������� ���������� ������
	 */
    public String getResult() {
        return m_sResult;
    }

	/**
	 * ��������� ��������� ������� �� ���������� ������
	 */
    public void setOutput(String Output) {
        m_sOutput = Output;
    }

	/**
	 * ��������� ��������� ������� �� ���������� ������
	 */
    public String getOutput() {
        return m_sOutput;
    }

    /**
     * ����� ���������� ����������
     */
    public void PrintRes() {
        Logger.log("ID: " + m_sID);
        Logger.log("Time: " + m_sTime);
        Logger.log("Output: " + m_sOutput);
        Logger.log("Result: " + m_sResult);
    }
}
