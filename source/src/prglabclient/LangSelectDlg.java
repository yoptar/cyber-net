/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prglabclient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Диалог выбора языка программирования
 * @author ove
 */
public class LangSelectDlg extends JDialog implements ActionListener{
    
    JComboBox m_langType;
    JButton   m_bNext;
    
	/**
	 * Конструктор класса
	 */
    public LangSelectDlg(){
        super();
        this.setTitle( LocaleData.getInstance().getString("chooselanguage") );
        setModal( true );
        //setSize( 300, 100 );
        initUI();
        setResizable( false );
        pack();
        
        Dimension scrDims = getToolkit().getScreenSize();
        Dimension dlgDims = getSize();
        setLocation( scrDims.width/2 - dlgDims.width/2, scrDims.height/2 - dlgDims.height/2 );
        
        setVisible( true );
    }
    
    private void initUI(){
        m_langType = new JComboBox();
        for( int i = 0; i < ClientApplet.m_langTypes.length; i++ ){
            m_langType.addItem( ClientApplet.m_langTypes[i] );
        }
        
        GridBagLayout      l = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        getContentPane().setLayout( l );
        
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = c.REMAINDER;
        c.gridheight = 1;
        c.fill = c.HORIZONTAL;
        c.insets = new Insets( 10, 5, 5, 5 );
        
        JLabel langLabel = new JLabel( LocaleData.getInstance().getString("prglanguage") + ": " );
        
        l.setConstraints( langLabel, c );
        getContentPane().add( langLabel );
        
        l.setConstraints( m_langType, c );
        getContentPane().add( m_langType );
        
        m_bNext = new JButton( LocaleData.getInstance().getString("continue") );
        m_bNext.addActionListener( this );
        c.fill = c.NONE;
        l.setConstraints( m_bNext, c );
        getContentPane().add( m_bNext );
    }
    
	/**
	 * Обработка события выбора языка
	 */
    public void actionPerformed( ActionEvent e ){
        if( e.getSource() == m_bNext )
            dispose();
    }
    
	/**
	 * Метод для получения структуры LangType для выбранного языка
	 */
    public LangType getSelection(){
        return (LangType)m_langType.getSelectedItem();
    }
}
