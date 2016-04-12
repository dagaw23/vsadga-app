package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.TradeAlert;



public interface TradeAlertDao {

	int insert(String alertMessage, Integer symbolId);
	
	List<TradeAlert> getActualTradeAlertList(Date alertTimeFrom);
}
