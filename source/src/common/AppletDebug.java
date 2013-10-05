package common;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * ����� ��� �������� XML-��������� ������������ ������� � ��������� ������
 */
public class AppletDebug {

    String chooseServerIP(){
            final Dialog frm = new Dialog( new Frame() );
            frm.setModal( true );
            frm.setTitle( "������� IP �������" );
            //frm.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
            frm.addWindowListener( new WindowAdapter(){
                public void windowClosing( WindowEvent e ){
                    frm.dispose();
                }
            });
            frm.setSize( 400, 150 );

            TextArea jTA = new TextArea();
            frm.add( jTA );

            frm.setVisible( true );

            return jTA.getText();
    }

    void statusCheckPost( String text ){
            final Dialog frm = new Dialog( new Frame() );
            frm.setTitle( "����� �� �������" );
            frm.addWindowListener( new WindowAdapter(){
                public void windowClosing( WindowEvent e ){
                    frm.dispose();
                }
            });
            frm.setSize( 400, 300 );

            TextArea jTA = new TextArea( text );
            frm.add( jTA );

            frm.setVisible( true );
    }

    /**
     * ����� ��� �������� ��������� ������������ ������� � ��������� ������
     * @param input ������� ����� ������
     * @param output �������� ����� ������
     * @param results ����� ��������� (����� ��������)
     */
    public String doCheckPost( String input, String output, String results, String host ){

System.out.println("Host: [" + host + "]" );//, changing to de.ifmo.ru" );

          int    srv_port = 2005;
          //String Ip_Port = "localhost";
          //String Ip_Port = "cde.ifmo.ru";
          String Ip_Port = host;

//		Ip_Port = "de.ifmo.ru";

          try{
              String NL = "\r\n";

/*
����������� ������ ��� ���1: (������������� ��������� ���������)
noice: ���
k: �������� �������� ����������
bcount: ���. ���-�� �����
blimitbottom, blimittop: ����������� ������ ��� ���������� � ������
noice=5;k=0.84;bcount=10;blimitbottom=-3.0;blimittop=3.0

<input><!--noice=5;k=0.84--></input>
<output><!--dummy--></output>

------------------------------------------------------
����������� ������ ��� ���2: (������������� ��������� �������������� ���������)
cohfuncnoice: ������ ��� ��������� ������� ������������� (����� ����������� �������� ���������� �� ������� �������)
intervalnoice: ������ ��� ��������� ��������� ����������
cohcount: ���. ���-�� ����� ��� ��������� ������ �-��� �������������

<input><!--cohfuncnoice=0.09;intervalnoice=0.09;cohcount=2;--></input>
<output><!--dummy--></output>
------------------------------------------------------
����������� ������ ��� ���3: (�������� �����-�������)
noice: ��� (���������� ������)

<input><!--noice=0.05--></input>
<output><!--dummy--></output>
*/
              
              input = dlc.util.HtmlParamEscaper.escapeParam( input );
              output = dlc.util.HtmlParamEscaper.escapeParam( output );

              String req = "" +
              "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
              "<!DOCTYPE Request SYSTEM \"http://de.ifmo.ru/--DTD/Request.dtd\">" +
              "<Request>" +
                  "<Conditions>" +
                      "<ConditionForChecking id=\"1\" Time=\"-1\">" +
                          "<Input>" +
                            "<!--" + input + "-->" +
                          "</Input>" +
                          "<Output>" +
                            "<!--" + output + "-->" +
                          "</Output>" +
                      "</ConditionForChecking>" +
/*
                      "<ConditionForChecking id=\"2\" Time=\"-1\">" +
                          "<Input>" +
                            "<!--" + input + "-->" +
                          "</Input>" +
                          "<Output>" +
                            "<!--" + output + "-->" +
                          "</Output>" +
                      "</ConditionForChecking>" +
*/
                  "</Conditions>"+
                  "<Instructions>"+
              "<!--";

              req += results;

              req += "-->"+
                  "</Instructions>"+
              "</Request>";
/*
              String defIp_Port = Ip_Port;
              Ip_Port = chooseServerIP();
              try{
                   srv_port = Integer.parseInt( Ip_Port.substring( Ip_Port.indexOf(":")+1 ) );
                   Ip_Port = Ip_Port.substring( 0, Ip_Port.indexOf(":") );
              }catch( Exception ipExc ){
                  Ip_Port = defIp_Port;
              }
*/              

System.out.println( "REQ: =========" );
System.out.println( req );

              //String toSend = "redirect" + NL +
              String toSend = "check" + NL +
                      "url:rlcp://ove:ove@" + Ip_Port + ":" + srv_port + NL +
                      "content-length:" + req.length() + NL + NL +
                      req + NL;

              Socket s = new Socket( Ip_Port, srv_port );
//              s.setSoTimeout( 5000 );
              OutputStream os = s.getOutputStream();
              InputStream  is = s.getInputStream();

              os.write( toSend.getBytes() );
              
              int ch = 0;
			  byte []buffer = new byte[ 255 ];
		      int byteCnt = 0;

              while( (ch=is.read()) != -1  ){
				 buffer[ byteCnt++ ] = (byte)ch;
				 if( byteCnt >= buffer.length ){
					byte []newBuffer = new byte[ buffer.length<<1 ];
					System.arraycopy( buffer, 0, newBuffer, 0, buffer.length );
					buffer = newBuffer;
				 }
			  }

              s.close();

//              System.out.println( "result from server:\n" + sb.toString() );

//              statusCheckPost( "����� ��: " + Ip_Port + ":" + srv_port + "\n" + sb.toString() );
              
              return new String( buffer, 0, byteCnt );

          }catch( Exception e ){
              e.printStackTrace();
//              statusCheckPost( "������ ���������� � ��������: " + Ip_Port + ":" + srv_port );
          }
          return null;
      }
}
