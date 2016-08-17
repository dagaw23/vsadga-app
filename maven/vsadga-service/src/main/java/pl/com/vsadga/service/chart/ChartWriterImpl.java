package pl.com.vsadga.service.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.TimeFrameName;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.ChartPatameters;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.utils.DateConverter;

public class ChartWriterImpl implements ChartWriter {

	private class CustomHighLowRenderer extends HighLowRenderer {
		/**
		 * wygenerowany UID
		 */
		private static final long serialVersionUID = -5719129664341419798L;

		@Override
		public Paint getItemPaint(int row, int column) {
			return barSeriesColor.get(column);
		}

		@Override
		public Stroke getSeriesStroke(int series) {
			return new BasicStroke(2);
		}
	}

	private class CustomXYAbsorbVolumeRenderer extends XYBarRenderer {
		/**
		 * wygenerowany UID
		 */
		private static final long serialVersionUID = -7363861519971368725L;

		@Override
		public Paint getItemPaint(int row, int column) {
			return absVolSeriesColor.get(column);
		}

		@Override
		public Stroke getSeriesStroke(int series) {
			return new BasicStroke(2);
		}
	}
	
	private class CustomXYBarRenderer extends XYBarRenderer {
		/**
		 * wygenerowany UID
		 */
		private static final long serialVersionUID = -7363861519971368725L;

		@Override
		public Paint getItemPaint(int row, int column) {
			return barSeriesColor.get(column);
		}

		@Override
		public Stroke getSeriesStroke(int series) {
			return new BasicStroke(2);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartWriterImpl.class);

	private List<Color> absVolSeriesColor;
	
	private List<Color> barSeriesColor;

	private final int CHART_HEIGHT = 550;

	private final int CHART_WIDTH = 550;

	private CurrencyDataService currencyDataService;

	/**
	 * parametry konfiguracyjne dla ramki
	 */
	private Map<TimeFrameName, Integer> frameConfigMap;

	/**
	 * pełna ścieżka do pliku konfiguracyjnego JASPER
	 */
	private String pathToJasperFile;

	/**
	 * ścieżka do plików JPG - zakończona znakiem '/'
	 */
	private String pathToJpgFile;

	/**
	 * ścieżka do plików PDF - zakończona znakiem '/'
	 */
	private String pathToPdfFile;

	@Override
	public boolean deleteAccumulateChartJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		File file = getAccumChartFile(symbol, timeFrame);

		return file.delete();
	}

	@Override
	public boolean deleteChartJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		File file = getChartFile(symbol, timeFrame);

		return file.delete();
	}

	@Override
	public void initConfigParams(String pathToJasperFile, String pathToJpgFile, String pathToPdfFile,
			Map<TimeFrameName, Integer> frameConfigMap) throws BaseServiceException {
		this.pathToJasperFile = pathToJasperFile;
		this.pathToJpgFile = pathToJpgFile;
		this.pathToPdfFile = pathToPdfFile;
		this.frameConfigMap = frameConfigMap;
	}

	/**
	 * @param currencyDataService
	 *            the currencyDataService to set
	 */
	public void setCurrencyDataService(CurrencyDataService currencyDataService) {
		this.currencyDataService = currencyDataService;
	}

	@Override
	public void writeAccumulateChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		int bar_count = 0;

		try {
			// pobierz liczbę drukowanych barów:
			bar_count = getBarCount(timeFrame);

			if (bar_count == 0)
				return;

			// utwórz wykres:
			JFreeChart chart = createCombinedAccumulateChart(symbol, timeFrame, bar_count);

			// zapisz wykres do JPG:
			ChartUtilities.saveChartAsJPEG(getAccumChartFile(symbol, timeFrame), chart, CHART_WIDTH, CHART_HEIGHT);

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("::writeChartToJpg:: wyjatek IOException!");
			throw new BaseServiceException(e);
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::writeChartToJpg:: wyjatek Throwable!");
			throw new BaseServiceException(th);
		}

	}

	@Override
	public void writeChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		int bar_count = 0;

		try {
			// pobierz liczbę drukowanych barów:
			bar_count = getBarCount(timeFrame);

			if (bar_count == 0)
				return;

			// utwórz wykres:
			JFreeChart chart = createCombinedChart(symbol, timeFrame, bar_count);

			// zapisz wykres do JPG:
			ChartUtilities.saveChartAsJPEG(getChartFile(symbol, timeFrame), chart, CHART_WIDTH, CHART_HEIGHT);

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("::writeChartToJpg:: wyjatek IOException!");
			throw new BaseServiceException(e);
		} catch (Throwable th) {
			th.printStackTrace();
			LOGGER.error("::writeChartToJpg:: wyjatek Throwable!");
			throw new BaseServiceException(th);
		}
	}
	
	@Override
	public void writeAccumulateChartToPdf(String symbolName1, String symbolName2) throws BaseServiceException {
		JasperReport compiledReport = null;
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		try {
			// kompiluj szablon raportu:
			compiledReport = JasperCompileManager.compileReport(pathToJasperFile);

			// wpisz wymagane parametry:
			parameters.put("chartParams", getAccuVolParameters(symbolName1, symbolName2));

			JasperPrint filledReport = JasperFillManager.fillReport(compiledReport, parameters,
					new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(filledReport,
					pathToPdfFile + getAccumPdfFileName(symbolName1, symbolName2));

		} catch (JRException e) {
			e.printStackTrace();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	@Override
	public void writeChartToPdf(String symbolName1, String symbolName2) {// "GOLD", "GBPUSD"
		JasperReport compiledReport = null;
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		try {
			// kompiluj szablon raportu:
			compiledReport = JasperCompileManager.compileReport(pathToJasperFile);

			// wpisz wymagane parametry:
			parameters.put("chartParams", getParameters(symbolName1, symbolName2));

			JasperPrint filledReport = JasperFillManager.fillReport(compiledReport, parameters,
					new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(filledReport,
					pathToPdfFile + getPdfFileName(symbolName1, symbolName2));

		} catch (JRException e) {
			e.printStackTrace();
		} catch (Throwable th) {
			th.printStackTrace();
		}

	}
	
	private JFreeChart createCombinedAccumulateChart(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount) throws BaseServiceException {
		// wczytaj dane z tabeli:
		OHLCDataset bar_dataset = getDataset(symbol, timeFrame, barToPrintCount);
		OHLCDataset abs_volume_dataset = getAbsorbVolumeDataset(symbol, timeFrame, barToPrintCount);

		// 1) Axis:
		DateAxis domainAxis = new DateAxis("Czas");
		domainAxis.setLowerMargin(0.02); // reduce the default margins
		domainAxis.setUpperMargin(0.02);
		domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
		// ---
		NumberAxis rangeAxis = new NumberAxis("Cena");
		rangeAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.0000"));
		rangeAxis.setAutoRange(true);
		// ---
		NumberAxis volumeAxis = new NumberAxis("Wolumen");
		// ---
		NumberAxis accuVolumeAxis = new NumberAxis("Skumulowany");

		// 2) Renderer:
		CustomHighLowRenderer chartRenderer = new CustomHighLowRenderer();
		chartRenderer.setDrawOpenTicks(false);
		// ---
		CustomXYBarRenderer volumeRenderer = new CustomXYBarRenderer();
		volumeRenderer.setSeriesPaint(0, Color.black);
		volumeRenderer.setShadowVisible(false);
		// ---
		CustomXYAbsorbVolumeRenderer absVolumeRenderer = new CustomXYAbsorbVolumeRenderer();
		absVolumeRenderer.setSeriesPaint(0, Color.black);
		absVolumeRenderer.setShadowVisible(false);

		// 3) Main & volume plot:
		XYPlot mainPlot = new XYPlot(bar_dataset, domainAxis, rangeAxis, chartRenderer);
		// ---
		XYPlot volumePlot = new XYPlot(getVolumeDataset(bar_dataset, 10 * 1000), domainAxis, volumeAxis, volumeRenderer);
		volumePlot.setBackgroundPaint(Color.white);
		// ---
		XYPlot absorbVolumePlot = new XYPlot(getVolumeDataset(abs_volume_dataset, 10 * 1000), domainAxis, accuVolumeAxis, absVolumeRenderer);
		absorbVolumePlot.setBackgroundPaint(Color.white);

		// 4) Zlozenie wykresow:
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(domainAxis);
		combinedPlot.add(mainPlot, 4);
		combinedPlot.add(volumePlot, 2);
		combinedPlot.add(absorbVolumePlot, 1);

		// 5) Utworzenie wykresu:
		JFreeChart chart = new JFreeChart(getChartName(symbol, timeFrame), null, combinedPlot, false);
		chart.setBackgroundPaint(Color.white);

		return chart;
	}

	private JFreeChart createCombinedChart(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount)
			throws BaseServiceException {
		// wczytaj dane z tabeli:
		OHLCDataset bar_dataset = getDataset(symbol, timeFrame, barToPrintCount);

		// 1) Axis:
		DateAxis domainAxis = new DateAxis("Czas");
		domainAxis.setLowerMargin(0.02); // reduce the default margins
		domainAxis.setUpperMargin(0.02);
		domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
		// ---
		NumberAxis rangeAxis = new NumberAxis("Cena");
		rangeAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.0000"));
		rangeAxis.setAutoRange(true);
		// ---
		NumberAxis volumeAxis = new NumberAxis("Wolumen");

		// 2) Renderer:
		CustomHighLowRenderer chartRenderer = new CustomHighLowRenderer();
		// renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
		// StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
		// new SimpleDateFormat("dd-MMM-yyyy"),
		// new DecimalFormat("0.00")));
		chartRenderer.setDrawOpenTicks(false);
		// ---
		CustomXYBarRenderer volumeRenderer = new CustomXYBarRenderer();
		volumeRenderer.setSeriesPaint(0, Color.black);
		volumeRenderer.setShadowVisible(false);

		// 3) Main & volume plot:
		XYPlot mainPlot = new XYPlot(bar_dataset, domainAxis, rangeAxis, chartRenderer);
		// mainPlot.setRenderer(chartRenderer);
		// plot1.setBackgroundPaint(Color.white);
		// plot1.setDomainGridlinePaint(Color.darkGray);
		// plot1.setRangeGridlinePaint(Color.darkGray);
		// ---
		XYPlot volumePlot = new XYPlot(getVolumeDataset(bar_dataset, 10 * 1000), domainAxis, volumeAxis,
				volumeRenderer);
		volumePlot.setBackgroundPaint(Color.white);
		// plot2.setDomainGridlinePaint(Color.darkGray);
		// plot2.setRangeGridlinePaint(Color.darkGray);

		// 4) Zlozenie wykresow:
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(domainAxis);
		combinedPlot.add(mainPlot, 4);
		combinedPlot.add(volumePlot, 1);
		// cplot.setGap(5.0);
		// cplot.setDomainGridlinePaint(Color.white);
		// cplot.setDomainGridlinesVisible(true);

		// 5) Utworzenie wykresu:
		JFreeChart chart = new JFreeChart(getChartName(symbol, timeFrame), null, combinedPlot, false);
		chart.setBackgroundPaint(Color.white);

		return chart;
	}
	
	private OHLCDataset getAbsorbVolumeDataset(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount) throws BaseServiceException {
		LOGGER.info("   getAbsorbVolumeDataset: " + symbol.getSymbolName() + " " + timeFrame.getTimeFrameDesc());

		// kolorowanie barów:
		absVolSeriesColor = new ArrayList<Color>();

		List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
		OHLCDataItem item = null;

		// pobierz dane z tabeli:
		List<BarData> barData_list = currencyDataService.getLastNbarData(barToPrintCount, symbol, timeFrame);
		BarData bar_data = null;

		for (int i = 0; i < barData_list.size(); i++) {
			bar_data = barData_list.get(i);
			item = new OHLCDataItem(bar_data.getBarTime(), 0, bar_data.getBarHigh().doubleValue(), bar_data
					.getBarLow().doubleValue(), bar_data.getBarClose().doubleValue(), bar_data.getVolumeAbsorb());

			// kolor dla bara danych:
			if (bar_data.getBarType() == BarType.UP_BAR)
				absVolSeriesColor.add(Color.green);
			else if (bar_data.getBarType() == BarType.DOWN_BAR)
				absVolSeriesColor.add(Color.red);
			else
				absVolSeriesColor.add(Color.black);

			dataItems.add(item);
		}

		return new DefaultOHLCDataset(symbol.getSymbolName(),
				dataItems.toArray(new OHLCDataItem[dataItems.size()]));
	}

	private File getAccumChartFile(CurrencySymbol symbol, TimeFrame timeFrame) {
		return new File(pathToJpgFile + getAccumChartFileName(symbol, timeFrame));
	}
	
	private String getAccumChartFileName(CurrencySymbol symbol, TimeFrame timeFrame) {
		return getAccumChartFileName(symbol.getSymbolName(), timeFrame.getTimeFrameDesc());
	}
	
	private String getAccumChartFileName(String symbolName, String timeFrameDesc) {
		return "ACC_VOL" + "-" + symbolName + "-" + timeFrameDesc + ".jpg";
	}
	
	private int getBarCount(TimeFrame timeFrame) {
		Integer value = null;
		String desc = timeFrame.getTimeFrameDesc();

		// czy jest mapa wypełniona:
		if (frameConfigMap == null) {
			LOGGER.error("   [CHART] Brak konfiguracji dla liczby barow [" + frameConfigMap + "].");
			return 0;
		}

		if (desc.equals("D1")) {
			value = frameConfigMap.get(TimeFrameName.D1);
		} else if (desc.equals("H4")) {
			value = frameConfigMap.get(TimeFrameName.H4);
		} else if (desc.equals("H1")) {
			value = frameConfigMap.get(TimeFrameName.H1);
		} else if (desc.equals("M15")) {
			value = frameConfigMap.get(TimeFrameName.M15);
		} else if (desc.equals("M5")) {
			value = frameConfigMap.get(TimeFrameName.M5);
		}

		if (value == null) {
			LOGGER.error("   [CHART] Brak konfiguracji dla [" + desc + "] w mapie frameConfigMap.");
			return 0;
		}

		return value.intValue();
	}

	private File getChartFile(CurrencySymbol symbol, TimeFrame timeFrame) {
		return new File(pathToJpgFile + getChartFileName(symbol, timeFrame));
	}

	private String getChartFileName(CurrencySymbol symbol, TimeFrame timeFrame) {
		return getChartFileName(symbol.getSymbolName(), timeFrame.getTimeFrameDesc());
	}

	private String getChartFileName(String symbolName, String timeFrameDesc) {
		return symbolName + "-" + timeFrameDesc + ".jpg";
	}
	
	private String getChartName(CurrencySymbol symbol, TimeFrame timeFrame) {
		return "Wykres " + symbol.getSymbolName() + " - " + timeFrame.getTimeFrameDesc();
	}

	private OHLCDataset getDataset(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount)
			throws BaseServiceException {
		LOGGER.info("   getDataset: " + symbol.getSymbolName() + " " + timeFrame.getTimeFrameDesc());

		// kolorowanie barów:
		barSeriesColor = new ArrayList<Color>();

		List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
		OHLCDataItem item = null;

		// pobierz dane z tabeli:
		List<BarData> barData_list = currencyDataService.getLastNbarData(barToPrintCount + 1, symbol, timeFrame);
		BarData bar_data = null;
		BigDecimal prev_bar_close = null;

		for (int i = 0; i < barData_list.size(); i++) {
			bar_data = barData_list.get(i);

			// pierwszy bar pomijany do danych:
			if (i == 0) {
				prev_bar_close = bar_data.getBarClose();
				continue;
			}

			item = new OHLCDataItem(bar_data.getBarTime(), 0, bar_data.getBarHigh().doubleValue(), bar_data
					.getBarLow().doubleValue(), bar_data.getBarClose().doubleValue(), bar_data.getBarVolume());

			// kolor dla bara danych:
			if (prev_bar_close.compareTo(bar_data.getBarClose()) < 0)
				barSeriesColor.add(Color.blue);
			else if (prev_bar_close.compareTo(bar_data.getBarClose()) > 0)
				barSeriesColor.add(Color.red);
			else
				barSeriesColor.add(Color.black);

			dataItems.add(item);
			prev_bar_close = bar_data.getBarClose();
		}

		return new DefaultOHLCDataset(symbol.getSymbolName(),
				dataItems.toArray(new OHLCDataItem[dataItems.size()]));
	}
	
	private ChartPatameters getAccuVolParameters(String symbolName1, String symbolName2) {
		ChartPatameters params = new ChartPatameters();

		params.setSymbolName1(symbolName1);
		params.setSymbol1H4Path(pathToJpgFile + getAccumChartFileName(symbolName1, "H4"));
		params.setSymbol1H1Path(pathToJpgFile + getAccumChartFileName(symbolName1, "H1"));
		params.setSymbol1M15Path(pathToJpgFile + getAccumChartFileName(symbolName1, "M15"));
		params.setSymbol1M5Path(pathToJpgFile + getAccumChartFileName(symbolName1, "M5"));

		params.setSymbolName2(symbolName2);
		params.setSymbol2H4Path(pathToJpgFile + getAccumChartFileName(symbolName2, "H4"));
		params.setSymbol2H1Path(pathToJpgFile + getAccumChartFileName(symbolName2, "H1"));
		params.setSymbol2M15Path(pathToJpgFile + getAccumChartFileName(symbolName2, "M15"));
		params.setSymbol2M5Path(pathToJpgFile + getAccumChartFileName(symbolName2, "M5"));

		return params;
	}

	private ChartPatameters getParameters(String symbolName1, String symbolName2) {
		ChartPatameters params = new ChartPatameters();

		params.setSymbolName1(symbolName1);
		params.setSymbol1D1Path(pathToJpgFile + getChartFileName(symbolName1, "D1"));
		params.setSymbol1H4Path(pathToJpgFile + getChartFileName(symbolName1, "H4"));
		params.setSymbol1H1Path(pathToJpgFile + getChartFileName(symbolName1, "H1"));
		params.setSymbol1M15Path(pathToJpgFile + getChartFileName(symbolName1, "M15"));
		params.setSymbol1M5Path(pathToJpgFile + getChartFileName(symbolName1, "M5"));

		params.setSymbolName2(symbolName2);
		params.setSymbol2D1Path(pathToJpgFile + getChartFileName(symbolName2, "D1"));
		params.setSymbol2H4Path(pathToJpgFile + getChartFileName(symbolName2, "H4"));
		params.setSymbol2H1Path(pathToJpgFile + getChartFileName(symbolName2, "H1"));
		params.setSymbol2M15Path(pathToJpgFile + getChartFileName(symbolName2, "M15"));
		params.setSymbol2M5Path(pathToJpgFile + getChartFileName(symbolName2, "M5"));

		return params;
	}
	
	private String getAccumPdfFileName(String symbolName1, String symbolName2) {
		return "ACC_VOL_" + symbolName1 + "_" + symbolName2 + "_" + DateConverter.dateToString(new Date(), "yyMMdd-HHmm")
				+ ".pdf";

	}

	private String getPdfFileName(String symbolName1, String symbolName2) {
		return symbolName1 + "_" + symbolName2 + "_" + DateConverter.dateToString(new Date(), "yyMMdd-HHmm")
				+ ".pdf";

	}

	private IntervalXYDataset getVolumeDataset(final OHLCDataset priceDataset, final long barWidthInMilliseconds) {
		return new AbstractIntervalXYDataset() {

			@Override
			public Number getEndX(int series, int item) {
				return priceDataset.getX(series, item).doubleValue() + barWidthInMilliseconds / 2;
			}

			@Override
			public Number getEndY(int series, int item) {
				return priceDataset.getVolume(series, item);
			}

			@Override
			public int getItemCount(int series) {
				return priceDataset.getItemCount(series);
			}

			@Override
			public int getSeriesCount() {
				return priceDataset.getSeriesCount();
			}

			@Override
			public Comparable getSeriesKey(int series) {
				return priceDataset.getSeriesKey(series) + "-Volume";
			}

			@Override
			public Number getStartX(int series, int item) {
				return priceDataset.getX(series, item).doubleValue() - barWidthInMilliseconds / 2;
			}

			@Override
			public Number getStartY(int series, int item) {
				return new Double(0.0);
			}

			@Override
			public Number getX(int series, int item) {
				return priceDataset.getX(series, item);
			}

			@Override
			public Number getY(int series, int item) {
				return priceDataset.getVolume(series, item);
			}
		};
	}

}
