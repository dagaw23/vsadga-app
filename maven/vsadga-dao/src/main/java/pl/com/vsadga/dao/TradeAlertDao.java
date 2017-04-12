package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.alert.AlertType;
import pl.com.vsadga.data.alert.TradeAlert;
import pl.com.vsadga.dto.alert.TradeAlertDto;



public interface TradeAlertDao {

	int insert(String alertMessage, AlertType alertType, Integer symbolId, Integer timeFrameId, Date barTime, String barStatus);
	
	int update(Integer id, String alertMessage, String barStatus);
	
	List<TradeAlertDto> getActualTradeAlertList(Date alertTimeFrom);
	
	Integer exist(Integer symbolId, Integer timeFrameId, Date barTime, AlertType alertType);
	
	List<TradeAlertDto> getByFrameAndSymbol(Date alertTimeFrom, String symbolId, String frameId);
	
	List<TradeAlertDto> getByFrame(Date alertTimeFrom, String frameId);
	
	List<TradeAlertDto> getBySymbol(Date alertTimeFrom, String symbolId);
}
