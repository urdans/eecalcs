package eecalcs.voltagedrop;

import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.RoConductor;
import eecalcs.conductors.Size;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import org.apache.commons.math3.complex.Complex;
import tools.ResultMessage;
import tools.ROResultMessages;
import tools.ResultMessages;

/**
 Provides methods for calculation of the voltage drop across conductors and for
 calculation of the maximum length of a circuit for a given maximum voltage
 drop.
 <br><br>
 Before any computation is done, all the required input values are validated. If
 there is any invalid input value, the results of the calculations are useless,
 like:
 <br>
 - Zero for voltage drop;<br>
 - Null for the minimum conductor size.<br>
<br>
 When a zero or null value is returned, the resultMessages field contains the
 message explaining the reason. Most of those reasons are failing in the
 validation of the input data. However, there are few messages that are obtained
 during calculation time. These are related to NEC code violations and no
 valid results are obtained for the given set of conditions.<br>
 The resultMessages field must be checked for the presence of those messages.
 See {@link ResultMessages} class for how to use it.
 */

public class VoltDrop implements ROVoltDrop {
	private VoltageSystemAC sourceVoltage = VoltageSystemAC.v120_1ph_2w;
	private Conductor conductor;
	private int sets = 1;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private double maxVoltageDropPercent = 3;
	private Material conduitMaterial = Material.PVC;

	//region Predefined messages
	private static final ResultMessage ERROR01	= new ResultMessage("Source voltage must be greater that zero.",-1);
	private static final ResultMessage ERROR02	= new ResultMessage("Invalid conduit material.",-2);
	private static final ResultMessage ERROR03	= new ResultMessage("Invalid conductor size.",-3);
	private static final ResultMessage ERROR04	= new ResultMessage("Number of sets must be between 1 and 10.",-4);
	private static final ResultMessage ERROR05	= new ResultMessage("One way conductor length must be greater than 0.",-5);
	private static final ResultMessage ERROR06	= new ResultMessage("Load current must be greater than 0.",-6);
	private static final ResultMessage ERROR07	= new ResultMessage("Motor factor must be between 0.7 and 1.",-7);
	private static final ResultMessage ERROR08	= new ResultMessage("Voltage drop for determining conductor sizing must be between 0.5% and 25%",-8);
	private static final ResultMessage ERROR09	= new ResultMessage("Invalid conductor object.",-9);
	private static final ResultMessage ERROR20	= new ResultMessage("Load current exceeds maximum allowed ampacity of the set.",-20);
	private static final ResultMessage ERROR21	= new ResultMessage("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310" +
			".10(H)(1)",-21);
	private static final ResultMessage ERROR30	= new ResultMessage("No length can achieve that voltage drop under the given conditions.", -30);
	private static final ResultMessage ERROR31	= new ResultMessage("No building conductor can achieve that voltage drop under the given conditions.",
			-31);
	private static final ResultMessage WARNN21	= new ResultMessage(ERROR21.message,21);
	//Quedé aquí 6: why am I using a warning message that is also an error?
	// when is an error and when is a warning? This must be refactored to be
	// consistent with the way the size per ampacity is performed. But also,
	// how to tell the user that the actual size is under 1/0? shouldn't the
	// calculated size be included in the message as part of the error
	// message or as a warning message?
	private double maxLengthAC;	//maximum length of a circuit for the given max AC voltage drop
	private double maxLengthDC;	//maximum length of a circuit for the given max DC voltage drop
	private double actualVoltageDropPercentageAC; //actual AC voltage drop percentage for the calculated conductor size.
	private double actualVoltageDropPercentageDC; //actual DC voltage drop percentage for the calculated conductor size.
	//endregion

	/**
	 Container for messages resulting from validation of input variables and
	 calculations performed by this class.

	 @see ResultMessages
	 */
	private final ResultMessages resultMessages = new ResultMessages();

	/**
	 @return The {@link ROResultMessages} object containing all the error and
	 warning messages of this object.
	 */
	public ROResultMessages getResultMessages(){
		return resultMessages;
	}

	/*
	Checks that all input values are valid for the calculation of AC voltage
	drop. If a value is not valid, its corresponding error message is added
	to the resultMessages field.
	 */
	private boolean checkInputForACVoltageDrop(){
		resultMessages.clearMessages();
		if(conductor == null)
			resultMessages.add(ERROR09);
		else{
			if(conductor.getSize() == null)
				resultMessages.add(ERROR03);
			else {
				if (ConductorProperties.compareSizes(conductor.getSize(), Size.AWG_1$0) < 0 && sets > 1)
					resultMessages.add(ERROR21);
				if(loadCurrent > sets * conductor.getCorrectedAndAdjustedAmpacity())
					resultMessages.add(ERROR20);
			}
			if(conductor.getLength() <= 0)
				resultMessages.add(ERROR05);
		}
		if(sourceVoltage == null)
			resultMessages.add(ERROR01);
		if(sets <= 0 || sets>10)
			resultMessages.add(ERROR04);
		if(loadCurrent <= 0 )
			resultMessages.add(ERROR06);
		if(powerFactor < 0.7 || powerFactor > 1.0 )
			resultMessages.add(ERROR07);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25.0)
			resultMessages.add(ERROR08);
		if(conduitMaterial == null)
			resultMessages.add(ERROR02);
		return !resultMessages.hasErrors();
	}

	/*
	Checks that all input values are valid for the calculation of DC voltage
	drop. If a value is not valid, its corresponding error message is added
	to the resultMessages field.
 	*/
	private boolean checkInputForDCVoltageDrop(){
		resultMessages.clearMessages();
		if(sourceVoltage == null)
			resultMessages.add(ERROR01);
		if(sets <= 0 || sets>10)
			resultMessages.add(ERROR04);
		if(conductor != null && conductor.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25.0)
			resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	/*
	Checks that all input values are valid for the calculation of the conductor
	size for the maximum AC voltage drop. If a value is not valid, its
	corresponding error message is added to the resultMessages field.
 	*/
	private boolean checkInputForACSizeCalculation(){
		resultMessages.clearMessages();
		if(sourceVoltage == null)
			resultMessages.add(ERROR01);
		if(sets <= 0 || sets>10)
			resultMessages.add(ERROR04);
		if(conductor == null || conductor.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(powerFactor < 0.7 || powerFactor > 1.0)
			resultMessages.add(ERROR07);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25)
			resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	/*
	Checks that all input values are valid for the calculation of the conductor
	size for the maximum DC voltage drop. If a value is not valid, its
	corresponding error message is added to the resultMessages field.
 	*/
	private boolean checkInputForDCSizeCalculation(){
		resultMessages.clearMessages();
		if(sourceVoltage == null)
			resultMessages.add(ERROR01);
		if(sets <= 0 || sets>10)
			resultMessages.add(ERROR04);
		if(conductor == null || conductor.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25)
			resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	/**
	 Constructs a VoltDrop object for the given conductor object.
	 The property's values are:
	 <br><br>
	 <p><b>Source voltage</b>: defaults to 120 volts, single phase, 2 wires.
	 <p><b>Conductor</b>: provided in the constructor.
	 <p><b>Sets</b>: defaults to 1 set.
	 <p><b>Load current</b>: defaults to 10 amps.
	 <p><b>Motor factor</b>: defaults to 1.0.
	 <p><b>Conduit material</b>: defaults to PVC.
	 <p><b>Maximum allowed voltage drop</b>: defaults to 3 percent.
	 <br><br>
	 All these values can be changed or obtained by its corresponding setter
	 and getter.

	 @param conductor The existing conductor object.
	 @see Conductor
	 */
	public VoltDrop(Conductor conductor){
		this.conductor = conductor;
	}

	/**
	 Default constructor for this voltage drop object.
	 The default property's values are:
	 <br><br>
	 <p><b>Source voltage</b>: defaults to 120 volts, single phase, 2 wires..
	 <p><b>Conductor</b>: null. It can later be assigned with {@link
	 #setConductor(Conductor)}
	 <p><b>Sets</b>: defaults to 1 set.
	 <p><b>Load current</b>: defaults to 10 amps.
	 <p><b>Motor factor</b>: defaults to 1.0.
	 <p><b>Conduit material</b>: defaults to PVC.
	 <p><b>Maximum allowed voltage drop</b>: defaults to 3 percent.
	 <br><br>
	 */
	public VoltDrop(){
	}

	//----Common setters ang getters
	/**
	 Sets the conductor for this voltage drop object.

	 @param conductor The conductor for the voltage drop.
	 */
	public void setConductor(Conductor conductor) {
		this.conductor = conductor;
	}

	/**
	 Gets the conductor that this voltage drop object uses.
	 @return The conductor that this voltage drop object uses.
	 */
	public RoConductor getConductor() {
		return conductor;
	}

	/**
	 Sets the source system voltage for this VoltDrop object.

	 @param sourceVoltage The new source system voltage.
	 Notice that no validation is performed at this point.
	 The user must check for the presence of errors or warnings after obtaining
	 a calculation result of zero or null.
	 @see VoltageSystemAC
	 */
	public void setSourceVoltage(VoltageSystemAC sourceVoltage) {
		this.sourceVoltage = sourceVoltage;
	}

	/**
	 Sets the number of sets of conductors in parallel.

	 @param sets The number of sets in parallel.
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setSets(int sets) {
		this.sets = sets;
	}

	/**
	 Sets the load's current.

	 @param loadCurrent The current of the load in amperes.
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setLoadCurrent(double loadCurrent) {
		this.loadCurrent = loadCurrent;
	}

	/**
	 Sets the power factor of the load.

	 @param powerFactor The power factor of the load.
	 Should be a number between 0.7 and 1.0 inclusive.
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setPowerFactor(double powerFactor) {
		this.powerFactor = powerFactor;
	}

	/**
	 Sets the maximum allowed voltage drop. This value is used to compute the
	 the size and the maximum length of the circuit conductors that would have a
	 voltage drop less or equal to the specified value.

	 @param maxVoltageDropPercent The maximum voltage drop in percentage.
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setMaxVoltageDropPercent(double maxVoltageDropPercent) {
		this.maxVoltageDropPercent = maxVoltageDropPercent;
	}

	/**
	 Sets the material of the conduit.

	 @param conduitMaterial The material of the conduit holding the conductor.
	 For the rare scenario of conductors installed in free air, the user should
	 use the default PVC material.<br>
	 If the conductor is part of a cable, the outer jacket of the cable will
	 define the conduit (raceway) material. For AC or MC cable, it's steel. For
	 any of the NM cable, the PVC material must be used.<br>
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setConduitMaterial(Material conduitMaterial) {
		this.conduitMaterial = conduitMaterial;
	}

	//----AC Calculations
	@Override
	public double getACVoltageDropPercentage() {
		if(checkInputForACVoltageDrop())
			return 100.0 * getACVoltageDrop()/sourceVoltage.getVoltage();
		return 0;
	}

	@Override
	public double getACVoltageDrop() {
		if(checkInputForACVoltageDrop())
			return sourceVoltage.getVoltage() - getACVoltageAtLoad();
		return 0;
	}

	@Override
	public double getACVoltageAtLoad(){
		if(checkInputForACVoltageDrop())
			return getGenericACVoltageAtLoad(conductor.getSize());
		return 0;
	}

	@Override
	public Size getCalculatedSizeAC(){
		if(checkInputForACSizeCalculation())
			return computeSizeAC();
		return null;
	}

	@Override
	public double getMaxLengthACForCalculatedSize(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthAC;
	}

	@Override
	public double getMaxLengthACForActualConductor(){
		if(checkInputForACSizeCalculation()){
			maxLengthAC = computeMaxLengthAC(conductor.getSize());
			if(maxLengthAC <= 0) {
				resultMessages.add(ERROR30);
				return 0;
			}
		}
		return maxLengthAC;
	}

	@Override
	public double getActualVoltageDropPercentageAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageAC;
	}

	/*
	 Calculates and returns the AC voltage at the load terminals, fed by the
	 preset conductor, but of the given size.
	 */
	private double getGenericACVoltageAtLoad(Size size){
		double k = sourceVoltage.getPhases() == 1 ? 2 : sourceVoltage.getFactor();
		double oneWayACResistance = ConductorProperties.getACResistance(
				size,
				conductor.getMetal(),
				conduitMaterial,
				conductor.getLength(),
				sets
		);
		double oneWayConductorReactance = ConductorProperties.getReactance(
				size,
				ConduitProperties.isMagnetic(conduitMaterial),
				conductor.getLength(),
				sets
		);
		Complex totalConductorImpedanceComplex = new Complex(k * oneWayACResistance, k * oneWayConductorReactance);
		Complex sourceVoltageComplex = new Complex(sourceVoltage.getVoltage(), 0);
		Complex loadCurrentComplex = new Complex(loadCurrent * powerFactor, -loadCurrent * Math.sin(Math.acos(powerFactor)));
		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);
		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);
		return voltageAtLoadComplex.abs();
	}

	/*
	 Calculates and return the size of the preset conductor whose AC voltage
	 drop percentage is less or equal to the given maximum voltage drop.
	 Simultaneously, the max length and the actual voltage drop percentage is
	 calculated and saved in the corresponding fields.
 	*/
	private Size computeSizeAC(){
		for(Size size : Size.values()) {
			if(loadCurrent >
				sets *
				ConductorProperties.getAmpacity(
					size,
					conductor.getMetal(),
					conductor.getTemperatureRating()
				))
				continue;

			actualVoltageDropPercentageAC = 100 * (sourceVoltage.getVoltage() - getGenericACVoltageAtLoad(size)) / sourceVoltage.getVoltage();
			if(actualVoltageDropPercentageAC <= maxVoltageDropPercent){
				maxLengthAC = computeMaxLengthAC(size);
				if(maxLengthAC <= 0) {
					resultMessages.add(ERROR30);
					return null;
				}
				if(sets > 1 && ConductorProperties.compareSizes(size, Size.AWG_1$0) < 0)
					resultMessages.add(WARNN21);
				return size;
			}
		}
		resultMessages.add(ERROR31);
		return null;
	}

	/*
	 Calculates and return the maximum length that the preset conductor of the
	 calculated size can reach while maintaining a voltage drop percentage less
	 or equal to the given maximum voltage drop percentage.
	 */
	private double computeMaxLengthAC(Size size){
		double conductorR =	ConductorProperties.getACResistance(size, conductor.getMetal(), conduitMaterial) * 0.001 / sets;
		double conductorX = ConductorProperties.getReactance(size, ConduitProperties.isMagnetic(conduitMaterial)) * 0.001 / sets;
		double theta = Math.acos(powerFactor);
		double Vs2 = Math.pow(sourceVoltage.getVoltage(), 2);
		double k = sourceVoltage.getPhases() == 1 ? 2 : sourceVoltage.getFactor();
		double A = k * loadCurrent * (conductorR * powerFactor + conductorX * Math.sin(theta));
		double B = k * loadCurrent * (conductorX * powerFactor - conductorR * Math.sin(theta));
		double C = Vs2 * (1 - Math.pow(1 - maxVoltageDropPercent/100, 2));
		double Rad = 4 * Vs2 * A * A - 4 * (A * A + B * B) * C;
		if(Rad<0)
			//return Rad;
			Rad = 0;
		//double len2 = (2 * sourceVoltage.getVoltage() * A + Math.sqrt(Rad))/(2 * (A * A + B * B));
		//len1 is always the smallest value between the two lengths and produces a voltage drop across the conductor that is less that the
		//voltage source, that is len1 is always the correct value, unless it's a negative number.
		double len1 = (2 * sourceVoltage.getVoltage() * A - Math.sqrt(Rad))/(2 * (A * A + B * B));
		if(len1 > 0)
			return len1;
		return 0;
	}

	//----DC Calculations
	@Override
	public double getDCVoltageDropPercentage() {
		if(checkInputForDCVoltageDrop())
			return 100.0 * getDCVoltageDrop()/sourceVoltage.getVoltage();
		return 0;
	}

	@Override
	public double getDCVoltageDrop() {
		if(checkInputForDCVoltageDrop())
			return sourceVoltage.getVoltage() - getDCVoltageAtLoad();
		return 0;
	}

	@Override
	public double getDCVoltageAtLoad(){
		if(checkInputForDCVoltageDrop())
			return getGenericDCVoltageAtLoad(conductor.getSize());
		return 0;
	}

	@Override
	public Size getCalculatedSizeDC(){
		if(checkInputForDCSizeCalculation())
			return computeSizeDC();
		return null;
	}

	@Override
	public double getMaxLengthDCForCalculatedSize(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthDC;
	}

	@Override
	public double getMaxLengthDCForActualConductor(){
		if(checkInputForDCSizeCalculation()) {
			maxLengthDC = computeMaxLengthDC(conductor.getSize());
			if(maxLengthDC <= 0) {
				resultMessages.add(ERROR30);
				return 0;
			}
		}
		return maxLengthDC;
	}

	@Override
	public double getActualVoltageDropPercentageDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageDC;
	}

	/*
	Calculates and returns the DC voltage at the load terminals, fed by the
	preset conductor, of the given size.
 	*/
	private double getGenericDCVoltageAtLoad(Size size){
		double oneWayDCResistance;
		oneWayDCResistance = ConductorProperties.getDCResistance(
				size,
				conductor.getMetal(),
				conductor.getLength(),
				sets,
				conductor.getCopperCoating());
		return sourceVoltage.getVoltage() - 2 * oneWayDCResistance * loadCurrent;
	}

	/*
	Calculates and return the size of the preset conductor whose DC voltage drop
	percentage is less or equal to the given maximum voltage drop.
	Simultaneously, the max length and the actual voltage drop percentage is
	calculated and saved in the corresponding fields.
 	*/
	private Size computeSizeDC(){
		for(Size size : Size.values()){
			actualVoltageDropPercentageDC = 100 * (sourceVoltage.getVoltage() - getGenericDCVoltageAtLoad(size)) / sourceVoltage.getVoltage();
			if(actualVoltageDropPercentageDC <= maxVoltageDropPercent){
				maxLengthDC = computeMaxLengthDC(size);
				if(maxLengthDC <= 0) {
					resultMessages.add(ERROR30);
					return null;
				}
				if(sets > 1 && ConductorProperties.compareSizes(size, Size.AWG_1$0) < 0)
					resultMessages.add(WARNN21);
				return size;
			}
		}
		resultMessages.add(ERROR31);
		return null;
	}

	/*
	Calculates and return the maximum length that the preset conductor of the
	calculated size can reach while maintaining a voltage drop percentage less
	or equal to the given maximum voltage drop percentage.
	 */
	private double computeMaxLengthDC(Size conductorSize){
		double dCResistance;
		dCResistance = ConductorProperties.getDCResistance(
				conductorSize,
				conductor.getMetal(),
				conductor.getLength(),
				sets,
				conductor.getCopperCoating());
		return sourceVoltage.getVoltage() * maxVoltageDropPercent * conductor.getLength() / (200 * loadCurrent * dCResistance);
	}

	@Override
	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}
}