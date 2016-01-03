package pl.com.frxdream.dao;

import java.util.List;

import pl.com.frxdream.data.TimeFrame;

public interface TimeFrameDao {

	public List<TimeFrame> getAllActive();
}
