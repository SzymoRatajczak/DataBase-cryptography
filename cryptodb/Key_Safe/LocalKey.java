package cryptodb.Key_Safe;
import cryptodb.*;

import cryptodb.DataConversion.Utils;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.sql.*;


final public class LocalKey {
	//this class represents crypto key that is used to protect our data
	 
private String keyData=null; //encrypted key
private byte[] kek=null;//key encrypting keys
private String keyID=null;//encrypted ID
private String kekID=null;//ID of key encrypting keys
private byte[]rawKey=null;//unencrypted key

public LocalKey(String keyId,String keyData,byte[]kek)
{
	this.keyID=keyId;
	this.keyData=keyData;
	this.kek=kek;
	
}

public LocalKey()
{
	
}


//decrypt operations
final private byte[]decryptKey()
{
	SecretKeySpec key=null;
	try
	{
		//it initiates encryption process
		SecretKeySpec kekSpec=new SecretKeySpec(kek,"AES");
		
		//defines type of encryption
		Cipher cipher=Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, kekSpec);
		rawKey=cipher.doFinal(getKeyBytes());
		
	}
	catch(NoSuchAlgorithmException nsae)
	{
		System.out.println("Algorithm not found");
	}
	
	catch(NoSuchPaddingException nspe)
	{
		System.out.println(nspe);
	}
	
	catch(InvalidKeyException ike)
	{
		System.out.println(ike);
	}
	
	catch(IllegalBlockSizeException ibse)
	{
		System.out.println(ibse);
	}
	catch(BadPaddingException bpe)
	{
		System.out.println(bpe);
	}
	return rawKey;
}

	//it allows LocalKey returns own version of SecretKeySpec

public final SecretKeySpec getSecretKeySpec()
{
	byte[]rawKey=decryptKey();
	SecretKeySpec key=new SecretKeySpec(rawKey,"AES");
	return key;
}
	
//when LocalKey is no longer needed , key data and encrypting key must be zeroed out 

final public void wipe()
{
	wipeRawKey();
	wipeKek();
}


final public void wipeRawKey()
{
	if(rawKey!=null)
	{
		for(int i=0;i<rawKey.length;i++)
		{
			rawKey[i]=(byte)0x00;
		}
		rawKey=null;
	}
	
}


final public void wipeKek()
{
	if(kek!=null)
	{
		for (int i=0;i<kek.length;i++)
		{
			kek[i]=(byte)0x00;
		}
		kek=null;
	}
}

// methods handles unencrypted key must be private  because such key can exist
//only in kernel zone

final public void setKeyData(String key)
{
	this.keyData=key;
}

final public byte[]getKeyBytes()
{
	return Utils.hexStringToBytes(keyData);
}


final public String getKeyData()
{
	return keyData;
}
final public void setKekId(String kekId)
{
	this.kekID=kekId;
}

final public String getKekId()
{
	return kekID;
}

final public void setKeyID(String KeyID)
{
	this.keyID=KeyID;
}

final public String getKeyID()
{
	return keyID;
}

final  public void setrawKey(byte[]rawKey)
{
	this.rawKey=rawKey;
}

final public byte[]getRawKey()
{
	return rawKey;
}
//decrpytion process presumes LocalKey object is in existence
}
