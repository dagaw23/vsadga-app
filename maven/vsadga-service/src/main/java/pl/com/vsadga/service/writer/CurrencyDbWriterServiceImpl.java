package pl.com.vsadga.service.writer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.CurrencyWritedDao;
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

	private CurrencyWritedDao currencyWritedDao;

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	/**
	 * @param currencyWritedDao
	 *            the currencyWritedDao to set
	 */
	public void setCurrencyWritedDao(CurrencyWritedDao currencyWritedDao) {
		this.currencyWritedDao = currencyWritedDao;
	}

	@Override
	public void write(CurrencySymbol symbol, TimeFrame timeFrame, List<String> recordList) throws BaseServiceException {
		int tme_frm = timeFrame.getTimeFrame().intValue();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int act_minute = cal.get(Calendar.MINUTE);
		
		// 5 minut
		if (tme_frm == 5) {
			
			// 0, 5, 10, ..., 55
			if ((act_minute % 5) == 0) {
				// aktualizacja poprzedniego bara jako ostateczny
				// oraz wpisanie aktualnego bara:
				barDataDao.insertOrUpdate(cal)
				
			}
			
		}
		
		// 

	}
	
	private void insertOrUpdateNewBy5(CurrencySymbol symbol, TimeFrame timeFrame, List<String> recordList, GregorianCalendar actCal) {
		
		for (String rec : recordList) {
			// pobierz datę z rekordu:
			
			// TODO
			
			
		}
	}
	
	private GregorianCalendar getRecordGregorian(String record) {
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 6) {
			LOGGER.error("Nieprawidlowy rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}
		
		return DateConverter.stringToGregorian(rec_tab[0], "yyyy.MM.dd HH:mm:ss");
	}

	@Override
	public void writeAll(List<String> recordList, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {
		List<BarData> bar_data_list = new ArrayList<BarData>();
		BarData bar_data = null;
		Timestamp last_write = null;
		long max_date = 0;

		try {
			// jeśli lista jest pusta: zakończenie metody
			if (recordList.isEmpty()) {
				LOGGER.info("   #NOT WRITED# " + symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc() + " ["
						+ bar_data_list.size() + "].");
				return;
			}

			// przepisz na listę obiektów:
			for (String rec : recordList) {
				bar_data = getBarData(rec, symbol.getId());

				if (bar_data.getBarTime().longValue() > max_date)
					max_date = bar_data.getBarTime();

				bar_data_list.add(bar_data);
			}

			// wstawienie wszystkiech barów:
			barDataDao.batchInsert(bar_data_list, symbol, timeFrame);

			// ustawienie czasu ostatniej aktualizacji dla symbolu:
			last_write = currencyWritedDao.getWritedTime(symbol.getId(), timeFrame.getId());
			if (last_write == null) {
				currencyWritedDao.insert(max_date, symbol.getId(), timeFrame.getId());

				LOGGER.info("   #WRITED# " + symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc() + " ["
						+ bar_data_list.size() + "] - LAST ["
						+ DateConverter.dateToString(new Date(max_date), "yyyyMMdd hh:mm:ss") + "].");
			} else {
				currencyWritedDao.update(max_date, symbol.getId(), timeFrame.getId());

				LOGGER.info("   #WRITED# " + symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc() + " ["
						+ bar_data_list.size() + "] - FROM ["
						+ DateConverter.dateToString(new Date(last_write.getTime()), "yyyyMMdd hh:mm:ss") + "] TO ["
						+ DateConverter.dateToString(new Date(max_date), "yyyyMMdd hh:mm:ss") + "].");
			}

		} catch (ParseException e) {
			e.printStackTrace();
			throw new BaseServiceException("::writeAll:: wyjatek ParseException!", e);
		}
	}

	private BarData getBarData(String record, Integer symbolId, int processPhase) throws ParseException {
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 6) {
			LOGGER.error("Nieprawidlowy rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}

		// 2015.12.07 14:05:00
		BarData bar_data = new BarData();
		bar_data.setBarTime(DateConverter.stringToDate(rec_tab[0], "yyyy.MM.dd HH:mm:ss"));

		bar_data.setBarClose(new BigDecimal(rec_tab[3]));
		bar_data.setBarHigh(new BigDecimal(rec_tab[1]));
		bar_data.setBarLow(new BigDecimal(rec_tab[2]));
		bar_data.setBarVolume(Integer.valueOf(rec_tab[4]));
		bar_data.setImaCount(new BigDecimal(rec_tab[5]));
		bar_data.setSymbolId(symbolId);

		bar_data.setIndicatorNr(null);
		bar_data.setIndicatorWeight(null);
		bar_data.setIsConfirm(null);
		bar_data.setProcessPhase(processPhase);

		return bar_data;
	}

}
