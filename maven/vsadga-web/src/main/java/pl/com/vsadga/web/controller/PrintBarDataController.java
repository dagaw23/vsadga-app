package pl.com.vsadga.web.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.web.utils.FormatUtils;

@Controller
public class PrintBarDataController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintBarDataController.class);

	@Autowired
	private ConfigDataService configDataService;

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@RequestMapping("/audusd")
	public ModelAndView processAudUsd() {
		return process("AUDUSD");
	}

	@RequestMapping("/eurusd")
	public ModelAndView processEurUsd() {
		return process("EURUSD");
	}

	@RequestMapping("/gbpaud")
	public ModelAndView processGbpAud() {
		return process("GBPAUD");
	}

	@RequestMapping("/gbpcad")
	public ModelAndView processGbpCad() {
		return process("GBPCAD");
	}

	@RequestMapping("/gbpusd")
	public ModelAndView processGbpUsd() {
		return process("GBPUSD");
	}

	@RequestMapping("/gold")
	public ModelAndView processGold() {
		return process("GOLD");

	}

	@RequestMapping("/oil")
	public ModelAndView processOil() {
		return process("OIL");
	}

	@RequestMapping("/usdcad")
	public ModelAndView processUsdCad() {
		return process("USDCAD");
	}

	@RequestMapping("/usdchf")
	public ModelAndView processUsdChf() {
		return process("USDCHF");
	}

	@RequestMapping("/usdjpy")
	public ModelAndView processUsdJpy() {
		return process("USDJPY");
	}

	private List<BarData> getBarDataList(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException,
			ParseException {
		Date to_date = null;
		Integer bar_count = null;

		// liczba barów do wyświetlenia jest wymagana:
		bar_count = getIntParamValue("VISIBILITY_BAR_COUNT", 5);

		// pobierz datę graniczną wyświetlania:
		to_date = getDateParamValue("VISIBILITY_END_DATE", "yyyy/MM/dd HH:mm");

		if (to_date == null)
			return currencyDataService.getLastNbarData(bar_count, symbol, timeFrame);
		else
			return currencyDataService.getLastNbarDataToDate(bar_count, symbol, timeFrame, to_date);

	}

	private String getErrorMessage(String symbolName, String msg) {
		LOGGER.info("   [" + symbolName + "] " + msg);

		return msg;
	}

	private String getTableContent(List<BarData> dataList, TimeFrame timeFrame) {
		StringBuffer result = new StringBuffer();

		StringBuffer indy_row = new StringBuffer();
		StringBuffer trend_row = new StringBuffer();

		StringBuffer vol_size_row = new StringBuffer();
		StringBuffer vol_trd_row = new StringBuffer();
		StringBuffer time_row = new StringBuffer();

		result.append("<table>");
		String vol_thrm = null;

		for (BarData bar_data : dataList) {
			// pomijamy nie przetworzone jeszcze do minimum 2:
			if (bar_data.getProcessPhase().intValue() < 2)
				continue;

			// indicator:
			indy_row.append("<td>");
			if (bar_data.getIndicatorNr() != null) {
				int ind_nr = bar_data.getIndicatorNr().intValue();

				if (ind_nr > 0) {

					if (ind_nr <= 10 || ind_nr == 40 || ind_nr == 58) {
						if (bar_data.getIsConfirm() != null && bar_data.getIsConfirm().booleanValue()) {
							indy_row.append("<b style='color:red;'>");
							indy_row.append(bar_data.getIndicatorNr().intValue());
							indy_row.append("</b>");
						} else {
							indy_row.append(bar_data.getIndicatorNr().intValue());
						}
					} else {
						indy_row.append(bar_data.getIndicatorNr().intValue());
					}
				}
			}
			indy_row.append("</td>");

			// trend:
			trend_row.append("<td width='20' style='background-color:");
			if (bar_data.getTrendIndicator().equals("U")) {
				trend_row.append("green");
			} else if (bar_data.getTrendIndicator().equals("D")) {
				trend_row.append("red");
			} else {
				trend_row.append("gray");
			}
			trend_row.append("'/>");

			// trend wolumenu:
			vol_trd_row.append("<td width='20' style='background-color:");
			vol_thrm = bar_data.getVolumeThermometer();

			if (vol_thrm == null) {
				vol_trd_row.append("white");
			} else if (vol_thrm.equals("U")) {
				vol_trd_row.append("lightgreen");
			} else if (vol_thrm.equals("D")) {
				vol_trd_row.append("lightcoral");
			} else if (vol_thrm.equals("S")) {
				vol_trd_row.append("lightgray");
			} else if (vol_thrm.equals("L")) {
				vol_trd_row.append("black");
			} else {
				vol_trd_row.append("white");
			}
			vol_trd_row.append("'/>");

			vol_size_row.append("<td style='font-size:6px, font-family:Arial'>").append(bar_data.getVolumeSize())
					.append("</td>");

			// minuta lub godzina bara:
			time_row.append("<td style='font-size:6px, font-family:Arial'>");
			if (timeFrame.getTimeFrameDesc().startsWith("M"))
				time_row.append(formatDate(bar_data.getBarTime(), "mm"));
			else if (timeFrame.getTimeFrameDesc().startsWith("H"))
				time_row.append(formatDate(bar_data.getBarTime(), "HH"));
			time_row.append("</td>");
		}

		// indicators:
		result.append("<tr height='10' style='font-size:11px; text-align:center'>").append(indy_row.toString())
				.append("</tr>");
		// trend cen:
		result.append("<tr height='20'>").append(trend_row.toString()).append("</tr>");
		// wolumeny:
		result.append("<tr height='10' style='font-size:11px; text-align:center'>")
				.append(vol_size_row.toString()).append("</tr>");
		result.append("<tr height='20'>").append(vol_trd_row.toString()).append("</tr>");
		// minuta/godzina:
		result.append("<tr height='10' style='font-size:11px; text-align:center'>").append(time_row.toString())
				.append("</tr>");

		result.append("</table>");

		return result.toString();
	}

	private ModelAndView process(String symbolName) {
		CurrencySymbol symbol = null;
		List<BarData> bar_list = null;
		String html_tab = "<br><b>" + symbolName + "</b><br>";

		// pobierz symbol:
		symbol = symbolService.getCurrencySymbolByName(symbolName);

		// pobierz ramki aktywne:
		List<TimeFrame> frm_list = timeFrameService.getAllActive();

		if (symbol == null || frm_list.isEmpty())
			return new ModelAndView("error-message", "error_msg", getErrorMessage(symbolName, "Brak symbolu ["
					+ symbol + "] lub pusta lista TIMEFRAME [" + frm_list.size() + "]."));

		try {
			// przetworzenie dla każdej ramki aktywnej:
			for (TimeFrame tme_frm : frm_list) {
				// pobierz listę barów:
				bar_list = getBarDataList(symbol, tme_frm);

				// czy są bary do wyświetlenia:
				if (bar_list.isEmpty())
					continue;

				// wpisz zakres data:
				html_tab += "<br><br> Time: " + tme_frm.getTimeFrameDesc() + " ["
						+ FormatUtils.formatSimpleDate(bar_list.get(0).getBarTime()) + " - "
						+ FormatUtils.formatSimpleDate(bar_list.get(bar_list.size() - 1).getBarTime()) + "] <br>";

				html_tab += getTableContent(bar_list, tme_frm);
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new ModelAndView("print-data", "html_tab", html_tab);
	}

}
