package prglabclient.codeedit;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/**
 * Класс реализует панель (JPanel) для отображения JEditorPane с номерами строк
 * (при добавлении в контейнер следует использовать getEditPane())
 */
public class EditLineNumbersPane extends JPanel implements CaretListener{

    JEditorPane pane;
    JScrollPane scrollPane;
	JPanel      rightPane;

	/**
	 * Конструктор
	 * @param vKeywords список ключевых слов (для подсветки синтаксиса)
	 * @param bCaseSens признак необходимости учета регистра символов при подсветке
	 */
    public EditLineNumbersPane( java.util.Vector vKeywords, boolean bCaseSens ) {
        super();

		setLayout( new BorderLayout() );

        pane = new JEditorPane() // we need to override paint so that the linenumbers stay in sync
        {
            public void paint(Graphics g) {
                super.paint(g);
                //EditLineNumbersPane.this.repaint();
				rightPane.repaint();
            }
        };

        //Jerome code...  переопределяем поведение по нажатию Enter
        pane.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                //System.out.println("Code = " + e.getKeyCode());

                if (e.getKeyCode() == 10)
                {
                    e.consume(); //отмена стандартного поведения по этому событию
                    SyntaxDocument doc = (SyntaxDocument) pane.getDocument();

                    int carDot = pane.getCaret().getDot();
                    int carMark = pane.getCaret().getMark();

                    if (carDot != carMark) // удаление выделенной области
                    {
                        //System.out.println("caretD = " + carDot);
                        //System.out.println("caretM = " + carMark);

                        try {
                            doc.remove(Math.min(carDot, carMark), Math.max(carDot, carMark) - Math.min(carDot, carMark));
                        } catch (BadLocationException ex) {
                            Logger.getLogger(EditLineNumbersPane.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                    // вставка переноса строки и автоотступа
                    int carPos = pane.getCaretPosition();
                    //int carPos = pane.getCaretPosition();
                    //pane.getCaret().

                    int currentLine = doc.getDefaultRootElement().getElementIndex( carPos );
                    Element elm = doc.getDefaultRootElement().getElement( currentLine );
                    //Element elmNext = doc.getDefaultRootElement().getElement(currentLine + 1);
                    
                    //System.out.println("caret = " + carPos);
                    //System.out.println("currentLine = " + currentLine);
                    //System.out.println("Element = " + elm);

                    int start = elm.getStartOffset();
                    int indentCounter = start;

                    StringBuffer indentToSet = new StringBuffer();
                    indentToSet.append("\n");
                    


                    try {

                        while (indentCounter < doc.getLength() && doc.getText(indentCounter, 1).charAt(0) == ' ' && indentCounter < carPos)
                        {
                            //System.out.println("indent works  " + indentCounter);
                            indentCounter++;
                            indentToSet.append(" ");
                        }
                            //indentToSet.insert(0,"\n");
                        doc.insertString(carPos, indentToSet.toString(), null);
                            //doc.remove(carPos + indentToSet.length(), 2);
                            //pane.setCaretPosition(carPos);

                    } catch (BadLocationException ex) {
                        Logger.getLogger(EditLineNumbersPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        //end Jerome code..

        scrollPane = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		final JEditorPane edPane = pane;
		final JScrollPane scrPane = scrollPane;

		rightPane = new JPanel(){
			public void paint( Graphics g ){
                super.paint(g);

//				if( edPane == null || scrPane == null ) return;

                int start =
                        edPane.viewToModel(scrPane.getViewport().getViewPosition()); //starting pos in document
                int end =
                        edPane.viewToModel( new Point(
                            scrPane.getViewport().getViewPosition().x + edPane.getWidth(),
                            scrPane.getViewport().getViewPosition().y + edPane.getHeight()) );

                Document doc = edPane.getDocument();
                int startline = doc.getDefaultRootElement().getElementIndex(start);
                int endline = doc.getDefaultRootElement().getElementIndex(end);

                int fontHeight = g.getFontMetrics(edPane.getFont()).getHeight(); // font height

                for (int line = startline,  y = 0; line <= endline+1; line++, y += fontHeight){
                    g.drawString(Integer.toString(line), 0, y);
                }
			}
		};

        rightPane.setMinimumSize(new Dimension(30, 30));
        rightPane.setPreferredSize(new Dimension(30, 30));
        rightPane.setMinimumSize(new Dimension(30, 30));

        final java.util.Vector vKwords = new java.util.Vector( vKeywords );
        final boolean bCaseSensitive = bCaseSens;
        pane.setEditorKitForContentType("text/lang", new StyledEditorKit(){
            public Document createDefaultDocument(){
                SyntaxDocument res = new SyntaxDocument();
                res.setKeywords( vKwords, bCaseSensitive );
                return res;
            }
        });
        pane.setContentType( "text/lang" );

        pane.putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE );
        pane.setFont( new Font("Console", Font.PLAIN, 14 ) );
        setFont( pane.getFont() );
        
        pane.addCaretListener( this );

		add( rightPane, BorderLayout.WEST );
		add( scrollPane );//, BorderLayout.CENTER );
    }
    
	/**
	 * Метод для получения панели, содержащей JEditorPane с номерами строк
	 */
/*
    public JPanel getEditPane(){
        JPanel pane = new JPanel( new BorderLayout() );
        pane.add( this, BorderLayout.WEST );
        pane.add( scrollPane );
        return pane;
    }
*/

	/**
	 * Метод для получения экземпляра Document, связанного с EditorPane
	 */
    public Document getDocument(){
        return pane.getDocument();
    }
	/**
	 * Метод для получения JEditorPane
	 */
    public JEditorPane getEditorPane(){
        return pane;
    }
	/**
	 * Метод для получения текста из JEditorPane (с потерей разметки)
	 */
    public String getText(){
        return pane.getText();
    }
/*
    public void paint(Graphics g) {
        super.paint(g);

// We need to properly convert the points to match the viewport
// Read docs for viewport
        int start =
                pane.viewToModel(scrollPane.getViewport().getViewPosition()); //starting pos in document
        int end =
                pane.viewToModel( new Point(
                    scrollPane.getViewport().getViewPosition().x + pane.getWidth(),
                    scrollPane.getViewport().getViewPosition().y + pane.getHeight()) );
// end pos in doc

// translate offsets to lines
        Document doc = pane.getDocument();
        int startline = doc.getDefaultRootElement().getElementIndex(start) ;
        int endline = doc.getDefaultRootElement().getElementIndex(end);

        int fontHeight = g.getFontMetrics(pane.getFont()).getHeight(); // font height

        for (int line = startline,  y = 0; line <= endline+1; line++, y += fontHeight) {
            g.drawString(Integer.toString(line), 0, y);
        }
    }
*/    

	/**
	 * Обработчик события смещения каретки (для подсветки синтаксиса)
	 */
    public void caretUpdate( CaretEvent e ){
        ((SyntaxDocument)pane.getDocument()).highlightPair( pane.getCaretPosition() );

    }

// test main
/*    
    UndoManager m_undoManager;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout( new BorderLayout());
        final EditLineNumbersPane nr = new EditLineNumbersPane( new java.util.Vector( java.util.Arrays.asList( new String[]{ "for", "do", "while" } ) ), true );
        frame.getContentPane().add(nr, BorderLayout.WEST);
        frame.getContentPane().add(nr.scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(new Dimension(400, 400));
        frame.setVisible( true );
    }
*/
}
