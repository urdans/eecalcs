package eecalcs.conductors;
/**
 * Contains only static booleans constants indicating coating
 */
public enum Coating {
	COATED(true), UNCOATED(false);
	private boolean coating;

	Coating(boolean coating){
		this.coating = coating;
	}

	/**
	 Asks if this Coating object is coated or not.
	 @return True if the value represents a coated copper conductor, false
	 otherwise.
	 */
	public boolean isCoated() {
		return coating;
	}
}
