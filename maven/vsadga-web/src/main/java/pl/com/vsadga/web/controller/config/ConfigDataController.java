package pl.com.vsadga.web.controller.config;

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

import pl.com.vsadga.data.ConfigData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.web.controller.BaseController;
import pl.com.vsadga.web.model.config.ConfigDataModel;

@Controller
public class ConfigDataController extends BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDataController.class);
	
	@Autowired
	private ConfigDataService configDataService;

	@RequestMapping(value = "/get-config-data", method = RequestMethod.GET)
	public String getForm(Model model) {
		List<ConfigData> data_list = null;
		
		try {
			data_list = configDataService.getAll();
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		ConfigDataModel configDataModel = new ConfigDataModel();
		model.addAttribute("configDataModel", configDataModel);
		model.addAttribute("configList", data_list);
		
		return "config/get-data-view";
	}
	
	@RequestMapping(value = "/get-config-data", method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute ConfigDataModel configDataModel, Model model) {
		List<ConfigData> data_list = null;
		
		try {
			// pobierz dane wg czasu i waloru:
			data_list = configDataService.getAll();
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		//model.addAttribute("barDataModel", barDataModel);
		model.addAttribute("dataList", data_list);
		
		return "config/show-data-view";
	}
	
	@RequestMapping(value = "/show-config-data")
	public String getConfigParamsList(Model model) {
		List<ConfigData> data_list = null;
		
		try {
			data_list = configDataService.getAll();
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("configList", data_list);
		
		return "config/show-data-view";
	}
	
	@RequestMapping(value = "/config-data/{id}/update", method = RequestMethod.GET)
	public String showUpdateConfigForm(@PathVariable("id") int id, Model model) {
		ConfigData conf_data = null;
		
		try {
			// pobierz parametr wg ID:
			conf_data = configDataService.get(id);
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		ConfigDataModel data_model = new ConfigDataModel();
		data_model.setParamId(conf_data.getId());
		
		model.addAttribute("configData", conf_data);
		model.addAttribute("configDataModel", data_model);
		
		return "config/data-form-view";
	}
	
	@RequestMapping(value = "/update-config-data", method = RequestMethod.POST)
	public String updateConfigForm(@ModelAttribute ConfigDataModel configDataModel, Model model) {
		int row_upd = 0;
		List<ConfigData> data_list = null;
		
		try {
			// modyfikuj rekord:
			row_upd = configDataService.update(configDataModel.getParamId(), configDataModel.getParamNewValue());
			
			// pobierz listę parametrów:
			data_list = configDataService.getAll();
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("configList", data_list);
		
		return "config/show-data-view";
	}
	

}
