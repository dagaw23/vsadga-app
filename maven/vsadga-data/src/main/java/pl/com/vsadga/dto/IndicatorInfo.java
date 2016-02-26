package pl.com.vsadga.dto;

public class IndicatorInfo {

	/**
	 * wartość numeryczna wskaźnika
	 */
	private int indicatorNr;

	/**
	 * czy wskaźnik został przetworzony, czy za mało jest danych do jego przetworzenia (np. w
	 * przypadku za małej próbki danych do wyliczenia - dla początkowych wskaźników z kolekcji)
	 */
	private boolean isProcessIndy;

	public IndicatorInfo(boolean isProcessIndy) {
		super();
		this.indicatorNr = 0;
		this.isProcessIndy = isProcessIndy;
	}

	public IndicatorInfo(int indicatorNr, boolean isProcessIndy) {
		super();
		this.indicatorNr = indicatorNr;
		this.isProcessIndy = isProcessIndy;
	}

	/**
	 * @return the indicatorNr
	 */
	public int getIndicatorNr() {
		return indicatorNr;
	}

	/**
	 * @return the isProcessIndy
	 */
	public boolean isProcessIndy() {
		return isProcessIndy;
	}
	
}
