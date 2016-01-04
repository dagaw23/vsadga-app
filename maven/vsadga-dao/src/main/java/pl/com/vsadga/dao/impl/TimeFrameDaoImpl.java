package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.dao.TimeFrameDao;
import pl.com.vsadga.data.TimeFrame;

public class TimeFrameDaoImpl extends JdbcDaoBase implements TimeFrameDao {

	private final String ALL_COLUMNS = "id, time_frame, time_frame_desc, is_active";

	private final String TAB_NME = "fxschema.time_frame_list";

	public TimeFrameDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public List<TimeFrame> getAllActive() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME;

		return getJdbcTemplate().query(sql, new RowMapper<TimeFrame>() {

			@Override
			public TimeFrame mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TimeFrameList(rs);
			}
		});
	}

	private TimeFrame rs2TimeFrameList(final ResultSet rs) throws SQLException {
		TimeFrame obj = new TimeFrame();

		obj.setId(rs.getInt("id"));
		obj.setIsActive(rs.getBoolean("is_active"));
		obj.setTimeFrame(rs.getInt("time_frame"));
		obj.setTimeFrameDesc(rs.getString("time_frame_desc"));

		return obj;
	}

}
