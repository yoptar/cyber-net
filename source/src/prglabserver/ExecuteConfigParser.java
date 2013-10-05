package prglabserver;

import org.dom4j.*;
import org.dom4j.io.*;
import java.util.*;
import java.io.*;

import dlc.Logger;

/**
 * Класс реализует парсер конфигурации в формате XML
 */
public class ExecuteConfigParser {
    
    private static String substVars( String value, HashMap vars ) throws Exception{
        
        String prefix = "%";
        String suffix = "%";
        
        String res = value.toUpperCase();
        
        for( Object o : vars.keySet() ){
            String key = prefix + o + suffix;
            res = res.replaceAll( key, ((String)vars.get(o)).replace("\\", "\\\\") );
        }
        
        return res;
    }
    
    private static HashMap parseEnvironment( String env ) throws Exception{
        HashMap res = new HashMap();
        StringTokenizer st = new StringTokenizer( env, "\r\n=", true );
        final int FIND_EQ = 0, FIND_NL = 1;
        int stage = FIND_EQ;
        String sVar = "";
        String sValue = "";
        while( st.hasMoreTokens() ){
            String token = st.nextToken();
            if( stage == FIND_EQ ){
                if( token.equals("=") )
                    stage = FIND_NL;
                else
                    sVar = token;
            }else if( stage == FIND_NL ){
                if( token.equals("\r") || token.equals("\n") ){
                    stage = FIND_EQ;
                    res.put( sVar.toUpperCase(), substVars(sValue, res) );
                    sVar = ""; sValue = "";
                }else{
                    sValue = token;
                }
            }
        }
        return res;
    }
    
	/**
	 * Метод для чтения конфигурации
	 * @param doc экземпляр org.dom4j.Document
	 * @return набор конфигураций. Ключ - краткое название языка, значение - экземпляр ExecuteConfig.
	 */
    public static HashMap readConfig( Document doc ) throws Exception{
        HashMap res = new HashMap();
        
        List configs = doc.selectNodes( "//ExecuteConfig" );
        for( Iterator it = configs.iterator(); it.hasNext(); ){
            Node n = (Node)it.next();
            String sType = n.selectSingleNode("@type").getText();
            
            String env = n.selectSingleNode("CommandsEnv/comment()").getText();
            HashMap envMap = parseEnvironment( env );
/*            
            //dump read env
            Logger.log( "Environment:" );
            for( Object key : envMap.keySet() ){
                Logger.log( key + "=" + envMap.get(key) );
            }
            // <<--
*/          
            List commands = n.selectNodes( "Command" );
            ArrayList<CommandArguments> aCmds = new ArrayList<CommandArguments>();
            for( Iterator cmdIt = commands.iterator(); cmdIt.hasNext(); ){
                Node cmdNode = (Node)cmdIt.next();
                
                String []args = CmdLineParser.parseArguments( cmdNode.selectSingleNode("Args/comment()").getText().trim() );
                
                String []opts = new String[0];
                if( args.length > 1 )
                    opts = (String[])Arrays.asList(args).subList(1, args.length).toArray( new String[0] );
                
                boolean bPrefixWithDir = false;
                if( cmdNode.selectSingleNode("Args/@prefixWithDir") != null )
                    bPrefixWithDir = cmdNode.selectSingleNode("Args/@prefixWithDir").getText().equalsIgnoreCase("true");

				String timeOut = cmdNode.valueOf( "@timeOut" );

				if( !cmdNode.valueOf("@name").equalsIgnoreCase("run") ){
    				if( timeOut == null ){
    					Logger.log( "ExecuteConfigParser.readConfig - no @timeOut for ExecuteConfig." + sType + " and target=" + cmdNode.valueOf("@name") + "\n" +
    						"\tassume default: " + ExecuteConfig.m_defTimeOut );

    					timeOut = "" + ExecuteConfig.m_defTimeOut;
    				}
    				try{
    					int iTimeOut = Integer.parseInt( timeOut );
    					if( iTimeOut <= 0 )
    						throw new Exception( "ExecuteConfigParser.readConfig - invalid timeOut: [" + timeOut + "]" );
    				}catch( Exception timeOutParseExc ){
    					Logger.log( "ExecuteConfigParser.readConfig - invalid @timeOut for ExecuteConfig." + sType + " and target=" + cmdNode.valueOf("@name") + "\n" +
    						"\tassume default: " + ExecuteConfig.m_defTimeOut );
    					timeOut = "" + ExecuteConfig.m_defTimeOut;
    				}
				}else
					timeOut = "" + ExecuteConfig.m_defTimeOut;

                CommandArguments cmdArgs = new CommandArguments(
                    cmdNode.selectSingleNode("@name").getText(),
                    args[0],
                    opts,
                    bPrefixWithDir,
					Integer.parseInt( timeOut )*1000
                );
                aCmds.add( cmdArgs );
            }

            ExecuteConfig execConfig = new ExecuteConfig( aCmds, sType, envMap,
                    n.selectSingleNode("CompileDir/comment()").getText(),
                    n.selectSingleNode("ProgramFile/comment()").getText() );

            res.put(sType.toLowerCase(), execConfig);
        }
/*
        //dump each execute config
        for( Object o : res.keySet() ){
            Logger.log( ((ExecuteConfig)res.get(o)).toString() );
        }
        // <<--
*/        
        return res;
    }
/*    
    public static void main( String []args ){
        try{
            Document doc = new SAXReader().read( new InputStreamReader( new FileInputStream("resources/Config.xml") ) );
            HashMap config = readConfig( doc );
        }catch( Exception exc ){
            exc.printStackTrace();
        }
    }
*/
}
