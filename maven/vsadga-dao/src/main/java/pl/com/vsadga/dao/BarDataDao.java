package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.BarData;

public interface BarDataDao {

	void batchInsert(String frameDesc, final List<BarData> dataList);
	
	boolean existBarData(Integer symbolId, String frameDesc, Date barDate);
	
	List<BarData> getLastNbarsData(Integer symbolId, String frameDesc, int size);
	
	/**
	 * Pobiera listę barów dla waloru i ramki czasowej, który jeszcze nie został przetworzony wg kolumny PROCESS_PHASE (z wartością 1).
	 * 
	 * @param symbolId
	 * @param frameDesc
	 * @return
	 */
	List<BarData> getNotProcessBarDataList(Integer symbolId, String frameDesc);
	
	List<BarData> getBarDataList(Integer symbolId, String frameDesc);
	
	int insert(String frameDesc, BarData data);
	
	/**
	 * Dla podanego rekordu wg ID - aktualizuje cenę: zamknięcia, maksymalną i minimalną oraz wolumen i status przetworzenia rekordu.
	 * 
	 * @param frameDesc opis dotyczący ramki czasowej
	 * @param id identyfikator rekordu
	 * @param barData aktualizowane dane
	 * @return
	 */
	int update(String frameDesc, Integer id, BarData barData);
	
	int updateIndyData(Integer barDataId, String frameDesc, Integer nr, Integer weight, Boolean isConfirm,
			Integer phase);
	
	int updateProcessPhase(Integer id, Integer processPhase, String frameDesc);
	
	int updateProcessPhaseWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator, Integer trendWeight, String volumeThermometer);
	
	BarData getBySymbolAndTime(Integer symbolId, String frameDesc, Date barTime);
	
}
