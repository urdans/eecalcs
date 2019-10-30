package eecalcs.conductors;

/**
 * Contains only static strings with the conductor valid sizes
 */
public enum Size {
	AWG_14("14 AWG"),
	AWG_12("12 AWG"),
	AWG_10("10 AWG"),
	AWG_8("8 AWG"),
	AWG_6("6 AWG"),
	AWG_4("4 AWG"),
	AWG_3("3 AWG"),
	AWG_2("2 AWG"),
	AWG_1("1 AWG"),
	AWG_1$0("1/0 AWG"),
	AWG_2$0("2/0 AWG"),
	AWG_3$0("3/0 AWG"),
	AWG_4$0("4/0 AWG"),
	KCMIL_250("250 KCMIL"),
	KCMIL_300("300 KCMIL"),
	KCMIL_350("350 KCMIL"),
	KCMIL_400("400 KCMIL"),
	KCMIL_500("500 KCMIL"),
	KCMIL_600("600 KCMIL"),
	KCMIL_700("700 KCMIL"),
	KCMIL_750("750 KCMIL"),
	KCMIL_800("800 KCMIL"),
	KCMIL_900("900 KCMIL"),
	KCMIL_1000("1000 KCMIL"),
	KCMIL_1250("1250 KCMIL"),
	KCMIL_1500("1500 KCMIL"),
	KCMIL_1750("1750 KCMIL"),
	KCMIL_2000("2000 KCMIL");
	private String name;
	private static String[] names;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	private Size(String name){
		this.name = name;
	}

	/**
	 * Returns the string name that this enum represents.
	 * @return The string name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns an array of the string names that the enum values represent
	 * @return An array of strings
	 */
	public static String[] getNames(){
		return names;
	}
}