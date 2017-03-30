package pl.com.vsadga.web.controller.symbol;

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
	
	@Autowired
	private SymbolService symbolService;

	@RequestMapping("/show-symbol-list")
	public ModelAndView showSymbolList() {
		ModelAndView mav = new ModelAndView("symbol/symbol-list");
		
		// lista symboli:
		mav.addObject("symbolList", symbolService.getAll());
		
		return mav;
	}
	
	@RequestMapping(value = "/symbol-edit/{id}/update", method = RequestMethod.GET)
	public String symbolUpdate(@PathVariable("id") int id, Model model) {
		// pobierz symbol wg ID:
		CurrencySymbol symbol = symbolService.getById(id);
		
		model.addAttribute("currencySymbol", symbol);
		model.addAttribute("buttonName", "Modyfikuj");
		
		return "symbol/symbol-form-view";
	}
	
	@RequestMapping(value = "/symbol-edit-done", method = RequestMethod.POST)
	public String symbolEditForm(@ModelAttribute CurrencySymbol currencySymbol, Model model) {
		// modyfikuj rekord:
		int row_upd = symbolService.update(currencySymbol);
		
		model.addAttribute("symbolList", symbolService.getAll());
		
		return "symbol/symbol-list";
	}
	
	
	
	@RequestMapping("/symbol-edit/add")
	public String symbolEditAddForm(Model model) {
		
		// lista symboli:
		model.addAttribute("currencySymbol", new CurrencySymbol());
		model.addAttribute("buttonName", "Dodaj");
		
		return "symbol/symbol-form-view";
	}

}
