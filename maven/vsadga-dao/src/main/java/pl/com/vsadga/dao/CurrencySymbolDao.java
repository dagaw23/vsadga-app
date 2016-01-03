package pl.com.frxdream.dao;

import java.util.List;

import pl.com.frxdream.data.CurrencySymbol;

public interface CurrencySymbolDao {

	List<CurrencySymbol> getActiveSymbols();
}
