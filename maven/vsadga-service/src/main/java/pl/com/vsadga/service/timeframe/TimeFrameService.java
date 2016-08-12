package pl.com.vsadga.service.timeframe;

import java.util.List;

import pl.com.vsadga.data.TimeFrame;

public interface TimeFrameService {

	List<TimeFrame> getAll();

	List<TimeFrame> getAllActive();

	List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame);
}
