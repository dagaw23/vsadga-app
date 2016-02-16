package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public interface BarDataDao {

	void batchInsert(String frameDesc, final List<BarData> dataList);
	
	boolean existBarData(Integer symbolId, String frameDesc, Date barDate);
	
	List<BarData> getLastNbarsData(Integer symbolId, String frameDesc, int size);
	
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
	
	BarData getBySymbolAndTime(Integer symbolId, String frameDesc, Date barTime);
	
}
