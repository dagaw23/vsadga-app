package pl.com.vsadga.service.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
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
			return new BasicStroke(3);
		}

	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartWriterImpl.class);

	private OHLCSeries barOhlcSeries;

	private List<Color> barSeriesColor;

	private final int CHART_HEIGHT = 550;

	private final int CHART_WIDTH = 550;

	private CurrencyDataService currencyDataService;

	//"/My-workspaces/vsadga-workspace/jreports/chartreport.jrxml"
	private String pathToJasperFile;

	/**
	 * ścieżka do plików JPG - zakończona znakiem '/'
	 */
	private String pathToJpgFile;//"/My-workspaces/vsadga-workspace/work/"

	//"/My-workspaces/vsadga-workspace/reports/"
	private String pathToPdfFile;

	private TimeSeries volumeOhlcSeries;
	
	@Override
	public boolean deleteChartJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		File file = getChartFile(symbol, timeFrame);
		
		return file.delete();
	}

	@Override
	public void initConfigParams(String pathToJasperFile, String pathToJpgFile, String pathToPdfFile) throws BaseServiceException {
		this.pathToJasperFile = pathToJasperFile;
		this.pathToJpgFile = pathToJpgFile;
		this.pathToPdfFile = pathToPdfFile;
	}

	/**
	 * @param currencyDataService
	 *            the currencyDataService to set
	 */
	public void setCurrencyDataService(CurrencyDataService currencyDataService) {
		this.currencyDataService = currencyDataService;
	}

	@Override
	public void writeChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount) throws BaseServiceException {

		try {
			// utwórz wykres:
			JFreeChart chart = createCombinedChart(symbol, timeFrame, barToPrintCount);

			// zapisz wykres do JPG:
			ChartUtilities.saveChartAsJPEG(getChartFile(symbol, timeFrame), chart, CHART_WIDTH,	CHART_HEIGHT);

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

	private JFreeChart createCombinedChart(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount)
			throws BaseServiceException {
		// wczytaj dane z tabeli:
		fillDataset(symbol, timeFrame, barToPrintCount);

		CustomHighLowRenderer renderer1 = new CustomHighLowRenderer();
		renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("dd-MMM-yyyy"),
				new DecimalFormat("0.00")));
		renderer1.setDrawOpenTicks(false);
		DateAxis domainAxis = new DateAxis("Data");
		NumberAxis rangeAxis = new NumberAxis("Cena");
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.00"));
		rangeAxis.setAutoRange(true);
		rangeAxis.setAutoRangeIncludesZero(false);

		XYPlot plot1 = new XYPlot(getBarDataset(), domainAxis, rangeAxis, renderer1);
		plot1.setBackgroundPaint(Color.white);
		plot1.setDomainGridlinePaint(Color.darkGray);
		plot1.setRangeGridlinePaint(Color.darkGray);
		//plot1.setRangePannable(true);

		XYBarRenderer renderer2 = new XYBarRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("dd-MMM-yyyy"),
				new DecimalFormat("0,000.00")));
		renderer2.setDrawBarOutline(false);
		renderer2.setSeriesPaint(0, Color.black);
		renderer2.setSeriesStroke(0, new BasicStroke(2));

		XYPlot plot2 = new XYPlot(new TimeSeriesCollection(volumeOhlcSeries), null, new NumberAxis("Wolumen"),
				renderer2);
		plot2.setBackgroundPaint(Color.white);
		plot2.setDomainGridlinePaint(Color.darkGray);
		plot2.setRangeGridlinePaint(Color.darkGray);

		CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(domainAxis);
		cplot.add(plot1, 3);
		cplot.add(plot2, 2);
		cplot.setGap(5.0);
		cplot.setDomainGridlinePaint(Color.white);
		cplot.setDomainGridlinesVisible(true);
		//cplot.setDomainPannable(true);

		JFreeChart chart = new JFreeChart(getChartName(symbol, timeFrame), JFreeChart.DEFAULT_TITLE_FONT, cplot,
				false);
		// ChartUtilities.applyCurrentTheme(chart);
		renderer2.setShadowVisible(false);
		renderer2.setBarPainter(new StandardXYBarPainter());

		return chart;
	}
	
	@SuppressWarnings("deprecation")
	private TimeSeries getTimeSeries(CurrencySymbol symbol, TimeFrame timeFrame) {
		if (timeFrame.getTimeFrame() < 60)
			return new TimeSeries(getDatasetName(symbol, timeFrame), Minute.class);
		else if (timeFrame.getTimeFrame() >= 60 && timeFrame.getTimeFrame() < 1440)
			return new TimeSeries(getDatasetName(symbol, timeFrame), Hour.class);
		else
			return new TimeSeries(getDatasetName(symbol, timeFrame), Day.class);
		
	}
	
	private RegularTimePeriod getTimePeriod(CurrencySymbol symbol, TimeFrame timeFrame, BarData barData) {
		if (timeFrame.getTimeFrame() < 60)
			return new Minute(barData.getBarTime());
		else if (timeFrame.getTimeFrame() >= 60 && timeFrame.getTimeFrame() < 1440)
			return new Hour(barData.getBarTime());
		else
			return new Day(barData.getBarTime());
		
	}
	
	private String getDatasetName(CurrencySymbol symbol, TimeFrame timeFrame) {
		return symbol.getSymbolName() + "-" + timeFrame.getTimeFrameDesc();
	}

	private void fillDataset(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount) throws BaseServiceException {
		LOGGER.info("   fillDataset: " + symbol.getSymbolName() + " " + timeFrame.getTimeFrameDesc());
		
		//TODO dodac parametryzowane wpisywanie z rozna iloscia barow dla D1, H4, H1, M15, M5
		
		// dane bara i wolumen:
		OHLCSeries s1 = new OHLCSeries(symbol.getSymbolName());
		//TimeSeries s2 = new TimeSeries(symbol.getSymbolName(), Minute.class);
		TimeSeries s2 = getTimeSeries(symbol, timeFrame);
		barSeriesColor = new ArrayList<Color>();

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

			s1.add(new FixedMillisecond(bar_data.getBarTime()), 0, bar_data.getBarHigh().doubleValue(), bar_data
					.getBarLow().doubleValue(), bar_data.getBarClose().doubleValue());
			s2.add(getTimePeriod(symbol, timeFrame, bar_data), bar_data.getBarVolume());

			// kolor dla bara danych:
			if (prev_bar_close.compareTo(bar_data.getBarClose()) < 0)
				barSeriesColor.add(Color.blue);
			else if (prev_bar_close.compareTo(bar_data.getBarClose()) > 0)
				barSeriesColor.add(Color.red);
			else
				barSeriesColor.add(Color.black);

			prev_bar_close = bar_data.getBarClose();
		}

		this.barOhlcSeries = s1;
		this.volumeOhlcSeries = s2;
	}

	private XYDataset getBarDataset() {
		OHLCSeriesCollection coll = new OHLCSeriesCollection();
		coll.addSeries(barOhlcSeries);

		return coll;
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

	private String getPdfFileName(String symbolName1, String symbolName2) {
		return symbolName1 + "_" + symbolName2 + "_" + DateConverter.dateToString(new Date(), "yyMMdd-HHmm")
				+ ".pdf";

	}

}
