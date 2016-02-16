package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.CurrencySymbolDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.CurrencySymbol;

public class CurrencySymbolDaoImpl extends JdbcDaoBase implements CurrencySymbolDao {

	private final String ALL_COLUMNS = "id, symbol_name, is_active, table_name";

	private final String TAB_NME = "fxschema.currency_symbol";

	public CurrencySymbolDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public List<CurrencySymbol> getActiveSymbols() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " where is_active is true order by symbol_name";

		return getJdbcTemplate().query(sql, new RowMapper<CurrencySymbol>() {

			@Override
			public CurrencySymbol mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2SymbolList(rs);
			}

		});
	}

	private CurrencySymbol rs2SymbolList(final ResultSet rs) throws SQLException {
		CurrencySymbol result = new CurrencySymbol();

		result.setId(rs.getInt("id"));
		result.setSymbolName(rs.getString("symbol_name"));
		result.setIsActive(rs.getBoolean("is_active"));
		result.setTableName(rs.getString("table_name"));

		return result;
	}

}
