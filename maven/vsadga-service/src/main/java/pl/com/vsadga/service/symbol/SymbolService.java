package pl.com.frxdream.service.symbol;

import java.util.List;

import pl.com.frxdream.data.CurrencySymbol;

public interface SymbolService {

	List<CurrencySymbol> getActiveSymbols();
}
