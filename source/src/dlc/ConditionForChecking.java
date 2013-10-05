package dlc;


/**
 * Класс, реализующий контейнер для набора условий выполнения программы.
 */
public class ConditionForChecking {

    /**
     * Максимальное время выполнения
     */
    private long m_nTime;
    /**
     * Идентификатор наборов
     */
    private int  m_ID;

    /**
     * Входной набор данных
     */
    private String m_Input;
    /**
     * Ожидаемый выходной набор данных
     */
    private String m_Output;

    /**
	 * Метод установки входных данных ( в виде набора строк )
	 */
    public void setInput(String input) {
        m_Input = input;
    }

    /**
	 * Метод для установки выходных данных ( в виде набора строк )
	 */
    public void setOutput(String output) {
        m_Output = output;
    }

    /**
	 * Метод для установки максимального времени выполнения
	 */
    public void setTime(long time) {
        m_nTime = time;
    }

    /**
	 * Метод для получения максимальное время выполнения
	 */
    public long getTime() {
        return m_nTime;
    }

    /**
	 * Метод для установки идентификатора условия
	 */
    public void setID(int id) {
        m_ID = id;
    }

    /**
	 * Метод для получения идентификатора условия
	 */
    public String getInput() {
        return m_Input;
    }

    /**
	 * Метод для получения выходных данных сервера
	 */
    public String getOutput() {
        return m_Output;
    }

    /**
	 * Метод для получения идентификатора условия
	 */
    public String getID() {
        return Integer.toString(m_ID);
    }

    /**
     * Вывод отладочной информации
     */
    public void dumpInputOutput() {
        Logger.log(" ID: " + Integer.toString(m_ID));
        Logger.log(" Time: " + Long.toString(m_nTime));
        Logger.log(" Input: " + m_Input);
        Logger.log(" Output: " + m_Output);

    }
}