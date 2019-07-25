package eecalcs;

import java.util.ArrayList;
import java.util.List;

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

	public static class VoltageData {
		public String tag;
		public int value;
		public int phases;
		public VoltageData(String tag, int value, int phases){
			this.tag = tag;
			this.value = value;
			this.phases = phases;
		}
	}

	private SystemACVoltages() {

	}

	public static List<VoltageData> getVoltages() {
		return voltages;
	}
}
