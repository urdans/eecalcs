package eecalcs.conductors;

import eecalcs.conduits.Material;

public class MetalCond {
	protected int amp60;
	protected int amp75;
	protected int amp90;
	protected double resInPVCCond;
	protected double resInALCond;
	protected double resInSteelCond;

	public MetalCond(int amp60, int amp75, int amp90, double resInPVCCond, double resInALCond, double resInSteelCond) {
		this.amp60 = amp60;
		this.amp75 = amp75;
		this.amp90 = amp90;
		this.resInPVCCond = resInPVCCond;
		this.resInALCond = resInALCond;
		this.resInSteelCond = resInSteelCond;
	}

	public int getAmpacity(int temperature){
		if(temperature == 60) return amp60;
		if(temperature == 75) return amp75;
		if(temperature == 90) return amp90;
		return 0;
	}

	public double getACResistance(Material material){
		if(material == Material.PVC) return resInPVCCond;
		if(material == Material.ALUMINUM) return resInALCond;
		if(material == Material.STEEL) return resInSteelCond;
		return 0;
	}

	public double getACResistance(Material material, double oneWayLength, int numberOfSets){
		return getACResistance(material) * 0.001 * oneWayLength / numberOfSets;
	}
}
