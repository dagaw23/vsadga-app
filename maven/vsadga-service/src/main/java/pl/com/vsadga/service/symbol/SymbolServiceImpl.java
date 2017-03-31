package pl.com.vsadga.service.symbol;

import java.util.List;

import pl.com.vsadga.dao.CurrencySymbolDao;
import pl.com.vsadga.data.CurrencySymbol;

public class SymbolServiceImpl implements SymbolService {

	private CurrencySymbolDao symbolListDao;

	@Override
	public int delete(Integer id) {
		return symbolListDao.delete(id);
	}

	@Override
	public List<CurrencySymbol> getActiveSymbols() {
		return symbolListDao.getActiveSymbols();
	}

	@Override
	public List<CurrencySymbol> getAll() {
		return symbolListDao.getAll();
	}

	@Override
	public CurrencySymbol getById(Integer id) {
		return symbolListDao.getById(id);
	}

	@Override
	public CurrencySymbol getCurrencySymbolByName(String symbolName) {
		return symbolListDao.getCurrencySymbolByName(symbolName);
	}

	@Override
	public int insert(CurrencySymbol currencySymbol) {
		// pobierz ostatni ID:
		Integer rec_id = symbolListDao.getLastId();
		int id = 0;

		if (rec_id == null)
			id = 1;
		else
			id = rec_id.intValue() + 1;

		// aktualizacja zawartości ID:
		currencySymbol.setId(id);

		return symbolListDao.insert(currencySymbol);
	}

	/**
	 * @param symbolListDao
	 *            the symbolListDao to set
	 */
	public void setSymbolListDao(CurrencySymbolDao symbolListDao) {
		this.symbolListDao = symbolListDao;
	}

	@Override
	public int update(CurrencySymbol currencySymbol) {
		return symbolListDao.update(currencySymbol.getSymbolName(), currencySymbol.getIsActive(),
				currencySymbol.getFuturesSymbol(), currencySymbol.getId());
	}

}
