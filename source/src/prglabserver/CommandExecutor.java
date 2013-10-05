package prglabserver;

import java.io.*;
import java.util.*;
import dlc.Logger;

/**
 * Класс реализует оболочку для запуска команды в отдельном потоке
 */
public class CommandExecutor {
    
    CommandThread m_thread;
    int           m_sleepQuant = 500;
	boolean       m_bTimeout = false;
    
	/**
	 * Перегруженный метод для запуска команды (запуск execCommand с input=null)
	 * @param cmd команда
	 * @param args аргументы
	 * @param dir домашний каталог для запуска команды
	 * @param timeout допустимой время на выполнение команды
	 * @param env переменные окружения
	 * @return признак успешного выполнения
	 */
    public boolean execCommand( String cmd, String []args, String dir, long timeout, HashMap env ){
        return execCommand( cmd, args, dir, timeout, env, null );
    }
    
	/**
	 * Метод для запуска команды
	 * @param cmd команда
	 * @param args аргументы
	 * @param dir домашний каталог для запуска команды
	 * @param timeout допустимой время на выполнение команды
	 * @param env переменные окружения
	 * @param input данные для записи во входной поток
	 * @return признак успешного выполнения команды
	 */
    public boolean execCommand( String cmd, String []args, String dir, long timeout, HashMap env, String input ){

		m_bTimeout = false;

        m_thread = new CommandThread( cmd, args, dir, env, input );

//System.out.println( "Max thread priority: " + Thread.MAX_PRIORITY );
//System.out.println( "Min thread priority: " + Thread.MIN_PRIORITY );

//System.out.println( "!!! Thread priority: " + m_thread.getPriority() );
//System.out.println( "\tcurrent priority: " + Thread.currentThread().getPriority() );
		m_thread.setPriority( Thread.MIN_PRIORITY );

        m_thread.start();

		m_thread.setPriority( Thread.MIN_PRIORITY );

		while( !m_thread.isStarted() ){
			try{
				Thread.sleep( 50 );
				Thread.yield();
			}catch( InterruptedException exc ){ break; }
		}

        long startTime = System.currentTimeMillis();
        while( !m_thread.isStopped() && (timeout <= 0 || (System.currentTimeMillis() - startTime) < timeout) ){

//        while( !m_thread.isStopped() ){

//            Logger.log( "ERR: ==>> " + m_thread.getStdError() + " <<== ERR" );
//            Logger.log( m_thread.getStdOut() );

            try{ Thread.sleep( m_sleepQuant ); Thread.yield(); }catch( InterruptedException exc ){ break; }
        }

		if( timeout > 0 && (System.currentTimeMillis() - startTime) >= timeout ){
			m_bTimeout = true;
			return false;
		}

//        Logger.log( m_thread.getStdError() );
//        Logger.log( m_thread.getStdOut() );
        
        //Таймаут учитывается в основном потоке проверяющего сервера
        //Таймаут учитывается в основном потоке проверяющего сервера
        //Таймаут учитывается в основном потоке проверяющего сервера
        
        return true;
/*        
        if( m_thread.isStopped() )
            return true;
        
        m_thread.stopExecution();
        return false;
 */
    }

	/**
	 * Метод для получения флага time out (время выполнения команды превысило допустимый предел)
	 */
	public boolean isTimeout(){
		return m_bTimeout;
	}
    
	/**
	 * Метод для получения данных с выходного потока
	 */
    public String getResultOutput(){
        return m_thread.getStdOut();
    }
	/**
	 * Метод для получения данных с потока ошибок
	 */
    public String getResultErrors(){
        return m_thread.getStdError();
    }
    
	/**
	 * Метод для получения кода завершения команды
	 */
    public int getResultCode(){
        return m_thread.getResultCode();
    }

	/**
	 * Метод для принудительного останова потока (по таймауту, заданному в проверяющем наборе
	 */
	public void stopExecution(){
		try{ m_thread.stopExecution(); }catch( Exception exc ){
			Logger.log( "WARNING: CommandExecutor.stopExecution FAILED" );
		}
	}

	/**
	 * Метод для получения реального времени выполнения команды
	 */
	public long getExecTime(){
		return m_thread.getExecTime();
	}

	/**
	 * Метод возвращает флаг - запущен ли процесс
	 */
	public boolean isStarted(){
		if( m_thread == null )
			return false;
		return m_thread.isStarted();
	}
}
