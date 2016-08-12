package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.ConfigData;

public interface ConfigDataDao {
	
	List<ConfigData> getAll();
	
	ConfigData get(Integer id);

	String getParam(String paramName);
	
	int update(Integer id, String paramValue);
}
