package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;

public interface CurrencySymbolDao {

	List<CurrencySymbol> getActiveSymbols();
	
	CurrencySymbol getCurrencySymbolByName(String symbolName);
}
