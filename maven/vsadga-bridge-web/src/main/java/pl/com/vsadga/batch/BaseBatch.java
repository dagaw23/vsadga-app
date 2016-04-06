package pl.com.vsadga.batch;

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

}
