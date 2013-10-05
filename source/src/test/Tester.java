package test;

import java.net.*;
import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

import dlc.Logger;

/**
 * ����� ����������� �������� ������ ��� �������� ����������� ������� �������.
 * ���� � ����� ������������ �����������, ��� �������� ��� ������� ����������.
 */
public class Tester{

	//url:rlcp://ove:ove@127.0.0.1:2003
	String m_url = "";
	String m_req = "";

	private String readStringFromIS( InputStream is ) throws Exception{
		int ch = 0;
		int byteCount = 0;

		int initBufSize = 64000;

		byte []buffer = new byte[ initBufSize  ];

		try{
			while( (ch = is.read()) >= 0 ){
				buffer[byteCount++] = (byte)ch;
				if( byteCount >= buffer.length ){
					byte []newBuffer = new byte[ buffer.length + initBufSize ];
					System.arraycopy( buffer, 0, newBuffer, 0, buffer.length );
					buffer = newBuffer;
				}
			}
		}catch( SocketException exc ){
			System.out.println( "WARNING: Connection reset" );
		}

		return new String( buffer, 0, byteCount );
	}

	/**
	 * �����������
	 * @param config ���� � ����� ������������
	 * @param outPath ���� ��� ������ ������ � ����������
	 */
	public Tester( File config, String outPath ) throws Exception{
		readConfig( config.getAbsolutePath() );

		try{

			String NL = "\r\n";

			m_req = "<?xml version=\"1.0\" encoding=\"" + java.nio.charset.Charset.defaultCharset() + "\"?>" + NL + m_req;

            String toSend = "check" + NL +
                    m_url + NL +
                    "content-length:" + m_req.length() + NL + NL +
					m_req + NL;

			int srv_port = 2003;
			StringTokenizer st = new StringTokenizer( m_url, ":/@", true );
			String sHost = "";
			String sPort = "";
			String sLogin = "";
			String sPasswd = "";
			int iState = 0;

			while( st.hasMoreTokens() ){

				String token = st.nextToken();

				if( token.equals("/") ){
					st.nextToken();
					sLogin = st.nextToken();
					st.nextToken();
					sPasswd = st.nextToken();
					st.nextToken();
					sHost = st.nextToken();
					st.nextToken();
					sPort = st.nextToken();
					break;
				}
			}

//Logger.log( "sHost: " + sHost );
//Logger.log( "sPort: " + sPort );
//Logger.log( "sLogin: " + sLogin );
//Logger.log( "sPasswd: " + sPasswd );

			srv_port = Integer.parseInt( sPort );

            Socket s = new Socket( sHost, srv_port );
            OutputStream os = s.getOutputStream();
            InputStream  is = s.getInputStream();

            os.write( toSend.getBytes("Windows-1251") );

            int ch = 0;
            StringBuffer sb = new StringBuffer();
/*
            while( (ch=is.read()) != -1  )
                sb.append( (char)ch );
*/
			sb.append( readStringFromIS( is ) );
            s.close();

//Logger.log("RESULT");
//Logger.log( sb );

			String xml = sb.toString();
			if( xml.indexOf("<") >= 0 ){
				xml = xml.substring( xml.indexOf("<") );

				saveOutputDocument( outPath + File.separator + config.getName(), xml, true );
			}else{
				saveOutputDocument( outPath + File.separator + config.getName(), xml, false );
			}

		}catch( Exception exc ){
			exc.printStackTrace();
			throw exc;
		}
	}

	/**
	 * ����� ��� ������ ������������ ��������� ������
	 */
	private void readConfig( String path ) throws Exception{
		Document doc = new SAXReader().read( path );
		m_url = doc.selectSingleNode( "//URL" ).getText();

		List<Node> nodes = doc.selectNodes( "//ConditionForChecking/Input/comment()" );
		for( Node n : nodes ){
			String val = n.getText().trim();
//			val = dlc.util.HtmlParamEscaper.escapeParam( val );
			n.setText( val );
		}

		nodes = doc.selectNodes( "//ConditionForChecking/Output/comment()" );
		for( Node n : nodes ){
			String val = n.getText().trim();
//			val = dlc.util.HtmlParamEscaper.escapeParam( val );
			n.setText( val );
		}

		m_req = doc.selectSingleNode( "//RLCPRequest/.//*" ).asXML();

		System.out.println( "REQ:" );
		System.out.println( m_req );
	}

	/**
	 * ���������� ���������� FilenameFilter ��� ���������� XML-������
	 */
	public static class XMLFilterAdapter implements FilenameFilter{
		public boolean accept( File dir, String name ){
			int p = name.indexOf( "." );
			if( p < 0 ) return false;
			if( (p+1) >= name.length() ) return false;
			String ext = name.substring( p+1 );
			return ext.equalsIgnoreCase( "xml" );
		}
	}

	private void saveOutputDocument( String path, String doc, boolean bReformat ) throws Exception{
		if( bReformat ){
			Document xdoc = new SAXReader().read( new StringReader(doc) );
			OutputFormat outFmt = OutputFormat.createPrettyPrint();
			outFmt.setEncoding( "Windows-1251" );
			XMLWriter wr = new XMLWriter( new FileOutputStream( path ), outFmt );
			wr.write( xdoc );
			wr.close();
		}else{
			FileOutputStream fos = new FileOutputStream( path );
			fos.write( doc.getBytes() );
			fos.close();
		}
	}

	/**
	 * ����� ��� ������� ������.
	 * �������� args[0] - ���� � �������� � ����-��������
	 * �������� args[1] - ���� � �������� � ��������� �������
	 */
	public static void main( String []args ){

            if( args.length < 2 ){
                Logger.log("USAGE: java test.Tester <testset-dir> <output-dir>" );
                return;
            }

            new File( args[1] ).mkdirs();

            final File []tsFiles = new File( args[0] ).listFiles( new XMLFilterAdapter() );
			final String outPath = args[1];
            
            for( int j = 0; j < tsFiles.length; j++ ){

				final int i = j;

				Thread t = new Thread(){
					public void run(){
		                try{

							System.out.println( "Processing " + tsFiles[i].getName() );

							long startTime = System.currentTimeMillis();
		                    new Tester( tsFiles[i], outPath );

							long endTime = System.currentTimeMillis();

							System.out.println( tsFiles[i].getName() + " - exec time: " + (endTime - startTime) );
		                }catch( Exception exc ){
		                    Logger.log( "test.Tester FAILED at " + tsFiles[i].getAbsolutePath() );
		                    Logger.log( "\t" + exc.getMessage() );
						}
					}
                };
				t.start();
            }

//            Logger.log( "Thats all. Look at " + args[1] + " to see servers output!" );
	}
}
