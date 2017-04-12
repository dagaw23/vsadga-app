package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.alert.AlertType;
import pl.com.vsadga.data.alert.TradeAlert;
import pl.com.vsadga.dto.alert.TradeAlertDto;

public class TradeAlertDaoImpl extends JdbcDaoBase implements TradeAlertDao {

	private final String ALL_COLUMNS = "id, alert_time, alert_message, alert_type, symbol_id, time_frame_id, bar_time, bar_status";

	private final String SEQ_NME = "fxschema.trade_alert_seq";

	private final String TAB_NME = "fxschema.trade_alert";

	public TradeAlertDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Integer exist(Integer symbolId, Integer timeFrameId, Date barTime, AlertType alertType) {
		String sql = "select id from " + TAB_NME
				+ " where symbol_id=? and time_frame_id=? and bar_time=? and alert_type=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getInt(1);
				else
					return null;
			}

		}, symbolId, timeFrameId, new Timestamp(barTime.getTime()), convert(alertType));
	}

	@Override
	public List<TradeAlertDto> getActualTradeAlertList(Date alertTimeFrom) {
		String sql = "select ta.id, ta.alert_time, ta.alert_message, ta.alert_type, ta.bar_time, "
				+ "ta.bar_status, cs.symbol_name, tf.time_frame_desc "
				+ "from fxschema.trade_alert ta, fxschema.currency_symbol cs, fxschema.time_frame tf "
				+ "where ta.symbol_id=cs.id and ta.time_frame_id=tf.id and ta.alert_time>? "
				+ "order by ta.alert_time desc";
		final SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");

		return getJdbcTemplate().query(sql, new RowMapper<TradeAlertDto>() {

			@Override
			public TradeAlertDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TradeAlertDto(rs, sdf);
			}

		}, new Timestamp(alertTimeFrom.getTime()));

	}

	@Override
	public List<TradeAlertDto> getByFrame(Date alertTimeFrom, String frameId) {
		String sql = "select ta.id, ta.alert_time, ta.alert_message, ta.alert_type, ta.bar_time, "
				+ "ta.bar_status, cs.symbol_name, tf.time_frame_desc "
				+ "from fxschema.trade_alert ta, fxschema.currency_symbol cs, fxschema.time_frame tf "
				+ "where ta.symbol_id=cs.id and ta.time_frame_id=tf.id and ta.time_frame_id=? and ta.alert_time>? "
				+ "order by ta.alert_time desc";
		final SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");

		return getJdbcTemplate().query(sql, new RowMapper<TradeAlertDto>() {

			@Override
			public TradeAlertDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TradeAlertDto(rs, sdf);
			}

		}, Integer.valueOf(frameId), new Timestamp(alertTimeFrom.getTime()));
	}

	@Override
	public List<TradeAlertDto> getByFrameAndSymbol(Date alertTimeFrom, String symbolId, String frameId) {
		String sql = "select ta.id, ta.alert_time, ta.alert_message, ta.alert_type, ta.bar_time, "
				+ "ta.bar_status, cs.symbol_name, tf.time_frame_desc "
				+ "from fxschema.trade_alert ta, fxschema.currency_symbol cs, fxschema.time_frame tf "
				+ "where ta.symbol_id=cs.id and ta.time_frame_id=tf.id "
				+ "and ta.symbol_id=? and ta.time_frame_id=? and ta.alert_time>? " + "order by ta.alert_time desc";
		final SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
		
		return getJdbcTemplate().query(sql, new RowMapper<TradeAlertDto>() {

			@Override
			public TradeAlertDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TradeAlertDto(rs, sdf);
			}

		}, Integer.valueOf(symbolId), Integer.valueOf(frameId), new Timestamp(alertTimeFrom.getTime()));
	}

	@Override
	public List<TradeAlertDto> getBySymbol(Date alertTimeFrom, String symbolId) {
		String sql = "select ta.id, ta.alert_time, ta.alert_message, ta.alert_type, ta.bar_time, "
				+ "ta.bar_status, cs.symbol_name, tf.time_frame_desc "
				+ "from fxschema.trade_alert ta, fxschema.currency_symbol cs, fxschema.time_frame tf "
				+ "where ta.symbol_id=cs.id and ta.time_frame_id=tf.id and ta.symbol_id=? and ta.alert_time>? "
				+ "order by ta.alert_time desc";
		final SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");

		return getJdbcTemplate().query(sql, new RowMapper<TradeAlertDto>() {

			@Override
			public TradeAlertDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2TradeAlertDto(rs, sdf);
			}

		}, Integer.valueOf(symbolId), new Timestamp(alertTimeFrom.getTime()));
	}

	@Override
	public int insert(String alertMessage, AlertType alertType, Integer symbolId, Integer timeFrameId,
			Date barTime, String barStatus) {
		String sql = "insert into " + TAB_NME + " (" + ALL_COLUMNS + ") values (nextval('" + SEQ_NME
				+ "'),?,?,?,?,?,?,?)";

		return getJdbcTemplate().update(sql, new Timestamp(new Date().getTime()), alertMessage,
				convert(alertType), symbolId, timeFrameId, new Timestamp(barTime.getTime()), barStatus);
	}

	@Override
	public int update(Integer id, String alertMessage, String barStatus) {
		String sql = "update " + TAB_NME + " set alert_time=?, alert_message=?, bar_status=? where id=?";

		return getJdbcTemplate().update(sql, new Timestamp(new Date().getTime()), alertMessage, barStatus, id);
	}

	private String convert(AlertType alertType) {
		if (alertType == AlertType.VOLUME)
			return "V";
		else
			return null;
	}

	private AlertType convert(String alertType) {
		if (alertType.equals("V"))
			return AlertType.VOLUME;
		else
			return null;
	}

	private TradeAlert rs2TradeAlert(final ResultSet rs) throws SQLException {
		TradeAlert result = new TradeAlert();

		result.setAlertMessage(rs.getString("alert_message"));
		result.setAlertTime(rs.getTimestamp("alert_time"));
		result.setAlertType(convert(rs.getString("alert_type")));
		result.setId(rs.getInt("id"));
		result.setSymbolId(rs.getInt("symbol_id"));
		result.setTimeFrameId(rs.getInt("time_frame_id"));
		result.setBarTime(new Date(rs.getTimestamp("bar_time").getTime()));
		result.setBarStatus(rs.getString("bar_status"));

		return result;
	}

	private TradeAlertDto rs2TradeAlertDto(final ResultSet rs, final SimpleDateFormat sdf) throws SQLException {
		TradeAlertDto result = new TradeAlertDto();

		result.setAlertMessage(rs.getString("alert_message"));
		result.setAlertTime(rs.getTimestamp("alert_time"));
		result.setAlertType(convert(rs.getString("alert_type")));

		result.setBarStatus(rs.getString("bar_status"));
		result.setBarTime(sdf.format(new Date(rs.getTimestamp("bar_time").getTime())));
		result.setId(rs.getInt("id"));

		result.setSymbolName(rs.getString("symbol_name"));
		result.setTimeFrameDesc(rs.getString("time_frame_desc"));

		return result;
	}
	
	

}
