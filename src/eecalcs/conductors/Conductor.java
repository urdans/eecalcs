package eecalcs.conductors;
/*
The class encapsulates only static data and static methods. This class is to be used in composition by the
class Conductor.
Class Circuit should inherit from Conductor
Class Feeder, Branch and Service should inherit from class Circuit
Class Load is for generic loads. Other load types should inherit from class Load and get specialized.

This class groups all the properties related to a conductor of a defined size. The class is able to build and return any conductor with
all these properties (as defined in NEC2014) and also build a conductor object (as defined below) that encapsulates only the
properties that pertain to a particular set of conditions.

Other classes should be defined separately; Composition will be preferred above inheritance, unless abstraction is necessary (which will
probably be the case for the class load and its descendant)

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

Class Circuit:
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

import tools.EEToolsException;

/**
 * Encapsulates the properties and methods for a single conductor as a physical real life object.
 */
public class Conductor {
	//region static
	private static class TempCorrectionFactor{
		private int minTC;
		private int maxTC;
		private int minTF;
		private int maxTF;
		private double correctionFactor60;
		private double correctionFactor75;
		private double correctionFactor90;

		public TempCorrectionFactor(int minTC, int maxTC, int minTF, int maxTF, double correctionFactor60, double correctionFactor75,
		                            double correctionFactor90) {
			this.minTC = minTC;
			this.maxTC = maxTC;
			this.minTF = minTF;
			this.maxTF = maxTF;
			this.correctionFactor60 = correctionFactor60;
			this.correctionFactor75 = correctionFactor75;
			this.correctionFactor90 = correctionFactor90;
		}

		public double getCorrectionFactor(int tempRating){
			if(tempRating == 60) return correctionFactor60;
			if(tempRating == 75) return correctionFactor75;
			if(tempRating == 90) return correctionFactor90;
			return 0;
		}

		public boolean inRangeC(int ambientTempC){
			return ambientTempC >= minTC & ambientTempC <= maxTC;
		}

		public boolean inRangeF(int ambientTempF){
			return ambientTempF >= minTF & ambientTempF <= maxTF;
		}
	}
	private static TempCorrectionFactor[] tempCorrectionFactors;

	private static double getCorrectionFactorC(int ambientTemperatureC, int temperatureRating){
		for(TempCorrectionFactor tcf: tempCorrectionFactors){
			if(tcf.inRangeC(ambientTemperatureC)) return tcf.getCorrectionFactor(temperatureRating);
		}
		return 0;
	}

	private static double getCorrectionFactorF(int ambientTemperatureF, int temperatureRating){
		for(TempCorrectionFactor tcf: tempCorrectionFactors){
			if(tcf.inRangeF(ambientTemperatureF)) return tcf.getCorrectionFactor(temperatureRating);
		}
		return 0;
	}

	static {
		tempCorrectionFactors = new TempCorrectionFactor[]{
				new TempCorrectionFactor(-15, 10, 5, 50, 1.29, 1.2, 1.15),
				new TempCorrectionFactor(11, 15, 51, 59, 1.22, 1.15, 1.12),
				new TempCorrectionFactor(16, 20, 60, 68, 1.15, 1.11, 1.08),
				new TempCorrectionFactor(21, 25, 69, 77, 1.08, 1.05, 1.04),
				new TempCorrectionFactor(26, 30, 78, 86, 1, 1, 1),
				new TempCorrectionFactor(31, 35, 87, 95, 0.91, 0.94, 0.96),
				new TempCorrectionFactor(36, 40, 96, 104, 0.82, 0.88, 0.91),
				new TempCorrectionFactor(41, 45, 105, 113, 0.71, 0.82, 0.87),
				new TempCorrectionFactor(46, 50, 114, 122, 0.58, 0.75, 0.82),
				new TempCorrectionFactor(51, 55, 123, 131, 0.41, 0.67, 0.76),
				new TempCorrectionFactor(56, 60, 132, 140, 0, 0.58, 0.71),
				new TempCorrectionFactor(61, 65, 141, 149, 0, 0.47, 0.65),
				new TempCorrectionFactor(66, 70, 150, 158, 0, 0.33, 0.58),
				new TempCorrectionFactor(71, 75, 159, 167, 0, 0, 0.5),
				new TempCorrectionFactor(76, 80, 168, 176, 0, 0, 0.41),
				new TempCorrectionFactor(81, 85, 177, 185, 0, 0, 0.29),
		};
	}
	//endregion

	protected String size = "12"; //empty means invalid size
	protected Metal metal = Metal.COPPER;
	protected String insulation = "THW"; //empty means invalid insulation
	protected double length = 100;
	protected double ampacity = 0; //size or insulation is invalid
	protected int temperatureRating = 75; //zero means size or insulation is invalid
	protected int ambientTemperatureC = 30;
	protected int ambientTemperatureF = 86;
	protected boolean copperCoated = Coating.UNCOATED;

	/**
	 * Constructs a conductor from the given characteristics
	 * @param size The size of the conductor as defined by {@link Size}
	 * @param metal The conductor metal as defined by {@link Metal}
	 * @param insulation The conductor's insulation type as defined by {@link Insul}
	 * @param length The length of the conductor in feet
	 */
	public Conductor(String size, Metal metal, String insulation, double length) {
		this.size = ConductorProperties.isValidSize(size) ? size : "";
		this.insulation = ConductorProperties.isValidInsulationName(insulation)? insulation: "";
		this.metal = metal;
		this.length = Math.abs(length);
		temperatureRating = ConductorProperties.getInsulationTemperatureCelsius(insulation);
		setAmpacity();
	}

	/**
	 * Constructs a Conductor object as a deep copy of an existing conductor object
	 * @param conductor The existing conductor to be copied.
	 */
	public Conductor(Conductor conductor) {
		this.size = conductor.size;
		this.metal = conductor.metal;
		this.insulation = conductor.insulation;
		this.length = conductor.length;
		this.ampacity = conductor.ampacity;
		this.temperatureRating = conductor.temperatureRating;
		this.ambientTemperatureC = conductor.ambientTemperatureC;
		this.ambientTemperatureF = conductor.ambientTemperatureF;
		this.copperCoated =  conductor.copperCoated;
	}

	/**
	 * Constructs a default conductor object: size 12, copper, insulation type THW and length 100
	 */
	public Conductor(){
		setAmpacity();
	}

	/**
	 * Gets the size of this conductor
	 * @return The size of this conductor
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Sets the size to this conductor
	 * @param size The size of the conductor as defined by {@link Size}
	 */
	public void setSize(String size) {
		this.size = ConductorProperties.isValidSize(size) ? size : "";
		setAmpacity();
	}

	/**
	 * Gets the metal of this conductor
	 * @return The metal of this conductor
	 */
	public Metal getMetal() {
		return metal;
	}

	/**
	 * Sets the metal to this conductor
	 * @param metal The conductor metal as defined by {@link Metal}
	 */
	public void setMetal(Metal metal) {
		this.metal = metal;
		setAmpacity();
	}

	private void setAmpacity(){
		if(!isValid()){
			ampacity = 0;
			return;
		}
		ampacity = ConductorProperties.getAmpacity(size, metal, temperatureRating);
	}

	/**
	 * Gets the insulation type of this conductor
	 * @return The insulation type of this conductor
	 */
	public String getInsulation() {
		return insulation;
	}

	/**
	 * Sets the insulation type to this conductor
	 * @param insulation The conductor's insulation type as defined by {@link Insul}
	 */
	public void setInsulation(String insulation) {
		this.insulation = ConductorProperties.isValidInsulationName(insulation)? insulation: "";
		temperatureRating = ConductorProperties.getInsulationTemperatureCelsius(insulation);
		setAmpacity();
	}

	/**
	 * Gets the length of this conductor
	 * @return The length of this conductor in feet
	 */
	public double getLength() {
		return length;
	}

	/**
	 * Sets the length to this conductor
	 * @param length The length of the conductor in feet
	 */
	public void setLength(double length) {
		this.length = Math.abs(length);
	}

	/**
	 * Gets the ampacity of this conductor under its giving ambient temperature and for its insulation given's temperature rating
	 * @return The ampacity in amperes
	 */
	public double getAmpacity(){
		return ampacity * getCorrectionFactorF(ambientTemperatureF, temperatureRating);
	}

	/**
	 * Indicates if this conductor's size AND insulation name are valid
	 * @return True if valid
	 */
	public boolean isValid(){
		return ConductorProperties.isValidSize(size) & ConductorProperties.isValidInsulationName(insulation);
	}

	/**
	 * Gets the temperature rating of this conductor's insulation
	 * @return The temperature in degrees Celsius
	 */
	public int getTemperatureRating() {
		return temperatureRating;
	}

	/**
	 * Returns the area in square inches, of this insulated conductor (conductor and insulation altogether)
	 * @return The area in square inches
	 */
	public double getInsulatedAreaIn2(){
		return ConductorProperties.getInsulatedAreaIn2(size, insulation);
	}

	/**
	 * Returns the area of this conductor, in Circular Mils
	 * @return The area in circular mils
	 */
	public double getAreaCM(){
		return ConductorProperties.getAreaCM(size);
	}

	/**
	 * Gets the ambient temperature of this conductor
	 * @return The ambient temperature in degrees Celsius
	 */
	public int getAmbientTemperatureC() {
		return ambientTemperatureC;
	}

	/**
	 * Sets the ambient temperature to this conductor
	 * @param ambientTemperatureC The ambient temperature in degrees Celsius
	 */
	public void setAmbientTemperatureC(int ambientTemperatureC) {
		this.ambientTemperatureC = ambientTemperatureC;
		this.ambientTemperatureF = (int)Math.floor(ambientTemperatureC * 1.8 + 32);
	}

	/**
	 * Gets the ambient temperature of this conductor
	 * @return The ambient temperature in degrees Fahrenheits
	 */
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}

	/**
	 * Sets the ambient temperature to this conductor
	 * @param ambientTemperatureF The ambient temperature in degrees Fahrenheits
	 */
	public void setAmbientTemperatureF(int ambientTemperatureF) {
		this.ambientTemperatureF = ambientTemperatureF;
		this.ambientTemperatureC = (int)Math.ceil((ambientTemperatureF - 32) * 5/9);
	}

	/**
	 * Indicates if this copper conductor is coated.
	 * Notice that this property has no meaning when this conductor metal is aluminum.
	 * @return True if coated, false otherwise
	 */
	public boolean isCopperCoated(){
		if(metal == Metal.ALUMINUM)
			return false;
		return copperCoated;
	}

	/**
	 * Sets the coating to this copper conductor.
	 * Setting this property does not have any effect if this conductor metal is aluminum.
	 * @param copperCoated Indicates if the conductor is coated
	 */
	public void setCopperCoated(boolean copperCoated) {
		this.copperCoated = copperCoated;
	}
}
