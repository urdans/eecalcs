package eecalcs.loads;

import eecalcs.systems.VoltageSystemAC;

/**
 This class represents a general load which is any load not represented by any
 other class. Before using this load class, make sure that there is no other
 class that matches the behavior of the load you want to instantiate with this
 class.
 <p>This load does not require a OCPD device, a disconnect switch or an
 overload protection device and thus it will return a zero value when the
 rating of any of those devices is requested (this class does not override
 the corresponding rating-inquiring methods).
 */
public class GeneralLoad extends Load implements Continuousness{
	private final ContinuousBehavior continuousBehavior = new ContinuousBehavior(currents);

	/**
	 Constructs a general load with a default voltage system value of 120 volts,
	 1-phase 2-wires, nominal current and MCA of 10 amperes and non
	 continuous behavior.
	 */
	public GeneralLoad() {
		super();
		continuousBehavior.setNotifierDelegate(notifier);
	}

	/**
	 Constructs a general load with the given voltage system, the given
	 nominalCurrent value for the nominal current and MCA, and a non
	 continuous behavior.
	 */
	public GeneralLoad(VoltageSystemAC voltageSystemAC, double nominalCurrent){
		super(voltageSystemAC, nominalCurrent);
		continuousBehavior.setNotifierDelegate(notifier);
	}

	@Override
	public double getMCA() {
		return currents.MCA;
	}

	@Override
	public LoadType getLoadType() {
		return continuousBehavior.getLoadType();
	}

	@Override
	public void setContinuous() {
		continuousBehavior.setContinuous();
	}

	@Override
	public void setNonContinuous() {
		continuousBehavior.setNonContinuous();
	}

	@Override
	public void setMixed(double MCA) {
		continuousBehavior.setMixed(MCA);
	}

	/**
	 Sets a non-zero current value for this load.
	 Registered listeners receive notification of these changes.
	 If the load is mixed, setting its current will make it to become
	 noncontinuous.
	 @param nominalCurrent The new current of the load, in amperes. If this
	 value is zero, nothing is set.
	 */
	@Override
	public void setNominalCurrent(double nominalCurrent) {
		if(currents.nominalCurrent == nominalCurrent || nominalCurrent == 0)
			return;
		super.setNominalCurrent(nominalCurrent);
		continuousBehavior.updatedNominalCurrent();
	}
}
