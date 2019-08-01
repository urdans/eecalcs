package eecalcs;

import tools.Tools;

public class CompactConductorProperties {
	//todo refactor this class to use hasmap
	//region private static members
	//These values correspond to the sizes in array ConductorProperties.size in the subset [3,22]-[6]-[21]
	private static double[] bareConductorAreasIn2 = {0.0141, 0.0224, 0.0356, 0, 0.0564, 0.0702, 0.0887, 0.1110, 0.1405, 0.1772, 0.2124,
			0.2552,	0.2980,	0.3411,	0.4254,	0.5191,	0.6041,	0.6475,	0, 0.7838,	0.8825};

	private static String[] group1 = {"RHH", "RHW", "USE"};
	//Applies only to insulation types in group1, the order correspond to the sizes in array ConductorProperties.size in the subset [3,22]-[6]-[21]
	private static double[] area1 = {0.0531, 0.0683, 0.0881, 0, 0.1194, 0.1698, 0.1963, 0.2290, 0.2733, 0.3217, 0.4015, 0.4596, 0.5153,
			0.5741, 0.6793, 0.8413, 0.9503, 1.0118, 0, 1.2076, 1.2968};

	private static String[] group2 = {"THW", "THHW"};
	//Applies only to insulation types in group2, the order correspond to the sizes in array ConductorProperties.size in the subset [3,22]-[6]-[21]
	private static double[] area2 = {0.0510, 0.0660, 0.0881, 0, 0.1194, 0.1698, 0.1963, 0.2332, 0.2733, 0.3267, 0.4128, 0.4717, 0.5281,
			0.5876, 0.6939, 0.8659, 0.9676, 1.0386, 0, 1.1766, 1.2968};

	private static String[] group3 = {"THHN"};
	//Applies only to insulation types in group3, the order correspond to the sizes in array ConductorProperties.size in the subset [4,22]-[6]-[21]
	private static double[] area3 = {0, 0.0452, 0.0730, 0, 0.1017, 0.1352, 0.1590, 0.1924, 0.2290, 0.2780, 0.3525, 0.4071, 0.4656, 0.5216,
			0.6151, 0.7620, 0.8659, 0.9076, 0, 1.1196, 1.2370};

	private static String[] group4 = {"XHHW"};
	//Applies only to insulation types in group3, the order correspond to the sizes in array ConductorProperties.size in the subset [3,22]-[6]-[21]
	private static double[] area4 = {0.0394, 0.0530, 0.0730, 0, 0.1017, 0.1352, 0.1590, 0.1885, 0.2290, 0.2733, 0.3421, 0.4015, 0.4536,
			0.5026, 0.6082, 0.7542, 0.8659, 0.9331, 0, 1.0733, 1.1882};

	/*private static int checkWireSize(String wireSize, String minSize, String maxSize, String insulationName){

		String msg = "No area data for compact conductor \"" + Conductor.getSizeFullName(wireSize) +"\"";

		if(!insulationName.isEmpty()) msg = msg + " with insulation type \"" + insulationName + "\"";

		msg = msg + ".";

		int wireSizeIndex = Conductor.getIndexOfSize(wireSize);

		if(wireSizeIndex < Conductor.getIndexOfSize(minSize)
				|| wireSizeIndex > Conductor.getIndexOfSize(maxSize)
				|| wireSizeIndex == Conductor.getIndexOfSize("3")
				|| wireSizeIndex == Conductor.getIndexOfSize("800"))
			throw new EEToolsException(msg);
//		private static Message ERROR54	= new Message("No area data for such compact conductor.", -54);
		return wireSizeIndex;
	}*/
	//endregion

	//region public static members
/*	public static double getAreaOfBareConductor(String wireSize) {
		//valid for >=8 AWG and <=1000 except 3 AWG and 800 KCMIL
		int wireSizeIndex = checkWireSize(wireSize, "8","1000", "");
		return bareConductorAreasIn2[wireSizeIndex - 3];
	}*/

/*	public static double getConductorWireAreaIn2(String wireSize, String insulationName){

		int wireSizeIndex = checkWireSize(wireSize, "8","1000", insulationName);

		if(Tools.getArrayIndexOf(group1, insulationName) >= 0)
			return(area1[wireSizeIndex - 3]);

		if(Tools.getArrayIndexOf(group2, insulationName) >= 0)
			return(area2[wireSizeIndex - 3]);

		if(Tools.getArrayIndexOf(group4, insulationName) >= 0)
			return(area4[wireSizeIndex - 3]);

		wireSizeIndex = checkWireSize(wireSize, "6","1000", insulationName);

		if(Tools.getArrayIndexOf(group3, insulationName) >= 0)
			return(area3[wireSizeIndex - 3]);

		throw new EEToolsException("No area data for compact conductor \"" + Conductor.getSizeFullName(wireSize) + "\" with " +
				"insulation type \"" + insulationName + "\".");
	}*/
	//endregion
}
