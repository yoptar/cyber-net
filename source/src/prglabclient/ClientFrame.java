package prglabclient;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

import prglabclient.codeedit.EditLineNumbersPane;
import dlc.util.*;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.HashMap;

import org.dom4j.*;
import org.dom4j.io.*;
import java.io.*;

import java.net.URL;

/**
 * Класс для просмотра виртуальной установки в отдельном окне (для отладки)
 */
public class ClientFrame extends JFrame{

    LangSelectPane m_langPane;
    CodeEditPane   m_codePane;
    boolean        m_bInit = false;

	/**
	 * Конструктор класса
	 */
    public ClientFrame(){
        super();
        setSize( 800, 500 );
        m_codePane = new CodeEditPane();
        m_codePane.init();
        getContentPane().add(m_codePane);
        setVisible( true );
    }

	/**
	 * Метод отрисовки перегружен для активации окна выбора языка (в первый момент после инициализации установки)
	 * и для активации функции подсветки синтаксиса.
	 */
    public void paint( Graphics g ){
        if( !m_bInit ){
            super.paint( g );

            LangSelectDlg dlg = new LangSelectDlg();
            LangType sel = dlg.getSelection();

            boolean bFound = false;

            for (int i = 0; i < ClientApplet.m_langTypes.length; i++) {
                if (ClientApplet.m_langTypes[i].val.equals( sel.val ) ){
                    m_codePane.selectProgramType(i);
                    bFound = true;
                    break;
                }
            }

            if( !bFound ){
                System.out.println( "Unknown language: " + sel.val );
            }
            
            m_bInit = true;
            m_codePane.enableInputs();
            
            this.paintComponents(g);
        }
    }
    
	/**
	 * Метод для запуска установки в отдельном окне
	 */
    public static void main( String []args ){
        new ClientFrame();
    }
}
