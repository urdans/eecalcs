package eecalcs.conductors;

import eecalcs.conduits.Material;
import eecalcs.systems.TempRating;

import java.util.HashMap;
import java.util.Map;

import static tools.Tools.getArrayIndexOf;
import static tools.Tools.stringArrayContains;

/*
 * This class encapsulates only static data and methods about properties of conductors as defined in NEC 2014 tables 310.15(B)(16), 8, 9,
 * 5 & 5A.
 *
 *
 *  This class is to be used in composition by the
 * class Conductor.
 * Class Circuit should inherit from Conductor
 * Class Feeder, Branch and Service should inherit from class Circuit
 * Class Load is for generic loads. Other load types should inherit from class Load and get specialized.
 *
 * This class groups all the properties related to a conductor of a defined size. The class is able to build and return any conductor with
 * all these properties (as defined in NEC2014) and also build a conductor object (as defined below) that encapsulates only the
 * properties that pertain to a particular set of conditions.
 *
 * Other classes should be defined separately; Composition will be preferred above inheritance, unless abstraction is necessary (which will
 * probably be the case for the class load and its descendant)
 *
 * A conductor is an entity that encapsulates all the properties of a single conductor, when it is isolated, that is, the
 * represented characteristics don't depend upon other conditions, like ambient temperature, number of conductor per raceway, type of
 * raceway, voltage type (AC or DC), number of phases, special locations, load types, etc.
 *
 * Class Conductor:
 * ----------------
 * The independent properties of a conductor are:
 * -size
 * -metal (CU or AL)
 * -insulation (if any)
 * -length (one way length)
 *
 * Class Circuit:
 * -----------------------
 * The resistance and reactance of the conductor will depend on the raceway type and arrangement:
 * -If conductors are in free air or tray
 * -If they are inside a conduit and then, the metal of the conduit
 * -The number of conductors inside the raceway that are not in parallel (this should affect the resistance and the reactance of the
 * conductor but I haven't found a formulae or method that correlates these characteristics)
 * -The number of conductors inside the conduit that are in parallel (same note as before; table 9 is based on the assumption the system
 * voltage is three phase, 75°C, 60Hz, three single conductors in conduit; so, unless more information is found, I will always use the
 * values of table 9 but will leave room for improvement once the method that considers different scenarios is found).
 *
 * -The ampacity of the conductor will depend mainly on all the above listed variables but also on the location of the conductor, like when
 * it is in the rooftop (and the distance from the floor)
 *
 *
 * Class Feeder, Service, Branch and Tap:
 * --------------------------------------
 * -These classes are similar. They differ in the fact that the branch circuit directly feeds a load, while a feeder has a OCPD on each end.
 * A special Feeder is the Service class.
 * SOme of the properties of these classes are:
 * -Voltage
 * -Phases,
 * -Frequency
 *
 * The user must put the class CircuitConductor in the context of any of the classes Feeder, Service or Branch.
 *
 * For instance, the Branch class has a load object. The Feeder has a load intent. One or more branch circuits will always be connected to a
 * feeder through an OCPD.
 *
 * Branch circuits can be multiwire
 * Loads can be continuous or non continuous.
 *
 * Other classes must be designed, like for fuses, breakers, loads, motors, appliances, whatever, lights, AC equipment, panel, switchboard,
 * etc, etc.
 */

/**
 *  This class encapsulates static data and methods about properties of conductors as defined in NEC 2014 tables 310.15(B)(16), 8,
 *  9, 5 and 5A.
 */
public class ConductorProperties {
	private static class Properties{
		public String size;
		public int areaCM;
		public double nonMagXL;
		public double magXL;
		public int CuAmp60;
		public int CuAmp75;
		public int CuAmp90;
		public int AlAmp60;
		public int AlAmp75;
		public int AlAmp90;
		public double CuResInPVCCond;
		public double CuResInALCond;
		public double CuResInSteelCond;
		public double ALResInPVCCond;
		public double ALResInALCond;
		public double ALResInSteelCond;
		public double CuResDCUncoated;
		public double CuResDCCoated;
		public double ALResDC;

		public Properties(String size, int cuAmp60, int cuAmp75, int cuAmp90, int alAmp60, int alAmp75, int alAmp90, double nonMagXL,
		                  double magXL, double cuResInPVCCond, double cuResInALCond, double cuResInSteelCond, double aLResInPVCCond,
		                  double aLResInALCond, double aLResInSteelCond, int areaCM, double cuResDCUncoated, double cuResDCCoated,
		                  double aLResDC) {
			this.size = size;
			this.areaCM = areaCM;
			this.nonMagXL = nonMagXL;
			this.magXL = magXL;
			this.CuAmp60 = cuAmp60;
			this.CuAmp75 = cuAmp75;
			this.CuAmp90 = cuAmp90;
			this.AlAmp60 = alAmp60;
			this.AlAmp75 = alAmp75;
			this.AlAmp90 = alAmp90;
			this.CuResInPVCCond = cuResInPVCCond;
			this.CuResInALCond = cuResInALCond;
			this.CuResInSteelCond = cuResInSteelCond;
			this.ALResInPVCCond = aLResInPVCCond;
			this.ALResInALCond = aLResInALCond;
			this.ALResInSteelCond = aLResInSteelCond;
			this.CuResDCUncoated = cuResDCUncoated;
			this.CuResDCCoated = cuResDCCoated;
			this.ALResDC = aLResDC;
		}
	}
	//region static members
	private static String[] sizes;
	private static String[] sizeFullName;
	private static Properties[] table;
	private static Properties invalidPropertySet;
	//region insulation
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
	//endregion

	private ConductorProperties(){}

	//region non conductor table property methods
	/**
	 * Returns a {@link PropertySet} object for the given conductor size. If the size of the conductor is not valid, an
	 * invalidPropertySet object is returned.
	 * @param conductorSize The size of the conductor
	 * @return A {@link PropertySet} object.
	 * @see #getInvalidPropertySet()
	 */
	public static Properties bySize(String conductorSize){
		for (int i = 0; i < table.length; i++)
			if (table[i].size.equals(conductorSize)) return table[i];
		return invalidPropertySet;
	}

	/**
	 * Returns the area in square inches of an insulated conductor (conductor and insulation altogether) of size conductorSize and of
	 * insulation insulationName.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return The area of the insulated conductor or zero if any of the parameter is invalid.
	 */
	protected static double getInsulatedAreaIn2(String conductorSize, String insulationName){
		if(hasInsulatedArea(conductorSize, insulationName))
			return insulatedDimensions.get(insulationName).get(conductorSize);
		return 0;
	}

	/**
	 * Returns the area in square inches of a compact conductor (Table 5A) of size conductorSize
	 * and of insulation insulationName
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return The area of the compact conductor or zero if any of the parameter is invalid or the area is not defined in table 5.
	 */
	protected static double getCompactAreaIn2(String conductorSize, String insulationName){
		if(hasCompactArea(conductorSize, insulationName))
			return compactDimensions.get(insulationName).get(conductorSize);
		return 0;
	}

	/**
	 * Returns the area in square inches of bare compact conductor (Table 5A) of size conductorSize.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @return The area of the bare compact conductor or zero if the size is invalid or the area is not defined in table 5A.
	 */
	protected static double getCompactBareAreaIn2(String conductorSize){
		if(hasCompactBareArea(conductorSize))
			return compactBareDimensions.get(conductorSize);
		return 0;
	}

	/**
	 * Returns true if an insulated conductor of size conductorSize and insulation type insulationName has its area defined in table 5
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return True if the area is defined in table 5, false otherwise or parameters are not valid
	 */
	protected static boolean hasInsulatedArea(String conductorSize, String insulationName){
		return isValidSize(conductorSize) && isValidInsulationName(insulationName) && insulatedDimensions.get(insulationName).containsKey(conductorSize);
	}

	/**
	 * Returns true if a compact conductor of size conductorSize and insulation type insulationName has its area defined in table 5A
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return True if the area is defined in table 5A, false otherwise or parameters are not valid.
	 */
	protected static boolean hasCompactArea(String conductorSize, String insulationName){
		return isValidSize(conductorSize) && isValidInsulationName(insulationName) && compactDimensions.containsKey(insulationName)
				&& compactDimensions.get(insulationName).containsKey(conductorSize);
	}

	/**
	 * Returns true if a compact bare conductor of size conductorSize has its area defined in table 5A
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @return True if the area is defined in table 5A, false otherwise or parameter is not valid
	 */
	protected static boolean hasCompactBareArea(String conductorSize){
		return isValidSize(conductorSize) && compactBareDimensions.containsKey(conductorSize);
	}

	/**
	 * Asks if the given insulation is rated for 60 degrees Celsius
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return True if rated for 60 degrees Celsius, false otherwise
	 */
	public static boolean insulationIs60Celsius(String insulationName){
		return stringArrayContains(insulation60Celsius, insulationName);
	}

	/**
	 * Asks if the given insulation is rated for 75 degrees Celsius
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return True if rated for 75 degrees Celsius, false otherwise
	 */
	public static boolean insulationIs75Celsius(String insulationName){
		return stringArrayContains(insulation75Celsius, insulationName);
	}

	/**
	 * Asks if the given insulation is rated for 90 degrees Celsius
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return True if rated for 90 degrees Celsius, false otherwise
	 */
	public static boolean insulationIs90Celsius(String insulationName){
		return stringArrayContains(insulation90Celsius, insulationName);
	}

	/**
	 * Asks for the temperature rating of the given insulation.
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return The temperature rating of the insulation in degrees Celsius (60, 75 or 90) or zero if the insulation is not valid.
	 */
	public static int getInsulationTemperatureCelsius(String insulationName){
		if(insulationIs60Celsius(insulationName)) return 60;
		else if(insulationIs75Celsius(insulationName)) return 75;
		else if(insulationIs90Celsius(insulationName)) return 90;
		return 0;
	}

	/**
	 * Returns the index of the conductor size in the internal table of sizes.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @return The index of the conductor size in the internal table of sizes or -1 if the size is not valid
	 */
	private static int getIndexOfSize(String conductorSize) {
		return getArrayIndexOf(sizes, conductorSize);
	}

	/**
	 * Returns a {@link Metal} enum member corresponding to the conductorTypeIndex.
	 * @param conductorTypeIndex An integer value (0 or 1)
	 * @return Metal.COPPER for a conductorTypeIndex is zero, and Metal.ALUMINUM otherwise
	 */
	public static Metal getMetalPerIndex(int conductorTypeIndex){
		if(conductorTypeIndex == 0)
			return Metal.COPPER;
		return Metal.ALUMINUM;
	}

	/**
	 * Asks if the given conductor size is valid
	 * @param size The size of the conductor as defined by {@link Size}
	 * @return True if the size is valid, false otherwise.
	 */
	public static boolean isValidSize(String size) {
		return getIndexOfSize(size) != -1;
	}

	/**
	 * Asks if the given insulation name is valid
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return True if the insulation is valid, false otherwise.
	 */
	public static boolean isValidInsulationName(String insulationName) {
		return (getArrayIndexOf(insulation60Celsius, insulationName) != -1)
				|| (getArrayIndexOf(insulation75Celsius, insulationName) != -1)
				|| (getArrayIndexOf(insulation90Celsius, insulationName) != -1);
	}

	/**
	 * Compares two conductor sizes. This methods does not check for validity of the conductor sizes. If one of the conductor is invalid
	 * the result will not make any sense.
	 * @param sizeLeft The size of the left side conductor to be compared as defined by {@link Size}
	 * @param sizeRight The size of the right side conductor to be compared  as defined by {@link Size}
	 * @return -1 if sizeLeft is smaller than sizeRight, 0 if both are the same size of 1 if sizeLeft is bigger than sizeRight
	 */
	public static int compareSizes(String sizeLeft, String sizeRight){
		int left = getIndexOfSize(sizeLeft);
		int right = getIndexOfSize(sizeRight);
		return Integer.compare(left, right);
	}

	/**
	 * @return Return a table containing all the conductor sizes as defined defined by {@link Size}
	 */
	public static String[] getSizes() {
		return sizes;
	}

	/**
	 * Returns the full size name of the given conductor size.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @return The full size name including the prefix for AWG or KCMIL. If the size is not valid an empty string is returned.
	 */
	protected static String getFullSizeName(String conductorSize) {
		if(isValidSize(conductorSize))
			return sizeFullName[getArrayIndexOf(sizes, conductorSize)];
		return "";
	}

	/**
	 * Returns the invalidPropertySet object.
	 * An invalidPropertySet object is a particular PropertySet object that contains only zeroes for all the properties of the a
	 * conductor. The size of the conductor returned by an invalidPropertySet object is "Not assigned!".
	 * @return The invalidPropertySet object.
	 * @see PropertySet
	 */
	public static Properties getInvalidPropertySet() {
		return invalidPropertySet;
	}
	//endregion
/******************************************************************************************************************************************/
	//region conductor property specific methods
	/**
	 * Returns the reactance property of this conductor under the given magnetic conduit condition
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param magneticConduit Indicates if the conduit is magnetic or not
	 * @return The reactance of this conductor in ohms per 1000 feet
	 */
	public double getReactance(String conductorSize, boolean magneticConduit){
		if(magneticConduit) return bySize(conductorSize).magXL;
		return bySize(conductorSize).nonMagXL;
	}

	/**
	 * Returns the total reactance of this conductor under the given magnetic conduit condition, for the given length and number of
	 * parallel conductors
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param magneticConduit Indicates if the conduit is magnetic or not
	 * @param oneWayLength The length in feet of this conductor
	 * @param numberOfSets The number of conductors in parallel
	 * @return The total reactance under the specified conditions
	 */
	public double getReactance(String conductorSize, boolean magneticConduit, double oneWayLength, int numberOfSets){
		return getReactance(conductorSize, magneticConduit) * 0.001 * oneWayLength / numberOfSets;
	}

	/**
	 * Returns the area property, in Circular Mils, of this conductor
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @return Returns the area in Circular Mils of this conductor
	 */
	public int getAreaCM(String conductorSize) {
		return bySize(conductorSize).areaCM;
	}

	/**
	 * Returns the DC resistance of this conductor size for the given metal. If the specified metal is aluminum, the
	 * copperCoated parameter is ignored.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param metal The metal of the conductor.
	 * @param copperCoated Indicates for a copper conductor if it is coated or not.
	 * @return The DC resistance of this conductor in ohms per 1000 feet.
	 */
	public double getDCResistance(String conductorSize, Metal metal, boolean copperCoated) {
		if(metal == Metal.COPPER) {
			if (copperCoated)
				return bySize(conductorSize).CuResDCCoated;
			return bySize(conductorSize).CuResDCUncoated;
		}
		return bySize(conductorSize).ALResDC;
	}

	/**
	 * Returns the DC resistance of this conductor size for the given metal, length, sets, etc. If the specified metal is aluminum, the
	 * copperCoated parameter is ignored.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param metal The metal of the conductor.
	 * @param length The length of the conductor.
	 * @param numberOfSets The number of conductors in parallel.
	 * @param copperCoated Indicates for a copper conductor if it is coated or not.
	 * @return The DC resistance in ohms of this conductor size under the given conditions.
	 */
	public double getDCResistance(String conductorSize, Metal metal, double length, int numberOfSets, boolean copperCoated) {
		return getDCResistance(conductorSize, metal, copperCoated) * 0.001 * length / numberOfSets;
	}

	/**
	 * Returns the AC resistance of this conductor size for the given metal and conduit material.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param metal The metal of the conductor.
	 * @param conduitMaterial The material type of the conduit as specified in {@link Material}.
	 * @return The AC resistance in ohms per 1000 feet.
	 */
	public double getACResistance(String conductorSize, Metal metal, Material conduitMaterial) {
		if(metal == Metal.COPPER) {
			if(conduitMaterial == Material.PVC)
				return bySize(conductorSize).CuResInPVCCond;
			else if(conduitMaterial == Material.ALUMINUM)
				return bySize(conductorSize).CuResInALCond;
			else
				return bySize(conductorSize).CuResInSteelCond;
		}
		else{
			if(conduitMaterial == Material.PVC)
				return bySize(conductorSize).ALResInPVCCond;
			else if(conduitMaterial == Material.ALUMINUM)
				return bySize(conductorSize).ALResInALCond;
			else
				return bySize(conductorSize).ALResInSteelCond;
		}
	}

	/**
	 * Returns the AC resistance of this conductor size for the given metal, conduit material, length and number of sets.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param metal The metal of the conductor.
	 * @param conduitMaterial The material type of the conduit as specified in {@link Material}.
	 * @param length The length of the conductor in feet.
	 * @param numberOfSets The number of sets (conductors in parallel per phase)
	 * @return The AC resistance in ohms of this conductor size under the given conditions.
	 */
	public double getACResistance(String conductorSize, Metal metal, Material conduitMaterial, double length, int numberOfSets) {
		return getACResistance(conductorSize, metal, conduitMaterial) * 0.001 * length / numberOfSets;
	}

	/**
	 * Returns the ampacity of this conductor size for the given metal and temperature rating.
	 * @param conductorSize The size of the conductor as defined by {@link Size}
	 * @param metal The metal of the conductor as defined in {@link Metal}.
	 * @param temperatureRating The temperature rating as defined in {@link eecalcs.systems.TempRating}
	 * @return The ampacity of this conductor size in amperes.
	 */
	public double getAmpacity(String conductorSize, Metal metal, int temperatureRating) {
		if(metal == Metal.COPPER) {
			if(temperatureRating == TempRating.T60)
				return bySize(conductorSize).CuAmp60;
			else if(temperatureRating == TempRating.T75)
				return bySize(conductorSize).CuAmp75;
			else if(temperatureRating == TempRating.T90)
				return bySize(conductorSize).CuAmp90;
		}
		else{
			if(temperatureRating == TempRating.T60)
				return bySize(conductorSize).AlAmp60;
			else if(temperatureRating == TempRating.T75)
				return bySize(conductorSize).AlAmp75;
			else if(temperatureRating == TempRating.T90)
				return bySize(conductorSize).AlAmp90;
		}
		return 0;
	}

	//endregion

	static {
		//region conductor sizes and full size names
		sizes = new String[]{Size.S14, Size.S12, Size.S10, Size.S8, Size.S6, Size.S4, Size.S3, Size.S2, Size.S1, Size.Z1, Size.Z2,
				Size.Z3, Size.Z4, Size.K250, Size.K300, Size.K350, Size.K400, Size.K500, Size.K600, Size.K700, Size.K750, Size.K800,
				Size.K900, Size.K1000, Size.K1250, Size.K1500, Size.K1750, Size.K2000};
		sizeFullName = new String[]{"AWG 14", "AWG 12", "AWG 10", "AWG 8", "AWG 6", "AWG 4", "AWG 3", "AWG 2", "AWG 1",
				"AWG 1/0", "AWG 2/0", "AWG 3/0", "AWG 4/0", "250 KCMIL", "300 KCMIL", "350 KCMIL", "400 KCMIL", "500 KCMIL", "600 KCMIL",
				"700 KCMIL", "750 KCMIL", "800 KCMIL", "900 KCMIL", "1000 KCMIL", "1250 KCMIL", "1500 KCMIL", "1750 KCMIL", "2000 KCMIL"};
		//endregion
		//region table of conductors' properties
		table = new Properties[]{
				new Properties(sizes[0],   15,  20,  25,   0,   0,   0, 0.058000, 0.073000, 3.100000, 3.100000,	3.100000, 4.130600,4.130600, 4.130600,    4110, 3.070000, 3.190000, 5.060000),
				new Properties(sizes[1],   20,  25,  30,  15,  20,  25, 0.054000, 0.068000, 2.000000, 2.000000,	2.000000, 3.200000,3.200000, 3.200000,    6530, 1.930000, 2.010000, 3.180000),
				new Properties(sizes[2],   30,  35,  40,  25,  30,  35, 0.054000, 0.068000, 1.200000, 1.200000,	1.200000, 2.000000,2.000000, 2.000000,   10380, 1.210000, 1.260000, 2.000000),
				new Properties(sizes[3],   40,  50,  55,  35,  40,  45, 0.050000, 0.063000, 0.780000, 0.780000,	0.780000, 1.300000,1.300000, 1.300000,   16510, 0.764000, 0.786000, 1.260000),
				new Properties(sizes[4],   55,  65,  75,  40,  50,  55, 0.051000, 0.064000, 0.490000, 0.490000,	0.490000, 0.810000,0.810000, 0.810000,   26240, 0.491000, 0.510000, 0.808000),
				new Properties(sizes[5],   70,  85,  95,  55,  65,  75, 0.048000, 0.060000, 0.310000, 0.310000,	0.310000, 0.510000,0.510000, 0.510000,   41740, 0.308000, 0.321000, 0.508000),
				new Properties(sizes[6],   85, 100, 115,  65,  75,  85, 0.047000, 0.059000, 0.250000, 0.250000,	0.250000, 0.400000,0.410000, 0.400000,   52620, 0.245000, 0.254000, 0.403000),
				new Properties(sizes[7],   95, 115, 130,  75,  90, 100, 0.045000, 0.057000, 0.190000, 0.200000,	0.200000, 0.320000,0.320000, 0.320000,   66360, 0.194000, 0.201000, 0.319000),
				new Properties(sizes[8],  110, 130, 145,  85, 100, 115, 0.046000, 0.057000, 0.150000, 0.160000,	0.160000, 0.250000,0.260000, 0.250000,   83690, 0.154000, 0.160000, 0.253000),
				new Properties(sizes[9],  125, 150, 170, 100, 120, 135, 0.044000, 0.055000, 0.120000, 0.130000,	0.120000, 0.200000,0.210000, 0.200000,  105600, 0.122000, 0.127000, 0.201000),
				new Properties(sizes[10], 145, 175, 195, 115, 135, 150, 0.043000, 0.054000, 0.100000, 0.100000,	0.100000, 0.160000,0.160000, 0.160000,  133100, 0.096700, 0.101000, 0.159000),
				new Properties(sizes[11], 165, 200, 225, 130, 155, 175, 0.042000, 0.052000, 0.077000, 0.082000,	0.079000, 0.130000,0.130000, 0.130000,  167800, 0.076600, 0.079700, 0.126000),
				new Properties(sizes[12], 195, 230, 260, 150, 180, 205, 0.041000, 0.051000, 0.062000, 0.067000,	0.063000, 0.100000,0.110000, 0.100000,  211600, 0.060800, 0.062600, 0.100000),
				new Properties(sizes[13], 215, 255, 290, 170, 205, 230, 0.041000, 0.052000, 0.052000, 0.057000,	0.054000, 0.085000,0.090000, 0.086000,  250000, 0.051500, 0.053500, 0.084700),
				new Properties(sizes[14], 240, 285, 320, 195, 230, 260, 0.041000, 0.051000, 0.044000, 0.049000,	0.045000, 0.071000,0.076000, 0.072000,  300000, 0.042900, 0.044600, 0.070700),
				new Properties(sizes[15], 260, 310, 350, 210, 250, 280, 0.040000, 0.050000, 0.038000, 0.043000,	0.039000, 0.061000,0.066000, 0.063000,  350000, 0.036700, 0.038200, 0.060500),
				new Properties(sizes[16], 280, 335, 380, 225, 270, 305, 0.040000, 0.049000, 0.033000, 0.038000,	0.035000, 0.054000,0.059000, 0.055000,  400000, 0.032100, 0.033100, 0.052900),
				new Properties(sizes[17], 320, 380, 430, 260, 310, 350, 0.039000, 0.048000, 0.027000, 0.032000,	0.029000, 0.043000,0.048000, 0.045000,  500000, 0.025800, 0.026500, 0.042400),
				new Properties(sizes[18], 350, 420, 475, 285, 340, 385, 0.039000, 0.048000, 0.023000, 0.028000,	0.025000, 0.036000,0.041000, 0.038000,  600000, 0.021400, 0.022300, 0.035300),
				new Properties(sizes[19], 385, 460, 520, 315, 375, 425, 0.038500, 0.048000, 0.021000, 0.026000,	0.021900, 0.032500,0.038000, 0.033700,  700000, 0.018400, 0.018900, 0.030300),
				new Properties(sizes[20], 400, 475, 535, 320, 385, 435, 0.038000, 0.048000, 0.019000, 0.024000,	0.021000, 0.029000,0.034000, 0.031000,  750000, 0.017100, 0.017600, 0.028200),
				new Properties(sizes[21], 410, 490, 555, 330, 395, 445, 0.037800, 0.047600, 0.018200, 0.023000,	0.020400, 0.027800,0.032600, 0.029800,  800000, 0.016100, 0.016600, 0.026500),
				new Properties(sizes[22], 435, 520, 585, 355, 425, 480, 0.037400, 0.046800, 0.016600, 0.021000,	0.019200, 0.025400,0.029800, 0.027400,  900000, 0.014300, 0.014700, 0.023500),
				new Properties(sizes[23], 455, 545, 615, 375, 445, 500, 0.037000, 0.046000, 0.015000, 0.019000,	0.018000, 0.023000,0.027000, 0.025000, 1000000, 0.012900, 0.013200, 0.021200),
				new Properties(sizes[24], 495, 590, 665, 405, 485, 545, 0.036000, 0.046000, 0.011351, 0.014523,	0.014523, 0.017700,0.023436, 0.021600, 1250000, 0.010300, 0.010600, 0.016900),
				new Properties(sizes[25], 525, 625, 705, 435, 520, 585, 0.035000, 0.045000, 0.009798, 0.013127,	0.013127, 0.015000,0.020941, 0.019300, 1500000, 0.008580, 0.008830, 0.014100),
				new Properties(sizes[26], 545, 650, 735, 455, 545, 615, 0.034000, 0.045000, 0.008710, 0.012275,	0.012275, 0.013100,0.019205, 0.017700, 1750000, 0.007350, 0.007560, 0.012100),
				new Properties(sizes[27], 555, 665, 750, 470, 560, 630, 0.034000, 0.044000, 0.007928, 0.011703,	0.011703, 0.011700,0.018011, 0.016600, 2000000, 0.006430, 0.006620, 0.010600),
		};
		invalidPropertySet = new Properties("Not assigned!", 0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0);
		//endregion
		//region temperature of insulators
		// XHHW & THHW are duplicated in 75 and 90 degrees columns. It is assumed both are 90 by definition of their double Hs
		insulation60Celsius = new String[]{Insul.TW};
		insulation75Celsius = new String[]{Insul.RHW, Insul.THW, Insul.THWN, Insul.USE, Insul.ZW};
		insulation90Celsius = new String[]{Insul.TBS, Insul.SA, Insul.SIS, Insul.FEP, Insul.FEPB, Insul.MI, Insul.RHH, Insul.RHW2,
				Insul.THHN, Insul.THHW, Insul.THW2, Insul.THWN2, Insul.USE2, Insul.XHH, Insul.XHHW, Insul.XHHW2, Insul.ZW2};
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


}
/*RELEASE NOTES
As a general rule, when a class computes things whose results are predictable, the class should not raise any exceptions nor manage
any error messages. Simply, it must return empty string values, or singular int or double numbers, or null, but also should provide the
caller with helper methods for validation of the input data, like validating that a conductor size is correct before calling any function
that uses size as parameter. However, the validating helper methods are not necessary since the computed return value of the methods can
indicated that some input was wrong or simply the value is not listed for the input variables.
*/