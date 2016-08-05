import java.io.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.data.general.DefaultPieDataset; /* We will use DefaultPieDataset to define the data for the Pie Chart */
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.JFreeChart;
public class PDFPieChartExample {  
	//http://www.jfree.org/forum/viewtopic.php?f=10&t=27002
	//http://viralpatel.net/blogs/generate-pie-chart-bar-graph-in-pdf-using-itext-jfreechart/
	//http://www.jfree.org/phpBB2/viewtopic.php?t=616
	//https://vangjee.wordpress.com/2010/11/03/how-to-use-and-not-use-itext-and-jfreechart/
	//http://thinktibits.blogspot.com/2011/05/insert-pie-chart-pdf-itext-example.html
     public static void main(String[] args){
        try {
                /* We will define the data for the Pie Chart Using the Code below */
                /* Declare dataset object using the code below */
                DefaultPieDataset myPiedataset = new DefaultPieDataset();
                /* Define Values for the Pie Chart - Programming Languages Percentage Difficulty */
                myPiedataset.setValue("Java", 12.9);
                myPiedataset.setValue("C++", 37.9);
                myPiedataset.setValue("C", 86.5);
                myPiedataset.setValue("VB", 80.5);
                myPiedataset.setValue("Shell Script", 19.5);
				
                /* With the dataset defined for Pie Chart, we can invoke a method in ChartFactory object to create Pie Chart and Return a JFreeChart object*/
                /* This method returns a JFreeChart object back to us */
                /* We specify the chart title, dataset, legend, tooltip and URLs in this method as input */
                JFreeChart PDFPieChart=ChartFactory.createPieChart("Programming - Pie Chart Example",myPiedataset,true,true,false);
                /* We have a Pie chart object, and now need to find a procedure to insert it into PDF using iText */
                int width=640; /* Width of our chart */
                int height=480; /* Height of our chart */                
				
                Document PieChart=new Document(new Rectangle(width,height)); /* Create a New Document Object for PDF */                
                /* Create PDF Writer Object that will physically write the PDF file to File Output Stream */
                PdfWriter writer=PdfWriter.getInstance(PieChart,new FileOutputStream("Add_Pie_Chart_Using_JFreeChart.pdf"));
                /* Ready with document objects, open the document object to push contents */
                PieChart.open();
                /* Add some Metadata to identify document later */
                PieChart.addTitle("How to Add a Pie Chart to a PDF file using iText");
                PieChart.addAuthor("Thinktibits");                
                PieChart.addKeywords("iText,PieChart,JFreeChart,PDF,Example Tutorial");
                /* Get Direct Content of the PDF document for writing */
                PdfContentByte Add_Chart_Content= writer.getDirectContent();
                /* Create a template using the PdfContent Byte object */
                PdfTemplate template_Chart_Holder=Add_Chart_Content.createTemplate(width,height);
                /* Create a 2D graphics object and Rectangle object as before to write on the template */
                Graphics2D Graphics_Chart=template_Chart_Holder.createGraphics(width,height,new DefaultFontMapper());                
                Rectangle2D Chart_Region=new Rectangle2D.Double(0,0,540,380);
                /* Invoke the draw method passing the Graphics and Rectangle 2D object to draw the piechart */
                PDFPieChart.draw(Graphics_Chart,Chart_Region);            
                Graphics_Chart.dispose();
                /* Add template to PdfContentByte and then to the PDF document */
                Add_Chart_Content.addTemplate(template_Chart_Holder,0,0);
                /* Close the Document, writer will create a beautiful Pie chart inside the PDF document */
                PieChart.close();
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
}

ackage org.jstockchart.plot;

import java.awt.BasicStroke;

import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jstockchart.area.PriceArea;
import org.jstockchart.area.TimeseriesArea;
import org.jstockchart.area.VolumeArea;
import org.jstockchart.axis.TimeseriesDateAxis;
import org.jstockchart.axis.TimeseriesNumberAxis;
import org.jstockchart.axis.logic.CentralValueAxis;
import org.jstockchart.axis.logic.LogicDateAxis;
import org.jstockchart.axis.logic.LogicNumberAxis;
import org.jstockchart.dataset.TimeseriesDataset;

/**
 * Creates <code>CombinedDomainXYPlot</code> and <code>XYPlot</code> for the
 * timeseries chart.
 * 
 * @author Sha Jiang
 */
public class TimeseriesPlot {

    private static final long serialVersionUID = 8799771872991017065L;

    private TimeseriesDataset dataset = null;

    private SegmentedTimeline timeline = null;

    private TimeseriesArea timeseriesArea = null;

    /**
     * Creates a new <code>TimeseriesPlot</code> instance.
     * 
     * @param dataset
     *            timeseries data set(<code>null</code> not permitted).
     * @param timeline
     *            a "segmented" timeline.
     * @param timeseriesArea
     *            timeseries area.
     */
    public TimeseriesPlot(TimeseriesDataset dataset,
            SegmentedTimeline timeline, TimeseriesArea timeseriesArea) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;

        this.timeline = timeline;

        if (timeseriesArea == null) {
            throw new IllegalArgumentException(
                    "Null 'timeseriesArea' argument.");
        }
        this.timeseriesArea = timeseriesArea;
    }

    private CombinedDomainXYPlot createCombinedXYPlot() {
        LogicDateAxis logicDateAxis = timeseriesArea.getlogicDateAxis();
        TimeseriesDateAxis dateAxis = new TimeseriesDateAxis(logicDateAxis
                .getLogicTicks());
        if (timeline != null) {
            dateAxis.setTimeline(timeline);
        }

        CombinedDomainXYPlot combinedDomainXYPlot = new CombinedDomainXYPlot(
                dateAxis);
        combinedDomainXYPlot.setGap(timeseriesArea.getGap());
        combinedDomainXYPlot.setOrientation(timeseriesArea.getOrientation());
        combinedDomainXYPlot.setDomainAxis(dateAxis);
        combinedDomainXYPlot.setDomainAxisLocation(timeseriesArea
                .getDateAxisLocation());
        combinedDomainXYPlot.setDomainPannable(true);
        combinedDomainXYPlot.setRangePannable(true);

        if (timeseriesArea.getPriceWeight() <= 0
                && timeseriesArea.getVolumeWeight() <= 0) {
            throw new IllegalArgumentException(
                    "Illegal weight value: priceWeight="
                            + timeseriesArea.getPriceWeight()
                            + ", volumeWeight="
                            + timeseriesArea.getVolumeWeight());
        }

        if (timeseriesArea.getPriceWeight() > 0) {
            XYPlot pricePlot = createPricePlot();
            combinedDomainXYPlot
                    .add(pricePlot, timeseriesArea.getPriceWeight());
        }

        if (timeseriesArea.getVolumeWeight() > 0) {
            XYPlot volumePlot = createVolumePlot();
            combinedDomainXYPlot.add(volumePlot, timeseriesArea
                    .getVolumeWeight());
        }

        return combinedDomainXYPlot;
    }

    private XYPlot createPricePlot() {
        PriceArea priceArea = timeseriesArea.getPriceArea();
        TimeSeriesCollection priceDataset = new TimeSeriesCollection();
        priceDataset.addSeries(dataset.getPriceTimeSeries().getTimeSeries());
        if (priceArea.isAverageVisible()) {
            priceDataset.addSeries(dataset.getAverageTimeSeries()
                    .getTimeSeries());
        }

        CentralValueAxis logicPriceAxis = priceArea.getLogicPriceAxis();
        TimeseriesNumberAxis priceAxis = new TimeseriesNumberAxis(
                logicPriceAxis.getLogicTicks());
        XYLineAndShapeRenderer priceRenderer = new XYLineAndShapeRenderer(true,
                false);
        priceAxis.setUpperBound(logicPriceAxis.getUpperBound());
        priceAxis.setLowerBound(logicPriceAxis.getLowerBound());
        priceRenderer.setSeriesPaint(0, priceArea.getPriceColor());
        priceRenderer.setSeriesPaint(1, priceArea.getAverageColor());

        TimeseriesNumberAxis rateAxis = new TimeseriesNumberAxis(logicPriceAxis
                .getRatelogicTicks());
        rateAxis.setUpperBound(logicPriceAxis.getUpperBound());
        rateAxis.setLowerBound(logicPriceAxis.getLowerBound());

        XYPlot plot = new XYPlot(priceDataset, null, priceAxis, priceRenderer);
        plot.setBackgroundPaint(priceArea.getBackgroudColor());
        plot.setOrientation(priceArea.getOrientation());
        plot.setRangeAxisLocation(priceArea.getPriceAxisLocation());

        if (priceArea.isRateVisible()) {
            plot.setRangeAxis(1, rateAxis);
            plot.setRangeAxisLocation(1, priceArea.getRateAxisLocation());
            plot.setDataset(1, null);
            plot.mapDatasetToRangeAxis(1, 1);
        }

        if (priceArea.isMarkCentralValue()) {
            Number centralPrice = logicPriceAxis.getCentralValue();
            if (centralPrice != null) {
                plot.addRangeMarker(new ValueMarker(centralPrice.doubleValue(),
                        priceArea.getCentralPriceColor(), new BasicStroke()));
            }
        }
        return plot;
    }

    private XYPlot createVolumePlot() {
        VolumeArea volumeArea = timeseriesArea.getVolumeArea();
        LogicNumberAxis logicVolumeAxis = volumeArea.getLogicVolumeAxis();

        TimeseriesNumberAxis volumeAxis = new TimeseriesNumberAxis(
                logicVolumeAxis.getLogicTicks());
        volumeAxis.setUpperBound(logicVolumeAxis.getUpperBound());
        volumeAxis.setLowerBound(logicVolumeAxis.getLowerBound());
        volumeAxis.setAutoRangeIncludesZero(false);
        XYBarRenderer volumeRenderer = new XYBarRenderer();
        volumeRenderer.setSeriesPaint(0, volumeArea.getVolumeColor());
        volumeRenderer.setShadowVisible(false);

        XYPlot plot = new XYPlot(new TimeSeriesCollection(dataset
                .getVolumeTimeSeries()), null, volumeAxis, volumeRenderer);
        plot.setBackgroundPaint(volumeArea.getBackgroudColor());
        plot.setOrientation(volumeArea.getOrientation());
        plot.setRangeAxisLocation(volumeArea.getVolumeAxisLocation());
        return plot;
    }

    public CombinedDomainXYPlot getTimeseriesPlot() {
        return createCombinedXYPlot();
    }

    public TimeseriesDataset getDataset() {
        return dataset;
    }

    public void setDataset(TimeseriesDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;
    }

    public SegmentedTimeline getTimeline() {
        return timeline;
    }

    public void setTimeline(SegmentedTimeline timeline) {
        this.timeline = timeline;
    }

    public TimeseriesArea getTimeseriesArea() {
        return timeseriesArea;
    }

    public void setTimeseriesArea(TimeseriesArea timeseriesArea) {
        if (timeseriesArea == null) {
            throw new IllegalArgumentException(
                    "Null 'timeseriesArea' argument.");
        }
        this.timeseriesArea = timeseriesArea;
    }
}

package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.Range;
import org.jfree.data.time.Minute;
import org.jfree.data.xy.OHLCDataItem;
import org.jstockchart.JStockChartFactory;
import org.jstockchart.area.PriceArea;
import org.jstockchart.area.TimeseriesArea;
import org.jstockchart.area.VolumeArea;
import org.jstockchart.axis.TickAlignment;
import org.jstockchart.axis.logic.CentralValueAxis;
import org.jstockchart.axis.logic.LogicDateAxis;
import org.jstockchart.axis.logic.LogicNumberAxis;
import org.jstockchart.dataset.TimeseriesDataset;
import org.jstockchart.model.TimeseriesItem;
import org.jstockchart.util.DateUtils;

/**
 * Demo application for JStockChart timeseries.
 * 
 * @author Sha Jiang
 */
public class TimeseriesChartDemo {

    public static int period = 400;

    public static void main(String[] args) throws IOException {
        String imageDir = "./images";
        File images = new File(imageDir);
        if (!images.exists()) {
            images.mkdir();
        }
        String imageFile = imageDir + "/jstockchart-timeseries.png";


        Date startTime = DateUtils.createDate(2008, 1, 1, 9, 30, 0);
        Date endTime = DateUtils.createDate(2008, 1, 1, 15, 0, 0);
        // 'data' is a list of TimeseriesItem instances.
        List<TimeseriesItem> data = getData("AAPL", period, "d");

        // the 'timeline' indicates the segmented time range '00:00-11:30, 13:00-24:00'.
        SegmentedTimeline timeline = new SegmentedTimeline(
                SegmentedTimeline.DAY_SEGMENT_SIZE, 1351, 89);
        timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 780
                * SegmentedTimeline.DAY_SEGMENT_SIZE);

        // Creates timeseries data set.
        TimeseriesDataset dataset = new TimeseriesDataset(Minute.class, 1,
                timeline, true);
        dataset.addDataItems(data);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(','); 
        DecimalFormat df = new DecimalFormat(".##", otherSymbols);

        // Creates logic price axis.
        CentralValueAxis logicPriceAxis = new CentralValueAxis(
                dataset.getPriceTimeSeries().getTimeSeries().getValue(data.size()-1).doubleValue(), new Range(
                        dataset.getMinPrice().doubleValue(), dataset
                                .getMaxPrice().doubleValue()), 9,
                                df);
        PriceArea priceArea = new PriceArea(logicPriceAxis);

        // Creates logic volume axis.
        LogicNumberAxis logicVolumeAxis = new LogicNumberAxis(new Range(dataset
                .getMinVolume().doubleValue(), dataset.getMaxVolume()
                .doubleValue()), 5, new DecimalFormat("0"));
        VolumeArea volumeArea = new VolumeArea(logicVolumeAxis);

        TimeseriesArea timeseriesArea = new TimeseriesArea(priceArea,
                volumeArea, createlogicDateAxis(DateUtils
                        .createDate(2008, 1, 1)));

        JFreeChart jfreechart = JStockChartFactory.createTimeseriesChart(
                "Stock chart test with two seperate displays", dataset, timeline, timeseriesArea,
                false);


        JFrame outside = new JFrame();
        ChartPanel chartPanel = new ChartPanel(jfreechart, false);

        chartPanel.setMouseWheelEnabled(true);

        outside.add(chartPanel);

        outside.setVisible(true);

        ChartUtilities
                .saveChartAsPNG(new File(imageFile), jfreechart, 545, 300);
    }

    // Specifies date axis ticks.
    private static LogicDateAxis createlogicDateAxis(Date baseDate) {
        LogicDateAxis logicDateAxis = new LogicDateAxis(baseDate,
                new SimpleDateFormat("HH:mm"));
        logicDateAxis.addDateTick("09:30", TickAlignment.START);
        logicDateAxis.addDateTick("10:00");
        logicDateAxis.addDateTick("10:30");
        logicDateAxis.addDateTick("11:00");
        logicDateAxis.addDateTick("11:30", TickAlignment.END);
        logicDateAxis.addDateTick("13:00", TickAlignment.START);
        logicDateAxis.addDateTick("13:30");
        logicDateAxis.addDateTick("14:00");
        logicDateAxis.addDateTick("14:30", TickAlignment.END);
        logicDateAxis.addDateTick("15:00", TickAlignment.END);
        return logicDateAxis;
    }

    static List<TimeseriesItem> dataItems;

    static boolean TodayAdded = true;

    static ArrayList<Double> prices;
    static ArrayList<Date> dates;

    static List<TimeseriesItem> getData(String stockSymbol, int periodToLoad, String periodUnit) {

        TodayAdded = true;

        dataItems = new ArrayList<TimeseriesItem>();

        Date today = new Date();
        today = addDays(today, 1);
        Date beginDate = addDays(today, -periodToLoad);

        GregorianCalendar BEGIN = (GregorianCalendar) DateToCalendar(beginDate);
        GregorianCalendar END   = (GregorianCalendar) DateToCalendar(today);

        String QUOTE = constructURL(stockSymbol, BEGIN, END, periodUnit);

        try {
            String strUrl = QUOTE;
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateFormat df = new SimpleDateFormat("y-M-d");

            dates = new ArrayList<Date>();
            prices = new ArrayList<Double>();

            String inputLine;
            in.readLine();
            int counter = 0;

            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date date       = df.parse( st.nextToken() );
                double open     = Double.parseDouble( st.nextToken() );
                double high     = Double.parseDouble( st.nextToken() );
                double low      = Double.parseDouble( st.nextToken() );
                double close    = Double.parseDouble( st.nextToken() );
                double volume   = Double.parseDouble( st.nextToken() );
                double adjClose = Double.parseDouble( st.nextToken() );

                double price = close;

                dataItems.add(new TimeseriesItem(date, close, volume));

                System.out.println(close);

                dates.add(date);
                prices.add(close);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Reversal of dates
        Collections.reverse(dates);
        Collections.reverse(prices);
        //Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest


        return dataItems;
    }

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static String constructURL(String symbol, Calendar start, Calendar end, String periodUnit) {
        return "http://ichart.finance.yahoo.com/table.csv" +
    "?s=" +
                symbol + 
    "&a=" +
                Integer.toString(start.get(Calendar.MONTH)) +
    "&b=" +
                start.get(Calendar.DAY_OF_MONTH) +
    "&c=" +
                Integer.toString(start.get(Calendar.YEAR)) +
    "&d=" +
                Integer.toString(end.get(Calendar.MONTH)) +
    "&e=" +
                Integer.toString(end.get(Calendar.DAY_OF_MONTH)) +
    "&f=" +
                Integer.toString(end.get(Calendar.YEAR)) +
    "&g=" +
                periodUnit +
    "&ignore=.csv";
    }

    public static Calendar DateToCalendar(Date date){ 
          Calendar cal = Calendar.getInstance();
          cal.setTime(date);
          return cal;
        }
}

// Inspirations:
// -------------
// a) http://www.roseindia.net/chartgraphs/candle-stick-chart.shtml
// b) 3 times nearly the same code:
// http://www.jfree.org/forum/viewtopic.php?f=10&t=24521
// http://stackoverflow.com/questions/18413534/jfreechart-crashes-when-using-yahoo-finance-quotes
// http://www.jfree.org/forum/viewtopic.php?f=10&t=24521

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

public class Exemple144_JFreeChart_Candlestick_Chart {

    public static void main(String args[]) {

        // 1. Download MSFT quotes from Yahoo Finance and store them as OHLCDataItem
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        try {
            String strUrl = "http://ichart.yahoo.com/table.csv?s=MSFT&a=3&b=1&c=2013&d=3&e=15&f=2050&g=d";
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateFormat df = new SimpleDateFormat("y-M-d");

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date date = df.parse(st.nextToken());
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());

                // adjust data:
                open = open * adjClose / close;
                high = high * adjClose / close;
                low = low * adjClose / close;

                OHLCDataItem item = new OHLCDataItem(date, open, high, low, adjClose, volume);
                dataItems.add(item);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(dataItems); // Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest.
        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        OHLCDataset dataset = new DefaultOHLCDataset("MSFT", data);

        // 2. Create chart
        JFreeChart chart = ChartFactory.createCandlestickChart("MSFT", "Time", "Price", dataset, false);

        // 3. Set chart background
        chart.setBackgroundPaint(Color.white);

        // 4. Set a few custom plot features
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // light yellow = new Color(0xffffe0)
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        ((NumberAxis) plot.getRangeAxis()).setAutoRangeIncludesZero(false);

        // 5. Skip week-ends on the date axis
        ((DateAxis) plot.getDomainAxis()).setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        // 6. No volume drawn
        ((CandlestickRenderer) plot.getRenderer()).setDrawVolume(false);

        // 7. Create and display full-screen JFrame
        JFrame myFrame = new JFrame();
        myFrame.setResizable(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.add(new ChartPanel(chart), BorderLayout.CENTER);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Insets insets = kit.getScreenInsets(myFrame.getGraphicsConfiguration());
        Dimension screen = kit.getScreenSize();
        myFrame.setSize((int) (screen.getWidth() - insets.left - insets.right), (int) (screen.getHeight() - insets.top - insets.bottom));
        myFrame.setLocation((int) (insets.left), (int) (insets.top));
        myFrame.setVisible(true);
    }
}

package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Crosshair;
//import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.joda.time.LocalDate;

import model.ChartIndicator;
import model.IndicatorType;
import model.Stock;

public class TimeSeriesPanel extends JPanel implements ChartMouseListener {

	private ChartPanel chartPanel;
	private JFreeChart chart;
	private XYDataset dataSet;
	private XYPlot mainPlot;
	private CombinedDomainXYPlot combinedPlot;
	private DateAxis domainAxis;
	private NumberAxis rangeAxis;
	private XYItemRenderer chartRenderer;
	private Stock selectedStock;
	private String stockSymbol;
	private LocalDate startDate;
	private LocalDate endDate;
	private String rangeSelection;
	private LocalDate customEndDate;
	private LocalDate customStartDate;
	private String chartType;
	private boolean includeVolumeOverlay;
	private boolean includeVolumeBelow;
	private Stock compareStock;
	private ChartExpandDialog expandedChart;
	private Crosshair xCrosshair;
	private Crosshair yCrosshair;
	private Crosshair xCrosshairLabel;
	private Crosshair yCrosshairLabel;

	private JLabel stockChartLabel;
	private JLabel periodLabel;
	private JLabel rangeLabel;
	private JLabel startDateLabel;
	private JLabel endDateLabel;
	private JLabel chartTypeLabel;
	private JLabel scaleLabel;
	private JLabel volumeLabel;
	private JLabel indicatorsLabel;
	private JLabel colourLabel;
	private JLabel comparisonLabel;
	private JLabel stockComparisonLabel;
	private JComboBox stockChartCombo;
	private JComboBox periodCombo;
	private JComboBox rangeCombo;
	// want to change to date selectors
	private JTextField startDateField;
	private JTextField endDateField;
	private JComboBox chartTypeCombo;
	private ButtonGroup scaleButtons;
	private JRadioButton logarithmicRadio;
	private JRadioButton linearRadio;
	private JComboBox volumeCombo;
	private JComboBox compareStockCombo;
	private JButton indicatorButton;
	private JPanel toolPanel;
	private JButton refreshButton;
	private JButton expandButton;

	private Stock[] stockArray;

	public TimeSeriesPanel(List<Stock> stocks) {

		// TEST SETTINGS

		stockArray = stocks.toArray(new Stock[stocks.size()]);
		stockSymbol = stockArray[0].toString();

		// Create Tool Panel
		toolPanel();

		// Create JFreeChart object and chartPanel
		// Must implement check of selected chartType and create accordingly
		createChart();
		addChart();

		setLayout(new BorderLayout());
		add(chartPanel, BorderLayout.CENTER);

		add(toolPanel, BorderLayout.SOUTH);

		// Border
		Border inset = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border title = BorderFactory.createTitledBorder("Time Series");
		setBorder(BorderFactory.createCompoundBorder(inset, title));

	}

	// Method responsible for reading the downloaded csv data
	protected OHLCDataItem[] getData(LocalDate startDate, LocalDate endDate) {
		return selectedStock.getData(startDate, endDate);
	}

	protected AbstractXYDataset getDataSet(String stockSymbol, LocalDate startDate, LocalDate endDate) {
		// // This is the dataset we are going to create
		// DefaultOHLCDataset dataset = null;
		// // This is the data needed for the dataset
		// OHLCDataItem[] data;
		//
		// data = getData(stockSymbol, startDate, endDate);
		//
		// // Create a dataset, an Open, High, Low, Close dataset
		// dataset = new DefaultOHLCDataset(stockSymbol, data);

		return selectedStock.getDataSet(startDate, endDate);
	}

	private void toolPanel() {
		// Instantiate the JPanel
		toolPanel = new JPanel();

		// Instantiate all the labels for the tool panel
		// Potentially move this to anonymous methods in the gridbag addition
		stockChartLabel = new JLabel("Stock: ");
		periodLabel = new JLabel("Period: ");
		rangeLabel = new JLabel("Range: ", JLabel.RIGHT);
		startDateLabel = new JLabel("From: ");
		endDateLabel = new JLabel("To: ");
		chartTypeLabel = new JLabel("Chart Type: ");
		scaleLabel = new JLabel("Scale: ");
		volumeLabel = new JLabel("Volume: ");
		indicatorsLabel = new JLabel("Indicators: ");
		colourLabel = new JLabel("Colour: ");
		comparisonLabel = new JLabel("Comparison");
		stockComparisonLabel = new JLabel("Stock: ");

		// Check the state of the list of stocks before instantiating and
		// populating the combo box's which have a choice of those from active
		// models
		if (stockArray != null) {
			stockChartCombo = new JComboBox(stockArray);
			compareStockCombo = new JComboBox();
			compareStockCombo.addItem("None");
			for (Stock stock : stockArray) {
				compareStockCombo.addItem(stock);
			}
		}

		// Instantiate all toolPanel elements
		periodCombo = new JComboBox();
		rangeCombo = new JComboBox();
		startDateField = new JTextField(8);
		endDateField = new JTextField(8);
		chartTypeCombo = new JComboBox();
		scaleButtons = new ButtonGroup();
		logarithmicRadio = new JRadioButton("Logarithmic");
		linearRadio = new JRadioButton("Linear");
		volumeCombo = new JComboBox();
		indicatorButton = new JButton("Manage");
		refreshButton = new JButton("Refresh Chart");
		expandButton = new JButton("Expand Chart");

		// STOCK CHARTED
		selectedStock = stockArray[0];
		stockChartCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedStock = (Stock) stockChartCombo.getSelectedItem();
				stockSymbol = selectedStock.toString();
			}
		});

		// PERIOD
		DefaultComboBoxModel periodModel = new DefaultComboBoxModel();
		periodModel.addElement("Weekly");
		periodModel.addElement("Daily");
		periodCombo.setModel(periodModel);

		// RANGE
		DefaultComboBoxModel rangeModel = new DefaultComboBoxModel();
		// Change to array loop addition
		rangeModel.addElement("1M");
		rangeModel.addElement("3M");
		rangeModel.addElement("6M");
		rangeModel.addElement("YTD");
		rangeModel.addElement("1Y");
		rangeModel.addElement("2Y");
		rangeModel.addElement("5Y");
		rangeModel.addElement("10Y");
		rangeModel.addElement("Max");
		rangeModel.addElement("Custom");
		rangeModel.setSelectedItem("6M");
		rangeCombo.setModel(rangeModel);
		rangeLabel.setDisplayedMnemonic(KeyEvent.VK_U);
		rangeLabel.setLabelFor(rangeCombo);

		startDateLabel.setEnabled(false);
		startDateField.setEnabled(false);
		endDateLabel.setEnabled(false);
		endDateField.setEnabled(false);

		startDateField.setUI(new HintTextField(" YYYY-MM-DD", true, new Color(126, 126, 126)));
		endDateField.setUI(new HintTextField(" YYYY-MM-DD", true, new Color(126, 126, 126)));

		rangeSelection = (String) rangeCombo.getSelectedItem();

		rangeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rangeSelection = (String) rangeCombo.getSelectedItem();
				boolean custom = rangeSelection.equals("Custom");
				if (custom) {
					startDateLabel.setEnabled(true);
					startDateField.setEnabled(true);
					endDateLabel.setEnabled(true);
					endDateField.setEnabled(true);
				} else {
					startDateLabel.setEnabled(false);
					startDateField.setEnabled(false);
					endDateLabel.setEnabled(false);
					endDateField.setEnabled(false);
				}
			}
		});

		startDateField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				int location = startDateField.getCaretPosition();
				if (location == 4 || location == 7) {
					startDateField.setText(startDateField.getText() + "-");
				}
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});

		endDateField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				int location = startDateField.getCaretPosition();
				if (location == 4 || location == 7) {
					startDateField.setText(startDateField.getText() + "-");
				}
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});

		startDateField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
			}

			public void focusLost(FocusEvent arg0) {
				if (startDateField.getText().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
					customStartDate = LocalDate.parse(startDateField.getText());
				} else {
					Utils.print("Incorrect Date Format");
				}
			}
		});
		endDateField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
			}

			public void focusLost(FocusEvent arg0) {
				if (endDateField.getText().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
					customEndDate = LocalDate.parse(endDateField.getText());
				} else {
					Utils.print("Incorrect Date Format");
				}
			}
		});

		// CHART TYPE
		DefaultComboBoxModel chartTypeModel = new DefaultComboBoxModel();
		chartTypeModel.addElement("Line");
		chartTypeModel.addElement("Candle");
		chartTypeModel.addElement("Bar");
		chartTypeModel.addElement("Area");
		chartTypeCombo.setModel(chartTypeModel);

		chartType = "Line";

		chartTypeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chartType = (String) chartTypeCombo.getSelectedItem();
			}
		});

		// SCALE
		scaleButtons.add(linearRadio);
		scaleButtons.add(logarithmicRadio);
		linearRadio.setSelected(true);
		linearRadio.setActionCommand("linear");
		logarithmicRadio.setActionCommand("logarithmic");

		// VOLUME
		volumeCombo.addItem("None");
		volumeCombo.addItem("Overlay");
		volumeCombo.addItem("Below");
		includeVolumeOverlay = false;
		includeVolumeBelow = false;
		volumeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				includeVolumeOverlay = volumeCombo.getSelectedItem().equals("Overlay");
				includeVolumeBelow = volumeCombo.getSelectedItem().equals("Below");
			}
		});

		// INDICATORS
		indicatorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedStock.editIndicatorDialog();
			}
		});

		///// parameter field should update based on the indicator chosen
		///// eventually want a list to be able to add indicators

		// COMPARISON
		compareStock = null;
		compareStockCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!compareStockCombo.getSelectedItem().equals("None")) {
					compareStock = (Stock) compareStockCombo.getSelectedItem();
				} else {
					compareStock = null;
				}
			}
		});
		// REFRESH BUTTON
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// refreshChart();
				createChart();
				chartPanel.setChart(chart);
			}
		});
		refreshButton.setMnemonic(KeyEvent.VK_R);

		// EXPAND BUTTON
		expandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createChart();
				if (selectedStock.hasExpandedChart()) {
					selectedStock.refreshExpandedChart(chart);
				} else {
					selectedStock.createExpandedChart(chart);
				}
			}
		});

		layoutToolPanel();

		Dimension dim = getPreferredSize();
		dim.height = (160);
		toolPanel.setPreferredSize(dim);

		// Border
		Border innerBorder = BorderFactory.createTitledBorder("Chart Attributes");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		toolPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

	}

	private void layoutToolPanel() {
		toolPanel.setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();

		// LAYOUT

		gc.gridy = 0;
		// FIRST ROW///FIRST COLUMN///
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;

		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(stockChartLabel, gc);

		gc.gridx = 1;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(stockChartCombo, gc);

		// SECOND ROW///FIRST COLUMN///
		gc.gridy = 1;

		gc.weighty = 0.1;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(periodLabel, gc);

		gc.gridx = 1;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(periodCombo, gc);

		// THIRD ROW///FIRST COLUMN///
		gc.gridy = 2;

		gc.weighty = 0.1;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(scaleLabel, gc);

		gc.gridx = 1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(linearRadio, gc);

		gc.gridx = 1;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(logarithmicRadio, gc);

		// FIRST ROW///SECOND COLUMN///
		gc.gridy = 0;

		gc.weighty = 0.1;
		gc.gridx = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(chartTypeLabel, gc);

		gc.gridx = 3;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(chartTypeCombo, gc);
		// SECOND ROW///SECOND COLUMN///
		gc.gridy = 1;

		gc.weighty = 0.1;
		gc.gridx = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(rangeLabel, gc);

		gc.gridx = 3;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(rangeCombo, gc);

		gc.gridy = 2;

		gc.weighty = 0.1;
		gc.gridx = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(startDateLabel, gc);

		gc.gridx = 3;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(startDateField, gc);

		gc.gridy = 3;

		gc.weighty = 0.1;
		gc.gridx = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(endDateLabel, gc);

		gc.gridx = 3;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(endDateField, gc);

		// FIRST ROW///THIRD COLUMN///
		gc.gridy = 0;

		gc.weighty = 0.1;
		gc.gridx = 4;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(volumeLabel, gc);

		gc.gridx = 5;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(volumeCombo, gc);

		// SECOND ROW///THIRD COLUMN///
		gc.gridy = 1;

		gc.weighty = 0.1;
		gc.gridx = 4;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(indicatorsLabel, gc);

		gc.gridx = 5;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_START;

		toolPanel.add(indicatorButton, gc);

		// THIRD ROW///THIRD COLUMN///
		gc.gridx = 4;
		gc.weighty = 0.1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(comparisonLabel, gc);

		gc.gridx = 5;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		toolPanel.add(compareStockCombo, gc);

		// FIRST ROW///FOURTH COLUMN///
		gc.gridy = 1;

		gc.weighty = 0.1;
		gc.gridx = 7;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(refreshButton, gc);

		gc.gridy = 2;

		gc.weighty = 0.1;
		gc.gridx = 7;
		gc.anchor = GridBagConstraints.LINE_END;
		toolPanel.add(expandButton, gc);

		// gc.gridx = 7;
		// gc.gridy = 0;
		// gc.anchor = GridBagConstraints.LINE_START;
		// toolPanel.add(compareStockCombo, gc);

	}

	public void setStocks(List<Stock> stocks) {
		stockArray = stocks.toArray(new Stock[stocks.size()]);

		if (stockChartCombo != null) {
			stockChartCombo.setModel(new DefaultComboBoxModel());
			// stockChartCombo.removeAllItems();
		} else {
			System.out.println("Null stockChartCombo");
		}
		if (compareStockCombo != null) {
			compareStockCombo.setModel(new DefaultComboBoxModel());
			compareStockCombo.addItem("None");
			// stockChartCombo.removeAllItems();
		} else {
			System.out.println("Null compareStockCombo");
		}

		for (Stock s : stockArray) {
			stockChartCombo.addItem(s);
			compareStockCombo.addItem(s);
		}
	}

	private void getEndDate() {
		if (rangeSelection.equals("Custom") && customEndDate != null) {
			endDate = customEndDate;
		} else {
			endDate = new LocalDate();
		}
	}

	private void getStartDate() {
		if (rangeSelection.equals("1M")) {
			startDate = new LocalDate().minusMonths(1);
		} else if (rangeSelection.equals("3M")) {
			startDate = new LocalDate().minusMonths(3);
		} else if (rangeSelection.equals("6M")) {
			startDate = new LocalDate().minusMonths(6);
		} else if (rangeSelection.equals("YTD")) {
			int year = new LocalDate().getYear();
			startDate = new LocalDate(year, 1, 1);
		} else if (rangeSelection.equals("1Y")) {
			startDate = new LocalDate().minusYears(1);
		} else if (rangeSelection.equals("2Y")) {
			startDate = new LocalDate().minusYears(2);
		} else if (rangeSelection.equals("5Y")) {
			startDate = new LocalDate().minusYears(5);
		} else if (rangeSelection.equals("10Y")) {
			startDate = new LocalDate().minusYears(10);
		} else if (rangeSelection.equals("Max")) {
			startDate = new LocalDate().minusYears(100);
		} else if (rangeSelection.equals("Custom") && customStartDate != null) {
			startDate = customStartDate;
		} else {
			Utils.print("No range selected");
		}
	}

	public void createChart() {
		//TODO refactor this whole method
		//TODO refactor legend addition and labelgenerator
		
		domainAxis = new DateAxis("Date");
		rangeAxis = new NumberAxis("Price");
		rangeAxis.setAutoRangeIncludesZero(false);
		domainAxis.setLowerMargin(0.02); // reduce the default margins
		domainAxis.setUpperMargin(0.02);

		getEndDate();
		getStartDate();
		dataSet = getDataSet(stockSymbol, startDate, endDate);
		mainPlot = new XYPlot(dataSet, domainAxis, rangeAxis, chartRenderer);

		if (chartType.equals("Line")) {
			chartRenderer = new XYLineAndShapeRenderer(true, false);
			chartRenderer.setSeriesPaint(0, Color.white);
			mainPlot.setRenderer(chartRenderer);
		} else if (chartType.equals("Candle")) {
			chartRenderer = new CandlestickRenderer();
			if (chartRenderer instanceof CandlestickRenderer) {
				((CandlestickRenderer) chartRenderer).setDrawVolume(false);
				((CandlestickRenderer) chartRenderer).setSeriesPaint(0, Utils.chartBackgroundColor);
			}
			mainPlot.setRenderer(chartRenderer);

		} else if (chartType.equals("Bar")) {
			chartRenderer = new HighLowRenderer();
			mainPlot.setRenderer(chartRenderer);
			domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
		} else if (chartType.equals("Area")) {
			chartRenderer = new XYAreaRenderer();
			mainPlot.setRenderer(chartRenderer);
			mainPlot.setForegroundAlpha(0.5F);
			domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
		} else {
			System.out.println("No chart type selected");
		}

		combinedPlot = new CombinedDomainXYPlot(domainAxis);
		combinedPlot.add(mainPlot, 4);
		LegendTitle mainLegendtitle = new LegendTitle(mainPlot);
		mainLegendtitle.setItemFont(new Font("Dialog", 0, 9));
		mainLegendtitle.setBackgroundPaint(new Color(255, 255, 255, 200));
		mainLegendtitle.setFrame(new BlockBorder(Color.white));
		mainLegendtitle.setPosition(RectangleEdge.BOTTOM);
		XYTitleAnnotation mainXYtitleannotation = new XYTitleAnnotation(0.01D, 0.08D, mainLegendtitle,
				RectangleAnchor.BOTTOM_LEFT);
		mainXYtitleannotation.setMaxWidth(0.47999999999999998D);
		mainPlot.addAnnotation(mainXYtitleannotation);

		if (includeVolumeBelow) {
			IntervalXYDataset volumeDataset = getVolumeDataset((OHLCDataset) dataSet, 24 * 60 * 60 * 1000); // Each
																											// bar
																											// is
																											// 24
																											// hours
																											// wide
			NumberAxis volumeAxis = new NumberAxis("Volume");
			XYBarRenderer volumeRenderer = new XYBarRenderer();
			XYPlot volumePlot = new XYPlot(volumeDataset, domainAxis, volumeAxis, volumeRenderer);
			volumeRenderer.setSeriesPaint(0, Utils.logoGreen);
			volumeRenderer.setShadowVisible(false);
			volumePlot.setBackgroundPaint(Utils.chartBackgroundColor);
			combinedPlot.add(volumePlot, 1);
		} else if (includeVolumeOverlay) {
			NumberAxis numberaxis1 = new NumberAxis("Volume");
			numberaxis1.setUpperMargin(0.15f);
			mainPlot.setDataset(1, getVolumeDataset((OHLCDataset) dataSet, 24 * 60 * 60 * 1000));
			mainPlot.setRangeAxis(1, numberaxis1);
			mainPlot.mapDatasetToRangeAxis(1, 1);
			XYBarRenderer xybarrenderer = new XYBarRenderer(0.10000000000000001D);
			mainPlot.setRenderer(1, xybarrenderer);
			xybarrenderer.setBarPainter(new StandardXYBarPainter());
			xybarrenderer.setSeriesPaint(0, Utils.logoGreenAlpha);
			xybarrenderer.setShadowVisible(false);
		}

		if (compareStock != null) {
			NumberAxis compareRangeAxis = new NumberAxis(compareStock.toString() + "'s Price");
			compareRangeAxis.setAutoRangeIncludesZero(false);
			mainPlot.setDataset(2, compareStock.getDataSet(startDate, endDate));
			mainPlot.setRangeAxis(2, compareRangeAxis);
			mainPlot.mapDatasetToRangeAxis(1, 2);
			XYLineAndShapeRenderer compareRenderer = new XYLineAndShapeRenderer(true, false);
			compareRenderer.setSeriesPaint(0, Color.red);
			mainPlot.setRenderer(2, compareRenderer);
		}
		int i = 3;
		for (final ChartIndicator indicator : selectedStock.getIndicators().values()) {
			indicator.getData(startDate, endDate);
			if (indicator.getType().equals(IndicatorType.BB)) {
				mainPlot.setDataset(i, indicator.dataset1);
				XYLineAndShapeRenderer indicatorRenderer1 = new XYLineAndShapeRenderer(true, false);
				indicatorRenderer1.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				mainPlot.setRenderer(i, indicatorRenderer1);
				i++;
				mainPlot.setDataset(i, indicator.dataset2);
				XYLineAndShapeRenderer indicatorRenderer2 = new XYLineAndShapeRenderer(true, false);
				indicatorRenderer2.setSeriesPaint(0, Utils.chartRed1);
				mainPlot.setRenderer(i, indicatorRenderer2);
				i++;
				mainPlot.setDataset(i, indicator.dataset3);
				XYLineAndShapeRenderer indicatorRenderer3 = new XYLineAndShapeRenderer(true, false);
				indicatorRenderer3.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				mainPlot.setRenderer(i, indicatorRenderer3);
				i++;
				indicatorRenderer1.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "Bollinger Bands";
						return label;
					}
				});
			} else if (indicator.getType().equals(IndicatorType.EMA) || indicator.getType().equals(IndicatorType.SMA)) {
				mainPlot.setDataset(i, indicator.dataset1);
				XYLineAndShapeRenderer indicatorRenderer = new XYLineAndShapeRenderer(true, false);
				indicatorRenderer.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				mainPlot.setRenderer(i, indicatorRenderer);
				i++;
				indicatorRenderer.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						if(indicator.getType().equals(IndicatorType.EMA)){
							String label = "EMA";
							return label;
						}else{
							return "SMA";
						}
						
					}
				});
			} else if (indicator.getType().equals(IndicatorType.MFI)) {
				NumberAxis mfiAxis = new NumberAxis("MFI");
				XYLineAndShapeRenderer mfiRenderer = new XYLineAndShapeRenderer(true, false);
				XYPlot mfiPlot = new XYPlot(indicator.dataset1, domainAxis, mfiAxis, mfiRenderer);
				mfiRenderer.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				mfiPlot.setBackgroundPaint(Utils.chartBackgroundColor);
				combinedPlot.add(mfiPlot, 1);
				mfiRenderer.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "MFI";
						return label;
					}
				});
				
				LegendTitle legendtitle = new LegendTitle(mfiPlot);
				legendtitle.setItemFont(new Font("Dialog", 0, 9));
				legendtitle.setBackgroundPaint(new Color(255, 255, 255, 200));
				legendtitle.setFrame(new BlockBorder(Color.white));
				legendtitle.setPosition(RectangleEdge.BOTTOM);
				XYTitleAnnotation xytitleannotation = new XYTitleAnnotation(0.01D, 0.08D, legendtitle,
						RectangleAnchor.BOTTOM_LEFT);
				xytitleannotation.setMaxWidth(0.47999999999999998D);
				mfiPlot.addAnnotation(xytitleannotation);
			} else if (indicator.getType().equals(IndicatorType.RSI)) {
				NumberAxis rsiAxis = new NumberAxis("RSI");
				XYLineAndShapeRenderer rsiRenderer = new XYLineAndShapeRenderer(true, false);
				XYPlot rsiPlot = new XYPlot(indicator.dataset1, domainAxis, rsiAxis, rsiRenderer);
				rsiRenderer.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				rsiRenderer.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "RSI";
						return label;
					}
				});
				rsiPlot.setBackgroundPaint(Utils.chartBackgroundColor);
				combinedPlot.add(rsiPlot, 1);
				
				LegendTitle legendtitle = new LegendTitle(rsiPlot);
				legendtitle.setItemFont(new Font("Dialog", 0, 9));
				legendtitle.setBackgroundPaint(new Color(255, 255, 255, 200));
				legendtitle.setFrame(new BlockBorder(Color.white));
				legendtitle.setPosition(RectangleEdge.BOTTOM);
				XYTitleAnnotation xytitleannotation = new XYTitleAnnotation(0.01D, 0.08D, legendtitle,
						RectangleAnchor.BOTTOM_LEFT);
				xytitleannotation.setMaxWidth(0.47999999999999998D);
				rsiPlot.addAnnotation(xytitleannotation);
			} else if (indicator.getType().equals(IndicatorType.SO)) {

				NumberAxis soAxis = new NumberAxis("Stoch");
				XYLineAndShapeRenderer soRenderer1 = new XYLineAndShapeRenderer(true, false);
				XYPlot soPlot = new XYPlot(indicator.dataset1, domainAxis, soAxis, soRenderer1);
				soRenderer1.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				soRenderer1.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "%K";
						return label;
					}
				});
				
				soPlot.setDataset(1, indicator.dataset2);
				XYLineAndShapeRenderer soRenderer2 = new XYLineAndShapeRenderer(true, false);
				soRenderer2.setSeriesPaint(0, Utils.chartRed2);
				soPlot.setRenderer(1, soRenderer2);
				soRenderer2.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "%D";
						return label;
					}
				});
				soPlot.setBackgroundPaint(Utils.chartBackgroundColor);
				combinedPlot.add(soPlot, 2);
				
				LegendTitle legendtitle = new LegendTitle(soPlot);
				legendtitle.setItemFont(new Font("Dialog", 0, 9));
				legendtitle.setBackgroundPaint(new Color(255, 255, 255, 200));
				legendtitle.setFrame(new BlockBorder(Color.white));
				legendtitle.setPosition(RectangleEdge.BOTTOM);
				XYTitleAnnotation xytitleannotation = new XYTitleAnnotation(0.01D, 0.08D, legendtitle,
						RectangleAnchor.BOTTOM_LEFT);
				xytitleannotation.setMaxWidth(0.47999999999999998D);
				soPlot.addAnnotation(xytitleannotation);
			} else if (indicator.getType().equals(IndicatorType.MACD)) {

				NumberAxis soAxis = new NumberAxis("MACD");
				XYLineAndShapeRenderer macdRenderer1 = new XYLineAndShapeRenderer(true, false);
				XYPlot macdPlot = new XYPlot(indicator.dataset1, domainAxis, soAxis, macdRenderer1);
				macdRenderer1.setSeriesPaint(0, Utils.getcolor(indicator.getType()));
				macdRenderer1.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "MACD";
						return label;
					}
				});
				
				
				macdPlot.setDataset(1, indicator.dataset2);
				XYLineAndShapeRenderer macdRenderer2 = new XYLineAndShapeRenderer(true, false);
				macdRenderer2.setSeriesPaint(0, Utils.chartBlue3);
				macdPlot.setRenderer(1, macdRenderer2);
				macdRenderer2.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "SIGNAL";
						return label;
					}
				});

				macdPlot.setDataset(2, getVolumeDataset((OHLCDataset) indicator.dataset3, 24 * 60 * 60 * 1000));
				XYBarRenderer macdRenderer3 = new XYBarRenderer();
				macdPlot.setRenderer(2, macdRenderer3);
				macdRenderer3.setBarPainter(new StandardXYBarPainter());
				macdRenderer3.setSeriesPaint(0, Utils.chartRedAlpha);
				macdRenderer3.setShadowVisible(false);
				macdRenderer3.setLegendItemLabelGenerator(new StandardXYSeriesLabelGenerator() {
					public String generateLabel(XYDataset dataset, int series) {
						String label = "HIST";
						return label;
					}
				});
				

				LegendTitle legendtitle = new LegendTitle(macdPlot);
				legendtitle.setItemFont(new Font("Dialog", 0, 9));
				legendtitle.setBackgroundPaint(new Color(255, 255, 255, 200));
				legendtitle.setFrame(new BlockBorder(Color.white));
				legendtitle.setPosition(RectangleEdge.BOTTOM);
				XYTitleAnnotation xytitleannotation = new XYTitleAnnotation(0.01D, 0.08D, legendtitle,
						RectangleAnchor.BOTTOM_LEFT);
				xytitleannotation.setMaxWidth(0.47999999999999998D);
				macdPlot.addAnnotation(xytitleannotation);

				macdPlot.setBackgroundPaint(Utils.chartBackgroundColor);
				combinedPlot.add(macdPlot, 2);

			}
		}

		chart = new JFreeChart(stockSymbol, null, combinedPlot, false);
		chart.setBackgroundPaint(Utils.backgroundColor);

		chartStyling();
	}

	public void chartStyling() {
		mainPlot.setBackgroundPaint(Utils.chartBackgroundColor);
	}

	public void chartMouseClicked(ChartMouseEvent event) {
	}

	public void chartMouseMoved(ChartMouseEvent event) {
		Rectangle2D dataArea = this.chartPanel.getScreenDataArea();
		JFreeChart chart = event.getChart();
		CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) chart.getPlot();
		XYPlot plot = (XYPlot) combinedPlot.getSubplots().get(0);
		ValueAxis xAxis = plot.getDomainAxis();
		double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
		// make the crosshairs disappear if the mouse is out of range
		if (!xAxis.getRange().contains(x)) {
			x = Double.NaN;
		}
		double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
		this.xCrosshair.setValue(x);
		this.yCrosshair.setValue(y);

		this.xCrosshairLabel.setValue(x);
		this.yCrosshairLabel.setValue(y);

	}

	public void addChart() {
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(300, 100));

		chartPanel.addChartMouseListener((ChartMouseListener) this);
		CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
		BasicStroke crossHairStroke = new BasicStroke(1.0f, // Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				2.0f, // Miter limit
				new float[] { 3.0f, 3.0f }, // Dash pattern
				0.0f);
		this.xCrosshair = new Crosshair(Double.NaN, Utils.logoGreen, crossHairStroke);

		this.yCrosshair = new Crosshair(Double.NaN, Utils.logoGreen, crossHairStroke);
		crosshairOverlay.addDomainCrosshair(xCrosshair);
		crosshairOverlay.addRangeCrosshair(yCrosshair);
		this.chartPanel.addOverlay(crosshairOverlay);

		CrosshairOverlay crosshairOverlay2 = new CrosshairOverlay();
		this.xCrosshairLabel = new Crosshair(Double.NaN, new Color(0, 0, 0, 0.0f), crossHairStroke);
		this.xCrosshairLabel.setLabelBackgroundPaint(Utils.logoGreen);
		this.xCrosshairLabel.setLabelOutlinePaint(Utils.logoGreen);
		this.xCrosshairLabel.setStroke(new BasicStroke(5.0f));
		this.xCrosshairLabel.setLabelOutlineVisible(false);
		this.xCrosshairLabel.setLabelVisible(true);

		this.yCrosshairLabel = new Crosshair(Double.NaN, new Color(0, 0, 0, 0.0f), crossHairStroke);
		this.yCrosshairLabel.setLabelBackgroundPaint(Utils.logoGreen);
		this.yCrosshairLabel.setLabelOutlinePaint(Utils.logoGreen);
		this.yCrosshairLabel.setStroke(new BasicStroke(5.0f));
		this.yCrosshairLabel.setLabelOutlineVisible(false);
		this.yCrosshairLabel.setLabelVisible(true);

		crosshairOverlay2.addDomainCrosshair(xCrosshairLabel);
		crosshairOverlay2.addRangeCrosshair(yCrosshairLabel);
		this.chartPanel.addOverlay(crosshairOverlay2);
	}

	protected static IntervalXYDataset getVolumeDataset(final OHLCDataset priceDataset,
			final long barWidthInMilliseconds) {
		return new AbstractIntervalXYDataset() {
			public int getSeriesCount() {
				return priceDataset.getSeriesCount();
			}

			public Comparable getSeriesKey(int series) {
				return priceDataset.getSeriesKey(series) + "-Volume";
			}

			public int getItemCount(int series) {
				return priceDataset.getItemCount(series);
			}

			public Number getX(int series, int item) {
				return priceDataset.getX(series, item);
			}

			public Number getY(int series, int item) {
				return priceDataset.getVolume(series, item);
			}

			public Number getStartX(int series, int item) {
				return priceDataset.getX(series, item).doubleValue() - barWidthInMilliseconds / 2;
			}

			public Number getEndX(int series, int item) {
				return priceDataset.getX(series, item).doubleValue() + barWidthInMilliseconds / 2;
			}

			public Number getStartY(int series, int item) {
				return new Double(0.0);
			}

			public Number getEndY(int series, int item) {
				return priceDataset.getVolume(series, item);
			}
		};
	}
https://github.com/kylebyrne/Agora/blob/master/Agora/src/gui/TimeSeriesPanel.java
}