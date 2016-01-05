package pl.com.vsadga.service.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public class CurrencyDataServiceImpl implements CurrencyDataService {
	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDataServiceImpl.class);
	
	private BarDataDao barDataDao;
	
	/**
	 * @param barDataDao the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	@Override
	public List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame) {
		return barDataDao.getLastNbarsData(size, symbol, timeFrame);
	}

}
