package eecalcs.conductors;

/**
 Contains only static strings with the insulation valid types
 */
public enum Insul{
	TW("TW"),
	RHW("RHW"),
	THW("THW"),
	THWN("THWN"),
	USE("USE"),
	ZW("ZW"),
	TBS("TBS"),
	SA("SA"),
	SIS("SIS"),
	FEP("FEP"),
	FEPB("FEPB"),
	MI("MI"),
	RHH("RHH"),
	RHW2("RHW-2"),
	THHN("THHN"),
	THHW("THHW"),
	THW2("THW-2"),
	THWN2("THWN-2"),
	USE2("USE-2"),
	XHH("XHH"),
	XHHW("XHHW"),
	XHHW2("XHHW-2"),
	ZW2("ZW-2");
	private String name;
	private static String[] names;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	Insul(String name){
		this.name = name;
	}

	/**
	 Returns the string name that this enum represents.

	 @return The string name.
	 */
	public String getName() {
		return name;
	}

	/**
	 Returns an array of the string names that the enum values represent.

	 @return An array of strings.
	 */
	public static String[] getNames(){
		return names;
	}
}