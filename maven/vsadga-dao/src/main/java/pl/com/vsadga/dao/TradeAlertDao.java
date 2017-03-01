package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.TradeAlert;



public interface TradeAlertDao {

	int insert(String alertMessage, Integer symbolId, Integer timeFrameId, Date barTime, String barStatus);
	
	int update(Integer id, String alertMessage, String barStatus);
	
	List<TradeAlert> getActualTradeAlertList(Date alertTimeFrom);
	
	TradeAlert exist(Integer symbolId, Integer timeFrameId, Date barTime);
}
