package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import pl.com.vsadga.dao.ConfigDataDao;
import pl.com.vsadga.dao.JdbcDaoBase;

public class ConfigDataDaoImpl extends JdbcDaoBase implements ConfigDataDao {

	private final String ALL_COLUMNS = "id, param_name, param_value";

	private final String TAB_NME = "fxschema.config_data";

	public ConfigDataDaoImpl(DataSource dataSource) {
		super(dataSource);
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

}
