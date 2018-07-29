package cryptodb.Consumer;
import cryptodb.*;
import cryptodb.DbManager.DbManager;
import cryptodb.Exceptions.CustomerNotFoundException;
import cryptodb.Exceptions.InvalidKeyStateException;
import cryptodb.Exceptions.KeyNotFoundException;
import cryptodb.Exceptions.LiveKeyNotFoundException;
import cryptodb.Exceptions.MultipleAliasIdException;
import cryptodb.Provider_Receipts.CompoundCryptoReceipt;
import cryptodb.Provider_Receipts.CryptoReceipt;
import cryptodb.Provider_Receipts.DecryptionResult;
import cryptodb.Provider_Receipts.Provider;

import java.sql.*;
import java.util.HashMap;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

//it reflects our customer
public class CustomerManger {

	public static final String FIRST_NAME="first_name";
	public static final String LAST_NAME="last_name";
	public static final String EMAIL_ADDR="email";
	public static final String CREDIT_CARD="credit_card";
	public static final String EXP_DATE="exp_date";
	
	
	public static void main(String[]args)
	{
		String usage="Usage:CustomerManager{add,print,find,replacePiiKey}";
		if(args.length==0)
		{
			System.out.println(usage);
			System.exit(2);
		}
		
		CustomerManger mgr=new CustomerManger();
		if(args[0].equals("add"))
		{
			if(args.length==7)
			{
				CustomerInfo cust=new CustomerInfo();
				cust.setFirstName(args[1]);
				cust.setLastName(args[2]);
				cust.setEmail(args[3]);
				cust.setZipCode(args[4]);
			
			CreditCardInfo cci=new CreditCardInfo();
			cci.setCreditCard(args[5]);
			cci.setExpDate(args[6]);
			
			cust.setCreditCardInfo(cci);
			mgr.addCustomer(cust);
			}
			else
			{
				System.out.println("Usage:CustomManager add<first> <last><email><zip><cc><exp>");
			}
		}
		else if(args[0].equals("print"))
		{
			if(args.length==2)
			{
				String custId=args[1];
				mgr.printAll(custId);
			}
			else
			{
				System.out.println("Usage:CustomerManager print cust_id");
			}
		}
		else if(args[0].equals("find"))
		{
			if(args.length==4)
			{
				try
				{
					CustomerInfo cust=mgr.findCustomer(args[1],args[2],args[3]);
					mgr.printCustomer(cust);
					mgr.printCreditCardInfo(cust.getCreditCardInfo());
				}
				catch( Exception e)
				{
					System.out.println("Customer:"+ "not found");
				}
			 
			}
			else
			{
				System.out.println("Usage:CustomerManger find"+"<first_name><last_name><zip_code>");
			}
		}
		else if(args[0].equals("replacePiiKey"))
		{
			if(args.length==2)
			{
				mgr.replacePiiKey(args[1]);
			}
			else
			{
				System.out.println("Usage:CustomerManager replace"+"<alias_id");
			}
		}
		else
		{
			System.out.println(usage);
		}
	}
	
	
	void addCustomer(CustomerInfo cust)
	{
		CreditCardInfo cci=cust.getCreditCardInfo();
		
		PreparedStatement pstmt=null;
				try
		{
					Provider provider =new Provider();
					CompoundCryptoReceipt piiReceipts=provider.encrypt(cust, "pii");
					CompoundCryptoReceipt cciReceipts=provider.encrypt(cci,"cci");
					String query="INSERT Into customer_info" + "values(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					Connection con=DbManager.getDbConnection();
					pstmt=con.prepareStatement(query);
					pstmt.setString(1,piiReceipts.getCipherText(FIRST_NAME));
					pstmt.setString(2,piiReceipts.getIv(FIRST_NAME));
					pstmt.setString(3, cust.getFirstInitial());
					pstmt.setString(4, piiReceipts.getCipherText(LAST_NAME));
					pstmt.setString(5, piiReceipts.getIv(LAST_NAME));
					pstmt.setString(6, cust.getLastInitial());
					pstmt.setString(7, piiReceipts.getCipherText(EMAIL_ADDR));
					pstmt.setString(8, piiReceipts.getIv(EMAIL_ADDR));
					pstmt.setString(9, cust.getZip());
					pstmt.setString(10,cciReceipts.getCipherText(CREDIT_CARD));
					pstmt.setString(11, cciReceipts.getIv(CREDIT_CARD));
					pstmt.setString(12, cci.getLastFour());
					pstmt.setString(13, cciReceipts.getCipherText(EXP_DATE));
					pstmt.setString(14, cciReceipts.getIv(EXP_DATE));
					pstmt.setString(15, piiReceipts.getAliasId());
					pstmt.setString(16, cciReceipts.getAliasId());
					
					int rows=pstmt.executeUpdate();
		}
				catch(NoSuchAlgorithmException e)
				{
					System.out.println(e.toString());
				}
				catch(MultipleAliasIdException e)
				{
					System.out.println("Attempted to multiple alias IDs in a compound receipt");
				}
				catch(LiveKeyNotFoundException e)
				{
				System.out.println("Key family has not  got a live key");
				}
				catch(KeyNotFoundException e)
				{
					System.out.println("Key:"+e.getKeyId()+"not found by an engine"+ e.getEngine());;
				}
				catch(SQLException e)
				{
					System.out.println(e);
				}
				finally
				{
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
	
	//String type is not a secure type so we should not use this one , the best would be bytes arrays
	//but in real life scenario it is impossible  totally avoid String  so 
	//we must take trade off and use String for unencrypted data
	//but bear that in mind that String is not Secure type 

//it displays all  customer informations
	
	private void printAll(String custId)
	{
		try
		{
			CustomerInfo cust=getCustomer(custId);
			printCustomer(cust);
			printCreditCardInfo(cust.getCreditCardInfo());
			
		}
		catch( Exception e)
		{
			System.out.println("Customer not found");
		}
		 
	}
	
	private void printCreditCardInfo(CreditCardInfo card)
	{
		System.out.println("Credit Card:"+card.getFullCreditCard());
		System.out.println("Expiration:"+card.getExpDate());
		
	}
	
	private void printCustomer(CustomerInfo cust)
	{
		System.out.println("First name:"+cust.getFullFirst());
		System.out.println("Last name:"+cust.getFullLast());
		System.out.println("Email:"+cust.getEmail());
		System.out.println("Zip code:"+cust.getZip());
	}
	CustomerInfo getCustomer(String custId)
	throws CustomerNotFoundException,InvalidKeyStateException{
		CustomerInfo cust=new CustomerInfo();
		CreditCardInfo card=new CreditCardInfo();
		ResultSet rs=null;
		
		PreparedStatement pstmt=null;
		try
		{
			String query="Select *  From customer_info Where customer_id=?";
			Connection con=DbManager.getDbConnection();
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, custId);
			rs=pstmt.executeQuery();
			
			//we are using CompoundCryptoReceipt to store all receipts for keys in range of from pii to cci
			CompoundCryptoReceipt piiReceipts=new CompoundCryptoReceipt();
			
			CompoundCryptoReceipt cciReceipt=new CompoundCryptoReceipt();
			
			if(rs.next())
			{
				piiReceipts.addReceipt(FIRST_NAME, new CryptoReceipt(rs.getString("first_name"),rs.getString("first_name_iv"),rs.getString("pii_scope_key_id")));
				
				piiReceipts.addReceipt(LAST_NAME, new CryptoReceipt(rs.getString("last_name"),rs.getString("last_name_iv"),rs.getString("pii_scope_key_id")));
				
				piiReceipts.addReceipt(EMAIL_ADDR, new CryptoReceipt(rs.getString("email_address"),rs.getString("email_address_iv"),rs.getString("pii_scope_key_id")));
				
			cciReceipt.addReceipt(CREDIT_CARD, new CryptoReceipt(rs.getString("credit_card_num"),rs.getString("credit_card-num_iv"),rs.getString("cci_scope_key_id")));
			
			cciReceipt.addReceipt(EXP_DATE, new CryptoReceipt(rs.getString("expiration_date"),rs.getString("expiration_date_iv"),rs.getString("cci_scope_key_id")));
			
			
			Provider provider=new Provider();
			DecryptionResult piiPlainText=provider.decrypt(piiReceipts);
			DecryptionResult ciiPlainText=provider.decrypt(cciReceipt);
			
			String FirstName=rs.getString("first_initial")+piiPlainText.getPlainText(FIRST_NAME);
			
			String lastName=rs.getString("last_inital")+piiPlainText.getPlainText(LAST_NAME);
			
			cust.setFirstName(FirstName);
			cust.setLastName(lastName);
			cust.setEmail(piiPlainText.getPlainText(EMAIL_ADDR));
			cust.setZipCode(piiPlainText.getPlainText("zip-code"));
			
			String creditCardNum=ciiPlainText.getPlainText(CREDIT_CARD)+rs.getString("last_four_ccn");
			card.setCreditCard(creditCardNum);
			card.setExpDate(ciiPlainText.getPlainText(EXP_DATE));
			
			cust.setCreditCardInfo(card);			
			}
			else
			{
				CustomerNotFoundException e=new CustomerNotFoundException(custId);
			 throw e;
			}
			
		}
		catch(MultipleAliasIdException e)
		{
			System.out.println("Attempted to put multiple aliasIds in a CompoundReceipt");
		}
		catch(KeyNotFoundException e)
		{
			System.out.println("Key not found");
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
		catch(InvalidKeyStateException e)
		{
			System.out.println(e);
		}
		finally
		{
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
		return cust;
	
	}
	
	
	//to find customer first of all we are using his initials and zip code
	private CustomerInfo  findCustomer(String firstName,String lastName,String zipCode)
	{
		String firstInitial=firstName.substring(0, 1);
		String lastInitial=lastName.substring(0, 1);
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		CustomerInfo cust=null;
		
		try
		{
			String query="Select * from customer_info Where first_initial=? AND last_initial=? AND zip_code=?";
			Connection con=DbManager.getDbConnection();
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, firstInitial);
			pstmt.setString(2, lastInitial);
			pstmt.setString(3, zipCode);
			rs=pstmt.executeQuery();
			
			boolean custNotFound=true;
			
			while(custNotFound && rs.next())
			{
				cust=getCustomer(rs.getString("customer_id"));
				if(cust.getFullFirst().equals(firstName)&& cust.getFullLast().equals(lastName))
				{
					custNotFound=false;
				}
			}
			
		}
		catch(InvalidKeyStateException e)
		{
			System.out.println(e);
		}
		catch(CustomerNotFoundException e)
		{
			System.out.println(e);
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
		finally
		{
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
		return cust;
		//we are assuming that disclosure of initials is not risk,
		//disclosure of the first two litters of  out first and last name is also acceptable
		//but everything boils down to how unique  credentials are
		
	}
	
	// when key expiration date is met or key was captured  it must be replaced ,thereafter
	// key replacement takes place in  keyFamily
	private void replacePiiKey(String aliasId)
	{
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		CustomerInfo cust=null;
		try
		{
			
			String query="Select * From customer_info Where pii_scope_key_id=? ";
			Connection con=DbManager.getDbConnection();
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, aliasId);
			rs=pstmt.executeQuery();
			
			
			Provider provider =new Provider();
			CompoundCryptoReceipt  piiReceipts=null;
			String custId=null;
			while(rs.next())
			{
				piiReceipts=new CompoundCryptoReceipt();
				custId=rs.getString("customer_id");
			
			
			piiReceipts.addReceipt(FIRST_NAME,new CryptoReceipt( rs.getString("first_name"),rs.getString("first_name_iv"),rs.getString("pii_scope_key_id")));
			piiReceipts.addReceipt(LAST_NAME, new CryptoReceipt(rs.getString("last_name"),rs.getString("last_name_iv"),rs.getString("pii_scope_key_id")));
			piiReceipts.addReceipt(EMAIL_ADDR, new CryptoReceipt(rs.getString("email_address"),rs.getString("email_address_iv"),rs.getString("pii_scope_key_id")));
			
			CompoundCryptoReceipt cryptoReceipts=provider.replaceKey(piiReceipts, "pii");
			
			updatePii(cryptoReceipts,custId);
		}
		}
		catch(SQLException e)
		{
			System.out.println(e);
		}
		catch(LiveKeyNotFoundException e)
		{
			System.out.println(e);
		}
		catch(KeyNotFoundException e)
		{
			System.out.println(e);
		}
		catch(MultipleAliasIdException e)
		{
			System.out.println(e);
		}
		catch(NoSuchAlgorithmException e)
		{
			System.out.println(e);
		}
		catch(InvalidKeyStateException e)
		{
			System.out.println(e);
		}
		
		finally
		{
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
				
			}
			pstmt=null;
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
				
			}
			rs=null;
		}
		
	}
	private void updatePii(CompoundCryptoReceipt receipts,String custId)
	{
	 PreparedStatement pstmt=null;
	 ResultSet rs=null;
	 try
	 {
		 String query="Update customer_info Set first_name=?,first_name_iv=?,last_name=?,last_name_iv=?,email_address=?,email_address_iv=?,pii_scope_key_id=?";
		 Connection conn=DbManager.getDbConnection();
		 pstmt=conn.prepareStatement(query);
		 pstmt.setString(1, receipts.getCipherText(FIRST_NAME));
		 pstmt.setString(2, receipts.getIv(FIRST_NAME));
		 pstmt.setString(3, receipts.getCipherText(LAST_NAME));
		 pstmt.setString(4, receipts.getIv(LAST_NAME));
		 pstmt.setString(5, receipts.getCipherText(EMAIL_ADDR));;
		 pstmt.setString(6, receipts.getIv(EMAIL_ADDR));
		 pstmt.setString(7, receipts.getAliasId());
		 pstmt.setString(8,custId);
		 pstmt.executeUpdate();
	 }
	 catch(SQLException e)
	 {
		 System.out.println(e);
	 }
	 finally
	 {
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
			 
		 }
		 pstmt=null;
	 }
	
	//COnsumer is pivotal class -this class is very between business/credentials informations and crypto system 
	
}
}
