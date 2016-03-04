package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.service.BaseServiceException;

public interface VolumeProcessor {

	/**
	 * Dodaje do danych wewnętrznych - informację o aktualnym barze (wolumen).
	 * 
	 * @param actualBar
	 * @return 0: dane gotowe, można wyliczać trend, <br/>
	 *         1: dane jeszcze nie są gotowe (Niezdefiniowane), <br/>
	 *         2: dane są gotowe, ale Level bar
	 * @throws BaseServiceException
	 */
	int addVolumeThermometerData(BarData actualBar) throws BaseServiceException;

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

	void clearProcessData();

}
