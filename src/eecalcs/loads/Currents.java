package eecalcs.loads;

/**
 A struct like class holding values for a nominal current and an MCA. It is
 used by load objects for sharing these fields by using its container as a
 reference, between composition classes.
 */
class Currents {
	/** The nominal current of a load, in amperes */
	public double nominalCurrent;
	/** The Minimum Circuit Ampacity that defines the size of the conductor
	 feeding a load.*/
	public double MCA;
}
