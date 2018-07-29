package cryptodb.Exceptions;

public class LiveKeyNotFoundException extends Exception {
private String KeyFamily;

public LiveKeyNotFoundException(String keyFamily)
{
	this.KeyFamily=keyFamily;
}

public String getKeyFamily()
{
	return KeyFamily;
}
	
	
}
