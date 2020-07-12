package eecalcs.systems;
/**
 Enums to represent standards system voltages.
 <ul>
 <li><b>v120_1ph_2w</b>: 120v, 1Ø, 2 wires.</li>
 <li><b>v208_1ph_2w</b>: 208v, 1Ø, 2 wires.</li>
 <li><b>v208_1ph_3w</b>: 208v, 1Ø, 3 wires.</li>
 <li><b>v208_3ph_3w</b>: 208v, 3Ø, 3 wires.</li>
 <li><b>v208_3ph_4w</b>: 208v, 3Ø, 4 wires.</li>
 <li><b>v240_1ph_2w</b>: 240v, 1Ø, 2 wires.</li>
 <li><b>v240_1ph_3w</b>: 240v, 1Ø, 3 wires.</li>
 <li><b>v240_3ph_3w</b>: 240v, 3Ø, 3 wires.</li>
 <li><b>v240_3ph_4w</b>: 240v, 3Ø, 4 wires.</li>
 <li><b>v277_1ph_2w</b>: 277v, 1Ø, 2 wires.</li>
 <li><b>v480_1ph_2w</b>: 480v, 1Ø, 2 wires.</li>
 <li><b>v480_1ph_3w</b>: 480v, 1Ø, 3 wires.</li>
 <li><b>v480_3ph_2w</b>: 480v, 3Ø, 2 wires.</li>
 <li><b>v480_3ph_3w</b>: 480v, 3Ø, 3 wires.</li>
 </ul>
 */
public enum VoltageSystemAC{									   //neutral is CCC
	v120_1ph_2w("120v 1Ø 2W",120, 1, 2),//2w:yes
	v208_1ph_2w("208v 1Ø 2W",208, 1, 2),//2w:no neutral
	v208_1ph_2wN("208v 1Ø 2W High leg",208, 1, 2),//2w:yes
	v208_1ph_3w("208v 1Ø 3W",208, 1, 3),//3w:yes
	v208_3ph_3w("208v 3Ø 3W",208, 3, 3),//3w:no neutral
	v208_3ph_4w("208v 3Ø 4W",208, 3, 4),//4w:no, but if load>50% harmonic:yes
	v240_1ph_2w("240v 1Ø 2W",240, 1, 2),//2w:no neutral
	v240_1ph_3w("240v 1Ø 3W",240, 1, 3),//3w:yes
	v240_3ph_3w("240v 3Ø 3W",240, 3, 3),//3w:no neutral
	v240_3ph_4w("240v 3Ø 4W",240, 3, 4),//4w:no, but if load>50% harmonic:yes
	v277_1ph_2w("277v 1Ø 2W",277, 1, 2),//2w:yes
	v480_1ph_2w("480v 1Ø 2W",480, 1, 2),//2w:no neutral
	v480_1ph_3w("480v 1Ø 3W",480, 1, 3),//3w:yes
	v480_3ph_3w("480v 3Ø 3W",480, 3, 3),//3w:no neutral
	v480_3ph_4w("480v 3Ø 4W",480, 3, 4);//4w:no, but if load>50% harmonic:yes

	private String name;
	private int voltage;
	private int phases;
	private static String[] names;
	private int wires;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	VoltageSystemAC(String name, int voltage, int phases, int wires){
		this.name = name;
		this.voltage = voltage;
		this.phases = phases;
		this.wires = wires;
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
	public int getPhases(){
		return phases;
	}

	/**
	 Returns the number of wires associated with this voltage enum.

	 @return The number of wires.
	 */
	public int getWires(){
		return wires;
	}

	/**
	 Returns the square root of 3 for the ph3 value or 1 for the ph1.

	 @return The square root of 3 for the ph3 value or 1 for the ph1.
	 */
	public double getFactor(){
		return Math.sqrt(phases);
	}
}
