package dataBaseUtils;

public interface IGenericDB {
	void createDB(String nameDB, Object infoforCreateDB);
	void deleteDB(String nameDB);
	void writeToDB(String nameDB, Object infoWrite);	
	<T> T readFromDB(String ftomDB ,Object infoRead);
	void close();
}
