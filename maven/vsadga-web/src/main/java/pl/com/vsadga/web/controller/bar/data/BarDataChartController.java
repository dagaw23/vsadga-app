package pl.com.vsadga.web.controller.bar.data;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.chart.ChartWriter;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.web.controller.BaseController;
import pl.com.vsadga.web.model.bar.data.BarDataChartModel;

@Controller
public class BarDataChartController extends BaseController {
	
	@Autowired 
	private ServletContext servletContext;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;
	
	@Autowired
	private ChartWriter chartWriter;
	
	@Autowired
	private ConfigDataService configDataService;

	@RequestMapping("/show-bar-data-chart")
	public ModelAndView showSymbolList() {
		ModelAndView mav = new ModelAndView("bar-data/bar-data-chart");

		// lista symboli:
		mav.addObject("symbolList", symbolService.getActiveSymbols());

		// lista ramek:
		mav.addObject("frameList", timeFrameService.getAllActive());

		mav.addObject("barDataChartModel", new BarDataChartModel());

		return mav;
	}
	
	@RequestMapping(value = "/bar-data-chart", method = RequestMethod.POST)
	public ResponseEntity<byte[]> getImageAsResponseEntity(@ModelAttribute BarDataChartModel barDataChartModel, Model model) {
		CurrencySymbol symbol = null;
		//String path = null;
		ByteArrayOutputStream out = null;
		
		try {
			// nagłówki:
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			headers.setCacheControl(CacheControl.noCache().getHeaderValue());
			
			// TODO: przeniesc to do sesji - te parametry
			// konfiguracja:
			if (chartWriter.isNotInitConfig())
				chartWriter.initConfigParams(configDataService.getParam("JASPER_XML_PATH"),
						configDataService.getParam("CHART_JPG_WRITE_PATH"));

			symbol = symbolService.getById(barDataChartModel.getSymbolId());
			out = chartWriter.writeChartToJpg(symbol, barDataChartModel.getFrame(), barDataChartModel.getBarCount());
			
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);
			
			return responseEntity;
		} catch (BaseServiceException e) {
			e.printStackTrace();
			return null;
		}
		//return "bar-data/bar-data-chart";
	}

}
