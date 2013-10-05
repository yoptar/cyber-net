package prglabclient;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Класс панели для изначального выбора языка программирования
 */
public class LangSelectPane extends JPanel implements ActionListener {
    
    JComboBox m_langType;
    JButton   m_bNext;
    ActionListener m_listener;
    
    public int  m_iSelectedType = 0;
    
	/**
	 * Конструктор класса
	 * @param listener слушатель событий, который будет активирован после выбора
	 */
    public LangSelectPane( ActionListener listener ){
        
        m_listener = listener;
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout( gbl );
        
        m_langType = new JComboBox();
        for( int i = 0; i < ClientApplet.m_langTypes.length; i++ ){
            m_langType.addItem( ClientApplet.m_langTypes[i] );
        }
        m_bNext = new JButton( LocaleData.getInstance().getString("next") + " >>" );
        
        c.fill = c.NONE;
        c.anchor = c.SOUTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = c.REMAINDER;
        
        JLabel lTypeLabel = new JLabel( LocaleData.getInstance().getString("chooselanguage") + ":" );
        gbl.setConstraints( lTypeLabel, c );
        add( lTypeLabel );
        
        c.weighty = 1.0;
        c.anchor = c.NORTHEAST;
        c.gridwidth = 1;
        c.insets = new Insets( 5, 5, 5, 5 );
        
        gbl.setConstraints( m_langType, c );
        add( m_langType );
        c.gridwidth = c.REMAINDER;
        c.anchor = c.NORTHWEST;
        gbl.setConstraints( m_bNext, c );
        add( m_bNext );
        m_bNext.addActionListener( this );
    }
    
	/**
	 * Обработчик подтверждения выбора языка (реакция на кнопку m_bNext)
	 */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_bNext && m_listener != null ){
            m_iSelectedType = m_langType.getSelectedIndex();
            m_listener.actionPerformed( new ActionEvent(this, 0, "") );
        }
    }
/*    
    public static void main( String []args ){
        JFrame frm = new JFrame();
        frm.getContentPane().add( new LangSelectPane( null ) );
        frm.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frm.setSize( 500, 300 );
        frm.setVisible( true );
    }
*/
}
