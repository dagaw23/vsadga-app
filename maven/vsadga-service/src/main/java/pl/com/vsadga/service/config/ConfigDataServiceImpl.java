package pl.com.vsadga.service.config;

import java.util.List;

import pl.com.vsadga.dao.ConfigDataDao;
import pl.com.vsadga.data.ConfigData;
import pl.com.vsadga.service.BaseServiceException;

public class ConfigDataServiceImpl implements ConfigDataService {

	private ConfigDataDao configDataDao;

	@Override
	public ConfigData get(Integer id) throws BaseServiceException {
		try {
			return configDataDao.get(id);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::get:: wyjatek Throwable!", th);
		}
	}

	@Override
	public List<ConfigData> getAll() throws BaseServiceException {
		try {
			return configDataDao.getAll();
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::getAll:: wyjatek Throwable!", th);
		}
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

	/**
	 * @param configDataDao
	 *            the configDataDao to set
	 */
	public void setConfigDataDao(ConfigDataDao configDataDao) {
		this.configDataDao = configDataDao;
	}

	@Override
	public int update(Integer id, String paramValue) throws BaseServiceException {
		try {
			return configDataDao.update(id, paramValue);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::update:: wyjatek Throwable!", th);
		}
	}

}
