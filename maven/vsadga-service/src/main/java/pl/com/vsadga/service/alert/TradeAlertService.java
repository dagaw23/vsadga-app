package pl.com.vsadga.service.alert;

import java.util.List;

import pl.com.vsadga.data.alert.TradeAlert;

public interface TradeAlertService {

	/**
	 * Pobiera listę wszystkich alertów - z ostatniej godziny.
	 * 
	 * @return
	 */
	List<TradeAlert> getActualTradeAlertList();
	
	List<TradeAlert> getByFrameAndSymbol(String symbol, String frame);
}
