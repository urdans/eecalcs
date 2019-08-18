package eecalcs.conductors;

import eecalcs.conductors.MetalCond;

public class CopperCond extends MetalCond {
	private double resDCUncoated;
	private double resDCCoated;

	public CopperCond(int amp60, int amp75, int amp90, double resInPVCCond, double resInALCond, double resInSteelCond,
	                  double cuResDCUncoated, double cuResDCCoated) {
		super(amp60, amp75, amp90, resInPVCCond, resInALCond, resInSteelCond);
		resDCUncoated = cuResDCUncoated;
		resDCCoated = cuResDCCoated;
	}

	public double getDCResistance(Boolean isCoated) {
		if(isCoated) return resDCCoated;
		return resDCUncoated;
	}

	public double getDCResistance(Boolean isCoated, double oneWayLength, int numberOfSets) {
		return getDCResistance(isCoated) * 0.001 * oneWayLength / numberOfSets;
	}
}
