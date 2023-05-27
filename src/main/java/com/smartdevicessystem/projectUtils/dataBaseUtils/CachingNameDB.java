package com.smartdevicessystem.projectUtils.dataBaseUtils;

import java.util.concurrent.ConcurrentHashMap;

public class CachingNameDB {

	private ConcurrentHashMap<String, String> mapNameDB = new ConcurrentHashMap<>();
	
	/*-------------SINGLETON----------------------------------*/
	private CachingNameDB() {}
	
	public static CachingNameDB getCachingNameDB() {
		return InnerInstance.INSTANCE;
	}
	
	private static class InnerInstance {
		private static final CachingNameDB INSTANCE= new CachingNameDB();
	}
	/*--------------------------------------------------------*/	
	
	public void add(String id, String nameDB) {
		mapNameDB.put(id, nameDB);
	}
	
	public String getNameDBByID(String id) {
		return mapNameDB.get(id);
	}
}
