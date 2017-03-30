package pl.com.vsadga.service.process;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.alert.VolumeAlert;
import pl.com.vsadga.service.BaseServiceException;

public interface TradeAlertProcessor {

	void checkVolumeSize(CurrencySymbol symbol, List<VolumeAlert> volumeAlertList) throws BaseServiceException;

}
