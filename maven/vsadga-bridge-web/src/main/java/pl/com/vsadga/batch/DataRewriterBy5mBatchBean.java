package pl.com.frxdream.batch;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.frxdream.data.CurrencySymbol;
import pl.com.frxdream.data.TimeFrame;
import pl.com.frxdream.io.BarDataFileReader;
import pl.com.frxdream.io.ReaderException;
import pl.com.frxdream.service.BaseServiceException;
import pl.com.frxdream.service.config.ConfigDataService;
import pl.com.frxdream.service.symbol.CurrencyWritedService;
import pl.com.frxdream.service.symbol.SymbolService;
import pl.com.frxdream.service.timeframe.TimeFrameService;
import pl.com.frxdream.service.writer.CurrencyDbWriterService;
import pl.com.frxdream.utils.DateConverter;

@Component
public class DataRewriterBy5mBatchBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataRewriterBy5mBatchBean.class);
	
	@Autowired
	private ConfigDataService configDataService;
	
	@Autowired
	private SymbolService symbolService;
	
	@Autowired
	private TimeFrameService timeFrameService;
	
	@Autowired
	private BarDataFileReader barDataFileReader;
	
	@Autowired
	private CurrencyWritedService currencyWritedService;
	
	@Autowired
	private CurrencyDbWriterService currencyDbWriterService;
	

	@Scheduled(cron="0,30 * * * * *")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;
		String file_path = null;
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		
		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		// czy są zdefiniowane aktywne symbole:
		if (symbol_list.isEmpty()) {
			LOGGER.info("   ### Brak aktywnych symboli [" + symbol_list.size() + "].");
			return;
		}
		
		// pobierz listę aktywnych timeframe:
		tmefrm_list = timeFrameService.getAllActive();
		// czy są zdefiniowane timeFrames:
		if (tmefrm_list.isEmpty()) {
			LOGGER.info("   ### Brak aktywnych timeFrame [" + tmefrm_list.size() + "].");
			return;
		}
		
		try {
			// pobierz ścieżkę dostępu do plików z danymi:
			file_path = configDataService.getParam("MT4_PATH");
			
			if (file_path == null || file_path.trim().isEmpty()) {
				LOGGER.info("   ### Brak parametru o nazwie MT4_PATH [" + file_path + "].");
				return;
			}
			
		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		}
		
		

		LOGGER.info("Invoked on " + dateFormat.format(System.currentTimeMillis()) + ".");
	}
	
	private void rewriteFileContent2db(List<CurrencySymbol> symbolList, List<TimeFrame> tmeFrameList, String filePath) throws BaseServiceException {
		List<String> rec_list = null;
		Timestamp write_time = null;
		
		
		try {
		for (CurrencySymbol symbol_list : symbolList) {
			
			for (TimeFrame tmefrm_list : tmeFrameList) {
				// pobierz całą zawartość pliku:
				rec_list = barDataFileReader.readAll(filePath, symbol_list.getSymbolName(), tmefrm_list.getTimeFrameDesc());
				
				// pobierz czas ostatniego zapisu waloru:
				write_time = currencyWritedService.getWritedTime(symbol_list.getId(), tmefrm_list.getId());
				
				if (write_time == null) {
					// wpisz wszystkie rekordy do tabeli:
					currencyDbWriterService.writeAll(rec_list, symbol_list, tmefrm_list);
				} else {
					
					writeFilePart(rec_list, write_time);
					
				}
			}
		}
		
		} catch (ReaderException e) {
			e.printStackTrace();
			LOGGER.error("::rewriteFileContent2db:: wyjatek ReaderException!");
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error("::rewriteFileContent2db:: wyjatek ParseException!");
		}
	}
	
	private int writeFilePart(List<String> recordList, Timestamp lastWriteTime, CurrencySymbol symbol, ) throws ParseException {
		int write_count = 0;
		List<String> rec_2_write_list = new ArrayList<String>();
		
		for (String rec : recordList) {
			if (lastWriteTime.getTime() < getRecordTime(rec)) {
				LOGGER.info("   ### Zapis wg daty z rekordu [" + rec + "].");
				
				rec_2_write_list.add(rec);
				
				write_count++;
			}
		}
		
		// zapis batchowy:
		currencyDbWriterService.writeAll(rec_2_write_list, symbolList, timeFrameList);
		return write_count;
	}
	
	private long getRecordTime(String record) throws ParseException {
		String[] rec_tab =  record.split(";");
		
		return DateConverter.stringToDate(rec_tab[0], "yyyy.MM.dd HH:mm:ss").getTime();
	}
	
	private GregorianCalendar getWriteTime(Timestamp timeStamp, int timeFrameInd) {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timeStamp.getTime());
		
		return cal;
		
		//cal.set(GregorianCalendar.SECOND, 0);
		//cal.set(GregorianCalendar.MILLISECOND, 0);
		
		//if (timeFrameInd == 5) {
//			cal.add(GregorianCalendar.MINUTE, amount);
	//		
		//} else if (timeFrameInd == 15) {
			
		//} else if (timeFrameInd == 60) {
			
		//} else if (timeFrameInd == 240) {
			
		//} else if (timeFrameInd == 1440) {
			
		//} else {
			//LOGGER.info("Przedzial czasowy [" + timeFrameInd + "] nie jest wspierany.");
			//return null;
		//}
			
		
		//DateConverter.dateToString(date)
		
	}
}
