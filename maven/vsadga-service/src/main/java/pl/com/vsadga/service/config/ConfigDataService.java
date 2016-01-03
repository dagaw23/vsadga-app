package pl.com.frxdream.service.config;

import pl.com.frxdream.service.BaseServiceException;

public interface ConfigDataService {

	public String getParam(String paramName) throws BaseServiceException;
}
