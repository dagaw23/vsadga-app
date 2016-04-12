package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.dao.TradeAlertDao;
import pl.com.vsadga.data.TradeAlert;

public class TradeAlertDaoImpl extends JdbcDaoBase implements TradeAlertDao {

	private final String ALL_COLUMNS = "id, alert_time, alert_message, symbol_id";

	private final String SEQ_NME = "fxschema.trade_alert_seq";

	private final String TAB_NME = "fxschema.trade_alert";

	public TradeAlertDaoImpl(DataSource dataSource) {
		super(dataSource);
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
	public int insert(String alertMessage, Integer symbolId) {
		String sql = "insert into " + TAB_NME + " (" + ALL_COLUMNS + ") values (nextval('" + SEQ_NME + "'),?,?,?)";

		return getJdbcTemplate().update(sql, new Timestamp(new Date().getTime()), alertMessage, symbolId);
	}

	private TradeAlert rs2TradeAlert(final ResultSet rs) throws SQLException {
		TradeAlert result = new TradeAlert();

		result.setAlertMessage(rs.getString("alert_message"));
		result.setAlertTime(rs.getTimestamp("alert_time"));
		result.setId(rs.getInt("id"));
		result.setSymbolId(rs.getInt("symbol_id"));

		return result;
	}

}
