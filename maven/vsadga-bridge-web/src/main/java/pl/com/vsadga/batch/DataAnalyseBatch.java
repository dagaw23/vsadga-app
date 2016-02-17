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

	public void cronJob() {
		// @Scheduled(cron = "10 0/1 * * * MON-FRI")
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
					data_list = currencyDataService.getBarDataList(symbol.getId(), tme_frame.getTimeFrameDesc());
					
					

					print(data_list);
				}
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}

	private void print(List<BarData> barDataList) {

		for (BarData data : barDataList)
			LOGGER.info("   > " + DateConverter.dateToString(data.getBarTime(), "yyyyMMdd hh:mm:ss") + ","
					+ data.getBarVolume() + "," + data.getBarHigh() + "," + data.getBarLow() + ","
					+ data.getBarClose() + "," + data.getImaCount() + "," + data.getSymbolId() + ".");
	}
}
