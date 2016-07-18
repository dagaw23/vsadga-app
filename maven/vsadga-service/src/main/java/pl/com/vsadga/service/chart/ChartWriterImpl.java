package pl.com.vsadga.service.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

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
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

public class ChartWriterImpl implements ChartWriter {

	private CurrencyDataService currencyDataService;

	public ChartWriterImpl() {
	}
	
	/**
	 * @param currencyDataService the currencyDataService to set
	 */
	public void setCurrencyDataService(CurrencyDataService currencyDataService) {
		this.currencyDataService = currencyDataService;
	}



	private IntervalXYDataset createVolumeDataset(CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {

		TimeSeries s1 = new TimeSeries(symbol.getSymbolName());

		List<BarData> barData_list = currencyDataService.getLastNbarData(20, symbol, timeFrame);

		for (BarData bar_data : barData_list)
			s1.add(new Day(bar_data.getBarTime()), bar_data.getBarVolume());

		return new TimeSeriesCollection(s1);
	}

	public void print(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		Document document = null;
		PdfWriter pdfWriter = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(new File("/tmp/abc.pdf"));

			document = new Document();
			pdfWriter = PdfWriter.getInstance(document, fos);

			// open document
			document.open();

			// add image
			JFreeChart chart = createCombinedChart(symbol, timeFrame);
			BufferedImage bufferedImage = chart.createBufferedImage(300, 300);
			Image image = Image.getInstance(pdfWriter, bufferedImage, 1.0f);
			document.add(image);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (document != null)
				document.close();
			
			if (pdfWriter != null)
				pdfWriter.close();
		}

	}

	private OHLCDataset createPriceDataset(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {

		OHLCSeries s1 = new OHLCSeries(symbol.getSymbolName());

		List<BarData> barData_list = currencyDataService.getLastNbarData(20, symbol, timeFrame);

		for (BarData bar_data : barData_list)
			s1.add(new Day(bar_data.getBarTime()), 0, bar_data.getBarHigh().doubleValue(), bar_data.getBarLow()
					.doubleValue(), bar_data.getBarClose().doubleValue());

		OHLCSeriesCollection dataset = new OHLCSeriesCollection();
		dataset.addSeries(s1);

		return dataset;
	}

	private JFreeChart createCombinedChart(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {

		OHLCDataset data1 = createPriceDataset(symbol, timeFrame);

		XYItemRenderer renderer1 = new HighLowRenderer();
		renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("dd-MMM-yyyy"),
				new DecimalFormat("0.00")));
		renderer1.setSeriesPaint(0, Color.blue);
		DateAxis domainAxis = new DateAxis("Date");
		NumberAxis rangeAxis = new NumberAxis("Price");
		rangeAxis.setNumberFormatOverride(new DecimalFormat("$0.00"));
		rangeAxis.setAutoRange(true);
		rangeAxis.setAutoRangeIncludesZero(false);

		XYPlot plot1 = new XYPlot(data1, domainAxis, rangeAxis, renderer1);
		plot1.setBackgroundPaint(Color.lightGray);
		plot1.setDomainGridlinePaint(Color.white);
		plot1.setRangeGridlinePaint(Color.white);
		plot1.setRangePannable(true);

		IntervalXYDataset data2 = createVolumeDataset(symbol, timeFrame);
		XYBarRenderer renderer2 = new XYBarRenderer();
		renderer2.setDrawBarOutline(false);
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("dd-MMM-yyyy"),
				new DecimalFormat("0,000.00")));
		renderer2.setSeriesPaint(0, Color.red);

		XYPlot plot2 = new XYPlot(data2, null, new NumberAxis("Volume"), renderer2);
		plot2.setBackgroundPaint(Color.lightGray);
		plot2.setDomainGridlinePaint(Color.white);
		plot2.setRangeGridlinePaint(Color.white);

		CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(domainAxis);
		cplot.add(plot1, 3);
		cplot.add(plot2, 2);
		cplot.setGap(8.0);
		cplot.setDomainGridlinePaint(Color.white);
		cplot.setDomainGridlinesVisible(true);
		cplot.setDomainPannable(true);

		JFreeChart chart = new JFreeChart("ABC", JFreeChart.DEFAULT_TITLE_FONT, cplot, false);

		ChartUtilities.applyCurrentTheme(chart);
		renderer2.setShadowVisible(false);
		renderer2.setBarPainter(new StandardXYBarPainter());

		return chart;
	}

}
