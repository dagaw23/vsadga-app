public class PriceVolumeChart2 extends ApplicationFrame{

final static String filename = "A.txt";
*static TimeSeries t1 = new TimeSeries("49-day moving average");*

/**
 * Default constructor
 */
public PriceVolumeChart2(String title)
{
    super(title);
    JPanel panel = createDemoPanel();
    panel.setPreferredSize(new Dimension(500, 270));
    setContentPane(panel);
}

//create price dataset
//hard-coded here
private static OHLCDataset createPriceDataset(String filename)
{
    //the following data is taken from http://finance.yahoo.com/
    //for demo purposes...

    OHLCSeries s1 = new OHLCSeries(filename);

    try {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String inputLine;
        in.readLine();
        while ((inputLine = in.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(inputLine, ",");
            Date date       = df.parse( st.nextToken() );
            double open     = Double.parseDouble( st.nextToken() );
            double high     = Double.parseDouble( st.nextToken() );
            double low      = Double.parseDouble( st.nextToken() );
            double close    = Double.parseDouble( st.nextToken() );
            double volume   = Double.parseDouble( st.nextToken() );
            //double adjClose = Double.parseDouble( st.nextToken() );
            s1.add(new Day(date), open, high, low, close);
            *t1.add(new Day(date), close);*
        }
        in.close();
    }
    catch (Exception e) {
        e.printStackTrace();
    }



    OHLCSeriesCollection dataset = new OHLCSeriesCollection();
    dataset.addSeries(s1);

    return dataset;
}


//create volume dataset
private static IntervalXYDataset createVolumeDataset(String filename)
{
    //create dataset 2...
    TimeSeries s1 = new TimeSeries("Volume");

    try {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String inputLine;
        in.readLine();
        while ((inputLine = in.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(inputLine, ",");
            Date date = df.parse( st.nextToken() );
            st.nextToken();
            st.nextToken();
            st.nextToken();
            st.nextToken();
            double volume   = Double.parseDouble( st.nextToken() );
            //double adjClose = Double.parseDouble( st.nextToken() );
            s1.add(new Day(date), volume);
        }
        in.close();
    }
    catch (Exception e) {
        e.printStackTrace();
    }

    return new TimeSeriesCollection(s1);
}

private static JFreeChart createCombinedChart()
{
    OHLCDataset data1 = createPriceDataset(filename);

    XYItemRenderer renderer1 = new HighLowRenderer();
    renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
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

    //Overlay the Long-Term Trend Indicator
    *TimeSeries dataset3 = MovingAverage.createMovingAverage(t1, "LT", 49, 49);
    TimeSeriesCollection collection = new TimeSeriesCollection();
    collection.addSeries(dataset3);
    plot1.setDataset(1, collection);*
    plot1.setRenderer(1, new StandardXYItemRenderer());

    //add a second dataset (volume) and renderer
    IntervalXYDataset data2 = createVolumeDataset(filename);
    XYBarRenderer renderer2 = new XYBarRenderer();
    renderer2.setDrawBarOutline(false);
    renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
        StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
        new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")));
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


    //return the new combined chart
    JFreeChart chart = new JFreeChart("Sun Microsystems (SUNW)",
        JFreeChart.DEFAULT_TITLE_FONT, cplot, false);

    ChartUtilities.applyCurrentTheme(chart);
    renderer2.setShadowVisible(false);
    renderer2.setBarPainter(new StandardXYBarPainter());

    return chart;
}

//create a panel
public static JPanel createDemoPanel()
{
    JFreeChart chart = createCombinedChart();
    return new ChartPanel(chart);
}

public static void main(String[] args) {
    // TODO code application logic here
    PriceVolumeChart2 demo = new PriceVolumeChart2(
        "JFreeChart: CombinedXYPlotDemo1.java (base)");
    demo.pack();
    RefineryUtilities.centerFrameOnScreen(demo);
    demo.setVisible(true);
	
	JasperReport compiledReport = JasperCompileManager.compileReport("/path/plik.jrxml");
	
	HashMap<String, Object> parameters = new HashMap<String, Object>();
	parameters.put("bankDataBean", b);
	
	JasperPrint filledReport = JasperFillManager.fillReport(compiledReport, parameters, new JREmptyDataSource());
	JasperExportManager.exportReportToPdfFile(filledReport, "/path/file.pdf");
	
	chartParams.getGoldM5Path
	
	net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
}

						if (SD_FNWYRZAW.ne("DB") && SD_FNWYRZAW.ne("BI") && SD_FNWYRZAW.ne("KK") && SD_FNWYRZAW.ne("TR")
						&& SD_FNWYRZAW.ne("TS") && SD_FNWYRZAW.ne("RC") && SD_FNWYRZAW.ne("CM")
						&& SD_FNWYRZAW.ne("KC") && SD_FNWYRZAW.ne("BB"))
						
		if (tableName.equals("BANK") || tableName.equals("BRNCH") || tableName.equals("BNKID")
				|| tableName.equals("BRNID") || tableName.equals("BRNPR")) {
			if (basicDataFilenameDto.getContentDisc().equals("DB"))
				return true;
		} else if (tableName.equals("BIC")) {
			if (basicDataFilenameDto.getContentDisc().equals("BI"))
				return true;
		} else if (tableName.equals("KKISO") || tableName.equals("KODKR") || tableName.equals("KODWL")
				|| tableName.equals("KALEE") || tableName.equals("KALS2")) {
			if (basicDataFilenameDto.getContentDisc().equals("KK"))
				return true;
		} else if (tableName.equals("BCTDP") || tableName.equals("BCTIP")) {
			if (basicDataFilenameDto.getContentDisc().equals("TR"))
				return true;
		} else if (tableName.equals("T2SUB")) {
			if (basicDataFilenameDto.getContentDisc().equals("TS"))
				return true;
		} else if (tableName.equals("RCHTB")) {
			if (basicDataFilenameDto.getContentDisc().equals("RC"))
				return true;
		} else if (tableName.equals("CSMID")) {
			if (basicDataFilenameDto.getContentDisc().equals("CM"))
				return true;
		}
		
	Group SG_BIC = new Group();
	Group BI_BIC1 = SG_BIC.addMember(Var.CHAR, 8);
	Group BI_BIC2 = SG_BIC.addMember(Var.CHAR, 3);
	Group BI_BRNIDT = SG_BIC.addMember(Var.UNUMERIC, 8);
	Group BI_BICIND = SG_BIC.addMember(Var.CHAR, 1);
	Group BI_ACTDTE = SG_BIC.addMember(Var.UNUMERIC, 8);
	Group BI_EXPDTE = SG_BIC.addMember(Var.UNUMERIC, 8);
	Group BI_STSCT = SG_BIC.addMember(Var.CHAR, 1);
	Group BI_STSDD = SG_BIC.addMember(Var.CHAR, 1);
	Group BI_STSECH = SG_BIC.addMember(Var.CHAR, 1);
	
	select MAINT, MAXLEN, NAM1, CTRYCOD, rowid from kodkr where CTRYCOD = ?
	
	CTRYCOD        varchar2(2)			default ' ' not null,
	MAINT          varchar2(1)			default ' ' not null,
	MAXLEN         number(2)			default 0 	not null,
	NAM1           varchar2(35 char)	default ' ' not null,
	constraint pkkodkr primary key (CTRYCOD)

create table T2SUB
(
	ACTDTE		number(5)		default 0	not null,
	BICEE		varchar2(11)	default ' '	not null,
	BNK			varchar2(3) 	default ' '	not null,
	EXPDTE		number(5) 		default 0	not null,
	IBANSUB		varchar2(35) 	default ' '	not null,
	MAINT		varchar2(1) 	default ' '	not null,
	RTGSFLAG	varchar2(1) 	default ' '	not null,
	TYPACCT		varchar2(1) 	default ' '	not null
);

CREATE UNIQUE INDEX AP_T2SUB ON T2SUB (BICEE,BNK,IBANSUB,ACTDTE);


	Group SG_T2SUB = new Group();
	Group SU_BICEE = SG_T2SUB.addMember(Var.CHAR, 11);
	Group SU_BNK = SG_T2SUB.addMember(Var.CHAR, 3);
	Group SU_IBANSUB = SG_T2SUB.addMember(Var.CHAR, 35);
	Group SU_ACTDTE = SG_T2SUB.addMember(Var.UNUMERIC, 8);
	Group SU_EXPDTE = SG_T2SUB.addMember(Var.UNUMERIC, 8);
	Group SU_RTGSFLAG = SG_T2SUB.addMember(Var.CHAR, 1);
	Group SU_TYPACCT = SG_T2SUB.addMember(Var.CHAR, 1);
	
	run-bif-convert.sh
	IbregBifConverterUtil
	



}