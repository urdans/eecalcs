package eecalcs;

import java.util.HashMap;
import java.util.Map;

import static tools.Tools.getArrayIndexOf;
import static tools.Tools.stringArrayContains;

public class Conductor {
	//region static members
	public enum Metal { COPPER, ALUMINUM }
	public enum CopperCoating { COATED, UNCOATED	}
	public static String[] sizes;
	public static String[] sizeFullName;
	private static Conductor[] table;
	private static Conductor invalidConductor;
	private static String[] insulation60Celsius;
	private static String[] insulation75Celsius;
	private static String[] insulation90Celsius;
	private static Map<String, Double> TW;   //0
	private static Map<String, Double> RHW;  //1
	private static Map<String, Double> THW;  //2
	private static Map<String, Double> THWN; //3
	private static Map<String, Double> ZW;   //5
	private static Map<String, Double> FEP;  //9
	private static Map<String, Double> FEPB; //10
	private static Map<String, Double> RHH;  //12
	private static Map<String, Double> RHW2; //13
	private static Map<String, Double> THHN; //14
	private static Map<String, Double> THHW; //15
	private static Map<String, Double> THW2; //16
	private static Map<String, Double> THWN2;//17
	private static Map<String, Double> XHH;  //19
	private static Map<String, Double> XHHW; //20
	private static Map<String, Double> XHHW2;//21
	private static Map<String, Double> EMPTY;//4, 6, 7, 8, 11, 18, 22
	private static Map<String, Map<String, Double>> insulatedDimensions;//dimension of insulated building conductors, Table 5
	private static Map<String, Double> compactRHH; //12
	private static Map<String, Double> compactRHW; //1
	private static Map<String, Double> compactUSE; //4
	private static Map<String, Double> compactTHW; //2
	private static Map<String, Double> compactTHHW;//15
	private static Map<String, Double> compactTHHN;//14
	private static Map<String, Double> compactXHHW;//20
	private static Map<String, Double> compactBareDimensions;
	private static Map<String, Map<String, Double>> compactDimensions;//dimension of insulated compact building conductors, Table 5A
	private static String[] insulations;

	public static double getInsulatedAreaIn2(String conductorSize, String insulationName){
		if(hasInsulatedArea(conductorSize, insulationName))
			return insulatedDimensions.get(insulationName).get(conductorSize);
		else
			return 0;
	}

	public static double getCompactAreaIn2(String conductorSize, String insulationName){
		if(hasCompactArea(conductorSize, insulationName))
			return compactDimensions.get(insulationName).get(conductorSize);
		else
			return 0;
	}

	public static double getCompactBareAreaIn2(String conductorSize){
		if(hasCompactBareArea(conductorSize))
			return compactBareDimensions.get(conductorSize);
		else
			return 0;
	}

	public static boolean hasInsulatedArea(String conductorSize, String insulationName){
		return isValidSize(conductorSize) && isValidInsulationName(insulationName) && insulatedDimensions.get(insulationName).containsKey(conductorSize);
	}

	public static boolean hasCompactArea(String conductorSize, String insulationName){
		return isValidSize(conductorSize) && isValidInsulationName(insulationName) && compactDimensions.containsKey(insulationName)
				&& compactDimensions.get(insulationName).containsKey(conductorSize);
	}

	public static boolean hasCompactBareArea(String conductorSize){
		return isValidSize(conductorSize) && compactBareDimensions.containsKey(conductorSize);
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
		else if(insulationIs90Celsius(insulationName)) return 90;
		return 0;
	}

	private static int getIndexOfSize(String size) {
		return getArrayIndexOf(Conductor.sizes, size);
	}

	public static Metal getMetalPerIndex(int conductorTypeIndex){
		if(conductorTypeIndex == 0)
			return Metal.COPPER;
		else
			return Metal.ALUMINUM;
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
		return Integer.compare(left, right);
	}

	public static Conductor getConductorBySize(String size) {
		for (int i = 0; i < table.length; i++)
			if (table[i].Size.equals(size)) return table[i];
		return invalidConductor;
	}

	public static String getSizeFullName(String size) {
		if(isValidSize(size))
			return sizeFullName[getArrayIndexOf(Conductor.sizes, size)];
		else
			return "";
	}

	public static Conductor[] getTable() {
		return table;
	}

	public static Conductor getInvalidConductor(){
		return invalidConductor;
	}

	static {
		//region conductor sizes and full size names
		sizes = new String[]{"14", "12", "10", "8", "6", "4", "3", "2", "1", "1/0", "2/0", "3/0", "4/0", "250", "300", "350",
				"400", "500", "600", "700", "750", "800", "900", "1000", "1250", "1500", "1750", "2000"};
		sizeFullName = new String[]{"AWG 14", "AWG 12", "AWG 10", "AWG 8", "AWG 6", "AWG 4", "AWG 3", "AWG 2", "AWG 1",
				"AWG 1/0", "AWG 2/0", "AWG 3/0", "AWG 4/0", "250 KCMIL", "300 KCMIL", "350 KCMIL", "400 KCMIL", "500 KCMIL", "600 KCMIL",
				"700 KCMIL", "750 KCMIL", "800 KCMIL", "900 KCMIL", "1000 KCMIL", "1250 KCMIL", "1500 KCMIL", "1750 KCMIL", "2000 KCMIL"};
		//endregion
		//region table of conductors' properties
		table = new Conductor[]{
				new Conductor(Conductor.sizes[0],   15,  20,  25,   0,   0,   0, 0.058000, 0.073000, 3.100000, 3.100000,	3.100000, 4.130600,4.130600, 4.130600,    4110, 3.070000, 3.190000, 5.060000),
				new Conductor(Conductor.sizes[1],   20,  25,  30,  15,  20,  25, 0.054000, 0.068000, 2.000000, 2.000000,	2.000000, 3.200000,3.200000, 3.200000,    6530, 1.930000, 2.010000, 3.180000),
				new Conductor(Conductor.sizes[2],   30,  35,  40,  25,  30,  35, 0.054000, 0.068000, 1.200000, 1.200000,	1.200000, 2.000000,2.000000, 2.000000,   10380, 1.210000, 1.260000, 2.000000),
				new Conductor(Conductor.sizes[3],   40,  50,  55,  35,  40,  45, 0.050000, 0.063000, 0.780000, 0.780000,	0.780000, 1.300000,1.300000, 1.300000,   16510, 0.764000, 0.786000, 1.260000),
				new Conductor(Conductor.sizes[4],   55,  65,  75,  40,  50,  55, 0.051000, 0.064000, 0.490000, 0.490000,	0.490000, 0.810000,0.810000, 0.810000,   26240, 0.491000, 0.510000, 0.808000),
				new Conductor(Conductor.sizes[5],   70,  85,  95,  55,  65,  75, 0.048000, 0.060000, 0.310000, 0.310000,	0.310000, 0.510000,0.510000, 0.510000,   41740, 0.308000, 0.321000, 0.508000),
				new Conductor(Conductor.sizes[6],   85, 100, 115,  65,  75,  85, 0.047000, 0.059000, 0.250000, 0.250000,	0.250000, 0.400000,0.410000, 0.400000,   52620, 0.245000, 0.254000, 0.403000),
				new Conductor(Conductor.sizes[7],   95, 115, 130,  75,  90, 100, 0.045000, 0.057000, 0.190000, 0.200000,	0.200000, 0.320000,0.320000, 0.320000,   66360, 0.194000, 0.201000, 0.319000),
				new Conductor(Conductor.sizes[8],  110, 130, 145,  85, 100, 115, 0.046000, 0.057000, 0.150000, 0.160000,	0.160000, 0.250000,0.260000, 0.250000,   83690, 0.154000, 0.160000, 0.253000),
				new Conductor(Conductor.sizes[9],  125, 150, 170, 100, 120, 135, 0.044000, 0.055000, 0.120000, 0.130000,	0.120000, 0.200000,0.210000, 0.200000,  105600, 0.122000, 0.127000, 0.201000),
				new Conductor(Conductor.sizes[10], 145, 175, 195, 115, 135, 150, 0.043000, 0.054000, 0.100000, 0.100000,	0.100000, 0.160000,0.160000, 0.160000,  133100, 0.096700, 0.101000, 0.159000),
				new Conductor(Conductor.sizes[11], 165, 200, 225, 130, 155, 175, 0.042000, 0.052000, 0.077000, 0.082000,	0.079000, 0.130000,0.130000, 0.130000,  167800, 0.076600, 0.079700, 0.126000),
				new Conductor(Conductor.sizes[12], 195, 230, 260, 150, 180, 205, 0.041000, 0.051000, 0.062000, 0.067000,	0.063000, 0.100000,0.110000, 0.100000,  211600, 0.060800, 0.062600, 0.100000),
				new Conductor(Conductor.sizes[13], 215, 255, 290, 170, 205, 230, 0.041000, 0.052000, 0.052000, 0.057000,	0.054000, 0.085000,0.090000, 0.086000,  250000, 0.051500, 0.053500, 0.084700),
				new Conductor(Conductor.sizes[14], 240, 285, 320, 195, 230, 260, 0.041000, 0.051000, 0.044000, 0.049000,	0.045000, 0.071000,0.076000, 0.072000,  300000, 0.042900, 0.044600, 0.070700),
				new Conductor(Conductor.sizes[15], 260, 310, 350, 210, 250, 280, 0.040000, 0.050000, 0.038000, 0.043000,	0.039000, 0.061000,0.066000, 0.063000,  350000, 0.036700, 0.038200, 0.060500),
				new Conductor(Conductor.sizes[16], 280, 335, 380, 225, 270, 305, 0.040000, 0.049000, 0.033000, 0.038000,	0.035000, 0.054000,0.059000, 0.055000,  400000, 0.032100, 0.033100, 0.052900),
				new Conductor(Conductor.sizes[17], 320, 380, 430, 260, 310, 350, 0.039000, 0.048000, 0.027000, 0.032000,	0.029000, 0.043000,0.048000, 0.045000,  500000, 0.025800, 0.026500, 0.042400),
				new Conductor(Conductor.sizes[18], 350, 420, 475, 285, 340, 385, 0.039000, 0.048000, 0.023000, 0.028000,	0.025000, 0.036000,0.041000, 0.038000,  600000, 0.021400, 0.022300, 0.035300),
				new Conductor(Conductor.sizes[19], 385, 460, 520, 315, 375, 425, 0.038500, 0.048000, 0.021000, 0.026000,	0.021900, 0.032500,0.038000, 0.033700,  700000, 0.018400, 0.018900, 0.030300),
				new Conductor(Conductor.sizes[20], 400, 475, 535, 320, 385, 435, 0.038000, 0.048000, 0.019000, 0.024000,	0.021000, 0.029000,0.034000, 0.031000,  750000, 0.017100, 0.017600, 0.028200),
				new Conductor(Conductor.sizes[21], 410, 490, 555, 330, 395, 445, 0.037800, 0.047600, 0.018200, 0.023000,	0.020400, 0.027800,0.032600, 0.029800,  800000, 0.016100, 0.016600, 0.026500),
				new Conductor(Conductor.sizes[22], 435, 520, 585, 355, 425, 480, 0.037400, 0.046800, 0.016600, 0.021000,	0.019200, 0.025400,0.029800, 0.027400,  900000, 0.014300, 0.014700, 0.023500),
				new Conductor(Conductor.sizes[23], 455, 545, 615, 375, 445, 500, 0.037000, 0.046000, 0.015000, 0.019000,	0.018000, 0.023000,0.027000, 0.025000, 1000000, 0.012900, 0.013200, 0.021200),
				new Conductor(Conductor.sizes[24], 495, 590, 665, 405, 485, 545, 0.036000, 0.046000, 0.011351, 0.014523,	0.014523, 0.017700,0.023436, 0.021600, 1250000, 0.010300, 0.010600, 0.016900),
				new Conductor(Conductor.sizes[25], 525, 625, 705, 435, 520, 585, 0.035000, 0.045000, 0.009798, 0.013127,	0.013127, 0.015000,0.020941, 0.019300, 1500000, 0.008580, 0.008830, 0.014100),
				new Conductor(Conductor.sizes[26], 545, 650, 735, 455, 545, 615, 0.034000, 0.045000, 0.008710, 0.012275,	0.012275, 0.013100,0.019205, 0.017700, 1750000, 0.007350, 0.007560, 0.012100),
				new Conductor(Conductor.sizes[27], 555, 665, 750, 470, 560, 630, 0.034000, 0.044000, 0.007928, 0.011703,	0.011703, 0.011700,0.018011, 0.016600, 2000000, 0.006430, 0.006620, 0.010600),
		};
		invalidConductor = new Conductor("Not assigned!", 0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0);
		//endregion
		//region temperature of insulators
		// XHHW & THHW are duplicated in 75 and 90 degrees columns. It is assumed both are 90 by definition of their double Hs
		insulation60Celsius = new String[]{"TW"};
		insulation75Celsius = new String[]{"RHW", "THW", "THWN", "USE", "ZW"};
		insulation90Celsius = new String[]{"TBS", "SA", "SIS", "FEP", "FEPB", "MI", "RHH", "RHW-2", "THHN", "THHW", "THW-2",
				"THWN-2", "USE-2", "XHH", "XHHW", "XHHW-2", "ZW-2"};
		insulations = new String[insulation60Celsius.length + insulation75Celsius.length + insulation90Celsius.length];
		System.arraycopy(insulation60Celsius, 0, insulations,0, insulation60Celsius.length);
		System.arraycopy(insulation75Celsius, 0, insulations, insulation60Celsius.length, insulation75Celsius.length);
		System.arraycopy(insulation90Celsius, 0, insulations, insulation60Celsius.length + insulation75Celsius.length,
				insulation90Celsius.length);
		//endregion
		//region TW
		TW = new HashMap<>();
		TW.put(sizes[0], 0.0139);
		TW.put(sizes[1], 0.0181);
		TW.put(sizes[2], 0.0243);
		TW.put(sizes[3], 0.0437);
		TW.put(sizes[4], 0.0726);
		TW.put(sizes[5], 0.0973);
		TW.put(sizes[6], 0.1134);
		TW.put(sizes[7], 0.1333);
		TW.put(sizes[8], 0.1901);
		TW.put(sizes[9], 0.2223);
		TW.put(sizes[10], 0.2624);
		TW.put(sizes[11], 0.3117);
		TW.put(sizes[12], 0.3718);
		TW.put(sizes[13], 0.4596);
		TW.put(sizes[14], 0.5281);
		TW.put(sizes[15], 0.5958);
		TW.put(sizes[16], 0.6619);
		TW.put(sizes[17], 0.7901);
		TW.put(sizes[18], 0.9729);
		TW.put(sizes[19], 1.101);
		TW.put(sizes[20], 1.1652);
		TW.put(sizes[21], 1.2272);
		TW.put(sizes[22], 1.3561);
		TW.put(sizes[23], 1.4784);
		TW.put(sizes[24], 1.8602);
		TW.put(sizes[25], 2.1695);
		TW.put(sizes[26], 2.4773);
		TW.put(sizes[27], 2.7818);
		//endregion
		//region RHW
		RHW = new HashMap<>();
		RHW.put(sizes[0], 0.0209);
		RHW.put(sizes[1], 0.026);
		RHW.put(sizes[2], 0.0333);
		RHW.put(sizes[3], 0.0556);
		RHW.put(sizes[4], 0.0726);
		RHW.put(sizes[5], 0.0973);
		RHW.put(sizes[6], 0.1134);
		RHW.put(sizes[7], 0.1333);
		RHW.put(sizes[8], 0.1901);
		RHW.put(sizes[9], 0.2223);
		RHW.put(sizes[10], 0.2624);
		RHW.put(sizes[11], 0.3117);
		RHW.put(sizes[12], 0.3718);
		RHW.put(sizes[13], 0.4596);
		RHW.put(sizes[14], 0.5281);
		RHW.put(sizes[15], 0.5958);
		RHW.put(sizes[16], 0.6619);
		RHW.put(sizes[17], 0.7901);
		RHW.put(sizes[18], 0.9729);
		RHW.put(sizes[19], 1.101);
		RHW.put(sizes[20], 1.1652);
		RHW.put(sizes[21], 1.2272);
		RHW.put(sizes[22], 1.3561);
		RHW.put(sizes[23], 1.4784);
		RHW.put(sizes[24], 1.8602);
		RHW.put(sizes[25], 2.1695);
		RHW.put(sizes[26], 2.4773);
		RHW.put(sizes[27], 2.7818);
		//endregion
		//region THW
		THW = TW;
		//endregion
		//region THWN
		THWN = new HashMap<>();
		THWN.put(sizes[0], 0.0097);
		THWN.put(sizes[1], 0.0133);
		THWN.put(sizes[2], 0.0211);
		THWN.put(sizes[3], 0.0366);
		THWN.put(sizes[4], 0.0507);
		THWN.put(sizes[5], 0.0824);
		THWN.put(sizes[6], 0.0973);
		THWN.put(sizes[7], 0.1158);
		THWN.put(sizes[8], 0.1562);
		THWN.put(sizes[9], 0.1855);
		THWN.put(sizes[10], 0.2223);
		THWN.put(sizes[11], 0.2679);
		THWN.put(sizes[12], 0.3237);
		THWN.put(sizes[13], 0.397);
		THWN.put(sizes[14], 0.4608);
		THWN.put(sizes[15], 0.5242);
		THWN.put(sizes[16], 0.5863);
		THWN.put(sizes[17], 0.7073);
		THWN.put(sizes[18], 0.8676);
		THWN.put(sizes[19], 0.9887);
		THWN.put(sizes[20], 1.0496);
		THWN.put(sizes[21], 1.1085);
		THWN.put(sizes[22], 1.2311);
		THWN.put(sizes[23], 1.3478);
		//endregion
		//region ZW
		ZW = new HashMap<>();
		ZW.put(sizes[0], 0.0139);
		ZW.put(sizes[1], 0.0181);
		ZW.put(sizes[2], 0.0243);
		ZW.put(sizes[3], 0.0437);
		ZW.put(sizes[4], 0.059);
		ZW.put(sizes[5], 0.0814);
		ZW.put(sizes[6], 0.0962);
		ZW.put(sizes[7], 0.1146);
		//endregion
		//region FEP
		FEP = new HashMap<>();
		FEP.put(sizes[0], 0.01);
		FEP.put(sizes[1], 0.0137);
		FEP.put(sizes[2], 0.0191);
		FEP.put(sizes[3], 0.0333);
		FEP.put(sizes[4], 0.0468);
		FEP.put(sizes[5], 0.067);
		FEP.put(sizes[6], 0.0804);
		FEP.put(sizes[7], 0.0973);
		//endregion
		//region FEPB
		FEPB = FEP;
		//endregion
		//region RHH
		RHH = RHW;
		//endregion
		//region RHW-2
		RHW2 = RHW;
		//endregion
		//region THHN
		THHN = THWN;
		//endregion
		//region THHW
		THHW = TW;
		//endregion
		//region THW-2
		THW2 = TW;
		//endregion
		//region THWN2
		THWN2 = THWN;
		//endregion
		//region XHH
		XHH = new HashMap<>();
		XHH.put(sizes[0], 0.0139);
		XHH.put(sizes[1], 0.0181);
		XHH.put(sizes[2], 0.0243);
		XHH.put(sizes[3], 0.0437);
		XHH.put(sizes[4], 0.059);
		XHH.put(sizes[5], 0.0814);
		XHH.put(sizes[6], 0.0962);
		XHH.put(sizes[7], 0.1146);
		XHH.put(sizes[8], 0.1534);
		XHH.put(sizes[9], 0.1825);
		XHH.put(sizes[10], 0.219);
		XHH.put(sizes[11], 0.2642);
		XHH.put(sizes[12], 0.3197);
		XHH.put(sizes[13], 0.3904);
		XHH.put(sizes[14], 0.4536);
		XHH.put(sizes[15], 0.5166);
		XHH.put(sizes[16], 0.5782);
		XHH.put(sizes[17], 0.6984);
		XHH.put(sizes[18], 0.8709);
		XHH.put(sizes[19], 0.9923);
		XHH.put(sizes[20], 1.0532);
		XHH.put(sizes[21], 1.1122);
		XHH.put(sizes[22], 1.2351);
		XHH.put(sizes[23], 1.3519);
		XHH.put(sizes[24], 1.718);
		XHH.put(sizes[25], 2.0156);
		XHH.put(sizes[26], 2.3127);
		XHH.put(sizes[27], 2.6073);
		//endregion
		//region XHHW
		XHHW = XHH;
		//endregion
		//region XHHW-2
		XHHW2 = XHH;
		//endregion
		//region areaEMPTY for insulations TBS, SA, SIS, MI, USE, USE-2 and ZW-2
		EMPTY = new HashMap<>();
		//endregion
		//region dimensions of insulated conductors
		insulatedDimensions = new HashMap<>();
		insulatedDimensions.put(insulations[0], TW);
		insulatedDimensions.put(insulations[1], RHW);
		insulatedDimensions.put(insulations[2], THW);
		insulatedDimensions.put(insulations[3], THWN);
		insulatedDimensions.put(insulations[4], EMPTY);  //USE
		insulatedDimensions.put(insulations[5], ZW);
		insulatedDimensions.put(insulations[6], EMPTY);  //TBS
		insulatedDimensions.put(insulations[7], EMPTY);  //SA
		insulatedDimensions.put(insulations[8], EMPTY);  //SIS
		insulatedDimensions.put(insulations[9], FEP);
		insulatedDimensions.put(insulations[10], FEPB);
		insulatedDimensions.put(insulations[11], EMPTY); //MI
		insulatedDimensions.put(insulations[12], RHH);
		insulatedDimensions.put(insulations[13], RHW2);
		insulatedDimensions.put(insulations[14], THHN);
		insulatedDimensions.put(insulations[15], THHW);
		insulatedDimensions.put(insulations[16], THW2);
		insulatedDimensions.put(insulations[17], THWN2);
		insulatedDimensions.put(insulations[18], EMPTY); //USE-2
		insulatedDimensions.put(insulations[19], XHH);
		insulatedDimensions.put(insulations[20], XHHW);
		insulatedDimensions.put(insulations[21], XHHW2);
		insulatedDimensions.put(insulations[22], EMPTY); //ZW-2
		//endregion
		//region compactRHH
		compactRHH = new HashMap<>();     //conductor size
		compactRHH.put(sizes[3], 0.0531); //8
		compactRHH.put(sizes[4], 0.0683); //6
		compactRHH.put(sizes[5], 0.0881); //4
		compactRHH.put(sizes[7], 0.1194); //2
		compactRHH.put(sizes[8], 0.1698); //1
		compactRHH.put(sizes[9], 0.1963); //1/0
		compactRHH.put(sizes[10], 0.229); //2/0
		compactRHH.put(sizes[11], 0.2733); //3/0
		compactRHH.put(sizes[12], 0.3217); //4/0
		compactRHH.put(sizes[13], 0.4015); //250
		compactRHH.put(sizes[14], 0.4596); //300
		compactRHH.put(sizes[15], 0.5153); //350
		compactRHH.put(sizes[16], 0.5741); //400
		compactRHH.put(sizes[17], 0.6793); //500
		compactRHH.put(sizes[18], 0.8413); //600
		compactRHH.put(sizes[19], 0.9503); //700
		compactRHH.put(sizes[20], 1.0118); //750
		compactRHH.put(sizes[22], 1.2076); //900
		compactRHH.put(sizes[23], 1.2968); //1000
		//endregion
		//region compactRHW
		compactRHW = compactRHH;
		//endregion
		//region compactUSE
		compactUSE = compactRHH;
		//endregion
		//region compactTHW
		compactTHW = new HashMap<>();    //conductor size
		compactTHW.put(sizes[3], 0.051); //8
		compactTHW.put(sizes[4], 0.066); //6
		compactTHW.put(sizes[5], 0.0881); //4
		compactTHW.put(sizes[7], 0.1194); //2
		compactTHW.put(sizes[8], 0.1698); //1
		compactTHW.put(sizes[9], 0.1963); //1/0
		compactTHW.put(sizes[10], 0.2332); //2/0
		compactTHW.put(sizes[11], 0.2733); //3/0
		compactTHW.put(sizes[12], 0.3267); //4/0
		compactTHW.put(sizes[13], 0.4128); //250
		compactTHW.put(sizes[14], 0.4717); //300
		compactTHW.put(sizes[15], 0.5281); //350
		compactTHW.put(sizes[16], 0.5876); //400
		compactTHW.put(sizes[17], 0.6939); //500
		compactTHW.put(sizes[18], 0.8659); //600
		compactTHW.put(sizes[19], 0.9676); //700
		compactTHW.put(sizes[20], 1.0386); //750
		compactTHW.put(sizes[22], 1.1766); //900
		compactTHW.put(sizes[23], 1.2968); //1000
		//endregion
		//region compactTHHW
		compactTHHW = compactTHW;
		//endregion
		//region compactTHHN
		compactTHHN = new HashMap<>();     //conductor size
		compactTHHN.put(sizes[4], 0.0452); //6
		compactTHHN.put(sizes[5], 0.073);  //4
		compactTHHN.put(sizes[7], 0.1017); //2
		compactTHHN.put(sizes[8], 0.1352); //1
		compactTHHN.put(sizes[9], 0.159);  //1/0
		compactTHHN.put(sizes[10], 0.1924);//2/0
		compactTHHN.put(sizes[11], 0.229); //3/0
		compactTHHN.put(sizes[12], 0.278); //4/0
		compactTHHN.put(sizes[13], 0.3525);//250
		compactTHHN.put(sizes[14], 0.4071);//300
		compactTHHN.put(sizes[15], 0.4656);//350
		compactTHHN.put(sizes[16], 0.5216);//400
		compactTHHN.put(sizes[17], 0.6151);//500
		compactTHHN.put(sizes[18], 0.762); //600
		compactTHHN.put(sizes[19], 0.8659);//700
		compactTHHN.put(sizes[20], 0.9076);//750
		compactTHHN.put(sizes[22], 1.1196);//900
		compactTHHN.put(sizes[23], 1.237); //1000
		//endregion
		//region compactXHHW
		compactXHHW = new HashMap<>();      //conductor size
		compactXHHW.put(sizes[3], 0.0394);  //8
		compactXHHW.put(sizes[4], 0.053);   //6
		compactXHHW.put(sizes[5], 0.073);   //4
		compactXHHW.put(sizes[7], 0.1017);  //2
		compactXHHW.put(sizes[8], 0.1352);  //1
		compactXHHW.put(sizes[9], 0.159);   //1/0
		compactXHHW.put(sizes[10], 0.1885); //2/0
		compactXHHW.put(sizes[11], 0.229);  //3/0
		compactXHHW.put(sizes[12], 0.2733); //4/0
		compactXHHW.put(sizes[13], 0.3421); //250
		compactXHHW.put(sizes[14], 0.4015); //300
		compactXHHW.put(sizes[15], 0.4536); //350
		compactXHHW.put(sizes[16], 0.5026); //400
		compactXHHW.put(sizes[17], 0.6082); //500
		compactXHHW.put(sizes[18], 0.7542); //600
		compactXHHW.put(sizes[19], 0.8659); //700
		compactXHHW.put(sizes[20], 0.9331); //750
		compactXHHW.put(sizes[22], 1.0733); //900
		compactXHHW.put(sizes[23], 1.1882); //1000
		//endregion
		//region dimension of compact conductors
		compactDimensions = new HashMap<>();
		compactDimensions.put(insulations[12], compactRHH);
		compactDimensions.put(insulations[ 1], compactRHW);
		compactDimensions.put(insulations[ 4], compactUSE);
		compactDimensions.put(insulations[ 2], compactTHW);
		compactDimensions.put(insulations[15], compactTHHW);
		compactDimensions.put(insulations[14], compactTHHN);
		compactDimensions.put(insulations[20], compactXHHW);
		//endregion
		//region dimension of compact bare conductors
		compactBareDimensions = new HashMap<>();      //conductor size
		compactBareDimensions.put(sizes[3], 0.0141);  //8
		compactBareDimensions.put(sizes[4], 0.0224);  //6
		compactBareDimensions.put(sizes[5], 0.0356);  //4
		compactBareDimensions.put(sizes[7], 0.0564);  //2
		compactBareDimensions.put(sizes[8], 0.0702);  //1
		compactBareDimensions.put(sizes[9], 0.0887);  //1/0
		compactBareDimensions.put(sizes[10], 0.111);  //2/0
		compactBareDimensions.put(sizes[11], 0.1405); //3/0
		compactBareDimensions.put(sizes[12], 0.1772); //4/0
		compactBareDimensions.put(sizes[13], 0.2124); //250
		compactBareDimensions.put(sizes[14], 0.2552); //300
		compactBareDimensions.put(sizes[15], 0.298);  //350
		compactBareDimensions.put(sizes[16], 0.3411); //400
		compactBareDimensions.put(sizes[17], 0.4254); //500
		compactBareDimensions.put(sizes[18], 0.5191); //600
		compactBareDimensions.put(sizes[19], 0.6041); //700
		compactBareDimensions.put(sizes[20], 0.6475); //750
		compactBareDimensions.put(sizes[22], 0.7838); //900
		compactBareDimensions.put(sizes[23], 0.8825); //1000
		//endregion
	}
	//endregion
	//region instance members
	public String Size;
	public int areaCM;
	public Copper copper = new Copper();
	public Aluminum aluminum = new Aluminum();
	public Reactance reactance = new Reactance();

	private Conductor(String size, int CuAmp60, int CuAmp75, int CuAmp90, int AlAmp60, int AlAmp75, int AlAmp90, double nonMagXL,
	                  double magXL, double CuResInPVCCond, double CuResInALCond, double CuResInSteelCond, double ALResInPVCCond,
	                  double ALResInALCond, double ALResInSteelCond, int areaCM, double CuResDCUncoated, double CuResDCCoated,
	                  double ALResDC) {
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
/*
The actual class should have a different name, like ConductorProperties.

This class groups all the properties related to a conductor of a defined size. The class is able to build and return any conductor with
all these properties (as defined in NEC2014) and also build a conductor object (as defined below) that encapsulates only the
properties that pertain to a particular set of conditions.

A conductor is an entity that encapsulates all the properties of a single conductor, when it is isolated, that is, the
represented characteristics don't depend upon other conditions, like ambient temperature, number of conductor per raceway, type of
raceway, voltage type (AC or DC), number of phases, special locations, load types, etc.

Class Conductor:
----------------
The independent properties of a conductor are:
-size
-metal (CU or AL)
-insulation (if any)
-length (one way length)

Class CircuitConductor:
-----------------------
The resistance and reactance of the conductor will depend on the raceway type and arrangement:
-If conductors are in free air or tray
-If they are inside a conduit and then, the metal of the conduit
-The number of conductors inside the raceway that are not in parallel (this should affect the resistance and the reactance of the
conductor but I haven't found a formulae or method that correlates these characteristics)
-The number of conductors inside the conduit that are in parallel (same note as before; table 9 is based on the assumption the system
voltage is three phase, 75°C, 60Hz, three single conductors in conduit; so, unless more information is found, I will always use the
values of table 9 but will leave room for improvement once the method that considers different scenarios is found).

-The ampacity of the conductor will depend mainly on all the above listed variables but also on the location of the conductor, like when
it is in the rooftop (and the distance from the floor)

Class Feeder, Service, Branch and Tap:
--------------------------------------
-These classes are similar. They differ in the fact that the branch circuit directly feeds a load, while a feeder has a OCPD on each end.
A special Feeder is the Service class.
SOme of the properties of these classes are:
-Voltage
-Phases,
-Frequency

The user must put the class CircuitConductor in the context of any of the classes Feeder, Service or Branch.

For instance, the Branch class has a load object. The Feeder has a load intent. One or more branch circuits will always be connected to a
feeder through an OCPD.

Branch circuits can be multiwire
Loads can be continuous or non continuous.

Other classes must be designed, like for fuses, breakers, loads, motors, appliances, whatever, lights, AC equipment, panel, switchboard,
etc, etc.






 */
	public double getInsulatedAreaIn2(String insulationName){
		return getInsulatedAreaIn2(this.Size, insulationName);
	}

	public double getCompactAreaIn2(String insulationName){
		return getCompactAreaIn2(this.Size, insulationName);
	}

	public double getCompactBareAreaIn2(){
		if(hasCompactBareArea())
			return compactBareDimensions.get(this.Size);
		else
			return 0;
	}

	public boolean hasInsulatedArea(String insulationName){
		return hasInsulatedArea(this.Size, insulationName);
	}

	public boolean hasCompactArea(String insulationName){
		return hasCompactArea(this.Size, insulationName);
	}

	public boolean hasCompactBareArea(){
		return isValidSize(this.Size) && compactBareDimensions.containsKey(this.Size);
	}

	public boolean isValidSize() {
		return isValidSize(this.Size);
	}

	public boolean isInvalid(){
		return this == invalidConductor;
	}

	public String getSizeFullName() {
		if(this == invalidConductor)
			return Size;
		return getSizeFullName(Size);
	}

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

	public double getACConductorResistance(Conductor.Metal conductorMetal, Conduit.Material conduitMaterial,
	                                       double length, int numberOfSets){
		//region compute total conductor resistance
		double resistance;
		if(conductorMetal == Conductor.Metal.COPPER){
			if (conduitMaterial == Conduit.Material.PVC) {
				resistance = this.copper.resistance.ac.inPVCCond;
			} else if (conduitMaterial == Conduit.Material.ALUMINUM) {
				resistance = this.copper.resistance.ac.inALCond;
			} else {
				resistance = this.copper.resistance.ac.inSteelCond;
			}
		}else{ //conductor is aluminum
			if (conduitMaterial == Conduit.Material.PVC) {
				resistance = this.aluminum.resistance.ac.inPVCCond;
			} else if (conduitMaterial == Conduit.Material.ALUMINUM) {
				resistance = this.aluminum.resistance.ac.inALCond;
			} else {
				resistance = this.aluminum.resistance.ac.inSteelCond;
			}
		}
		resistance = resistance * length * 0.001 / numberOfSets;
		return resistance;
	}

	public double getDCConductorResistance(Conductor.Metal conductorMetal, Conductor.CopperCoating copperCoating,
	                                       double length, int numberOfSets){
		double resistance;
		if(conductorMetal == Conductor.Metal.COPPER){
			if (copperCoating == Conductor.CopperCoating.COATED)
				resistance = this.copper.resistance.dc.coated;
			else
				resistance = this.copper.resistance.dc.uncoated;
		}else
			resistance = this.aluminum.resistance.dc;
		resistance = resistance * length * 0.001 / numberOfSets;
		return resistance;
	}
	//endregion
}
/*RELEASE NOTES
As a general rule, when a class computes things whose results are predictable, the class should not raise any exceptions nor manage
any error messages. Simply, it must return empty string values, or singular int or double numbers, or null, but also should provide the
caller with helper methods for validation of the input data, like validating that a conductor size is correct before calling any function
that uses size as parameter.
*/