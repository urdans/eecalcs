package eecalcs.loads;

import eecalcs.circuits.Circuit;
import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;
//quedé aquí: review this java doc and move it to the BaseLoad class.
/**This is a generic and base class load for all type of loads.
 <p>An electric load object provides information about its basic requirements
 and has the following properties (RO: read only; R&W: read and write):
 <ol>
 <li>Voltage, phases and number of wires (voltage system).</li>
 <dd>(R&W). Expressed in volts. Like 120 volts, single phase, 2-wire.
 It's a non-null field. Refer to {@link VoltageSystemAC} for details.</dd>
 <li>Nominal current.</li>
 <dd>(R&W). In amperes, apply only for the phase (hot) conductors. It's a
 non-zero positive value.
 <p>Voltage, nominal current and power factor are independent variables that
 define the real and apparent power (P & S). Voltage and nominal current can be
 set when calling the constructor, or later on using their own setters.</dd>
 <li>Nominal neutral current.</li>
 <dd>(RO). In amperes. Typically this value is zero for 3Ø, 3-wire loads or
 the value of the phase current for 1Ø, 2-and-3-wire loads. Descendant
 classes can override this property and provide setter methods when required.
 This property provides a current value that is only used for sizing the neutral
 conductor. It should not be used for any other purpose.
 <p>For a nonlinear load, this property can return a value that is as big as
 173% the nominal current of the phase conductors (for 3Ø balanced
 non-linear loads) or as big as 200% the nominal current of the
 phase conductors for 3Ø system feeding only single-phase non-linear
 loads.<br>
 "*****************************************"
 Future: more investigation is required for this.
 "*****************************************"
 </dd>
 <li>Apparent power (S).</li>
 <dd>(RO). In volt-amperes. It's a calculated value from the voltage system and
 the nominal current of the load.</dd>
 <li>Real power (P).</li>
 <dd>(RO). In watts. It's a calculated value from the voltage system, the
 nominal current and the power factor of the load.</dd>
 <li>Power factor.</li>
 <dd>(R&W). A positive number between 0.7 and 1.0. Its setter constrain the
 provided value to this rage. The default value is 1.0.</dd>
 <li>MCA: Minimum circuit ampacity.</li>
 <dd>(RO). In amperes.</dd>
 <li>The MCA to nominal current ratio.</li>
 <dd>(RO). Named as MCAMultiplier. It's the quotient between the MCA and the
 nominal current. This property can be used by other classes (Circuit class, for
 instance) to compute the size of the conductor feeding this load.</dd>
 <li>Maximum Overcurrent Protection Device rating.</li>
 <dd>(RO). Named OCPD. In amperes. Also know as OCPD for some loads.
 An OCPD provides short-circuit, ground-fault protection and, in some cases,
 overload protection.
 <p>It refers to the maximum rating of the device (fuse, circuit breaker,
 etc.) that provides short-circuit and ground-fault protection to the load,
 when the device is required by the load. Its value is determine internally
 based on the type of loads and the NEC rules that apply to the type of load
 and its electrical characteristics.
 Notice that for some loads this OCPD device also provides overload protection.
 <p>For this base class, its value is zero, indicating that no OCPD is
 required by this load, and thus, the rating of the OCPD must be determined
 outside of this class (only to protect the conductors feeding this load),
 i.e., this base class does not have a maximum overcurrent protection device
 rating requirement. Descendant classes override this property to
 return the proper value according to the load type.</dd>
 <li>Minimum disconnect switch rating.</li>
 <dd>(RO). In amperes. Refers to the minimum rating of the disconnect switch
 (when required). For this base class load its value is zero, indicating the
 disconnect switch is not required. Descendant classes override this property
 to return the proper value according to the load type.</dd>
 <li>Overload protection rating.</li>
 <dd>(RO). In amperes. Refer to the rating of the overload protection for this
 load (when required). For this base class load its value is zero, indicating
 that a separate overload protection device is not required and that the
 overload protection is to be provided by the OCPD.
 Descendant classes override this property to return the proper value according
 to the load type.</dd>
 <li>Description of the load.</li>
 <dd>(R&W). It's a string describing the load. In some cases the description can
 be arbitrary but in others, it should comply with the requirements for
 describing a circuit load (load name and location). For this base class
 load, its a null value.</dd>
 </ol>
 <p><b>When a load is a panel and thus, fed from a feeder:</b>
 <p>The NEC-220.61 explains that the load of a feeder or service neutral is the
 maximum unbalanced load, that is, the maximum net calculated load between
 the neutral and any one phase conductor, i.e. for phase A load = 25650,
 phase B load = 32340, and phase C load=28600, the maximum unbalanced load is
 for phase B, 32340; because of the "net calculated" words, these values are
 obtained after applying demand factor and other calculation rules.
 <p>From the feeder or service neutral load, the current is calculated to
 determine the size of the conductors. However, the code mandates that for
 3-wire 2Ø or 5-wire 2Ø systems (very old system not covered by
 this software) this current must be multiplied by 1.4.
 <p>The code also permits a reduction of 70% to the neutral current after it
 is calculated per NEC-220.61(B)(1) (the loads of ranges, ovens,etc. per table
 220.55 and dryers per table 220.54) or per NEC-220.61(B)(2) (the excess of
 200 amps for system voltages of: 3W DC; 1Ø 3W; 3Ø 4W; or the old 2Ø 3W and
 5W systems.
 <p>The code prohibits any reductions of the neutral current calculated from
 220.61(C)(1) (1Ø 3W circuits fed from 3Ø 4W wye-connected systems) or from
 220.61(C)(2) (portion consisting of nonlinear loads supplied from 3Ø 4W
 wye-connected systems).<br><br>

 <p>Descendant classes are specialized loads and they add specific methods that
 modify the internals. Descendant classes can provided specialized methods
 and implement extra behaviors.
  */
public final class GeneralLoad extends BaseLoad implements Load {
	/**Defines artificially if this load is a linear or a nonlinear load.*/
	private boolean _isNonlinear = false;

	/**
	 Construct a load object with the given parameters.
	 @param voltageSystem The voltage system of the load. If a null value is
	 provided, the default value will be assumed.
	 @param nominalCurrent The nominal current of the load in amperes. If a
	 negative value is provided, its absolute value will be taken. If a zero
	 value is provided, the default value will be assumed.
	 @see VoltageSystemAC
	 */
	public GeneralLoad(VoltageSystemAC voltageSystem, double nominalCurrent) {
		type = Type.NONCONTINUOUS;
		powerFactor = 1.0;
		notifier = new NotifierDelegate(this);
		if(voltageSystem == null)
			this.voltageSystem = VoltageSystemAC.v120_1ph_2w;
		if(nominalCurrent == 0)
			this.nominalCurrent = 10.0;
		this.voltageSystem = voltageSystem;
		this.nominalCurrent = Math.abs(nominalCurrent);
		MCA = nominalCurrent;
	}

	/**
	 Constructs a GeneralLoad object with the following default values:<br>
	 - System AC voltage = 120v, 1 Ø, 2 wires.<br>
	 - Nominal current = 10 amperes<br>
	 */
	public GeneralLoad(){
		this(VoltageSystemAC.v120_1ph_2w, 10);
	}

	@Override
	public Circuit.CircuitType getRequiredCircuitType() {
		return Circuit.CircuitType.DEDICATED_BRANCH;
	}

	@Override
	public double getMaxOCPDRating(){
		return 0;
	}

	@Override
	public double getDSRating(){
		return 0;
	}

	@Override
	public boolean NHSRRuleApplies(){
		return true;
	}

	@Override
	public double getOverloadRating(){
		return 0;
	}

	@Override
	public boolean isNonlinear(){
		return _isNonlinear;
	}

	/**
	 Makes this load a continuous load, implying that the MCA value changes to
	 1.25*nominalCurrent. The load type changes to CONTINUOUS. Registered
	 listeners are notified of this change.
	 */
	public void setContinuous() {
		if(type == Type.CONTINUOUS)
			return;
		_setContinuousness(Type.CONTINUOUS, -1);
	}

	/**
	 Makes this load a non continuous load, implying that the MCA value changes
	 to the same value of nominalCurrent. Registered listeners are notified
	 of this change.
	 */
	public void setNonContinuous() {
		if(type == Type.NONCONTINUOUS)
			return;
		_setContinuousness(Type.NONCONTINUOUS, -1);
	}

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
	public void setMixed(double MCA) {
		if(this.MCA == MCA && type == Type.MIXED)
			return;
		if(MCA <= nominalCurrent) {
			setNonContinuous();
			return;
		}
		_setContinuousness(Type.MIXED, MCA);
	}

	/**Sets the new behavior of the load and notifies its listeners about it.
	 If the parameter is null nothing is set.*/
	private void _setContinuousness(Type type, double mca){
		Type oldType = this.type;
		double oldMCA = MCA;
		this.type = type;
		if(type == Type.CONTINUOUS)
			MCA = 1.25 * nominalCurrent;
		else if(type == Type.NONCONTINUOUS)
			MCA = nominalCurrent;
		else //MIXED
			MCA = mca;
		notifier.info.addFieldChange("type", oldType,
				this.type);
		notifier.info.addFieldChange("MCA", oldMCA, MCA);
		notifier.notifyAllListeners();
	}

	/**
	 Sets the nonlinear behavior of this load.
	 @param flag If true, the load is set to nonlinear (load with harmonics).
	 If false, the load is set as a linear one (the default).<br>
	 "*************************"<br>
	 Future: This is a temporary method.
	 A nonlinear load must be a descendant class that returns true for
	 its isNonLinear() method. A load is just linear like this load or
	 nonlinear as a descendant specialized load.<br>
	 "*************************"
	 */
	public void setNonlinear(boolean flag){
		if(_isNonlinear == flag)
			return;
		notifier.info.addFieldChange("_isNonlinear", _isNonlinear, flag);
		_isNonlinear = flag;
		notifier.notifyAllListeners();
	}

	@Override
	public void setNominalCurrent(double nominalCurrent){
		super.setNominalCurrent(nominalCurrent);
	}

	@Override
	public void setVoltageSystem(VoltageSystemAC voltageSystem){
		super.setVoltageSystem(voltageSystem);
	}

	@Override
	public void setPowerFactor(double powerFactor) {
		super.setPowerFactor(powerFactor);
	}
}
