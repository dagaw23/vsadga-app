package pl.com.vsadga.data;

/**
 * Zawiera informację o wielkości spreadu, jaki jest na barze w porównaniu z poprzednimi barami.
 */
public enum SpreadSize {
	
	VH(5), // Very High
	Hi(4), // High
	AV(3), // Average
	Lo(2), // Low
	VL(1), // Very Low
	N(0); // Not Defined
	
	/**
	 * waga spreadu
	 */
	private int weight;
	
	private SpreadSize(int weight) {
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}
	
}
