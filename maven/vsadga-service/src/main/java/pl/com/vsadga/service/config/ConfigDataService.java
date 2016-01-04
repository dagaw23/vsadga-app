package pl.com.vsadga.service.config;

import pl.com.vsadga.service.BaseServiceException;

public interface ConfigDataService {

	public String getParam(String paramName) throws BaseServiceException;
}
