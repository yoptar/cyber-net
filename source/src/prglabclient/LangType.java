package prglabclient;

/**
 * ����� ��� �������� ����� ����������������
 */
public class LangType{
	/**
	 * �������� ����� (������������ � �������)
	 */
    public String name;
	/**
	 * ������� �������� ����� (���������� �� ������)
	 */
    public String val;
	/**
	 * ������� ���������������� � �������� ��������
	 */
    public boolean bCaseSens = false;
	/**
	 * ����� ������� ���������������� ���������
	 */
    public String simpleProgram = "";

	/**
	 * �����������
	 */
    public LangType( String name, String val, boolean bCaseSens, String simpleProgram ){
        this.name = name;
        this.val = val;
        this.bCaseSens = bCaseSens;
        this.simpleProgram = simpleProgram;
    }
	/**
	 * ������������� �����, ���������� ���� name
	 */
    public String toString(){ 
        return name;
    }
}
