package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.TimeFrame;

public interface TimeFrameDao {

	public List<TimeFrame> getAllActive();
}
