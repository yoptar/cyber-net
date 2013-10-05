package prglabclient;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

import prglabclient.codeedit.EditLineNumbersPane;

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
 * Панель редактирования кода
 */
public class CodeEditPane extends JPanel implements dlc.Console, ActionListener, ItemListener {

    JComboBox m_chPrgType;
    int m_prevPrgType = -1;
    EditLineNumbersPane m_taCode;
    JTextArea m_taInput;
    JTextArea m_taOutput;
    JButton m_bTest;
    JTabbedPane m_inoutPane;
    JSplitPane m_codeInOutSplit;
    JButton m_bUndo;
    JButton m_bRedo;
    JButton m_bDebug;
    UndoManager m_undoManager;
    HashMap m_keywords = new HashMap();
    URL m_serverURL;
    boolean m_bInit = true;
    LocaleData m_locData = LocaleData.getInstance();

    /**
     * Разблокировка элементов управления
     */
    public void enableInputs() {
        enableInputs(true);
    }

    /**
     * Блокировка/разблокировка элементов управления
     */
    public void enableInputs(boolean bEnable) {
        m_chPrgType.setEnabled(bEnable);
        m_taCode.getEditorPane().setEnabled(bEnable);
        m_taInput.setEnabled(bEnable);
        m_taOutput.setEnabled(bEnable);
        m_inoutPane.setEnabled(bEnable);
//        m_bUndo.setEnabled( bEnable );
//        m_bRedo.setEnabled( bEnable );
        m_bDebug.setEnabled(bEnable);
    }

    protected ImageIcon loadResourceIcon(String name) {
        Image img = loadResource(name);
        return new ImageIcon(img);
    }

    /**
     * Метод для указания языка программирования. При его вызове в поле ввода
     * кода записывается простая программа на языке.
     */
    public void selectProgramType(int iType) {
        m_prevPrgType = iType;
        m_chPrgType.setSelectedIndex(iType);
        ((prglabclient.codeedit.SyntaxDocument) m_taCode.getDocument()).setKeywords(
                (Vector) m_keywords.get(ClientApplet.m_langTypes[iType].val),
                ClientApplet.m_langTypes[iType].bCaseSens);
//        m_taCode.getEditorPane().getDocument().setText( ClientApplet.m_langTypes[iType].simpleProgram );
        setProgramText(ClientApplet.m_langTypes[iType].simpleProgram);
    }

    /**
     * Установка исходного кода программы в поле ввода текста (используется при
     * десериализации ответа из параметра аплета)
     */
    public void setProgramText(String program) {
//        m_taCode.getEditorPane().setText( program );
        try {
            m_taCode.getEditorPane().getDocument().remove(0, m_taCode.getEditorPane().getDocument().getLength());
            m_taCode.getEditorPane().getDocument().insertString(0, program, null);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        m_undoManager.discardAllEdits();
    }

    protected Image loadResource(String name) {
        Image res = null;
        try {
            name = "/" + name;
            InputStream is = getClass().getResourceAsStream(name);
            int len = (int) is.available();
            byte[] img = new byte[len];
            is.read(img);
            is.close();
            res = getToolkit().createImage(img, 0, len);

            MediaTracker mt = new MediaTracker(this);
            mt.addImage(res, 0);
            mt.waitForAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Инициализация по умолчанию, хост сервера - 127.0.0.1
     */
    public void init() {
        try {
            m_serverURL = new URL("127.0.0.1");
        } catch (Exception exc) {
            //This should not be! :)
            exc.printStackTrace();
        }
        init(m_serverURL);
    }

    /**
     * Метод инициализации панели
     */
    public void init(URL appletCodeBase) {

        m_serverURL = appletCodeBase;

        m_keywords.put(ClientApplet.m_langTypes[ ClientApplet.LANG_C].val,
                new Vector(Arrays.asList(new String[]{
            //http://www.cppreference.com/keywords/all.html
            "asm", "auto", "bool", "break", "case", "catch", "char", "class",
            "const", "const_cast", "continue", "default", "delete",
            "do", "double", "dynamic_cast", "delete", "else", "enum", "explicit",
            "export", "extern", "false", "float", "for", "friend",
            "goto", "if", "inline", "int", "long", "mutable", "namespace",
            "new", "operator", "private", "protected", "public", "register",
            "reinterpret_cast", "return", "short", "signed", "sizeof", "static",
            "static_cast", "struct", "switch", "template", "this", "throw",
            "true", "try", "typedef", "typeid", "typename", "union", "unsigned",
            "using", "virtual", "void", "volatile", "wchar_t", "while"
        })));
        m_keywords.put(ClientApplet.m_langTypes[ ClientApplet.LANG_PAS].val, new Vector(Arrays.asList(new String[]{
            //http://gnu-pascal.de/gpc/Keywords.html#Keywords - полный список
            "absolute",
            "abstract",
            "all",
            "and",
            "and_then",
            "array",
            "as",
            "asm",
            "asmname",
            "attribute",
            "begin",
            "bindable",
            "c",
            "case",
            "c_language",
            "class",
            "const",
            "constructor",
            "destructor",
            "div",
            "do",
            "downto",
            "else",
            "end",
            "export",
            "exports",
            "external",
            "far",
            "file",
            "finalization",
            "for",
            "forward",
            "function",
            "goto",
            "if",
            "implementation",
            "import",
            "in",
            "inherited",
            "initialization",
            "interface",
            "interrupt",
            "is",
            "label",
            "library",
            "mod",
            "module",
            "name",
            "near",
            "nil",
            "not",
            "object",
            "of",
            "only",
            "operator",
            "or",
            "or_else",
            "otherwise",
            "packed",
            "pow",
            "private",
            "procedure",
            "program",
            "property",
            "protected",
            "public",
            "published",
            "qualified",
            "record",
            "repeat",
            "resident",
            "restricted",
            "segment",
            "set",
            "shl",
            "shr",
            "then",
            "to",
            "type",
            "unit",
            "until",
            "uses",
            "value",
            "var",
            "view",
            "virtual",
            "while",
            "with",
            "xor"
        })));
        /*
         m_keywords.put( ClientApplet.m_langTypes[ ClientApplet.LANG_BAS ].val, new Vector( Arrays.asList( new String[]{
         //http://www.yabasic.de/yabasic.htm#reserved_word
         "ABS",
         "ACOS",
         "AND",
         "ARRAYDIM",
         "ARRAYDIMENSION",
         "ARRAYSIZE",
         "AS",
         "ASC",
         "ASIN",
         "AT",
         "ATAN",
         "BEEP",
         "BELL",
         "BIN$",
         "BIND",
         "BITBLIT",
         "BITBLIT$",
         "BITBLT",
         "BITBLT$",
         "BOX",
         "BREAK",
         "CASE",
         "CHR$",
         "CIRCLE",
         "CLEAR",
         "CLOSE",
         "COLOR",
         "COLOUR",
         "COMPILE",
         "CONTINUE",
         "COS",
         "CURVE",
         "DATA",
         "DATE$",
         "DEC",
         "DEFAULT",
         "DIM",
         "DO",
         "DOT",
         "ELSE",
         "ELSEIF",
         "ELSIF",
         "END",
         "ENDIF",
         "EOF",
         "EOR",
         "ERROR",
         "EXECUTE",
         "EXECUTE$",
         "EXIT",
         "EXP",
         "EXPORT",
         "FI",
         "FILL",
         "FILLED",
         "FOR",
         "FRAC",
         "GETBIT$",
         "GETSCREEN$",
         "GLOB",
         "GOSUB",
         "GOTO",
         "HEX$",
         "IF",
         "INKEY$",
         "INPUT",
         "INSTR",
         "INT",
         "INTERRUPT",
         "LABEL",
         "LEFT$",
         "LEN",
         "LET",
         "LINE",
         "LOCAL",
         "LOG",
         "LOOP",
         "LOWER$",
         "LTRIM$",
         "MAX",
         "MID$",
         "MIN",
         "MOD",
         "MOUSEB",
         "MOUSEBUTTON",
         "MOUSEMOD",
         "MOUSEMODIFIER",
         "MOUSEX",
         "MOUSEY",
         "NEW",
         "NEXT",
         "NOT",
         "NUMPARAM",
         "ON",
         "OPEN",
         "OR",
         "ORIGIN",
         "PAUSE",
         "PEEK",
         "PEEK$",
         "POKE",
         "PRINT",
         "PRINTER",
         "PUTBIT",
         "PUTSCREEN",
         "RAN",
         "READ",
         "READING",
         "RECT",
         "RECTANGLE",
         "REDIM",
         "REPEAT",
         "RESTORE",
         "RETURN",
         "REVERSE",
         "RIGHT$",
         "RINSTR",
         "RTRIM$",
         "SCREEN",
         "SEEK",
         "SIG",
         "SIN",
         "SLEEP",
         "SPLIT",
         "SPLIT$",
         "SQR",
         "SQRT",
         "STATIC",
         "STEP",
         "STR$",
         "SUB",
         "SUBROUTINE",
         "SWITCH",
         "SYSTEM",
         "SYSTEM$",
         "TAN",
         "TELL",
         "TEXT",
         "THEN",
         "TIME$",
         "TO",
         "TOKEN",
         "TOKEN$",
         "TRIANGLE",
         "TRIM$",
         "UNTIL",
         "UPPER$",
         "USING",
         "VAL",
         "WAIT",
         "WEND",
         "WHILE",
         "WINDOW",
         "WRITING",
         "XOR"
         })));
         */
        m_keywords.put(ClientApplet.m_langTypes[ ClientApplet.LANG_JAVA].val, new Vector(Arrays.asList(new String[]{
            //http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
            "abstract", "continue", "for", "new", "switch", "assert", "default",
            "goto", "package", "synchronized", "boolean", "do", "if", "private", "this",
            "break", "double", "implements", "protected", "throw", "byte",
            "else", "import", "public", "throws", "case", "enum", "instanceof",
            "return", "transient", "catch", "extends", "int",
            "short", "try", "char", "final", "interface", "static",
            "void", "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while"
        })));

        m_chPrgType = new JComboBox(ClientApplet.m_langTypes);
        m_prevPrgType = 0;
        m_chPrgType.setEditable(false);
        m_chPrgType.addItemListener(this);
        m_taCode = new EditLineNumbersPane((Vector) m_keywords.get(ClientApplet.m_langTypes[ m_prevPrgType].val), ClientApplet.m_langTypes[ m_prevPrgType].bCaseSens);

        m_undoManager = new prglabclient.codeedit.UndoBlockingManager(this, m_taCode);

        m_taInput = new JTextArea();
        m_taOutput = new JTextArea();
        m_taOutput.setEditable(false);

        m_taCode.getDocument().addUndoableEditListener(m_undoManager);
        m_taCode.getEditorPane().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Z && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && m_undoManager.canUndo()) {
                    m_undoManager.undo();
                } else if (e.getKeyCode() == KeyEvent.VK_Y && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && m_undoManager.canRedo()) {
                    m_undoManager.redo();
                }
            }
        });

        ImageIcon[] icons = new ImageIcon[3];
        try {
            icons[0] = loadResourceIcon("resources/images/undo.png");
            icons[1] = loadResourceIcon("resources/images/redo.png");
            icons[2] = loadResourceIcon("resources/images/run.png");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        m_bUndo = new JButton(icons[0]);
        m_bUndo.setToolTipText(m_locData.getString("undo"));
        m_bRedo = new JButton(icons[1]);
        m_bRedo.setToolTipText(m_locData.getString("redo"));
        m_bDebug = new JButton(icons[2]);
        m_bDebug.setToolTipText(m_locData.getString("exec"));
        m_bUndo.addActionListener(this);
        m_bRedo.addActionListener(this);
        m_bDebug.addActionListener(this);
        m_bUndo.setEnabled(false);
        m_bRedo.setEnabled(false);

        JToolBar jTB = new JToolBar();
        jTB.setFloatable(false);
        jTB.add(m_bUndo);
        jTB.add(m_bRedo);
        jTB.add(m_bDebug);

        JPanel typePane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        typePane.add(new JLabel(m_locData.getString("prglanguage") + ": "));
        typePane.add(m_chPrgType);

        JPanel tbPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tbPane.add(jTB);
        JPanel topPane = new JPanel(new GridLayout(1, 2));
        topPane.add(tbPane);
        topPane.add(typePane);

        m_inoutPane = new JTabbedPane();
        m_inoutPane.addTab(m_locData.getString("inputstream"),
                new JScrollPane(m_taInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        m_inoutPane.addTab(m_locData.getString("outputstream"),
                new JScrollPane(m_taOutput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        //m_codeInOutSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, m_taCode.getEditPane(), m_inoutPane );
        m_codeInOutSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_taCode, m_inoutPane);

        JPanel centerPane = new JPanel(new BorderLayout());
        centerPane.add(m_codeInOutSplit);
        centerPane.add( /*typePane*/topPane, BorderLayout.NORTH);

        setLayout(new BorderLayout());

        add(centerPane);
        /*
         if( getParameter("debug-on") != null && getParameter("debug-on").equals( "true" ) ){
         m_bTest = new JButton( "Отправить запрос на сервер [debug]");
         getContentPane().add( m_bTest, BorderLayout.SOUTH );
         m_bTest.addActionListener( this );
         }
         */
        /**
         * TODO: 1. restore source code and program type 2. set it to
         * m_chPrgType and m_taCode
         */
    }

    /**
     * Метод paint перегружен для установки положения панелей в SplitPane
     */
    public void paint(Graphics g) {
        super.paint(g);
        if (m_bInit) {
            m_bInit = false;
            m_codeInOutSplit.setDividerLocation(0.7);
        }
    }

    /**
     * Реализация метода из интерфейса ItemListener (обработка выбора языка
     * программирования)
     */
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() != e.SELECTED) {
            return;
        }

        if (e.getSource() == m_chPrgType) {

//System.out.println( m_prevPrgType + "/" + m_chPrgType.getSelectedIndex() );

            if (m_prevPrgType == m_chPrgType.getSelectedIndex()) {
                return;
            }

//System.out.println( "\tProcessing change of state..." );

            if (m_undoManager.canUndo()) {
                if (JOptionPane.showConfirmDialog(this, m_locData.getString("changewarning"), m_locData.getString("warning"), //Внимание, при смене языка программирования\nв область исходного кода будет вставлен пример программы на выбранном языке. Продолжить?", "Внимание",
                        JOptionPane.YES_NO_OPTION)
                        != JOptionPane.YES_OPTION) {
                    m_chPrgType.setSelectedIndex(m_prevPrgType);
                    return;
                }
            }

            m_prevPrgType = m_chPrgType.getSelectedIndex();

            LangType lt = (LangType) m_chPrgType.getSelectedItem();

            System.out.println("Lang: " + lt);

            ((prglabclient.codeedit.SyntaxDocument) m_taCode.getDocument()).setKeywords(
                    (Vector) m_keywords.get(lt.val), lt.bCaseSens);
            try {
                //((prglabclient.codeedit.UndoBlockingManager)m_undoManager).
//                m_taCode.getEditorPane().getDocument().insertString( 0, "\n", null );
//                m_taCode.getEditorPane().getDocument().remove( 0, 1 );
                ((prglabclient.codeedit.SyntaxDocument) m_taCode.getEditorPane().getDocument()).processChangedLines(0,
                        m_taCode.getEditorPane().getDocument().getLength());
            } catch (Exception exc) {
            }

            //clear all and print our simple program for this language
/*
             m_taCode.getEditorPane().setText( lt.simpleProgram );
            
             m_undoManager.discardAllEdits();
             */
            setProgramText(lt.simpleProgram);
        }
    }

    /**
     * Реализация метода из интерфейса ActionListener
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == m_bTest || e.getSource() == m_bDebug) {

//System.out.println( getResults() );

            String results = getResults();
            System.out.println("Answer size: " + results.getBytes().length);
            if (results.getBytes().length > 30000) {
                JOptionPane.showMessageDialog(this, m_locData.getString("sizewarning"), m_locData.getString("warning"), JOptionPane.WARNING_MESSAGE);// "Размер текста превышает 25 тыс. символов", "Внимание", JOptionPane.WARNING_MESSAGE );
                return;
            }

            String answer = new common.AppletDebug().doCheckPost(m_taInput.getText(), "", getResults(), m_serverURL.getHost());
//            String answer = new common.AppletDebug().doCheckPost( m_taInput.getText(), "", getResults(), "192.168.2.2" );
            m_inoutPane.setSelectedIndex(1);
            if (answer == null) {
                m_taOutput.setText(m_locData.getString("serverconnerror"));
            } else {
                try {
                    if (answer.indexOf("<?") >= 0) {
                        answer = answer.substring(answer.indexOf("<?"));
                        Document doc = new SAXReader().read(new StringReader(answer));
                        String NL = System.getProperty("line.separator");
                        String srvAnswer = (doc.selectSingleNode("//CheckingResult/comment()") == null ? ""
                                : doc.selectSingleNode("//CheckingResult/comment()").getText());
                        srvAnswer = dlc.util.HtmlParamEscaper.unescapeParam(srvAnswer);
                        m_taOutput.setText(m_locData.getString("serverresponse") + ": " + NL + srvAnswer + NL);
                    } else {
                        m_taOutput.setText(m_locData.getString("errorrespproc") + ": " + answer.trim());
                    }
                } catch (Exception exc) {
                    m_taOutput.setText(m_locData.getString("errordocproc") + ": " + exc);
                }
            }
        } else if (e.getSource() == m_bUndo) {
            m_undoManager.undo();
        } else if (e.getSource() == m_bRedo) {
            m_undoManager.redo();
        } else if (e.getSource() == m_taCode) {

            if (m_undoManager.canUndo()) {
                m_bUndo.setEnabled(true);
            } else {
                m_bUndo.setEnabled(false);
            }

            if (m_undoManager.canRedo()) {
                m_bRedo.setEnabled(true);
            } else {
                m_bRedo.setEnabled(false);
            }
        }
    }

    /**
     * Метод формирования сериализованного ответа для передачи на сервер
     */
    public String getResults() {
        LangType lt = (LangType) m_chPrgType.getSelectedItem();
        String sType = lt.val;
        String sCode = m_taCode.getText();

        sCode = dlc.util.HtmlParamEscaper.escapeParam(sCode);

        String NL = System.getProperty("line.separator");

        StringBuffer res = new StringBuffer();
        res.append("<?xml version=\"1.0\" encoding=\"" + java.nio.charset.Charset.defaultCharset() + "\"?>" + NL);
        res.append("<Program langType=\"" + sType + "\"><!--");
        res.append(sCode);
        res.append("--></Program>" + NL);
        String escapedRes = dlc.util.HtmlParamEscaper.escapeParam(
                dlc.util.HtmlParamEscaper.escapeParam(res.toString()));
//        System.out.println( escapedRes );
        return escapedRes;
    }
}
