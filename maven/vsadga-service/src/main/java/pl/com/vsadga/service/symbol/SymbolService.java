package pl.com.vsadga.service.symbol;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;

public interface SymbolService {

	List<CurrencySymbol> getActiveSymbols();
	
	List<CurrencySymbol> getAll();
	
	CurrencySymbol getById(Integer id);
	
	CurrencySymbol getCurrencySymbolByName(String symbolName);
	
	int update(CurrencySymbol currencySymbol);
}
