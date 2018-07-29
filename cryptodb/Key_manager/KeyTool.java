package cryptodb.Key_manager;
import java.sql.Timestamp;

import java.sql.*;
import java.util.Calendar;

import cryptodb.DbManager.DbManager;
import cryptodb.Exceptions.InvalidKeyStateException;
import cryptodb.Exceptions.LiveKeyNotFoundException;
import cryptodb.Key_Safe.LocalKey;
import cryptodb.Key_Safe.LocalKeyStore;
import cryptodb.SQL.Documentation.KeyAlias;

import java.text.SimpleDateFormat;
import java.text.ParseException;
public class KeyTool {
 

	//KeyTool take user's credentials and (if it is necessary) cooperates with KeyAlias,LocalKeyStore and LocalKey
	//it's possible KeyTool will check and modify our data before  they will be forwarded
	

public static void Main(String[] args)
{
	String usage="KeyTool {load,update,print,printLive," + "retire,terminate,genKek} ";
	if(args.length==0)
	{
		System.out.println(usage);
		System.exit(2);
	}
	KeyTool tool=new KeyTool();

if(args[0].equals("load"))
{
	
	if(args.length==5)
	{
		String alias=args[1];
		String engine=args[2];
		String family=args[3];
		String days_pending=args[4];
		tool.loadKey(alias,engine,family,days_pending);
		
	}
	else
	{
		System.out.println("KeyTool load <alias><engine>" + "<family><days_pending> ");
		
	}
	
}
else if(args[0].equals("print"))
{
	if(args.length==2)
	{
		String keyId=args[1];
		tool.printKey(keyId);
		
	}
	else
	{
		System.out.println("KeyTool print keyId");
	}
}
else if(args[0].equals("printLive"))
{
	if(args.length==2)
	{
		String family=args[1];
		tool.printLiveKey(family);
	}
	else
	{
		System.out.println("KeyTool print family");
	}
}

else if(args[0].equals("retire"))
{
	if(args.length==2)
	{
		String retire=args[1];
		tool.retireKey(retire);
	}
	else
	{
		System.out.println("KeyTool retire aliasId");
	}
	
}
else if(args[0].equals("terminate"))
{
	if(args.length==2)
	{
		String terminate=args[1];
		tool.terminateKey(terminate);
		
	}
	else
	{
		System.out.println("KeyTool terminate<alias_id>");
	}
	
}
else if(args[0].equals("update"))
{
	if(args.length>=2)
	{
		if(args[1].equals("date"))
		{
			if(args.length==4)
			{
				
				tool.updateActivationDate(args[2],args[3]);
				
			}
			else
			{
				System.out.println("KeyTool update date <alias_id> <date>");
			}
		}
		else if(args[1].equals("family"))
		{
			if(args.length==4)
			{
				tool.updateKeyFamily(args[2],args[3]);
			}
			else
			{
				System.out.println("KeyTool upadate family<alias_id> <family>");
			}
		}
		else if(args[1].equals("alias"))
		{
			if(args.length==4)
			{
				tool.updateAlias(args[2],args[3]);
			}
			else
			{
				System.out.println("KeyTool update alias <alias_id> <alias>");
			}
			
			
		}
		else if(args[1].equals("engine"))
		{
			if(args.length==4)
			{
				tool.updateEngine(args[2],args[3]);
			}
			else
			{
				System.out.println("KeyTool update engine <alias_id> <engine>");
			}
		}
		else if(args[1].equals("key_id"))
		{
			if(args.length==4)
			{
				tool.updateKeyId(args[2],args[3]);
				
			}
			else
			{
				System.out.println("KeyTool update key_id<alias_id><key_id");
			}
		}
		
			
	}
	else
	{
		System.out.println("KeyTool update{date,family,alias}"+"engine,key_id}");

	}
}
else if(args[0].equals("genKek"))
{
	if(args.length==1)
	{
		tool.generateKek();
	}
	else
	{
		System.out.println("KeyTool genKek");
	}
}
else
{
	System.out.println(usage);
}

}
//this method generates new key encrypting key  
void generateKek()
{
	LocalKeyStore keyStore=new LocalKeyStore();
	String kekId=keyStore.generateNewKek();
	System.out.println("KEK ID:"+kekId);
	keyStore.replaceKek(kekId);
}


//it creates new key  that will be put in cooperation with engine 
private void loadKey(String alias,String engine,String family,String days_pending)
{
	String vaultKeyId=null;
	if(engine.equals("local"))
	{
		LocalKey localKey=makeNewRawKey();
		vaultKeyId=localKey.getKeyID();
	}
	else
	{
		System.out.println("Unknown engine");
	}
	if(vaultKeyId!=null)
	{
		try
		{
			KeyAlias.getNewAlias(alias, engine, family, days_pending, vaultKeyId);
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}

private LocalKey makeNewRawKey()
{
	LocalKey localKey=null;
	LocalKeyStore keyStore=new LocalKeyStore();
	localKey=keyStore.generateKey();
	return	 localKey;
}

private void printKey(String AliasId)
{
	KeyAlias alias=new KeyAlias(AliasId);
	printKey(alias);
}

private void printKey(KeyAlias alias)
{
	alias.populate();
	System.out.println("Alias ID:"+alias.getAliasId());
	System.out.println("Alias:"+alias.getKeyAlias());
	System.out.println("Family:"+alias.getKeyFamily());
	System.out.println("Engine:"+alias.getEngine());
	System.out.println("Key Id:"+alias.getKeyId());
	System.out.println("Activation date:"+alias.getActivationDate());
	System.out.println("Status:"+alias.getStatus());
	try
	{
		System.out.println("State:"+alias.getKeyState());
		
	}
	
	catch( Exception e)
	{
		System.out.println("State:UNKOWN");
	}
 
}


//it lets us know which key from given key family is alive
private void printLiveKey(String keyFamily)
{
	try
	{
		KeyAlias liveKeyAlias=KeyAlias.getLiveKeyAlias(keyFamily);
		printKey(liveKeyAlias);
		
	}
	catch( Exception e)
	{
		System.out.println("Live Key Not Found");
	}
}

final private void retireKey(String keyId)
{
	try
	{
		KeyAlias alias=new KeyAlias(keyId);
		alias.retireKey();
		alias.save();
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
}


//this method is responsible for removing the key 
//so it's very sensitive operation  because once key is removed all encrypted data will be lost 
final private void terminateKey(String aliasId)
{
	PreparedStatement pstmt=null;
	try
	{
		KeyAlias alias=new KeyAlias(aliasId);
		String state=alias.getKeyState();
		
		if(state.equals(KeyAlias.RETIRED)|| state.equals(KeyAlias.PENDING))
		{
			alias.terminateKey();
			alias.save();
	String query="Delete From local_key_store"+ "Where key_id=?";
	Connection con=DbManager.getDbConnection();
	pstmt=con.prepareStatement(query);
	pstmt.setString(1, alias.getKeyId());
	int rows=pstmt.executeUpdate();	
		}
		else
		{
			System.out.println("Key is not retired");
		}
		
	}
	catch(SQLException e)
	{
		System.out.println(e);
	}
	catch(InvalidKeyStateException e)
	{
		System.out.println(e);
	}
	finally
	{
		if(pstmt!=null)
		{
			try
			{
				pstmt.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			pstmt=null;
		}
	}
	
}

private void updateActivationDate(String aliasId,String dateString)
{
	boolean success=false;
	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	df.setTimeZone(java.util.TimeZone.getTimeZone("America/LosAngeles"));
	try
	{
		java.util.Date date=new java.util.Date();
		java.sql.Timestamp dbTime=null;
		if(dateString.equals("asap"))
		{
			date=new java.util.Date(date.getTime()+5000);
			dateString=df.format(date);
			
		}
		else
		{
			date=df.parse(dateString);
		}
		KeyAlias alias=new KeyAlias(aliasId);
		alias.populate();
		String state=alias.getKeyState();
		Timestamp now=new Timestamp((Calendar.getInstance()).getTimeInMillis());
		
		if(state.equals(KeyAlias.PENDING)&& date.after(now))
		{
			alias.SetActivationDate(Timestamp.valueOf(dateString));
			alias.save();
		success=true;
		}
		
		
			
	}
	catch (Exception e)
	{
		System.out.println("Key:"+e);
	}
	if(!success)
	{
		System.out.println("Update failed.Check key state"+"and make sure that activation date is a future date");
		
	}
}


private void updateKeyFamily(String aliasId,String keyFamily)
{
	try
	{
		KeyAlias alias=new KeyAlias(aliasId);
		alias.populate();
		String state=alias.getKeyState();
		if(state.equals(KeyAlias.PENDING))
		{
			alias.setKeyFamily(keyFamily);
			alias.save();
		}
		else
		{
			System.out.println("Only pending keys may be updated");
		}
		
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
}

private void updateAlias(String aliasID,String aliasLabel)
{
	try
	{
		KeyAlias alias=new KeyAlias(aliasID);
		alias.populate();
		String state=alias.getKeyState();
		if(state.equals(KeyAlias.PENDING))
		{
			alias.setKeyAlias(aliasLabel);
			alias.save();
	}
		else
		{
			System.out.println("Only pending keys may be updated");
		}
			
	}
	catch(Exception e)
	{
		System.out.println("Key is unknown"+e);
	}
}

private void updateEngine(String aliasId,String engine)
{
	try
	{
		KeyAlias alias=new KeyAlias(aliasId);
		alias.populate();
		String state=alias.getKeyState();
		if(state.equals(KeyAlias.PENDING))
		{
			
			alias.SetEngine(engine);
			alias.save();
		}
		else
		{
			System.out.println("Only pending keys may be updated");
		}
		
	}
	catch(Exception e)
	{
		System.out.println("Key is unknown:"+e);
	}
}

private void updateKeyId(String aliasId,String keyId)
{
	try
	{
		KeyAlias alias=new KeyAlias(aliasId);
		alias.populate();
		String state=alias.getKeyState();
		if(state.equals(KeyAlias.PENDING))
		{
			alias.SetKeyId(keyId);
			alias.save();
		}
		else
		{
			System.out.println("Only pending keys may be udpated");
		}
		
	}
	catch(Exception e)
	{
		System.out.println("Unknown state:"+e);
	}
}



}

