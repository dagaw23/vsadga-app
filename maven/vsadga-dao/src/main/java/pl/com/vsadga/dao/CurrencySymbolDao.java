package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;

public interface CurrencySymbolDao {

	List<CurrencySymbol> getActiveSymbols();
	
	List<CurrencySymbol> getAll();
	
	CurrencySymbol getCurrencySymbolByName(String symbolName);
	
	CurrencySymbol getById(Integer id);
	
	int update(String symbolName, boolean isActive, String futuresSymbol, Integer id);
}
