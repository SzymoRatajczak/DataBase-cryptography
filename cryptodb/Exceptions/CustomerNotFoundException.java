package cryptodb.Exceptions;

public class CustomerNotFoundException extends Exception 
{
	
	private String custId;

public CustomerNotFoundException(String custId)
{
	this.custId=custId;
}

public String getCustomId()
{
	return custId;
}
	
}
