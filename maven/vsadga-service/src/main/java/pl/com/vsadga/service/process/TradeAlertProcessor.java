package pl.com.vsadga.service.process;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface TradeAlertProcessor {

	void checkTradeAlert(CurrencySymbol symbol, List<TimeFrame> timeFrameList) throws BaseServiceException;
	
}
