package cryptodb.DataConversion;
final  public class Utils {

	//Description of data conversion  
	
	
	
	//Bytes matrix is converted into hex chain
	public static String bytes2HexString(byte[]theBytes)
	{
		
		//We do not filter entry data because this is only  test environment
//in production environment we will use a bit different approach
		
		StringBuffer hexString=new StringBuffer();
		String ConvertedByte="";
		
		for (int i=0;i<theBytes.length;i++)
		{
			ConvertedByte=Integer.toHexString(theBytes[i]);
			
			
			if(ConvertedByte.length()<2)
			{
				ConvertedByte="00".substring(ConvertedByte.length())+ConvertedByte;
	
			}
	
			else if(ConvertedByte.length()>2)
			{
				ConvertedByte=ConvertedByte.substring(ConvertedByte.length()-2);
				
			}
			hexString.append(ConvertedByte.toUpperCase());
		}
		return hexString.toString();
		
	}
	
	//it converts hex chain into bytes matrix
	
	public static byte[] hexStringToBytes(String hexString)
	{
		byte[]theBytes=new byte[hexString.length()/2];
		byte leftHalf=0x0;
		byte rightHalf=0x0;
		
		for(int i=0,j=0;i<hexString.length()/2;i++,j=i*2)
		{
			rightHalf=(byte)(Byte.parseByte(hexString.substring(j+1, j+2),16)&(byte)0xF);
			leftHalf=(byte)((Byte.parseByte(hexString.substring(j,j+1),16)<<4)&(byte)0xF0);
			
			theBytes[i]=(byte)(leftHalf|rightHalf);
			
		}
		return	 theBytes;
	
		
		
	}
	
	
}
