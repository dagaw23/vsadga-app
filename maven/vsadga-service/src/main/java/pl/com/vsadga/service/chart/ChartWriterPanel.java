package pl.com.vsadga.service.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class ChartWriterPanel extends JPanel {

	private OHLCSeries ohlcSeries;
	
	private List<Color> dataColor;
	
	private List<Color> volumeColor;
	
	public static void main(String[] args) {
		
		new Runnable() {
			
			@Override
			public void run() {
				ChartWriterPanel chart = new ChartWriterPanel();
				chart.setVisible(true);
				chart.repaint();
				
				JFrame frame = new JFrame();
				frame.setBounds(200, 200, 876, 549);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setContentPane(chart);
	            frame.pack();
	            frame.setVisible(true);
			}
		}.run();
	}

	public ChartWriterPanel() {
		// Create OHLC series for the chart
		ohlcSeries = createPriceDataset("").getSeries(0);
		
		// Create chart itself
	    JFreeChart chart = createCombinedChart();
	    ChartPanel chartPanel = new ChartPanel(chart);
	    this.add(chartPanel);
	}
	
	private class CustomHighLowRenderer extends HighLowRenderer {
		
		@Override
		public Paint getItemPaint(int row, int column) {
			return dataColor.get(column);
		}
		
		@Override
		public Stroke getSeriesStroke(int series) {
			return new BasicStroke(4);
		}
		
	}
	
	private JFreeChart createCombinedChart() {
		OHLCDataset data1 = createPriceDataset("abc");
		IntervalXYDataset data2 = createVolumeDataset("cde");

		CustomHighLowRenderer renderer1 = new CustomHighLowRenderer();
		renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
				new SimpleDateFormat("dd-MMM-yyyy"),
				new DecimalFormat("0.00")));
		renderer1.setDrawOpenTicks(false);
		DateAxis domainAxis = new DateAxis("Data");
		NumberAxis rangeAxis = new NumberAxis("Cena");
		rangeAxis.setNumberFormatOverride(new DecimalFormat("0.00"));
		rangeAxis.setAutoRange(true);
		rangeAxis.setAutoRangeIncludesZero(false);

		XYPlot plot1 = new XYPlot(data1, domainAxis, rangeAxis, renderer1);
		plot1.setBackgroundPaint(Color.white);
		plot1.setDomainGridlinePaint(Color.darkGray);
		plot1.setRangeGridlinePaint(Color.darkGray);
		plot1.setRangePannable(true);

		XYBarRenderer renderer2 = new XYBarRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
				new SimpleDateFormat("dd-MMM-yyyy"),
				new DecimalFormat("0,000.00")));
		renderer2.setDrawBarOutline(false);
		renderer2.setSeriesPaint(0, Color.black);
		renderer2.setSeriesStroke(0, new BasicStroke(2));

		XYPlot plot2 = new XYPlot(data2, null, new NumberAxis("Wolumen"), renderer2);
		plot2.setBackgroundPaint(Color.white);
		plot2.setDomainGridlinePaint(Color.darkGray);
		plot2.setRangeGridlinePaint(Color.darkGray);

		CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(domainAxis);
		cplot.add(plot1, 3);
		cplot.add(plot2, 2);
		cplot.setGap(5.0);
		cplot.setDomainGridlinePaint(Color.white);
		cplot.setDomainGridlinesVisible(true);
		cplot.setDomainPannable(true);

		JFreeChart chart = new JFreeChart("Wykres", JFreeChart.DEFAULT_TITLE_FONT, cplot, false);

		//ChartUtilities.applyCurrentTheme(chart);
		renderer2.setShadowVisible(false);
		renderer2.setBarPainter(new StandardXYBarPainter());

		return chart;
	}

	public ChartWriterPanel(LayoutManager arg0) {
		super(arg0);
	}

	public ChartWriterPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public ChartWriterPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	private IntervalXYDataset createVolumeDataset(String symbolName) {
		TimeSeries s1 = new TimeSeries(symbolName);

		s1.add(new FixedMillisecond(getDate(8, 0)), 405);
		s1.add(new FixedMillisecond(getDate(8, 5)), 833);

		s1.add(new FixedMillisecond(getDate(8, 10)), 161);
		s1.add(new FixedMillisecond(getDate(8, 15)), 682);

		s1.add(new FixedMillisecond(getDate(8, 20)), 165);
		s1.add(new FixedMillisecond(getDate(8, 25)), 172);

		return new TimeSeriesCollection(s1);
	}

	private OHLCSeriesCollection createPriceDataset(String symbolName) {
		this.dataColor = new ArrayList<Color>();
		OHLCSeries s1 = new OHLCSeries(symbolName);

		s1.add(new FixedMillisecond(getDate(8, 0)), new Double("0.00"), new Double("1325.17"), new Double("1324.65"), new Double("1325.16"));
		dataColor.add(Color.red);
		s1.add(new FixedMillisecond(getDate(8, 5)), new Double("0.00"), new Double("1325.84"), new Double("1324.14"), new Double("1324.75"));
		dataColor.add(Color.red);

		s1.add(new FixedMillisecond(getDate(8, 10)), new Double("0.00"), new Double("1326.13"), new Double("1325.53"), new Double("1325.73"));
		dataColor.add(Color.blue);
		s1.add(new FixedMillisecond(getDate(8, 15)), new Double("0.00"), new Double("1325.97"), new Double("1324.74"), new Double("1325.75"));
		dataColor.add(Color.blue);

		s1.add(new FixedMillisecond(getDate(8, 20)), new Double("0.00"), new Double("1326.55"), new Double("1325.83"), new Double("1325.95"));
		dataColor.add(Color.blue);
		s1.add(new FixedMillisecond(getDate(8, 25)), new Double("0.00"), new Double("1326.94"), new Double("1326.54"), new Double("1326.55"));
		dataColor.add(Color.blue);
		
		OHLCSeriesCollection dataset = new OHLCSeriesCollection();
		dataset.addSeries(s1);

		return dataset;
	}

	private Date getDate(int hour, int minute) {
		GregorianCalendar gregCal = new GregorianCalendar();
		gregCal.setTime(new Date());

		gregCal.set(Calendar.MINUTE, minute);
		gregCal.set(Calendar.HOUR_OF_DAY, hour);

		return gregCal.getTime();
	}

}
