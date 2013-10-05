package prglabserver;

import java.util.*;

/**
 * Класс реализует разбор параметров командной строки
 */
public class CmdLineParser {

	/**
	 * Метод для разбора параметров командной строки
	 */
    public static String []parseArguments( String args ){
        
        StringTokenizer st = new StringTokenizer( args, "\" ", true );
        boolean bInQuote = false;
        StringBuffer cplxToken = new StringBuffer();
        ArrayList<String> result = new ArrayList<String>();
        while( st.hasMoreTokens() ){
            String token = st.nextToken();
            if( token.equals("\"") ){
                cplxToken.append( "\"" );
                bInQuote = !bInQuote;
            }else if( bInQuote ){
                cplxToken.append( token );
            }else if( token.equals(" ") ){
                if( cplxToken.length() > 0 ){
                    result.add( cplxToken.toString() );
                    cplxToken = new StringBuffer();
                }
            }else{
                cplxToken.append( token );
            }
        }
        if( cplxToken.length() > 0 )
            result.add( cplxToken.toString() );
/*
        Logger.log( "CmdLine: " );
        for( int i = 0; i < result.size(); i++ ){
            Logger.log( " - " + result.get(i) );
        }
*/
        
        return (String[])result.toArray( new String[0] );
    }
 }
