package pl.com.vsadga.web.controller;

import java.io.File;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.service.BaseServiceException;

@Controller
public class PrintPdfFilesController extends BaseController {
	
	@RequestMapping("/pdf-print")
	public ModelAndView printPdfFileList() {
		StringBuffer html_buff = new StringBuffer();
		html_buff.append("<br><br>Alert printer");
		
		String pdf_path = null;
		
		try {
			pdf_path = getStringParamValue("CHART_PDF_WRITE_PATH");
			
			File dir = new File(pdf_path);
			
			for (String file_name : dir.list()) {
				html_buff.append("<a href='");
				html_buff.append(pdf_path + file_name);
				html_buff.append("'>");
				html_buff.append(file_name);
				html_buff.append("</a>");
				html_buff.append("<br>");
			}
			
			
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("print-pdf", "html_tab", html_buff.toString());
	}

}
