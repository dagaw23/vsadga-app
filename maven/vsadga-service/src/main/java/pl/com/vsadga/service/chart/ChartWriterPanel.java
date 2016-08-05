package pl.com.vsadga.service.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Paint;
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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

public class ChartWriterPanel extends JPanel {

	private OHLCDataset ohlcDataset;
	
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
		ohlcDataset = createPriceDataset("");
		
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
			return new BasicStroke(2);
		}
	}
	
	private class CustomXYBarRenderer extends XYBarRenderer {
		
		@Override
		public Paint getItemPaint(int row, int column) {
			return dataColor.get(column);
		}
		
		@Override
		public Stroke getSeriesStroke(int series) {
			return new BasicStroke(2);
		}
		
	}
	
	private IntervalXYDataset getVolumeDataset(final OHLCDataset priceDataset, final long barWidthInMilliseconds) {
		return new AbstractIntervalXYDataset() {
			
			@Override
			public Number getY(int series, int item) {
				return priceDataset.getVolume(series, item);
			}
			
			@Override
			public Number getX(int series, int item) {
				return priceDataset.getX(series, item);
			}
			
			@Override
			public int getItemCount(int series) {
				return priceDataset.getItemCount(series);
			}
			
			@Override
			public Number getStartY(int series, int item) {
				return new Double(0.0);
			}
			
			@Override
			public Number getStartX(int series, int item) {
				return priceDataset.getX(series, item).doubleValue() - barWidthInMilliseconds / 2;
			}
			
			@Override
			public Number getEndY(int series, int item) {
				return priceDataset.getVolume(series, item);
			}
			
			@Override
			public Number getEndX(int series, int item) {
				return priceDataset.getX(series, item).doubleValue() + barWidthInMilliseconds / 2;
			}
			
			@Override
			public Comparable getSeriesKey(int series) {
				return priceDataset.getSeriesKey(series) + "-Volume";
			}
			
			@Override
			public int getSeriesCount() {
				return priceDataset.getSeriesCount();
			}
		};
	}
	
	private JFreeChart createCombinedChart() {
		OHLCDataset data1 = createPriceDataset("abc");
		IntervalXYDataset data2 = getVolumeDataset(data1, 10 * 1000);
		
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
		//renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
		//		StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
		//		new SimpleDateFormat("dd-MMM-yyyy"),
		//		new DecimalFormat("0.00")));
		chartRenderer.setDrawOpenTicks(false);
		// ---
		CustomXYBarRenderer volumeRenderer = new CustomXYBarRenderer();
		volumeRenderer.setSeriesPaint(0, Color.black);
		volumeRenderer.setShadowVisible(false);
		//renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
		//		StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
		//		new SimpleDateFormat("dd-MMM-yyyy"),
		//		new DecimalFormat("0,000.00")));
		//renderer2.setDrawBarOutline(false);
		//renderer2.setSeriesStroke(0, new BasicStroke(2));
		
		
		// 3) Main & volume plot:
		XYPlot mainPlot = new XYPlot(data1, domainAxis, rangeAxis, chartRenderer);
		//mainPlot.setRenderer(chartRenderer);
		//plot1.setBackgroundPaint(Color.white);
		//plot1.setDomainGridlinePaint(Color.darkGray);
		//plot1.setRangeGridlinePaint(Color.darkGray);
		// ---
		XYPlot volumePlot = new XYPlot(data2, domainAxis, volumeAxis, volumeRenderer);
		volumePlot.setBackgroundPaint(Color.white);
		//plot2.setDomainGridlinePaint(Color.darkGray);
		//plot2.setRangeGridlinePaint(Color.darkGray);

		// 4) Zlozenie wykresow:
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(domainAxis);
		combinedPlot.add(mainPlot, 4);
		combinedPlot.add(volumePlot, 1);
		//cplot.setGap(5.0);
		//cplot.setDomainGridlinePaint(Color.white);
		//cplot.setDomainGridlinesVisible(true);
		//cplot.setDomainPannable(true);
		
		// 5) Utworzenie wykresu:
		JFreeChart chart = new JFreeChart("Wykres", null, combinedPlot, false);
		chart.setBackgroundPaint(Color.white);

		//JFreeChart chart = new JFreeChart("Wykres", JFreeChart.DEFAULT_TITLE_FONT, cplot, false);
		//ChartUtilities.applyCurrentTheme(chart);
		//renderer2.setShadowVisible(false);
		//renderer2.setBarPainter(new StandardXYBarPainter());

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

	private OHLCDataset createPriceDataset(String symbolName) {
		this.dataColor = new ArrayList<Color>();
		List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
		OHLCDataItem item = null;
		
		item = new OHLCDataItem(getDate(8, 0), new Double("0.00"), new Double("1325.17"), new Double("1324.65"), new Double("1325.16"), 405);
		dataItems.add(item);
		dataColor.add(Color.red);

		item = new OHLCDataItem(getDate(8, 5), new Double("0.00"), new Double("1325.84"), new Double("1324.14"), new Double("1324.75"), 833);
		dataItems.add(item);
		dataColor.add(Color.red);

		item = new OHLCDataItem(getDate(8, 10), new Double("0.00"), new Double("1326.13"), new Double("1325.53"), new Double("1325.73"), 161);
		dataItems.add(item);
		dataColor.add(Color.blue);
		item = new OHLCDataItem(getDate(8, 15), new Double("0.00"), new Double("1325.97"), new Double("1324.74"), new Double("1325.75"), 682);
		dataItems.add(item);
		dataColor.add(Color.blue);

		item = new OHLCDataItem(getDate(8, 20), new Double("0.00"), new Double("1326.55"), new Double("1325.83"), new Double("1325.95"), 165);
		dataItems.add(item);
		dataColor.add(Color.blue);
		item = new OHLCDataItem(getDate(8, 25), new Double("0.00"), new Double("1326.94"), new Double("1326.54"), new Double("1326.55"), 172);
		dataItems.add(item);
		dataColor.add(Color.blue);
		
		return new DefaultOHLCDataset(symbolName, dataItems.toArray(new OHLCDataItem[dataItems.size()]));
	}

	private Date getDate(int hour, int minute) {
		GregorianCalendar gregCal = new GregorianCalendar();
		gregCal.setTime(new Date());

		gregCal.set(Calendar.MINUTE, minute);
		gregCal.set(Calendar.HOUR_OF_DAY, hour);

		return gregCal.getTime();
	}

}
