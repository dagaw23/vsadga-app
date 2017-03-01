package pl.com.vsadga.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.TradeAlertProcessor;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;

@Component
public class TradeAlertBatch extends BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeAlertBatch.class);

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private TradeAlertProcessor tradeAlertProcessor;

	@Scheduled(cron = "15 0/5 * * * SUN-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// pobierz listę aktywnych symboli:
			symbol_list = symbolService.getActiveSymbols();
			// pobierz listę aktywnych timeframe:
			tmefrm_list = timeFrameService.getAllActive();

			if (symbol_list.isEmpty() || tmefrm_list.isEmpty()) {
				LOGGER.info("   [BATCH] Zadne symbole nie sa aktywne [" + symbol_list.size()
						+ "] ani ramy czasowe [" + tmefrm_list.size() + "].");
				return;
			}
			
			// aktualny czas:
			//GregorianCalendar act_date = new GregorianCalendar();
			//act_date.setTime(new Date());
			// aktualna minuta:
			//int min_nr = act_date.get(Calendar.MINUTE);

			for (CurrencySymbol symbol : symbol_list) {
				LOGGER.info("   [ALERT] Symbol [" + symbol.getSymbolName() + "].");

				//tradeAlertProcessor.checkTradeAlert(symbol, tmefrm_list);
				
				tradeAlertProcessor.checkVolumeSize(symbol, tmefrm_list);
			}

		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}

	private boolean isProcessBatch() throws BaseServiceException {
		Integer is_alert = getIntParamValue("IS_BATCH_TRADE_ALERT");

		if (is_alert == null)
			throw new BaseServiceException("::isProcessBatch:: brak parametru IS_BATCH_TRADE_ALERT [" + is_alert
					+ "] w tabeli parametrow.");

		if (is_alert.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch alertow trade [" + is_alert.intValue() + "].");
			return false;
		}
	}
}
