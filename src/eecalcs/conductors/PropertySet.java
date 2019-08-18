/*This class encapsulates conductor properties defined in table 310.15(B)(16) and in tables 8 & 9*/
package eecalcs.conductors;

public class PropertySet {
	private String size;
	private int areaCM;
	private double nonMagXL;
	private double magXL;
	private CopperCond copperCond;
	private AluminumCond aluminumCond;

	public PropertySet(String size, int CuAmp60, int CuAmp75, int CuAmp90, int AlAmp60, int AlAmp75, int AlAmp90, double nonMagXL,
	                   double magXL, double CuResInPVCCond, double CuResInALCond, double CuResInSteelCond, double ALResInPVCCond,
	                   double ALResInALCond, double ALResInSteelCond, int areaCM, double CuResDCUncoated, double CuResDCCoated,
	                   double ALResDC){
		this.size = size;
		this.areaCM = areaCM;
		this.nonMagXL = nonMagXL;
		this.magXL = magXL;
		copperCond = new CopperCond(CuAmp60, CuAmp75, CuAmp90, CuResInPVCCond, CuResInALCond, CuResInSteelCond,
				CuResDCUncoated, CuResDCCoated);
		aluminumCond = new AluminumCond(AlAmp60, AlAmp75, AlAmp90, ALResInPVCCond, ALResInALCond, ALResInSteelCond, ALResDC);
	}

	public double getReactance(boolean magneticConduit){
		if(magneticConduit) return magXL;
		return nonMagXL;
	}

	public double getReactance(boolean magneticConduit, double oneWayLength, int numberOfSets){
		return getReactance(magneticConduit) * 0.001 * oneWayLength / numberOfSets;
	}

	public int getAreaCM() {
		return areaCM;
	}

	public CopperCond forCopper(){
		return copperCond;
	}

	public AluminumCond forAluminum(){
		return aluminumCond;
	}

	public MetalCond byMetal(Metal metal){
		if(metal == Metal.COPPER) return copperCond;
		return aluminumCond;
	}

	public String getSize() {
		return size;
	}

	public String getFullSizeName() {
		return CondProp.getFullSizeName(size);
	}

	public boolean isInvalid(){
		return this == CondProp.getInvalidPropertySet();
	}

	public double getInsulatedAreaIn2(String insulationName){
		return CondProp.getInsulatedAreaIn2(size, insulationName);
	}

	public double getCompactAreaIn2(String insulationName){
		return CondProp.getCompactAreaIn2(size, insulationName);
	}

	public double getCompactBareAreaIn2(){
		return CondProp.getCompactBareAreaIn2(size);
	}

	public boolean hasInsulatedArea(String insulationName){
		return CondProp.hasInsulatedArea(size, insulationName);
	}

	public boolean hasCompactArea(String conductorSize, String insulationName){
		return CondProp.hasCompactArea(size, insulationName);
	}

	public boolean hasCompactBareArea(){
		return CondProp.hasCompactBareArea(size);
	}

}

