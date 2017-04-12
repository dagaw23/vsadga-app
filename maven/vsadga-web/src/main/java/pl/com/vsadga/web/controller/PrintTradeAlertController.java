package pl.com.vsadga.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.service.alert.TradeAlertService;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.web.model.alert.AlertDataModel;

@Controller
public class PrintTradeAlertController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintTradeAlertController.class);

	@Autowired
	private TradeAlertService tradeAlertService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private ConfigDataService configDataService;

	@RequestMapping("/alert")
	public ModelAndView printTradeAlertList() {
		ModelAndView mav = new ModelAndView("print-alert");

		// lista ramek:
		mav.addObject("frameList", timeFrameService.getAllActive());

		// lista symboli:
		mav.addObject("symbolList", symbolService.getActiveSymbols());

		// konfiguracja ilości minut:
		int day_back = configDataService.getParamWithDefaultValue("TRADE_ALERT_DAY_BACK", "5");

		// lista wszystkich alertów:
		mav.addObject("alertList", tradeAlertService.getActualTradeAlertList(day_back));

		mav.addObject("alertDataModel", new AlertDataModel());

		return mav;
	}

	@RequestMapping(value = "/alert-filter", method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute AlertDataModel alertDataModel, Model model) {

		// lista ramek:
		model.addAttribute("frameList", timeFrameService.getAllActive());

		// lista symboli:
		model.addAttribute("symbolList", symbolService.getActiveSymbols());

		// konfiguracja ilości minut:
		int day_back = configDataService.getParamWithDefaultValue("TRADE_ALERT_DAY_BACK", "5");

		// lista wszystkich alertów:
		model.addAttribute(
				"alertList",
				tradeAlertService.getByFrameAndSymbol(alertDataModel.getSymbolSelected(),
						alertDataModel.getFrameSelected(), day_back));

		return "print-alert";
	}

}
