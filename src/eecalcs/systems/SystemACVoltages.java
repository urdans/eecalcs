package eecalcs.systems;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for residential and commercial system voltages
 */
public class SystemACVoltages {
	private static List<VoltageData> voltages;

	static {
		voltages = new ArrayList<>();
		voltages.add(new VoltageData("120v 1Ø", 120, 1));
		voltages.add(new VoltageData("208v 1Ø", 208, 1));
		voltages.add(new VoltageData("208v 3Ø", 208, 3));
		voltages.add(new VoltageData("240v 1Ø", 240, 1));
		voltages.add(new VoltageData("240v 3Ø", 240, 3));
		voltages.add(new VoltageData("277v 1Ø", 277, 1));
		voltages.add(new VoltageData("480v 1Ø", 480, 1));
		voltages.add(new VoltageData("480v 3Ø", 480, 3));
	}

	/**
	 * Inner class for the data structure containing infomation about the system voltage
	 */
	public static class VoltageData {
		/**
		 * A string containing the tag for this voltage system
		 */
		public String tag;
		/**
		 * The voltage of this voltage system
		 */
		public int value;
		/**
		 * The number of phases of this voltage system
		 */
		public int phases;

		/**
		 * Constructs a voltage system object with the given data. No data validation is performed
		 * @param tag The string tag of this system voltage
		 * @param value The voltage of this system voltage
		 * @param phases The number of phases of this system voltage
		 */
		public VoltageData(String tag, int value, int phases){
			this.tag = tag;
			this.value = value;
			this.phases = phases;
		}
	}

	private SystemACVoltages() {}

	/**
	 * Returns the list containing the registered system voltages
	 * @return The list containing the registered system voltages
	 */
	public static List<VoltageData> getVoltages() {
		return voltages;
	}
}
