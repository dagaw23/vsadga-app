package pl.com.vsadga.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public class BarDataDaoImpl extends JdbcDaoBase implements BarDataDao {

	private final String ALL_COLUMNS = "id, bar_time, bar_low, bar_high, bar_close, bar_volume, ima_count, symbol_id";

	private final String SCHM_NME = "fxschema.";

	public BarDataDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void batchInsert(final List<BarData> recordList, CurrencySymbol symbolList, TimeFrame timeFrameList) {
		String tab_name = SCHM_NME + getTableName(symbolList, timeFrameList);
		String seq_name = SCHM_NME + getTableName(symbolList, timeFrameList) + "_seq";

		String sql = "insert into " + tab_name + "(" + ALL_COLUMNS + ") values (nextval('" + seq_name
				+ "'),?, ?,?,?, ?,?,?)";

		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return recordList.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setTimestamp(1, new Timestamp(recordList.get(i).getBarTime().getTime()));
				ps.setBigDecimal(2, recordList.get(i).getBarLow());
				ps.setBigDecimal(3, recordList.get(i).getBarHigh());
				ps.setBigDecimal(4, recordList.get(i).getBarClose());
				ps.setInt(5, recordList.get(i).getBarVolume());
				ps.setBigDecimal(6, recordList.get(i).getImaCount());
				ps.setInt(7, recordList.get(i).getSymbolId());
			}
		});

	}

	@Override
	public List<BarData> getLastNbarsData(final int count, final CurrencySymbol symbol, final TimeFrame timeFrame) {
		String tab_name = SCHM_NME + getTableName(symbol, timeFrame);
		
		String sql = "select " + ALL_COLUMNS + " from " + tab_name + " where symbol_id=? order by bar_time desc";
		
		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}
			
		}, symbol.getId());
		
		return data_list.subList(0, count);
	}

	private String getTableName(CurrencySymbol symbol, TimeFrame timeFrame) {
		String tab_name = "data_"  +  timeFrame.getTimeFrameDesc();

		// dla M5 - dodaj jeszcze symbol:
		if (timeFrame.getTimeFrame() == 5) {
			return tab_name + "_" + symbol.getTableName();
		} else
			return tab_name;
	}
	
	private BarData rs2BarData(final ResultSet rs) throws SQLException {
		BarData data = new BarData();
		
		data.setId(rs.getInt("id"));
		data.setBarTime(new Date(rs.getTimestamp("bar_time").getTime()));
		data.setBarLow(rs.getBigDecimal("bar_low"));
		data.setBarHigh(rs.getBigDecimal("bar_high"));
		data.setBarClose(rs.getBigDecimal("bar_close"));
		data.setBarVolume(rs.getInt("bar_volume"));
		data.setImaCount(rs.getBigDecimal("ima_count"));
		data.setSymbolId(rs.getInt("symbol_id"));
		
		return data;
	}

}
