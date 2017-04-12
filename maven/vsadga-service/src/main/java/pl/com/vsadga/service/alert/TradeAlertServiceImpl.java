package pl.com.vsadga.service.alert;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.dto.alert.TradeAlertDto;

public class TradeAlertServiceImpl implements TradeAlertService {

	private TradeAlertDao tradeAlertDao;

	@Override
	public List<TradeAlertDto> getActualTradeAlertList(int dayBackLimit) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -dayBackLimit);

		return tradeAlertDao.getActualTradeAlertList(cal.getTime());
	}

	@Override
	public List<TradeAlertDto> getByFrameAndSymbol(String symbol, String frame, int dayBackLimit) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -dayBackLimit);

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
