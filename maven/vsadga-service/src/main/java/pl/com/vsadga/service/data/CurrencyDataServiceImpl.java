package pl.com.vsadga.service.data;

import java.util.ArrayList;
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
import pl.com.vsadga.dto.bar.BarSimpleDto;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.utils.DateConverter;

public class CurrencyDataServiceImpl implements CurrencyDataService {
	private class BarDataSortDesc implements Comparator<BarData> {

		@Override
		public int compare(BarData o1, BarData o2) {
			if (o1.getBarTime().getTime() > o2.getBarTime().getTime())
				return 1;
			else if (o1.getBarTime().getTime() < o2.getBarTime().getTime())
				return -1;
			else
				return 0;
		}

	}

	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDataServiceImpl.class);

	private BarDataDao barDataDao;

	@Override
	public void backupArchiveData(String frameDesc, Date barDate, Integer tableNr) throws BaseServiceException {
		List<BarData> data_list = null;

		try {
			// wczytaj listę barów:
			data_list = barDataDao.getAllToMaxDate(frameDesc, barDate);

			if (data_list.size() == 0) {
				LOGGER.info("   [ARCH] Brak barow do archiwizacji.");
				return;
			}

			// wpisz listę barów - do tabeli archiwalnej:
			int[] row_cnt = barDataDao.writeAllToArchive(data_list, frameDesc, tableNr);

			// usuń bary z tabeli oryginalnej:
			int[] del_cnt = barDataDao.deleteAll(frameDesc, data_list);

			LOGGER.info("   [ARCH] Wpisano: [" + row_cnt.length + "], usunieto: [" + del_cnt.length
					+ "] - dla ramy czasowej [" + frameDesc + "] i numeru tabeli [" + tableNr
					+ "] - z data graniczna [" + DateConverter.dateToString(barDate, "yy/MM/dd HH:mm") + "].");
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::backupArchiveData:: wyjatek " + th.getClass().getName() + "!");
		}
	}

	@Override
	public int deleteAll(String frameDesc, List<BarData> dataList) throws BaseServiceException {
		try {
			return barDataDao.deleteAll(frameDesc, dataList).length;
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::deleteAll:: wyjatek Throwable!");
		}
	}

	@Override
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException {
		return barDataDao.getBarDataList(symbolId, timeFrameDesc);
	}

	@Override
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime)
			throws BaseServiceException {
		return barDataDao.getBarDataList(symbolId, timeFrameDesc, startTime);
	}

	@Override
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime, Date endTime)
			throws BaseServiceException {
		return barDataDao.getBarDataList(symbolId, timeFrameDesc, startTime, endTime);
	}

	@Override
	public List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {
		List<BarData> result_list = null;

		try {
			result_list = barDataDao.getLastNbarsData(symbol.getId(), timeFrame.getTimeFrameDesc(), size);

			// sortowanie listy wynikowej:
			Collections.sort(result_list, new BarDataSortDesc());

			return result_list;
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::getLastNbarData:: wyjatek " + th.getCause() + "!", th);
		}
	}

	@Override
	public List<BarSimpleDto> getLastNbarData(int size, Integer symbolId, String timeFrameDesc)
			throws BaseServiceException {
		List<BarData> data_list = barDataDao.getLastNbarsData(symbolId, timeFrameDesc, size);
		List<BarSimpleDto> dto_list = new ArrayList<BarSimpleDto>();

		for (BarData bar_data : data_list)
			dto_list.add(new BarSimpleDto(DateConverter.dateToString(bar_data.getBarTime(), "yy/MM/dd HH:mm"),
					bar_data.getBarHigh(), bar_data.getBarLow(), bar_data.getBarClose(), bar_data.getBarVolume(),
					bar_data.getProcessPhase()));

		return dto_list;
	}

	@Override
	public List<BarData> getLastNbarDataToDate(int size, CurrencySymbol symbol, TimeFrame timeFrame, Date fromTime)
			throws BaseServiceException {
		List<BarData> result_list = null;

		try {
			result_list = barDataDao.getLastNbarsDataToDate(symbol.getId(), timeFrame.getTimeFrameDesc(), size,
					fromTime);

			// sortowanie listy wynikowej:
			Collections.sort(result_list, new BarDataSortDesc());

			return result_list;
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::getLastNbarDataToDate:: wyjatek " + th.getCause() + "!", th);
		}
	}

	@Override
	public List<BarData> getNotProcessBarDataList(Integer symbolId, String timeFrameDesc)
			throws BaseServiceException {
		return barDataDao.getNotProcessBarDataList(symbolId, timeFrameDesc);
	}

	@Override
	public int insert(String frameDesc, BarData data) throws BaseServiceException {
		try {
			return barDataDao.insert(frameDesc, data);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseServiceException("::insert:: wyjatek Throwable!", th);
		}
	}

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	@Override
	public int update(String frameDesc, BarData barData) throws BaseServiceException {
		return barDataDao.update(frameDesc, barData);
	}

	@Override
	public int update(String frameDesc, BarData barData, Integer processPhase) throws BaseServiceException {
		return barDataDao.update(frameDesc, barData, processPhase);
	}

}
