package eecalcs.conductors;

/**
 Defines the metal conductor types
 */
public enum Metal {
	COPPER("CU"),
	ALUMINUM("AL");
	private String symbol;
	private static String[] symbols;

	static{
		symbols = new String[values().length];
		for(int i=0; i<values().length; i++)
			symbols[i] = values()[i].getSymbol();
	}

	Metal(String name){
		this.symbol = name;
	}

	/**
	 Returns the string name that this enum represents.
	 @return The string name.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 Returns an array of the string names that the enum values represent.
	 @return An array of strings
	 */
	public static String[] getSymbols(){
		return symbols;
	}
}
