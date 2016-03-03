package pl.com.vsadga.service.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public class CurrencyDataServiceImpl implements CurrencyDataService {
	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDataServiceImpl.class);

	private BarDataDao barDataDao;

	@Override
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException {
		return barDataDao.getBarDataList(symbolId, timeFrameDesc);
	}

	@Override
	public List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {
		List<BarData> result_list = barDataDao
				.getLastNbarsData(symbol.getId(), timeFrame.getTimeFrameDesc(), size);

		// sortowanie listy wynikowej:
		Collections.sort(result_list, new Comparator<BarData>() {

			@Override
			public int compare(BarData o1, BarData o2) {
				if (o1.getBarTime().getTime() > o2.getBarTime().getTime())
					return 1;
				else if (o1.getBarTime().getTime() < o2.getBarTime().getTime())
					return -1;
				else
					return 0;
			}
		});

		return result_list;
	}

	@Override
	public List<BarData> getLastNbarDataFromTime(int size, CurrencySymbol symbol, TimeFrame timeFrame,
			Date fromTime) throws BaseServiceException {
		try {
			return barDataDao.getLastNbarsDataFromTime(symbol.getId(), timeFrame.getTimeFrameDesc(), size,
					fromTime);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::getLastNbarDataFromTime:: wyjatek " + th.getCause() + "!", th);
		}
	}

	@Override
	public List<BarData> getNotProcessBarDataList(Integer symbolId, String timeFrameDesc)
			throws BaseServiceException {
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
