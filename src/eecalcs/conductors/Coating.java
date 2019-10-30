package eecalcs.conductors;
/**
 * Contains only static booleans constants indicating coating
 */
/*todo refactor this to use enum*/
public enum Coating {
	COATED(true), UNCOATED(false);
	private boolean coating;
	private Coating(boolean coating){
		this.coating = coating;
	}

	public boolean isCoated() {
		return coating;
	}
}
