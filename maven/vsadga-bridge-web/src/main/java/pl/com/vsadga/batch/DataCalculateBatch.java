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
	
	@Scheduled(cron = "30 0/1 7-23 * * SUN-FRI")
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
				// TODO
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
	
	private void processAllBarData() {
		List<CurrencySymbol> symbol_list = null;
		List<BarData> bar_list = null;
		GregorianCalendar bar_time = null;
		int day_of_mth = 0;
		List<BarData> hour_bar_list = new ArrayList<BarData>();
		List<BarData> day_bar_list = new ArrayList<BarData>();
		BarData day_bar = null;
		
		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		if (symbol_list.isEmpty()) {
			LOGGER.info("   [CALC] Zaden symbol nie jest aktywny [" + symbol_list.size() + "].");
			return;
		}
		
		try {
		for (CurrencySymbol sym : symbol_list) {
			int del_cnt = 0;
			
			// usuń dane z D1 - dla symbolu:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "D1");
			if (bar_list.isEmpty()) {
				LOGGER.info("   [CALC] Brak danych dla [" + sym.getSymbolName() + "] i D1.");
			} else {
				del_cnt = currencyDataService.deleteAll("D1", bar_list);
				LOGGER.info("   [CALC] Usunieto rekordy dla [" + sym.getSymbolName() + "] i D1 - w liczbie [" + del_cnt + "].");
			}
			
			// usuń dane z W1 - dla symbolu:
			// TODO: dodac takze przetwarzanie W1
			//bar_list = currencyDataService.getBarDataList(sym.getId(), "W1");
			//if (bar_list.isEmpty()) {
			//	LOGGER.info("   [CALC] Brak danych dla [" + sym.getSymbolName() + "] i W1.");
			//} else {
			//	del_cnt = currencyDataService.deleteAll("W1", bar_list);
			//	LOGGER.info("   [CALC] Usunieto rekordy dla [" + sym.getSymbolName() + "] i W1 - w liczbie [" + del_cnt + "].");
			//}
			
			// pobierz dane wg symbolu - z H1:
			bar_list = currencyDataService.getBarDataList(sym.getId(), "H1");
			
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
			
			// dodaj bary dniowe do tabeli:
			LOGGER.info("   [CALC] Symbol [" + sym.getSymbolName() + "] - dodano [" + addDayData(day_bar_list, "D1") + "] rekordy do D1.");
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
