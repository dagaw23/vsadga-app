package pl.com.vsadga.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;

public abstract class BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseBatch.class);

	@Autowired
	private ConfigDataService configDataService;

	protected GregorianCalendar getActualTimeWithShift() throws BaseServiceException {
		Integer hour_shift = getIntParamValue("HOUR_SHIFT");

		if (hour_shift == null)
			throw new BaseServiceException("::getActualTimeWithShift:: brak parametru HOUR_SHIFT [" + hour_shift
					+ "] w tabeli parametrow.");

		GregorianCalendar greg_cal = new GregorianCalendar();
		greg_cal.setTime(new Date());
		greg_cal.add(Calendar.HOUR_OF_DAY, hour_shift.intValue());

		return greg_cal;
	}

	protected Integer getIntParamValue(String paramName) throws BaseServiceException {
		String param_value = configDataService.getParam(paramName);

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru [" + paramName + ":" + param_value + "] w tabeli CONFIG_DATA.");
			return null;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [BATCH] Parametr [" + paramName + ":" + param_value + "] nie jest numeryczny.");
			return null;
		}

		return Integer.valueOf(param_value);
	}

	protected String getStringParamValue(String paramName) throws BaseServiceException {
		String param_value = configDataService.getParam(paramName);

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru [" + paramName + ":" + param_value + "] w tabeli CONFIG_DATA.");
			return null;
		}

		return param_value;
	}

	protected boolean isProcessBatch(String paramName) throws BaseServiceException {
		Integer is_process = getIntParamValue(paramName);

		if (is_process == null)
			throw new BaseServiceException("::isProcessBatch:: brak parametru " + paramName + " [" + is_process
					+ "] w tabeli parametrow.");

		if (is_process.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch wg parametru " + paramName + " [" + is_process.intValue()
					+ "].");
			return false;
		}
	}

}
