package pl.com.vsadga.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.chart.ChartWriter;

@Controller
public class PrintTradeAlertController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintTradeAlertController.class);
	
	//private TradeAlertService tradeAlertService;
	
	@Autowired
	private ChartWriter chartWriter;
	
	@RequestMapping("/alert")
	public ModelAndView printTradeAlertList() {
		StringBuffer html_buff = new StringBuffer();
		html_buff.append("<br><br>Alert printer");
		
		//List<TradeAlert> alert_list = null;
		//alert_list = tradeAlertService.getActualTradeAlertList();
		
		CurrencySymbol symbol = new CurrencySymbol();
		symbol.setIsActive(true);
		symbol.setSymbolName("GOLD");
		
		TimeFrame timeFrame = new TimeFrame();
		timeFrame.setIsActive(true);
		timeFrame.setTimeFrame(5);
		timeFrame.setTimeFrameDesc("M5");
		
		try {
			chartWriter.print(symbol, timeFrame);
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		/*if (alert_list.isEmpty()) {
			html_buff.append("<br>Brak alertow.");
		} else {
			html_buff.append("<table>");
			
			for (TradeAlert alert : alert_list) {
				html_buff.append("<tr><td>");
				html_buff.append(alert.getAlertMessage());
				html_buff.append("</td><td>");
				html_buff.append(formatDate(alert.getAlertTime(), "yy/MM/dd HH:mm"));
				html_buff.append("</td></tr>");
			}
			
			html_buff.append("</table>");
		}*/
		
		return new ModelAndView("print-alert", "html_tab", html_buff.toString());
	}
}
