package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.service.BaseServiceException;

public interface VolumeProcessor {

	/**
	 * Weryfikuje, jaki jest trend wolumenu - kontrolując aktualny bar z poprzednimi dwoma barami.
	 * 
	 * @param actualBar
	 * @return "L": aktualny bar jest LEVEL, <br/>
	 *         "U": trend rosnący, <br/>
	 *         "D": trend malejący, <br/>
	 *         "S": trend boczny, <br/>
	 *         "N": niezdefiniowany.
	 * @throws BaseServiceException
	 */
	String checkVolumeThermometer(BarData actualBar) throws BaseServiceException;

}
