package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.service.BaseServiceException;

public interface IndicatorProcessor {

	/**
	 * Wylicza wskaźnik dla statusu bara 1.
	 * 
	 * @param barData
	 * @param frameDesc
	 * @return
	 * @throws BaseServiceException
	 */
	IndicatorInfo getDataIndicator(BarData barData, String frameDesc) throws BaseServiceException;
	
	/**
	 * Dodaje jedynie informację dotyczącą bara - dla wyliczeń następnych barów.
	 * 
	 * @param barData
	 * @throws BaseServiceException
	 */
	void addIndicatorData(BarData barData) throws BaseServiceException;
	
	/**
	 * Dodaje jedynie informację dotyczącą bara - dla wyliczeń następnych barów.
	 * 
	 * @param barData
	 * @throws BaseServiceException
	 */
	void addIndicatorData(BarData barData, Boolean isBarToConfirmation) throws BaseServiceException;
	
}
