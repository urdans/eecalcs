package eecalcs.voltagedrop;

import eecalcs.conductors.Coating;
import eecalcs.conductors.Metal;
import eecalcs.conduits.Material;
import eecalcs.unused.ConductorProperties;
import eecalcs.conduits.Conduit;
import tools.Message;
import tools.ResultMessages;
import org.apache.commons.math3.complex.Complex;

public class VDropCalculator {

	//region private fields
	//input
	private double sourceVoltage = 120;
	private String size = "12";
	private int phases = 1;
	private Material conduitMaterial = Material.PVC;
	private Metal conductorMetal = Metal.COPPER;
	private int numberOfSets = 1;
	private double oneWayLength = 100;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private boolean copperCoating = Coating.UNCOATED;
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
	private ConductorProperties conductorProperties = ConductorProperties.getConductorBySize(size);
	private double actualVoltageDropPercentageAC;
	private double maxLengthAC;
	private double actualVoltageDropPercentageDC;
	private double maxLengthDC;
	//endregion

	//region public fields
	public ResultMessages resultMessages = new ResultMessages();
	//endregion

	//region setters
	public void setSourceVoltage(double sourceVoltage) {
		this.sourceVoltage = sourceVoltage;
	}

	public void setConductorSize(String conductorSize) {
		size = conductorSize;
	}

	public void setPhases(int phases) {
		this.phases = phases;
	}

	public void setConduitMaterial(Material conduitMaterial) {
		this.conduitMaterial = conduitMaterial;
	}

	public void setConductorMetal(Metal conductorMetal) {
		this.conductorMetal = conductorMetal;
	}

	public void setNumberOfSets(int numberOfSets) {
		this.numberOfSets = numberOfSets;
	}

	public void setOneWayLength(double oneWayLength) {
		this.oneWayLength = oneWayLength;
	}

	public void setLoadCurrent(double loadCurrent) {
		this.loadCurrent = loadCurrent;
	}

	public void setPowerFactor(double powerFactor) {
		this.powerFactor = powerFactor;
	}

	public void setCopperCoating(boolean copperCoating) {
		this.copperCoating = copperCoating;
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
		return size;
	}

	public int getPhases() {
		return phases;
	}

	public Material getConduitMaterial() {
		return conduitMaterial;
	}

	public Metal getConductorMetal() {
		return conductorMetal;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}

	public double getOneWayLength() {
		return oneWayLength;
	}

	public double getLoadCurrent() {
		return loadCurrent;
	}

	public double getPowerFactor() {
		return powerFactor;
	}

	public boolean isCoated() {
		return copperCoating;
	}

	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}
	//endregion input fields

	//region AC voltage drop output
	public double getVoltageAtLoadAC() {
		if(checkACVDInput()) return computeVoltageAtLoadAC(size);
		return 0;
	}

	public double getVoltageDropVoltsAC() {
		if(checkACVDInput()) return sourceVoltage - getVoltageAtLoadAC();
		return 0;
	}

	public double getVoltageDropPercentageAC() {
		if(checkACVDInput()) return 100 * getVoltageDropVoltsAC() / sourceVoltage;
		return 0;
	}
	//endregion voltage drop output

	//region DC voltage drop output
	public double getVoltageAtLoadDC() {
		if(checkDCVDInput()) return computeVoltageAtLoadDC(size);
		return 0;
	}

	public double getVoltageDropVoltsDC() {
		if(checkDCVDInput()) return sourceVoltage - getVoltageAtLoadDC();
		return 0;
	}

	public double getVoltageDropPercentageDC() {
		if(checkDCVDInput()) return 100 * getVoltageDropVoltsDC() / sourceVoltage;
		return 0;
	}
	//endregion voltage drop output

	//region Conductor sizing per ACVD
	public ConductorProperties getCalculatedSizeAC(double maxVoltageDropPercent){
		setMaxVoltageDropPercent(maxVoltageDropPercent);
		return getCalculatedSizeAC();
	}

	public ConductorProperties getCalculatedSizeAC(){
		if(checkACCSInput()) return computeSizeAC();
		return ConductorProperties.getInvalidConductorProperties();
	}

	public double getActualVoltageDropPercentageAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors()) return 0;
		return actualVoltageDropPercentageAC;
	}

	public double getMaxLengthAC(){
		getCalculatedSizeAC();
		if(resultMessages.hasErrors()) return 0;
		return maxLengthAC;
	}
	//endregion

	//region Conductor sizing per DCVD
	public ConductorProperties getCalculatedSizeDC(double maxVoltageDropPercent){
		setMaxVoltageDropPercent(maxVoltageDropPercent);
		return getCalculatedSizeDC();
	}

	public ConductorProperties getCalculatedSizeDC(){
		if(checkDCCSInput()) return computeSizeDC();
		return ConductorProperties.getInvalidConductorProperties();
	}

	public double getActualVoltageDropPercentageDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors()) return 0;
		return actualVoltageDropPercentageDC;
	}

	public double getMaxLengthDC(){
		getCalculatedSizeDC();
		if(resultMessages.hasErrors()) return 0;
		return maxLengthDC;
	}
	//endregion

	//region private methods
	//region COMMON
	private double getMaxSetAmpacity(){
		if(conductorMetal == Metal.COPPER)
			return conductorProperties.copper.ampacity.t90 * numberOfSets;
		else
			return conductorProperties.aluminum.ampacity.t90 * numberOfSets;
	}
	//endregion

	//region ACVD
	private boolean checkACVDInput(){
		resultMessages.clearMessages();
		if(sourceVoltage <=0 ) resultMessages.add(ERROR01);
		if(!(phases == 1 || phases == 3)) resultMessages.add(ERROR02);
		if(numberOfSets < 1 || numberOfSets > 10) resultMessages.add(ERROR04);
		if(oneWayLength <= 0) resultMessages.add(ERROR05);
		if(loadCurrent <= 0) resultMessages.add(ERROR06);
		if(ConductorProperties.isValidSize(size)){
			conductorProperties = ConductorProperties.getConductorBySize(size);
			if(!resultMessages.containsMessage(ERROR04)) {
				if (numberOfSets > 1 && ConductorProperties.compareSizes(conductorProperties.Size, "1/0") < 0) resultMessages.add(ERROR21);
			}
			if(!resultMessages.containsMessage(ERROR06)) {
				if (getMaxSetAmpacity() < loadCurrent) resultMessages.add(ERROR20);
			}
		}
		else
			resultMessages.add(ERROR03);
		if(powerFactor < 0.7 || powerFactor > 1.0) resultMessages.add(ERROR07);
		return !resultMessages.hasErrors();
	}

	private double computeVoltageAtLoadAC(String conductorSize){
		return computeVoltageAtLoadAC(ConductorProperties.getConductorBySize(conductorSize));
	}

	private double computeVoltageAtLoadAC(ConductorProperties aConductorProperties){
		double k = getK();
		double oneWayACResistance = aConductorProperties.getACConductorResistance(conductorMetal, conduitMaterial, oneWayLength, numberOfSets);
		//getOneWayACConductorResistance(aConductor);
		double oneWayConductorReactance = getOneWayConductorReactance(aConductorProperties);
		Complex totalConductorImpedanceComplex = new Complex(k * oneWayACResistance, k * oneWayConductorReactance);
		Complex sourceVoltageComplex = new Complex(sourceVoltage, 0);
		Complex loadCurrentComplex = new Complex(loadCurrent * powerFactor, -loadCurrent * Math.sin(Math.acos(powerFactor)));
		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);
		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);
		return voltageAtLoadComplex.abs();
	}

/*	private double getOneWayACConductorResistance(Conductor aConductor){
		//region compute total conductor resistance
		double resistance;
		if(conductorMetal == Conductor.Metal.COPPER){
			if (conduitMaterial == Conduit.Material.PVC) {
				resistance = aConductor.copper.resistance.ac.inPVCCond;
			} else if (conduitMaterial == Conduit.Material.ALUMINUM) {
				resistance = aConductor.copper.resistance.ac.inALCond;
			} else {
				resistance = aConductor.copper.resistance.ac.inSteelCond;
			}
		}else{ //conductor is aluminum
			if (conduitMaterial == Conduit.Material.PVC) {
				resistance = aConductor.aluminum.resistance.ac.inPVCCond;
			} else if (conduitMaterial == Conduit.Material.ALUMINUM) {
				resistance = aConductor.aluminum.resistance.ac.inALCond;
			} else {
				resistance = aConductor.aluminum.resistance.ac.inSteelCond;
			}
		}
		resistance = resistance * oneWayLength * 0.001 / numberOfSets;
		return resistance;
	}*/

	private double getOneWayConductorReactance(ConductorProperties aConductorProperties){
		double reactance;
		if (conduitMaterial == Material.PVC || conduitMaterial == Material.ALUMINUM) {
			reactance = aConductorProperties.reactance.inNonMagCond;
		} else {
			reactance = aConductorProperties.reactance.inMagCond;
		}
		reactance = reactance * oneWayLength * 0.001 / numberOfSets;
		return reactance;
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
		if(numberOfSets < 1 || numberOfSets > 10) resultMessages.add(ERROR04);
		if(oneWayLength <= 0) resultMessages.add(ERROR05);
		if(loadCurrent <= 0) resultMessages.add(ERROR06);
		if(ConductorProperties.isValidSize(size)){
			conductorProperties = ConductorProperties.getConductorBySize(size);
			if(!resultMessages.containsMessage(ERROR04)) {
				if (numberOfSets > 1 && ConductorProperties.compareSizes(conductorProperties.Size, "1/0") < 0) resultMessages.add(ERROR21);
			}
			if(!resultMessages.containsMessage(ERROR06)) {
				if (getMaxSetAmpacity() < loadCurrent) resultMessages.add(ERROR20);
			}
		}
		else
			resultMessages.add(ERROR03);
		return !resultMessages.hasErrors();
	}

	private double computeVoltageAtLoadDC(String conductorSize){
		return computeVoltageAtLoadDC(ConductorProperties.getConductorBySize(conductorSize));
	}

	private double computeVoltageAtLoadDC(ConductorProperties aConductorProperties){
//		return sourceVoltage - 2 * getOneWayDCConductorResistance(aConductor) * loadCurrent;
		return sourceVoltage - 2 * aConductorProperties.getDCConductorResistance(conductorMetal, copperCoating, oneWayLength, numberOfSets) * loadCurrent;
	}

/*	private double getOneWayDCConductorResistance(Conductor aConductor){
		double resistance;

		if(conductorMetal == Conductor.Metal.COPPER){
			if (copperCoating == Conductor.CopperCoating.COATED)
				resistance = aConductor.copper.resistance.dc.coated;
			else
				resistance = aConductor.copper.resistance.dc.uncoated;
		}else
			resistance = aConductor.aluminum.resistance.dc;

		resistance = resistance * oneWayLength * 0.001 / numberOfSets;
		return resistance;
	}*/
	//endregion

	//region CS per ACVD
	private boolean checkACCSInput(){
		resultMessages.clearMessages();
		if(sourceVoltage <=0 ) resultMessages.add(ERROR01);
		if(!(phases == 1 || phases == 3)) resultMessages.add(ERROR02);
		if(numberOfSets < 1 || numberOfSets > 10) resultMessages.add(ERROR04);
		if(oneWayLength <= 0) resultMessages.add(ERROR05);
		if(loadCurrent <= 0) resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25) resultMessages.add(ERROR08);
		if(powerFactor < 0.7 || powerFactor > 1.0) resultMessages.add(ERROR07);
		return !resultMessages.hasErrors();
	}

	private ConductorProperties computeSizeAC(){
		for(ConductorProperties _conductorProperties : ConductorProperties.getTable()){
			actualVoltageDropPercentageAC = 100 * (sourceVoltage - computeVoltageAtLoadAC(_conductorProperties)) / sourceVoltage;

			if(actualVoltageDropPercentageAC <= maxVoltageDropPercent){
				maxLengthAC = computeMaxLengthAC(_conductorProperties);
				if(maxLengthAC <= 0) {
					resultMessages.add(ERROR30);
					return ConductorProperties.getInvalidConductorProperties();
				}
				if(numberOfSets > 1 && ConductorProperties.compareSizes(_conductorProperties.Size,"1/0") < 0) resultMessages.add(WARNN21);
				return _conductorProperties;
			}
		}
		resultMessages.add(ERROR31);
		return ConductorProperties.getInvalidConductorProperties();
	}

	private double computeMaxLengthAC(ConductorProperties _conductorProperties){
		double conductorR = _conductorProperties.getACConductorResistance(conductorMetal, conduitMaterial, oneWayLength, numberOfSets) / oneWayLength;
		//double conductorR = (getOneWayACConductorResistance(_conductor) / oneWayLength);
		double conductorX = (getOneWayConductorReactance(_conductorProperties) / oneWayLength);
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
		if(sourceVoltage <=0 ) resultMessages.add(ERROR01);
		if(numberOfSets < 1 || numberOfSets > 10) resultMessages.add(ERROR04);
		if(oneWayLength <= 0) resultMessages.add(ERROR05);
		if(loadCurrent <= 0) resultMessages.add(ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25) resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	private ConductorProperties computeSizeDC(){
		for(ConductorProperties _conductorProperties : ConductorProperties.getTable()){
			actualVoltageDropPercentageDC = 100 * (sourceVoltage - computeVoltageAtLoadDC(_conductorProperties)) / sourceVoltage;

			if(actualVoltageDropPercentageDC <= maxVoltageDropPercent){
				maxLengthDC = computeMaxLengthDC(_conductorProperties);
				if(maxLengthDC <= 0) {
					resultMessages.add(ERROR30);
					return ConductorProperties.getInvalidConductorProperties();
				}
				if(numberOfSets > 1 && ConductorProperties.compareSizes(_conductorProperties.Size,"1/0") < 0) resultMessages.add(WARNN21);
				return _conductorProperties;
			}
		}
		resultMessages.add(ERROR31);
		return ConductorProperties.getInvalidConductorProperties();
	}

	private double computeMaxLengthDC(ConductorProperties _conductorProperties){
		return sourceVoltage * maxVoltageDropPercent * oneWayLength / (200 * loadCurrent * _conductorProperties.getDCConductorResistance(conductorMetal, copperCoating, oneWayLength, numberOfSets));
	}
	//endregion
	//endregion
	//endregion
}
