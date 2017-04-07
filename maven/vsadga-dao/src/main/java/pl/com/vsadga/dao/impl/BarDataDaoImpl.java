package pl.com.vsadga.dao.impl;

import java.math.BigDecimal;
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
import pl.com.vsadga.data.VolumeSize;
import pl.com.vsadga.dto.BarType;

public class BarDataDaoImpl extends JdbcDaoBase implements BarDataDao {

	private final String ALL_COLUMNS = "id, bar_time, bar_low, bar_high, bar_close, bar_volume, "
			+ "ima_count, bar_type, indicator_nr, is_confirm, trend_indicator, trend_weight, "
			+ "volume_type, volume_absorb, volume_size, process_phase, symbol_id";

	private final String SCHM_NME = "fxschema.";

	public BarDataDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public int[] deleteAll(final String frameDesc, final List<BarData> dataList) {
		String sql = "delete from " + getTableName(frameDesc) + " where id=?";

		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return dataList.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, dataList.get(i).getId());
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
	public List<BarData> getAllToMaxDate(String frameDesc, Date barDate) {
		String sql = "select id, bar_time, bar_low, bar_high, bar_close, bar_volume, "
				+ "ima_count, bar_type, indicator_nr, indicator_weight, is_confirm, "
				+ "trend_indicator, trend_weight, volume_thermometer, volume_absorb, "
				+ "volume_type, volume_size, spread_size, process_phase, symbol_id from "
				+ getTableName(frameDesc) + " where bar_time < ? order by bar_time asc";

		return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, new Timestamp(barDate.getTime()));
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
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(timeFrameDesc)
				+ " where symbol_id=? and bar_time>=? order by bar_time asc";

		return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId, startTime);
	}

	@Override
	public List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc, Date startTime, Date endTime) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(timeFrameDesc)
				+ " where symbol_id=? and bar_time>=? and bar_time<? order by bar_time asc";

		return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId, startTime, endTime);
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
				+ " where symbol_id=? order by bar_time desc LIMIT ?";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId, size);

		return data_list;
	}

	@Override
	public List<BarData> getLastBarDataSortAsc(Integer symbolId, String frameDesc, int barCount) {
		String sql = "select IN1.* from (select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? order by bar_time desc LIMIT ?) as IN1 order by bar_time asc";

		return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId, barCount);
	}

	@Override
	public List<BarData> getLastNbarsDataToDate(Integer symbolId, String frameDesc, int size, Date cutoffDate) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? and bar_time<=? order by bar_time desc";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId, new Timestamp(cutoffDate.getTime()));

		if (data_list.size() <= size)
			return data_list;
		else
			return data_list.subList(0, size);
	}

	@Override
	public BarData getLastProcessBarData(Integer symbolId, String frameDesc) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? and process_phase>1 order by bar_time desc";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId);

		if (data_list.size() < 1)
			return null;
		else
			return data_list.get(0);
	}

	@Override
	public Integer getMaxVolume(Integer symbolId, String frameDesc, Date maxDate, int limit) {
		String sql = "select max(bar_volume) from (select bar_volume from " + getTableName(frameDesc)
				+ " where symbol_id = ? and bar_time < ? order by bar_time desc LIMIT ?) as q1";

		return getJdbcTemplate().queryForInt(sql, symbolId, new Timestamp(maxDate.getTime()), limit);
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
	public int getRowNumber(Integer symbolId, String frameDesc, Date barTime) {
		String sql = "select count(*) from " + getTableName(frameDesc) + " where bar_time < ? and symbol_id = ?";

		return getJdbcTemplate().queryForInt(sql, new Timestamp(barTime.getTime()), symbolId);
	}

	@Override
	public int insert(String frameDesc, BarData data) {
		String sql = "insert into " + getTableName(frameDesc) + " (" + ALL_COLUMNS + ") values (nextval('"
				+ getSeqName(frameDesc) + "'),?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?,?)";

		return getJdbcTemplate().update(sql, data.getBarTime(), data.getBarLow(), data.getBarHigh(),
				data.getBarClose(), data.getBarVolume(), data.getImaCount(), convert(data.getBarType()),
				data.getIndicatorNr(), data.getIsConfirm(), data.getTrendIndicator(), data.getTrendWeight(),
				data.getVolumeType(), data.getVolumeAbsorb(), convert(data.getVolumeSize()),
				data.getProcessPhase(), data.getSymbolId());
	}

	@Override
	public int update(String frameDesc, BarData barData) {
		String sql = "update "
				+ getTableName(frameDesc)
				+ " set bar_low=?, bar_high=?, bar_close=?, bar_volume=?, volume_type=? where symbol_id=? and bar_time=?";

		return getJdbcTemplate().update(sql, barData.getBarLow(), barData.getBarHigh(), barData.getBarClose(),
				barData.getBarVolume(), barData.getVolumeType(), barData.getSymbolId(), barData.getBarTime());
	}

	@Override
	public int update(String frameDesc, BarData barData, Integer processPhase) {
		String sql = "update "
				+ getTableName(frameDesc)
				+ " set bar_low=?, bar_high=?, bar_close=?, bar_volume=?, volume_type=?, process_phase=? where symbol_id=? and bar_time=?";

		return getJdbcTemplate().update(sql, barData.getBarLow(), barData.getBarHigh(), barData.getBarClose(),
				barData.getBarVolume(), barData.getVolumeType(), processPhase, barData.getSymbolId(),
				barData.getBarTime());
	}

	@Override
	public int update(String frameDesc, Integer id, BarData barData) {
		String sql = "update "
				+ getTableName(frameDesc)
				+ " set bar_low=?, bar_high=?, bar_close=?, bar_volume=?, volume_type=?, ima_count=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, barData.getBarLow(), barData.getBarHigh(), barData.getBarClose(),
				barData.getBarVolume(), barData.getVolumeType(), barData.getImaCount(), barData.getProcessPhase(),
				id);
	}

	@Override
	public int updateIndicatorConfirmation(Integer id, Integer processPhase, boolean isConfirm, String frameDesc) {
		String sql = "update " + getTableName(frameDesc) + " set process_phase=?, is_confirm=? where id=?";

		return getJdbcTemplate().update(sql, processPhase, isConfirm, id);
	}

	@Override
	public int updateIndicatorData(BarData barData, Integer processPhase, String frameDesc) {
		String sql = "update " + getTableName(frameDesc)
				+ " set bar_type=?, indicator_nr=?, is_confirm=?, trend_indicator=?, trend_weight=?, "
				+ "volume_absorb=?, volume_size=?, process_phase=? where id=?";

		return getJdbcTemplate().update(sql, convert(barData.getBarType()), barData.getIndicatorNr(),
				barData.getIsConfirm(), barData.getTrendIndicator(), barData.getTrendWeight(),
				barData.getVolumeAbsorb(), convert(barData.getVolumeSize()), processPhase, barData.getId());
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

	@Override
	public int[] writeAllToArchive(final List<BarData> dataList, String frameDesc, Integer tableNr) {
		String sql = "insert into " + getArchTableName(frameDesc, tableNr) + " (" + ALL_COLUMNS
				+ ") values (?,?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?)";

		return getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return dataList.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, dataList.get(i).getId());
				ps.setTimestamp(2, new Timestamp(dataList.get(i).getBarTime().getTime()));
				ps.setBigDecimal(3, dataList.get(i).getBarLow());
				ps.setBigDecimal(4, dataList.get(i).getBarHigh());
				ps.setBigDecimal(5, dataList.get(i).getBarClose());
				ps.setInt(6, dataList.get(i).getBarVolume());

				ps.setBigDecimal(7, dataList.get(i).getImaCount());
				ps.setString(8, convert(dataList.get(i).getBarType()));
				ps.setInt(9, dataList.get(i).getIndicatorNr());
				ps.setBoolean(10, dataList.get(i).getIsConfirm());

				ps.setString(11, dataList.get(i).getTrendIndicator());
				ps.setInt(12, dataList.get(i).getTrendWeight());
				ps.setInt(13, dataList.get(i).getVolumeAbsorb());

				ps.setString(14, convert(dataList.get(i).getVolumeSize()));
				ps.setInt(15, dataList.get(i).getProcessPhase());
				ps.setInt(16, dataList.get(i).getSymbolId());
			}
		});
	}

	private String convert(BarType barType) {
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

	private String convert(VolumeSize volumeSize) {
		if (volumeSize == null)
			return null;
		if (volumeSize == VolumeSize.VH)
			return "VH";
		else if (volumeSize == VolumeSize.Hi)
			return "Hi";
		else if (volumeSize == VolumeSize.AV)
			return "AV";
		else if (volumeSize == VolumeSize.Lo)
			return "Lo";
		else if (volumeSize == VolumeSize.N)
			return "N";
		else
			return "N";

	}

	private BarType convertBarType(String barType) {
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

	private VolumeSize convertVolumeSize(String volumeSize) {
		if (volumeSize == null)
			return null;
		else if (volumeSize.equals("VH"))
			return VolumeSize.VH;
		else if (volumeSize.equals("Hi"))
			return VolumeSize.Hi;
		else if (volumeSize.equals("AV"))
			return VolumeSize.AV;
		else if (volumeSize.equals("Lo"))
			return VolumeSize.Lo;
		else
			return VolumeSize.N;
	}

	private String getArchTableName(String timeFrameDesc, int tableNr) {
		return SCHM_NME + "arch_data_" + timeFrameDesc.toLowerCase() + "_" + tableNr;
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
		data.setVolumeType(rs.getString("volume_type"));

		data.setImaCount(rs.getBigDecimal("ima_count"));
		data.setBarType(convertBarType(rs.getString("bar_type")));
		data.setIndicatorNr(rs.getInt("indicator_nr"));
		data.setIsConfirm(rs.getBoolean("is_confirm"));
		data.setTrendIndicator(rs.getString("trend_indicator"));
		data.setTrendWeight(rs.getInt("trend_weight"));

		data.setVolumeAbsorb(rs.getInt("volume_absorb"));
		data.setVolumeSize(convertVolumeSize(rs.getString("volume_size")));
		data.setProcessPhase(rs.getInt("process_phase"));
		data.setSymbolId(rs.getInt("symbol_id"));

		return data;
	}

	@Override
	public BarData getLastEndedBarData(Integer symbolId, String frameDesc) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? and process_phase > 0 order by bar_time desc limit 1";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId);

		if (data_list.size() < 1)
			return null;
		else
			return data_list.get(0);
	}

	@Override
	public BarData getLastNotEndedBarData(Integer symbolId, String frameDesc) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(frameDesc)
				+ " where symbol_id=? and process_phase = 0 order by bar_time desc limit 1";

		List<BarData> data_list = getJdbcTemplate().query(sql, new RowMapper<BarData>() {

			@Override
			public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2BarData(rs);
			}

		}, symbolId);

		if (data_list.size() < 1)
			return null;
		else
			return data_list.get(0);
	}

	@Override
	public List<BarData> getPartialDataNext(Integer symbolId, String timeFrameDesc, int limit, Date dateFrom) {
		String sql = "select " + ALL_COLUMNS + " from " + getTableName(timeFrameDesc) + " where symbol_id=?";

		if (dateFrom != null)
			sql += " and bar_time < ?";

		sql += " order by bar_time desc limit ?";

		if (dateFrom != null) {
			return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

				@Override
				public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs2BarData(rs);
				}

			}, symbolId, new Timestamp(dateFrom.getTime()), limit);
		} else {
			return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

				@Override
				public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs2BarData(rs);
				}

			}, symbolId, limit);
		}
	}

	@Override
	public List<BarData> getPartialDataPrev(Integer symbolId, String timeFrameDesc, int limit, Date dateFrom) {
		String sql = "select IN1.* from (" + "select " + ALL_COLUMNS + " from " + getTableName(timeFrameDesc)
				+ " where symbol_id=?";

		if (dateFrom != null)
			sql += " and bar_time > ?";

		sql += " order by bar_time asc limit ?) as IN1 order by bar_time desc";

		if (dateFrom != null) {
			return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

				@Override
				public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs2BarData(rs);
				}

			}, symbolId, new Timestamp(dateFrom.getTime()), limit);
		} else {
			return getJdbcTemplate().query(sql, new RowMapper<BarData>() {

				@Override
				public BarData mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs2BarData(rs);
				}

			}, symbolId, limit);
		}
	}

}
