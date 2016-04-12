package pl.com.vsadga.service.process.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.TradeAlertProcessor;

public class TradeAlertProcessorImpl implements TradeAlertProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeAlertProcessorImpl.class);

	private BarDataDao barDataDao;

	private TradeAlertDao tradeAlertDao;

	@Override
	public void checkTradeAlert(CurrencySymbol symbol, List<TimeFrame> timeFrameList) throws BaseServiceException {
		BarData bar_data = null;
		String[] trend_tab = new String[4];
		String msg = null;

		for (TimeFrame tme_frm : timeFrameList) {
			bar_data = barDataDao.getLastProcessBarData(symbol.getId(), tme_frm.getTimeFrameDesc());

			if (bar_data == null) {
				LOGGER.info("   [TRADE] Brak bara wg symbolu [" + symbol.getSymbolName() + "] i ramy czasowej ["
						+ tme_frm.getTimeFrameDesc() + "] - koniec sprawdzania.");
				return;
			}

			// zapisanie wskaźnika trendu:
			if (tme_frm.getTimeFrame().intValue() == 5)
				trend_tab[0] = bar_data.getTrendIndicator();
			else if (tme_frm.getTimeFrame().intValue() == 15)
				trend_tab[1] = bar_data.getTrendIndicator();
			else if (tme_frm.getTimeFrame().intValue() == 60)
				trend_tab[2] = bar_data.getTrendIndicator();
			else if (tme_frm.getTimeFrame().intValue() == 240)
				trend_tab[3] = bar_data.getTrendIndicator();
		}

		if (trend_tab[0] == null || trend_tab[1] == null || trend_tab[2] == null || trend_tab[3] == null) {
			LOGGER.info("   [TRADE] Symbol [" + symbol.getSymbolName()
					+ "] jeszcze nie gotowy do sprawdzenia trade'u [" + trend_tab[0] + "," + trend_tab[1] + ","
					+ trend_tab[2] + "," + trend_tab[3] + "].");
			return;
		}

		if (trend_tab[0].equals(trend_tab[1]) && trend_tab[1].equals(trend_tab[2])
				&& trend_tab[2].equals(trend_tab[3])) {
			msg = "READY to trade [" + symbol.getSymbolName() + "] - ";

			if (trend_tab[0].equals("U"))
				msg += "UP trade.";

			if (trend_tab[0].equals("D"))
				msg += "DOWN trade.";

			tradeAlertDao.insert(msg, symbol.getId());
			return;
		}

		if (trend_tab[1].equals(trend_tab[2]) && trend_tab[2].equals(trend_tab[3])) {
			msg = "WATCH to trade [" + symbol.getSymbolName() + "] - ";

			if (trend_tab[0].equals("U"))
				msg += "UP trade.";

			if (trend_tab[0].equals("D"))
				msg += "DOWN trade.";

			tradeAlertDao.insert(msg, symbol.getId());
			return;
		}

	}

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	/**
	 * @param tradeAlertDao
	 *            the tradeAlertDao to set
	 */
	public void setTradeAlertDao(TradeAlertDao tradeAlertDao) {
		this.tradeAlertDao = tradeAlertDao;
	}

}
