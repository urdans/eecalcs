package eecalcs.loads;

import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;

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
public class Load {
	private LoadType loadType = LoadType.NONCONTINUOUS;
	protected VoltageSystemAC voltageSystem;
	protected double powerFactor = 1.0;
	protected final NotifierDelegate notifier = new NotifierDelegate(this);
	protected String description;
	/**Defines if this load is a linear or a nonlinear load.
	 Todo To be removed once a descendant load NonLinear class is implemented.*/
	private boolean _isNonlinear = false;
	/**The nominal current of a load, in amperes. Along with the power factor
	 and voltage it defines this load real and apparent power.*/
	public double nominalCurrent;
	/**The Minimum Circuit Ampacity that is required for the conductor
	 feeding this load. For this class, it's a read-only property whose value
	 is defines as follows:<br>
	 for a noncontinuous load, MCA = nominal current<br>
	 for a continuous load, MCA = 1.25 x nominal current.<br>
	 Descendant classes add a setter to this property and override its getter
	 to detach the relationship between these two values based on the
	 continuousness behavior of the load but must keep it equal or bigger
	 than the nominal current.<br>
	 For example, a piece of refrigerant equipment could not be a continuous
	 load and still have an MCA value above the nominal current.*/
	public double MCA;

	/**
	 Construct a load object with the given parameters.
	 @param voltageSystem The voltage system of the load. If a null value is
	 provided, the default value will be assumed.
	 @param nominalCurrent The nominal current of the load in amperes. If a
	 negative value is provided, its absolute value will be taken. If a zero
	 value is provided, the default value will be assumed.
	 @see VoltageSystemAC
	 */
	public Load(VoltageSystemAC voltageSystem, double nominalCurrent) {
		if(voltageSystem == null)
			this.voltageSystem = VoltageSystemAC.v120_1ph_2w;
		if(nominalCurrent == 0)
			this.nominalCurrent = 10.0;
		this.voltageSystem = voltageSystem;
		this.nominalCurrent = Math.abs(nominalCurrent);
		MCA = nominalCurrent;
	}

	/**
	 Constructs a Load object with the following default values:<br>
	 - System AC voltage = 120v, 1 Ø, 2 wires.<br>
	 - Nominal current = 10 amperes<br>
	 */
	public Load(){
		this(VoltageSystemAC.v120_1ph_2w, 10);
	}

	/**
	 Sets the voltage system of this load.
	 Registered listeners receive notification of this change.
	 @param voltageSystem The new voltage system for this load. If this
	 parameter is null, nothing is set.
	 @see VoltageSystemAC
	 */
	public void setVoltageSystem(VoltageSystemAC voltageSystem) {
		if(this.voltageSystem == voltageSystem || voltageSystem == null)
			return;
		notifier.info.addFieldChange("voltageSystem", this.voltageSystem, voltageSystem );
		this.voltageSystem = voltageSystem;
		notifier.notifyAllListeners();
	}

	/**
	 @return The voltage system of this load.
	 @see VoltageSystemAC
	 */
	public VoltageSystemAC getVoltageSystem() {
		return voltageSystem;
	}

	/**
	 Sets a non-zero positive value for this load nominal current. If this
	 load is non-continuous, MCA is updated to this value; if this load is
	 continuous, MCA is updated to 1.25 times this value. If the load is
	 mixed and this value is bigger than MCA, this load changes to
	 non-continuous and MCA is updated to this value, otherwise, MCA does not
	 change.
	 Registered listeners receive notification of these changes.
	 @param nominalCurrent The new current of the load, in amperes. If this
	 value is zero, nothing is set.
	 */
	public void setNominalCurrent(double nominalCurrent){
		if(this.nominalCurrent == nominalCurrent || nominalCurrent == 0)
			return;
		nominalCurrent = Math.abs(nominalCurrent);
		double oldMCA = MCA;

		if(loadType == LoadType.NONCONTINUOUS)
			MCA = nominalCurrent;
		else if (loadType == LoadType.CONTINUOUS)
			MCA = 1.25 * nominalCurrent;
		else {//MIXED
			if(nominalCurrent >= MCA) {
				loadType = LoadType.NONCONTINUOUS;
				MCA = nominalCurrent;
			}
		}

		notifier.info.addFieldChange("nominalCurrent", this.nominalCurrent,
				nominalCurrent);
		notifier.info.addFieldChange("MCA", oldMCA, MCA);
		this.nominalCurrent = nominalCurrent;
		notifier.notifyAllListeners();
	}

	/**
	 @return The nominal current of this load, in amperes.
	 */
	public double getNominalCurrent() {
		return nominalCurrent;
	}

	/**
	 @return The neutral current of this load, in amperes, for the only
	 purpose of determining the size of the neutral conductor.<br><br>
	 <p>The criteria for determining this current at this base class, is as
	 follows:
	 <p>- All 1φ loads having a neutral and all 3φ loads having a neutral will
	 have a neutral current equal to the phase current. The 3φ-4W loads have
	 a neutral that even if it is not a CCC, it is sized the same as the
	 phase conductors.
	 <p>- For all the other loads (the ones that do not have a neutral
	 conductor), the returned value is zero.<br><br>
	 <p><b>Descendant classes that account for harmonics and/or behave as
	 panels, should override this method so that for 3φ loads having a
	 neutral, the value of the current can be lower or higher than the phase
	 current, accordingly.</b>
	 */
	public double getNeutralCurrent() {
		if (voltageSystem.hasNeutral())
			return nominalCurrent;
		//no neutral, no current.
		return 0;
	}

	/**
	 @return The apparent power of this load, in volt-amperes.
	 */
	public double getVoltAmperes() {
		return voltageSystem.getVoltage() * nominalCurrent * voltageSystem.getFactor();
	}

	/**
	 @return The real power of this load, in watts.
	 */
	public double getWatts() {
		return getVoltAmperes() * powerFactor;
	}

	/**
	 Sets the power factor of this load. This will change indirectly the
	 value of the real power of this load.
	 <p>Registered listeners receive notification of these changes (pf & P).
	 @param powerFactor A value >= 0.7  and <=1.0 representing the new power
	 factor of the load. Any value above or below the acceptable limits will be
	 trimmed to the limit values, without notice.
	 */
	public void setPowerFactor(double powerFactor) {
		if(this.powerFactor == powerFactor)
			return;
		powerFactor = Math.abs(powerFactor);
		if(powerFactor < 0.7)
			powerFactor = 0.7;
		else if(powerFactor > 1.0)
			powerFactor = 1.0;
		double oldWatts = getWatts();
		notifier.info.addFieldChange("powerFactor", this.powerFactor, powerFactor);
		this.powerFactor = powerFactor;
		notifier.info.addFieldChange("watts", oldWatts, getWatts());
		notifier.notifyAllListeners();
	}

	/**
	 @return The power factor of this load. A positive number between 0.7 and
	 1.0 inclusive.
	 */
	public double getPowerFactor() {
		return powerFactor;
	}

	/**
	 @return The Minimum Circuit Ampacity of this loads, in amperes.
	 */
	public double getMCA() {
		return MCA;
	}

	/**
	 @return The quotient between the MCA and the load nominal current. Notice
	 this value is greater or equal to 1.
	 */
	public double getMCAMultiplier() {
		return MCA / nominalCurrent;
	}

	/**
	 @return The maximum overcurrent protection device (OCPD) rating
	 (protection for short-circuit, ground-fault and overload), in amperes.
	 If the returned value is 0, it means that a specific OCDP rating is not
	 required for this load and thus, the rating of the OCPD must be
	 determined at the circuit level to protect the conductors feeding this
	 load.
	 <p>The is100PercentRated parameter may look trivial. It is up to the
	 load object to decide to use it or not. For some load types, the NEC
	 allows to skip the 1.25*Inom increase in OCDP size if the OCDP and its
	 enclosure are 100% rated. As this exception does not apply to all loads,
	 each load object must decide to account for it or ignore it.
	 <p>For this base class the returned value is zero. Descendant load classes
	 having particular requirements for an OCPD rating, must override this
	 method to return the proper value.
	 @param is100PercentRated Indicates if the requested value is for an
	 OCPD (and enclosure) that is 100% rated.
	 */
	public double getMaxOCPDRating(boolean is100PercentRated){
		return 0;
		/*When is100PercentRated is accounted for, use this:
		 * if(is100PercentRated || !isContinuous)
		 *   return Inom
		 * else
		 *   return 1.25*Inom
		 * Few loads would use this approach!*/
	}

	/**
	 @return The minimum rating in amperes of the disconnect switch (DS) for
	 this load, if a DS is required. If the returned value is 0, it means a
	 specific disconnect switch rating is not required for this load, and
	 thus the rating should be determined at the circuit level, usually a
	 value equal or greater than the circuit's OCPD rating.
	 <p>For this base class the returned value is zero. Descendant load classes
	 requiring or possibly having a DS (Disconnect Switch) must override this
	 method to return the proper DS rating.
	 */
	public double getDSRating(){
		return 0;
	}

	/**
	 @return True if the Next Higher Standard Rating rule can be applied to
	 this load's OCPD, false otherwise.
	 <p>The returned value is meaningful only if
	 {@link #getMaxOCPDRating(boolean)} return a non zero value.

	 <p>For this base class the returned value is true. Descendant load classes
	 that do not allow the application of this rule must override this method
	 to return false.
	 */
	public boolean NHSRRuleApplies(){
		return true;
	}

	/**
	 @return The maximum ampere rating of the overload protection device.
	 If the returned value is 0, it means a specific rating for a separate
	 overload protection device is not required for this load and that the
	 overcurrent protection is provided by the OCPD (Short-Circuit and
	 Ground-Fault Protection).
	 <p>For this base class the returned value is zero. Descendant load classes
	 requiring a specific separate overload protection device rating must
	 override this method to return the proper overload protection rating.
	 */
	public double getOverloadRating(){
		return 0;
	}

	/**
	 Sets the description of this load.
	 @param description The description of the load. This should comply with the
	 NEC requirements for describing loads in panel circuit identification.
	 */
	public void setDescription(String description){
		if(this.description != null)
			if(this.description.equals(description))
				return;
		notifier.info.addFieldChange("description", this.description, description);
		this.description = description;
		notifier.notifyAllListeners();
	}

	/**
	 @return The description of this load.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 @return The {@link NotifierDelegate notifier delegate} object for this
	 load.
	 */
	public NotifierDelegate getNotifier() {
		return notifier;
	}

	/**
	 @return True if the neutral conductor of this load is a current-carrying
	 conductor as defined by the NEC rule 310.15(B)(5). False otherwise or if
	 this load voltage system does not have a neutral.<br>
	 */
	public boolean isNeutralCurrentCarrying(){
		if(isNonlinear()) //the neutral carries the harmonics in all configurations
			return voltageSystem.hasNeutral();
		if(voltageSystem.hasNeutral()){ //almost all are CCC except the 4w
			return voltageSystem.getWires() != 4;
		}
		return false;
	}

	/**
	 @return True if this is a nonlinear load (a load with harmonics); false
	 otherwise.<br>
	 For this base class the returned value is false. Descendant load classes
	 with a nonlinear behavior must override this method to return true.*/
	public boolean isNonlinear(){
		//todo: to be removed when NonLinearLoad is implemented.
		return /*false*/ _isNonlinear;
	}

	/**
	 @return The type of the load in regards to its continuousness.
	 @see LoadType
	 */
	public LoadType getLoadType() {
		return loadType;
	}

	/**
	 Makes this load a continuous load, implying that the MCA value changes to
	 1.25*nominalCurrent. The load type changes to CONTINUOUS. Registered
	 listeners are notified of this change.
	 */
	public void setContinuous() {
		if(loadType == LoadType.CONTINUOUS)
			return;
		_setContinuousness(LoadType.CONTINUOUS, -1);
	}

	/**
	 Makes this load a non continuous load, implying that the MCA value changes
	 to the same value of nominalCurrent. Registered listeners are notified
	 of this change.
	 */
	public void setNonContinuous() {
		if(loadType == LoadType.NONCONTINUOUS)
			return;
		_setContinuousness(LoadType.NONCONTINUOUS, -1);
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
		if(this.MCA == MCA && loadType == LoadType.MIXED)
			return;
		if(MCA <= nominalCurrent) {
			setNonContinuous();
			return;
		}
		_setContinuousness(LoadType.MIXED, MCA);
	}

	/**Sets the new behavior of the load and notifies its listeners about it.
	 If the parameter is null nothing is set.*/
	private void _setContinuousness(LoadType loadType, double mca){
		LoadType oldLoadType = this.loadType;
		double oldMCA = MCA;
		this.loadType = loadType;
		if(loadType == LoadType.CONTINUOUS)
			MCA = 1.25 * nominalCurrent;
		else if(loadType == LoadType.NONCONTINUOUS)
			MCA = nominalCurrent;
		else //MIXED
			MCA = mca;
		notifier.info.addFieldChange("loadType", oldLoadType,
				this.loadType);
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
}
