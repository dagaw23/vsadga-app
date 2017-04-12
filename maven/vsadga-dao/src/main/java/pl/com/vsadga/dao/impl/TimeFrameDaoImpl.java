package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.dao.TimeFrameDao;
import pl.com.vsadga.data.TimeFrame;

public class TimeFrameDaoImpl extends JdbcDaoBase implements TimeFrameDao {

	private final String ALL_COLUMNS = "id, time_frame, time_frame_desc, is_file_frame, is_logical_frame, is_active";

	private final String TAB_NME = "fxschema.time_frame";

	public TimeFrameDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public int delete(Integer id) {
		String sql = "delete from " + TAB_NME + " where id=?";

		return getJdbcTemplate().update(sql, id);
	}

	@Override
	public List<TimeFrame> getAll() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " order by time_frame asc";

		return getJdbcTemplate().query(sql, new RowMapper<TimeFrame>() {

			@Override
			public TimeFrame mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TimeFrameList(rs);
			}
		});
	}

	@Override
	public List<TimeFrame> getAllActive() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME
				+ " where is_active is true order by time_frame asc";

		return getJdbcTemplate().query(sql, new RowMapper<TimeFrame>() {

			@Override
			public TimeFrame mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TimeFrameList(rs);
			}
		});
	}

	@Override
	public List<TimeFrame> getAllFile() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME
				+ " where is_file_frame is true order by time_frame asc";

		return getJdbcTemplate().query(sql, new RowMapper<TimeFrame>() {

			@Override
			public TimeFrame mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TimeFrameList(rs);
			}
		});
	}

	@Override
	public List<TimeFrame> getAllLogical() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME
				+ " where is_logical_frame is true order by time_frame asc";

		return getJdbcTemplate().query(sql, new RowMapper<TimeFrame>() {

			@Override
			public TimeFrame mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TimeFrameList(rs);
			}
		});
	}

	@Override
	public TimeFrame getById(Integer id) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " where id=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<TimeFrame>() {

			@Override
			public TimeFrame extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2TimeFrameList(rs);
				else
					return null;
			}

		}, id);
	}

	@Override
	public List<TimeFrame> getByTime(Integer fromTimeFrame, Integer toTimeFrame) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME
				+ " where time_frame>=? and time_frame<=? order by time_frame desc";

		return getJdbcTemplate().query(sql, new RowMapper<TimeFrame>() {

			@Override
			public TimeFrame mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TimeFrameList(rs);
			}
		}, fromTimeFrame, toTimeFrame);
	}

	@Override
	public TimeFrame getByTimeFrameDesc(String timeFrameDesc) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " where time_frame_desc=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<TimeFrame>() {

			@Override
			public TimeFrame extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2TimeFrameList(rs);
				else
					return null;
			}

		}, timeFrameDesc);
	}

	@Override
	public int insert(TimeFrame timeFrame) {
		String sql = "insert into " + TAB_NME + "(" + ALL_COLUMNS + ") values (?,?,?, ?,?,?)";

		return getJdbcTemplate().update(sql, timeFrame.getId(), timeFrame.getTimeFrame(), timeFrame.getTimeFrameDesc(),
				timeFrame.getIsFileFrame(), timeFrame.getIsLogicalFrame(), timeFrame.getIsActive());
	}

	@Override
	public int update(TimeFrame timeFrame) {
		String sql = "update " + TAB_NME + " set time_frame=?, time_frame_desc=?, is_file_frame=?, is_logical_frame=?, is_active=? where id=?";

		return getJdbcTemplate().update(sql, timeFrame.getTimeFrame(), timeFrame.getTimeFrameDesc(), timeFrame.getIsFileFrame(), 
				timeFrame.getIsLogicalFrame(), timeFrame.getIsActive(), timeFrame.getId());
	}

	private TimeFrame rs2TimeFrameList(final ResultSet rs) throws SQLException {
		TimeFrame obj = new TimeFrame();

		obj.setId(rs.getInt("id"));
		obj.setIsActive(rs.getBoolean("is_active"));
		obj.setIsFileFrame(rs.getBoolean("is_file_frame"));
		obj.setIsLogicalFrame(rs.getBoolean("is_logical_frame"));
		obj.setTimeFrame(rs.getInt("time_frame"));
		obj.setTimeFrameDesc(rs.getString("time_frame_desc"));

		return obj;
	}

	@Override
	public Integer getLastId() {
		String sql = "select max(id) from " + TAB_NME;

		return getJdbcTemplate().query(sql, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getInt(1);
				else
					return null;
			}

		});
	}

}
