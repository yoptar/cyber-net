package dlc;

/**
 * Класс, реализующий результат выполнения для конкретного входного набора данных
 */
public class CheckingResult {

    /**
     * Данные, полученные на выходе программы
     */
    private String m_sOutput = "";
    /**
     * Идентификатор пары входного/выходного наборов
     */
    private String m_sID = "";
    /**
     * Время выполнения программы
     */
    private String m_sTime = "";
    /**
     * Результат выполнения программы (оценка)
     */
    private String m_sResult = "";

	/**
	 * установка идентификатора набора
	 */
    public void setID(String ID) {
        m_sID = ID;
    }

	/**
	 * получение идентификатора набора
	 */
    public String getID() {
        return m_sID;
    }

	/**
	 * установка допустимого времени выполнения набора
	 */
    public void setTime(long Time) {
        m_sTime = Long.toString(Time);
    }

	/**
	 * получение допустимого времени выполнения набора
	 */
    public String getTime() {
        return m_sTime;
    }

	/**
	 * установка результата выполнения работы
	 */
    public void setResult(String Result) {
        m_sResult = Result;
    }

	/**
	 * получение результата выполнения работы
	 */
    public String getResult() {
        return m_sResult;
    }

	/**
	 * установка состояния сервера по завершении работы
	 */
    public void setOutput(String Output) {
        m_sOutput = Output;
    }

	/**
	 * получение состояния сервера по завершении работы
	 */
    public String getOutput() {
        return m_sOutput;
    }

    /**
     * Вывод отладочной информации
     */
    public void PrintRes() {
        Logger.log("ID: " + m_sID);
        Logger.log("Time: " + m_sTime);
        Logger.log("Output: " + m_sOutput);
        Logger.log("Result: " + m_sResult);
    }
}
