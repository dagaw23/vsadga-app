package pl.com.vsadga.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.utils.DateConverter;

@Component
public class DataAnalyseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyseBatch.class);

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private BarDataProcessor barDataProcessor;

	@Scheduled(cron = "10 * * * * MON-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;

		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		// pobierz listę aktywnych timeframe:
		tmefrm_list = timeFrameService.getAllActive();

		if (symbol_list.isEmpty() || tmefrm_list.isEmpty()) {
			LOGGER.info("   ###Not active### Symbole [" + symbol_list.size() + "] lub TimeFrame ["
					+ tmefrm_list.size() + "].");
			return;
		}

		List<BarData> data_list = null;
		try {
			for (CurrencySymbol symbol : symbol_list) {
				for (TimeFrame tme_frame : tmefrm_list) {
					LOGGER.info("   [PROC] " + symbol.getSymbolName() + " in " + tme_frame.getTimeFrameDesc()
							+ ".");

					// pobierz listę danych z bara:
					data_list = currencyDataService.getLastNbarData(100, symbol, tme_frame);
					int list_size = data_list.size();
					String msg = "   [PROC] Liczba barow do przetworzenia: " + list_size;

					if (list_size > 0)
						msg += " ("
								+ DateConverter.dateToString(data_list.get(0).getBarTime(), "yy/MM/dd HH:mm")
								+ ","
								+ DateConverter.dateToString(data_list.get(list_size - 1).getBarTime(),
										"yy/MM/dd HH:mm") + ").";

					LOGGER.info(msg);

					// przetwórz listę barów:
					barDataProcessor.processBarsData(data_list, tme_frame);
				}
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}

}
