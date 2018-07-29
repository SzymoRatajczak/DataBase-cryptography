package cryptodb.Engine;
import java.security.*;
import javax.crypto.spec.*;

import cryptodb.Exceptions.KeyNotFoundException;
import cryptodb.Key_Safe.LocalKey;
import cryptodb.Key_Safe.LocalKeyStore;

import javax.crypto.*;

 //it works like a bridge between encryption and decryption calls
public class EngineWrapper {

	public EngineWrapper()
	{
		
	}
	
	//this method decrypts actual business data
	//1. Key is taken from key database and decrypted
	//2.Key encrypting key is removed from LocalKey (it will not be needed any more)
	//3.we are preparing IV and cipher
	//4.at the end key must be removed and byte array is returned
	
	public byte[]encrypt(byte[]plaintext,byte[]rawIv,String keyId)
	throws KeyNotFoundException{
		byte[]ciphertext=null;
		LocalKey localKey=null;
		
		try
		{
			LocalKeyStore keyStore=new LocalKeyStore();
			localKey=keyStore.getLocalKey(keyId);
			SecretKeySpec key=localKey.getSecretKeySpec();
			localKey.wipeKek();
			
			IvParameterSpec Iv=new IvParameterSpec(rawIv);
			Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key,Iv);
			ciphertext=cipher.doFinal(plaintext);
			
			//no possibility to clean up memory
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("Algorithm not found");
		}
		catch(NoSuchPaddingException e)
		{
			System.out.println(e);
		}
		catch(InvalidKeyException e)
		{
			System.out.println(e);
		}
		catch(IllegalBlockSizeException e)
		{
			System.out.println(e);
		}
		catch(BadPaddingException e)
		{
			System.out.println(e);
		}
		catch(InvalidAlgorithmParameterException e)
		{
			System.out.println(e);
		}
		finally
		{
			if(localKey!=null)
			{
				localKey.wipe();
			}
		}
		return ciphertext;
		
	}
	
	//the same algorithm for decryption
	public byte[]decrypt(byte[]ciphertext,byte[]rawIv,String keyId)
	{
		byte[]plaintext=null;
		LocalKey localKey=null;
		try
		{
			LocalKeyStore keyStore=new LocalKeyStore();
			localKey=keyStore.getLocalKey(keyId);
			SecretKeySpec key=localKey.getSecretKeySpec();
			localKey.wipe();
			
			IvParameterSpec Iv=new IvParameterSpec(rawIv);
			Cipher cipher=Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key,Iv);
			plaintext=cipher.doFinal(ciphertext);
			
		}
		catch(InvalidKeyException e)
		{
			System.out.println(e);
		}
		
		catch(NoSuchAlgorithmException e)
		{
			System.out.println("Algorithm not found"+e);
		}
		catch(NoSuchPaddingException e)
		{
			System.out.println(e);
		}
		catch(IllegalBlockSizeException e)
		{
			System.out.println(e);
		}
		catch(BadPaddingException e)
		{
			System.out.println(e);
		}
		catch(InvalidAlgorithmParameterException e)
		{
			System.out.println(e);
		}
		finally
		{
			if(localKey!=null)
			{
				localKey.wipe();
			}
		}
		return plaintext;
	}

	
}
