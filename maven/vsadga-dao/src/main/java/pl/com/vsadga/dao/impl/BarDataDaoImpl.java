package pl.com.vsadga.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarType;

public class BarDataDaoImpl extends JdbcDaoBase implements BarDataDao {

	private final String ALL_COLUMNS = "id, bar_time, bar_low, bar_high, bar_close, bar_volume, "
			+ "ima_count, bar_type, indicator_nr, indicator_weight, is_confirm, "
			+ "trend_indicator, trend_weight, volume_thermometer, volume_absorb, "
			+ "volume_size, spread_size, process_phase, symbol_id";

	private final String SCHM_NME = "fxschema.";

	public BarDataDaoImpl(DataSource dataSource) {
		super(dataSource);
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
	public List<BarData> getBarDataList(Integer symbolId, String frameDesc) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? order by bar_time asc";

		return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId);
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

		if (data_list.size() <= size)
			return data_list;
		else
			return data_list.subList(0, size);
	}

	@Override
	public List<BarData> getLastNbarsDataFromTime(Integer symbolId, String frameDesc, int size, Date fromTime) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? and bar_time<=? order by bar_time desc";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId, new Timestamp(fromTime.getTime()));

		if (data_list.size() <= size)
			return data_list;
		else
			return data_list.subList(0, size);
	}

	@Override
	public List<BarData> getNotProcessBarDataList(Integer symbolId, String frameDesc) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? and process_phase=1 order by bar_time asc";

		return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId);
	}

	@Override
	public int insert(String frameDesc, BarData data) {
		String sql = "insert into " + getTableName(frameDesc) + " (" + ALL_COLUMNS + ") values (nextval('"
				+ getSeqName(frameDesc) + "'),?,?,?,?,?, ?,?,?,?,?, ?,?,?,?, ?,?,?,?)";

		return getJdbcTemplate().update(sql, data.getBarTime(), data.getBarLow(), data.getBarHigh(),
				data.getBarClose(), data.getBarVolume(), data.getImaCount(), data.getBarType(),
				data.getIndicatorNr(), data.getIndicatorWeight(), data.getIsConfirm(), data.getTrendIndicator(),
				data.getTrendWeight(), data.getVolumeThermometer(), data.getVolumeAbsorb(), data.getVolumeSize(),
				data.getSpreadSize(), data.getProcessPhase(), data.getSymbolId());
	}

	@Override
	public int update(String frameDesc, Integer id, BarData barData) {
		String sql = "update " + getTableName(frameDesc)
				+ " set bar_low=?, bar_high=?, bar_close=?, bar_volume=?, ima_count=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, barData.getBarLow(), barData.getBarHigh(), barData.getBarClose(),
				barData.getBarVolume(), barData.getImaCount(), barData.getProcessPhase(), id);
	}

	@Override
	public int updateIndicatorData(BarData barData, Integer processPhase, String frameDesc) {
		String sql = "update " + getTableName(frameDesc)
				+ " set bar_type=?, indicator_nr=?, indicator_weight=?, is_confirm=?, "
				+ "trend_indicator=?, trend_weight=?, volume_thermometer=?, volume_absorb=?, "
				+ "volume_size=?, spread_size=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, getBarType(barData.getBarType()), barData.getIndicatorNr(),
				barData.getIndicatorWeight(), barData.getIsConfirm(), barData.getTrendIndicator(),
				barData.getTrendWeight(), barData.getVolumeThermometer(), barData.getVolumeAbsorb(),
				barData.getVolumeSize(), barData.getSpreadSize(), processPhase, barData.getId());
	}

	@Override
	public int updateIndicatorWithTrend(Integer id, String frameDesc, Integer processPhase, String trendIndicator,
			Integer trendWeight, String volumeThermometer, Integer indyNr, Boolean isConfirm) {
		String sql = "update " + getTableName(frameDesc)
				+ " set process_phase=?, trend_indicator=?, trend_weight=?, volume_thermometer=?, "
				+ "indicator_nr=?, is_confirm=? where id=?";

		return getJdbcTemplate().update(sql, processPhase, trendIndicator, trendWeight, volumeThermometer, indyNr,
				isConfirm, id);
	}

	@Override
	public int updateIndyData(Integer barDataId, String frameDesc, Integer nr, Integer weight, Boolean isConfirm,
			Integer phase) {
		String sql = "update " + getTableName(frameDesc) + " set indicator_nr=?, indicator_weight=?, "
				+ "is_confirm=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, nr, weight, isConfirm, phase, barDataId);
	}

	@Override
	public int updateProcessPhase(Integer id, Integer processPhase, String frameDesc) {
		String sql = "update " + getTableName(frameDesc) + " set process_phase=? where id=?";

		return getJdbcTemplate().update(sql, processPhase, id);
	}

	@Override
	public int updateProcessPhaseWithTrend(Integer id, String frameDesc, Integer processPhase,
			String trendIndicator, Integer trendWeight, String volumeThermometer) {
		String sql = "update " + getTableName(frameDesc)
				+ " set process_phase=?, trend_indicator=?, trend_weight=?, volume_thermometer=? where id=?";

		return getJdbcTemplate().update(sql, processPhase, trendIndicator, trendWeight, volumeThermometer, id);
	}

	@Override
	public int updateVolumeAbsorbtion(String frameDesc, Integer id, Integer volumeAbsorb) {
		String sql = "update " + getTableName(frameDesc) + " set volume_absorb=? where id=?";

		return getJdbcTemplate().update(sql, volumeAbsorb, id);

	}

	@Override
	public int updateVolumeAvg(Integer id, String frameDesc, BigDecimal volumeAvgShort,
			BigDecimal volumeAvgMedium, BigDecimal volumeAvgLong) {

		String sql = "update " + getTableName(frameDesc)
				+ " set volume_avg_short=?, volume_avg_medium=?, volume_avg_long=? where id=?";

		return getJdbcTemplate().update(sql, volumeAvgShort, volumeAvgMedium, volumeAvgLong, id);
	}

	private String getBarType(BarType barType) {
		if (barType == null)
			return null;
		else if (barType == BarType.UP_BAR)
			return "U";
		else if (barType == BarType.DOWN_BAR)
			return "D";
		else if (barType == BarType.LEVEL_BAR)
			return "L";
		else
			return null;
	}

	private BarType getBarType(String barType) {
		if (barType == null)
			return null;
		else if (barType.equals("U"))
			return BarType.UP_BAR;
		else if (barType.equals("D"))
			return BarType.DOWN_BAR;
		else if (barType.equals("L"))
			return BarType.LEVEL_BAR;
		else
			return null;
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
		data.setBarType(getBarType(rs.getString("bar_type")));
		data.setIndicatorNr(rs.getInt("indicator_nr"));
		data.setIndicatorWeight(rs.getInt("indicator_weight"));
		data.setIsConfirm(rs.getBoolean("is_confirm"));

		data.setTrendIndicator(rs.getString("trend_indicator"));
		data.setTrendWeight(rs.getInt("trend_weight"));
		data.setVolumeThermometer(rs.getString("volume_thermometer"));
		data.setVolumeAbsorb(rs.getInt("volume_absorb"));

		data.setVolumeSize(rs.getString("volume_size"));
		data.setSpreadSize(rs.getString("spread_size"));
		data.setProcessPhase(rs.getInt("process_phase"));
		data.setSymbolId(rs.getInt("symbol_id"));

		return data;
	}

}
