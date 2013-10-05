package dlc;

import java.util.*;

/**
 * Класс, реализующий среду выполнения программы на стороне проверяющего сервера.
 */
public class Program {

    /**
     * проверяющие наборы, максимальное время выполнения и идентификатор входного/выходного наборов
     * (экземпляры ConditionForChecking)
     */
    private Vector m_Conditions = new Vector();//условия( входные и выходные)
    /**
     * Результаты работы для всех элементов m_Conditions
     * (экземпляры CheckingResult)
     */
    private Vector m_Response = new Vector();
    /**
     * Текст программы
     */
    private String m_program = "";

    /**
     * Класс реализует дополнительный поток для запуска экземпляра Processor
     */
    public class ProgramThread extends Thread {

        Processor m_proc;
        String m_input;
        String m_output;
        String[] m_outputRef;
        boolean m_bRes = false;
        boolean m_bStopped = false;

        /**
         * Конструктор класса
         * @param proc экземпляр класса Processor
         * @param input входной набор данных
         * @param output эталонный выходной набор данных
         * @param outputRef выходной параметр, в который записывается состояние сервера на момент завершение работы
         */
        public ProgramThread(Processor proc, String input, String output, String[] outputRef) {
            m_proc = proc;
            m_input = input;
            m_output = output;
            m_outputRef = outputRef;
        }

        /**
         * Метод класса Thread. Запускает Processor.run, с полученными в конструкторе данными
         */
        public void run() {

            try {
                m_bRes = m_proc.run(m_input, m_output);
                m_outputRef[0] = m_proc.getOutput();
            } catch (Exception exc) {
                m_bRes = false;
                m_outputRef[0] = "0";
            }

            m_bStopped = true;
        }

        /**
         * Метод для получения результата проверки ответа
         */
        public boolean getResult() {
            return m_bRes;
        }

        /**
         * Метод для получения состояния сервера на момент завершения работы
         */
        public String getOutput() {
            return m_outputRef[0];
        }

        /**
         * Метод для проверки самостоятельного останова работы (в случае успешного и своевременного выполнения Processor.run)
         */
        public boolean isStopped() {
            return m_bStopped;
        }
    }

    /**
     * Добавление входного условия
     */
    public void addCondition(ConditionForChecking cond) {
        m_Conditions.add((ConditionForChecking) cond);
    }

    /**
     * Метод для установки текста программы
     * @param code исходный код
     */
    public void setCode(String code) {
        if (code == null) {
            m_program = "";
        } else {
            m_program = code;
        }
    }

    /**
     * Метод, запускающий программу на выполнение для всех проверяющих наборов
     * @return список экземпляров CheckingResult
     */
    public Vector runForCheck(Config config, Processor proc) {

        try {

            m_Response.clear();

            proc.init(m_program);

//Logger.log("\t - Starting. Total count of condition: " + m_Conditions.size() );

            boolean bWasTimeout = false;
            long timeOutTime = 0;

            for (int i = 0; i < m_Conditions.size(); i++) {

                CheckingResult chResult = new CheckingResult();
                ConditionForChecking cfCheck = (ConditionForChecking) m_Conditions.elementAt(i);

                if (bWasTimeout) {
                    CheckingResult timeOutRes = new CheckingResult();
                    timeOutRes.setTime(timeOutTime);
                    timeOutRes.setResult("0");
                    timeOutRes.setOutput("Time out at [run]");
                    timeOutRes.setID(cfCheck.getID());
                    m_Response.addElement(timeOutRes);
                    continue;
                }

//cfCheck.dumpInputOutput();

                chResult.setID(cfCheck.getID());
                chResult.setTime(0);
                chResult.setOutput("0");

//Logger.log("\t - running for condition: " + i + ", ID: " + cfCheck.getID() );

                long timeLimit = cfCheck.getTime() * 1000;

                if (timeLimit < 0) {
                    timeLimit = config.getDebugTimeout();
                }

//Logger.log( "Program.runForCheck - debug timeout: " + timeLimit );

                String output = "";
                try {

                    // TODO: добавить ограничение по времени
                    // TODO: добавить ограничение по времени
                    // TODO: добавить ограничение по времени

                    String[] outRef = new String[]{output};

                    ProgramThread pThread = new ProgramThread(proc, cfCheck.getInput(), cfCheck.getOutput(), outRef);
                    pThread.setPriority(Thread.currentThread().getPriority() - 1);
                    pThread.start();

                    System.out.println("Waiting for starting...");

                    while (!((prglabserver.CommandProcessor) proc).isStarted()) {
                        Thread.sleep(50);
                        Thread.yield();
                    }

                    long startTime = System.currentTimeMillis();

                    System.out.println("Waiting for stopping...");

                    while ((System.currentTimeMillis() - startTime) < timeLimit
                            && !pThread.isStopped()) {

                        Thread.sleep(50);
                        Thread.yield();
                    }
                    //was long execTime = System.currentTimeMillis() - startTime;
                    long execTime = ((prglabserver.CommandProcessor) proc).getExecTime();

                    System.out.println("Exec time: " + execTime);

//Logger.log( "Terminating thread... (count=" + Thread.activeCount() + ")" );

                    try {
                        pThread.interrupt();
                    } catch (Exception interruptExc) {
                    }

//Logger.log( "Thread terminated! (count=" + Thread.activeCount() + ")" );

                    chResult.setTime(execTime);

//Logger.log("TestSetOutput: " + cfCheck.getOutput() );
//Logger.log("UserOutput   : " + outRef[0] );
                    if (execTime >= timeLimit) {
                        chResult.setResult("0");
                        chResult.setOutput("Time out at [run]");

                        if (i == 0) {

                            Logger.log("Program.runForCheck: TOGGLE TIMEOUT for\n\tConditionForChecking.ID=" + cfCheck.getID());

                            bWasTimeout = true;
                            timeOutTime = execTime;
                        }
                    } else {
                        if (pThread.getResult()) {
                            chResult.setResult("1");
                        } else {
                            chResult.setResult("0");
                        }
                        chResult.setOutput(outRef[0]);
                    }

                    ((prglabserver.CommandProcessor) proc).stopExecution();

                } catch (Exception runExc) {
                    runExc.printStackTrace();
                }

                chResult.PrintRes();
                m_Response.addElement(chResult);
            }

        } catch (Exception exc) {
            Logger.log("Program.runForCheck( Processor ) - FAILED: " + exc.getMessage());
        } finally {
            Logger.log("Program.runForCheck() - finally release Processor.");
            try {
                proc.release();
            } catch (Exception finExc) {
                Logger.log(finExc);
            }
        }

        return m_Response;
    }
}
