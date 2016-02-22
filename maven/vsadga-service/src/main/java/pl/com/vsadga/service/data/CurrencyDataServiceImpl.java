package pl.com.vsadga.service.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.utils.DateConverter;

public class CurrencyDataServiceImpl implements CurrencyDataService {
	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDataServiceImpl.class);

	private BarDataDao barDataDao;

	@Override
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException {
		// przetwarzamy maksymalnie 5 dni wstecz:
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, -120);

		LOGGER.info("   [GET] " + symbolId + "," + timeFrameDesc + ":"
				+ DateConverter.dateToString(cal.getTime(), "yyyy/MM/dd HH:mm") + ".");

		return barDataDao.getBarDataList(symbolId, timeFrameDesc, cal.getTime());
	}

	@Override
	public List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {
		return barDataDao.getLastNbarsData(symbol.getId(), timeFrame.getTimeFrameDesc(), size);
	}

	@Override
	public List<BarData> getNotProcessBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException {
		return barDataDao.getNotProcessBarDataList(symbolId, timeFrameDesc);
	}

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

}
