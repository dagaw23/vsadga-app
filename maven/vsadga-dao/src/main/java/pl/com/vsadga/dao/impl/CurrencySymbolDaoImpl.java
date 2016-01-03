package pl.com.frxdream.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import pl.com.frxdream.dao.JdbcDaoBase;
import pl.com.frxdream.dao.CurrencySymbolDao;
import pl.com.frxdream.data.CurrencySymbol;

public class CurrencySymbolDaoImpl extends JdbcDaoBase implements CurrencySymbolDao {

	private final String ALL_COLUMNS = "id, symbol_name, is_active, m5_tab_nr";

	private final String TAB_NME = "fxschema.symbol_list";

	public CurrencySymbolDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public List<CurrencySymbol> getActiveSymbols() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME;

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
		result.setM5TabNr(rs.getInt("m5_tab_nr"));

		return result;
	}

}
