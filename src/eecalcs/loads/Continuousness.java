package eecalcs.loads;

/**
 Describes the behaviors that a candidate load must have due to its
 continuousness.
 */
public interface Continuousness {
	/**
	 Describe the type of loads based on its continuousness. The purpose of
	 this classification is to define how the MCA is determined foe each case:
	 <br><p>
	 - <b>CONTINUOUS:</b> A load where the maximum current is expected to
	 continue for
	 3 hours or more. A continuous load has an MCA that is 1.25 times its
	 nominal current.<br><p>
	 - <b>NONCONTINUOUS:</b> A load that does not run continuously for 3 hours or
	 more. This type of load has an MCA equal to the nominal current.<br>
	 - <b>MIXED:</b> Applies to compound loads having continuous and
	 non-continuous subcomponent loads. The MCA value is not calculated by
	 the load, but it is provided by the user using the load setter. It never
	 has a value that is less than the nominal current of the load.<p>
	 */
	enum LoadType {
		CONTINUOUS,
		NONCONTINUOUS,
		MIXED
	}

	/**
	 @return The type of the load in regards to its continuousness.
	 @see LoadType
	 */
	LoadType getLoadType();

	/**
	 Makes this load a continuous load, implying that the MCA value changes to
     1.25*nominalCurrent. The load type changes to CONTINUOUS. Registered
	 listeners are notified of this change.
	 */
	void setContinuous();

	/**
	 Makes this load a non continuous load, implying that the MCA value changes
     to the same value of nominalCurrent. Registered listeners are notified
	 of this change.
	 */
	void setNonContinuous();

	/**
	 Sets explicitly the MCA for this load and mark this load as a mixed load.
     Notice that MCA should always be equal or greater than the load's nominal
     current. An attempt to set an MCA lesser than the load's nominal current
	 will convert this load to a NONCONTINUOUS one, with an MCA equal to the
	 load's nominal current. Also notice that there is no limitation on how big
	 the MCA value can be, in regards to the load current. Registered
	 listeners are notified of this change.

	 @param MCA The new minimum circuit ampacity (MCA) for this load.
	 */
	void setMixed(double MCA);

}
