package pl.com.vsadga.web.controller.bar.data;

import java.util.ArrayList;
import java.util.List;

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

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.bar.BarSimpleDto;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.web.controller.BaseController;
import pl.com.vsadga.web.dto.FrameDto;
import pl.com.vsadga.web.dto.SymbolDto;
import pl.com.vsadga.web.model.alert.AlertDataModel;
import pl.com.vsadga.web.model.bar.data.BarDataModel;
import pl.com.vsadga.web.model.bar.data.BarInfoModel;

@Controller
public class BarDataController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BarDataController.class);
	
	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;
	
	@Autowired
	private CurrencyDataService currencyDataService;
	
	@RequestMapping("/show-bar-data-list")
	public ModelAndView showSymbolList() {
		ModelAndView mav = new ModelAndView("bar-data/bar-data-list");

		// lista symboli:
		mav.addObject("symbolList", symbolService.getActiveSymbols());
		
		// lista ramek:
		mav.addObject("frameList", timeFrameService.getAllActive());
		
		mav.addObject("barDataModel", new BarDataModel());
		
		return mav;
	}
	
	@RequestMapping(value = "/bar-data-filter", method = RequestMethod.POST)
	public String barDataFilter(@ModelAttribute BarDataModel barDataModel, Model model) {
		Integer row_id = 0;
		
		// lista symboli:
		model.addAttribute("symbolList", symbolService.getActiveSymbols());
		
		// lista ramek:
		model.addAttribute("frameList", timeFrameService.getAllActive());
		
		// lista danych:
		List<BarData> data_list = currencyDataService.getPartialData(Integer.valueOf(barDataModel.getSymbolId()), barDataModel.getFrame(), 20, null);
		
		if (data_list.size() > 0)
			row_id = data_list.get(data_list.size()-1).getId();
		else
			row_id = 0;
		
		model.addAttribute("barDataList", convert(data_list));
		model.addAttribute("rowId", row_id);
		
		return "bar-data/bar-data-list";
	}
	
	@RequestMapping(value = "/bar-data/{id}/{symbolId}/{frame}/next", method = RequestMethod.GET)
	public String barDataNext(@PathVariable("id") Integer id, @PathVariable("symbolId") Integer symbolId, @PathVariable("frame") String frame, Model model) {
		Integer row_id = 0;
		
		// lista symboli:
		model.addAttribute("symbolList", symbolService.getActiveSymbols());
		
		// lista ramek:
		model.addAttribute("frameList", timeFrameService.getAllActive());
		
		// lista danych:
		List<BarData> data_list = currencyDataService.getPartialData(symbolId, frame, 20, id);
		
		if (data_list.size() > 0)
			row_id = data_list.get(data_list.size()-1).getId();
		else
			row_id = 0;
		
		model.addAttribute("barDataList", convert(data_list));
		model.addAttribute("rowId", row_id);
		model.addAttribute("barDataModel", new BarDataModel(frame, symbolId));
		
		
		return "bar-data/bar-data-list";
	}
	
	private List<BarInfoModel> convert(List<BarData> dataList) {
		List<BarInfoModel> result = new ArrayList<BarInfoModel>();
		
		for (BarData data : dataList)
			result.add(new BarInfoModel(data));
		
		return result;
	}
	
	@RequestMapping(value = "/get-bar-data", method = RequestMethod.GET)
	public String getForm(Model model) {
		List<CurrencySymbol> sym_list = symbolService.getActiveSymbols();
		List<TimeFrame> frm_list = timeFrameService.getByTime(5, 1440);
		
		List<SymbolDto> symbol_list = new ArrayList<SymbolDto>();
		List<FrameDto> timeFrame_list = new ArrayList<FrameDto>();
		SymbolDto symbol = null;
		FrameDto frame = null;
		
		for (CurrencySymbol sym : sym_list) {
			symbol = new SymbolDto(sym.getId(), sym.getSymbolName());
			symbol_list.add(symbol);
		}
		
		for (TimeFrame frm : frm_list) {
			frame = new FrameDto(frm.getId(), frm.getTimeFrameDesc());
			timeFrame_list.add(frame);
		}
		
		BarDataModel barDataModel = new BarDataModel();
		model.addAttribute("barDataModel", barDataModel);
		model.addAttribute("symbolList", symbol_list);
		model.addAttribute("frameList", timeFrame_list);
		
		return "bar-data/get-data-view";
	}
	
	@RequestMapping(value = "/get-bar-data", method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute BarDataModel barDataModel, Model model) {
		List<BarSimpleDto> data_list = null;
		
		try {
			// pobierz dane wg czasu i waloru:
			data_list = currencyDataService.getLastNbarData(50, Integer.valueOf(barDataModel.getSymbolId()), barDataModel.getFrame());
		} catch (NumberFormatException | BaseServiceException e) {
			e.printStackTrace();
		}
		
		//model.addAttribute("barDataModel", barDataModel);
		model.addAttribute("dataList", data_list);
		
		return "bar-data/show-data-view";
	}

}
