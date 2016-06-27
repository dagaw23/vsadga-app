package pl.com.vsadga.data;

/**
 * Zawiera informację o wielkości wolumenu, jaki jest w porównaniu z poprzednimi barami. 
 */
public enum VolumeSize {
	VH(4), // Ultra High
	Hi(3), // High
	AV(2), // Average
	Lo(1), // Low
	N(0); // Not Defined
	
	/**
	 * waga wolumenu
	 */
	private int weight;
	
	private VolumeSize(int weight) {
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}
}
