package pl.com.vsadga.service.writer;

import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.Mt4FileRecord;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.utils.DateConverter;

public class CurrencyDbWriterServiceImpl implements CurrencyDbWriterService {
	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDbWriterServiceImpl.class);

	private BarDataDao barDataDao;

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	@Override
	public void writeOrUpdate(CurrencySymbol symbol, TimeFrame timeFrame, List<BarData> barDataList)
			throws BaseServiceException {
		BarData bar_exist = null;
		int add_cnt = 0;
		int upd_cnt = 0;

		for (BarData barData : barDataList) {
			bar_exist = barDataDao.getBySymbolAndTime(barData.getSymbolId(), timeFrame.getTimeFrameDesc(),
					barData.getBarTime());

			// brak bara w tabeli - wpisujemy nowy tak jak jest:
			if (bar_exist == null) {
				barDataDao.insert(timeFrame.getTimeFrameDesc(), barData);
				add_cnt++;
			} else if (bar_exist.getProcessPhase().intValue() == 0) {
				barDataDao.update(timeFrame.getTimeFrameDesc(), bar_exist.getId(), barData);
				upd_cnt++;
			}
		}

		LOGGER.info("   [" + symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc() + "] Bary dodane ["
				+ add_cnt + "], zaktualizowane [" + upd_cnt + "].");
	}

	private BarData getNewBarData(Mt4FileRecord record, Integer symbolId, int processPhase) throws ParseException {
		BarData bar_data = new BarData();

		bar_data.setBarTime(record.getBarTime().getTime());
		bar_data.setBarHigh(record.getBarHigh());
		bar_data.setBarLow(record.getBarLow());

		bar_data.setBarClose(record.getBarClose());
		bar_data.setBarVolume(record.getBarVolume());
		bar_data.setImaCount(record.getImaCount());

		bar_data.setSymbolId(symbolId);
		bar_data.setProcessPhase(processPhase);

		return bar_data;
	}

	private BarData getPartBarData(Mt4FileRecord record, int processPhase) throws ParseException {
		BarData bar_data = new BarData();

		bar_data.setBarHigh(record.getBarHigh());
		bar_data.setBarLow(record.getBarLow());

		bar_data.setBarClose(record.getBarClose());
		bar_data.setBarVolume(record.getBarVolume());
		bar_data.setImaCount(record.getImaCount());

		bar_data.setProcessPhase(processPhase);

		return bar_data;
	}

	private GregorianCalendar getRecordTime(String record) throws ParseException {
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 6) {
			LOGGER.error("Nieprawidlowy rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}

		return DateConverter.stringToGregorian(rec_tab[0], "yyyy.MM.dd HH:mm:ss");
	}

}
