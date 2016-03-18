package pl.com.vsadga.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.BarData;

public interface BarDataDao {

	void batchInsert(String frameDesc, final List<BarData> dataList);

	boolean existBarData(Integer symbolId, String frameDesc, Date barDate);

	List<BarData> getLastNbarsData(Integer symbolId, String frameDesc, int size);

	List<BarData> getLastNbarsDataFromTime(Integer symbolId, String frameDesc, int size, Date fromTime);

	/**
	 * Modyfikuje wolumen absorbcyjny w podanym barze.
	 * 
	 * @param symbolId
	 *            ID symbolu
	 * @param frameDesc
	 *            rodzaj ramy czasowej
	 * @param id
	 *            identyfikator pojedynczego bara
	 * @param volumeAbsorb
	 *            nowa wartość wolumenu absorbcyjnego
	 * @return liczba zmodyfikowanych wierszy w tabeli
	 */
	int updateVolumeAbsorbtion(Integer symbolId, String frameDesc, Integer id, Integer volumeAbsorb);

	/**
	 * Pobiera listę barów dla waloru i ramki czasowej, który jeszcze nie został przetworzony wg
	 * kolumny PROCESS_PHASE (z wartością 1).
	 * 
	 * @param symbolId
	 * @param frameDesc
	 * @return
	 */
	List<BarData> getNotProcessBarDataList(Integer symbolId, String frameDesc);

	List<BarData> getBarDataList(Integer symbolId, String frameDesc);

	int insert(String frameDesc, BarData data);

	/**
	 * Dla podanego rekordu wg ID - aktualizuje cenę: zamknięcia, maksymalną i minimalną oraz
	 * wolumen i status przetworzenia rekordu.
	 * 
	 * @param frameDesc
	 *            opis dotyczący ramki czasowej
	 * @param id
	 *            identyfikator rekordu
	 * @param barData
	 *            aktualizowane dane
	 * @return
	 */
	int update(String frameDesc, Integer id, BarData barData);

	int updateIndyData(Integer barDataId, String frameDesc, Integer nr, Integer weight, Boolean isConfirm,
			Integer phase);

	int updateProcessPhase(Integer id, Integer processPhase, String frameDesc);

	int updateProcessPhaseWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator,
			Integer trendWeight, String volumeThermometer);

	int updateIndicatorWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator,
			Integer trendWeight, String volumeThermometer, Integer indyNr, Boolean isConfirm);

	int updateVolumeAvg(Integer id, String frameDesc, BigDecimal volumeAvgShort, BigDecimal volumeAvgMedium, BigDecimal volumeAvgLong);
	
	int updateProcessPhaseWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator, Integer trendWeight, String volumeThermometer);
	
	int updateIndicatorWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator, Integer trendWeight, String volumeThermometer, Integer indyNr, Boolean isConfirm);
	
	int updateVolumeAvg(Integer id, String frameDesc, BigDecimal volumeAvgShort, BigDecimal volumeAvgMedium, BigDecimal volumeAvgLong);
	
	BarData getBySymbolAndTime(Integer symbolId, String frameDesc, Date barTime);

}
