package pl.com.vsadga.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;

@Component
public class DataBackupBatch extends BaseBatch {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataBackupBatch.class);

	@Autowired
	private CurrencyDataService currencyDataService;

	//@Scheduled(cron = "30 * * * * *")
	@Scheduled(cron = "0 58 23 * * SAT")
	public void cronJob() {

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// backup:
			currencyDataService.backupArchiveData("m5", getCutoffDate("m5").getTime(), getArchTableNumber("m5"));
			currencyDataService.backupArchiveData("m15", getCutoffDate("m15").getTime(), getArchTableNumber("m15"));
			currencyDataService.backupArchiveData("h1", getCutoffDate("h1").getTime(), getArchTableNumber("h1"));

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		}
	}

	private int getArchTableNumber(String frameDesc) throws BaseServiceException {
		// nazwa parametru:
		String param_name = frameDesc.toUpperCase() + "_TABLE_NR";

		// numer archiwalnej tabeli:
		Integer tab_nr = getIntParamValue(param_name);

		if (tab_nr == null)
			throw new BaseServiceException("::getCutoffDate:: brak parametru [" + param_name + ":" + tab_nr + "].");

		return tab_nr.intValue();
	}

	private GregorianCalendar getCutoffDate(String frameDesc) throws BaseServiceException {
		// nazwa parametru:
		String param_name = frameDesc.toUpperCase() + "_DAYS_STAY";

		// liczba dni jaka pozostaje:
		Integer day_stay = getIntParamValue(param_name);

		if (day_stay == null)
			throw new BaseServiceException("::getCutoffDate:: brak parametru [" + param_name + ":" + day_stay
					+ "].");

		GregorianCalendar act_cal = new GregorianCalendar();
		act_cal.setTime(new Date());

		act_cal.add(Calendar.DAY_OF_YEAR, -day_stay);

		return act_cal;
	}

	private boolean isProcessBatch() throws BaseServiceException {
		Integer is_data_backup = getIntParamValue("IS_BATCH_BACKUP");

		if (is_data_backup == null)
			throw new BaseServiceException("::isProcessBatch:: brak parametru IS_BATCH_BACKUP [" + is_data_backup
					+ "].");

		if (is_data_backup.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony backup danych [" + is_data_backup + "].");
			return false;
		}
	}

}
