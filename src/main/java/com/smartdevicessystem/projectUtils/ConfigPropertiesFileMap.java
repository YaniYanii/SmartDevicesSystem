package projectUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


public class ConfigPropertiesFileMap extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	HashMap<String, String> propertiesMap = new HashMap<>();
	
	
	public ConfigPropertiesFileMap(String pathToConfigFile){
		/*/home/yana/config_app"*/
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(pathToConfigFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		
		for (Iterator<Entry<Object, Object>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<Object, Object> entry = iterator.next();
			propertiesMap.put((String)entry.getKey(), (String)entry.getValue());
		}		
		properties.clear();
	}
	
	@Override
	public String get(Object key) {
		return propertiesMap.get(key);
	}
}