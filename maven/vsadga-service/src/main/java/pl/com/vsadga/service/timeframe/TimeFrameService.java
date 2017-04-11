package pl.com.vsadga.service.timeframe;

import java.util.List;

import pl.com.vsadga.data.TimeFrame;

public interface TimeFrameService {

	int delete(Integer id);

	List<TimeFrame> getAll();

	List<TimeFrame> getAllActive();

	List<TimeFrame> getAllFile();

	List<TimeFrame> getAllLogical();

	TimeFrame getById(Integer id);

	List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame);

	int insert(TimeFrame timeFrame);

	int update(TimeFrame timeFrame);
}
