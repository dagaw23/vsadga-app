package pl.com.vsadga.service.writer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
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
	public void write(CurrencySymbol symbol, TimeFrame timeFrame, List<String> recordList, int hourShift)
			throws BaseServiceException {
		int tme_frm = timeFrame.getTimeFrame().intValue();

		GregorianCalendar sys_date = getSystemDate(hourShift);
		int act_minute = sys_date.get(Calendar.MINUTE);

		try {
			// *** 5 minut ***
			if (tme_frm == 5) {

				if ((act_minute % 5) == 0) {
					// 0, 5, 10, ..., 55
					//insertOrUpdateBy5(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, sys_date);
					insertOrUpdateBarData(symbol.getId(), timeFrame, recordList, sys_date);
				} else {
					// aktualizacja tylko ostatniego:
					//updateBy5(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, (act_minute % 5), sys_date);
					updateBarData(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, (act_minute % 5), sys_date);
				}
			}

			// *** 15 minut ***
			if (tme_frm == 15) {

				if ((act_minute % 15) == 0) {
					// 0, 15, 30, 45
					insertOrUpdateBarData(symbol.getId(), timeFrame, recordList, sys_date);
				} else {
					updateBarData(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, (act_minute % 15), sys_date);
				}
			}
			
			// *** 60 minut ***
			if (tme_frm == 60) {

				// pełna godzina:
				if (act_minute == 0)
					insertOrUpdateBarData(symbol.getId(), timeFrame, recordList, sys_date);
				else
					updateBarData(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, act_minute, sys_date);
			}
			
			// *** 240 minut ***
			if (tme_frm == 240) {
				
				// pełna godzina:
				if (act_minute == 0)
					insertOrUpdateBarDataBy4h(symbol.getId(), timeFrame, recordList, sys_date);
				else
					updateBarDataBy4h(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, act_minute, sys_date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BaseServiceException("::write:: wyjatek ParseException!", e);
		}
	}

	@Override
	public void writeAll(List<String> recordList, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {
		List<BarData> bar_data_list = new ArrayList<BarData>();
		BarData bar_data = null;

		try {
			// jeśli lista jest pusta: zakończenie metody
			if (recordList.isEmpty()) {
				LOGGER.info("   #NOT WRITED# " + symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc()
						+ " [" + bar_data_list.size() + "].");
				return;
			}

			// przepisz na listę obiektów:
			for (String rec : recordList) {
				bar_data = getBarData(rec, symbol.getId(), 1);
				bar_data_list.add(bar_data);
			}

			// wstawienie wszystkiech barów:
			barDataDao.batchInsert(timeFrame.getTimeFrameDesc(), bar_data_list);

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

	private BarData getNewBarData(String record, Integer symbolId, int processPhase) throws ParseException {
		BarData bar_data = new BarData();
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 6) {
			LOGGER.error("Nieprawidlowy rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}

		bar_data.setBarTime(DateConverter.stringToDate(rec_tab[0], "yyyy.MM.dd HH:mm:ss"));
		bar_data.setBarHigh(new BigDecimal(rec_tab[1]));
		bar_data.setBarLow(new BigDecimal(rec_tab[2]));

		bar_data.setBarClose(new BigDecimal(rec_tab[3]));
		bar_data.setBarVolume(Integer.valueOf(rec_tab[4]));
		bar_data.setImaCount(new BigDecimal(rec_tab[5]));

		bar_data.setSymbolId(symbolId);
		bar_data.setIndicatorNr(null);
		bar_data.setIndicatorWeight(null);
		bar_data.setIsConfirm(null);
		bar_data.setProcessPhase(processPhase);

		return bar_data;
	}

	private BarData getPartBarData(String record, int processPhase) throws ParseException {
		BarData bar_data = new BarData();
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 6) {
			LOGGER.error("Nieprawidlowy rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}

		bar_data.setBarHigh(new BigDecimal(rec_tab[1]));
		bar_data.setBarLow(new BigDecimal(rec_tab[2]));
		bar_data.setBarClose(new BigDecimal(rec_tab[3]));

		bar_data.setBarVolume(Integer.valueOf(rec_tab[4]));
		bar_data.setImaCount(new BigDecimal(rec_tab[5]));
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

	/**
	 * Zwraca aktualną datę systemową - ale z wyzerowanymi sekundami i milisekundami.
	 * 
	 * @return
	 */
	private GregorianCalendar getSystemDate(int hourShift) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());

		// przesunięcie godziny:
		cal.add(Calendar.HOUR_OF_DAY, hourShift);

		// wyzerowanie sekund i milisekund:
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	/**
	 * Wstawia nowy bar do tabeli. Przy wstawianym rekordzie - jest ustawiany status
	 * PROCESS_PHASE=1.
	 * 
	 * @param record
	 * @param symbolId
	 * @param frameDesc
	 * @param recTime
	 * @throws ParseException
	 */
	private void insertNewBarData(String record, Integer symbolId, String frameDesc, GregorianCalendar recTime)
			throws ParseException {
		int upd_cnt = 0;
		BarData exist_rec = barDataDao.getBySymbolAndTime(symbolId, frameDesc, recTime.getTime());

		if (exist_rec == null) {
			barDataDao.insert(frameDesc, getNewBarData(record, symbolId, 0));
			LOGGER.info("   [ADD] Nowy rekord [" + upd_cnt + "].");
		} else {
			LOGGER.info("   [ERROR] Rekord w tabeli nie powinien jeszcze istniec [" + exist_rec
					+ "] dla rekordu pliku plaskiego [" + record + "].");
		}
	}

	private void insertOrUpdateBy5(Integer symbolId, String frameDesc, List<String> recordList,
			GregorianCalendar actualDate) throws ParseException {
		GregorianCalendar rec_cal = null;

		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -5);

		for (String rec : recordList) {
			// czas z rekordu pliku płaskiego:
			rec_cal = getRecordTime(rec);

			// aktualny bar - wstawienie, jeśli nie ma w tabeli:
			if (rec_cal.compareTo(actualDate) == 0) {
				LOGGER.info("   [DATE] aktualny wg REC ["
						+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "] i SYS ["
						+ DateConverter.dateToString(actualDate.getTime(), "HH:mm:ss,SSS") + "].");

				insertNewBarData(rec, symbolId, frameDesc, rec_cal);
				continue;
			}

			// poprzedni bar - końcowa aktualizacja części wartości:
			if (rec_cal.compareTo(prev_date) == 0) {
				LOGGER.info("   [DATE] prev wg REC ["
						+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "] i PREV ["
						+ DateConverter.dateToString(prev_date.getTime(), "HH:mm:ss,SSS") + "].");

				insertOrUpdateLastBarData(rec, symbolId, frameDesc, rec_cal, 1);
				continue;
			}

			// poprzednie bary - tylko jeśli nie ma ich w tabeli lub jeśli mają staus 0:
			LOGGER.info("   [DATE] wg REC [" + DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS")
					+ "] i SYS [" + DateConverter.dateToString(actualDate.getTime(), "HH:mm:ss,SSS") + "].");
			insertOrUpdatePrevBarData(rec, symbolId, frameDesc, rec_cal);
		}
	}

	private void insertOrUpdateBarData(Integer symbolId, TimeFrame tmeFrm, List<String> recordList,
			GregorianCalendar actualDate) throws ParseException {
		GregorianCalendar rec_cal = null;

		// wyliczenie daty dla poprzedniego bara:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -(tmeFrm.getTimeFrame()));

		for (String rec : recordList) {
			// czas z rekordu pliku płaskiego:
			rec_cal = getRecordTime(rec);

			// aktualny bar - wstawienie, jeśli nie ma w tabeli:
			if (rec_cal.compareTo(actualDate) == 0) {
				//LOGGER.info("   [BAR] Nowy bar " + tmeFrm.getTimeFrame() + "M - data z REC ["
				//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data SYS ["
				//		+ DateConverter.dateToString(actualDate.getTime(), "HH:mm:ss,SSS") + "].");

				insertNewBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal);
				continue;
			}

			// poprzedni bar - końcowa aktualizacja części wartości:
			if (rec_cal.compareTo(prev_date) == 0) {
				//LOGGER.info("   [BAR] Poprzedni bar " + tmeFrm.getTimeFrame() + "M - data z REC ["
				//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data PREV ["
				//		+ DateConverter.dateToString(prev_date.getTime(), "HH:mm:ss,SSS") + "].");

				insertOrUpdateLastBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal, 1);
				continue;
			}

			// poprzednie bary - tylko jeśli nie ma ich w tabeli lub jeśli mają staus 0:
			//LOGGER.info("   [BAR] Weryfikacja bara " + tmeFrm.getTimeFrame() + "M - data z REC ["
			//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data SYS ["
			//		+ DateConverter.dateToString(actualDate.getTime(), "HH:mm:ss,SSS") + "].");
			insertOrUpdatePrevBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal);
		}
	}
	
	private void insertOrUpdateBarDataBy4h(Integer symbolId, TimeFrame tmeFrm, List<String> recordList,
			GregorianCalendar actualDate) throws ParseException {
		GregorianCalendar rec_cal = null;

		// wyliczenie daty dla poprzedniego bara:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -(tmeFrm.getTimeFrame()));

		for (String rec : recordList) {
			// czas z rekordu pliku płaskiego:
			rec_cal = getRecordTime(rec);

			// aktualny bar - wstawienie, jeśli nie ma w tabeli:
			if (rec_cal.compareTo(actualDate) < 0 && rec_cal.compareTo(prev_date) > 0) {
				//LOGGER.info("   [BAR] Nowy bar " + tmeFrm.getTimeFrame() + "M - data z REC ["
				//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data SYS ["
				//		+ DateConverter.dateToString(actualDate.getTime(), "HH:mm:ss,SSS") + "].");

				insertOrUpdateLastBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal, 0);
				//insertNewBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal);
				continue;
			}

			// poprzedni bar - końcowa aktualizacja części wartości:
			if (rec_cal.compareTo(prev_date) == 0) {
				//LOGGER.info("   [BAR] Poprzedni bar " + tmeFrm.getTimeFrame() + "M - data z REC ["
				//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data PREV ["
				//		+ DateConverter.dateToString(prev_date.getTime(), "HH:mm:ss,SSS") + "].");

				insertOrUpdateLastBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal, 1);
				continue;
			}

			// poprzednie bary - tylko jeśli nie ma ich w tabeli lub jeśli mają staus 0:
			//LOGGER.info("   [BAR] Weryfikacja bara " + tmeFrm.getTimeFrame() + "M - data z REC ["
			//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data SYS ["
			//		+ DateConverter.dateToString(actualDate.getTime(), "HH:mm:ss,SSS") + "].");
			insertOrUpdatePrevBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), rec_cal);
		}
	}

	/**
	 * Wstawia nowy bar do tabeli (jeśli jeszcze tam nie istnieje) lub aktualizuje istniejący już
	 * bar w tabeli. Przy wstawianym oraz aktualizowanym rekordzie - jest ustawiany status
	 * PROCESS_PHASE=1.
	 * 
	 * @param record
	 * @param symbolId
	 * @param frameDesc
	 * @param recTime
	 * @throws ParseException
	 */
	private void insertOrUpdateLastBarData(String record, Integer symbolId, String frameDesc,
			GregorianCalendar recTime, int processPhase) throws ParseException {
		int upd_cnt = 0;
		BarData exist_rec = barDataDao.getBySymbolAndTime(symbolId, frameDesc, recTime.getTime());

		if (exist_rec == null) {
			// wstawienie brakującego bara - ze statusem końcowym:
			upd_cnt = barDataDao.insert(frameDesc, getNewBarData(record, symbolId, processPhase));

			LOGGER.info("   [ADD] NOWY rekord [" + upd_cnt + "]");
		} else {
			// aktualizacja - ze statusem końcowym
			upd_cnt = barDataDao.update(frameDesc, exist_rec.getId(), getPartBarData(record, processPhase));

			LOGGER.info("   [UPD] Zaktualizowany rekord [" + upd_cnt + "] o ID=[" + exist_rec.getId() + "].");
		}
	}

	/**
	 * Wstawia nowy bar do tabeli (jeśli jeszcze tam nie istnieje) lub aktualizuje istniejący już
	 * bar w tabeli (jeśli jego status równy 0 w kolumnie PROCESS_PHASE).<br/>
	 * Przy wstawianym oraz aktualizowanym rekordzie - jest ustawiany status PROCESS_PHASE=1.
	 * 
	 * @param record
	 * @param symbolId
	 * @param frameDesc
	 * @param recTime
	 * @throws ParseException
	 */
	private void insertOrUpdatePrevBarData(String record, Integer symbolId, String frameDesc,
			GregorianCalendar recTime) throws ParseException {
		int upd_cnt = 0;
		BarData exist_rec = barDataDao.getBySymbolAndTime(symbolId, frameDesc, recTime.getTime());

		if (exist_rec == null) {
			// wstawienie brakującego bara - ze statusem końcowym:
			upd_cnt = barDataDao.insert(frameDesc, getNewBarData(record, symbolId, 1));

			LOGGER.info("   [ADD] Rekord NOWY zalegly [" + upd_cnt + "]");
		} else if (exist_rec.getProcessPhase().intValue() == 0) {
			// aktualizacja - ze statusem końcowym
			upd_cnt = barDataDao.update(frameDesc, exist_rec.getId(), getPartBarData(record, 1));

			LOGGER.info("   [UPD] Rekord UPDATE zalegly [" + upd_cnt + "] z PROCESS_PHASE ["
					+ exist_rec.getProcessPhase() + "]");
		}
	}

	private void updateBarData(Integer symbolId, String frameDesc, List<String> recordList, int minute,
			GregorianCalendar actualDate) throws ParseException {
		GregorianCalendar rec_cal = null;

		// wyliczenie czasu dla bara jeszcze nie skończonego:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -minute);

		for (String rec : recordList) {
			// czas z rekordu pliku płaskiego:
			rec_cal = getRecordTime(rec);

			// bar do aktualizacji:
			if (rec_cal.compareTo(prev_date) == 0) {
				//LOGGER.info("   [BAR] Aktualizacja bara - minutka [" + minute + "], data z REC ["
				//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data PREV ["
				//		+ DateConverter.dateToString(prev_date.getTime(), "HH:mm:ss,SSS") + "].");

				insertOrUpdateLastBarData(rec, symbolId, frameDesc, rec_cal, 0);
				continue;
			}

			// pozostałe bary - tylko wstawienie, jeśli nie ma ich w tabeli lub jeśli jeszcze ich
			// status równy 0:
			//LOGGER.info("   [BAR] Analiza poprzedniego bara - minutka [" + minute + "], data z REC ["
			//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data PREV ["
			//		+ DateConverter.dateToString(prev_date.getTime(), "HH:mm:ss,SSS") + "].");
			insertOrUpdatePrevBarData(rec, symbolId, frameDesc, rec_cal);
		}
	}
	
	private void updateBarDataBy4h(Integer symbolId, String frameDesc, List<String> recordList, int minute,
			GregorianCalendar actualDate) throws ParseException {
		GregorianCalendar rec_cal = null;

		// wyliczenie czasu dla bara jeszcze nie skończonego:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -minute);
		
		GregorianCalendar prev_date_2 = new GregorianCalendar();
		prev_date_2.setTime(actualDate.getTime());
		prev_date_2.add(Calendar.MINUTE, -minute);
		prev_date_2.add(Calendar.HOUR_OF_DAY, -4);

		for (String rec : recordList) {
			// czas z rekordu pliku płaskiego:
			rec_cal = getRecordTime(rec);

			// bar do aktualizacji:
			if (rec_cal.compareTo(prev_date) < 0 && rec_cal.compareTo(prev_date_2) > 0) {
				insertOrUpdateLastBarData(rec, symbolId, frameDesc, rec_cal, 0);
				continue;
			}
			
			// bar do aktualizacji:
			if (rec_cal.compareTo(prev_date) == 0) {
				insertOrUpdateLastBarData(rec, symbolId, frameDesc, rec_cal, 0);
				continue;
			}
			
			// 

			// pozostałe bary - tylko wstawienie, jeśli nie ma ich w tabeli lub jeśli jeszcze ich
			// status równy 0:
			//LOGGER.info("   [BAR] Analiza poprzedniego bara - minutka [" + minute + "], data z REC ["
			//		+ DateConverter.dateToString(rec_cal.getTime(), "HH:mm:ss,SSS") + "], data PREV ["
			//		+ DateConverter.dateToString(prev_date.getTime(), "HH:mm:ss,SSS") + "].");
			insertOrUpdatePrevBarData(rec, symbolId, frameDesc, rec_cal);
		}
	}

}
