package pl.com.vsadga.service.alert;

import java.util.List;

import pl.com.vsadga.dto.alert.TradeAlertDto;

public interface TradeAlertService {

	/**
	 * Pobiera listę wszystkich alertów - z ostatniej godziny.
	 * 
	 * @return
	 */
	List<TradeAlertDto> getActualTradeAlertList(int dayBackLimit);
	
	List<TradeAlertDto> getByFrameAndSymbol(String symbol, String frame, int dayBackLimit);
}
