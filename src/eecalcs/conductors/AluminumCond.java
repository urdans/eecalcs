package eecalcs.conductors;

public class AluminumCond extends MetalCond{
	private double resDC;

	public AluminumCond(int amp60, int amp75, int amp90, double resInPVCCond, double resInALCond, double resInSteelCond, double resDC) {
		super(amp60, amp75, amp90, resInPVCCond, resInALCond, resInSteelCond);
		this.resDC = resDC;
	}

	public double getDCResistance() {
		return resDC;
	}

	public double getDCResistance(double oneWayLength, int numberOfSets) {
		return getDCResistance() * 0.001 * oneWayLength / numberOfSets;
	}
}
