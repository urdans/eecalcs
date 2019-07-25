package eecalcs;

import org.apache.commons.math3.complex.Complex;

public class VoltageDrop {

	//region private fields
	private boolean mustRecalculate = true;
	//input
	private double sourceVoltage = 120;
	private int phases = 1;
	private Conductor conductor = Conductor.getConductorBySize("12");
	private Conduit.Material conduitMaterial = Conduit.Material.PVC;
	private Conductor.Metal conductorMetal = Conductor.Metal.COPPER;
	private int numberOfSets = 1;
	private double oneWayLength = 100;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private Conductor.CopperCoating copperCoating = Conductor.CopperCoating.UNCOATED;
	//output variables
	private double voltageAtLoadAC;
//	private double voltageAtLoadDC;
	//endregion

	//region public fields
	public ResultMessages resultMessages = new ResultMessages();
	//endregion

	//region setters
	public void setSourceVoltage(double sourceVoltage) {
		if(this.sourceVoltage != sourceVoltage) {
			this.sourceVoltage = sourceVoltage;
			mustRecalculate = true;
		}
		if(sourceVoltage < 0){
			mustRecalculate = false;
			resultMessages.add("Source voltage must be greater that zero.",-1);
		}else{
			resultMessages.remove(-1);
		}
	}

	public void setPhases(int phases) {
		if(this.phases != phases) {
			this.phases = phases;
			mustRecalculate = true;
		}
		if(!(phases == 1 || phases == 3)){
			mustRecalculate = false;
			resultMessages.add("Number of phases must be 1 or 3.",-2);
		}else{
			resultMessages.remove(-2);
		}
	}

	public void setConductorSize(String conductorSize) {
		if(!conductor.Size.equals(conductorSize)){
			if(Conductor.isValidSize(conductorSize)){
				conductor = Conductor.getConductorBySize(conductorSize);
				mustRecalculate = true;
				resultMessages.remove(-3);
			}else{
				mustRecalculate = false;
				conductor = Conductor.getInvalidConductor();
				resultMessages.add("\"" + conductorSize + "\"" +" is not a valid conductor size.",-3);
			}
		}
		checkSets();
		checkAmpacity();
	}

	public void setConduitMaterial(Conduit.Material conduitMaterial) {
		if(this.conduitMaterial != conduitMaterial){
			this.conduitMaterial = conduitMaterial;
			mustRecalculate = true;
		}
	}

	public void setConductorMetal(Conductor.Metal conductorMetal) {
		if(this.conductorMetal != conductorMetal) {
			this.conductorMetal = conductorMetal;
			mustRecalculate = true;
		}
	}

	public void setNumberOfSets(int numberOfSets) {
		if(this.numberOfSets != numberOfSets) {
			this.numberOfSets = numberOfSets;
			mustRecalculate = true;
		}
		if(numberOfSets < 1 || numberOfSets > 10){
			mustRecalculate = false;
			resultMessages.add("Number of sets must be between 1 and 10.",-4);
		}else{
			resultMessages.remove(-4);
		}
		checkSets();
		checkAmpacity();
	}

	public void setOneWayLength(double oneWayLength) {
		if(this.oneWayLength != oneWayLength) {
			this.oneWayLength = oneWayLength;
			mustRecalculate = true;
		}
		if(oneWayLength <= 0){
			mustRecalculate = false;
			resultMessages.add("One way conductor length must be greater than 0.",-5);
		}else{
			resultMessages.remove(-5);
		}
	}

	public void setLoadCurrent(double loadCurrent) {
		if(this.loadCurrent != loadCurrent) {
			this.loadCurrent = loadCurrent;
			mustRecalculate = true;
		}
		if(loadCurrent <= 0){
			mustRecalculate = false;
			resultMessages.add("Load current must be greater than 0.",-6);
		}else{
			resultMessages.remove(-6);
		}
		checkSets();
		checkAmpacity();
	}

	public void setPowerFactor(double powerFactor) {
		if(this.powerFactor != powerFactor) {
			this.powerFactor = powerFactor;
			mustRecalculate = true;
		}
		if(powerFactor < 0.7 || powerFactor > 1.0){
			mustRecalculate = false;
			resultMessages.add("Power factor must be between 0.7 and 1.",-7);
		}else{
			resultMessages.remove(-7);
		}
	}
	//endregion

	//region getters
	public double getSourceVoltage() {
		return sourceVoltage;
	}

	public int getPhases() {
		return phases;
	}

	public Conductor getConductor() {
		return conductor;
	}

	public Conduit.Material getConduitMaterial() {
		return conduitMaterial;
	}

	public Conductor.Metal getConductorMetal() {
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

	public Conductor.CopperCoating getCopperCoating() {
		return copperCoating;
	}

	//region AC voltage drop output
	public double getVoltageAtLoadAC() {
		if(resultMessages.hasErrors()) return 0;
		if(mustRecalculate){
			voltageAtLoadAC = computeVoltageAtLoadAC(conductor);
			mustRecalculate = false;
		}
		return voltageAtLoadAC;
	}

	public double getVoltageDropVoltsAC() {
		if(resultMessages.hasErrors()) return 0;
		return sourceVoltage - getVoltageAtLoadAC();
	}

	public double getVoltageDropPercentageAC() {
		if(resultMessages.hasErrors()) return 0;
		return 100 * getVoltageDropVoltsAC() / sourceVoltage;
	}
	//endregion voltage drop output

	//region DC voltage drop output
	public double getVoltageAtLoadDC() {
		if(resultMessages.hasErrors()) return 0;
		return computeVoltageAtLoadDC(conductor);
	}

	public double getVoltageDropVoltsDC() {
		if(resultMessages.hasErrors()) return 0;
		return sourceVoltage - getVoltageAtLoadDC();
	}

	public double getVoltageDropPercentageDC() {
		if(resultMessages.hasErrors()) return 0;
		return 100 * getVoltageDropVoltsDC() / sourceVoltage;
	}
	//endregion voltage drop output
	//endregion

	//region private methods
	private double computeVoltageAtLoadAC(Conductor aConductor){
		double k = getK();
		double oneWayACResistance = getOneWayACConductorResistance(aConductor);
		double oneWayConductorReactance = getOneWayConductorReactance(aConductor);
		Complex totalConductorImpedanceComplex = new Complex(k * oneWayACResistance, k * oneWayConductorReactance);
		Complex sourceVoltageComplex = new Complex(sourceVoltage, 0);
		Complex loadCurrentComplex = new Complex(loadCurrent * powerFactor, -loadCurrent * Math.sin(Math.acos(powerFactor)));
		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);
		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);
		return voltageAtLoadComplex.abs();
	}

	private double getOneWayACConductorResistance(Conductor aConductor){
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
	}

	private double getOneWayConductorReactance(Conductor aConductor){
		double reactance;
		if (conduitMaterial == Conduit.Material.PVC || conduitMaterial == Conduit.Material.ALUMINUM) {
			reactance = aConductor.reactance.inNonMagCond;
		} else {
			reactance = aConductor.reactance.inMagCond;
		}
		reactance = reactance * oneWayLength * 0.001 / numberOfSets;
		return reactance;
	}

	private double getK(){
		if(phases == 1) return 2;
		else return Math.sqrt(3);
	}

	private double computeVoltageAtLoadDC(Conductor aConductor){
		double voltageAtLoadDC = sourceVoltage - 2 * getOneWayDCConductorResistance(aConductor) * loadCurrent;
		return voltageAtLoadDC;
	}

	private double getOneWayDCConductorResistance(Conductor aConductor){
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
	}

	private void checkSets(){
		if(numberOfSets > 1 && Conductor.compareSizes(conductor.Size,"1/0") < 0) {
			mustRecalculate = false;
			resultMessages.add("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310.10(H)(1)", -9);
		}else{
			resultMessages.remove(-9);
		}
	}

	private void checkAmpacity(){
		if(getMaxSetAmpacity() < loadCurrent) {
			mustRecalculate = false;
			resultMessages.add("Load current exceeds maximum allowed conductor set's ampacity at 90°C. NEC-310.15(B)(16)",-8);
		}else{
			resultMessages.remove(-8);
		}
	}

	private double getMaxSetAmpacity(){
		if(conductorMetal == Conductor.Metal.COPPER)
			return conductor.copper.ampacity.t90 * numberOfSets;
		else
			return conductor.aluminum.ampacity.t90 * numberOfSets;
	}
	//endregion
}
