package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.TimeFrame;

public interface TimeFrameDao {

	int delete(Integer id);
	
	List<TimeFrame> getAll();
	
	List<TimeFrame> getAllActive();
	
	List<TimeFrame> getAllFile();
	
	List<TimeFrame> getAllLogical();
	
	TimeFrame getById(Integer id);
	
	List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame);
	
	TimeFrame getByTimeFrameDesc(String timeFrameDesc);
	
	int insert(TimeFrame timeFrame);
	
	int update(TimeFrame timeFrame);
	
	Integer getLastId();
}
