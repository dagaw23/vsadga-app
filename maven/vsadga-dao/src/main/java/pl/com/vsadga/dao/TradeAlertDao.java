package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.alert.AlertType;
import pl.com.vsadga.data.alert.TradeAlert;



public interface TradeAlertDao {

	int insert(String alertMessage, AlertType alertType, Integer symbolId, Integer timeFrameId, Date barTime, String barStatus);
	
	int update(Integer id, String alertMessage, String barStatus);
	
	List<TradeAlert> getActualTradeAlertList(Date alertTimeFrom);
	
	Integer exist(Integer symbolId, Integer timeFrameId, Date barTime, AlertType alertType);
	
	List<TradeAlert> getByFrameAndSymbol(Date alertTimeFrom, String symbolId, String frameId);
	
	List<TradeAlert> getByFrame(Date alertTimeFrom, String frameId);
	
	List<TradeAlert> getBySymbol(Date alertTimeFrom, String symbolId);
}
