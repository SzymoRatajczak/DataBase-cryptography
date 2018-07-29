package cryptodb.Exceptions;

public class KeyNotFoundException  extends Exception{
    
	private String keyId;
	private String engine;
	public KeyNotFoundException(String engine,String keyID)
	{
		this.keyId=keyID;
		this.engine=engine;
	}
	
	public String getKeyId() {
		return keyId;
	}
	
	public String getEngine()
	{
		return engine;
	}
	
}
