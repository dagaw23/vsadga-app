package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.TradeAlert;

public class TradeAlertDaoImpl extends JdbcDaoBase implements TradeAlertDao {

	private final String ALL_COLUMNS = "id, alert_time, alert_message, symbol_id, time_frame_id, bar_time, bar_status";

	private final String SEQ_NME = "fxschema.trade_alert_seq";

	private final String TAB_NME = "fxschema.trade_alert";

	public TradeAlertDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public TradeAlert exist(Integer symbolId, Integer timeFrameId, Date barTime) {
		String sql = "select id from " + TAB_NME + " where symbol_id=? and time_frame_id=? and bar_time=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<TradeAlert>() {

			@Override
			public TradeAlert extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2TradeAlert(rs);
				else
					return null;
			}

		}, symbolId, timeFrameId, new Timestamp(barTime.getTime()));
	}

	@Override
	public List<TradeAlert> getActualTradeAlertList(Date alertTimeFrom) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME
				+ " where alert_time > ? order by alert_time desc";

		return getJdbcTemplate().query(sql, new RowMapper<TradeAlert>() {

			@Override
			public TradeAlert mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TradeAlert(rs);
			}

		}, new Timestamp(alertTimeFrom.getTime()));

	}

	@Override
	public int insert(String alertMessage, Integer symbolId, Integer timeFrameId, Date barTime, String barStatus) {
		String sql = "insert into " + TAB_NME + " (" + ALL_COLUMNS + ") values (nextval('" + SEQ_NME
				+ "'),?,?,?,?,?,?)";

		return getJdbcTemplate().update(sql, new Timestamp(new Date().getTime()), alertMessage, symbolId,
				timeFrameId, new Timestamp(barTime.getTime()), barStatus);
	}

	@Override
	public int update(Integer id, String alertMessage, String barStatus) {
		String sql = "update " + TAB_NME + " set alert_time=?, alert_message=?, bar_status=? where id=?";
		
		return getJdbcTemplate().update(sql, new Timestamp(new Date().getTime()), alertMessage, barStatus, id);
	}

	private TradeAlert rs2TradeAlert(final ResultSet rs) throws SQLException {
		TradeAlert result = new TradeAlert();

		result.setAlertMessage(rs.getString("alert_message"));
		result.setAlertTime(rs.getTimestamp("alert_time"));
		result.setId(rs.getInt("id"));
		result.setSymbolId(rs.getInt("symbol_id"));
		result.setTimeFrameId(rs.getInt("time_frame_id"));
		result.setBarTime(new Date(rs.getTimestamp("bar_time").getTime()));
		result.setBarStatus(rs.getString("bar_status"));

		return result;
	}

}
