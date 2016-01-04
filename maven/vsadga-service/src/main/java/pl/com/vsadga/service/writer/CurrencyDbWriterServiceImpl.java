package pl.com.vsadga.service.writer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.utils.DateConverter;

public class CurrencyDbWriterServiceImpl implements CurrencyDbWriterService {
	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDbWriterServiceImpl.class);

	private BarDataDao barDataDao;
	
	private BarData getBarData(String record, Integer symbolListId) throws ParseException {
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 5) {
			LOGGER.error("Nieprawidlowy rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}

		// 2015.12.07 14:05:00
		BarData bar_data = new BarData();
		bar_data.setBarTime(DateConverter.stringToDate(rec_tab[0], "yyyy.MM.dd HH:mm:ss").getTime());
		
		bar_data.setBarLow(new BigDecimal(rec_tab[1]));
		bar_data.setBarHigh(new BigDecimal(rec_tab[2]));
		bar_data.setBarClose(new BigDecimal(rec_tab[3]));
		bar_data.setBarVolume(Integer.valueOf(rec_tab[4]));
		bar_data.setSymbolListId(symbolListId);
		
		return bar_data;
	}

	/**
	 * @param barDataDao the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	@Override
	public void writeAll(List<String> recordList, CurrencySymbol symbolList, TimeFrame timeFrameList) throws BaseServiceException {
		List<BarData> bar_data_list = new ArrayList<BarData>();

		try {
			// przepisz na listę obiektów:
			for (String rec : recordList)
				bar_data_list.add(getBarData(rec, symbolList.getId()));

			barDataDao.batchInsert(bar_data_list, symbolList, timeFrameList);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BaseServiceException("::writeAll:: wyjatek ParseException!", e);
		}
	}

}
