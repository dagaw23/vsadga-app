package pl.com.vsadga.service.process.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.TimeFrameDao;
import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.alert.AlertType;
import pl.com.vsadga.dto.alert.VolumeAlert;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.TradeAlertProcessor;
import pl.com.vsadga.utils.DateConverter;

public class TradeAlertProcessorImpl implements TradeAlertProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeAlertProcessorImpl.class);

	private BarDataDao barDataDao;

	private TradeAlertDao tradeAlertDao;

	private TimeFrameDao timeFrameDao;


	private void addVolumeTradeAlert(CurrencySymbol symbol, TimeFrame timeFrame, Date barTime, String barStatus,
			AlertType alertType) {
		String message = "Zwiekszony wolumen dla symbolu [" + symbol.getSymbolName() + "] i ramki ["
				+ timeFrame.getTimeFrameDesc() + "] dla bara ["
				+ DateConverter.dateToString(barTime, "yyyyMMdd-HH.mm") + "]   <<"
				+ DateConverter.dateToString(new Date(), "yyyyMMdd-HH.mm.ss") + ">>";

		tradeAlertDao.insert(message, alertType, symbol.getId(), timeFrame.getId(), barTime, barStatus);
	}

	@Override
	public void checkVolumeSize(CurrencySymbol symbol, List<VolumeAlert> volumeAlertList)
			throws BaseServiceException {
		BarData bar_data = null;
		TimeFrame time_frame = null;
		Integer trade_alert_id = null;
		int max_volume = 0;

		// tylko bary zakończone już:
		for (VolumeAlert vol_alert : volumeAlertList) {

			// pobierz ostatni bar zakończony:
			bar_data = barDataDao.getLastEndedBarData(symbol.getId(), vol_alert.getTimeFrameDesc());
			if (bar_data == null)
				continue;

			time_frame = timeFrameDao.getByTimeFrameDesc(vol_alert.getTimeFrameDesc());
			if (time_frame == null) {
				LOGGER.error("   [ALERT] Brak ramki czasowej [" + time_frame + "] wg ["
						+ vol_alert.getTimeFrameDesc() + "]!");
				continue;
			}

			// pobierz alert dla aktualnego bara:
			trade_alert_id = tradeAlertDao.exist(symbol.getId(), time_frame.getId(), bar_data.getBarTime(),
					AlertType.VOLUME);

			if (trade_alert_id != null)
				continue;

			// pobierz maksymalny wolumen z N barów wstecz:
			max_volume = barDataDao.getMaxVolume(symbol.getId(), vol_alert.getTimeFrameDesc(),
					bar_data.getBarTime(), vol_alert.getLimitSize());

			if (bar_data.getBarVolume().intValue() > max_volume)
				addVolumeTradeAlert(symbol, time_frame, bar_data.getBarTime(), "E", AlertType.VOLUME);
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

	/**
	 * @param timeFrameDao
	 *            the timeFrameDao to set
	 */
	public void setTimeFrameDao(TimeFrameDao timeFrameDao) {
		this.timeFrameDao = timeFrameDao;
	}

	/*
	 * private void checkTradeAlert(CurrencySymbol symbol, List<TimeFrame> timeFrameList) throws
	 * BaseServiceException { BarData bar_data = null; String[] trend_tab = new String[4]; String
	 * msg = null;
	 * 
	 * for (TimeFrame tme_frm : timeFrameList) { bar_data =
	 * barDataDao.getLastProcessBarData(symbol.getId(), tme_frm.getTimeFrameDesc());
	 * 
	 * if (bar_data == null) { LOGGER.info("   [TRADE] Brak bara wg symbolu [" +
	 * symbol.getSymbolName() + "] i ramy czasowej [" + tme_frm.getTimeFrameDesc() +
	 * "] - koniec sprawdzania."); return; }
	 * 
	 * // zapisanie wskaźnika trendu: if (tme_frm.getTimeFrame().intValue() == 5) trend_tab[0] =
	 * bar_data.getTrendIndicator(); else if (tme_frm.getTimeFrame().intValue() == 15) trend_tab[1]
	 * = bar_data.getTrendIndicator(); else if (tme_frm.getTimeFrame().intValue() == 60)
	 * trend_tab[2] = bar_data.getTrendIndicator(); else if (tme_frm.getTimeFrame().intValue() ==
	 * 240) trend_tab[3] = bar_data.getTrendIndicator(); }
	 * 
	 * if (trend_tab[0] == null || trend_tab[1] == null || trend_tab[2] == null || trend_tab[3] ==
	 * null) { LOGGER.info("   [TRADE] Symbol [" + symbol.getSymbolName() +
	 * "] jeszcze nie gotowy do sprawdzenia trade'u [" + trend_tab[0] + "," + trend_tab[1] + "," +
	 * trend_tab[2] + "," + trend_tab[3] + "]."); return; }
	 * 
	 * if (trend_tab[0].equals(trend_tab[1]) && trend_tab[1].equals(trend_tab[2]) &&
	 * trend_tab[2].equals(trend_tab[3])) { msg = "READY to trade [" + symbol.getSymbolName() +
	 * "] - ";
	 * 
	 * // bary muszą być U lub D: String bar_typ = trend_tab[3]; if (!bar_typ.equals("U") &&
	 * !bar_typ.equals("D")) return;
	 * 
	 * if (bar_typ.equals("U")) msg += "UP trade.";
	 * 
	 * if (bar_typ.equals("D")) msg += "DOWN trade.";
	 * 
	 * tradeAlertDao.insert(msg, symbol.getId()); return; }
	 * 
	 * if (trend_tab[1].equals(trend_tab[2]) && trend_tab[2].equals(trend_tab[3])) { msg =
	 * "WATCH to trade [" + symbol.getSymbolName() + "] - ";
	 * 
	 * // bary muszą być U lub D: String bar_typ = trend_tab[3]; if (!bar_typ.equals("U") &&
	 * !bar_typ.equals("D")) return;
	 * 
	 * if (bar_typ.equals("U")) msg += "UP trade.";
	 * 
	 * if (bar_typ.equals("D")) msg += "DOWN trade.";
	 * 
	 * tradeAlertDao.insert(msg, symbol.getId()); return; }
	 * 
	 * if (trend_tab[0].equals(trend_tab[1]) && trend_tab[1].equals(trend_tab[2])) { msg =
	 * "SCALP trade [" + symbol.getSymbolName() + "] - ";
	 * 
	 * // bary muszą być U lub D: String bar_typ = trend_tab[2]; if (!bar_typ.equals("U") &&
	 * !bar_typ.equals("D")) return;
	 * 
	 * if (bar_typ.equals("U")) msg += "UP trade.";
	 * 
	 * if (bar_typ.equals("D")) msg += "DOWN trade.";
	 * 
	 * tradeAlertDao.insert(msg, symbol.getId()); return; }
	 * 
	 * }
	 */
}
