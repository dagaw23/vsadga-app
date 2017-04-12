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

import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.timeframe.TimeFrameService;

@Controller
public class EditTimeFrameController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditTimeFrameController.class);

	@Autowired
	private TimeFrameService timeFrameService;

	@RequestMapping(value = "/frame-edit/{id}/delete", method = RequestMethod.GET)
	public String frameDelete(@PathVariable("id") int id, Model model) {
		// usuń ramkę wg ID:
		int row_del = timeFrameService.delete(id);

		if (row_del != 1)
			LOGGER.error("   [ERROR] Nieudane usuniecie rekordu [" + row_del + "] z tabeli TIME_FRAME wg ID ["
					+ id + "]!");

		model.addAttribute("frameList", timeFrameService.getAll());
		return "frame/frame-list";
	}

	@RequestMapping("/frame-edit/add")
	public String frameEditAddForm(Model model) {

		// lista symboli:
		model.addAttribute("timeFrameRecord", new TimeFrame());

		return "frame/frame-form-add-view";
	}

	@RequestMapping(value = "/frame-edit-done", method = RequestMethod.POST)
	public String frameEditForm(@ModelAttribute TimeFrame timeFrame, Model model) {
		// modyfikuj rekord:
		int row_upd = timeFrameService.update(timeFrame);

		if (row_upd != 1)
			LOGGER.error("   [ERROR] Nieudana aktualizacja rekordu [" + row_upd + "] w tabeli TIME_FRAME!");

		model.addAttribute("frameList", timeFrameService.getAll());
		return "frame/frame-list";
	}

	@RequestMapping(value = "/frame-edit/{id}/update", method = RequestMethod.GET)
	public String frameUpdate(@PathVariable("id") int id, Model model) {
		// pobierz frame wg ID:
		TimeFrame tme_frm = timeFrameService.getById(id);

		model.addAttribute("timeFrameRecord", tme_frm);
		return "frame/frame-form-edit-view";
	}

	@RequestMapping("/show-frame-list")
	public ModelAndView showFrameList() {
		ModelAndView mav = new ModelAndView("frame/frame-list");

		// lista ramek czasowych:
		mav.addObject("frameList", timeFrameService.getAll());

		return mav;
	}

	@RequestMapping(value = "/frame-new-add", method = RequestMethod.POST)
	public String symbolNewForm(@ModelAttribute TimeFrame timeFrameRecord, Model model) {
		// dodaj rekord:
		int row_add = timeFrameService.insert(timeFrameRecord);

		if (row_add != 1)
			LOGGER.error("   [ERROR] Nieudane dodanie rekordu [" + row_add + "] do tabeli TIME_FRAME ["
					+ timeFrameRecord + "]!");

		model.addAttribute("frameList", timeFrameService.getAll());
		return "frame/frame-list";
	}

}
