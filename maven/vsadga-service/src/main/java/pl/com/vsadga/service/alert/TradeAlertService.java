package pl.com.vsadga.service.alert;

import java.util.List;

import pl.com.vsadga.data.TradeAlert;

public interface TradeAlertService {

	/**
	 * Pobiera listę wszystkich alertów - z ostatniej godziny.
	 * 
	 * @return
	 */
	List<TradeAlert> getActualTradeAlertList();
}
