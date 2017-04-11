package pl.com.vsadga.web.controller.frame;

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
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.timeframe.TimeFrameService;

@Controller
public class EditTimeFrameController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EditTimeFrameController.class);
	
	@Autowired
	private TimeFrameService timeFrameService;
	
	@RequestMapping("/show-frame-list")
	public ModelAndView showFrameList() {
		ModelAndView mav = new ModelAndView("frame/frame-list");

		// lista ramek czasowych:
		mav.addObject("frameList", timeFrameService.getAll());

		return mav;
	}
	
	@RequestMapping(value = "/frame-edit/{id}/update", method = RequestMethod.GET)
	public String frameUpdate(@PathVariable("id") int id, Model model) {
		// pobierz frame wg ID:
		TimeFrame tme_frm = timeFrameService.getById(id);

		model.addAttribute("timeFrame", tme_frm);
		return "frame/frame-form-edit-view";
	}
	
	@RequestMapping(value = "/frame-edit-done", method = RequestMethod.POST)
	public String symbolEditForm(@ModelAttribute TimeFrame timeFrame, Model model) {
		// modyfikuj rekord:
		int row_upd = timeFrameService.update(timeFrame);

		if (row_upd != 1)
			LOGGER.error("   [ERROR] Nieudana aktualizacja rekordu [" + row_upd + "] w tabeli TIME_FRAME!");

		model.addAttribute("frameList", timeFrameService.getAll());
		return "frame/frame-list";
	}


}
