package eecalcs.systems;

/**
 Container for residential and commercial system voltages.
 */
public class SystemAC {
	/**
	 Enum to represent phases.
	 <br>
	 <ul>
	 <li><b>ph1</b>: 1</li>
	 <li><b>ph3</b>: 3</li>
	 </ul>
	 */
	public enum Phase{
		ph1(1),
		ph3(3);
		private int numberOfPhases;
		Phase(int numberOfPhases){
			this.numberOfPhases = numberOfPhases;
		}
		public int getNumberOfPhases(){
			return numberOfPhases;
		}
	}

	/**
	 Enums to represent standards system voltages.
	 <ul>
	 <li><b>v120_1ph</b> -> 120v 1Ø -> 2 wires.</li>
	 <li><b>v208_1ph</b> -> 208v 1Ø -> 2 or 3 wires.</li>
	 <li><b>v208_3ph</b> -> 208v 3Ø -> 3 or 4 wires.</li>
	 <li><b>v240_1ph</b> -> 240v 1Ø -> 2 or 3 wires.</li>
	 <li><b>v240_3ph</b> -> 240v 3Ø -> 3 or 4 wires.</li>
	 <li><b>v277_1ph</b> -> 277v 1Ø -> 2 wires.</li>
	 <li><b>v480_1ph</b> -> 480v 1Ø -> 2 or 3 wires.</li>
	 <li><b>v480_3ph</b> -> 480v 3Ø -> 3 or 2 wires.</li>
	 </ul>
	 */
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
		private Wires wires;

		static{
			names = new String[values().length];
			for(int i=0; i<values().length; i++)
				names[i] = values()[i].getName();
		}

		Voltage(String name, int voltage, Phase phases){
			this.name = name;
			this.voltage = voltage;
			this.phases = phases;
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

		 @return An array of strings
		 */
		public static String[] getNames(){
			return names;
		}

		/**
		 Returns the nominal voltage value associated with this voltage enum.

		 @return The nominal voltage in volts.
		 */
		public int getVoltage(){
			return voltage;
		}

		/**
		 Returns the number of phases associated with this voltage enum.

		 @return The number of phases.
		 */
		public Phase getPhases(){
			return phases;
		}

		/**
		 Returns the square root of 3 for the ph3 value or 1 for the ph1.

		 @return The square root of 3 for the ph3 value or 1 for the ph1.
		 */
		public double getFactor(){
			return Math.sqrt(phases.getNumberOfPhases());
		}
	}

	/**
	 Enums to represent the number of wires in a system. This never counts the
	 grounding conductor, only hots and neutrals.
	 <br>
	 <ul>
	 <li><b>W2</b>: "2 wires", 2</li>
	 <li><b>W3</b>: "3 wires", 3</li>
	 <li><b>W4</b>: "4 wires", 4</li>
	 </ul>
	 */
	public enum Wires{
		W2("2 wires", 2),//1h+1n or 2h
		W3("3 wires", 3),//3h or 2h+1n
		W4("4 wires", 4);//3h+1n
		private String name;
		private int value;
		private static String[] names;

		static{
			names = new String[values().length];
			for(int i=0; i<values().length; i++)
				names[i] = values()[i].getName();
		}

		Wires(String name, int value){
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
