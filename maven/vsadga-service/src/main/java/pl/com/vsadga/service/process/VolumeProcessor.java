package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.VolumeSize;
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
	 * 
	 * 
	 * @param actualBar
	 * @return 0: żaden istotny wolumen, 1: duży wolumen krótkoterminowy, 2: duży wolumen
	 *         średnioterminowy, 3: duży wolumen długoterminowy, 4: duży wolumen w bardzo długim
	 *         czasie.
	 * @throws BaseServiceException
	 */
	VolumeSize getVolumeSize(BarData actualBar, TimeFrame timeFrame) throws BaseServiceException;

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
	//String getVolumeThermometer(BarData actualBar) throws BaseServiceException;

	void initLevelPositions(String[] h4PositionTab, String[] h1PositionTab, String[] m15PositionTab,
			String[] m5PositionTab) throws BaseServiceException;

}
