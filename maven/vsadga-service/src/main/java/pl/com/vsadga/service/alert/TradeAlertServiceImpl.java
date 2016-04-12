package pl.com.vsadga.service.alert;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.TradeAlert;

public class TradeAlertServiceImpl implements TradeAlertService {

	private TradeAlertDao tradeAlertDao;

	/**
	 * @param tradeAlertDao the tradeAlertDao to set
	 */
	public void setTradeAlertDao(TradeAlertDao tradeAlertDao) {
		this.tradeAlertDao = tradeAlertDao;
	}

	@Override
	public List<TradeAlert> getActualTradeAlertList() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, -1);
		
		return tradeAlertDao.getActualTradeAlertList(cal.getTime());
	}
	
	
}
