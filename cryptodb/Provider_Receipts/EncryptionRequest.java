package cryptodb.Provider_Receipts;
import java.util.HashMap;


//to support Provider class
public interface EncryptionRequest {

	//Consumer puts plainText here follow by this text will be sent to the encryption function of Provider class 
	
	public HashMap getPlainTexts();
	
	
}
