package prglabserver;

import java.util.*;

/**
 * Класс для описания всех команд, необходимых для компиляции и запуска испытуемой программы
 * для данного (m_type) языка программирования
 */
public class ExecuteConfig {

    public ArrayList<CommandArguments> m_commands;
    public String    m_type;
    public HashMap   m_env;
    public String    m_baseDir;
    public String    m_programFile;

	/**
	 * Таймаут выполнения команды (по умолчанию, в мсек)
	 */
	public final static long m_defTimeOut = 15000;
    
	/**
	 * Конструктор
	 * @param cmds список команд (компиляция, линковка, запуск)
	 * @param type язык программирования
	 * @param env переменные окружения
	 * @param baseDir домашний каталог
	 * @param имя исполняемого файла
	 */
    public ExecuteConfig( ArrayList<CommandArguments> cmds, String type, HashMap env,
            String baseDir, String programFile ){
        m_commands = new ArrayList<CommandArguments>( cmds );
        m_type = type;
        m_env = new HashMap( env );
        m_baseDir = baseDir;
        m_programFile = programFile;
    }
    
	/**
	 * Перегруженный метод - возвращает информацию о данной конфигурации
	 */
    public String toString(){
        String NL = "\r\n";
        StringBuffer res = new StringBuffer();
        res.append( "ExecuteConfig" + NL );
        res.append( " - type: " + m_type + NL );
        res.append( " - envVars: " + NL );
        for( Object o : m_env.keySet() ){
            res.append( "\t - " + o + "=" + m_env.get(o) + NL );
        }
        res.append( " - Commands: " + NL );
        for( CommandArguments ca : m_commands ){
            res.append( "\t - name: " + ca.m_name + NL );
            res.append( "\t - cmd: " + ca.m_cmd + NL );
            for( int i = 0; i < ca.m_args.length; i++ ){
                res.append( "\t\t [" + ca.m_args[i] + "]" + NL );
            }
        }
        return res.toString();
    }
}
