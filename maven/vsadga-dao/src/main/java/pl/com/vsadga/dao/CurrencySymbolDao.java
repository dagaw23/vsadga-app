package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;

public interface CurrencySymbolDao {

	int delete(Integer id);

	List<CurrencySymbol> getActiveSymbols();

	List<CurrencySymbol> getAll();

	CurrencySymbol getById(Integer id);

	CurrencySymbol getCurrencySymbolByName(String symbolName);

	int insert(CurrencySymbol currencySymbol);

	int update(String symbolName, boolean isActive, String futuresSymbol, Integer id);
	
	Integer getLastId();
}
