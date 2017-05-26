package pl.com.vsadga.service.config;

import java.util.List;

import pl.com.vsadga.data.ConfigData;
import pl.com.vsadga.service.BaseServiceException;

public interface ConfigDataService {

	List<ConfigData> getAll() throws BaseServiceException;
	
	ConfigData get(Integer id) throws BaseServiceException;

	String getParam(String paramName) throws BaseServiceException;
	
	int getParamWithDefaultValue(String paramName, String defaultValue);
	
	int update(Integer id, String paramValue) throws BaseServiceException;
	
	int update(String paramName, String paramValue) throws BaseServiceException;

}
