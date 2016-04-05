package pl.com.vsadga.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.BarData;

public interface BarDataDao {

	boolean existBarData(Integer symbolId, String frameDesc, Date barDate);

	/**
	 * Pobiera listę wszystkich barów od początku do wskazanej daty (z wyłączeniem tego bara).
	 * 
	 * @param frameDesc
	 *            opis dotyczący ramki czasowej
	 * @param barDate
	 *            data graniczna, do której są pobierane bary
	 * @return
	 */
	List<BarData> getAllToMaxDate(String frameDesc, Date barDate);

	List<BarData> getBarDataList(Integer symbolId, String frameDesc);

	BarData getBySymbolAndTime(Integer symbolId, String frameDesc, Date barTime);

	List<BarData> getLastNbarsData(Integer symbolId, String frameDesc, int size);

	List<BarData> getLastNbarsDataFromTime(Integer symbolId, String frameDesc, int size, Date fromTime);

	/**
	 * Pobiera listę barów dla waloru i ramki czasowej, który jeszcze nie został przetworzony wg
	 * kolumny PROCESS_PHASE (z wartością 1).
	 * 
	 * @param symbolId
	 * @param frameDesc
	 * @return
	 */
	List<BarData> getNotProcessBarDataList(Integer symbolId, String frameDesc);

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

	int updateIndicatorData(BarData barData, Integer processPhase, String frameDesc);

	int updateIndicatorWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator,
			Integer trendWeight, String volumeThermometer, Integer indyNr, Boolean isConfirm);

	int updateIndyData(Integer barDataId, String frameDesc, Integer nr, Integer weight, Boolean isConfirm,
			Integer phase);

	int updateProcessPhase(Integer id, Integer processPhase, String frameDesc);

	int updateProcessPhaseWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator,
			Integer trendWeight, String volumeThermometer);

	/**
	 * Modyfikuje wolumen absorbcyjny w podanym barze.
	 * 
	 * @param frameDesc
	 *            rodzaj ramy czasowej
	 * @param id
	 *            identyfikator pojedynczego bara
	 * @param volumeAbsorb
	 *            nowa wartość wolumenu absorbcyjnego
	 * @return liczba zmodyfikowanych wierszy w tabeli
	 */
	int updateVolumeAbsorbtion(String frameDesc, Integer id, Integer volumeAbsorb);

	int updateVolumeAvg(Integer id, String frameDesc, BigDecimal volumeAvgShort, BigDecimal volumeAvgMedium,
			BigDecimal volumeAvgLong);

	/**
	 * Wpisuje listę przesłanych barów - do tabeli archiwalnej.
	 * 
	 * @param dataList
	 * @param frameDesc
	 * @param tableNr
	 * @return
	 */
	int[] writeAllToArchive(List<BarData> dataList, String frameDesc, Integer tableNr);

}
