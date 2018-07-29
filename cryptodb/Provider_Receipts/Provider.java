package cryptodb.Provider_Receipts;
import java.util.*;


import cryptodb.DataConversion.Utils;
import cryptodb.Engine.EngineWrapper;
import cryptodb.Exceptions.InvalidKeyStateException;
import cryptodb.Exceptions.KeyNotFoundException;
import cryptodb.Exceptions.LiveKeyNotFoundException;
import cryptodb.Exceptions.MultipleAliasIdException;
import cryptodb.Documentation.KeyAlias;

import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


//it is a bridge between Consumer and EngineWrapper
public class Provider {
//When consument calls Provider in order to encrypt data , he passes EncryptionRequest object and KeyFamily name which must be in use 
	
	
	//encrypt methods generates IV , takes alias of actual key and iterates through EncrypionRequest objects
public CompoundCryptoReceipt encrypt(EncryptionRequest request,String KeyFamily)
throws LiveKeyNotFoundException,KeyNotFoundException,MultipleAliasIdException,NoSuchAlgorithmException{
	byte[]iv=new byte[16];
	SecureRandom ivGenerator=SecureRandom.getInstance("SHA1PRNG");
	
	CompoundCryptoReceipt receipts=new CompoundCryptoReceipt();
	String cryptoColumn=null;
	String plainText=null;
	byte[]cipherText=null;
	CryptoReceipt receipt=null;
	
	EngineWrapper engine=new EngineWrapper();
	KeyAlias alias=KeyAlias.getLiveKeyAlias(KeyFamily);
	String keyId=alias.getKeyId();
	String aliasID=alias.getAliasId();
	
	HashMap plaintexts=request.getPlainTexts();
	Set cryptoColumns=plaintexts.keySet();
	Iterator i=cryptoColumns.iterator();
	while(i.hasNext())
	{
		cryptoColumn=(String)i.next();
		plainText=(String)plaintexts.get(cryptoColumn);
		ivGenerator.nextBytes(iv);
		
		cipherText=engine.encrypt(plainText.getBytes(), iv, keyId);
		
		receipt=new CryptoReceipt(Utils.bytes2HexString(cipherText),Utils.bytes2HexString(iv),aliasID);
		

	}
	return receipts;
	
	
}
	
//Decryption is not much different than encryption
//we just take alias ID from CompoundCryptoReceipt instead of actual key
public DecryptionResult decrypt(CompoundCryptoReceipt receiptWrapper)
throws InvalidKeyStateException,KeyNotFoundException{
	DecryptionResult plaintext=new DecryptionResult();
	
	byte[]plaintexts=null;
	String cryptoColumn=null;
	CryptoReceipt receipt=null;
	String aliasId=receiptWrapper.getAliasId();
	KeyAlias alias=new KeyAlias(aliasId);
	if(! alias.isValidForDecryption())
	{
		throw(new InvalidKeyStateException(aliasId));
	}
	EngineWrapper engine=new EngineWrapper();
	HashMap receipts=receiptWrapper.getAllReceipt();
	Set cryptoColumns=receipts.keySet();
	Iterator i=cryptoColumns.iterator();
	while(i.hasNext())
	{
		cryptoColumn=(String)i.next();
		receipt=(CryptoReceipt)receipts.get(cryptoColumns);
		
		plaintexts=engine.decrypt(Utils.hexStringToBytes(receipt.getCipherText()),Utils.hexStringToBytes(receipt.getIv()),alias.getKeyId());
		 
		plaintext.addPlainText(cryptoColumn,new String(plaintexts));
		
	}
	return plaintext;
}

public CompoundCryptoReceipt replaceKey(CompoundCryptoReceipt receiptWrapper,String KeyFamily)
throws InvalidKeyStateException,LiveKeyNotFoundException,KeyNotFoundException,MultipleAliasIdException,NoSuchAlgorithmException{
	KeyAlias alias=new KeyAlias(receiptWrapper.getAliasId());
	String status=alias.getKeyState();
	CompoundCryptoReceipt receipts=null;
	if(alias.equals(KeyAlias.EXPIRED))
	{
		DecryptionResult plaintext=decrypt(receiptWrapper);
		receipts=encrypt(plaintext,KeyFamily);
		
	}
	else
	{
		System.out.println("Key status:UNKOWN");
		
	}
	return receipts;
		
}




}
