package cryptodb.Provider_Receipts;
import java.util.*;

import cryptodb.Exceptions.MultipleAliasIdException;

//this is complex form of receipt
public class CompoundCryptoReceipt {

	private HashMap receipts=new HashMap();
	private String aliasId=null;
	
	public void addReceipt(String cryptoColumn,CryptoReceipt receipt)
	throws MultipleAliasIdException{
		if(aliasId!=null)
		{
			if(!aliasId.equals(receipt.getAliasId()))
			{
				MultipleAliasIdException e=new MultipleAliasIdException(this.aliasId,receipt.getAliasId());
			 
			}
			
		}
		else
		{
			this.aliasId=receipt.getAliasId();
		}
		receipts.put(cryptoColumn, receipt);
			
	}
	
	public String getCipherText(String cryptoColumn)
	{
		CryptoReceipt receipt=(CryptoReceipt)receipts.get(cryptoColumn);
		return receipt.getCipherText();
	}
	
	public String getIv(String cryptoColumn)
	{
		CryptoReceipt receipt=(CryptoReceipt)receipts.get(cryptoColumn);
		return receipt.getIv();
	}
	
	public String getAliasId()
	{
		return aliasId;
	}
	public HashMap getAllReceipt()
	{
		return receipts;
	}
}
