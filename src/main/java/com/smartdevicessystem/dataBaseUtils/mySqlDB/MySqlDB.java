package dataBaseUtils.mySqlDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.management.RuntimeErrorException;
import dataBaseUtils.IGenericDB;
import java.sql.SQLException;

public class MySqlDB implements IGenericDB{
	
	private final Connection connection;
	
	public MySqlDB(String DB_URL, String DB_USER, String PASS){
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(DB_URL, DB_USER, PASS);
		} catch (SQLException e) {
			System.out.println(e);
			throw new RuntimeErrorException(null);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			throw new RuntimeErrorException(null);
		}
		
	}

	
	@Override
	public void createDB(String nameDB, Object query) {
		
		String[] tableQery = (String[])query;
		
		try(Statement stmt = connection.createStatement()) {
		         stmt.executeUpdate("CREATE DATABASE " + nameDB);
		         stmt.executeUpdate("USE " + nameDB);
		         
		         if(tableQery != null) {
		        	 for (int i = 0; i < tableQery.length; ++i) {
		        		 stmt.executeUpdate(tableQery[i]);
		        		 }
		         }
		         
		      } catch (SQLException e) {
		         throw new RuntimeException(e.toString());
		      }	
	}

	
	@Override
	public void deleteDB(String nameDB) {
		try(Statement stmt = connection.createStatement()) {
				 stmt.executeUpdate("USE " + nameDB);
				 stmt.executeUpdate("DROP DATABASE " + nameDB);
		      } catch (SQLException e) {
		         e.printStackTrace();
		         throw new RuntimeException("");
		      }			
	}
	
	
	@Override
	public void writeToDB(String nameDB, Object query) {
		
		String[] sqlInsertStr = (String[])query;
		
		try(Statement stmt = connection.createStatement()) {
				 stmt.executeUpdate("USE " + nameDB);
				 
				 for (int i = 0; i < sqlInsertStr.length; ++i) {
					 System.out.println(sqlInsertStr[i]);
						stmt.executeUpdate(sqlInsertStr[i]);
					}
		      } catch (SQLException e) {
		         e.printStackTrace();
		         throw new RuntimeException("");
		      }		
	}

	
	@Override
	public ResultSet readFromDB(String ftomDB ,Object query) {
		
		try {
			Statement stmt = connection.createStatement();
			 stmt.executeUpdate("USE " + ftomDB);
			return stmt.executeQuery((String)query);
				 
	      } catch (SQLException e) {
	         e.printStackTrace();
	         throw new RuntimeException("");
	      }		 
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}