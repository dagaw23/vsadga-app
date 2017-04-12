package pl.com.vsadga.service.timeframe;

import java.util.List;

import pl.com.vsadga.dao.TimeFrameDao;
import pl.com.vsadga.data.TimeFrame;

public class TimeFrameServiceImpl implements TimeFrameService {

	private TimeFrameDao timeFrameListDao;

	@Override
	public int delete(Integer id) {
		return timeFrameListDao.delete(id);
	}

	@Override
	public List<TimeFrame> getAll() {
		return timeFrameListDao.getAll();
	}

	@Override
	public List<TimeFrame> getAllActive() {
		return timeFrameListDao.getAllActive();
	}

	@Override
	public List<TimeFrame> getAllFile() {
		return timeFrameListDao.getAllFile();
	}

	@Override
	public List<TimeFrame> getAllLogical() {
		return timeFrameListDao.getAllLogical();
	}

	@Override
	public TimeFrame getById(Integer id) {
		return timeFrameListDao.getById(id);
	}

	@Override
	public List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame) {
		return timeFrameListDao.getByTime(fromTimeFrame, toTimeFrame);
	}

	@Override
	public int insert(TimeFrame timeFrame) {
		// pobierz ostatni ID:
		Integer rec_id = timeFrameListDao.getLastId();
		int id = 0;

		if (rec_id == null)
			id = 1;
		else
			id = rec_id.intValue() + 1;

		// aktualizacja zawartości ID:
		timeFrame.setId(id);

		return timeFrameListDao.insert(timeFrame);
	}

	/**
	 * @param timeFrameListDao
	 *            the timeFrameListDao to set
	 */
	public void setTimeFrameListDao(TimeFrameDao timeFrameListDao) {
		this.timeFrameListDao = timeFrameListDao;
	}

	@Override
	public int update(TimeFrame timeFrame) {
		return timeFrameListDao.update(timeFrame);
	}

}
