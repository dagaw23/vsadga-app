package pl.com.vsadga.service.symbol;

import java.sql.Timestamp;

import pl.com.vsadga.dao.CurrencyWritedDao;

public class CurrencyWritedServiceImpl implements CurrencyWritedService {
	
	private CurrencyWritedDao currencyWritedDao;
	
	/**
	 * @param currencyWritedDao the currencyWritedDao to set
	 */
	public void setCurrencyWritedDao(CurrencyWritedDao currencyWritedDao) {
		this.currencyWritedDao = currencyWritedDao;
	}

	@Override
	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId) {
		return currencyWritedDao.getWritedTime(symbolId, timeFrameId);
	}

}
