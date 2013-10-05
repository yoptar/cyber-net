package prglabserver;

import java.io.*;
import java.util.*;
import dlc.Logger;

/**
 * ����� ��������� �������� ��� ������� ������� � ��������� ������
 */
public class CommandExecutor {
    
    CommandThread m_thread;
    int           m_sleepQuant = 500;
	boolean       m_bTimeout = false;
    
	/**
	 * ������������� ����� ��� ������� ������� (������ execCommand � input=null)
	 * @param cmd �������
	 * @param args ���������
	 * @param dir �������� ������� ��� ������� �������
	 * @param timeout ���������� ����� �� ���������� �������
	 * @param env ���������� ���������
	 * @return ������� ��������� ����������
	 */
    public boolean execCommand( String cmd, String []args, String dir, long timeout, HashMap env ){
        return execCommand( cmd, args, dir, timeout, env, null );
    }
    
	/**
	 * ����� ��� ������� �������
	 * @param cmd �������
	 * @param args ���������
	 * @param dir �������� ������� ��� ������� �������
	 * @param timeout ���������� ����� �� ���������� �������
	 * @param env ���������� ���������
	 * @param input ������ ��� ������ �� ������� �����
	 * @return ������� ��������� ���������� �������
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
        
        //������� ����������� � �������� ������ ������������ �������
        //������� ����������� � �������� ������ ������������ �������
        //������� ����������� � �������� ������ ������������ �������
        
        return true;
/*        
        if( m_thread.isStopped() )
            return true;
        
        m_thread.stopExecution();
        return false;
 */
    }

	/**
	 * ����� ��� ��������� ����� time out (����� ���������� ������� ��������� ���������� ������)
	 */
	public boolean isTimeout(){
		return m_bTimeout;
	}
    
	/**
	 * ����� ��� ��������� ������ � ��������� ������
	 */
    public String getResultOutput(){
        return m_thread.getStdOut();
    }
	/**
	 * ����� ��� ��������� ������ � ������ ������
	 */
    public String getResultErrors(){
        return m_thread.getStdError();
    }
    
	/**
	 * ����� ��� ��������� ���� ���������� �������
	 */
    public int getResultCode(){
        return m_thread.getResultCode();
    }

	/**
	 * ����� ��� ��������������� �������� ������ (�� ��������, ��������� � ����������� ������
	 */
	public void stopExecution(){
		try{ m_thread.stopExecution(); }catch( Exception exc ){
			Logger.log( "WARNING: CommandExecutor.stopExecution FAILED" );
		}
	}

	/**
	 * ����� ��� ��������� ��������� ������� ���������� �������
	 */
	public long getExecTime(){
		return m_thread.getExecTime();
	}

	/**
	 * ����� ���������� ���� - ������� �� �������
	 */
	public boolean isStarted(){
		if( m_thread == null )
			return false;
		return m_thread.isStarted();
	}
}
