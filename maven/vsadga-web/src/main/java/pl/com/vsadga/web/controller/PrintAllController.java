package pl.com.vsadga.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;

@Controller
public class PrintAllController {

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@RequestMapping("/all")
	public ModelAndView process() {
		List<BarData> bar_list = null;
		List<CurrencySymbol> sym_list = symbolService.getActiveSymbols();
		List<TimeFrame> frm_list = timeFrameService.getAllActive();
		String html_tab = "";

		try {
			for (CurrencySymbol sym : sym_list) {
				for (TimeFrame frame : frm_list) {
					html_tab += "<br><br> Symbol:" + sym.getSymbolName() + " in " + frame.getTimeFrameDesc() + ": <br>";
					bar_list = currencyDataService.getLastNbarData(20, sym, frame);

					html_tab += getMessage(bar_list);
				}
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}

		return new ModelAndView("print-all", "html_tab", html_tab);
	}
	
	private String getMessage(List<BarData> dataList) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		StringBuffer row1 = new StringBuffer();
		StringBuffer row2 = new StringBuffer();
		StringBuffer row3 = new StringBuffer();
		result.append("<table><tr height='20'>");
		
		for (BarData bar_data : dataList) {
			row1.append("<td width='20' style='background-color:"); 
			
			if (bar_data.getTrendIndicator().equals("U")) {
				row1.append("green");
			} else if (bar_data.getTrendIndicator().equals("D")) {
				row1.append("red");
			} else {
				row1.append("gray");
			}
			row1.append("'/>");
			
			row2.append("<td style='font-size:6px, font-family:Arial'>").append(bar_data.getBarClose()).append("</td>");
			row3.append("<td style='font-size:6px, font-family:Arial'>").append(bar_data.getBarVolume()).append("</td>");
			
			i++;
			
			if (i >= 20)
				break;
		}
		
		result.append(row1.toString());
		result.append("</tr><tr height='10'>");
		result.append(row2.toString());
		result.append("</tr><tr height='10'>");
		result.append(row3.toString());
		result.append("</tr></table>");
		
		return result.toString();
	}
}
