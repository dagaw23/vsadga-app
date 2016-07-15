package pl.com.vsadga.service.writer;

import java.text.ParseException;
import java.util.Calendar;
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
	public void write(CurrencySymbol symbol, TimeFrame timeFrame, List<Mt4FileRecord> recordList, GregorianCalendar sysTime) throws BaseServiceException {
		int tme_frm = timeFrame.getTimeFrame().intValue();

		// aktualna minuta z czasu systomego:
		int act_minute = sysTime.get(Calendar.MINUTE);

		try {
			// *** 5 minut ***
			if (tme_frm == 5) {

				if ((act_minute % 5) == 0) {
					// 0, 5, 10, ..., 55
					insertOrUpdateBarData(symbol.getId(), timeFrame, recordList, sysTime);
				} else {
					// aktualizacja tylko ostatniego:
					updateBarData(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, (act_minute % 5), sysTime);
				}
			}

			// *** 15 minut ***
			if (tme_frm == 15) {

				if ((act_minute % 15) == 0) {
					// 0, 15, 30, 45
					insertOrUpdateBarData(symbol.getId(), timeFrame, recordList, sysTime);
				} else {
					updateBarData(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, (act_minute % 15), sysTime);
				}
			}

			// *** 60 minut ***
			if (tme_frm == 60) {

				// pełna godzina:
				if (act_minute == 0)
					insertOrUpdateBarData(symbol.getId(), timeFrame, recordList, sysTime);
				else
					updateBarData(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, act_minute, sysTime);
			}

			// *** 240 minut ***
			if (tme_frm == 240) {

				// pełna godzina:
				if (act_minute == 0)
					insertOrUpdateBarDataBy4h(symbol.getId(), timeFrame, recordList, sysTime);
				else
					updateBarDataBy4h(symbol.getId(), timeFrame.getTimeFrameDesc(), recordList, act_minute,	sysTime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BaseServiceException("::write:: wyjatek ParseException!", e);
		}
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
	private void insertNewBarData(Mt4FileRecord record, Integer symbolId, String frameDesc) throws ParseException {
		int upd_cnt = 0;
		BarData exist_rec = barDataDao.getBySymbolAndTime(symbolId, frameDesc, record.getBarTime().getTime());

		if (exist_rec == null) {
			barDataDao.insert(frameDesc, getNewBarData(record, symbolId, 0));
			LOGGER.info("   [ADD] Nowy rekord [" + upd_cnt + "].");
		} else {
			LOGGER.info("   [ERROR] Rekord w tabeli nie powinien jeszcze istniec [" + exist_rec
					+ "] dla rekordu pliku plaskiego [" + record + "].");
		}
	}

	private void insertOrUpdateBarData(Integer symbolId, TimeFrame tmeFrm, List<Mt4FileRecord> recordList, GregorianCalendar actualDate) throws ParseException {
		// wyliczenie daty dla poprzedniego bara:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -(tmeFrm.getTimeFrame()));

		for (Mt4FileRecord rec : recordList) {
			// aktualny bar - wstawienie, jeśli nie ma w tabeli:
			if (rec.getBarTime().compareTo(actualDate) == 0) {
				insertNewBarData(rec, symbolId, tmeFrm.getTimeFrameDesc());
				continue;
			}

			// poprzedni bar - końcowa aktualizacja części wartości:
			if (rec.getBarTime().compareTo(prev_date) == 0) {
				insertOrUpdateLastBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), 1);
				continue;
			}

			// poprzednie bary - tylko jeśli nie ma ich w tabeli lub jeśli mają staus 0:
			insertOrUpdatePrevBarData(rec, symbolId, tmeFrm.getTimeFrameDesc());
		}
	}
	
	private void insertOrUpdateBarDataBy4h(Integer symbolId, TimeFrame tmeFrm, List<Mt4FileRecord> recordList, GregorianCalendar actualDate) throws ParseException {
		// wyliczenie daty dla poprzedniego bara:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -(tmeFrm.getTimeFrame()));

		for (Mt4FileRecord rec : recordList) {
			// aktualny bar - wstawienie, jeśli nie ma w tabeli:
			if (rec.getBarTime().compareTo(actualDate) < 0 && rec.getBarTime().compareTo(prev_date) > 0) {
				insertOrUpdateLastBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), 0);
				continue;
			}

			// poprzedni bar - końcowa aktualizacja części wartości:
			if (rec.getBarTime().compareTo(prev_date) == 0) {
				insertOrUpdateLastBarData(rec, symbolId, tmeFrm.getTimeFrameDesc(), 1);
				continue;
			}

			// poprzednie bary - tylko jeśli nie ma ich w tabeli lub jeśli mają staus 0:
			insertOrUpdatePrevBarData(rec, symbolId, tmeFrm.getTimeFrameDesc());
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
	private void insertOrUpdateLastBarData(Mt4FileRecord record, Integer symbolId, String frameDesc, int processPhase) throws ParseException {
		int upd_cnt = 0;
		BarData exist_rec = barDataDao.getBySymbolAndTime(symbolId, frameDesc, record.getBarTime().getTime());

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
	private void insertOrUpdatePrevBarData(Mt4FileRecord record, Integer symbolId, String frameDesc)
			throws ParseException {
		int upd_cnt = 0;
		BarData exist_rec = barDataDao.getBySymbolAndTime(symbolId, frameDesc, record.getBarTime().getTime());

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

	private void updateBarData(Integer symbolId, String frameDesc, List<Mt4FileRecord> recordList, int minute, GregorianCalendar actualDate) throws ParseException {
		// wyliczenie czasu dla bara jeszcze nie skończonego:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -minute);

		for (Mt4FileRecord rec : recordList) {
			// bar do aktualizacji:
			if (rec.getBarTime().compareTo(prev_date) == 0) {
				insertOrUpdateLastBarData(rec, symbolId, frameDesc, 0);
				continue;
			}

			// pozostałe bary - tylko wstawienie, jeśli nie ma ich w tabeli lub jeśli jeszcze ich
			// status równy 0:
			insertOrUpdatePrevBarData(rec, symbolId, frameDesc);
		}
	}

	private void updateBarDataBy4h(Integer symbolId, String frameDesc, List<Mt4FileRecord> recordList, int minute, GregorianCalendar actualDate) throws ParseException {
		// wyliczenie czasu dla bara jeszcze nie skończonego:
		GregorianCalendar prev_date = new GregorianCalendar();
		prev_date.setTime(actualDate.getTime());
		prev_date.add(Calendar.MINUTE, -minute);

		GregorianCalendar prev_date_2 = new GregorianCalendar();
		prev_date_2.setTime(actualDate.getTime());
		prev_date_2.add(Calendar.MINUTE, -minute);
		prev_date_2.add(Calendar.HOUR_OF_DAY, -4);

		for (Mt4FileRecord rec : recordList) {
			// bar do aktualizacji:
			if (rec.getBarTime().compareTo(prev_date) < 0 && rec.getBarTime().compareTo(prev_date_2) > 0) {
				insertOrUpdateLastBarData(rec, symbolId, frameDesc, 0);
				continue;
			}

			// bar do aktualizacji:
			if (rec.getBarTime().compareTo(prev_date) == 0) {
				insertOrUpdateLastBarData(rec, symbolId, frameDesc, 0);
				continue;
			}

			//

			// pozostałe bary - tylko wstawienie, jeśli nie ma ich w tabeli lub jeśli jeszcze ich
			// status równy 0:
			insertOrUpdatePrevBarData(rec, symbolId, frameDesc);
		}
	}

}
