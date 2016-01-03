package pl.com.frxdream.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import pl.com.frxdream.dao.CurrencyWritedDao;
import pl.com.frxdream.dao.JdbcDaoBase;

public class CurrencyWritedDaoImpl extends JdbcDaoBase implements CurrencyWritedDao {
	
	private final String ALL_COLUMNS = "id, write_time, symbol_list_id, time_frame_id";

	private final String TAB_NME = "fxschema.currency_writed";

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

}
