package pl.com.vsadga.service.process;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.service.BaseServiceException;

public class BarDataProcessorImpl implements BarDataProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BarDataProcessorImpl.class);
	
	private Map<Integer, BarStatsData> barDataMap;
	
	/**
	 * minimalna ilość informacji potrzebna do wskaźników
	 */
	private int minimumStatsSize;
	
	private int actualMapPosition;
	
	private BarDataDao barDataDao;
	

	@Override
	public void processBarsData(List<BarData> barDataList, TimeFrame timeFrame) throws BaseServiceException {
		if (barDataList == null || barDataList.isEmpty()) {
			LOGGER.info("   [EMPTY] Lista barow [" + barDataList + "].");
			return;
		}
		
		barDataMap = new HashMap<Integer, BarStatsData>();
		actualMapPosition = 1;
		
		int bar_count = barDataList.size();
		BarData bar_data = null;
		String trend = null;
		
		
		Integer prev_bar_id = null;
		
		
		// 3
		// 5
		for (int i=0; i<bar_count; i++) {
			// pobierz bar:
			bar_data = barDataList.get(i);
			
			
			
			
			
			// zapisz informację o barze do mapy:
			//barDataMap.put(actualMapPosition, )
		}
		
		
	}
	
	private void processByPhase(BarData barData, String frameDesc) {
		// jeśli brak barów statystycznych: aktualizuj tylko status
		if (barDataMap.size() < minimumStatsSize) {
			LOGGER.info("   [STATS] Zbyt malo barow do analizy [" + barDataMap.size() + "-" + minimumStatsSize + "].");
			
			// jeśli status w tych barach < od 3 - ustawienie na 3:
			if (barData.getProcessPhase().intValue() < 3)
				barDataDao.updateProcessPhase(barData.getId(), 3, frameDesc);
			
			return;
		}
		
		
		
//		int proc_phase =barData.getProcessPhase().intValue();
//		
//		// jeśli już przetworzony do końca - zapisanie trendu:
//		if (proc_phase == 3) {
//			fillDataMap();
//			trend = bar_data.getTrendIndicator();
//		}
//		
//		// czy sygnał oczekuje potwierdzenia:
//		if (proc_phase == 2) {
//			trend = bar_data.getTrendIndicator();
//			prev_bar_id = bar_data.getId();
//		}
//		
//		// kompletne przetworzenie bara:
//		if (proc_phase == 1) {
//			processComplete(bar_data);
//		
//		}
	}

	private void fillDataMap() {
		Map<Integer, BarData> tmp_map = new HashMap<Integer, BarData>();
		
		if (barDataMap.size() > 5) {
			
			
		}
		
		
	}

	private void processComplete(BarData barData, String trend, BigDecimal prevIma) {
		BarData result = new BarData();
		
		// wyliczenie trendu:
		if (trend == null)
			result.setTrendIndicator("S");
		else {
			int comp = barData.getImaCount().compareTo(prevIma);
			
			if (trend.equals("S")) {
				if (comp > 0)
					result.setTrendIndicator("U");
				else if (comp < 0)
					result.setTrendIndicator("D");
				else
					result.setTrendIndicator("S");
			} else if (trend.equals("U")) {
				if (comp > 0)
					result.setTrendIndicator("U");
				else if (comp < 0)
					result.setTrendIndicator("S");
				else
					result.setTrendIndicator("U");
			} else if (trend.equals("D")) {
				if (comp > 0)
					result.setTrendIndicator("S");
				else if (comp < 0)
					result.setTrendIndicator("D");
				else
					result.setTrendIndicator("D");
				
			}
		}
		
		// 
			
		
		
	}

}
