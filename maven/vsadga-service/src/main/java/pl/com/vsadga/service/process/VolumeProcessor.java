package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.service.BaseServiceException;

public interface VolumeProcessor {

	/**
	 * Metoda zlicza wolumen absorbcyjny - kontynuuje zliczanie na tego samego typu bara (usuwa
	 * wielkość wolumenu absorbcyjengo z poprzedniego bara) lub rozpoczyna od początku zliczanie
	 * wolumenu absorbcyjnego (dla innego rodzaju typu bara).
	 * 
	 * @param actualBar
	 *            aktualnie przetwarzany bar (ze statusem 1)
	 * @return wolumen absorbcyjny
	 * @throws BaseServiceException
	 */
	int getAbsorptionVolume(BarData actualBar, String frameDesc) throws BaseServiceException;

	/**
	 * Weryfikuje, jaki jest trend wolumenu - kontrolując aktualny bar z poprzednimi dwoma barami.
	 * 
	 * @param actualBar
	 *            aktualnie przetwarzany bar (ze statusem 1)
	 * @return "L": aktualny bar jest LEVEL, <br/>
	 *         "U": trend rosnący, <br/>
	 *         "D": trend malejący, <br/>
	 *         "S": trend boczny, <br/>
	 *         "N": niezdefiniowany.
	 * @throws BaseServiceException
	 */
	String getVolumeThermometer(BarData actualBar) throws BaseServiceException;

}
