package pl.com.vsadga.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.BarData;

public class BarDataDaoImpl extends JdbcDaoBase implements BarDataDao {

	private final String ALL_COLUMNS = "id, bar_time, bar_low, bar_high, bar_close, bar_volume, ima_count, "
			+ "indicator_nr, indicator_weight, is_confirm, process_phase, symbol_id";

	private final String SCHM_NME = "fxschema.";

	public BarDataDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void batchInsert(String frameDesc, final List<BarData> dataList) {
		String sql = "insert into " + getTableName(frameDesc) + " (" + ALL_COLUMNS + ") values (nextval('"
				+ getSeqName(frameDesc) + "'),?, ?,?,?, ?,?, ?,?,?, ?,?)";

		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return dataList.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setTimestamp(1, new Timestamp(dataList.get(i).getBarTime().getTime()));
				ps.setBigDecimal(2, dataList.get(i).getBarLow());
				ps.setBigDecimal(3, dataList.get(i).getBarHigh());
				ps.setBigDecimal(4, dataList.get(i).getBarClose());
				ps.setInt(5, dataList.get(i).getBarVolume());
				ps.setBigDecimal(6, dataList.get(i).getImaCount());
				ps.setInt(7, dataList.get(i).getIndicatorNr());
				ps.setInt(8, dataList.get(i).getIndicatorWeight());
				ps.setBoolean(9, dataList.get(i).getIsConfirm());
				ps.setInt(10, dataList.get(i).getProcessPhase());
				ps.setInt(11, dataList.get(i).getSymbolId());
			}
		});

	}

	@Override
	public boolean existBarData(Integer symbolId, String frameDesc, Date barDate) {
		String sql = "select id from " + getTableName(frameDesc) + " where symbol_id=? and bar_time=?";

		Integer rec_id = getJdbcTemplate().query(sql, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getInt(1);
				else
					return null;
			}
		}, symbolId, barDate);

		if (rec_id != null)
			return true;
		else
			return false;
	}

	@Override
	public BarData getBySymbolAndTime(Integer symbolId, String frameDesc, Date barTime) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where bar_time=? and symbol_id=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<BarData>() {

			@Override
			public BarData extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2BarData(rs);
				else
					return null;
			}

		}, new Timestamp(barTime.getTime()), symbolId);
	}

	@Override
	public List<BarData> getLastNbarsData(Integer symbolId, String frameDesc, int size) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? order by bar_time desc";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId);

		return data_list.subList(0, size);
	}

	@Override
	public int insert(String frameDesc, BarData data) {
		String sql = "insert into " + getTableName(frameDesc) + " (" + ALL_COLUMNS + ") values (nextval('"
				+ getSeqName(frameDesc) + "'),?, ?,?,?, ?,?, ?,?,?, ?,?)";

		return getJdbcTemplate().update(sql, data.getBarTime(), data.getBarLow(), data.getBarHigh(),
				data.getBarClose(), data.getBarVolume(), data.getImaCount(), data.getIndicatorNr(),
				data.getIndicatorWeight(), data.getIsConfirm(), data.getProcessPhase(), data.getSymbolId());
	}

	@Override
	public int update(String frameDesc, Integer id, BarData barData) {
		String sql = "update " + getTableName(frameDesc)
				+ " set bar_low=?, bar_high=?, bar_close=?, bar_volume=?, ima_count=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, barData.getBarLow(), barData.getBarHigh(), barData.getBarClose(),
				barData.getBarVolume(), barData.getImaCount(), barData.getProcessPhase(), id);
	}

	@Override
	public int updateIndyData(Integer barDataId, String frameDesc, Integer nr, Integer weight, Boolean isConfirm,
			Integer phase) {
		String sql = "update " + getTableName(frameDesc) + " set indicator_nr=?, indicator_weight=?, "
				+ "is_confirm=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, nr, weight, isConfirm, phase, barDataId);
	}

	private String getSeqName(String timeFrameDesc) {
		return getTableName(timeFrameDesc) + "_seq";
	}

	private String getTableName(String timeFrameDesc) {
		return SCHM_NME + "data_" + timeFrameDesc.toLowerCase();
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

		data.setIndicatorNr(rs.getInt("indicator_nr"));
		data.setIndicatorWeight(rs.getInt("indicator_weight"));
		data.setIsConfirm(rs.getBoolean("is_confirm"));

		data.setProcessPhase(rs.getInt("process_phase"));
		data.setSymbolId(rs.getInt("symbol_id"));

		return data;
	}

}
