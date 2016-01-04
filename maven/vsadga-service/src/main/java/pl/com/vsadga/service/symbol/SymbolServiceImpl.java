package pl.com.vsadga.service.symbol;

import java.util.List;

import pl.com.vsadga.dao.CurrencySymbolDao;
import pl.com.vsadga.data.CurrencySymbol;

public class SymbolServiceImpl implements SymbolService {

	private CurrencySymbolDao symbolListDao;

	@Override
	public List<CurrencySymbol> getActiveSymbols() {

		return symbolListDao.getActiveSymbols();
	}

	/**
	 * @param symbolListDao
	 *            the symbolListDao to set
	 */
	public void setSymbolListDao(CurrencySymbolDao symbolListDao) {
		this.symbolListDao = symbolListDao;
	}

}
