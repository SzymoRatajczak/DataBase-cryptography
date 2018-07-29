package cryptodb.Consumer;

import java.util.HashMap;

import cryptodb.Provider_Receipts.EncryptionRequest;

//it stores informations about customer, his credit card and so on
//because this class implements EncryptionRequest interface it means data can be directly send to the encryption operations 
public class CustomerInfo implements EncryptionRequest{

	private String custId;
	private String first;
	private String firstInitial;
	private String last;
	private String lastInitial;
	private String email;
	private String zip;
	private CreditCardInfo cci;
	
	public void setcustomerId(String customId)
	{
		this.custId=customId;
	}
	public String getCustomerId()
	{
		return custId;
	}
	public void setFirstName(String fullFirst)
	{
		this.first=fullFirst.substring(1);
		this.firstInitial=fullFirst.substring(0,1);
	}
	
	public String getFullFirst()
	{
		return first+ " " + firstInitial;
	}
	public String getFirstInitial()
	{
		return firstInitial;
	}
	public String getFirst()
	{
		return first;
	}
	public void setLastName(String lastName)
	{
		this.last=lastName.substring(1);
		this.lastInitial=lastName.substring(0, 1);
	}
	public String getFullLast()
	{
		return last+ " " + lastInitial;
	}
	public String getLastInitial()
	{
		return lastInitial;
	}
	public String getLast()
	{
		return last;
	}
	public void setEmail(String email)
	{
		this.email=email;
	}
	public String getEmail()
	{
		return email;
	}
	
	public void setZipCode(String zipCode)
	{
		this.zip=zipCode;
	}
	public String getZip()
	{
		return zip;
	}
	public void setCreditCardInfo(CreditCardInfo cci)
	{
		this.cci=cci;
	}
	public CreditCardInfo getCreditCardInfo()
	{
		return cci;
	}

	public HashMap getPlainTexts()
	{
		HashMap plaintexts=new HashMap();
		plaintexts.put(CustomerManger.FIRST_NAME,first);
		plaintexts.put(CustomerManger.LAST_NAME,last);
		plaintexts.put(CustomerManger.EMAIL_ADDR,email);
		
		return plaintexts;
	}
}
