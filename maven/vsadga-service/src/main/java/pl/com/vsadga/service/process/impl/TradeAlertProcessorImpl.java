package pl.com.vsadga.service.process.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.TradeAlert;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.TradeAlertProcessor;

public class TradeAlertProcessorImpl implements TradeAlertProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeAlertProcessorImpl.class);

	private BarDataDao barDataDao;

	private TradeAlertDao tradeAlertDao;

	/*
	private void checkTradeAlert(CurrencySymbol symbol, List<TimeFrame> timeFrameList) throws BaseServiceException {
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

			// bary muszą być U lub D:
			String bar_typ = trend_tab[3];
			if (!bar_typ.equals("U") && !bar_typ.equals("D"))
				return;

			if (bar_typ.equals("U"))
				msg += "UP trade.";

			if (bar_typ.equals("D"))
				msg += "DOWN trade.";

			tradeAlertDao.insert(msg, symbol.getId());
			return;
		}

		if (trend_tab[1].equals(trend_tab[2]) && trend_tab[2].equals(trend_tab[3])) {
			msg = "WATCH to trade [" + symbol.getSymbolName() + "] - ";

			// bary muszą być U lub D:
			String bar_typ = trend_tab[3];
			if (!bar_typ.equals("U") && !bar_typ.equals("D"))
				return;

			if (bar_typ.equals("U"))
				msg += "UP trade.";

			if (bar_typ.equals("D"))
				msg += "DOWN trade.";

			tradeAlertDao.insert(msg, symbol.getId());
			return;
		}

		if (trend_tab[0].equals(trend_tab[1]) && trend_tab[1].equals(trend_tab[2])) {
			msg = "SCALP trade [" + symbol.getSymbolName() + "] - ";

			// bary muszą być U lub D:
			String bar_typ = trend_tab[2];
			if (!bar_typ.equals("U") && !bar_typ.equals("D"))
				return;

			if (bar_typ.equals("U"))
				msg += "UP trade.";

			if (bar_typ.equals("D"))
				msg += "DOWN trade.";

			tradeAlertDao.insert(msg, symbol.getId());
			return;
		}

	}
	*/
	
	private void addVolumeTradeAlert(TradeAlert tradeAlert, int volSize, CurrencySymbol symbol, TimeFrame timeFrame, Date barTime, String barStatus) {
		String message = "Alert wolumenowy [";
		
		if (volSize == 1)
			message += "HI";
		else if (volSize == 2)
			message += "VERY HI";
		else if (volSize == 3)
			message += "EXTREMELY";
		
		message += "] dla [" + symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc() + "].";
		
		if (tradeAlert == null)
			tradeAlertDao.insert(message, symbol.getId(), timeFrame.getId(), barTime, barStatus);
		else
			tradeAlertDao.update(tradeAlert.getId(), message, barStatus);
		
	}

	@Override
	public void checkVolumeSize(CurrencySymbol symbol, List<TimeFrame> timeFrameList) throws BaseServiceException {
		BarData bar_data = null;
		TradeAlert trade_alert = null;
		int max_1 = 0;
		int max_2 = 0;
		int max_3 = 0;
		int bar_vol = 0;
		int vol_typ = 0;
		
		// 1) bary zakończone już:
		for (TimeFrame tme_frm : timeFrameList) {
			max_1 = 0;
			max_2 = 0;
			max_3 = 0;
			vol_typ = 0;
			
			bar_data = barDataDao.getLastEndedBarData(symbol.getId(), tme_frm.getTimeFrameDesc());
			bar_vol = bar_data.getBarVolume().intValue();
			trade_alert = tradeAlertDao.exist(symbol.getId(), tme_frm.getId(), bar_data.getBarTime());
			
			// a) czy dany bar - ma już alert:
			if (trade_alert == null || trade_alert.getBarStatus().equals("T")) {
				max_1 = barDataDao.getMaxVolume(symbol.getId(), tme_frm.getTimeFrameDesc(), bar_data.getBarTime(), 50);
				max_2 = barDataDao.getMaxVolume(symbol.getId(), tme_frm.getTimeFrameDesc(), bar_data.getBarTime(), 100);
				max_3 = barDataDao.getMaxVolume(symbol.getId(), tme_frm.getTimeFrameDesc(), bar_data.getBarTime(), 300);
				
				if (bar_vol > max_3)
					vol_typ = 3;
				else if (bar_vol > max_2)
					vol_typ = 2;
				else if (bar_vol > max_1)
					vol_typ = 1;
				else
					vol_typ = 0;
				
				if (vol_typ > 0)
					addVolumeTradeAlert(trade_alert, vol_typ, symbol, tme_frm, bar_data.getBarTime(), "E");
			}
		}
		
		// 2) bary jeszcze nie zakończone:
		for (TimeFrame tme_frm : timeFrameList) {
			max_1 = 0;
			max_2 = 0;
			max_3 = 0;
			vol_typ = 0;
			
			bar_data = barDataDao.getLastNotEndedBarData(symbol.getId(), tme_frm.getTimeFrameDesc());
			bar_vol = bar_data.getBarVolume().intValue();
			trade_alert = tradeAlertDao.exist(symbol.getId(), tme_frm.getId(), bar_data.getBarTime());
			
			// a) wyliczenie poziomów:
			max_1 = barDataDao.getMaxVolume(symbol.getId(), tme_frm.getTimeFrameDesc(), bar_data.getBarTime(), 50);
			max_2 = barDataDao.getMaxVolume(symbol.getId(), tme_frm.getTimeFrameDesc(), bar_data.getBarTime(), 100);
			max_3 = barDataDao.getMaxVolume(symbol.getId(), tme_frm.getTimeFrameDesc(), bar_data.getBarTime(), 300);
			
			if (bar_vol > max_3)
				vol_typ = 3;
			else if (bar_vol > max_2)
				vol_typ = 2;
			else if (bar_vol > max_1)
				vol_typ = 1;
			else
				vol_typ = 0;
			
			if (vol_typ > 0)
				addVolumeTradeAlert(trade_alert, vol_typ, symbol, tme_frm, bar_data.getBarTime(), "T");
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
