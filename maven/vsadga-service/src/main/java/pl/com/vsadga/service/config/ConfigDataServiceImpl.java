package pl.com.frxdream.service.config;

import pl.com.frxdream.dao.ConfigDataDao;
import pl.com.frxdream.service.BaseServiceException;

public class ConfigDataServiceImpl implements ConfigDataService {

	private ConfigDataDao configDataDao;

	/**
	 * @param configDataDao
	 *            the configDataDao to set
	 */
	public void setConfigDataDao(ConfigDataDao configDataDao) {
		this.configDataDao = configDataDao;
	}

	@Override
	public String getParam(String paramName) throws BaseServiceException {
		try {
			return configDataDao.getParam(paramName);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::getParam:: wyjatek Throwable!", th);
		}
	}

}
