package pl.com.vsadga.service.data;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface CurrencyDataService {

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { BaseServiceException.class })
	void backupArchiveData(String frameDesc, Date barDate, Integer tableNr) throws BaseServiceException;
	
	int deleteAll(final String frameDesc, final List<BarData> dataList) throws BaseServiceException;

	List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException;
	
	/**
	 * Pobiera dane z podanego zakresu dat (większe równe startTime oraz mniejsze od endTime).
	 * 
	 * @param symbolId
	 * @param timeFrameDesc
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws BaseServiceException
	 */
	List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime, Date endTime) throws BaseServiceException;
	
	/**
	 * Pobiera dane, gdzie data jest większa lub równa od podanej daty.
	 * 
	 * @param symbolId
	 * @param timeFrameDesc
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws BaseServiceException
	 */
	List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime) throws BaseServiceException;

	List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;

	List<BarData> getLastNbarDataToDate(int size, CurrencySymbol symbol, TimeFrame timeFrame, Date fromTime)
			throws BaseServiceException;

	List<BarData> getNotProcessBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException;
	
	int insert(String frameDesc, BarData data) throws BaseServiceException;
	
	/**
	 * Aktualizuje zamknięcie, high, low i wolumen - dla bara o ID z tego bara. 
	 * 
	 * @param frameDesc
	 * @param data
	 * @return
	 * @throws BaseServiceException
	 */
	int update(String frameDesc, BarData data) throws BaseServiceException;
	
	/**
	 * Aktualizuje zamknięcie, high, low i wolumen - dla bara o ID z tego bara. 
	 * 
	 * @param frameDesc
	 * @param data
	 * @return
	 * @throws BaseServiceException
	 */
	int update(String frameDesc, BarData data, Integer processPhase) throws BaseServiceException;
}
