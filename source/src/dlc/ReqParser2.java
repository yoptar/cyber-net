package dlc;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * �����, ����������� ������ RLCP-�������.
 */
public class ReqParser2 {

    /**
     * ����� ��� ������� �������
     * @param reqXML XML-������ � ������� RLCP
     * @return ��������� ������ Program
     */
    public Program parse( String reqXML ){
        Program res = null;

        try{

            SAXReader r = new SAXReader();
/*
Logger.log( "========reqXML.start========");
Logger.log( reqXML );
Logger.log( "========reqXML.end========");
*/
            org.dom4j.Document doc = r.read( new StringReader(reqXML) );
            res = new Program();

            List nodes = doc.selectNodes("//ConditionForChecking");
            for( Iterator it = nodes.iterator(); it.hasNext(); ){
                Node n = (Node)it.next();

                ConditionForChecking cfc = new ConditionForChecking();
                cfc.setID( Integer.parseInt( n.selectSingleNode("@id").getText() ) );
                cfc.setTime( Long.parseLong( n.selectSingleNode("@Time").getText() ) );
                String input = n.valueOf( "Input/comment()" );
                String output = n.valueOf( "Output/comment()" );
                if( input == null ) input = "";
                else input = dlc.util.HtmlParamEscaper.unescapeParam(input.trim()).trim();
                if( output == null ) output = "";
                else output = dlc.util.HtmlParamEscaper.unescapeParam(output.trim()).trim();

                cfc.setInput( input.trim() );
                cfc.setOutput( output.trim() );
                res.addCondition( cfc );
            }

            if( nodes.size() == 0 ){
                throw new Exception( "ReqParser2.parse() FAILED - no ConditionForChecking received");
            }

            Hashtable deserialized = new Hashtable();
            String    instructions = "";
            try{
                instructions = doc.selectSingleNode("//Instructions/comment()").getText();
            }catch( Exception instrExc ){}
            /**
             * ��������� ������� ����� unescape, �.�. � ������ � ���������� ������,
             * ���� �� unescape �������� ��� ������������
             */

            res.setCode( instructions );
        }catch( Exception exc ){
            exc.printStackTrace();
            Logger.log("ERROR: ReqParser2.parse() FAILED: " + exc.getMessage() );
            return null;
        }
        return res;
    }
}
