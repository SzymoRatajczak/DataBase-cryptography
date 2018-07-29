package cryptodb.Consumer;

import java.util.HashMap;

import cryptodb.Provider_Receipts.EncryptionRequest;

public class CreditCardInfo  implements EncryptionRequest{

	private String creditCard;
	private String lastFour;
	private String expDate;
	
	public void setCreditCard(String fullCreditCard)
	{
		int length=fullCreditCard.length();
		lastFour=fullCreditCard.substring(length-4);
		creditCard=fullCreditCard.substring(0,length-4);
		
	}
	public String getFullCreditCard()
	{
		return creditCard+ " " + lastFour;
	}
	public String getLastFour()
	{
		return lastFour;
	}
	String getCreditCard()
	{
		return creditCard;
	}
	public void setExpDate(String expDate)
	{
		this.expDate=expDate;
	}
	public String getExpDate()
	{
		return expDate;
	}
	public HashMap getPlainTexts()
	{
		HashMap plaintexts=new HashMap();
		plaintexts.put(CustomerManger.CREDIT_CARD, creditCard);
	plaintexts.put(CustomerManger.EXP_DATE,expDate);
	
	return plaintexts;
	}
}
