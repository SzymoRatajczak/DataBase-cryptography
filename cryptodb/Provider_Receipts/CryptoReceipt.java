package cryptodb.Provider_Receipts;


//simple form of receipt
public class CryptoReceipt {

	private String ciphertext=null;
	private String iv=null;
	private String aliasId=null;
	
	public CryptoReceipt(String ct,String iv,String aliasId)
	{
		this.ciphertext=ct;
		this.iv=iv;
		this.aliasId=aliasId;
	}
	final public String getCipherText()
	{
		return ciphertext;
	}
	
	final public String getIv()
	{
		return iv;
	}
	final public String getAliasId()
	{
		return aliasId;
	}
}
