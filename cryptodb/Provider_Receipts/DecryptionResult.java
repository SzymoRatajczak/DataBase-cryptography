package cryptodb.Provider_Receipts;
import java.util.*;

//it contains  data being result of method-data for encrpytion
//this class implements EncryptionEquest in order to send any decrypted data to the encryption 
public class DecryptionResult implements EncryptionRequest {

	private HashMap plaintexts=new HashMap();
	
	public void addPlainText(String column,String plaintext)
	{
		plaintexts.put(column, plaintext);
	}
	
	public String getPlainText(String column)
	{
		return (String)plaintexts.get(column);
	}
	
	public HashMap getPlainTexts()
	{
		return plaintexts;
	}

	//HashMap is cohesive device between two classes
	//Consumer provides plain text that is passed to the encryption method of Provider class
}
