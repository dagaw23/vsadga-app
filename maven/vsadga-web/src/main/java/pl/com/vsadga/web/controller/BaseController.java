package pl.com.vsadga.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;

public abstract class BaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	private ConfigDataService configDataService;
	
	protected Integer getIntParamValue(String paramName, int defaultValue) throws BaseServiceException {
		String param_value = configDataService.getParam(paramName);

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [CONFIG] Brak parametru " + paramName + " [" + param_value + "] w tabeli CONFIG_DATA.");
			return defaultValue;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [CONFIG] Parametr " + paramName + " [" + param_value + "] nie jest numeryczny.");
			return defaultValue;
		}

		return Integer.valueOf(param_value);
	}
	
	protected Date getDateParamValue(String paramName, String pattern) throws BaseServiceException, ParseException {
		String param_value = configDataService.getParam(paramName);

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [CONFIG] Brak parametru " + paramName + " [" + param_value + "] w tabeli CONFIG_DATA.");
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setLenient(false);
		
		return sdf.parse(param_value);
	}
}
