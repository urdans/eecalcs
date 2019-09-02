package eecalcs.voltagedrop;

import eecalcs.conductors.*;
import eecalcs.conduits.Magnetic;
import org.apache.commons.math3.complex.Complex;
import tools.Message;
import tools.ResultMessages;

/**
 * Provides methods for calculation of the voltage drop across circuits and for calculation of the maximum length of a circuit for a
 * given maximum voltage drop.
 */
public class VDrop {
	//region private fields
	//default values of input variables
	private double sourceVoltage = 120;
	private int phases = 1;
	private Circuit circuit;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private double maxVoltageDropPercent = 3; //for AC and DC
	private static Message ERROR01	= new Message("Source voltage must be greater that zero.",-1);
	private static Message ERROR02	= new Message("Number of phases must be 1 or 3.",-2);
	private static Message ERROR03	= new Message("Invalid conductor size.",-3);
	private static Message ERROR04	= new Message("Number of sets must be between 1 and 10.",-4);
	private static Message ERROR05	= new Message("One way conductor length must be greater than 0.",-5);
	private static Message ERROR06	= new Message("Load current must be greater than 0.",-6);
	private static Message ERROR07	= new Message("Power factor must be between 0.7 and 1.",-7);
	private static Message ERROR08	= new Message("Voltage drop for determining conductor sizing must be between 0.5% and 25%",-8);
	private static Message ERROR20	= new Message("Load current exceeds maximum allowed conductor set's ampacity at 90°C. NEC-310.15(B)" +
			"(16)",-20);
	private static Message ERROR21	= new Message("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310" +
			".10(H)(1)",-21);
	private static Message ERROR30	= new Message("No length can achieve that voltage drop under the given conditions.", -30);
	private static Message ERROR31	= new Message("No building conductor can achieve that voltage drop under the given conditions.",
			-31);
	private static Message WARNN21	= new Message(ERROR21.message,21);
	//calculated
	private double actualVoltageDropPercentageAC;
	private double maxLengthAC;
	private double actualVoltageDropPercentageDC;
	private double maxLengthDC;
	//endregion

	//region constructor
	/**
	 * Constructs a VDrop object for a given circuit object. The result object will contain a deep copy of the given circuit object.
	 * @param circuit The existing circuit object.
	 */
	public VDrop(Circuit circuit){
		this.circuit = new Circuit(circuit);
	}
	//endregion

	//region public fields
	/**
	 * Container for messages resulting from calculations performed by this class.
	 * @see ResultMessages
	 */
	public ResultMessages resultMessages = new ResultMessages();
	//endregion

	//region setters
	/**
	 * Sets the value of the voltage at the source of the circuit to this VDrop object.
	 * @param sourceVoltage The voltage at the source in volts. Default value is 120.
	 */
	public void setSourceVoltage(double sourceVoltage) {
		this.sourceVoltage = sourceVoltage;
	}

	/**
	 * Sets the number of phases of the source voltage.
	 * @param phases The number of phases. Notice there is no validation for this input. Default value is 1.
	 */
	public void setPhases(int phases) {
		this.phases = phases;
	}

	/**
	 * Sets the circuit connected load's current.
	 * @param loadCurrent The current of the load in amperes. Default value is 10.
	 * Notice that no validation is performed while setting this value, so the user must check for the presence of errors or
	 * warnings after setting any value or obtaining a calculation result.
	 */
	public void setLoadCurrent(double loadCurrent) {
		this.loadCurrent = loadCurrent;
	}

	/**
	 * Sets the power factor of the circuit connected load.
	 * @param powerFactor The power factor of the load. Should be a number between 0.7 and 1.0 inclusive. Default value is 1.
	 * Notice that no validation is performed while setting this value, so the user must check for the presence of errors or
	 * warnings after setting any value or obtaining a calculation result.
	 */
	public void setPowerFactor(double powerFactor) {
		this.powerFactor = powerFactor;
	}

	/**
	 * Sets the maximum allowed voltage drop. This value is used to compute the maximum length of the circuit that would have a voltage
	 * drop less or equal to the specified value.
	 * @param maxVoltageDropPercent The maximum voltage drop in percentage. The default value is 3.
	 */
	public void setMaxVoltageDropPercent(double maxVoltageDropPercent) {
		this.maxVoltageDropPercent = maxVoltageDropPercent;
	}
	//endregion

	//region getters
	/**
	 * Gets the voltage value of the source feeding the circuit.
	 * @return The voltage value at the source
	 */
	public double getSourceVoltage() {
		return sourceVoltage;
	}

	/**
	 * Gets the number of phases of the voltage.
	 * @return The number of phases of the voltage
	 */
	public int getPhases() {
		return phases;
	}

	/**
	 * Returns the circuit connected load's current.
	 * @return The circuit connected load's current.
	 */
	public double getLoadCurrent() {
		return loadCurrent;
	}

	/**
	 * Returns the power factor of the circuit connected load.
	 * @return The power factor of the circuit connected load.
	 */
	public double getPowerFactor() {
		return powerFactor;
	}

	/**
	 * Returns the maximum allowed voltage drop.
	 * @return Tthe maximum allowed voltage drop.
	 */
	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}

	/**
	 * Returns the internal circuit object maintained by this VDrop object. You can get access to this object to set or get its properties.
	 * @return This object's internal circuit object.
	 */
	public Circuit getCircuit() {
		return circuit;
	}
	//endregion input fields

	//region AC voltage drop output

	/**
	 * Returns the calculated AC voltage at the load terminals. If the returned value is zero, check this object's {@link ResultMessages}
	 * object for errors.
	 * Notice that even if there are no errors, there could be warnings. The user should always check the ResultMessages object for
	 * errors and warnings.
	 * @return The calculated AC voltage at the load terminals, in volts.
	 * @see tools.ResultMessages
	 * @see #resultMessages
	 */
	public double getVoltageAtLoadAC() {
		if(checkACVDInput())
			return computeVoltageAtLoadAC(circuit.getSize());
		return 0;
	}

	/**
	 * Returns the calculated AC voltage drop. This voltage is obtained following the following method:
	 * The voltage V at the source is assumed to be at zero degrees (the reference angle).
	 * The Impedance Z of the conductors is calculated based on the given conditions (size, metal, conduit type, length, etc).
	 * The current I phasor modulus is the connected load's current. The phasor angle is determined by the power factor of the load.
	 * The complex voltage Vw (drop) across the circuit is calculated as Vw = Z x I
	 * The voltage drop returned is |V|-|Vw|.
	 * @return The calculated AC voltage drop in volts.
	 */
	public double getVoltageDropVoltsAC() {
		if(checkACVDInput())
			return sourceVoltage - getVoltageAtLoadAC();
		return 0;
	}

	/**
	 * Returns the calculated AC voltage drop in percentage.
	 * The percentage is in reference to the voltage at the source.
	 * @return The calculated AC voltage drop in percentage.
	 */
	public double getVoltageDropPercentageAC() {
		if(checkACVDInput())
			return 100 * getVoltageDropVoltsAC() / sourceVoltage;
		return 0;
	}
	//endregion AC voltage drop output

	//region DC voltage drop output
	/**
	 * Returns the calculated DC voltage at the load terminals. If the returned value is zero check this object's {@link ResultMessages}
	 * object for errors.
	 * Notice that even if there are no errors, there could be warnings. The user should always check the ResultMessages object for
	 * errors and warnings.
	 * @return The calculated DC voltage at the load terminals, in volts.
	 * @see tools.ResultMessages
	 * @see #resultMessages
	 */
	public double getVoltageAtLoadDC() {
		if(checkDCVDInput())
			return computeVoltageAtLoadDC(circuit.getSize());
		return 0;
	}

	/**
	 * Returns the calculated DC voltage drop. This voltage is obtained following the following method:
	 * Given the voltage V at the source.
	 * The resistance R of the conductors is calculated based on the given conditions (size, metal, length, etc).
	 * Given the connected load's current I.
	 * The voltage Vw (drop) across the circuit is calculated as Vw = R x I
	 * The voltage drop returned is V-Vw.
	 * @return The calculated DC voltage drop in volts.
	 */
	public double getVoltageDropVoltsDC() {
		if(checkDCVDInput())
			return sourceVoltage - getVoltageAtLoadDC();
		return 0;
	}

	/**
	 * Returns the calculated DC voltage drop in percentage.
	 * The percentage is in reference to the voltage at the source.
	 * @return The calculated DC voltage drop in percentage.
	 */
	public double getVoltageDropPercentageDC() {
		if(checkDCVDInput())
			return 100 * getVoltageDropVoltsDC() / sourceVoltage;
		return 0;
	}
	//endregion voltage drop output

	//region Conductor sizing per ACVD

	/**
	 * Returns the size of the circuit's conductor whose AC voltage drop (under the given conditions) is less or equals to the given
	 * maximum voltage drop. If the returned string is empty the user should check for errors in the ResultMessage object.
	 * @return The size of the conductor as defined in {@link Size}.
	 * @see #resultMessages
	 */
	public String getCalculatedSizeAC(){
		if(checkACCSInput())
			return computeSizeAC();
		return "";
	}

	/**
	 * Returns the actual AC voltage drop for the calculated conductor size, under the given conditions.
	 * @return The AC voltage drop in volts, of the calculated conductor.
	 * @see #getCalculatedSizeAC()
	 */
	public double getActualVoltageDropPercentageAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageAC;
	}

	/**
	 * Returns the calculated one way length of the circuit's conductor whose AC voltage drop (under the given conditions) is less or
	 * equals to the given maximum voltage drop. If the returned value is zero, the user should check for errors in the
	 * ResultMessage object.
	 * @return The maximum conductor's length in feet.
	 * @see #getCalculatedSizeAC()
	 */
	public double getMaxLengthAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthAC;
	}
	//endregion

	//region Conductor sizing per DCVD
	/**
	 * Returns the size of the circuit's conductor whose DC voltage drop (under the given conditions) is less or equals to the given
	 * maximum voltage drop. If the returned string is empty the user should check for errors in the ResultMessage object.
	 * @return The size of the conductor as defined in {@link Size}.
	 * @see #resultMessages
	 */
	public String getCalculatedSizeDC(){
		if(checkDCCSInput())
			return computeSizeDC();
		return "";
	}

	/**
	 * Returns the actual DC voltage drop for the calculated conductor size, under the given conditions.
	 * @return The DC voltage drop in volts, of the calculated conductor.
	 * @see #getCalculatedSizeDC()
	 */
	public double getActualVoltageDropPercentageDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageDC;
	}

	/**
	 * Returns the calculated one way length of the circuit's conductor whose DC voltage drop (under the given conditions) is less or
	 * equals to the given maximum voltage drop. If the returned value is zero, the user should check for errors in the
	 * ResultMessage object.
	 * @return The maximum conductor's length in feet.
	 * @see #getCalculatedSizeDC()
	 */
	public double getMaxLengthDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthDC;
	}
	//endregion

	//region private methods
	//region COMMON
	private double getMaxAmpacity(){
		return circuit.getAmpacity();
	}
	//endregion

	//region ACVD
	private boolean checkACVDInput(){
		resultMessages.clearMessages();
		if(sourceVoltage <=0 )
			resultMessages.add(ERROR01);
		if(!(phases == 1 || phases == 3))
			resultMessages.add(ERROR02);
		if(circuit.getNumberOfSets() < 1 || circuit.getNumberOfSets() > 10)
			resultMessages.add(ERROR04);
		if(circuit.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(ConductorProperties.isValidSize(circuit.getSize())){
			if(!resultMessages.containsMessage(ERROR04)) {
				if (circuit.getNumberOfSets() > 1 && ConductorProperties.compareSizes(circuit.getSize(), "1/0") < 0)
					resultMessages.add(ERROR21);
			}
			if(!resultMessages.containsMessage(ERROR06)) {
				if (getMaxAmpacity() < loadCurrent)
					resultMessages.add(ERROR20);
			}
		}
		else
			resultMessages.add(ERROR03);
		if(powerFactor < 0.7 || powerFactor > 1.0)
			resultMessages.add(ERROR07);
		return !resultMessages.hasErrors();
	}

	private double computeVoltageAtLoadAC(String conductorSize){
		double k = getK();
		double oneWayACResistance = ConductorProperties.getACResistance(conductorSize, circuit.getMetal(), circuit.getConduitMaterial(),
				circuit.getLength(), circuit.getNumberOfSets());

		double oneWayConductorReactance = ConductorProperties.getReactance(conductorSize, Magnetic.isMagnetic(circuit.getConduitMaterial()),
				circuit.getLength(), circuit.getNumberOfSets());
		Complex totalConductorImpedanceComplex = new Complex(k * oneWayACResistance,k * oneWayConductorReactance);
		Complex sourceVoltageComplex = new Complex(sourceVoltage, 0);
		Complex loadCurrentComplex = new Complex(loadCurrent * powerFactor, -loadCurrent * Math.sin(Math.acos(powerFactor)));
		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);
		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);
		return voltageAtLoadComplex.abs();
	}

	private double getK(){
		if(phases == 1) return 2;
		else return Math.sqrt(3);
	}
	//endregion

	//region DCVD
	private boolean checkDCVDInput(){
		resultMessages.clearMessages();
		if(sourceVoltage <=0 ) resultMessages.add(ERROR01);
		if(circuit.getNumberOfSets() < 1 || circuit.getNumberOfSets() > 10)
			resultMessages.add(ERROR04);
		if(circuit.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(ConductorProperties.isValidSize(circuit.getSize())){
			if(!resultMessages.containsMessage(ERROR04)) {
				if (circuit.getNumberOfSets() > 1 && ConductorProperties.compareSizes(circuit.getSize(), "1/0") < 0)
					resultMessages.add(ERROR21);
			}
			if(!resultMessages.containsMessage(ERROR06)) {
				if (getMaxAmpacity() < loadCurrent)
					resultMessages.add(ERROR20);
			}
		}
		else
			resultMessages.add(ERROR03);
		return !resultMessages.hasErrors();
	}

	private double computeVoltageAtLoadDC(String conductorSize){
		double oneWayDCResistance;
		oneWayDCResistance = ConductorProperties.getDCResistance(conductorSize, circuit.getMetal(), circuit.getLength(),
				circuit.getNumberOfSets(), circuit.isCopperCoated());
		return sourceVoltage - 2 * oneWayDCResistance * loadCurrent;
	}
	//endregion

	//region CS per ACVD
	private boolean checkACCSInput(){
		resultMessages.clearMessages();
		if(sourceVoltage <=0 )
			resultMessages.add(ERROR01);
		if(!(phases == 1 || phases == 3))
			resultMessages.add(ERROR02);
		if(circuit.getNumberOfSets() < 1 || circuit.getNumberOfSets() > 10)
			resultMessages.add(ERROR04);
		if(circuit.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25)
			resultMessages.add(ERROR08);
		if(powerFactor < 0.7 || powerFactor > 1.0)
			resultMessages.add(ERROR07);
		return !resultMessages.hasErrors();
	}

	private String computeSizeAC(){
		for(String s : ConductorProperties.getSizes()){
			actualVoltageDropPercentageAC = 100 * (sourceVoltage - computeVoltageAtLoadAC(s)) / sourceVoltage;
			if(actualVoltageDropPercentageAC <= maxVoltageDropPercent){
				maxLengthAC = computeMaxLengthAC(s);
				if(maxLengthAC <= 0) {
					resultMessages.add(ERROR30);
					return "";
				}
				if(circuit.getNumberOfSets() > 1 && ConductorProperties.compareSizes(s,"1/0") < 0)
					resultMessages.add(WARNN21);
				return s;
			}
		}
		resultMessages.add(ERROR31);
		return "";
	}

	private double computeMaxLengthAC(String conductorSize){
		double conductorR =	ConductorProperties.getACResistance(conductorSize, circuit.getMetal(), circuit.getConduitMaterial()) *
							0.001 / circuit.getNumberOfSets();
		double conductorX = ConductorProperties.getReactance(conductorSize, Magnetic.isMagnetic(circuit.getConduitMaterial())) *
							0.001 / circuit.getNumberOfSets();
		double theta = Math.acos(powerFactor);
		double Vs2 = Math.pow(sourceVoltage, 2);
		double A = getK() * loadCurrent * (conductorR * powerFactor + conductorX * Math.sin(theta));
		double B = getK() * loadCurrent * (conductorX * powerFactor - conductorR * Math.sin(theta));
		double C = Vs2 * (1 - Math.pow(1 - maxVoltageDropPercent/100, 2));
		double Rad = 4 * Vs2 * A * A - 4 * (A * A + B * B) * C;
		if(Rad<0) return Rad;
		//double len2 = (2 * sourceVoltage * A + Math.sqrt(Rad))/(2 * (A * A + B * B));
		//len1 is always the lesser value between the two lengths and produces a voltage drop across the conductor that is less that the
		// voltage source, that is len1 is always the correct value, unless it's a negative number.
		double len1 = (2 * sourceVoltage * A - Math.sqrt(Rad))/(2 * (A * A + B * B));
		if(len1 > 0) return len1;
		return 0;
	}
	//endregion

	//region CS per DCVD
	private boolean checkDCCSInput(){
		resultMessages.clearMessages();
		if(sourceVoltage <=0 )
			resultMessages.add(ERROR01);
		if(circuit.getNumberOfSets() < 1 || circuit.getNumberOfSets() > 10)
			resultMessages.add(ERROR04);
		if(circuit.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25)
			resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	private String computeSizeDC(){
		for(String conductorSize : ConductorProperties.getSizes()){
			actualVoltageDropPercentageDC = 100 * (sourceVoltage - computeVoltageAtLoadDC(conductorSize)) / sourceVoltage;
			if(actualVoltageDropPercentageDC <= maxVoltageDropPercent){
				maxLengthDC = computeMaxLengthDC(conductorSize);
				if(maxLengthDC <= 0) {
					resultMessages.add(ERROR30);
					return "";
				}
				if(circuit.getNumberOfSets() > 1 && ConductorProperties.compareSizes(conductorSize,"1/0") < 0)
					resultMessages.add(WARNN21);
				return conductorSize;
			}
		}
		resultMessages.add(ERROR31);
		return "";
	}

	private double computeMaxLengthDC(String conductorSize){
		double dCResistance;
		dCResistance = ConductorProperties.getDCResistance(conductorSize, circuit.getMetal(), circuit.getLength(),
				circuit.getNumberOfSets(), circuit.isCopperCoated());
		return sourceVoltage * maxVoltageDropPercent * circuit.getLength() / (200 * loadCurrent * dCResistance);
	}
	//endregion
	//endregion
}