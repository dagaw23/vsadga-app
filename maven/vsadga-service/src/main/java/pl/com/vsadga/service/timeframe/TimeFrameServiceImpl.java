package pl.com.vsadga.service.timeframe;

import java.util.List;

import pl.com.vsadga.dao.TimeFrameDao;
import pl.com.vsadga.data.TimeFrame;

public class TimeFrameServiceImpl implements TimeFrameService {

	private TimeFrameDao timeFrameListDao;

	@Override
	public List<TimeFrame> getAll() {
		return timeFrameListDao.getAll();
	}

	@Override
	public List<TimeFrame> getAllActive() {
		return timeFrameListDao.getAllActive();
	}

	@Override
	public List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame) {
		return timeFrameListDao.getByTime(fromTimeFrame, toTimeFrame);
	}

	/**
	 * @param timeFrameListDao
	 *            the timeFrameListDao to set
	 */
	public void setTimeFrameListDao(TimeFrameDao timeFrameListDao) {
		this.timeFrameListDao = timeFrameListDao;
	}

}
