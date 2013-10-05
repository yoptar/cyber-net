package prglabclient.codeedit;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.undo.*;
import java.util.*;

/**
 * Класс реализующий UndoManager с заданными точками "отката"
 */
public class UndoBlockingManager extends UndoManager{
    public boolean m_bBlockSaveUndo = false;
    
    /**
     * Significant changes (used for undoTo, redoTo)
     */
    ArrayList<UndoableEdit> m_sigList = new ArrayList<UndoableEdit>();
    int                     m_sigIndex = 0;
    boolean m_bNextSignificant = false;
    
    ActionListener m_listener;
    Object         m_source;
    
    /**
     * Конструктор
     * @param listener наблюдатель за состоянием списка undo/redo (пользовательский интерфейс)
     * @param source объект для передачи при отправке сообщения с событием
     */
    public UndoBlockingManager( ActionListener listener, Object source ){
        m_listener = listener;
        m_source = source;
    }
    
	/**
	 * Метод для сброса всей истории Undo/Redo
	 */
    public void discardAllEdits(){
        super.discardAllEdits();
        m_sigList.clear();
        m_sigIndex = 0;
        m_bNextSignificant = false;
        
        setLimit( 0 );
        
        if( m_listener != null )
            m_listener.actionPerformed( new ActionEvent(m_source, 0, "") );
        
        setLimit( -1 );
        
//        System.out.println( "discardAllEdits - CanUndo = " + canUndo() );
    }

	/**
	 * Начало новой истории Undo/Redo
	 */
    public void end(){
//        super.end();
        m_sigList.clear();
        m_sigIndex = 0;
        m_bNextSignificant = false;
        if( m_listener != null )
            m_listener.actionPerformed( new ActionEvent(m_source, 0, "") );

//        System.out.println( "end() - CanUndo = " + canUndo() );
    }

    /**
     * Метод для отметки следующего события unoableEventHappened как важное
     * (по важным событиям строится собственный список и методы undo/redo обрабатывают
     * несколько реальных записей в собственных списках)
     */
    public void markSignificant(){
        m_bNextSignificant = true;
    }

    /**
     * Реализация метода из класса UndoManager
     */
    public void undoableEditHappened( UndoableEditEvent e ){
        if( m_bNextSignificant ){
            if( m_sigIndex < m_sigList.size() && m_sigIndex > 0 )
                m_sigList = new ArrayList<UndoableEdit>( m_sigList.subList( 0, m_sigIndex ) );
            else if( m_sigIndex == 0 )
                m_sigList = new ArrayList<UndoableEdit>();
            
            m_sigList.add( e.getEdit() );
            m_bNextSignificant = false;
            m_sigIndex++;
        }
        m_listener.actionPerformed( new ActionEvent(m_source, 0, "") );
        super.undoableEditHappened( e );
    }
    
    /**
     * Перегруженный метод для отката до следующего &quot;важного&quot; исправления
     */
    public void undo() throws CannotUndoException{
        if( m_sigIndex > 0 && (m_sigIndex <= m_sigList.size()) ){
            m_sigIndex--;
            undoTo( m_sigList.get( m_sigIndex ) );

//System.out.println("Undo [" + m_sigIndex + "/" + m_sigList.size() + "]" );
        }
        m_listener.actionPerformed( new ActionEvent(m_source, 0, "") );
    }
    
    /**
     * Перегруженный метод для повтора до следующего &quot;важного&quot; исправления
     */
    public void redo() throws CannotRedoException{
        if( m_sigIndex >= 0 && m_sigIndex < m_sigList.size() ){
            redoTo( m_sigList.get( m_sigIndex ) );
            m_sigIndex++;

//System.out.println("Redo [" + m_sigIndex + "/" + m_sigList.size() + "]" );

        }
        m_listener.actionPerformed( new ActionEvent(m_source, 0, "") );
    }
}
