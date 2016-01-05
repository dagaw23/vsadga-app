package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import pl.com.vsadga.dao.CurrencyWritedDao;
import pl.com.vsadga.dao.JdbcDaoBase;

public class CurrencyWritedDaoImpl extends JdbcDaoBase implements CurrencyWritedDao {

	private final String ALL_COLUMNS = "id, write_time, symbol_list_id, time_frame_id";

	private final String TAB_NME = "fxschema.currency_writed";
	
	private final String SEQ_NME = "fxschema.currency_writed_seq";

	public CurrencyWritedDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId) {
		String sql = "select write_time from " + TAB_NME + " where symbol_list_id=? and time_frame_id=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<Timestamp>() {

			@Override
			public Timestamp extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getTimestamp("write_time");
				else
					return null;
			}

		}, symbolId, timeFrameId);
	}

	@Override
	public int insert(long writeTime, Integer symbolId, Integer timeFrameId) {
		String sql = "insert into " + TAB_NME + "(" + ALL_COLUMNS + ") values (nextval('" + SEQ_NME + "'), ?,?,?)";
		
		return getJdbcTemplate().update(sql, new Timestamp(writeTime), symbolId, timeFrameId);
	}

	@Override
	public int update(long writeTime, Integer symbolId, Integer timeFrameId) {
		String sql = "update " + TAB_NME + " set write_time=? where symbol_list_id=? and time_frame_id=?";
		
		return getJdbcTemplate().update(sql, new Timestamp(writeTime), symbolId, timeFrameId);
	}

}
