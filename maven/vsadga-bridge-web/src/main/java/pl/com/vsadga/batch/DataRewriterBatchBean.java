package pl.com.vsadga.batch;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.Mt4FileRecord;
import pl.com.vsadga.dto.VolumeData;
import pl.com.vsadga.io.BarDataFileReader;
import pl.com.vsadga.io.ReaderException;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.io.HttpReader;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.service.writer.CurrencyDbWriterService;
import pl.com.vsadga.utils.DateConverter;

@Component
public class DataRewriterBatchBean extends BaseBatch {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataRewriterBatchBean.class);

	@Autowired
	private BarDataFileReader barDataFileReader;

	@Autowired
	private CurrencyDbWriterService currencyDbWriterService;

	private String httpAccessKey;

	@Autowired
	private HttpReader httpReader;

	private boolean isHttpProxy;

	private String mt4LocalPath;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Scheduled(cron = "5 * * * * SUN-FRI")
	public void cronJob() {
		// data formatter:
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		int file_proc_count = 0;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// wczytanie danych konfiguracyjnych:
			if (!readConfigData()) {
				LOGGER.error("   [CONFIG] Brak jednego z wymaganych parametrow konfiguracyjnych.");
				return;
			}

			// przetworzenie plików płaskich z danymi:
			long start_time = System.currentTimeMillis();
			file_proc_count = processDataFiles();

			long stop_time = System.currentTimeMillis();
			LOGGER.info("   [PROC] Przetworzone zostaly [" + file_proc_count + "] pliki z danymi w czasie ["
					+ ((stop_time - start_time) / 1000) + "] s. <" + dateFormat.format(stop_time) + ">");

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		} catch (BatchProcessException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BatchProcessException!");
		}

	}

	private int getHourShift() throws BaseServiceException {
		String shift_value = null;
		boolean is_negative = false;
		int shift_int = 0;

		String hr_shift = getStringParamValue("HOUR_SHIFT");
		if (hr_shift == null) {
			LOGGER.info("   [SHIFT] Brak parametru konfiguracyjnego HOUR_SHIFT [" + hr_shift + "].");
			return 0;
		}

		if (hr_shift.startsWith("-")) {
			shift_value = hr_shift.substring(1);
			is_negative = true;
		} else
			shift_value = hr_shift;

		if (!StringUtils.isNumeric(shift_value)) {
			LOGGER.info("   [SHIFT] Parametr konfiguracyjny HOUR_SHIFT [" + hr_shift + "] nie jest numeryczny ["
					+ shift_value + "].");
			return 0;
		}

		shift_int = Integer.valueOf(shift_value);

		if (is_negative)
			shift_int *= -1;

		LOGGER.info("   [SHIFT] HOUR_SHIFT [" + hr_shift + "], numeric [" + shift_value + "], result ["
				+ shift_int + "].");
		return shift_int;
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

	private boolean isProcessBatch() throws BaseServiceException {
		Integer int_value = getIntParamValue("IS_BATCH_REWRITE");

		if (int_value == null)
			return false;

		if (int_value.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch przepisywania barow [" + int_value + "].");
			return false;
		}
	}

	/**
	 * Dla wczytanych symboli oraz ramek czasowych - wczytuje zawartość pliku płaskiego, który
	 * wpisuje do tabel bazy danych.
	 * @throws BatchProcessException 
	 */
	private int processDataFiles() throws BatchProcessException {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;
		int file_count = 0;

		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		// czy są zdefiniowane aktywne symbole:
		if (symbol_list.isEmpty()) {
			LOGGER.info("   ### Brak aktywnych symboli [" + symbol_list.size() + "].");
			return 0;
		}

		// pobierz listę aktywnych timeframe:
		tmefrm_list = timeFrameService.getAllActive();
		// czy są zdefiniowane timeFrames:
		if (tmefrm_list.isEmpty()) {
			LOGGER.info("   ### Brak aktywnych timeFrame [" + tmefrm_list.size() + "].");
			return 0;
		}

		for (CurrencySymbol sym : symbol_list) {
			for (TimeFrame frm : tmefrm_list) {
				LOGGER.info("   [WRITE] Symbol [" + sym.getSymbolName() + "], ramka [" + frm.getTimeFrameDesc()
						+ "].");

				rewriteFileContent2db(sym, frm);
				file_count++;
			}
		}

		return file_count;
	}

	/**
	 * Wczytuje dane konfiguracyjne - z tabeli CONFIG_DATA.
	 * 
	 * @return true: wszystkie dane konfiguracyjne zostały wczytane poprawnie, false: brak jednego z
	 *         parametrów konfiguracyjnych
	 * @throws BaseServiceException
	 *             błąd w trakcie wcztywania konfiguracji z tabeli
	 */
	private boolean readConfigData() throws BaseServiceException {
		String value = null;

		// pobierz ścieżkę dostępu do plików z danymi:
		value = getStringParamValue("MT4_PATH");
		if (value == null)
			return false;
		else
			this.mt4LocalPath = value.trim();

		// pobierz kod dostępu do serwisu HTTP:
		value = getStringParamValue("ACCESS_KEY");
		if (value == null)
			return false;
		else
			this.httpAccessKey = value.trim();

		// pobierz informację o proxy:
		Integer int_value = getIntParamValue("IS_HTTP_PROXY");
		if (value == null)
			return false;
		else {
			if (int_value.intValue() == 1)
				this.isHttpProxy = true;
			else
				this.isHttpProxy = false;

		}

		return true;
	}

	private void rewriteFileContent2db(CurrencySymbol symbol, TimeFrame timeFrame) throws BatchProcessException {
		List<Mt4FileRecord> rec_list = null;
		List<Mt4FileRecord> rec_volume_list = null;

		try {
		// pobierz przesunięcie godzin w dacie:
		int hr_shift = getHourShift();
		GregorianCalendar sys_date = getSystemDate(hr_shift);

			// pobierz całą zawartość pliku:
			rec_list = barDataFileReader.readAll(mt4LocalPath, symbol.getSymbolName(),
					timeFrame.getTimeFrameDesc());

			// jeśli pusta lista rekordów: przejdź do następnego
			if (rec_list.isEmpty()) {
				LOGGER.error("   [EMPTY] Lista rekordow [" + symbol.getSymbolName() + ","
						+ timeFrame.getTimeFrameDesc() + "].");
				return;
			}

			// zaczytaj wolumen rzeczywisty:
			rec_volume_list = updateBarVolumes(rec_list, symbol, timeFrame, sys_date);

			// wpisz rekordy lub aktualizuj w DB:
			currencyDbWriterService.write(symbol, timeFrame, rec_volume_list, sys_date);

		} catch (ReaderException e) {
			e.printStackTrace();
			throw new BatchProcessException("::rewriteFileContent2db:: wyjatek ReaderException!", e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BatchProcessException("::rewriteFileContent2db:: wyjatek ParseException!", e);
		} catch (BaseServiceException e) {
			e.printStackTrace();
			throw new BatchProcessException("::rewriteFileContent2db:: wyjatek BaseServiceException!", e);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BatchProcessException("::rewriteFileContent2db:: wyjatek Throwable!", th);
		} 
	}

	private List<Mt4FileRecord> updateBarVolumes(List<Mt4FileRecord> recordList, CurrencySymbol symbol,
			TimeFrame timeFrame, GregorianCalendar actualTime) throws BatchProcessException {
		List<Mt4FileRecord> result_list = new ArrayList<Mt4FileRecord>();
		GregorianCalendar min_time = null;
		int k = 0;
		String http_response = null;
		Map<GregorianCalendar, Integer> volume_map = new HashMap<GregorianCalendar, Integer>();

		if (StringUtils.isBlank(symbol.getFuturesSymbol())) {
			LOGGER.info("   [VOL] Brak wolumenu rzeczywistego [" + symbol.getFuturesSymbol() + "] dla symbolu ["
					+ symbol.getSymbolName() + "].");
			return recordList;
		}

		// pobierz datę minimalną:
		for (int i = 0; i < recordList.size(); i++) {
			k = i + 1;

			if (k == recordList.size())
				min_time = recordList.get(i).getBarTime();
		}

		try {
			http_response = httpReader.readFromUrl(symbol, timeFrame, actualTime.getTime(), min_time.getTime(),
					httpAccessKey, isHttpProxy);
			if (StringUtils.isBlank(http_response)) {
				LOGGER.info("   [VOL] Pusta odpowiedz HTTP [" + http_response + "] dla symbolu,ramki: ["
						+ symbol.getSymbolName() + "," + timeFrame + "].");
				return recordList;
			}

			String[] resp_tab = http_response.split("\n");
			String[] line_tab = null;

			for (String line : resp_tab) {
				line_tab = line.split(";");

				volume_map.put(DateConverter.stringToGregorian(line_tab[0].trim(), "dd.MM.yyyy HH:mm"),
						Integer.valueOf(line_tab[1].trim()));
			}

			// aktualizacja wolumenów:
			Integer real_volume = null;
			for (Mt4FileRecord file_rec : recordList) {
				real_volume = volume_map.get(file_rec.getBarTime());

				if (real_volume != null && real_volume.intValue() > 0) {
					LOGGER.info("      [" + file_rec.getBarTime() + "]: [" + real_volume + "].");

					file_rec.setBarVolume(real_volume);
				}

				result_list.add(file_rec);
			}

			return result_list;

		} catch (IOException e) {
			e.printStackTrace();
			throw new BatchProcessException("::updateBarVolumes:: wyjatek IOException!", e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new BatchProcessException("::updateBarVolumes:: wyjatek NumberFormatException!", e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BatchProcessException("::updateBarVolumes:: wyjatek ParseException!", e);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new BatchProcessException("::updateBarVolumes:: wyjatek Throwable!", th);
		}
	}

}
