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
 * Аплет виртуальной лаборатории
 */
public class ClientApplet extends JApplet implements dlc.Console, ActionListener {
    
    public final static int LANG_C = 0;
    public final static int LANG_JAVA = 1;
    public final static int LANG_PAS = 2;

    /**
     * Список поддерживаемых языков
     */
    public static LangType[] m_langTypes = new LangType[]{
        new LangType("C++", "msvc", true, "#include <iostream>\n\nusing namespace std;\n\nint main(){\n\tcout<<\"Hello world!\"<<endl;\n\treturn 0;\n}"),
//        new LangType("Basic", "bas", false, "print \"Hello world!\""),
        new LangType("Java", "java", true, "public class Main{\n\n\tpublic static void main( String []args ){\n\t\tSystem.out.println(\"Hello world!\");\n\t}\n}"),
        new LangType("Pascal", "pas", false, "program simple;\n\nbegin\n\tWriteLn('Hello world!');\nEnd.\n")
    };
    LangSelectPane m_langPane;
    CodeEditPane m_codePane;
    
    boolean m_bInit = false;

    /**
     * Перегруженный метод инициализации аплета
     */
    public void init() {
        setLayout(new BorderLayout());

        m_langPane = new LangSelectPane(this);
        m_codePane = new CodeEditPane();
        m_codePane.init( getCodeBase() );
        
        m_codePane.enableInputs( false );

        getContentPane().add(m_codePane);

        if (getParameter("parameter") != null && getParameter("parameter").trim().length() > 0 ) {
            String param = getParameter("parameter");

            try {
                /**
                 * преобразования param:
                 * 0. unescape (after XSLT escaping-output)
                 * 1. unescape -> XML
                 * 2. SAXReader.read (parse entities)
                 * 3. unescape -> program
                 */

                param = HtmlParamEscaper.unescapeParam(param);
                param = HtmlParamEscaper.unescapeParam(param);

                Document doc = new SAXReader().read(new StringReader(param));
                String langType = doc.valueOf("//Program/@langType");
                String program = HtmlParamEscaper.unescapeParam(doc.valueOf("//Program/comment()"));

                //Теоретически - лишнее
                program = HtmlParamEscaper.unescapeParam(program);

                //m_langPane.setVisible( false );
                //getContentPane().remove( m_langPane );
                for (int i = 0; i < m_langTypes.length; i++) {
                    if (m_langTypes[i].val.equals(langType)) {

                        m_codePane.selectProgramType(i);
                        m_codePane.enableInputs();
                        m_bInit = true;

				        m_codePane.setProgramText(program);

                        break;
                    }
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }


/*
        if (!bInit) {
            getContentPane().add(m_langPane);
        }
 */
    }
    
    public void paint( Graphics g ){

        if( !m_bInit ){
			m_bInit = true;

            super.paint( g );

            LangSelectDlg dlg = new LangSelectDlg();
            LangType sel = dlg.getSelection();
            
            boolean bFound = false;
            
            for (int i = 0; i < m_langTypes.length; i++) {
                if (m_langTypes[i].val.equals( sel.val ) ){
                    m_codePane.selectProgramType(i);
                    bFound = true;
                    break;
                }
            }
            
            if( !bFound ){
                System.out.println( "Unknown language: " + sel.val );
            }
            
//            m_bInit = true;
            m_codePane.enableInputs();

            this.paintComponents(g);
        }

        super.paint(g);
    }

    /**
     * Обработка событий (переход с панели выбора языка)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == m_langPane) {
            m_langPane.setVisible(false);
            //getContentPane().remove( m_langPane );
            m_codePane.selectProgramType(m_langPane.m_iSelectedType);
            getContentPane().add(m_codePane);
            invalidate();
        }
    }

    /**
     * Реализация метода из интерфейса dlc.Console (возвращает ответ в формате XML)
     */
    public String getResults() {
        return m_codePane.getResults();
    }
}
