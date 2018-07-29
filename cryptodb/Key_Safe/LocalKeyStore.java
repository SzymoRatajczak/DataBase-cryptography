package cryptodb.Key_Safe;
import cryptodb.*;
import cryptodb.DataConversion.Utils;
import cryptodb.DbManager.DbManager;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;



final public class LocalKeyStore {
//it handles interaction with key database and create LocalKey objects 

//this method generates key encrypting  keys
//and return ID of newly created key
//because it is a public method it may be called  out outside the kernel
	
final public String generateNewKek()
{
	byte[]rawKey=new byte[16];
	String kekID=null;
	PreparedStatement pstmt=null;
	
	try
	{
		
		//to generate a key we are using pseudo random number generator
		SecureRandom kekGenerator=SecureRandom.getInstance("SHA1PRNG");
		kekGenerator.nextBytes(rawKey);

	//Newly generated key is put into a key database and  ID of this key will be allotted to kekID value
		//key activation date will be set on current time 
		//Each time when this key is generated will be immediately available
		//and former keys will be available only for decrypion purposes
		
		String sqlstmt="INSERT INTO key_encrypting_keys"+"values(NULL,?,now())";
		
		Connection con =DbManager.getDbConnection();
		pstmt=con.prepareStatement(sqlstmt,Statement.RETURN_GENERATED_KEYS);
		pstmt.setBytes(1, rawKey);
int rows=pstmt.executeUpdate();

ResultSet rs=pstmt.getGeneratedKeys();
if(rs.next())
{
	kekID=rs.getString(1);
	
}
	
	}
	
	catch(NoSuchAlgorithmException nsae)
	{
		
	System.out.println(nsae);	
	}
	
	catch(SQLException e)
	{
		System.out.println(e);
	}
 
	
	//because we have public method row key may be zeroed out 
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
				//ignore
			}
			pstmt=null;
			
		}
		if(rawKey!=null)
		{
			for(int i=0;i<rawKey.length;i++)
			{
				rawKey[i]=(byte)0x00;
			}
		}
	}
	return kekID;
	
}
	
//Now, newly created key will be saved in binary format into  database
//Normally key encrypting keys are not encrypted 
//because they are only use for obfuscation purposes
//as well as they are stored in  secure database
//If we want to get  higher security level  we can divide this key and one part
//will be put into database and second part will be put into file system




//this method creates regular key

final public LocalKey generateKey()
{
	LocalKey localKey=new 	LocalKey();
	byte[]rawKey=new byte[16];
	
	try {
	SecureRandom ivGenerator=SecureRandom.getInstance("SHA1PRNG");
	ivGenerator.nextBytes(rawKey);
localKey.setrawKey(rawKey);
	}
	
	
	catch(NoSuchAlgorithmException e)
	{
		System.out.println(e);
	}

	
	encryptKey(localKey);
	saveKey(localKey);
	localKey.wipe();
	return localKey;
}



//this method takes  regular key to encrypt our data
final private void encryptKey(LocalKey localKey)
{
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	byte[]encryptedKey=null;
	byte[]keyData=null;
	
	//to find a key in a key database
	try
	{
		String query="Select *"+"From key_encrypting_keys"+"Where activation_data<=now()"+"Order By activation_date desc";
		Connection con=DbManager.getDbConnection();
		pstmt=con.prepareStatement(query);
		rs=pstmt.executeQuery();
	
	

	if(rs.next())
	{
		keyData=rs.getBytes("key_data");
		localKey.setKekId(rs.getString("kek_id"));
		
		
	}
	else
	{
		System.out.println("KeyEncryptingKeyNoFoundException");
	}
	
	//for encryption 
	SecretKeySpec kekSpec=new SecretKeySpec(keyData,"AES");
	Cipher cipher=Cipher.getInstance("AES/ECB/NoPadding");
	cipher.init(Cipher.ENCRYPT_MODE, kekSpec);
	
	encryptedKey=cipher.doFinal(localKey.getRawKey());
	localKey.setKeyData(Utils.bytes2HexString(encryptedKey));
	}
	
	catch(SQLException ex)
	{
		System.out.println(ex);
	}
	
	catch(NoSuchAlgorithmException ex)
	{
		System.out.println(ex);
	}
	catch(NoSuchPaddingException ex)
	{
		System.out.println(ex);
	}
	catch(InvalidKeyException ike)
	{
		System.out.println(ike);
	}
	catch(IllegalBlockSizeException ex)
	{
		System.out.println(ex);
	}
	
	catch(BadPaddingException ex)
	{
		System.out.println(ex);
	}
	finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
			catch(SQLException ex)
			{
				System.out.println(ex);
			}
			rs=null;
		}
		if(pstmt!=null)
		{
			try
			{
				pstmt.close();
				
			}
			catch(SQLException ex)
			{
				System.out.println(ex);
			}
			pstmt=null;
		}
		localKey.wipe();
		
		//because it is possibility some of key data left in our memory 
		//to be sure  we removed them we must zeroed keyData variable out 
		if(keyData!=null)
		{
			for(int i=0;i<keyData.length;i++)
			{
				keyData[i]=(byte)0x00;
			}
		}
	}
	keyData=null;
	
}

//After encryption  key must be put into the database 
//Database is giving ID to each new key 

final private void saveKey(LocalKey localkey)
{
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try
	{
		
		String sqlStmt="Insert  into local_key_store"+ "values (NULL,?,?)";
		Connection con=DbManager.getDbConnection();
		pstmt=con.prepareStatement(sqlStmt,Statement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, localkey.getKeyData());
		pstmt.setString(2, localkey.getKekId());
		int rows=pstmt.executeUpdate();
		rs=pstmt.getGeneratedKeys();
		String keyId=null;
		if(rs.next())
		{
			keyId=rs.getString(1);
			
		}
		localkey.setKeyID(keyId);
		
		
		
	}
	
	catch(SQLException e)
	{
		System.out.println(e);
	}
	finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			rs=null;
		}
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

//to replace key encrypting keys we must decrypt all keys encrypted by this one 
//follow by encrypt all of them again using  a new one


final public void replaceKek(String newKekId)
{
	LocalKey key=null;
	String keyString=null;
	String keyId=null;
	byte[]kekBytes=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	ArrayList keys=new ArrayList();
	try
	{
		String query="Select *"+"From local_key_store lks,"+"key_encrypting_keys kek"+"Where lks.kek_id=kek.kek_id";
		Connection con=DbManager.getDbConnection();
		pstmt=con.prepareStatement(query);
		rs=pstmt.executeQuery();
		
		while(rs.next())
		{
			keyId=rs.getString("lks.key_id");
			keyString=rs.getString("lks.key_data");
			kekBytes=rs.getBytes("kek.key_data");
			key=new LocalKey(keyId,keyString,kekBytes);
			keys.add(key);//it contains list of all LocalKeys , together with key encrypting keys
			
			
		}
		// this method is going through list of keys,
		//decrypting all of them,  follow by encrypting them again
	Iterator iterator=keys.iterator();
	while (iterator.hasNext())
	{
		key=(LocalKey)iterator.next();
		key.setrawKey(key.getRawKey());
		encryptKey(key);
		updateKey(key);
		key.wipe();
		
	}
	keys=null;
	}
	catch(SQLException e)
	{
		System.out.println(e);
	}
	finally
	{
		
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
		catch(SQLException e)
			{
			System.out.println(e);
			}
			rs=null;
		
		}
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
		if(keys!=null)
		{
			Iterator interator=keys.iterator();
			while(interator.hasNext())
			{
				key=(LocalKey)interator.next();
				key.wipe();
			}
		}
		
	}
	
}
//this method takes key from database, update it(using  a new key encrypting keys) and then
//put a newly created/updated key into database
final private void updateKey(LocalKey localKey)
{
	PreparedStatement pstmt=null;
	try
	{
		String sqlstmt="UPDATE local_key_store"+"SET key_data=?," + "  kek_id=?"+ "WHERE key_id=?";
		Connection con=DbManager.getDbConnection();
		pstmt=con.prepareStatement(sqlstmt);
		pstmt.setString(1, localKey.getKeyData());;
		pstmt.setString(2, localKey.getKekId());
		pstmt.setString(3, localKey.getKeyID());
		int rows=pstmt.executeUpdate();
	}
	catch(SQLException e)
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
//this method reads key database and pumps this informations into LocalKey object 

public final LocalKey getLocalKey(String keyId)
{
 
	LocalKey key=null;
	String keyString=null;
	byte[]kekBytes=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	
	try
	{
		String query="Select *" +" From local_key_store lks," + "   key_encrypting_keys kek"+ "Where lks.key_id=?"+ "AND lks.kek_id=kek.kek_id";           
	
	Connection con=DbManager.getDbConnection();
	pstmt=con.prepareStatement(query);
	pstmt.setString(1, keyId);
	rs=pstmt.executeQuery();
	
	if(rs.next())
	{
		keyString=rs.getString("lks.key_data");
		kekBytes=rs.getBytes("kek.key_data");
	}
	 
	key=new LocalKey(keyId,keyString,kekBytes);
	
	
	
	}
	catch(SQLException e)
	{
		System.out.println("SQLException:"+ e.getMessage());
		System.out.println("SQLState:"+e.getSQLState());
		System.out.println("VendorError:"+e.getErrorCode());
	}
	
	finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			rs=null;
		}
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

		
return key;
}

}
//key safe implementation is made of two classes: LocalKey and LocalKeyStore
//LocalKey has actual crypto key and provides access method to data
//One of the most important method in that class is wipe method that zeros out our keys
//LocalKeyStore class is responsible for managing our key in database
//It provides method to create,save,encrypt key

