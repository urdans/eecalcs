package eecalcs.voltagedrop;

import eecalcs.conductors.*;
import eecalcs.conduits.Magnetic;
import eecalcs.conduits.Material;
import org.apache.commons.math3.complex.Complex;
import tools.Message;
import tools.ResultMessages;

public class VDrop {
	//region private fields
	//input
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
	private static Message ERROR31	= new Message("No building conductor can achieve that voltage drop under these conditions.", -31);
	private static Message WARNN21	= new Message(ERROR21.message,21);
	//calculated
	private double actualVoltageDropPercentageAC;
	private double maxLengthAC;
	private double actualVoltageDropPercentageDC;
	private double maxLengthDC;
	//endregion

	//region constructor
	public VDrop(Circuit circuit){
		this.circuit = new Circuit(circuit);
	}
	//endregion

	//region public fields
	public ResultMessages resultMessages = new ResultMessages();
	//endregion

	//region setters
	public void setSourceVoltage(double sourceVoltage) {
		this.sourceVoltage = sourceVoltage;
	}

	public void setConductorSize(String conductorSize) {
		circuit.setSize(conductorSize);
	}

	public void setPhases(int phases) {
		this.phases = phases;
	}

	public void setConduitType(Material conduitType) {
		circuit.setConduitType(conduitType);
	}

	public void setMetal(Metal metal) {
		circuit.setMetal(metal);
	}

	public void setNumberOfSets(int numberOfSets) {
		circuit.setNumberOfSets(numberOfSets);
	}

	public void setOneWayLength(double oneWayLength) {
		circuit.setLength(oneWayLength);
	}

	public void setLoadCurrent(double loadCurrent) {
		this.loadCurrent = loadCurrent;
	}

	public void setPowerFactor(double powerFactor) {
		this.powerFactor = powerFactor;
	}

	public void setCopperCoating(boolean copperCoating) {
		circuit.setCopperCoated(copperCoating);
	}

	public void setMaxVoltageDropPercent(double maxVoltageDropPercent) {
		this.maxVoltageDropPercent = maxVoltageDropPercent;
	}
	//endregion

	//region getters
	//region input fields
	public double getSourceVoltage() {
		return sourceVoltage;
	}

	public String getSize() {
		return circuit.getSize();
	}

	public int getPhases() {
		return phases;
	}

	public Material getConduitType() {
		return circuit.getConduitType();
	}

	public Metal getMetal() {
		return circuit.getMetal();
	}

	public int getNumberOfSets() {
		return circuit.getNumberOfSets();
	}

	public double getOneWayLength() {
		return circuit.getLength();
	}

	public double getLoadCurrent() {
		return loadCurrent;
	}

	public double getPowerFactor() {
		return powerFactor;
	}

	public boolean isCoated() {
		return circuit.isCopperCoated();
	}

	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}
	//endregion input fields

	//region AC voltage drop output
	public double getVoltageAtLoadAC() {
		if(checkACVDInput())
			return computeVoltageAtLoadAC(circuit.getSize());
		return 0;
	}

	public double getVoltageDropVoltsAC() {
		if(checkACVDInput())
			return sourceVoltage - getVoltageAtLoadAC();
		return 0;
	}

	public double getVoltageDropPercentageAC() {
		if(checkACVDInput())
			return 100 * getVoltageDropVoltsAC() / sourceVoltage;
		return 0;
	}
	//endregion voltage drop output

	//region DC voltage drop output
	public double getVoltageAtLoadDC() {
		if(checkDCVDInput())
			return computeVoltageAtLoadDC(circuit.getSize());
		return 0;
	}

	public double getVoltageDropVoltsDC() {
		if(checkDCVDInput())
			return sourceVoltage - getVoltageAtLoadDC();
		return 0;
	}

	public double getVoltageDropPercentageDC() {
		if(checkDCVDInput())
			return 100 * getVoltageDropVoltsDC() / sourceVoltage;
		return 0;
	}
	//endregion voltage drop output

	//region Conductor sizing per ACVD
	public String getCalculatedSizeAC(){
		if(checkACCSInput())
			return computeSizeAC();
		return "";
	}

	public double getActualVoltageDropPercentageAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageAC;
	}

	public double getMaxLengthAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthAC;
	}
	//endregion

	//region Conductor sizing per DCVD
	public PropertySet getCalculatedSizeDC(){
		if(checkDCCSInput())
			return computeSizeDC();
		return CondProp.getInvalidPropertySet();
	}

	public double getActualVoltageDropPercentageDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return actualVoltageDropPercentageDC;
	}

	public double getMaxLengthDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors())
			return 0;
		return maxLengthDC;
	}
	//endregion

	//region private methods
	//region COMMON
	private double getSetMaxAmpacity(){
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
		if(CondProp.isValidSize(circuit.getSize())){
			if(!resultMessages.containsMessage(ERROR04)) {
				if (circuit.getNumberOfSets() > 1 && CondProp.compareSizes(circuit.getSize(), "1/0") < 0)
					resultMessages.add(ERROR21);
			}
			if(!resultMessages.containsMessage(ERROR06)) {
				if (getSetMaxAmpacity() < loadCurrent)
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
		PropertySet propertySet = CondProp.bySize(conductorSize);
		double k = getK();
		double oneWayACResistance = propertySet.byMetal(circuit.getMetal()).getACResistance(circuit.getConduitType(),
				circuit.getLength(), circuit.getNumberOfSets());
		double oneWayConductorReactance = propertySet.getReactance(Magnetic.isMagnetic(circuit.getConduitType()),
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
		if(CondProp.isValidSize(circuit.getSize())){
			if(!resultMessages.containsMessage(ERROR04)) {
				if (circuit.getNumberOfSets() > 1 && CondProp.compareSizes(circuit.getSize(), "1/0") < 0)
					resultMessages.add(ERROR21);
			}
			if(!resultMessages.containsMessage(ERROR06)) {
				if (getSetMaxAmpacity() < loadCurrent)
					resultMessages.add(ERROR20);
			}
		}
		else
			resultMessages.add(ERROR03);
		return !resultMessages.hasErrors();
	}

	private double computeVoltageAtLoadDC(String conductorSize){
		PropertySet propertySet = CondProp.bySize(conductorSize);
		double oneWayDCResistance;
		if(circuit.getMetal() == Metal.COPPER)
			oneWayDCResistance = propertySet.forCopper().getDCResistance(circuit.isCopperCoated(), circuit.getLength(),
					circuit.getNumberOfSets());
		else
			oneWayDCResistance = propertySet.forAluminum().getDCResistance(circuit.getLength(), circuit.getNumberOfSets());
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
		for(String s : CondProp.getSizes()){
			actualVoltageDropPercentageAC = 100 * (sourceVoltage - computeVoltageAtLoadAC(s)) / sourceVoltage;
			if(actualVoltageDropPercentageAC <= maxVoltageDropPercent){
				maxLengthAC = computeMaxLengthAC(s);
				if(maxLengthAC <= 0) {
					resultMessages.add(ERROR30);
					return "";
				}
				if(circuit.getNumberOfSets() > 1 && CondProp.compareSizes(s,"1/0") < 0)
					resultMessages.add(WARNN21);
				return s;
			}
		}
		resultMessages.add(ERROR31);
		return "";
	}

	private double computeMaxLengthAC(String conductorSize){
		PropertySet propertySet = CondProp.bySize(conductorSize);
		double conductorR = propertySet.byMetal(circuit.getMetal()).getACResistance(circuit.getConduitType()) * 0.001 / circuit.getNumberOfSets();
		double conductorX = propertySet.getReactance(Magnetic.isMagnetic(circuit.getConduitType())) *0.001 / circuit.getNumberOfSets();
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

	private PropertySet computeSizeDC(){
		for(String s : CondProp.getSizes()){
			PropertySet _conductorProperties = CondProp.bySize(s);
			actualVoltageDropPercentageDC = 100 * (sourceVoltage - computeVoltageAtLoadDC(s)) / sourceVoltage;
			if(actualVoltageDropPercentageDC <= maxVoltageDropPercent){
				maxLengthDC = computeMaxLengthDC(_conductorProperties);
				if(maxLengthDC <= 0) {
					resultMessages.add(ERROR30);
					return CondProp.getInvalidPropertySet();
				}
				if(circuit.getNumberOfSets() > 1 && CondProp.compareSizes(s,"1/0") < 0)
					resultMessages.add(WARNN21);
				return _conductorProperties;
			}
		}
		resultMessages.add(ERROR31);
		return CondProp.getInvalidPropertySet();
	}

	private double computeMaxLengthDC(PropertySet _conductorProperties){
		double dCResistance;
		if(circuit.getMetal() == Metal.COPPER)
			dCResistance = _conductorProperties.forCopper().getDCResistance(circuit.isCopperCoated(), circuit.getLength(),
				circuit.getNumberOfSets());
		else
			dCResistance = _conductorProperties.forAluminum().getDCResistance(circuit.getLength(), circuit.getNumberOfSets());

		return sourceVoltage * maxVoltageDropPercent * circuit.getLength() / (200 * loadCurrent * dCResistance);
	}
	//endregion
	//endregion
	//endregion
}