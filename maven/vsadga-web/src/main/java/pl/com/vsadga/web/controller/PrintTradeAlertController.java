package pl.com.vsadga.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.data.TradeAlert;
import pl.com.vsadga.service.alert.TradeAlertService;

@Controller
public class PrintTradeAlertController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintTradeAlertController.class);
	
	@Autowired
	private TradeAlertService tradeAlertService;
	
	@RequestMapping("/alert")
	public ModelAndView printTradeAlertList() {
		StringBuffer html_buff = new StringBuffer();
		html_buff.append("<br><br>Alert printer");
		List<TradeAlert> alert_list = null;
		
		alert_list = tradeAlertService.getActualTradeAlertList();
		
		if (alert_list.isEmpty()) {
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
		}
		
		return new ModelAndView("print-alert", "html_tab", html_buff.toString());
	}
}
