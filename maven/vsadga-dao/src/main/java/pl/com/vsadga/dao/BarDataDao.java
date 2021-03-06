package pl.com.vsadga.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.BarData;

public interface BarDataDao {

	/**
	 * Pobiera listę barów dla podanego symbolu i ramki czasowej - wg podanej fazy przetworzenia
	 * bara. Pobierane bary są sortowane rosnąco wg daty barów.
	 * 
	 * @param symbolId
	 *            ID symbolu bara
	 * @param timeFrameDesc
	 *            opis ramki czasowej bara
	 * @param processPhase
	 *            faza przetworzenia bara
	 * @return lista barów
	 */
	List<BarData> getBarDataListByPhase(Integer symbolId, String timeFrameDesc, Integer processPhase);
	
	/**
	 * Pobiera listę barów dla podanego symbolu i ramki czasowej - które są mniejsze od podanej daty
	 * oraz pobiera w podanej ilości na liście. Elementy na liście są posortowane malejąco wg daty
	 * bara.
	 * 
	 * @param symbolId
	 * @param timeFrameDesc
	 * @param limit
	 *            ilość pobieranych barów
	 * @param dateFrom
	 *            pobierane bary muszą mieć datę < od podanej daty
	 * @return
	 */
	List<BarData> getPartialDataNext(Integer symbolId, String timeFrameDesc, int limit, Date dateFrom);
	
	List<BarData> getPartialDataPrev(Integer symbolId, String timeFrameDesc, int limit, Date dateFrom);
	
	/**
	 * Usuwa wszystkie rekordy wg ID, które zostałe przesłane na liście
	 * 
	 * @param dataList
	 * @return
	 */
	int[] deleteAll(String frameDesc, List<BarData> dataList);

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
	
	List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime, Date endTime);
	
	List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime);
	
	BarData getBySymbolAndTime(Integer symbolId, String frameDesc, Date barTime);

	int getRowNumber(Integer symbolId, String frameDesc, Date barTime);

	List<BarData> getLastNbarsData(Integer symbolId, String frameDesc, int size);
	
	/**
	 * Pobiera najwcześniej zapisane bary do tabeli w podanej ilości oraz posortowane rosnąco wg daty bara.
	 * 
	 * @param symbolId
	 * @param frameDesc
	 * @param barCount ilość pobieranych barów
	 * 
	 * @return
	 */
	List<BarData> getLastBarDataSortAsc(Integer symbolId, String frameDesc, int barCount);
	
	List<BarData> getLastNbarsDataToDate(Integer symbolId, String frameDesc, int size, Date cutoffDate);

	/**
	 * Pobiera ostatni przetworzony (processPhase > 1) bar.
	 * 
	 * @param symbolId
	 * @param frameDesc opis dotyczący ramki czasowej
	 * @return
	 */
	BarData getLastProcessBarData(Integer symbolId, String frameDesc);
	
	/**
	 * Pobiera ostatni zakończony już bar (processPhase > 0).
	 * 
	 * @param symbolId
	 * @param frameDesc opis dotyczący ramki czasowej
	 * @return
	 */
	BarData getLastEndedBarData(Integer symbolId, String frameDesc);
	
	/**
	 * Pobiera ostatni nie zakończony jeszcze bar (processPhase = 0).
	 * 
	 * @param symbolId
	 * @param frameDesc opis dotyczący ramki czasowej
	 * @return
	 */
	BarData getLastNotEndedBarData(Integer symbolId, String frameDesc);
	
	Integer getMaxVolume(Integer symbolId, String frameDesc, Date maxDate, int limit);

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
	
	/**
	 * Dla podanego rekordu wg ID - aktualizuje cenę: zamknięcia, maksymalną i minimalną oraz
	 * wolumen rekordu.
	 * 
	 * @param frameDesc
	 * @param barData
	 * @return
	 */
	int update(String frameDesc, BarData barData);
	
	/**
	 * Dla podanego rekordu wg ID - aktualizuje cenę: zamknięcia, maksymalną i minimalną,
	 * wolumen rekordu oraz status.
	 * 
	 * @param frameDesc
	 * @param barData
	 * @return
	 */
	int update(String frameDesc, BarData barData, Integer processPhase);

	int updateIndicatorConfirmation(Integer id, Integer processPhase, boolean isConfirm, String frameDesc);
	
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
