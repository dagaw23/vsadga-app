package pl.com.vsadga.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public class BarDataDaoImpl extends JdbcDaoBase implements BarDataDao {

	private final String ALL_COLUMNS = "id, bar_time, bar_low, bar_high, bar_close, bar_volume, ima_count, symbol_list_id";

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
				ps.setTimestamp(1, new Timestamp(recordList.get(i).getBarTime()));
				ps.setBigDecimal(2, recordList.get(i).getBarLow());
				ps.setBigDecimal(3, recordList.get(i).getBarHigh());
				ps.setBigDecimal(4, recordList.get(i).getBarClose());
				ps.setInt(5, recordList.get(i).getBarVolume());
				ps.setBigDecimal(6, recordList.get(i).getImaCount());
				ps.setInt(7, recordList.get(i).getSymbolListId());
			}
		});

	}

	private String getTableName(CurrencySymbol symbolList, TimeFrame timeFrameList) {
		String tab_name = "data_" + timeFrameList.getTimeFrameDesc();

		// dla M5 - pobierz numer tabeli:
		if (timeFrameList.getTimeFrame() == 5) {
			return tab_name + "_" + symbolList.getM5TabNr();
		} else
			return tab_name;
	}

}
