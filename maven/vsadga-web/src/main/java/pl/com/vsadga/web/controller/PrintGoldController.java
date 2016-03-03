package pl.com.vsadga.web.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import pl.com.vsadga.utils.DateConverter;

@Controller
public class PrintGoldController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintGoldController.class);

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private ConfigDataService configDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@RequestMapping("/gold")
	public ModelAndView process() {
		CurrencySymbol symbol = null;
		List<BarData> bar_list = null;
		String html_tab = "<br><b>GOLD</b><br>";

		// pobierz symbol:
		symbol = symbolService.getCurrencySymbolByName("GOLD");

		// pobierz ramki aktywne:
		List<TimeFrame> frm_list = timeFrameService.getAllActive();

		if (symbol == null || frm_list.isEmpty())
			return new ModelAndView("error-message", "error_msg", getErrorMessage("GOLD", "Brak symbolu ["
					+ symbol + "] lub pusta lista TIMEFRAME [" + frm_list.size() + "]."));

		try {
			// przetworzenie dla każdej ramki aktywnej:
			for (TimeFrame tme_frm : frm_list) {
				html_tab += "<br><br> Time:" + tme_frm.getTimeFrameDesc() + ": <br>";

				// pobierz listę barów:
				bar_list = getBarDataList(symbol, tme_frm);

				html_tab += getTableContent(bar_list);
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new ModelAndView("print-data", "html_tab", html_tab);
	}

	private String getTableContent(List<BarData> dataList) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		
		StringBuffer indy_row = new StringBuffer();
		StringBuffer trend_row = new StringBuffer();
		
		StringBuffer cls_row = new StringBuffer();
		StringBuffer vol_row = new StringBuffer();
		StringBuffer ima_row = new StringBuffer();
		
		StringBuffer time_row = new StringBuffer();
		StringBuffer vol_trd_row = new StringBuffer();
		
		result.append("<table>");

		for (BarData bar_data : dataList) {
			// pomijamy nie przetworzone jeszcze do minimum 2:
			if (bar_data.getProcessPhase().intValue() < 2)
				continue;
			
			// indicator:
			indy_row.append("<td>");
			if (bar_data.getIndicatorNr() != null && bar_data.getIndicatorNr().intValue() > 0)
				indy_row.append(bar_data.getIndicatorNr().intValue());
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
			if (bar_data.getVolumeThermometer().equals("U")) {
				vol_trd_row.append("green");
			} else if (bar_data.getVolumeThermometer().equals("D")) {
				vol_trd_row.append("red");
			} else if (bar_data.getVolumeThermometer().equals("S")) {
				vol_trd_row.append("gray");
			} else if (bar_data.getVolumeThermometer().equals("L")) {
				vol_trd_row.append("black");
			} else {
				vol_trd_row.append("white");
			}
			vol_trd_row.append("'/>");

			cls_row.append("<td style='font-size:6px, font-family:Arial'>").append(bar_data.getBarClose())
					.append("</td>");
			vol_row.append("<td style='font-size:6px, font-family:Arial'>").append(bar_data.getBarVolume())
					.append("</td>");
			time_row.append("<td style='font-size:4px, font-family:Arial'>")
					.append(DateConverter.dateToString(bar_data.getBarTime(), "dd HH:mm")).append("</td>");
			ima_row.append("<td style='font-size:6px, font-family:Arial'>").append(bar_data.getImaCount())
					.append("</td>");
			i++;
		}

		// indicators:
		result.append("<tr height='10' style='font-size:6px'>").append(indy_row.toString()).append("</tr>");
		// trend cen:
		result.append("<tr height='20'>").append(trend_row.toString()).append("</tr>");
		// zamknięcie, wolumen, ima:
		result.append("<tr height='10'>").append(cls_row.toString()).append("</tr>");
		result.append("<tr height='10'>").append(vol_row.toString()).append("</tr>");
		result.append("<tr height='10'>").append(ima_row.toString()).append("</tr>");
		// godzina:
		result.append("<tr height='10'>").append(time_row.toString()).append("</tr>");
		result.append("<tr height='20'>").append(vol_trd_row.toString()).append("</tr>");
		
		result.append("</table>");

		return result.toString();
	}

	private List<BarData> getBarDataList(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException, ParseException {
		Date from_time = null;
		Integer bar_count = null;

		// liczba barów do wyświetlenia jest wymagana:
		bar_count = getIntParamValue("VISIBILITY_BAR_COUNT", 5);

		// pobierz datę graniczną wyświetlania:
		from_time = getDateParamValue("VISIBILITY_END_DATE", "yyyy/MM/dd HH:mm");

		if (from_time == null)
			return currencyDataService.getLastNbarData(bar_count, symbol, timeFrame);
		else
			return currencyDataService.getLastNbarDataFromTime(bar_count, symbol, timeFrame, from_time);

	}

	private String getErrorMessage(String symbolName, String msg) {
		LOGGER.info("   [" + symbolName + "] " + msg);

		return msg;
	}
}
