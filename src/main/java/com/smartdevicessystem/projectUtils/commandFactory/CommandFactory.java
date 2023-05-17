package com.smartdevicessystem.projectUtils.commandFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.smartdevicessystem.projectUtils.config.ConfigPropertiesFileMap;
import org.json.JSONObject;

import com.smartdevicessystem.projectUtils.dataBaseUtils.CachingNameDB;
import com.smartdevicessystem.projectUtils.dataBaseUtils.IGenericDB;
import com.smartdevicessystem.projectUtils.dataBaseUtils.mySqlDB.MySqlDB;

public class CommandFactory {
	
	private final ConcurrentHashMap<String, Function<JSONObject, JSONObject>> commandsTable = new ConcurrentHashMap<>();
	
	private final IGenericDB rdbmMysql;
	private final String manageDB = "IOTInfoDB";
	
	/*-------SINGLETON-------------------------*/
	private CommandFactory() {
		
		commandsTable.put("REGISTER COMPANY", this::registerCompanyCommand);
		commandsTable.put("REGISTER PRODUCT", this::registerProductCommand);
		commandsTable.put("REGISTER IOT", this::registerIOTCommand);
		commandsTable.put("DUMMY", this::dummyCommand);
		
		ConfigPropertiesFileMap configMap = new ConfigPropertiesFileMap("/home/yana/config_app");
		rdbmMysql = new MySqlDB(configMap.get("db.url"), configMap.get("db.username"), configMap.get("db.password"));
	}
	
	public static CommandFactory getCommandFactoryInstance(){
		return SingeltonInitClass.INSTANCE;
	}
	
	private static class SingeltonInitClass{
		private static CommandFactory INSTANCE = new CommandFactory();
	}
	
	/*--------PUBLIC METHODS ------------------ */
	public JSONObject execute(JSONObject json){	
		
		String key = json.getString("command");		
		return commandsTable.get(key).apply(json);
	}
	
	public void addCommand(Function<JSONObject, JSONObject> command, String key) {
		commandsTable.put(key, command);
	}
	

	/*-------REGISTER COMPANY COMMAND-----------*/
	private JSONObject registerCompanyCommand(JSONObject json) {

		JSONObject companyJson =  json.getJSONObject("info_company");
		String id = companyJson.getString("id_company");
		String name = companyJson.getString("name_company");	
				
		String nameCompanyDB = AddtoMangeDB(name, id);
		createNewDBCompany(nameCompanyDB);
		
		CachingNameDB.getCachingNameDB().add(id, nameCompanyDB);	
		
		JSONObject respomseJsonObject = new JSONObject();
		respomseJsonObject.append("result", "company created");
		
		return respomseJsonObject;
	}
		
		private String AddtoMangeDB(String name, String id) {
			
			String nameDB = new String("IOT" + id + name);
			String[] query = new String[] {"INSERT INTO Companies(id, name, db_name) VALUES (" + id + ", '" + name + "', '" + nameDB+ "')"};
			
			rdbmMysql.writeToDB(manageDB, query);	
			return nameDB;
			
		}
		
		private void createNewDBCompany(String nameDB) {
			String[] queriesTableCreate = new String[]{"CREATE TABLE Products (type VARCHAR(20) NOT NULL, model VARCHAR(50) NOT NULL, PRIMARY KEY (model))",
					"CREATE TABLE IOTProducts ( serial_number INT NOT NULL, model VARCHAR(50) NOT NULL, PRIMARY KEY (serial_number), FOREIGN KEY (model) REFERENCES Products(model))",
					"CREATE TABLE Updates ( id_update INT NOT NULL AUTO_INCREMENT, serial_number INT NOT NULL, date DATE, PRIMARY KEY (id_update), FOREIGN KEY (serial_number) REFERENCES IOTProducts(serial_number))"
				};
			
			rdbmMysql.createDB(nameDB, queriesTableCreate);
		}

	
	/*-------REGISTER PRODUCT COMMAND-----------*/


	private JSONObject registerProductCommand(JSONObject json) {
			
		String nameDB =  CachingNameDB.getCachingNameDB().getNameDBByID(json.getString("uid"));
		System.out.println("registerProductCommand ");
		if(nameDB == null) {
			nameDB = getNameDBfromMangeDB(json.getString("uid"));
			CachingNameDB.getCachingNameDB().add(json.getString("uid"), nameDB);
			
		}
		
		String type = json.getJSONObject("product").getString("type");
		String model = json.getJSONObject("product").getString("model");
		
		String[] query = new String[]{"INSERT INTO Products(type, model) VALUES ('" + type + "', '" + model + "')"};	
		
		rdbmMysql.writeToDB(nameDB, query);
		
		JSONObject respomseJsonObject = new JSONObject();
		respomseJsonObject.append("result", "product registered");
		
		return respomseJsonObject;
	}
	
	
	/*-------REGISTER IOT COMMAND---------------*/
	

		private JSONObject registerIOTCommand(JSONObject json) {
			
			String nameDB =  CachingNameDB.getCachingNameDB().getNameDBByID(json.getString("uid"));
			
			if(nameDB == null) {
				System.out.println("not exist in caching!");
				nameDB = getNameDBfromMangeDB(json.getString("uid"));
				CachingNameDB.getCachingNameDB().add(json.getString("uid"), nameDB);
			}
			
			String serialNumber = json.getJSONObject("iot").getString("serial_number");
			String model = json.getJSONObject("iot").getString("model");
			
			String[] query = new String[]{"INSERT INTO IOTProducts(serial_number, model) VALUES (" + serialNumber + ", '" + model + "')"};	
			
			rdbmMysql.writeToDB(nameDB, query);
			
			JSONObject respomseJsonObject = new JSONObject();
			respomseJsonObject.append("result", "product registered");
			
			return respomseJsonObject;
		}
		
		private String getNameDBfromMangeDB(String id) {
			
			String nameDB = null;
			
			ResultSet resultSet = rdbmMysql.readFromDB(manageDB, "SELECT db_name FROM Companies where id=" + id);
			
			 try {
				 if (resultSet.next()) {
				        nameDB = resultSet.getString("db_name");
				    }
				  resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return nameDB;
		}
		
		/*************************************************/
		
		private JSONObject dummyCommand(JSONObject json) {
			
			System.out.println("dummyCommand");
			
			return null;
		}

	/*-------UPDATEIOT COMMAND---------------*/

}
