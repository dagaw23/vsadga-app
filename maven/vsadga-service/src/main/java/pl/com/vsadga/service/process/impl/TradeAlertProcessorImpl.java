package pl.com.vsadga.service.process.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.TradeAlertProcessor;

public class TradeAlertProcessorImpl implements TradeAlertProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TradeAlertProcessorImpl.class);
	
	private BarDataDao barDataDao;

	@Override
	public void checkTradeAlert(CurrencySymbol symbol, List<TimeFrame> timeFrameList) throws BaseServiceException {
		List<BarData> bar_list = null;
		BarData bar_data = null;
		
		
		for (TimeFrame tme_frm : timeFrameList) {
			
			bar_list = barDataDao.getLastNbarsData(symbol.getId(), tme_frm.getTimeFrameDesc(), 1);
			
			if (bar_list.isEmpty()) {
				LOGGER.info("   [TRADE] Pusta lista dla [" + symbol.getSymbolName() + "] i ramy czasowej [" + tme_frm.getTimeFrameDesc() + "] - koniec sprawdzania.");
				return;
			}
			
			
			
		}
			
		
		
	}

}
