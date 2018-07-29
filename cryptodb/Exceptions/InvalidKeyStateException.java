package cryptodb.Exceptions;

public class InvalidKeyStateException  extends Exception{
private String KeyAliasId=null;

public InvalidKeyStateException(String keyAliasId)
{
	this.KeyAliasId=keyAliasId;
}

public String getAliasId()
{
	return KeyAliasId;
}

}
