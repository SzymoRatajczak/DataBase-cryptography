package cryptodb.Exceptions;


//if we add receipt  with different alias ID than others  we will get this exception
public class MultipleAliasIdException extends Exception {
	private String correctAliasId;
	private String rogueAliasId;
	
	public MultipleAliasIdException(String correctAliasId,String rogueAliasId)
	{
		this.correctAliasId=correctAliasId;
		this.rogueAliasId=rogueAliasId;
	}
	
	public String getCorrectAliasId()
	{
		return correctAliasId;
	}
	
	public String getRogueAliasId()
	{
		return rogueAliasId;
	}

}
