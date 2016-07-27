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
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.symbol.SymbolService;

@Component
public class DataCalculateBatch extends BaseBatch {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataCalculateBatch.class);
	
	@Autowired
	private SymbolService symbolService;
	
	@Autowired
	private CurrencyDataService currencyDataService;

	public DataCalculateBatch() {
	}
	
	@Scheduled(cron = "45 0 0,7-23 * * SUN-FRI")
	public void cronJob() {
		
		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch("IS_BATCH_DATA_CALCULATE"))
				return;
			
			processByMode();
			
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void processByMode() {
		String param_value = null;
		
		try {
			// pobierz tryb przetworzenia:
			param_value = getStringParamValue("DATA_CALCULATE_MODE");
			if (param_value == null)
				return;
			
			if (param_value.equals("ALL")) {
				processAllBarData();
			} else if (param_value.equals("STEP")) {
				processStepBarData();
			} else if (param_value.startsWith("FROM")) {
				// TODO
			} else {
				LOGGER.error("TRYB [" + param_value + "] nie jest obslugiwany.");
				return;
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isMidnightTime(GregorianCalendar actualTime) {
		if (actualTime.get(Calendar.HOUR_OF_DAY) == 0)
			return true;
		else
			return false;
	}
	
	private void updateActualDay(List<CurrencySymbol> symbolList, GregorianCalendar actualTime) throws BaseServiceException {
		List<BarData> bar_list = null;
		LOGGER.info("   [CALC] updateActualDay.");
		
		for (CurrencySymbol sym : symbolList) {
			
			// pobierz dane wg symbolu - z H1:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "H1", actualTime.getTime());
			
			if (bar_list.size() > 0)
				currencyDataService.update("D1", getMainSingleDayBar(bar_list));
		}
	}
	
	private void updateLastAndOpenNewDay(List<CurrencySymbol> symbolList, GregorianCalendar actualTime, GregorianCalendar prevTime) throws BaseServiceException {
		List<BarData> bar_list = null;
		LOGGER.info("   [CALC] updateLastAndOpenNewDay.");
		
		for (CurrencySymbol sym : symbolList) {
			
			// pobierz dane za poprzedni dzień - z H1:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "H1", prevTime.getTime(), actualTime.getTime());
			
			if (bar_list.size() > 0)
				currencyDataService.update("D1", getMainSingleDayBar(bar_list));
			
			// pobierz dane za nowy dzień:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "H1", actualTime.getTime());
			
			if (bar_list.size() > 0)
				currencyDataService.insert("D1", getSingleDayBar(bar_list));
		}
	}
	
	private void processStepBarData() {
		List<CurrencySymbol> symbol_list = null;
		GregorianCalendar time_shift = null;
		
		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		if (symbol_list.isEmpty()) {
			LOGGER.info("   [CALC] Zaden symbol nie jest aktywny [" + symbol_list.size() + "].");
			return;
		}
		
		
		try {
			// pobierz aktualną godzinę - z przesunięciem:
			time_shift = getActualTimeWithShift();
			
			if (isMidnightTime(time_shift)) {
				time_shift.set(Calendar.MINUTE, 0);
				time_shift.set(Calendar.SECOND, 0);
				time_shift.set(Calendar.MILLISECOND, 0);
				
				GregorianCalendar prev_day = new GregorianCalendar();
				prev_day.setTime(time_shift.getTime());
				prev_day.add(Calendar.HOUR_OF_DAY, -24);
				
				updateLastAndOpenNewDay(symbol_list, time_shift, prev_day);
				
			} else {
				time_shift.set(Calendar.HOUR_OF_DAY, 0);
				time_shift.set(Calendar.MINUTE, 0);
				time_shift.set(Calendar.SECOND, 0);
				time_shift.set(Calendar.MILLISECOND, 0);
				
				updateActualDay(symbol_list, time_shift);
			}
		
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void processAllBarData() {
		List<CurrencySymbol> symbol_list = null;
		List<BarData> bar_list = null;
		GregorianCalendar bar_time = null;
		int day_of_mth = 0;
		List<BarData> hour_bar_list = null;
		List<BarData> day_bar_list = null;

		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		if (symbol_list.isEmpty()) {
			LOGGER.info("   [CALC] Zaden symbol nie jest aktywny [" + symbol_list.size() + "].");
			return;
		}

		try {
			for (CurrencySymbol sym : symbol_list) {
				int del_cnt = 0;
				hour_bar_list = new ArrayList<BarData>();
				day_bar_list = new ArrayList<BarData>();
				day_of_mth = 0;

				// usuń dane z D1 - dla symbolu:
				bar_list = currencyDataService.getBarDataList(sym.getId(), "D1");
				if (bar_list.isEmpty()) {
					LOGGER.info("   [CALC] Brak danych dla [" + sym.getId() + "," + sym.getSymbolName()
							+ "] i D1.");
				} else {
					del_cnt = currencyDataService.deleteAll("D1", bar_list);
					LOGGER.info("   [CALC] Usunieto rekordy dla [" + sym.getId() + "," + sym.getSymbolName()
							+ "] i D1 - w liczbie [" + del_cnt + "].");
				}

				// pobierz dane wg symbolu - z H1:
				bar_list = currencyDataService.getBarDataList(sym.getId(), "H1");
				LOGGER.info("   [CALC] H1 dla [" + sym.getId() + "," + sym.getSymbolName() + "] w liczbie "
						+ bar_list.size());

				for (BarData bar_data : bar_list) {
					bar_time = new GregorianCalendar();
					bar_time.setTime(bar_data.getBarTime());

					if (day_of_mth == 0) {
						day_of_mth = bar_time.get(Calendar.DAY_OF_MONTH);
						hour_bar_list.add(bar_data);
						continue;
					}

					// zmiana dnia:
					if (bar_time.get(Calendar.DAY_OF_MONTH) != day_of_mth) {
						// pobierz bar dzienny:
						day_bar_list.add(getSingleDayBar(hour_bar_list));

						hour_bar_list = new ArrayList<BarData>();
						day_of_mth = bar_time.get(Calendar.DAY_OF_MONTH);
					}

					hour_bar_list.add(bar_data);
				}

				// zapisanie jeszcze ostatniej porcji danych:
				if (hour_bar_list.size() > 0)
					day_bar_list.add(getSingleDayBar(hour_bar_list));

				// dodaj bary dniowe do tabeli:
				LOGGER.info("   [CALC] Symbol [" + sym.getSymbolName() + "] - dodano ["
						+ addDayData(day_bar_list, "D1") + "] rekordy do D1.");
			}

		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}
	
	private int addDayData(List<BarData> dayBarData, String frameDesc) throws BaseServiceException {
		if (dayBarData.size() == 0) {
			LOGGER.info("   [CALC] Pusta lista barow dniowych [" + dayBarData.size() + "].");
			return 0;
		}
		
		BarData bar_data = null;
		BigDecimal bar_close = null;
		int add_count = 0;
		for (int i=0; i<dayBarData.size(); i++) {
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
	
	private BarData getSingleDayBar(List<BarData> barDataList) {
		BarData barData = new BarData();
		int process_phase = 0;
		
		if (barDataList.size() == 24)
			process_phase = 3;
		
		BigDecimal bar_high = null;
		BigDecimal bar_low = null;
		BigDecimal bar_close = null;
		Integer bar_volume = 0;
		
		for (BarData bar_data : barDataList) {
			if (bar_close == null) {
				bar_close = bar_data.getBarClose();
				bar_high = bar_data.getBarHigh();
				bar_low = bar_data.getBarLow();
				bar_volume += bar_data.getBarVolume();
				continue;
			}
			
			if (bar_data.getBarHigh().compareTo(bar_high) > 0)
				bar_high = bar_data.getBarHigh();
			
			if (bar_data.getBarLow().compareTo(bar_high) < 0)
				bar_low = bar_data.getBarLow();
			
			bar_close = bar_data.getBarClose();
			bar_volume += bar_data.getBarVolume();
		}
		
		barData.setBarHigh(bar_high);
		barData.setBarLow(bar_low);
		barData.setBarClose(bar_close);
		barData.setBarVolume(bar_volume);
		
		barData.setImaCount(new BigDecimal(0));
		barData.setBarTime(getSingleDayDate(barDataList.get(0)));
		barData.setSymbolId(barDataList.get(0).getSymbolId());
		barData.setProcessPhase(process_phase);
		
		return barData;
	}
	
	private BarData getMainSingleDayBar(List<BarData> barDataList) {
		BarData barData = new BarData();
		
		BigDecimal bar_high = null;
		BigDecimal bar_low = null;
		BigDecimal bar_close = null;
		Integer bar_volume = 0;
		
		for (BarData bar_data : barDataList) {
			if (bar_close == null) {
				bar_close = bar_data.getBarClose();
				bar_high = bar_data.getBarHigh();
				bar_low = bar_data.getBarLow();
				bar_volume += bar_data.getBarVolume();
				continue;
			}
			
			if (bar_data.getBarHigh().compareTo(bar_high) > 0)
				bar_high = bar_data.getBarHigh();
			
			if (bar_data.getBarLow().compareTo(bar_high) < 0)
				bar_low = bar_data.getBarLow();
			
			bar_close = bar_data.getBarClose();
			bar_volume += bar_data.getBarVolume();
		}
		
		barData.setBarHigh(bar_high);
		barData.setBarLow(bar_low);
		barData.setBarClose(bar_close);
		barData.setBarVolume(bar_volume);
		
		barData.setSymbolId(barDataList.get(0).getSymbolId());
		barData.setBarTime(getSingleDayDate(barDataList.get(0)));
		
		return barData;
	}
	
	private Date getSingleDayDate(BarData barData) {
		GregorianCalendar greg_cal = new GregorianCalendar();
		greg_cal.setTime(barData.getBarTime());
		
		greg_cal.set(Calendar.HOUR_OF_DAY, 0);
		greg_cal.set(Calendar.MINUTE, 0);
		greg_cal.set(Calendar.SECOND, 0);
		greg_cal.set(Calendar.MILLISECOND, 0);
		
		return greg_cal.getTime();
	}

}
