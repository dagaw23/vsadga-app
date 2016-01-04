package pl.com.vsadga.service.timeframe;

import java.util.List;

import pl.com.vsadga.dao.TimeFrameDao;
import pl.com.vsadga.data.TimeFrame;

public class TimeFrameServiceImpl implements TimeFrameService {
	
	private TimeFrameDao timeFrameListDao;

	@Override
	public List<TimeFrame> getAllActive() {
		return timeFrameListDao.getAllActive();
	}

	/**
	 * @param timeFrameListDao the timeFrameListDao to set
	 */
	public void setTimeFrameListDao(TimeFrameDao timeFrameListDao) {
		this.timeFrameListDao = timeFrameListDao;
	}
	
}
