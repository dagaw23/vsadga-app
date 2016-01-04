package pl.com.vsadga.service.config;

import pl.com.vsadga.dao.ConfigDataDao;
import pl.com.vsadga.service.BaseServiceException;

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
