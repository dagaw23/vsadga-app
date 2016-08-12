package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.ConfigDataDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.ConfigData;

public class ConfigDataDaoImpl extends JdbcDaoBase implements ConfigDataDao {

	private final String ALL_COLUMNS = "id, param_name, param_value";

	private final String TAB_NME = "fxschema.config_data";

	public ConfigDataDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public ConfigData get(Integer id) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " where id=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<ConfigData>() {

			@Override
			public ConfigData extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2ConfigData(rs);
				else
					return null;
			}

		}, id);
	}

	@Override
	public List<ConfigData> getAll() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " order by param_name asc";

		return getJdbcTemplate().query(sql, new RowMapper<ConfigData>() {

			@Override
			public ConfigData mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2ConfigData(rs);
			}

		});
	}

	@Override
	public String getParam(String paramName) {
		String sql = "select param_value from " + TAB_NME + " where param_name=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<String>() {

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getString("param_value");
				else
					return null;
			}

		}, paramName);
	}

	@Override
	public int update(Integer id, String paramValue) {
		String sql = "update " + TAB_NME + " set param_value=? where id=?";

		return getJdbcTemplate().update(sql, paramValue, id);
	}

	private ConfigData rs2ConfigData(final ResultSet rs) throws SQLException {
		ConfigData result = new ConfigData();

		result.setId(rs.getInt("ID"));
		result.setParamName(rs.getString("PARAM_NAME"));
		result.setParamValue(rs.getString("PARAM_VALUE"));

		return result;
	}

}
