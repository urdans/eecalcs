package eecalcs;

import static tools.Tools.getArrayIndexOf;

public class Conductor {
	//region enums
	public static enum Metal {
		COPPER, ALUMINUM
	}

	public static enum CopperCoating {
		COATED, UNCOATED
	}
	//endregion

	//region public static members forward
	public static Metal getMetalPerIndex(int conductorTypeIndex){
		if(conductorTypeIndex == 0)
			return Metal.COPPER;
		else
			return Metal.ALUMINUM;
	}

	public static String[] size = {"14", "12", "10", "8", "6", "4", "3", "2", "1", "1/0", "2/0", "3/0", "4/0", "250", "300", "350",
			"400", "500", "600", "700", "750", "800", "900", "1000", "1250", "1500", "1750", "2000"};
	public static String[] sizeFullName = {"AWG 14", "AWG 12", "AWG 10", "AWG 8", "AWG 6", "AWG 4", "AWG 3", "AWG 2", "AWG 1",
			"AWG 1/0", "AWG 2/0", "AWG 3/0", "AWG 4/0", "250 KCMIL", "300 KCMIL", "350 KCMIL", "400 KCMIL", "500 KCMIL", "600 KCMIL",
			"700 KCMIL", "750 KCMIL", "800 KCMIL", "900 KCMIL", "1000 KCMIL", "1250 KCMIL", "1500 KCMIL", "1750 KCMIL", "2000 KCMIL"};
	//endregion

	//region private static members
	//region tables
	private static Conductor[] table = {
			new Conductor(Conductor.size[0],   15,  20,  25,   0,   0,   0, 0.058000, 0.073000, 3.100000, 3.100000,	3.100000, 4.130600,4.130600, 4.130600,    4110, 3.070000, 3.190000, 5.060000),
			new Conductor(Conductor.size[1],   20,  25,  30,  15,  20,  25, 0.054000, 0.068000, 2.000000, 2.000000,	2.000000, 3.200000,3.200000, 3.200000,    6530, 1.930000, 2.010000, 3.180000),
			new Conductor(Conductor.size[2],   30,  35,  40,  25,  30,  35, 0.054000, 0.068000, 1.200000, 1.200000,	1.200000, 2.000000,2.000000, 2.000000,   10380, 1.210000, 1.260000, 2.000000),
			new Conductor(Conductor.size[3],   40,  50,  55,  35,  40,  45, 0.050000, 0.063000, 0.780000, 0.780000,	0.780000, 1.300000,1.300000, 1.300000,   16510, 0.764000, 0.786000, 1.260000),
			new Conductor(Conductor.size[4],   55,  65,  75,  40,  50,  55, 0.051000, 0.064000, 0.490000, 0.490000,	0.490000, 0.810000,0.810000, 0.810000,   26240, 0.491000, 0.510000, 0.808000),
			new Conductor(Conductor.size[5],   70,  85,  95,  55,  65,  75, 0.048000, 0.060000, 0.310000, 0.310000,	0.310000, 0.510000,0.510000, 0.510000,   41740, 0.308000, 0.321000, 0.508000),
			new Conductor(Conductor.size[6],   85, 100, 115,  65,  75,  85, 0.047000, 0.059000, 0.250000, 0.250000,	0.250000, 0.400000,0.410000, 0.400000,   52620, 0.245000, 0.254000, 0.403000),
			new Conductor(Conductor.size[7],   95, 115, 130,  75,  90, 100, 0.045000, 0.057000, 0.190000, 0.200000,	0.200000, 0.320000,0.320000, 0.320000,   66360, 0.194000, 0.201000, 0.319000),
			new Conductor(Conductor.size[8],  110, 130, 145,  85, 100, 115, 0.046000, 0.057000, 0.150000, 0.160000,	0.160000, 0.250000,0.260000, 0.250000,   83690, 0.154000, 0.160000, 0.253000),
			new Conductor(Conductor.size[9],  125, 150, 170, 100, 120, 135, 0.044000, 0.055000, 0.120000, 0.130000,	0.120000, 0.200000,0.210000, 0.200000,  105600, 0.122000, 0.127000, 0.201000),
			new Conductor(Conductor.size[10], 145, 175, 195, 115, 135, 150, 0.043000, 0.054000, 0.100000, 0.100000,	0.100000, 0.160000,0.160000, 0.160000,  133100, 0.096700, 0.101000, 0.159000),
			new Conductor(Conductor.size[11], 165, 200, 225, 130, 155, 175, 0.042000, 0.052000, 0.077000, 0.082000,	0.079000, 0.130000,0.130000, 0.130000,  167800, 0.076600, 0.079700, 0.126000),
			new Conductor(Conductor.size[12], 195, 230, 260, 150, 180, 205, 0.041000, 0.051000, 0.062000, 0.067000,	0.063000, 0.100000,0.110000, 0.100000,  211600, 0.060800, 0.062600, 0.100000),
			new Conductor(Conductor.size[13], 215, 255, 290, 170, 205, 230, 0.041000, 0.052000, 0.052000, 0.057000,	0.054000, 0.085000,0.090000, 0.086000,  250000, 0.051500, 0.053500, 0.084700),
			new Conductor(Conductor.size[14], 240, 285, 320, 195, 230, 260, 0.041000, 0.051000, 0.044000, 0.049000,	0.045000, 0.071000,0.076000, 0.072000,  300000, 0.042900, 0.044600, 0.070700),
			new Conductor(Conductor.size[15], 260, 310, 350, 210, 250, 280, 0.040000, 0.050000, 0.038000, 0.043000,	0.039000, 0.061000,0.066000, 0.063000,  350000, 0.036700, 0.038200, 0.060500),
			new Conductor(Conductor.size[16], 280, 335, 380, 225, 270, 305, 0.040000, 0.049000, 0.033000, 0.038000,	0.035000, 0.054000,0.059000, 0.055000,  400000, 0.032100, 0.033100, 0.052900),
			new Conductor(Conductor.size[17], 320, 380, 430, 260, 310, 350, 0.039000, 0.048000, 0.027000, 0.032000,	0.029000, 0.043000,0.048000, 0.045000,  500000, 0.025800, 0.026500, 0.042400),
			new Conductor(Conductor.size[18], 350, 420, 475, 285, 340, 385, 0.039000, 0.048000, 0.023000, 0.028000,	0.025000, 0.036000,0.041000, 0.038000,  600000, 0.021400, 0.022300, 0.035300),
			new Conductor(Conductor.size[19], 385, 460, 520, 315, 375, 425, 0.038500, 0.048000, 0.021000, 0.026000,	0.021900, 0.032500,0.038000, 0.033700,  700000, 0.018400, 0.018900, 0.030300),
			new Conductor(Conductor.size[20], 400, 475, 535, 320, 385, 435, 0.038000, 0.048000, 0.019000, 0.024000,	0.021000, 0.029000,0.034000, 0.031000,  750000, 0.017100, 0.017600, 0.028200),
			new Conductor(Conductor.size[21], 410, 490, 555, 330, 395, 445, 0.037800, 0.047600, 0.018200, 0.023000,	0.020400, 0.027800,0.032600, 0.029800,  800000, 0.016100, 0.016600, 0.026500),
			new Conductor(Conductor.size[22], 435, 520, 585, 355, 425, 480, 0.037400, 0.046800, 0.016600, 0.021000,	0.019200, 0.025400,0.029800, 0.027400,  900000, 0.014300, 0.014700, 0.023500),
			new Conductor(Conductor.size[23], 455, 545, 615, 375, 445, 500, 0.037000, 0.046000, 0.015000, 0.019000,	0.018000, 0.023000,0.027000, 0.025000, 1000000, 0.012900, 0.013200, 0.021200),
			new Conductor(Conductor.size[24], 495, 590, 665, 405, 485, 545, 0.036000, 0.046000, 0.011351, 0.014523,	0.014523, 0.017700,0.023436, 0.021600, 1250000, 0.010300, 0.010600, 0.016900),
			new Conductor(Conductor.size[25], 525, 625, 705, 435, 520, 585, 0.035000, 0.045000, 0.009798, 0.013127,	0.013127, 0.015000,0.020941, 0.019300, 1500000, 0.008580, 0.008830, 0.014100),
			new Conductor(Conductor.size[26], 545, 650, 735, 455, 545, 615, 0.034000, 0.045000, 0.008710, 0.012275,	0.012275, 0.013100,0.019205, 0.017700, 1750000, 0.007350, 0.007560, 0.012100),
			new Conductor(Conductor.size[27], 555, 665, 750, 470, 560, 630, 0.034000, 0.044000, 0.007928, 0.011703,	0.011703, 0.011700,0.018011, 0.016600, 2000000, 0.006430, 0.006620, 0.010600),
	};

	private static Conductor invalidConductor = new Conductor("Not assigned!", 0,0,0,0,0,0,0,0,0,0,0,15,20, 0,0,0,0,0);
	private static String[] insulation60Celsius = {"TW"};
	private static String[] insulation75Celsius = {"RHW", "THW", "THWN", "XHHW", "USE", "ZW"};
	private static String[] insulation90Celsius = {"TBS", "SA", "SIS", "FEP", "FEPB", "MI", "RHH", "RHW-2", "THHN", "THHW", "THW-2",
			"THWN-2", "USE-2", "XHH", "XHHW", "XHHW-2", "ZW-2"};

	private static String[] group1 = {"TW", "THHW", "THW", "THW-2"};
	//applies only to insulation types in group1, the order correspond to the 28 conductor sizes in array size
	private static double[] area1 = {0.0139, 0.0181, 0.0243, 0.0437, 0.0726, 0.0973, 0.1134, 0.1333, 0.1901, 0.2223, 0.2624, 0.3117,
			0.3718, 0.4596, 0.5281, 0.5958, 0.6619, 0.7901, 0.9729, 1.1010, 1.1652, 1.2272, 1.3561, 1.4784, 1.8602, 2.1695, 2.4773, 2.7818};

	private static String[] subGroup1 = {"RHW", "RHH", "RHW-2"};
	private static double[] subArea1 = {0.0209, 0.0260, 0.0333, 0.0556};

	private static String[] group2 = {"XHHW", "XHH", "XHHW-2"};
	private static String[] subGroup2 = {"ZW"};
	//applies only to insulation types in group2, the order correspond to the 28 conductor sizes in array size
	private static double[] area2 = {0.0139, 0.0181, 0.0243, 0.0437, 0.0590, 0.0814, 0.0962, 0.1146, 0.1534, 0.1825, 0.2190, 0.2642,
			0.3197, 0.3904, 0.4536, 0.5166, 0.5782, 0.6984, 0.8709, 0.9923, 1.0532, 1.1122, 1.2351, 1.3519, 1.7180, 2.0156, 2.3127, 2.6073};

	private static String[] group3 = {"THWN", "THWN-2", "THHN"};
	//applies only to insulation types in group3, the order correspond to 24 first conductor sizes in array size
	private static double[] area3 = {0.0097, 0.0133, 0.0211, 0.0366, 0.0507, 0.0824, 0.0973, 0.1158, 0.1562, 0.1855, 0.2223, 0.2679,
			0.3237, 0.3970, 0.4608, 0.5242, 0.5863, 0.7073, 0.8676, 0.9887, 1.0496, 1.1085, 1.2311, 1.3478};

	private static String[] group4 = {"FEP", "FEPB"};
	//applies only to insulation types in group4, the order correspond to 8 first conductor sizes in array size
	private static double[] area4 = {0.0100, 0.0137, 0.0191, 0.0333, 0.0468, 0.0670, 0.0804, 0.0973};
	//endregion tables
	
	//region methods
	private static boolean stringArrayContains(String[] array, String string) {
		return getArrayIndexOf(array, string) >= 0;
	}
	//endregion methods
	//endregion

	//region public static members
	public static double getAreaIn2(String conductorSize, String insulationName){
		if(!isValidSize(conductorSize))
			throw new EEToolsException("\"" + conductorSize + "\"" + " in not a valid conductor size.");

		if(!isValidInsulationName(insulationName))
			throw new EEToolsException("\"" + insulationName + "\"" + " in not a valid insulation type.");

		int conductorSizeIndex = getIndexOfSize(conductorSize);

		if(getArrayIndexOf(group1,insulationName) >= 0)
			return(area1[conductorSizeIndex]);

		if(getArrayIndexOf(group2,insulationName) >= 0)
			return(area2[conductorSizeIndex]);

		String exceptionMsg =
				"No area data for conductor " + getSizeFullName(conductorSize) +" with insulation type \"" + insulationName + "\".";

		if(getArrayIndexOf(group3,insulationName) >= 0) {
			if(conductorSizeIndex < 24)
				return (area3[conductorSizeIndex]);
			else
				throw new EEToolsException(exceptionMsg);
		}

		if(getArrayIndexOf(group4,insulationName) >= 0) {
			if(conductorSizeIndex < 8)
				return (area4[conductorSizeIndex]);
			else
				throw new EEToolsException(exceptionMsg);
		}

		if(getArrayIndexOf(subGroup2,insulationName) >= 0) {
			if(conductorSizeIndex < 8)
				return (area2[conductorSizeIndex]);
			else
				throw new EEToolsException(exceptionMsg);
		}

		if(getArrayIndexOf(subGroup1,insulationName) >= 0) {
			if(conductorSizeIndex < 4)
				return (subArea1[conductorSizeIndex]);
			else
				return (area1[conductorSizeIndex]);
		}
		return 0;
	}

	public static boolean insulationIs60Celsius(String insulationName){
		return stringArrayContains(insulation60Celsius, insulationName);
	}

	public static boolean insulationIs75Celsius(String insulationName){
		return stringArrayContains(insulation75Celsius, insulationName);
	}

	public static boolean insulationIs90Celsius(String insulationName){
		return stringArrayContains(insulation90Celsius, insulationName);
	}

	public static int getInsulationTemperatureCelsius(String insulationName){
		if(insulationIs60Celsius(insulationName)) return 60;
		else if(insulationIs75Celsius(insulationName)) return 75;
		else if(insulationIs90Celsius(insulationName)) return 75;
		else throw new EEToolsException("\"" + insulationName + "\"" + " in not a valid insulation type.");
	}

	public static int getIndexOfSize(String size) {
		return getArrayIndexOf(Conductor.size, size);
	}

	public static boolean isValidSize(String size) {
		return getIndexOfSize(size) != -1;
	}

	public static boolean isValidInsulationName(String insulationName) {
		return (getArrayIndexOf(insulation60Celsius, insulationName) != -1)
				|| (getArrayIndexOf(insulation75Celsius, insulationName) != -1)
				|| (getArrayIndexOf(insulation90Celsius, insulationName) != -1);
	}

	public static int compareSizes(String sizeLeft, String sizeRight){
		int left = getIndexOfSize(sizeLeft);
		int right = getIndexOfSize(sizeRight);
		if(left == -1 || right == -1)
			throw new EEToolsException("Cannot compare non existing conductor sizes");
		return Integer.compare(left, right);
	}

	public static Conductor getConductorBySize(String size) {
		for (int i = 0; i < table.length; i++){
			if (table[i].Size == size) return table[i];
		}
		throw new EEToolsException("\"" + size + "\"" +" is not a valid conductor size.");
	}

	public static String getSizeFullName(String size) {
		int index = getArrayIndexOf(Conductor.size, size);
		if(index < 0) throw new EEToolsException("Can't obtain a full conductor size name for an invalid conductor size: " + size);
		return sizeFullName[index];
	}

	public String getSizeFullName() {
		if(Size.equals("Not assigned!")) return Size;
		else return getSizeFullName(Size);
	}
	//endregion

	//region public field classes
	public class Copper {
		public CuResistance resistance = new CuResistance();
		public Ampacity ampacity = new Ampacity();
	}

	public class Aluminum {
		public AlResistance resistance = new AlResistance();
		public Ampacity ampacity = new Ampacity();
	}

	public class Ampacity {
		public int t60;
		public int t75;
		public int t90;
	}

	public class CuResistance {
		public AC ac = new AC();
		public DC dc = new DC();
	}

	public class AlResistance {
		public AC ac = new AC();
		public double dc;
	}

	public class Reactance {
		public double inNonMagCond;
		public double inMagCond;
	}

	public class AC {
		public double inPVCCond;
		public double inALCond;
		public double inSteelCond;
	}

	public class DC {
		public double coated;
		public double uncoated;
	}
	//endregion

	//region constructors
	private Conductor(String size, int CuAmp60, int CuAmp75, int CuAmp90, int AlAmp60, int AlAmp75, int AlAmp90, double nonMagXL,
	                  double magXL, double CuResInPVCCond, double CuResInALCond, double CuResInSteelCond, double ALResInPVCCond,
	                  double ALResInALCond, double ALResInSteelCond, int areaCM, double CuResDCUncoated, double CuResDCCoated,
	                  double ALResDC)
	{
		Size = size;
		this.areaCM = areaCM;
		copper.ampacity.t60 = CuAmp60;
		copper.ampacity.t75 = CuAmp75;
		copper.ampacity.t90 = CuAmp90;
		aluminum.ampacity.t60 = AlAmp60;
		aluminum.ampacity.t75 = AlAmp75;
		aluminum.ampacity.t90 = AlAmp90;
		reactance.inNonMagCond = nonMagXL;
		reactance.inMagCond = magXL;
		copper.resistance.ac.inPVCCond = CuResInPVCCond;
		copper.resistance.ac.inALCond = CuResInALCond;
		copper.resistance.ac.inSteelCond = CuResInSteelCond;
		copper.resistance.dc.uncoated = CuResDCUncoated;
		copper.resistance.dc.coated = CuResDCCoated;
		aluminum.resistance.ac.inPVCCond = ALResInPVCCond;
		aluminum.resistance.ac.inALCond = ALResInALCond;
		aluminum.resistance.ac.inSteelCond = ALResInSteelCond;
		aluminum.resistance.dc = ALResDC;
	}
	//endregion

	//region public fields
	public String Size;
	public int areaCM;
	public Copper copper = new Copper();
	public Aluminum aluminum = new Aluminum();
	public Reactance reactance = new Reactance();
	//endregion

	//region getters
	public static Conductor[] getTable() {
		return table;
	}

	public static Conductor getInvalidConductor(){
		return invalidConductor;
	}
	//endregion
}
