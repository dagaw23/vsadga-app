package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.TimeFrame;

public interface TimeFrameDao {

	List<TimeFrame> getAll();
	
	List<TimeFrame> getAllActive();
	
	List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame);
	
	TimeFrame getByTimeFrameDesc(String timeFrameDesc);
}
