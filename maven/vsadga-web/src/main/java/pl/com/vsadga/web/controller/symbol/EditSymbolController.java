package pl.com.vsadga.web.controller.symbol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.service.symbol.SymbolService;

@Controller
public class EditSymbolController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditSymbolController.class);

	@Autowired
	private SymbolService symbolService;

	@RequestMapping("/show-symbol-list")
	public ModelAndView showSymbolList() {
		ModelAndView mav = new ModelAndView("symbol/symbol-list");

		// lista symboli:
		mav.addObject("symbolList", symbolService.getAll());

		return mav;
	}

	@RequestMapping(value = "/symbol-edit/{id}/delete", method = RequestMethod.GET)
	public String symbolDelete(@PathVariable("id") int id, Model model) {
		// usuń symbol wg ID:
		int row_del = symbolService.delete(id);

		if (row_del != 1)
			LOGGER.error("   [ERROR] Nieudane usuniecie rekordu [" + row_del + "] z tabeli CURRENCY_SYMBOL!");

		model.addAttribute("symbolList", symbolService.getAll());
		return "symbol/symbol-list";
	}

	@RequestMapping("/symbol-edit/add")
	public String symbolEditAddForm(Model model) {

		// lista symboli:
		model.addAttribute("currencySymbol", new CurrencySymbol());

		return "symbol/symbol-form-add-view";
	}

	@RequestMapping(value = "/symbol-edit-done", method = RequestMethod.POST)
	public String symbolEditForm(@ModelAttribute CurrencySymbol currencySymbol, Model model) {
		// modyfikuj rekord:
		int row_upd = symbolService.update(currencySymbol);

		if (row_upd != 1)
			LOGGER.error("   [ERROR] Nieudana aktualizacja rekordu [" + row_upd + "] w tabeli CURRENCY_SYMBOL!");

		model.addAttribute("symbolList", symbolService.getAll());
		return "symbol/symbol-list";
	}

	@RequestMapping(value = "/symbol-new-add", method = RequestMethod.POST)
	public String symbolNewForm(@ModelAttribute CurrencySymbol currencySymbol, Model model) {
		// dodaj rekord:
		int row_add = symbolService.insert(currencySymbol);

		if (row_add != 1)
			LOGGER.error("   [ERROR] Nieudane dodanie rekordu [" + row_add + "] do tabeli CURRENCY_SYMBOL!");

		model.addAttribute("symbolList", symbolService.getAll());
		return "symbol/symbol-list";
	}

	@RequestMapping(value = "/symbol-edit/{id}/update", method = RequestMethod.GET)
	public String symbolUpdate(@PathVariable("id") int id, Model model) {
		// pobierz symbol wg ID:
		CurrencySymbol symbol = symbolService.getById(id);

		model.addAttribute("currencySymbol", symbol);
		return "symbol/symbol-form-edit-view";
	}

}
