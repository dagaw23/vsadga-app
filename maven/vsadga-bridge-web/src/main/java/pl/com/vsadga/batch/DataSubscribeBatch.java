package pl.com.vsadga.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.processor.DataStreamProcessor;
import pl.com.vsadga.processor.DataStreamProcessorException;
import pl.com.vsadga.service.BaseServiceException;

@Component
public class DataSubscribeBatch extends BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSubscribeBatch.class);
	
	@Autowired
	private DataStreamProcessor dataStreamProcessor;

	@Scheduled(cron = "5 * * * * *")
	public void cronJob() {

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;
			
			// czy strumieniowanie powinno zostac wlaczone:
			if (!isCheckedStreaming())
				return;
			
			
			
		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		} catch (DataStreamProcessorException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek DataStreamProcessorException!");
		}

	}

	private boolean isCheckedStreaming() throws BaseServiceException, DataStreamProcessorException {
		Integer sub_mode = getIntParamValue("DATA_SUBSCRIBE_MODE");
		
		if (sub_mode == null)
			return false;
		
		// status 0: wyłączony
		// status 1: włączony
		// status 11: włącz
		// status 10; wyłącz
		// pozostałe: nic nie rób
		
		if (sub_mode.intValue() == 11) {
			// 11: włącz strumieniowanie
			
			// wymagany jest użytkownik i hasło:
			String usr = getStringParamValue("DATA_SUBSCRIBE_USER");
			String pass = getStringParamValue("DATA_SUBSCRIBE_PASS");
			
			//if (usr == null || pass == null) TODO
			
			dataStreamProcessor.connect(usr, pass);
			
		} else if (sub_mode.intValue() == 10) {
			// 10: wyłącz strumieniowanie
			
			dataStreamProcessor.disconnect();
		} else if (sub_mode.intValue() == 12) {
			// 12: subskrybcja EURUSD
			
			dataStreamProcessor.subscribe("EURUSD");
		}
		
		// pozostałe wartości: nic nie robimy
		return true;
	}

	private boolean isProcessBatch() throws BaseServiceException {
		Integer int_value = getIntParamValue("IS_BATCH_SUBSCRIBE");

		if (int_value == null)
			return false;

		if (int_value.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch wczytywania barow [" + int_value
					+ "] wg klucza 'IS_BATCH_SUBSCRIBE'.");
			return false;
		}
	}

	//

	// DATA_SUBSCRIBE_MODE

}
