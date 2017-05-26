package pl.com.vsadga.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.process.VolumeProcessor;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.utils.DateConverter;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.command.CurrentUserDataCommand;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.message.response.CurrentUserDataResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;

@Component
public class DataAnalyseBatch extends BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyseBatch.class);

	@Autowired
	private BarDataProcessor barDataProcessor;

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private VolumeProcessor volumeProcessor;
	
	public static void main(String[] args) {
		
		try {
		// Create new connector
        SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.REAL);
        System.out.println("Connected to the server.");
        
        // Create new credentials
        Credentials credentials = new Credentials("user", "pswd");
        
        // Create and execute new login command
        LoginResponse loginResponse = APICommandFactory.executeLoginCommand(
                connector,         // APIConnector
                credentials        // Credentials
        );
        System.out.println("Login response.");
        
        // Check if user logged in correctly
        if(loginResponse.getStatus() == true) {
            
            // Print the message on console
        	System.out.println("User logged in [" + loginResponse.getStreamSessionId() + "].");
        	
        	// połączenie strumieniowe:
        	connectStreaming(connector);
        	
        	// pobranie ceny wg symbolu:
        	connector.subscribePrice("EURUSD");
        	connector.subscribeCandle("EURUSD");
        	
        	// pobranie aktualnej ceny:
        	SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, "EURUSD");
        	System.out.println("Initial symbol price: [" + symbolResponse.getSymbol().getAsk() + "].");
        	
            // Create and execute all symbols command (which gets list of all symbols available for the user)
            //AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
            
            // Print the message on console
            //System.out.println("Available symbols:");
            
            // List all available symbols on console
            //for(SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
            //	System.out.println("   -> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
            //}
        	
        	//ChartResponse resp = APICommandFactory.executeChartLastCommand(connector, "EURUSD", PERIOD_CODE.PERIOD_M5, 2L);
        	//List<RateInfoRecord> rec_list = resp.getRateInfos();
        	
        	//for (RateInfoRecord rec : rec_list) {
        	//	Date datka = new Date(rec.getCtm());
        	//	System.out.println(datka);
        	//}
        		
        } else {
            
            // Print the error on console
        	System.out.println("Error: user couldn't log in!");      
            
        }
        
        // Close connection
        connector.close();
        System.out.println("Connection closed");
		} catch (APICommunicationException e) {
			e.printStackTrace();
		} catch (APICommandConstructionException e) {
			e.printStackTrace();
		} catch (APIReplyParseException e) {
			e.printStackTrace();
		} catch (APIErrorResponse e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
		//SSLSocket socket;
		
//		Socket socket = null;
//		OutputStream os = null;
//		BufferedReader br = null;
//		String line = null;
//		
//		try {
//			SocketAddress proxy_addr = new InetSocketAddress("proxy-usr.kir.pl", 8080);
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, proxy_addr);
//			
//			socket = new Socket(proxy);
//			InetSocketAddress dest = new InetSocketAddress("xapia.x-station.eu", 5113);
//			socket.connect(dest);
//			
//			os = socket.getOutputStream();
//			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			
//			JSONObject cmd_args = new JSONObject();
//			cmd_args.put("userId", "1037101");
//			cmd_args.put("password", "");
//			
//			JSONObject obj = new JSONObject();
//			obj.put("command", "login");
//			obj.put("prettyPrint", false);
//	        obj.put("arguments", cmd_args);
//			
//			os.write(obj.toJSONString().getBytes());
//			
//			while ((line =br.readLine()) != null) 
//				System.out.println(line);
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				socket.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	
	private static void connectStreaming(final SyncAPIConnector connector) {
		try {
			connector.connectStream(new StreamingListener());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (APICommunicationException e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "10 * * * * SUN-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;
		List<BarData> data_list = null;
		int bar_back_count = 0;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// pobierz listę aktywnych symboli:
			symbol_list = symbolService.getActiveSymbols();
			// pobierz listę aktywnych timeframe:
			tmefrm_list = timeFrameService.getAllActive();

			if (symbol_list.isEmpty() || tmefrm_list.isEmpty()) {
				LOGGER.info("   [BATCH] Zadne symbole nie sa aktywne [" + symbol_list.size()
						+ "] ani ramy czasowe [" + tmefrm_list.size() + "].");
				return;
			}

			// pobierz konfigurację o  liczbie barów do przetworzenia:
			bar_back_count = getAnalyseBarCount();

			for (CurrencySymbol symbol : symbol_list) {
				for (TimeFrame tme_frame : tmefrm_list) {
					LOGGER.info("   [PROC] Symbol [" + symbol.getSymbolName() + "] in ["
							+ tme_frame.getTimeFrameDesc() + "].");

					// pobierz listę barów ze statusem 1: 
					data_list = getBarData(bar_back_count, symbol, tme_frame, 1);
					// przetwórz listę barów:
					barDataProcessor.processBarDataByPhase(data_list, tme_frame.getTimeFrameDesc());
					
					// pobierz listę barów ze statusem 2: 
					data_list = getBarData(bar_back_count, symbol, tme_frame, 2);
					// przetwórz listę barów:
					barDataProcessor.processBarDataByPhase(data_list, tme_frame.getTimeFrameDesc());
				}
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}

	private int getAnalyseBarCount() throws BaseServiceException {
		Integer param_int = getIntParamValue("ANALYSE_BAR_COUNT");

		if (param_int == null)
			throw new BaseServiceException("::getAnalyseBarCount:: brak parametru ANALYSE_BAR_COUNT [" + param_int
					+ "] w CONFIG_DATA.");

		return param_int;
	}

	private List<BarData> getBarData(int barBackCount, CurrencySymbol symbol, TimeFrame timeFrame,
			Integer processPhase) throws BaseServiceException {
		List<BarData> bar_list = currencyDataService.getBarDataListByPhase(symbol.getId(),
				timeFrame.getTimeFrameDesc(), processPhase);

		// wypisy kontrolne:
		int list_size = bar_list.size();
		String msg = "   [PROC] Liczba barow do przetworzenia: [" + list_size + "] ze statusem [" + processPhase
				+ "]";

		if (list_size > 0)
			msg += " (" + DateConverter.dateToString(bar_list.get(0).getBarTime(), "yy/MM/dd HH:mm") + ","
					+ DateConverter.dateToString(bar_list.get(list_size - 1).getBarTime(), "yy/MM/dd HH:mm")
					+ ").";

		LOGGER.info(msg);

		return bar_list;
	}

	private boolean isProcessBatch() throws BaseServiceException {
		Integer is_proc = getIntParamValue("IS_BATCH_ANALYSE");

		if (is_proc == null)
			return false;

		if (is_proc.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch analizy barow [" + is_proc + "].");
			return false;
		}
	}

}
