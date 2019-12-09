package eecalcs.voltagedrop;

import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Size;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import org.apache.commons.math3.complex.Complex;
import tools.Message;
import tools.ResultMessages;

/**
 Provides methods for calculation of the voltage drop across conductors
 and for calculation of the maximum length of a circuit for a given maximum
 voltage drop.
 <br><br>
 Before any computation is done, all the required input values are validated. If
 there is an invalid value, the result of the calculation are useless values,
 like:
 <br>
 - Zero for voltage drop;<br>
 - Null for determining the minimum size.<br>

 When a zero or null value is returned, the resultMessages field contains the
 message explaining the reason. Most of those reasons are input validation
 fails, but there are few messages that are obtained during calculation time and
 are that related to code violations, no mathematical results for the given set
 of conditions or even warnings. The resultMessages field must be checked for
 the presence of those messages. See {@link ResultMessages} class for details.
 */

public class VoltDrop {
	private VoltageSystemAC sourceVoltage = VoltageSystemAC.v120_1ph_2w;
	private Conductor conductor;
	private int sets = 1;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private double maxVoltageDropPercent = 3;
	private Material conduitMaterial = Material.PVC;

	//region messages
	private static Message ERROR01	= new Message("Source voltage must be greater that zero.",-1);
	private static Message ERROR02	= new Message("Invalid conduit material.",-2);
	private static Message ERROR03	= new Message("Invalid conductor size.",-3);
	private static Message ERROR04	= new Message("Number of sets must be between 1 and 10.",-4);
	private static Message ERROR05	= new Message("One way conductor length must be greater than 0.",-5);
	private static Message ERROR06	= new Message("Load current must be greater than 0.",-6);
	private static Message ERROR07	= new Message("Power factor must be between 0.7 and 1.",-7);
	private static Message ERROR08	= new Message("Voltage drop for determining conductor sizing must be between 0.5% and 25%",-8);
	private static Message ERROR09	= new Message("Invalid conductor object.",-9);
	private static Message ERROR20	= new Message("Load current exceeds maximum allowed ampacity of the set.",-20);
	private static Message ERROR21	= new Message("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310" +
			".10(H)(1)",-21);
	private static Message ERROR30	= new Message("No length can achieve that voltage drop under the given conditions.", -30);
	private static Message ERROR31	= new Message("No building conductor can achieve that voltage drop under the given conditions.",
			-31);
	private static Message WARNN21	= new Message(ERROR21.message,21);
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
	public ResultMessages resultMessages = new ResultMessages();

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
				if(loadCurrent > sets * conductor.getAmpacity())
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
		if(conductor == null || (conductor != null && conductor.getLength() <= 0) )
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
		if(conductor == null || (conductor != null && conductor.getLength() <= 0) )
			resultMessages.add(ERROR05);
		if(loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25)
			resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	/**
	 Constructs a VoltDrop object for the given conductor object.
	 The default property values are:
	 <br><br>
	 <p><b>Source voltage</b>: defaults to 120 volts.
	 <p><b>Conductor</b>: provided in the constructor.
	 <p><b>Sets</b>: defaults to 1 set.
	 <p><b>Load current</b>: defaults to 10 amps.
	 <p><b>Power factor</b>: defaults to 1.0.
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
	 Sets the source system voltage for this VoltDrop object.

	 @param sourceVoltage The new source system voltage.
	 Notice that no validation is performed while setting this value. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero or null.
	 @see VoltageSystemAC
	 */
	public void setSourceVoltage(VoltageSystemAC sourceVoltage) {
		this.sourceVoltage = sourceVoltage;
	}

	/**
	 Sets the number of sets of conductors in parallel.

	 @param sets The number of sets in parallel.
	 Notice that no validation is performed while setting this value. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setSets(int sets) {
		this.sets = sets;
	}

	/**
	 Sets the load's current.

	 @param loadCurrent The current of the load in amperes.
	 Notice that no validation is performed while setting this value. The user
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
	 Notice that no validation is performed while setting this value. The user
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
	 Notice that no validation is performed while setting this value. The user
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
	 Notice that no validation is performed while setting this value. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setConduitMaterial(Material conduitMaterial) {
		this.conduitMaterial = conduitMaterial;
	}

	//----AC Calculations
	/**
	 Calculates and returns the AC voltage drop percentage across the set of
	 conductors under the preset conditions.

	 @return The voltage drop in percentage. If the result is zero, the
	 resultMessage field content must be checked to determine the reason.
	 */
	public double getACVoltageDropPercentage() {
		if(checkInputForACVoltageDrop())
			return 100.0 * getACVoltageDrop()/sourceVoltage.getVoltage();
		return 0;
	}

	/**
	 Calculates and returns the AC voltage drop in volts across the set of
	 conductors under the preset conditions.

	 @return The voltage drop in volts. If the result is zero, the
	 resultMessage field content must be checked to determine the reason.
	 */
	public double getACVoltageDrop() {
		if(checkInputForACVoltageDrop())
			return sourceVoltage.getVoltage() - getACVoltageAtLoad();
		return 0;
	}

	/**
	 Calculates and returns the AC voltage in volts at the load terminals
	 under the preset conditions.

	 @return The voltage at the load terminals in volts. If the result is zero,
	 the resultMessage field content must be checked to determine the reason.
	 */
	public double getACVoltageAtLoad(){
		if(checkInputForACVoltageDrop())
			return getGenericACVoltageAtLoad(conductor.getSize());
		return 0;
	}

	/**
	 Returns the size of the circuit's conductor whose AC voltage drop (under
	 the given conditions) is less or equals to the given maximum voltage drop.
	 If the returned Size is null the user should check for errors in the
	 resultMessage field.

	 @return The size of the conductor as defined in {@link Size}.
	 @see #resultMessages
	 */
	public Size getCalculatedSizeAC(){
		if(checkInputForACSizeCalculation())
			return computeSizeAC();
		return null;
	}

	/**
	 Returns the calculated one way length of the circuit's conductor whose AC
	 voltage drop (under the given conditions) is less or equals to the given
	 maximum voltage drop. If the returned value is zero, the user must check
	 the result Message field content.

	 @return The maximum conductor's length in feet.
	 @see #getCalculatedSizeAC()
	 */
	public double getMaxLengthAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthAC;
	}

	/**
	 Returns the actual AC voltage drop for the calculated conductor size, under
	 the given conditions.

	 @return The AC voltage drop in volts, of the calculated conductor size.
	 @see #getCalculatedSizeAC()
	 */
	public double getActualVoltageDropPercentageAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageAC;
	}

	/*
	 Calculates and returns the AC voltage at the load terminals fed, by the
	 preset conductor, of the given size.
	 */
	private double getGenericACVoltageAtLoad(Size size){
		double k = sourceVoltage.getPhases() == 1 ? 2 : sourceVoltage.getFactor();
		double oneWayACResistance = ConductorProperties.getACResistance(
				size,
				conductor.getMetal(),
				conduitMaterial,
				conductor.getLength(),
				sets);
		double oneWayConductorReactance = ConductorProperties.getReactance(
				size,
				ConduitProperties.isMagnetic(conduitMaterial),
				conductor.getLength(),
				sets);
		Complex totalConductorImpedanceComplex = new Complex(k * oneWayACResistance, k * oneWayConductorReactance);
		Complex sourceVoltageComplex = new Complex(sourceVoltage.getVoltage(), 0);
		Complex loadCurrentComplex = new Complex(loadCurrent * powerFactor, -loadCurrent * Math.sin(Math.acos(powerFactor)));
		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);
		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);
		return voltageAtLoadComplex.abs();
	}

	/*
	 Calculates and return the size of the preset conductor whose AC voltage drop
	 percentage is less or equal to the given maximum voltage drop.
	 Simultaneously, the max length and the actual voltage drop percentage is
	 calculated and saved in the corresponding fields.
 	*/
	private Size computeSizeAC(){
		for(Size size : Size.values()) {
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
		if(Rad<0) return Rad;
		//double len2 = (2 * sourceVoltage * A + Math.sqrt(Rad))/(2 * (A * A + B * B));
		//len1 is always the smallest value between the two lengths and produces a voltage drop across the conductor that is less that the
		//voltage source, that is len1 is always the correct value, unless it's a negative number.
		double len1 = (2 * sourceVoltage.getVoltage() * A - Math.sqrt(Rad))/(2 * (A * A + B * B));
		if(len1 > 0)
			return len1;
		return 0;
	}

	//----DC Calculations
	/**
	 Calculates and returns the DC voltage drop percentage across the set of
	 conductors under the preset conditions.

	 @return The voltage drop in percentage. If the result is zero, the
	 resultMessage field content must be checked to determine the reason.
	 */
	public double getDCVoltageDropPercentage() {
		if(checkInputForDCVoltageDrop())
			return 100.0 * getDCVoltageDrop()/sourceVoltage.getVoltage();
		return 0;
	}

	/**
	 Calculates and returns the DC voltage drop in volts across the set of
	 conductors under the preset conditions.

	 @return The voltage drop in volts. If the result is zero, the
	 resultMessage field content must be checked to determine the reason.
	 */
	public double getDCVoltageDrop() {
		if(checkInputForDCVoltageDrop())
			return sourceVoltage.getVoltage() - getDCVoltageAtLoad();
		return 0;
	}

	/**
	 Calculates and returns the DC voltage in volts at the load terminals
	 under the preset conditions.

	 @return The voltage at the load terminals in volts. If the result is zero,
	 the resultMessage field content must be checked to determine the reason.
	 */
	public double getDCVoltageAtLoad(){
		if(checkInputForDCVoltageDrop())
			return getGenericDCVoltageAtLoad(conductor.getSize());
		return 0;
	}

	/**
	 Returns the size of the circuit's conductor whose DC voltage drop (under
	 the given conditions) is less or equals to the given maximum voltage drop.
	 If the returned value is null the user must check the resultMessage field
	 content.

	 @return The size of the conductor as defined in {@link Size}.
	 @see #resultMessages
	 */
	public Size getCalculatedSizeDC(){
		if(checkInputForDCSizeCalculation())
			return computeSizeDC();
		return null;
	}

	/**
	 Returns the calculated one way length of the circuit's conductor whose DC
	 voltage drop (under the given conditions) is less or equals to the given
	 maximum voltage drop. If the returned value is zero, the user must check
	 the resultMessage field content.

	 @return The maximum conductor's length in feet.
	 @see #getCalculatedSizeDC()
	 */
	public double getMaxLengthDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthDC;
	}

	/**
	 Returns the actual DC voltage drop for the calculated conductor size, under
	 the given conditions.

	 @return The DC voltage drop in volts, of the calculated conductor. If the
	 returned value is zero, check the resultMessage field content.
	 @see #getCalculatedSizeDC()
	 */
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
}