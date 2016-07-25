package pl.com.vsadga.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
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

	private String chartJpgWritePath;

	@Scheduled(cron = "30 0/5 0,7-23 * * SUN-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// wczytaj dane konfiguracyjne:
			readConfigData();

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
		chartWriter.writeChartToPdf("AUDUSD", "GBPUSD");
		chartWriter.writeChartToPdf("USDCAD", "EURUSD");
		chartWriter.writeChartToPdf("USDJPY", "NZDUSD");

		chartWriter.writeChartToPdf("USDCHF", "OIL");
		chartWriter.writeChartToPdf("GOLD", "SILVER");
		chartWriter.writeChartToPdf("US500", "GER30");
	}

	private int writeChartsToJpg(List<CurrencySymbol> symbolList, List<TimeFrame> timeFrameList)
			throws BaseServiceException {
		int chart_count = 0;
		for (CurrencySymbol symbol : symbolList) {
			for (TimeFrame timeFrame : timeFrameList) {
				chartWriter.writeChartToJpg(symbol, timeFrame, 100, chartJpgWritePath);
				chart_count++;
			}
		}

		return chart_count;
	}

	private int deleteChartsJpg(List<CurrencySymbol> symbolList, List<TimeFrame> timeFrameList)
			throws BaseServiceException {
		int chart_count = 0;
		for (CurrencySymbol symbol : symbolList) {
			for (TimeFrame timeFrame : timeFrameList) {
				if (chartWriter.deleteChartJpg(symbol, timeFrame, chartJpgWritePath))
					chart_count++;
			}
		}

		return chart_count;
	}

	private boolean readConfigData() throws BatchProcessException {
		String value = null;

		try {
			value = getStringParamValue("CHART_JPG_WRITE_PATH");

			if (value == null)
				return false;
			else
				this.chartJpgWritePath = value.trim();

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
