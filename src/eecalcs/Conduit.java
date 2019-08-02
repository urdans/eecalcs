package eecalcs;

import java.util.HashMap;
import java.util.Map;

public class Conduit {
	public static enum Material { PVC, ALUMINUM, STEEL }
	private static String[] tradeSizes;
	private static String[] types;
	private static Map<String, Double> areaEMT;
	private static Map<String, Double> areaENT;
	private static Map<String, Double> areaFMT;
	private static Map<String, Double> areaIMC;
	private static Map<String, Double> areaLFNCA;
	private static Map<String, Double> areaLFNCB;
	private static Map<String, Double> areaLFMC;
	private static Map<String, Double> areaRMC;
	private static Map<String, Double> areaPVC80;
	private static Map<String, Double> areaPVC40;
	private static Map<String, Double> areaHDPE;
	private static Map<String, Double> areaPVCA;
	private static Map<String, Double> areaPVCEB;
	private static Map<String, Map<String, Double>> dimensions;
	private static Message ERROR40	= new Message("Invalid conduit type.", -40);
	private static Message ERROR41	= new Message("Trade size not available for this conduit type.", -41);
	private static Message ERROR42	= new Message("Area of conduit not available.", -42);

	public static Material getConduitMaterialPerIndex(int conduitTypeIndex){
		if(conduitTypeIndex == 0)
			return Material.PVC;
		else if(conduitTypeIndex == 1)
			return Material.ALUMINUM;
		else
			return Material.STEEL;
	}

	public static String[] getTradeSizes() {
		return tradeSizes;
	}

	public static String[] getTypes() {
		return types;
	}

	public static boolean isValidType(String conduitType){
		return dimensions.containsKey(conduitType);
	}

	public static boolean hasArea(String conduitType, String tradeSize){
		return isValidType(conduitType) && dimensions.get(conduitType).containsKey(tradeSize);
	}

	public static double getArea(String conduitType, String tradeSize){
		if(hasArea(conduitType, tradeSize))
			return dimensions.get(conduitType).get(tradeSize);
		return 0;
	}

	static {
		//region conduit types and trade sizes
		tradeSizes = new String[]{"3/8", "1/2", "3/4", "1", "1-1/4", "1-1/2", "2", "2-1/2", "3", "3-1/2", "4", "5", "6"};
		types = new String[]{"EMT", "ENT", "FMC", "IMC", "LFNC-A", "LFNC-B", "LFMC", "RMC", "PVC-80", "PVC-40", "HDPE",
				"PVC-A", "PVC-EB"};
		//endregion
		//region EMT
		areaEMT = new HashMap<>();
		areaEMT.put(tradeSizes[1], 0.304);
		areaEMT.put(tradeSizes[2], 0.533);
		areaEMT.put(tradeSizes[3], 0.864);
		areaEMT.put(tradeSizes[4], 1.496);
		areaEMT.put(tradeSizes[5], 2.036);
		areaEMT.put(tradeSizes[6], 3.356);
		areaEMT.put(tradeSizes[7], 5.858);
		areaEMT.put(tradeSizes[8], 8.846);
		areaEMT.put(tradeSizes[9], 11.545);
		areaEMT.put(tradeSizes[10], 14.753);
		//endregion
		//region ENT
		areaENT = new HashMap<>();
		areaENT.put(tradeSizes[1], 0.285);
		areaENT.put(tradeSizes[2], 0.508);
		areaENT.put(tradeSizes[3], 0.832);
		areaENT.put(tradeSizes[4], 1.453);
		areaENT.put(tradeSizes[5], 1.986);
		areaENT.put(tradeSizes[6], 3.291);
		//endregion
		//region FMT
		areaFMT = new HashMap<>();
		areaFMT.put(tradeSizes[0], 0.116);
		areaFMT.put(tradeSizes[1], 0.317);
		areaFMT.put(tradeSizes[2], 0.533);
		areaFMT.put(tradeSizes[3], 0.817);
		areaFMT.put(tradeSizes[4], 1.277);
		areaFMT.put(tradeSizes[5], 1.858);
		areaFMT.put(tradeSizes[6], 3.269);
		areaFMT.put(tradeSizes[7], 4.909);
		areaFMT.put(tradeSizes[8], 7.069);
		areaFMT.put(tradeSizes[9], 9.621);
		areaFMT.put(tradeSizes[10], 12.566);
		//endregion
		//region IMC
		areaIMC = new HashMap<>();
		areaIMC.put(tradeSizes[1], 0.342);
		areaIMC.put(tradeSizes[2], 0.586);
		areaIMC.put(tradeSizes[3], 0.959);
		areaIMC.put(tradeSizes[4], 1.647);
		areaIMC.put(tradeSizes[5], 2.225);
		areaIMC.put(tradeSizes[6], 3.63);
		areaIMC.put(tradeSizes[7], 5.135);
		areaIMC.put(tradeSizes[8], 7.922);
		areaIMC.put(tradeSizes[9], 10.584);
		areaIMC.put(tradeSizes[10], 13.631);
		//endregion
		//region LFNCA
		areaLFNCA = new HashMap<>();
		areaLFNCA.put(tradeSizes[0], 0.192);
		areaLFNCA.put(tradeSizes[1], 0.312);
		areaLFNCA.put(tradeSizes[2], 0.535);
		areaLFNCA.put(tradeSizes[3], 0.854);
		areaLFNCA.put(tradeSizes[4], 1.502);
		areaLFNCA.put(tradeSizes[5], 2.018);
		areaLFNCA.put(tradeSizes[6], 3.343);
		//endregion
		//region LFNCB
		areaLFNCB = new HashMap<>();
		areaLFNCB.put(tradeSizes[0], 0.192);
		areaLFNCB.put(tradeSizes[1], 0.314);
		areaLFNCB.put(tradeSizes[2], 0.541);
		areaLFNCB.put(tradeSizes[3], 0.873);
		areaLFNCB.put(tradeSizes[4], 1.528);
		areaLFNCB.put(tradeSizes[5], 1.981);
		areaLFNCB.put(tradeSizes[6], 3.246);
		//endregion
		//region LFMC
		areaLFMC = new HashMap<>();
		areaLFMC.put(tradeSizes[0], 0.192);
		areaLFMC.put(tradeSizes[1], 0.314);
		areaLFMC.put(tradeSizes[2], 0.541);
		areaLFMC.put(tradeSizes[3], 0.873);
		areaLFMC.put(tradeSizes[4], 1.528);
		areaLFMC.put(tradeSizes[5], 1.981);
		areaLFMC.put(tradeSizes[6], 3.246);
		areaLFMC.put(tradeSizes[7], 4.881);
		areaLFMC.put(tradeSizes[8], 7.475);
		areaLFMC.put(tradeSizes[9], 9.731);
		areaLFMC.put(tradeSizes[10], 12.692);
		//endregion
		//region RMC
		areaRMC = new HashMap<>();
		areaRMC.put(tradeSizes[1], 0.314);
		areaRMC.put(tradeSizes[2], 0.549);
		areaRMC.put(tradeSizes[3], 0.887);
		areaRMC.put(tradeSizes[4], 1.526);
		areaRMC.put(tradeSizes[5], 2.071);
		areaRMC.put(tradeSizes[6], 3.408);
		areaRMC.put(tradeSizes[7], 4.866);
		areaRMC.put(tradeSizes[8], 7.499);
		areaRMC.put(tradeSizes[9], 10.01);
		areaRMC.put(tradeSizes[10], 12.882);
		areaRMC.put(tradeSizes[11], 20.212);
		areaRMC.put(tradeSizes[12], 29.158);
		//endregion
		//region PVC80
		areaPVC80 = new HashMap<>();
		areaPVC80.put(tradeSizes[1], 0.217);
		areaPVC80.put(tradeSizes[2], 0.409);
		areaPVC80.put(tradeSizes[3], 0.688);
		areaPVC80.put(tradeSizes[4], 1.237);
		areaPVC80.put(tradeSizes[5], 1.711);
		areaPVC80.put(tradeSizes[6], 2.874);
		areaPVC80.put(tradeSizes[7], 4.119);
		areaPVC80.put(tradeSizes[8], 6.442);
		areaPVC80.put(tradeSizes[9], 8.688);
		areaPVC80.put(tradeSizes[10], 11.258);
		areaPVC80.put(tradeSizes[11], 17.855);
		areaPVC80.put(tradeSizes[12], 25.598);
		//endregion
		//region PVC40
		areaPVC40 = new HashMap<>();
		areaPVC40.put(tradeSizes[1], 0.285);
		areaPVC40.put(tradeSizes[2], 0.508);
		areaPVC40.put(tradeSizes[3], 0.832);
		areaPVC40.put(tradeSizes[4], 1.453);
		areaPVC40.put(tradeSizes[5], 1.986);
		areaPVC40.put(tradeSizes[6], 3.291);
		areaPVC40.put(tradeSizes[7], 4.695);
		areaPVC40.put(tradeSizes[8], 7.268);
		areaPVC40.put(tradeSizes[9], 9.737);
		areaPVC40.put(tradeSizes[10], 12.554);
		areaPVC40.put(tradeSizes[11], 19.761);
		areaPVC40.put(tradeSizes[12], 28.567);
		//endregion
		//region HDPE
		areaHDPE = new HashMap<>();
		areaHDPE.put(tradeSizes[1], 0.285);
		areaHDPE.put(tradeSizes[2], 0.508);
		areaHDPE.put(tradeSizes[3], 0.832);
		areaHDPE.put(tradeSizes[4], 1.453);
		areaHDPE.put(tradeSizes[5], 1.986);
		areaHDPE.put(tradeSizes[6], 3.291);
		areaHDPE.put(tradeSizes[7], 4.695);
		areaHDPE.put(tradeSizes[8], 7.268);
		areaHDPE.put(tradeSizes[9], 9.737);
		areaHDPE.put(tradeSizes[10], 12.554);
		areaHDPE.put(tradeSizes[11], 19.761);
		areaHDPE.put(tradeSizes[12], 28.567);
		//endregion
		//region PVCA
		areaPVCA = new HashMap<>();
		areaPVCA.put(tradeSizes[1], 0.385);
		areaPVCA.put(tradeSizes[2], 0.65);
		areaPVCA.put(tradeSizes[3], 1.084);
		areaPVCA.put(tradeSizes[4], 1.767);
		areaPVCA.put(tradeSizes[5], 2.324);
		areaPVCA.put(tradeSizes[6], 3.647);
		areaPVCA.put(tradeSizes[7], 5.453);
		areaPVCA.put(tradeSizes[8], 8.194);
		areaPVCA.put(tradeSizes[9], 10.694);
		areaPVCA.put(tradeSizes[10], 13.723);
		//endregion
		//region PVCEB
		areaPVCEB = new HashMap<>();
		areaPVCEB.put(tradeSizes[6], 3.874);
		areaPVCEB.put(tradeSizes[8], 8.709);
		areaPVCEB.put(tradeSizes[9], 11.365);
		areaPVCEB.put(tradeSizes[10], 14.448);
		areaPVCEB.put(tradeSizes[11], 22.195);
		areaPVCEB.put(tradeSizes[12], 31.53);
		//endregion
		//region dimensions
		dimensions = new HashMap<>();
		dimensions.put(types[0], areaEMT);
		dimensions.put(types[1], areaENT);
		dimensions.put(types[2], areaFMT);
		dimensions.put(types[3], areaIMC);
		dimensions.put(types[4], areaLFNCA);
		dimensions.put(types[5], areaLFNCB);
		dimensions.put(types[6], areaLFMC);
		dimensions.put(types[7], areaRMC);
		dimensions.put(types[8], areaPVC80);
		dimensions.put(types[9], areaPVC40);
		dimensions.put(types[10], areaHDPE);
		dimensions.put(types[11], areaPVCA);
		dimensions.put(types[12], areaPVCEB);
		//endregion
	}
}
