package eecalcs.systems;

/**
 * Container for residential and commercial system voltages
 */
public class SystemAC {
	public enum Phase{
		ph1(1),
		ph3(3);
		private int value;

		private Phase(int value){
			this.value = value;
		}

		public int getValue(){
			return value;
		}
	}

	public enum Voltage{									//neutral is CCC
		v120_1ph("120v 1Ø",120, Phase.ph1),	//2w:yes
		v208_1ph("208v 1Ø",208, Phase.ph1),	//2w:no neutral present; 3w:yes
		v208_3ph("208v 3Ø",208, Phase.ph3),	//3w:no; 4w:no; >50% harmonic:yes
		v240_1ph("240v 1Ø",240, Phase.ph1),	//2w:no neutral present; 3w:yes
		v240_3ph("240v 3Ø",240, Phase.ph3),	//3w:no; 4w:no; >50% harmonic:yes
		v277_1ph("277v 1Ø",277, Phase.ph1),	//2w:yes
		v480_1ph("480v 1Ø",480, Phase.ph1),	//2w:no neutral present; 3w:yes
		v480_3ph("480v 3Ø",480, Phase.ph3);	//3w:no; 4w:no; >50% harmonic:yes

		private String name;
		private int voltage;
		private Phase phases;
		private static String[] names;

		static{
			names = new String[values().length];
			for(int i=0; i<values().length; i++)
				names[i] = values()[i].getName();
		}

		private Voltage(String name, int voltage, Phase phases){
			this.name = name;
			this.voltage = voltage;
			this.phases = phases;
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

		/**
		 * returns the nominal voltage value associated with this voltage enum
		 * @return The nominal voltage in volts.
		 */
		public int getVoltage(){
			return voltage;
		}

		/**
		 * returns the number of phases associated with this voltage enum
		 * @return The number of phases.
		 */
		public Phase getPhases(){
			return phases;
		}

	}

	public enum Wires{
		W2("2 wires", 2),
		W3("3 wires", 3),
		W4("4 wires", 4);
		private String name;
		private int value;
		private static String[] names;

		static{
			names = new String[values().length];
			for(int i=0; i<values().length; i++)
				names[i] = values()[i].getName();
		}

		private Wires(String name, int value){
			this.name = name;
			this.value = value;
		}

		/**
		 * returns the number of wires this enum wire represents.
		 * @return the number of wires.
		 */
		public int getValue(){
			return value;
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
}
