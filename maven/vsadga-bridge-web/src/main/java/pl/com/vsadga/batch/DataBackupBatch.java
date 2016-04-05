package pl.com.vsadga.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.data.CurrencyDataService;

@Component
public class DataBackupBatch {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataBackupBatch.class);

	@Autowired
	private ConfigDataService configDataService;

	@Autowired
	private CurrencyDataService currencyDataService;

	@Scheduled(cron = "30 * * * * MON-FRI")
	public void cronJob() {

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// backup:
			currencyDataService.backupArchiveData("m5", getCutoffDate("m5").getTime(), 1); // TODO
																							// parametryzowany
																							// numer
																							// tabeli
			currencyDataService.backupArchiveData("m15", getCutoffDate("m15").getTime(), 1);
			currencyDataService.backupArchiveData("h1", getCutoffDate("h1").getTime(), 1);

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		}
	}

	private GregorianCalendar getCutoffDate(String frameDesc) throws BaseServiceException {
		String param_name = frameDesc.toUpperCase() + "_DAYS_STAY";
		String param_value = configDataService.getParam(param_name);

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru " + param_name + " [" + param_value + "] w tabeli CONFIG_DATA.");
			throw new BaseServiceException("::getCutoffDate:: brak parametru [" + param_name + "].");
		}

		int day_stay = Integer.valueOf(param_value);

		GregorianCalendar act_cal = new GregorianCalendar();
		act_cal.setTime(new Date());

		act_cal.add(Calendar.DAY_OF_YEAR, -day_stay);
		act_cal.add(Calendar.HOUR_OF_DAY, -1);

		return act_cal;
	}

	private boolean isProcessBatch() throws BaseServiceException {
		String param_value = configDataService.getParam("IS_DATA_BACKUP");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru IS_DATA_BACKUP [" + param_value + "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [BATCH] Parametr IS_DATA_BACKUP [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch backupu danych [" + is_proc + "].");
			return false;
		}
	}

}
