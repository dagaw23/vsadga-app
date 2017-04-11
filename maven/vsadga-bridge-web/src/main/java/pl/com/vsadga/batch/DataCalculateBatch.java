package pl.com.vsadga.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.utils.DateConverter;

@Component
public class DataCalculateBatch extends BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataCalculateBatch.class);

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	public DataCalculateBatch() {
	}

	@Scheduled(cron = "45 0/5 * * * MON-FRI")
	public void cronJob() {

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch("IS_BATCH_DATA_CALCULATE"))
				return;

			if (!processByMode())
				LOGGER.error("   [CALC] Bledne zakonczenie naliczania barow D1!");

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		}
	}

	private int addDataByFrame(List<BarData> dayBarData, String frameDesc) throws BaseServiceException {
		if (dayBarData.size() == 0) {
			LOGGER.info("   [CALC] Pusta lista barow dniowych [" + dayBarData.size() + "].");
			return 0;
		}

		BarData bar_data = null;
		BigDecimal bar_close = null;
		int add_count = 0;
		for (int i = 0; i < dayBarData.size(); i++) {
			bar_data = dayBarData.get(i);

			if (i != 0) {
				int close_compare = bar_data.getBarClose().compareTo(bar_close);
				if (close_compare > 0)
					bar_data.setBarType(BarType.UP_BAR);
				else if (close_compare < 0)
					bar_data.setBarType(BarType.DOWN_BAR);
				else
					bar_data.setBarType(BarType.LEVEL_BAR);

			}

			// wstawienie bara do tabeli:
			add_count += currencyDataService.insert(frameDesc, bar_data);

			bar_close = bar_data.getBarClose();
		}

		return add_count;
	}

	private boolean deleteD1barData(CurrencySymbol symbol) {
		List<BarData> bar_list = null;
		int del_cnt = 0;

		try {
			// usuń dane z D1 - dla symbolu:
			bar_list = currencyDataService.getBarDataList(symbol.getId(), "D1");

			if (bar_list.isEmpty()) {
				LOGGER.info("   [DELETE] Brak danych wg [" + symbol.getId() + "," + symbol.getSymbolName()
						+ "] dla D1.");
			} else {
				del_cnt = currencyDataService.deleteAll("D1", bar_list);
				LOGGER.info("   [DELETE] Usunieto rekordy wg [" + symbol.getId() + "," + symbol.getSymbolName()
						+ "] dla D1 - w liczbie [" + del_cnt + "].");
			}

			return true;
		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::deleteD1barData:: wyjatek BaseServiceException!", e);
			return false;
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::deleteD1barData:: wyjatek Throwable!", th);
			return false;
		}
	}
	
	private boolean deleteW1barData(CurrencySymbol symbol) {
		List<BarData> bar_list = null;
		int del_cnt = 0;

		try {
			// usuń dane z W1 - dla symbolu:
			bar_list = currencyDataService.getBarDataList(symbol.getId(), "W1");

			if (bar_list.isEmpty()) {
				LOGGER.info("   [DELETE] Brak danych wg [" + symbol.getId() + "," + symbol.getSymbolName()
						+ "] dla W1.");
			} else {
				del_cnt = currencyDataService.deleteAll("W1", bar_list);
				LOGGER.info("   [DELETE] Usunieto rekordy wg [" + symbol.getId() + "," + symbol.getSymbolName()
						+ "] dla W1 - w liczbie [" + del_cnt + "].");
			}

			return true;
		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::deleteW1barData:: wyjatek BaseServiceException!", e);
			return false;
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::deleteW1barData:: wyjatek Throwable!", th);
			return false;
		}
	}

	/**
	 * 
	 * @param barDataList lista barów, z których tworzony jest zbiorczy bar
	 * @param barTime data i godzina tworzonego bara
	 * @param symbolId ID symbolu tworzonego bara
	 * @param processPhase status tworzonego bara
	 * @return
	 */
	private BarData getSingleSummaryBar(List<BarData> barDataList, Date barTime, Integer symbolId, Integer processPhase) {
		BarData barData = new BarData();

		BigDecimal bar_high = null;
		BigDecimal bar_low = null;
		BigDecimal bar_close = null;
		String volume_type = null;
		Integer bar_volume = 0;

		for (BarData bar_data : barDataList) {
			if (bar_close == null) {
				bar_close = bar_data.getBarClose();
				bar_high = bar_data.getBarHigh();
				bar_low = bar_data.getBarLow();
				bar_volume += bar_data.getBarVolume();
				volume_type = bar_data.getVolumeType();
				continue;
			}

			if (bar_data.getBarHigh().compareTo(bar_high) > 0)
				bar_high = bar_data.getBarHigh();

			if (bar_data.getBarLow().compareTo(bar_low) < 0)
				bar_low = bar_data.getBarLow();

			bar_close = bar_data.getBarClose();
			bar_volume += bar_data.getBarVolume();
			volume_type = bar_data.getVolumeType();
		}

		barData.setBarHigh(bar_high);
		barData.setBarLow(bar_low);
		barData.setBarClose(bar_close);
		barData.setBarVolume(bar_volume);

		barData.setImaCount(new BigDecimal(0));
		// barData.setBarTime(getSingleDayDate(barDataList.get(0)));
		barData.setBarTime(barTime);
		barData.setSymbolId(symbolId);
		barData.setProcessPhase(processPhase);
		barData.setVolumeType(volume_type);

		return barData;
	}

	private Date getCleanDayDate(GregorianCalendar gregCal) {
		GregorianCalendar greg_cal = new GregorianCalendar();
		greg_cal.setTime(gregCal.getTime());

		greg_cal.set(Calendar.HOUR_OF_DAY, 0);
		greg_cal.set(Calendar.MINUTE, 0);
		greg_cal.set(Calendar.SECOND, 0);
		greg_cal.set(Calendar.MILLISECOND, 0);

		return greg_cal.getTime();
	}
	
	private Date getCleanWeekDate(GregorianCalendar gregCal) {
		// który dzień tygodnia:
		int day_of_week_nr = gregCal.get(Calendar.DAY_OF_WEEK);
		
		GregorianCalendar week_begin = new GregorianCalendar();
		week_begin.setTime(gregCal.getTime());
		week_begin.add(Calendar.DAY_OF_WEEK, (-day_of_week_nr + 1));
		
		week_begin.set(Calendar.HOUR_OF_DAY, 0);
		week_begin.set(Calendar.MINUTE, 0);
		week_begin.set(Calendar.SECOND, 0);
		week_begin.set(Calendar.MILLISECOND, 0);

		return week_begin.getTime();
	}

	private boolean isMidnightTime(GregorianCalendar actualTime) {
		if (actualTime.get(Calendar.HOUR_OF_DAY) == 0)
			return true;
		else
			return false;
	}

	private boolean processAllBarBySymbol(String symbolName) {
		CurrencySymbol sym = symbolService.getCurrencySymbolByName(symbolName);
		if (sym == null) {
			LOGGER.info("   [CALC] Brak symbolu [" + sym + "] wg nazwy [" + symbolName + "].");
			return false;
		}

		LOGGER.info("   [CALC] Utworzenie D1 typu ::SYMBOL:: dla symbolu o nazwie [" + symbolName + "].");
		// usuń dane z D1 - dla symbolu:
		if (!deleteD1barData(sym))
			return false;

		// wylicz D1 wg H1 i wpisz do tabeli:
		//if (!processAndWriteHourBars(sym))
		//	return false;

		return true;
	}
	
	private GregorianCalendar getActualDateTime() {
		GregorianCalendar dateTime = new GregorianCalendar();
		dateTime.setTime(new Date());
		
		return dateTime;
	}

	private boolean processResetMode(String param, GregorianCalendar timeShift) {
		// RESET lub RESET:EUR
		List<CurrencySymbol> symbol_list = null;
		CurrencySymbol sym_obj = null;
		String symbol_name = null;
		
		if (param.length() > 5 && param.charAt(6) == ':') {
			symbol_name = param.substring(6);
			LOGGER.info("   [CALC] Wg typu ::RESET:: dla symbolu [" + symbol_name + "].");
			
			// czy jest taki symbol:
			sym_obj = symbolService.getCurrencySymbolByName(symbol_name);
			
			if (sym_obj == null) {
				LOGGER.error("   [CALC] Brak symbolu [" + sym_obj + "] wg nazwy [" + symbol_name  + "].");
				return false;
			}
			
			// usuń dane z D1 - dla symbolu:
			if (!deleteD1barData(sym_obj))
				return false;
			// usuń dane z W1 - dla symbolu:
			if (!deleteW1barData(sym_obj))
				return false;

			// wylicz D1 wg H1 i wpisz do tabeli:
			if (!calculateD1Period(sym_obj, timeShift))
				return false;
			// wylicz W1 wg D1 i wpisz do tabeli:
			if (!calculateW1Period(sym_obj, timeShift))
				return false;
		} else {
			// pobierz listę aktywnych symboli:
			symbol_list = symbolService.getActiveSymbols();
			
			if (symbol_list.isEmpty()) {
				LOGGER.info("   [CALC] Zaden symbol nie jest aktywny [" + symbol_list.size() + "].");
				return true;
			}
			
			LOGGER.info("   [CALC] Utworzenie D1 typu ::RESET:: dla symboli w liczbie [" + symbol_list.size() + "].");
			for (CurrencySymbol sym : symbol_list) {
				// usuń dane z D1 - dla symbolu:
				if (!deleteD1barData(sym))
					return false;
				// usuń dane z W1 - dla symbolu:
				if (!deleteW1barData(sym))
					return false;
				
				// wylicz D1 wg H1 i wpisz do tabeli:
				if (!calculateD1Period(sym, timeShift))
					return false;
				// wylicz W1 wg D1 i wpisz do tabeli:
				if (!calculateW1Period(sym, timeShift))
					return false;
			}
		}

		return true;
	}

	private boolean calculateD1Period(CurrencySymbol symbol, GregorianCalendar actDateTime) {
		List<BarData> bar_list = null;
		GregorianCalendar bar_time = null;
		Date prev_bar_time = null;
		int day_of_mth = 0;
		List<BarData> hour_bar_list = new ArrayList<BarData>();
		List<BarData> day_bar_list = new ArrayList<BarData>();

		try {

			// pobierz dane wg symbolu - z H1:
			bar_list = currencyDataService.getBarDataList(symbol.getId(), "H1");
			LOGGER.info("   [CALC] H1 dla [" + symbol.getId() + "," + symbol.getSymbolName() + "] w liczbie "
					+ bar_list.size());

			for (BarData bar_data : bar_list) {
				bar_time = new GregorianCalendar();
				bar_time.setTime(bar_data.getBarTime());

				if (day_of_mth == 0) {
					day_of_mth = bar_time.get(Calendar.DAY_OF_MONTH);
					hour_bar_list.add(bar_data);
					prev_bar_time = getCleanDayDate(bar_time);
					continue;
				}

				// zmiana dnia:
				if (bar_time.get(Calendar.DAY_OF_MONTH) != day_of_mth) {
					// zapisz bar dzienny:
					day_bar_list.add(getSingleSummaryBar(hour_bar_list, prev_bar_time, symbol.getId(), 3));

					hour_bar_list = new ArrayList<BarData>();
					day_of_mth = bar_time.get(Calendar.DAY_OF_MONTH);
					prev_bar_time = getCleanDayDate(bar_time);
				}

				hour_bar_list.add(bar_data);
			}

			// zapisanie jeszcze ostatniej porcji danych:
			if (hour_bar_list.size() > 0)
				day_bar_list.add(getSingleSummaryBar(hour_bar_list, prev_bar_time, symbol.getId(), 0));

			// dodaj bary dniowe do tabeli:
			LOGGER.info("   [CALC] Symbol [" + symbol.getSymbolName() + "] - dodano ["
					+ addDataByFrame(day_bar_list, "D1") + "] rekordy do D1.");

			return true;

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::processAndWriteHourBars:: wyjatek BaseServiceException!", e);
			return false;
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::processAndWriteHourBars:: wyjatek Throwable!", th);
			return false;
		}
	}
	
	private boolean calculateW1Period(CurrencySymbol symbol, GregorianCalendar actDateTime) {
		List<BarData> bar_list = null;
		GregorianCalendar bar_time = null;
		Date week_begin = null;
		int week_of_year = 0;
		List<BarData> day_bar_list = new ArrayList<BarData>();
		List<BarData> week_bar_list = new ArrayList<BarData>();

		try {

			// pobierz dane wg symbolu - z D1:
			bar_list = currencyDataService.getBarDataList(symbol.getId(), "D1");
			LOGGER.info("   [CALC] D1 dla [" + symbol.getId() + "," + symbol.getSymbolName() + "] w liczbie "
					+ bar_list.size());

			for (BarData bar_data : bar_list) {
				bar_time = new GregorianCalendar();
				bar_time.setTime(bar_data.getBarTime());

				if (week_of_year == 0) {
					week_of_year = bar_time.get(Calendar.WEEK_OF_YEAR);
					day_bar_list.add(bar_data);
					week_begin = getCleanWeekDate(bar_time);
					continue;
				}

				// zmiana tygodnia:
				if (bar_time.get(Calendar.WEEK_OF_YEAR) != week_of_year) {
					// zapisz bar tygodniowy:
					week_bar_list.add(getSingleSummaryBar(day_bar_list, week_begin, symbol.getId(), 3));

					day_bar_list = new ArrayList<BarData>();
					week_of_year = bar_time.get(Calendar.WEEK_OF_YEAR);
					week_begin = getCleanWeekDate(bar_time);
				}

				day_bar_list.add(bar_data);
			}

			// zapisanie jeszcze ostatniej porcji danych:
			if (day_bar_list.size() > 0)
				week_bar_list.add(getSingleSummaryBar(day_bar_list, week_begin, symbol.getId(), 0));

			// dodaj bary dniowe do tabeli:
			LOGGER.info("   [CALC] Symbol [" + symbol.getSymbolName() + "] - dodano ["
					+ addDataByFrame(week_bar_list, "W1") + "] rekordy do W1.");

			return true;

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::calculateW1Period:: wyjatek BaseServiceException!", e);
			return false;
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::calculateW1Period:: wyjatek Throwable!", th);
			return false;
		}
	}

	private boolean processByMode() {
		String param_value = null;
		GregorianCalendar time_shift = null;

		try {
			// pobierz tryb przetworzenia:
			param_value = getStringParamValue("DATA_CALCULATE_MODE");

			if (param_value == null)
				return false;
			
			// pobierz aktualną datę systemową z przesunięciem:
			time_shift = getActualTimeWithShift();
			
			// dopisanie wolumenów:
			//updateAbsorbVolume();
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return false;
		}


		if (param_value.startsWith("RESET")) {
			return processResetMode(param_value, time_shift);
		} else if (param_value.equals("NORMAL")) {
			return processNormalMode(time_shift);
		} else if (param_value.startsWith("PERIOD:")) {// TODO not implementaed yet
			String symbol_name = param_value.substring(7);
			return processAllBarBySymbol(symbol_name);
		} else {
			LOGGER.error("TRYB [" + param_value + "] nie jest obslugiwany.");
			return false;
		}
	}

	private void updateAbsorbVolume() throws BaseServiceException {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> frame_list = null;
		List<BarData> bar_list = null;
		Date start_date = null;
		Integer sum_volume = 0;
		BarData prev_bar = null;

		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		frame_list = timeFrameService.getAllActive();

		for (CurrencySymbol sym : symbol_list) {
			for (TimeFrame frm : frame_list) {
				bar_list = currencyDataService.getLastNbarData(3000, sym, frm);
				start_date = null;
				prev_bar = null;
				sum_volume = 0;

				for (BarData bar_data : bar_list) {
					if (start_date == null) {
						start_date = bar_data.getBarTime();
						sum_volume = bar_data.getBarVolume();
						prev_bar = bar_data;
						continue;
					}
					
					if (prev_bar.getBarType() == bar_data.getBarType()) {
						sum_volume += bar_data.getBarVolume();
						currencyDataService.updateAbsorbVolume(frm.getTimeFrameDesc(), bar_data.getId(), sum_volume);
					} else {
						currencyDataService.updateAbsorbVolume(frm.getTimeFrameDesc(), prev_bar.getId(), sum_volume);
						sum_volume = bar_data.getBarVolume();
						
						currencyDataService.updateAbsorbVolume(frm.getTimeFrameDesc(), bar_data.getId(), sum_volume);
					}

					prev_bar = bar_data;
				}

				LOGGER.info("   " + sym.getSymbolName() + "," + frm.getTimeFrameDesc() + ": START from ["
						+ DateConverter.dateToString(start_date, "yy/MM/dd HH:mm") + "], END ["
						+ DateConverter.dateToString(prev_bar.getBarTime(), "yy/MM/dd HH:mm") + "].");
			}
		}

	}

	private boolean processNormalMode(GregorianCalendar timeShift) {
		List<CurrencySymbol> symbol_list = null;
		
		// czy pełna godzina:
		if (timeShift.get(Calendar.MINUTE) != 0)
			return true;

		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		if (symbol_list.isEmpty()) {
			LOGGER.info("   [CALC] Zaden symbol nie jest aktywny [" + symbol_list.size() + "].");
			return true;
		}

		LOGGER.info("   [CALC] Utworzenie D1,W1 typu ::NORMAL:: dla symboli w liczbie [" + symbol_list.size() + "].");
		GregorianCalendar act_day = new GregorianCalendar();
		act_day.setTime(timeShift.getTime());
		try {

			if (isMidnightTime(timeShift)) {
				act_day.set(Calendar.MINUTE, 0);
				act_day.set(Calendar.SECOND, 0);
				act_day.set(Calendar.MILLISECOND, 0);

				GregorianCalendar prev_day = new GregorianCalendar();
				prev_day.setTime(timeShift.getTime());
				prev_day.add(Calendar.HOUR_OF_DAY, -24);

				closeDayBar(symbol_list, act_day, prev_day);
				closeWeekBar(symbol_list, act_day);

			} else {
				act_day.set(Calendar.HOUR_OF_DAY, 0);
				act_day.set(Calendar.MINUTE, 0);
				act_day.set(Calendar.SECOND, 0);
				act_day.set(Calendar.MILLISECOND, 0);

				updateActualDay(symbol_list, act_day);
				updateActualWeek(symbol_list, act_day);
			}

			return true;
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable th) {
			th.printStackTrace();
			return false;
		}
	}

	private void updateActualDay(List<CurrencySymbol> symbolList, GregorianCalendar actualTime)
			throws BaseServiceException {
		List<BarData> h1_list = null;
		LOGGER.info("   [CALC] updateActualDay: actualTime="
				+ DateConverter.dateToString(actualTime.getTime(), "yy/MM/dd HH:mm") + ".");

		for (CurrencySymbol sym : symbolList) {

			// pobierz dane wg symbolu - z H1:
			h1_list = currencyDataService.getBarDataList(sym.getId(), "H1", actualTime.getTime());

			if (h1_list.size() > 0)
				currencyDataService.insertOrUpdate("D1", getSingleSummaryBar(h1_list, actualTime.getTime(), sym.getId(), 0));
		}
	}
	
	private void updateActualWeek(List<CurrencySymbol> symbolList, GregorianCalendar actualTime) throws BaseServiceException {
		List<BarData> d1_list = null;
		
		// aktualizacja W1 tylko co 4 godziny:
		if (actualTime.get(Calendar.HOUR_OF_DAY) % 4 != 0)
			return;
		
		// który dzień tygodnia:
		int day_of_week_nr = actualTime.get(Calendar.DAY_OF_WEEK);
		
		GregorianCalendar week_begin = new GregorianCalendar();
		week_begin.setTime(actualTime.getTime());
		week_begin.add(Calendar.DAY_OF_WEEK, (-day_of_week_nr + 1));
		
		LOGGER.info("   [CALC] updateActualWeek: " + actualTime.get(Calendar.DAY_OF_WEEK) + ","	+ actualTime.get(Calendar.WEEK_OF_YEAR)
				+ ";" + week_begin.get(Calendar.DAY_OF_WEEK) + ","	+ week_begin.get(Calendar.WEEK_OF_YEAR)
				+ ", actualTime="
				+ DateConverter.dateToString(actualTime.getTime(), "yy/MM/dd HH:mm") + ", from=" 
				+ DateConverter.dateToString(week_begin.getTime(), "yy/MM/dd HH:mm") + ".");

		for (CurrencySymbol sym : symbolList) {

			// pobierz dane wg symbolu - z H1:
			d1_list = currencyDataService.getBarDataList(sym.getId(), "D1", week_begin.getTime());

			if (d1_list.size() > 0)
				currencyDataService.insertOrUpdate("W1", getSingleSummaryBar(d1_list, actualTime.getTime(), sym.getId(), 0));
		}
	}

	private void closeDayBar(List<CurrencySymbol> symbolList, GregorianCalendar actualTime, GregorianCalendar prevTime) throws BaseServiceException {
		List<BarData> bar_list = null;
		LOGGER.info("   [CALC] closeDayBar: prevTime="
				+ DateConverter.dateToString(prevTime.getTime(), "yy/MM/dd HH:mm") + ", actualTime="
				+ DateConverter.dateToString(actualTime.getTime(), "yy/MM/dd HH:mm") + ".");

		for (CurrencySymbol sym : symbolList) {

			// pobierz dane za poprzedni dzień - z H1:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "H1", prevTime.getTime(), actualTime.getTime());

			if (bar_list.size() > 0)
				currencyDataService.insertOrUpdate("D1", getSingleSummaryBar(bar_list, prevTime.getTime(), sym.getId(), 3));

			// pobierz dane za nowy dzień:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "H1", actualTime.getTime());

			if (bar_list.size() > 0)
				currencyDataService.insert("D1", getSingleSummaryBar(bar_list, actualTime.getTime(), sym.getId(), 0));
		}
	}
	
	private void closeWeekBar(List<CurrencySymbol> symbolList, GregorianCalendar actualTime) throws BaseServiceException {
		List<BarData> bar_list = null;
		
		// naliczenie zamkniętego week bara - w sobotę:
		if (actualTime.get(Calendar.DAY_OF_WEEK) != 7)
			return;
		
		// który dzień tygodnia:
		int day_of_week_nr = actualTime.get(Calendar.DAY_OF_WEEK);
		
		GregorianCalendar week_begin = new GregorianCalendar();
		week_begin.setTime(actualTime.getTime());
		week_begin.add(Calendar.DAY_OF_WEEK, (-day_of_week_nr + 1));
		
		LOGGER.info("   [CALC] closeWeekBar: beginOfWeek="
				+ DateConverter.dateToString(week_begin.getTime(), "yy/MM/dd HH:mm") + ".");

		for (CurrencySymbol sym : symbolList) {

			// pobierz dane za poprzedni tydzień - z D1:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "D1", week_begin.getTime(), actualTime.getTime());

			if (bar_list.size() > 0)
				currencyDataService.insertOrUpdate("W1", getSingleSummaryBar(bar_list, week_begin.getTime(), sym.getId(), 3));
		}
	}

}
