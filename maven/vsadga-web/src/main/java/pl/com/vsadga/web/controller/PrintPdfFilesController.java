package pl.com.vsadga.web.controller;

import java.io.File;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import pl.com.vsadga.service.BaseServiceException;

@Controller
public class PrintPdfFilesController extends BaseController {
	
	@RequestMapping("/pdf-print")
	public ModelAndView printPdfFileList(HttpServletRequest request) {
		StringBuffer html_buff = new StringBuffer();
		html_buff.append("<br><br>Alert printer");
		
		String pdf_path = null;
		
		try {
			pdf_path = getStringParamValue("CHART_PDF_WRITE_PATH");
			
			File dir = new File(pdf_path);
			
			for (String file_name : dir.list()) {
				html_buff.append("<a href='");
				html_buff.append(request.getContextPath());
				html_buff.append("/download/pdf/");
				html_buff.append(file_name.substring(0, file_name.length()-4));
				html_buff.append(".html'>");
				html_buff.append(file_name);
				html_buff.append("</a>");
				html_buff.append("<br>");
			}
			
			
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("print-pdf", "html_tab", html_buff.toString());
	}
	
	private void getPdfFileList(String pdfFilePath) {
		File dir = new File(pdfFilePath);
		String[] file_tab = dir.list();
		
		TreeSet<String> pdf_set = new TreeSet<String>(new Comparator<String>() {

			@Override
			public int compare(String fileName1, String fileName2) {
				
				
				fileName1.substring(fileName1.length()-12);
				
				
				
				return 0;
			}
		});
		
		
	}

}
