package pl.com.vsadga.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import pl.com.vsadga.dao.CurrencySymbolDao;
import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.data.CurrencySymbol;

public class CurrencySymbolDaoImpl extends JdbcDaoBase implements CurrencySymbolDao {

	private final String ALL_COLUMNS = "id, symbol_name, is_active, futures_symbol";

	private final String TAB_NME = "fxschema.currency_symbol";

	public CurrencySymbolDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public int delete(Integer id) {
		String sql = "delete from " + TAB_NME + " where id=?";

		return getJdbcTemplate().update(sql, id);
	}

	@Override
	public List<CurrencySymbol> getActiveSymbols() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME
				+ " where is_active is true order by symbol_name";

		return getJdbcTemplate().query(sql, new RowMapper<CurrencySymbol>() {

			@Override
			public CurrencySymbol mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2SymbolList(rs);
			}

		});
	}

	@Override
	public List<CurrencySymbol> getAll() {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " order by symbol_name";

		return getJdbcTemplate().query(sql, new RowMapper<CurrencySymbol>() {

			@Override
			public CurrencySymbol mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs2SymbolList(rs);
			}

		});
	}

	@Override
	public CurrencySymbol getById(Integer id) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " where id=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<CurrencySymbol>() {

			@Override
			public CurrencySymbol extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2SymbolList(rs);
				else
					return null;
			}

		}, id);
	}

	@Override
	public CurrencySymbol getCurrencySymbolByName(String symbolName) {
		String sql = "select " + ALL_COLUMNS + " from " + TAB_NME + " where symbol_name=?";

		return getJdbcTemplate().query(sql, new ResultSetExtractor<CurrencySymbol>() {

			@Override
			public CurrencySymbol extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs2SymbolList(rs);
				else
					return null;
			}

		}, symbolName);
	}

	@Override
	public Integer getLastId() {
		String sql = "select max(id) from " + TAB_NME;

		return getJdbcTemplate().query(sql, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return rs.getInt(1);
				else
					return null;
			}

		});
	}

	@Override
	public int insert(CurrencySymbol currencySymbol) {
		String sql = "insert into " + TAB_NME + "(" + ALL_COLUMNS + ") values (?,?,?,?)";

		return getJdbcTemplate().update(sql, currencySymbol.getId(), currencySymbol.getSymbolName(),
				currencySymbol.getIsActive(), currencySymbol.getFuturesSymbol());
	}

	@Override
	public int update(String symbolName, boolean isActive, String futuresSymbol, Integer id) {
		String sql = "update " + TAB_NME + " set symbol_name=?, is_active=?, futures_symbol=? where id=?";

		return getJdbcTemplate().update(sql, symbolName, isActive, futuresSymbol, id);
	}

	private CurrencySymbol rs2SymbolList(final ResultSet rs) throws SQLException {
		CurrencySymbol result = new CurrencySymbol();

		result.setId(rs.getInt("id"));
		result.setSymbolName(rs.getString("symbol_name"));
		result.setIsActive(rs.getBoolean("is_active"));
		result.setFuturesSymbol(rs.getString("futures_symbol"));

		return result;
	}

}
