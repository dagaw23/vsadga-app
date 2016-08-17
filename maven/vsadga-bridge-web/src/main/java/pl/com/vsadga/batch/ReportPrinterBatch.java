package pl.com.vsadga.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.TimeFrameName;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.chart.ChartWriter;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;

@Component
public class ReportPrinterBatch extends BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportPrinterBatch.class);

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private ChartWriter chartWriter;
	
	private TimeFrame d1TimeFrame;

	@Scheduled(cron = "0 1/2 0,7-23 * * MON-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// wczytaj dane konfiguracyjne:
			if (!readConfigData())
				return;

			// pobierz listę aktywnych symboli:
			symbol_list = symbolService.getActiveSymbols();
			// pobierz listę aktywnych timeframe:
			tmefrm_list = timeFrameService.getAllActive();

			if (symbol_list.isEmpty() || tmefrm_list.isEmpty()) {
				LOGGER.info("   [BATCH] Symbole nie sa aktywne [" + symbol_list.size() + "] lub ramy czasowe ["
						+ tmefrm_list.size() + "].");
				return;
			}

			// 1) Utworzenie wykresow w postaci JPG:
			int chart_writed2jpg = writeChartsToJpg(symbol_list, tmefrm_list);
			LOGGER.info("   [CHART] Utworzono [" + chart_writed2jpg + "] pliki JPG.");

			// 2) Utworzenie pliku PDF:
			createPdfReport();

			// 3) Usunięcie plików JPG:
			int chart_deleted = deleteChartsJpg(symbol_list, tmefrm_list);
			LOGGER.info("   [CHART] Usunieto [" + chart_deleted + "] pliki JPG.");

		} catch (BaseServiceException e) {
			e.printStackTrace();
		} catch (BatchProcessException e) {
			e.printStackTrace();
		}
	}

	private void createPdfReport() throws BaseServiceException {
		// zwykłe wykresy:
		chartWriter.writeChartToPdf("AUDUSD", "GBPUSD");
		chartWriter.writeChartToPdf("USDCAD", "EURUSD");
		chartWriter.writeChartToPdf("USDJPY", "NZDUSD");

		chartWriter.writeChartToPdf("USDCHF", "OIL");
		chartWriter.writeChartToPdf("GOLD", "SILVER");
		chartWriter.writeChartToPdf("US500", "GER30");
		
		// wykresy wolumenu skumulowanego:
		chartWriter.writeAccumulateChartToPdf("AUDUSD", "GBPUSD");
		chartWriter.writeAccumulateChartToPdf("USDCAD", "EURUSD");
		chartWriter.writeAccumulateChartToPdf("USDJPY", "NZDUSD");

		chartWriter.writeAccumulateChartToPdf("USDCHF", "OIL");
		chartWriter.writeAccumulateChartToPdf("GOLD", "SILVER");
		chartWriter.writeAccumulateChartToPdf("US500", "GER30");
	}

	private int writeChartsToJpg(List<CurrencySymbol> symbolList, List<TimeFrame> timeFrameList)
			throws BaseServiceException {
		int chart_count = 0;
		for (CurrencySymbol symbol : symbolList) {
			for (TimeFrame timeFrame : timeFrameList) {
				// zwykły wykres:
				chartWriter.writeChartToJpg(symbol, timeFrame);
				chart_count++;
				
				// wolumen skumulowany:
				chartWriter.writeAccumulateChartToJpg(symbol, timeFrame);
				chart_count++;
			}
			
			// dodanie jeszcze wykresu dla D1:
			chartWriter.writeChartToJpg(symbol, d1TimeFrame);
			chart_count++;
		}

		return chart_count;
	}

	private int deleteChartsJpg(List<CurrencySymbol> symbolList, List<TimeFrame> timeFrameList)
			throws BaseServiceException {
		int chart_count = 0;
		for (CurrencySymbol symbol : symbolList) {
			for (TimeFrame timeFrame : timeFrameList) {
				if (chartWriter.deleteChartJpg(symbol, timeFrame))
					chart_count++;
				
				if (chartWriter.deleteAccumulateChartJpg(symbol, timeFrame))
					chart_count++;
			}
			
			chartWriter.deleteChartJpg(symbol, d1TimeFrame);
			chart_count++;
		}

		return chart_count;
	}

	private boolean readConfigData() throws BatchProcessException {
		String jpg_path = null;
		String pdf_path = null;
		String jasper_path = null;
		Integer bar_count = 0;
		Map<TimeFrameName, Integer> config_map = new HashMap<TimeFrameName, Integer>();

		try {
			jpg_path = getStringParamValue("CHART_JPG_WRITE_PATH");
			if (jpg_path == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_JPG_WRITE_PATH w tabeli parametrow.");
				return false;
			}
			
			pdf_path = getStringParamValue("CHART_PDF_WRITE_PATH");
			if (pdf_path == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_PDF_WRITE_PATH w tabeli parametrow.");
				return false;
			}
			
			jasper_path = getStringParamValue("JASPER_XML_PATH");
			if (jasper_path == null) {
				LOGGER.error("   [REPORT] Brak parametru JASPER_XML_PATH w tabeli parametrow.");
				return false;
			}
			
			bar_count = getIntParamValue("CHART_BAR_COUNT_D1");
			if (bar_count == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_BAR_COUNT_D1 w tabeli parametrow.");
				return false;
			}
			config_map.put(TimeFrameName.D1, bar_count);
			
			bar_count = getIntParamValue("CHART_BAR_COUNT_H4");
			if (bar_count == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_BAR_COUNT_H4 w tabeli parametrow.");
				return false;
			}
			config_map.put(TimeFrameName.H4, bar_count);
			
			bar_count = getIntParamValue("CHART_BAR_COUNT_H1");
			if (bar_count == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_BAR_COUNT_H1 w tabeli parametrow.");
				return false;
			}
			config_map.put(TimeFrameName.H1, bar_count);
			
			bar_count = getIntParamValue("CHART_BAR_COUNT_M15");
			if (bar_count == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_BAR_COUNT_M15 w tabeli parametrow.");
				return false;
			}
			config_map.put(TimeFrameName.M15, bar_count);
			
			bar_count = getIntParamValue("CHART_BAR_COUNT_M5");
			if (bar_count == null) {
				LOGGER.error("   [REPORT] Brak parametru CHART_BAR_COUNT_M5 w tabeli parametrow.");
				return false;
			}
			config_map.put(TimeFrameName.M5, bar_count);
			
			chartWriter.initConfigParams(jasper_path, jpg_path, pdf_path, config_map);
			
			// inicjalizacja D1 timeFrame:
			d1TimeFrame = new TimeFrame();
			d1TimeFrame.setTimeFrame(1440);
			d1TimeFrame.setTimeFrameDesc("D1");

			return true;
		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::readConfigData:: wyjatek BaseServiceException!");
			throw new BatchProcessException(e);
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::readConfigData:: wyjatek Throwable!");
			throw new BatchProcessException(th);
		}

	}

	private boolean isProcessBatch() throws BaseServiceException {
		Integer is_report = getIntParamValue("IS_BATCH_REPORT_PRINTER");

		if (is_report == null)
			throw new BaseServiceException("::isProcessBatch:: brak parametru IS_BATCH_REPORT_PRINTER ["
					+ is_report + "] w tabeli parametrow.");

		if (is_report.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch tworzenia raportu PDF [" + is_report.intValue() + "].");
			return false;
		}
	}

}
