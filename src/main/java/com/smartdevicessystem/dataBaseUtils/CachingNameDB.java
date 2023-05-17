package dataBaseUtils;

import java.util.concurrent.ConcurrentHashMap;

public class CachingNameDB {

	private ConcurrentHashMap<String, String> mapNameDB = new ConcurrentHashMap<>();
	
	/*-------------SINGLETTON----------------------------------*/
	private CachingNameDB() {}
	
	public static CachingNameDB getCachingNameDB() {
		return InnerInstanse.iNSTANCE;
	}
	
	private static class InnerInstanse{
		private static CachingNameDB iNSTANCE= new CachingNameDB();
	}
	/*--------------------------------------------------------*/	
	
	public void add(String id, String nameDB) {
		mapNameDB.put(id, nameDB);
	}
	
	public String getNameDBByID(String id) {
		return mapNameDB.get(id);
	}
}
