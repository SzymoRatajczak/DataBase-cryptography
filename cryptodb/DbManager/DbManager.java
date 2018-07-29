package cryptodb.DbManager;
import java.sql.*;
public class DbManager {
static
{
	
try
{
	
	String driver="com.mysql.jdbc.Driver";
	Class.forName(driver).newInstance();
	
}

catch(ClassNotFoundException e)
{
	
	//handle exception
}

catch(InstantiationException e)
{
	//handle exception
	
	
}

catch(IllegalAccessException e)
{
	
	
	
}

}
	
	
	
public static Connection getDbConnection()
{
	
	Connection conn=null;
	try
	{
		
		//we are storing here our passwords(but only in test environment)
		//but in production environment we will never do so
		conn=DriverManager.getConnection("jdbc:mysql://localhost/cryptodb?"+"user=cryptusr&password=password");
		
	}
	catch(SQLException ex)
	{
		//handle exception
	}
	
	return conn;
	//here we are using only one database but in real life scenario  at least  two database  are in use
	//the first one is for our encrypted credentials data
	//the second one is part of the key safe, if we are using HSM  this database is part of this 
	//otherwise the database must be stored on well protected server
	

//In this project I will use HSM because it is much more secure approach than server
	

}
}
