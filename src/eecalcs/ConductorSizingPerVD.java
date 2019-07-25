package eecalcs;

import org.apache.commons.math3.complex.Complex;

public class ConductorSizingPerVD {

	//region private fields
	private boolean mustRecalculate = true;
	//input
	private double sourceVoltage = 120;
	private int phases = 1;
	private Conductor conductor = Conductor.getConductorBySize("12");
	private Conduit.Material conductorMaterial = Conduit.Material.PVC;
	private Conductor.Metal conductorMetal = Conductor.Metal.COPPER;
	private int numberOfSets = 1;
	private double oneWayLength = 100;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private Conductor.CopperCoating copperCoating = Conductor.CopperCoating.UNCOATED;
	private double maxVoltageDropPercent = 3; //default value
	//output
	//AC
	private Conductor conductorForMaxVoltageDropAC;
	private double maxLengthAC = 0;
	private double actualVoltageDropPercentageAC;
	//DC
	private Conductor conductorForMaxVoltageDropDC;
	private double maxLengthDC = 0;
	private double actualVoltageDropPercentageDC;
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

	public void setConductorMaterial(Conduit.Material conductorMaterial) {
		if(this.conductorMaterial != conductorMaterial){
			this.conductorMaterial = conductorMaterial;
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

	public void setMaxVoltageDropPercent(double maxVoltageDropPercent){
		if(this.maxVoltageDropPercent != maxVoltageDropPercent) {
			this.maxVoltageDropPercent = maxVoltageDropPercent;
			mustRecalculate = true;
		}
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25){
			mustRecalculate = false;
			resultMessages.add("Voltage drop for determining conductor sizing must be between 0.5% and 25%",-12);
		}else{
			resultMessages.remove(-12);
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

	public Conduit.Material getConductorMaterial() {
		return conductorMaterial;
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

	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}

	//Calculated values
	//AC
	public Conductor getCalculatedSizeAC(double maxVoltageDropPercent){
		setMaxVoltageDropPercent(maxVoltageDropPercent);
		return getCalculatedSizeAC();
	}

	public Conductor getCalculatedSizeAC(){
		if(resultMessages.hasErrors()) return Conductor.getInvalidConductor();
		checkRequirements();
		return conductorForMaxVoltageDropAC;
	}

	public double getActualVoltageDropPercentageAC(){
		if(resultMessages.hasErrors()) return 0;
		checkRequirements();
		return actualVoltageDropPercentageAC;
	}

	public double getMaxLengthAC() {
		if(resultMessages.hasErrors()) return 0;
		checkRequirements();
		return maxLengthAC;
	}

	//DC
	public Conductor getCalculatedSizeDC(double maxVoltageDropPercent){
		setMaxVoltageDropPercent(maxVoltageDropPercent);
		return getCalculatedSizeDC();
	}

	public Conductor getCalculatedSizeDC(){
		if(resultMessages.hasErrors()) return Conductor.getInvalidConductor();
		return conductorForMaxVoltageDropDC;
	}

	public double getActualVoltageDropPercentageDC(){
		if(resultMessages.hasErrors()) return 0;
//		checkRequirements();
		return actualVoltageDropPercentageDC;
	}

	public double getMaxLengthDC() {
		if(resultMessages.hasErrors()) return 0;
		computeSizeDC();
		return maxLengthDC;
	}
	//endregion

	//region private methods
	private Conductor computeSizeAC(){
		for(Conductor _conductor: Conductor.getTable()){
			actualVoltageDropPercentageAC = 100 * (sourceVoltage - computeVoltageAtLoadAC(_conductor)) / sourceVoltage;

			if(actualVoltageDropPercentageAC <= maxVoltageDropPercent){
				maxLengthAC = computeMaxLength(_conductor);
				if(maxLengthAC <= 0) {
					resultMessages.add("No length can achieve that voltage drop under the given conditions.", -10);
					return Conductor.getInvalidConductor();
				}else{
					conductorForMaxVoltageDropAC = _conductor;
					resultMessages.remove(-10);
					resultMessages.remove(-11);
					if(numberOfSets > 1 && Conductor.compareSizes(conductorForMaxVoltageDropAC.Size,"1/0") < 0) {
						resultMessages.add("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310.10" +
								"(H)(1)", 1);
					}else{
						resultMessages.remove(1);
					}
					return conductorForMaxVoltageDropAC;
				}
			}
		}
		resultMessages.add("No building conductor can achieve that voltage drop under these conditions.", -11);
		return Conductor.getInvalidConductor();
	}

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
			if (conductorMaterial == Conduit.Material.PVC) {
				resistance = aConductor.copper.resistance.ac.inPVCCond;
			} else if (conductorMaterial == Conduit.Material.ALUMINUM) {
				resistance = aConductor.copper.resistance.ac.inALCond;
			} else {
				resistance = aConductor.copper.resistance.ac.inSteelCond;
			}
		}else{ //conductor is aluminum
			if (conductorMaterial == Conduit.Material.PVC) {
				resistance = aConductor.aluminum.resistance.ac.inPVCCond;
			} else if (conductorMaterial == Conduit.Material.ALUMINUM) {
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
		if (conductorMaterial == Conduit.Material.PVC || conductorMaterial == Conduit.Material.ALUMINUM) {
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

	private double computeMaxLength(Conductor _conductor){
		double conductorR = (getOneWayACConductorResistance(_conductor) / oneWayLength);
		double conductorX = (getOneWayConductorReactance(_conductor) / oneWayLength);
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

	private void checkRequirements() {
		if (mustRecalculate) {
			mustRecalculate = false;
			computeSizeAC();
		}
	}

	//DC
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

	private Conductor computeSizeDC(){
		for(Conductor _conductor: Conductor.getTable()){
			actualVoltageDropPercentageDC = 100 * (sourceVoltage - computeVoltageAtLoadDC(_conductor)) / sourceVoltage;

			if(actualVoltageDropPercentageDC <= maxVoltageDropPercent){
				maxLengthDC = sourceVoltage * actualVoltageDropPercentageDC
						* (200*loadCurrent*getOneWayDCConductorResistance(_conductor)/oneWayLength);
				if(maxLengthDC <= 0) {
					resultMessages.add("No length can achieve that voltage drop under the given conditions.", -10);
					return Conductor.getInvalidConductor();
				}else{
					conductorForMaxVoltageDropDC = _conductor;
					resultMessages.remove(-10);
					resultMessages.remove(-11);
					if(numberOfSets > 1 && Conductor.compareSizes(conductorForMaxVoltageDropDC.Size,"1/0") < 0) {
						resultMessages.add("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310.10" +
								"(H)(1)", 1);
					}else{
						resultMessages.remove(1);
					}
					return conductorForMaxVoltageDropDC;
				}
			}
		}
		resultMessages.add("No building conductor can achieve that voltage drop under these conditions.", -11);
		return Conductor.getInvalidConductor();
	}

	private double computeVoltageAtLoadDC(Conductor aConductor){
		double voltageAtLoadDC = sourceVoltage - 2 * getOneWayDCConductorResistance(aConductor) * loadCurrent;
		return voltageAtLoadDC;
	}
}
