package pl.com.vsadga.data;

/**
 * Zawiera informację o wielkości wolumenu, jaki jest w porównaniu z poprzednimi barami. 
 */
public enum VolumeSize {
	UH(6), // Ultra High
	VH(5), // Very High
	Hi(4), // High
	AV(3), // Average
	Lo(2), // Low
	VL(1), // Very Low
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
