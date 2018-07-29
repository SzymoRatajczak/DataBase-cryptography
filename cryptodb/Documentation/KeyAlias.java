package cryptodb.Documentation;
import java.sql.*;

import java.time.DateTimeException;
import java.util.Calendar;

import cryptodb.DbManager.DbManager;
import cryptodb.Exceptions.AliasException;
import cryptodb.Exceptions.InvalidKeyStateException;

import java.lang.*;
import java.security.InvalidKeyException;

//this class defines alias of key  including  status flag of key and internal variables of documentation

public class KeyAlias { 
//define key attributes
	public static final String TERMINATED="TERMINATED";
	public static final String RETIRED="RETIRED";
	public static final String ACTIVE="ACTIVE";
	public static final String PENDING="PENDING";
	public static final String EXPIRED="EXPIRED";
	public static final String LIVE="LIVE";
	
	
	private boolean changed=false;
	
	private String aliasId=null;
	private String keyAlias=null;
	private String keyFamily=null;
	private String engine=null;
	private String keyId=null;
	private Timestamp activationDate=null;
	private String status=null;
	
	public KeyAlias(String aliasId)
	{
		this.aliasId=aliasId;
		populate();
	}
	
	//this part provides access to key attributes
	//each modification is switching  changed field

	public final String getAliasId()
	{
		return aliasId;
	}
	public final void setKeyAlias(String Keyalias)
	{
		this.keyAlias=Keyalias;
		changed=true;
	}
	public final String getKeyAlias()
	{
		return keyAlias;
	}
	
	public final void setKeyFamily(String keyFamily)
	{
		this.keyFamily=keyFamily;
		changed=true;
	}
	
	public final String getKeyFamily()
	{
		return keyFamily;
	}
	
	public final void SetEngine(String engine)
	{
		this.engine=engine;
		changed=true;
	}
	
	public final String getEngine()
	{
		return engine;
		
	}
	
	public final void SetKeyId(String keyId)
	{
		this.keyId=keyId;
		changed=true;
	}
	public final String getKeyId()
	{
		return keyId;
	}
	
	public final void SetActivationDate(Timestamp when)
	{
		this.activationDate=when;
		changed=true;
	}
	
	public final Timestamp getActivationDate()
	{
		return activationDate;
		
	}
	
	public final String getStatus()
	{
		return status;
	}
	//Key status can be ACTIVE,RETIRED,TERMINATED
	//Key can be created only when ACTIVE status is ON
	//Only EXPIRED key can be retired and only PENDING or RETIRED key can be TERMINATED
	
	
	private void setStatus(String status)
	{
		this.status=status;
		changed=true;
	}
	
	public final void retireKey()
		throws InvalidKeyStateException{
			if(getKeyState().equals(KeyAlias.EXPIRED))
		
			{
				status=KeyAlias.RETIRED;
				changed=true;
			}
			else
			{
				throw(new InvalidKeyStateException("Only expired keys can be retired"));
				
			}
		}
	
		public final void terminateKey()
	throws InvalidKeyStateException{
				if(getKeyState().equals(KeyAlias.PENDING) || getKeyState().equals(KeyAlias.RETIRED))
				{
					status=KeyAlias.TERMINATED;
					changed=true;
					
				}
				else
				{
					throw(new InvalidKeyStateException("Only pending or retired keys can be terminated"));
				}
			}
		

//this method takes information that are needed to create key alias follow by 
//save alias  into documentation and returns  ID of newly created documentation element 

		
		//Date field defines when key will be ACTIVE
		
	public	static KeyAlias getNewAlias(String alias,String engine,String family,String days_pending,String vaultKeyId)
		throws AliasException{
			try
			{
				int days=Integer.parseInt(days_pending);
				if(days<0)
				{
					throw(new DateTimeException("Date in the past"));
					
				}
			
			}
		
			catch(NumberFormatException e)
			{
				System.out.println(e);
			}
			
			String aliasId=null;
	        PreparedStatement pstmt=null;
			ResultSet rs=null;
			try
			{
				String stmt="Insert into key_manifest"+"Values("+"NULL,?,?,?,?"+"DATE_ADD(NOW() ,INTERVAL ?DAY ),"+ "'ACTIVE')";
				
				Connection con=DbManager.getDbConnection();
				pstmt=con.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1,alias);
				pstmt.setString(2, family);
				pstmt.setString(3,engine);
				pstmt.setString(4, vaultKeyId);
				pstmt.setString(5, days_pending);
				int rows=pstmt.executeUpdate();
				
				aliasId=null;
				
				if(rs.next())
				{
					aliasId=rs.getString(1);
					
				}
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			
			finally
			{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}
					catch(SQLException e)
					{
						System.out.println(e);
					}
					rs=null;
				}
				if(pstmt!=null)
				{
					try
					{
						pstmt.close();
					}
					catch(SQLException e)
					{
						System.out.println(e);
					}
					pstmt=null;
				}
				
			}
			KeyAlias newAlias=new KeyAlias(aliasId);
			newAlias.populate();
			return newAlias;
			
		}

		
//this method reads alias
//as a result of this method, KeyAlias object will be a resemblance of this what can be found into the documentation

public final void populate()
{
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try
	{
		String query="Select *"+"From key_manifest"+"Where alias_id=?";
		Connection con=DbManager.getDbConnection();
		pstmt=con.prepareStatement(query);
		pstmt.setString(1, aliasId);
		rs=pstmt.executeQuery();
		
		if(rs.next())
		{
			setKeyAlias(rs.getString("key_alias"));
			setKeyFamily(rs.getString("key_family"));
			SetEngine(rs.getString("engine"));
			SetKeyId(rs.getString("key_id"));
			SetActivationDate(rs.getTimestamp("key_activation_date"));
			setStatus(rs.getString("status"));
			
			
			
		}
		//change field must be set to let us know alias was just read and no modifications were implemented
		changed=false;
	}
	catch(SQLException e)
	{
		System.out.println(e);
	}
	
	finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			rs=null;
		}
		if(pstmt!=null)
		{
			try
			{
				pstmt.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			pstmt=null;
		}
		
	}


}
//this method takes actual key of given family
//Query generates list of all keys of given family
public static KeyAlias getLiveKeyAlias(String keyFamily)
{
	KeyAlias alias=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	try
	{
		String query=("Select * " + "From key_manifest"+"Where status='ACTIVE'"+"AND key_activation_date<=now()"+"AND key_familiy=?"+"ORDER BY key_activation_date DESC");
		
	Connection con=DbManager.getDbConnection();
	pstmt=con.prepareStatement(query);
	pstmt.setString(1, keyFamily);
	rs=pstmt.executeQuery();
	
	if(rs.next())
	{
		String aliasId=rs.getString("alias_id");
		alias=new KeyAlias(aliasId);
		alias.setKeyAlias(rs.getString("key_alias"));
		alias.setKeyFamily(rs.getString("key_family"));
		alias.SetEngine(rs.getString("engine"));
		alias.SetKeyId(rs.getString("key_id"));
		alias.SetActivationDate(rs.getTimestamp("key_activation_date"));
		alias.setStatus(rs.getString("status"));
		alias.changed=false;
		
		
	}
	else
	{
		Exception e=new  Exception(keyFamily);
		System.out.println(e);
	}
	
	
	
	}
	catch(SQLException e)
	{
		System.out.println(e);
	}
	finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			rs=null;
		}
		if(pstmt!=null)
		{
			try
			{
				pstmt.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			rs=null;
			
		}
		
	}
	return alias;
	
}
//if we change sth in alias it must saved

public final void save()
{
	PreparedStatement pstmt=null;
	try
	{
		String query="UPDATE key_manifest"+"SET key_alias=?,"+"key_family=?,"+"engine=?,"+"key_id=?,"+"key_activation_date=?,"+"status=?";
		Connection con=DbManager.getDbConnection();
		pstmt=con.prepareStatement(query);
		pstmt.setString(1, keyAlias);
		pstmt.setString(2, keyFamily);
		pstmt.setString(3, engine);
		pstmt.setString(4,keyId);
		pstmt.setTimestamp(5, activationDate);
		pstmt.setString(6,status);
		pstmt.setString(7,aliasId);
		
		int rows=pstmt.executeUpdate();
		changed=false;
		
	}
	catch(SQLException e)
	{
		System.out.println(e);
	}
	finally
	{
		if(pstmt!=null) {
			try
			{
				pstmt.close();
			}
			catch(SQLException e)
			{
				System.out.println(e);
			}
			pstmt=null;
			
		}
	}
	
}

//this method provides us with information about key status but 
//it must be called  out  before any crypto operations


public final String getKeyState()
{
	String state=null;
	if(changed)
	{	
		save();
	}
	
	Timestamp now=new Timestamp(  (Calendar.getInstance()).getTimeInMillis() );
	if(status.equals(KeyAlias.TERMINATED))
{
	state=KeyAlias.TERMINATED;
}
	else if(status.equals(KeyAlias.RETIRED))
	{
		state=KeyAlias.RETIRED;
	}
	else if(status.equals(KeyAlias.ACTIVE))
	{
		state=KeyAlias.ACTIVE;
	String family=getKeyFamily();
	try
	{
		//i assume that database's clock and JVM's clock are synchronized
		if(activationDate.after(now))
		{
			state=KeyAlias.PENDING;
			
		}
		else
		{
			KeyAlias liveAlias=KeyAlias.getLiveKeyAlias(family);
			if(aliasId.equals(liveAlias.getAliasId()))
			{
				state=KeyAlias.LIVE;
			}
			else
			{
				state=KeyAlias.EXPIRED;
			}
		}
		
	}
	catch(Exception e)
	{
		state=null;
	}
			
	
	
	}
	if(state==null)
	{
		Exception e=new Exception(aliasId);
		System.out.println(e);
		
		
		
	}
	return state;
	
	
}

//Decryption can be done only when key is ALIVE or EXPIRED
public boolean isValidForDecryption()
{
	boolean valid=false;
	Timestamp now=new Timestamp( (Calendar.getInstance()).getTimeInMillis());
	if(status.equals(KeyAlias.ACTIVE))
	{
		//key will be ALIVE in the future
		if(activationDate.after(now))
		{
			valid=false;
		}
		else
		{
			//key is/was already in use 
			valid=true;
		}
	}
	return valid;
}

}
	
	
	
	
	
	
	
	
	


