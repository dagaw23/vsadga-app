package pl.com.vsadga.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pl.com.vsadga.service.BaseServiceException;

@Controller
public class PdfFileDownloadController extends BaseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfFileDownloadController.class);

	@RequestMapping(value="/download/pdf/{fileName}", method = RequestMethod.GET)
    public void downloadPdfFile(HttpServletResponse response, @PathVariable("fileName") String fileName)  {
		String mimeType = "application/pdf";
		String pdf_path = null;
		File file = null;
		
		try {
			// pobierz ścieżkę do pliku:
			pdf_path = getStringParamValue("CHART_PDF_WRITE_PATH");
			
			if (pdf_path == null) {
				LOGGER.error("   [PdfFileDownload] Brak skonfigurowanej sciezki wg CHART_PDF_WRITE_PATH.");
				return;
			}
			
			file = new File(pdf_path + fileName + ".pdf");
			if (!file.exists()) {
				String msg = "Sorry. The file you are looking for does not exist!";
				LOGGER.error("   [PdfFileDownload] " + msg);
				OutputStream outputStream = response.getOutputStream();
				outputStream.write(msg.getBytes(Charset.forName("UTF-8")));
				outputStream.close();
				return;
			}
			
			response.setContentType(mimeType);
			
			/*
			 * "Content-Disposition : inline" will show viewable types [like
			 * images/text/pdf/anything viewable by browser] right on browser while others(zip e.g)
			 * will be directly downloaded [may provide save as popup, based on your browser
			 * setting.]
			 */
			//response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
			/*
			 * "Content-Disposition : attachment" will be directly download, may provide save as
			 * popup, based on your browser setting
			 */
	        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
	         
	        response.setContentLength((int)file.length());
	 
	        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
	 
			// Copy bytes from source to destination(outputstream in this example), closes both
			// streams.
	        FileCopyUtils.copy(inputStream, response.getOutputStream());
			
				
		} catch (BaseServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
     

}
