package pl.com.vsadga.service.alert;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.alert.TradeAlert;

public class TradeAlertServiceImpl implements TradeAlertService {

	private TradeAlertDao tradeAlertDao;

	@Override
	public List<TradeAlert> getActualTradeAlertList() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -5);

		return tradeAlertDao.getActualTradeAlertList(cal.getTime());
	}

	@Override
	public List<TradeAlert> getByFrameAndSymbol(String symbol, String frame) {
		// maksymalnie 5 dni wstecz:
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -5);

		if (symbol != null && !symbol.equals("ALL")) {
			if (frame != null && !frame.equals("ALL")) {
				return tradeAlertDao.getByFrameAndSymbol(cal.getTime(), symbol, frame);
			} else {
				return tradeAlertDao.getBySymbol(cal.getTime(), symbol);
			}
		} else {
			if (frame != null && !frame.equals("ALL")) {
				return tradeAlertDao.getByFrame(cal.getTime(), frame);
			} else {
				return tradeAlertDao.getActualTradeAlertList(cal.getTime());
			}
		}
	}

	/**
	 * @param tradeAlertDao
	 *            the tradeAlertDao to set
	 */
	public void setTradeAlertDao(TradeAlertDao tradeAlertDao) {
		this.tradeAlertDao = tradeAlertDao;
	}

}
