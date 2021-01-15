package eecalcs.circuits;

import eecalcs.conductors.*;
import eecalcs.conduits.*;
import eecalcs.loads.Load;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import eecalcs.voltagedrop.ROVoltDrop;
import eecalcs.voltagedrop.VoltDrop;
import tools.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 This class represents an electrical circuit as recognized by the NEC 2014.

 <br><br><u>The goals of this circuit class are:</u>
 <ol>
 <li><b>To calculate the right conductor/cable size:</b> based on the load
 properties (amperes, continuousness, type, etc.), the installation conditions
 (in free air, bundled or in conduit, ambient temperature, rooftop condition,
 etc.), and considering both ampacity (corrected and adjusted) and the maximum
 allowed voltage drop. Some other properties are required as a user
 input/preference, like if using conductor or cables, the conductor metal, the
 conduit material, the rating of the terminals/enclosures (if known), etc.</li>

 <li><b>Determine rating of the overcurrent protection device (OCPD):</b> based
 on both the properties of the served load and the chosen conductor size.</li>

 <li><b>Determine the correct size of the conduit:</b> If this circuit uses a
 conduit (or several conduits for more than one set of conductors), its size
 will be determine to accommodate its conductors/cables (cables in conduits are
 permitted by code but are not common).</li>
 </ol>

 <u>The physical objects that conform a circuit object are:</u>
 <ul>
 <li>One or more set of conductors or cables in parallel (conductors only or
 cables only, not a mix of them).</li>

 <li>One overcurrent protection device (OCPD).</li>

 <li>One connected load which is provided by the user when calling this class's
 constructor.</li>

 <li>Optional conduits or bundles.</li>
 </ul>

 <u>The characteristics or properties of a circuit are:</u>
 <ul>
 <li>The length of the circuit: which once assigned, propagates to its
 conduitables.</li>

 <li>The system voltage (number of phases, number of wires and if delta or wye
 connection for 3 phase feeder.)</li>

 <li>The number of sets of conductors/cables in parallel.</li>

 <li>How are the sets installed in parallel (see conduit sharing mechanism below
 for details).</li>

 <li>The circuit rating, which is the same as the rating of the OCPD.</li>

 <li>If the circuit serves one-end load (dedicated) or several sparse or
 distributed or daisy chained loads. This is defined by the load object
 itself.</li>

 <li>Conditions: rooftop, wet, damp, etc.</li>

 <li>The allowed voltage drop (based on if it's a feeder or a branch circuit and
 based of the load type, like fire pump running, fire pump starting, sensitive
 electronic equipment, etc.).</li>

 <li>If the OCPD is 100% or 80% rated.</li>
 </ul>

 <p><u>Conduit sharing mechanism:</u></p>
 By default, this class uses one conduit and several conductors. This is the
 most common scenario. However, other less common scenarios are also handled by
 this class: (in order of more common to less common)
 <ol>
 <li>One cable in free air (no conduit).</li>
 <li>More than one cable in free air (no conduit; not bundled).</li>
 <li>More than one cable in free air (no conduit; bundled).</li>
 <li>More than one set of insulated conductors in parallel, using one set of
 conductors per conduit.</li>
 <li>More than one set of insulated conductors in parallel, using one large
 conduit.</li>
 <li>More than one set of insulated conductors in parallel, using equal number
 of sets of conductors per conduit.</li>
 </ol>
 <b>Even more rare</b>:
 <ol>
 <li>One or more sets of insulated conductors in free air (no conduit; bundled
 or not; even though this is allowed, it would be considered a bad practice).
 </li>
 <li>More than one cable in parallel, using one cable per conduit.</li>
 <li>More than one cable in parallel, using one large conduit.</li>
 <li>More than one cable in parallel, using equal number of cables per
 conduit.
 (Cables in conduits are permitted by code but are not common, are very
 expensive and could be considered a bad practice)</li>
 </ol>

 <p>Out of this mechanism, there are three ways conduits are used by this class:
 <p>- Single conduit, (default) (applies for one set of conductors only).
 This conduit is owned by this class and can't be accessed from outside.
 <p>- Several conduits (applies for more than one set of conductors only).
 These conduits are owned by this class and can't be accessed from outside.
 <p>- Single conduit, not owned by this class. It's used for several
 one-set-only circuits that shares the same conduit.
 <br><br>
 <p>There are three exactly same ways bundles are used by this class as well.
 <p>These mechanisms are implemented for both conduits and bundles. Also, there
 can be the case where no conduit and no bundle is used at all. It can rare for
 insulated conductors but for cables it's the most commonly used installation.
 <br><br>

 <p><b><u>How this mechanism works.</u></b><br><br>
 ■ This class starts with one set of conductors using its private conduit.
 The circuit is said to be in "conduit circuit mode". The user can decide to
 switch back and forth from conductors to cables at any time.<br><br>
 ■ The user can increase and later decrease the number of sets. This is
 allowed only when in private conduit circuitMode.<br><br>
 ■ The user can put the circuit in "free air circuit mode", or in "bundle
 circuit mode" or back in "conduit circuit mode" by calling the appropriated
 methods:
 <p>&nbsp; - <em>SetFreeAirMode()</em>: No conduit, no bundle, just in free air.
 No restriction. If insulated conductor are used, a warning message is
 generated, since installing insulated conductor in free air is a bad practice.

 <p>&nbsp; - <em>SetConduitMode()</em>: "Private conduit circuit mode". No
 restriction. If cables are used, a warning message is generated, since using
 cables inside a conduit is rare and could be considered a bad practice.

 <p>&nbsp; - <em>SetConduitMode(Conduit sharedConduit)</em>: "Shared conduit
 circuit mode". sharedConduit must be a valid conduit and the number of sets
 must be one. If cables are used, a warning message is generated. If there are
 more than one set an error message is generated.

 <p>&nbsp; - <em>SetBundleMode()</em>: "Bundle circuit mode". No restriction. If
 insulated conductors are used, a warning message is generated.

 <p>&nbsp; - <em>SetBundleMode(Bundle sharedBundle)</em>: "Shared bundle circuit
 mode". No restriction. If insulated conductors are used, a warning message is
 generated.

 <br><br>
 ■ When in "shared conduit circuit mode" the user increases the number of sets
 to more than one, it will switch to private conduit circuitMode automatically.
 To return to shared circuitMode, first the number of sets must return to one
 and then the method <em>SetConduitMode(Conduit sharedConduit)</em> must be
 called. //<b>to be verified if this coded like this</b><br><br>
 ■ Keep in mind that some actions are not accomplished due to code rules,
 for example trying to use conductors smaller thant 1/0 AWG in parallel. In
 that case, the returned value makes no sense, like zero or null. This is an
 indication of error. There can also be warnings. In all cases, the user should
 ask the resultMessage field for the presence of messages//<b>Is this
 implemented?</b>. Refer to {@link tools.ResultMessages ResultMessages} for
 details.<br><br>
 */
public class Circuit {
	private CircuitMode circuitMode = CircuitMode.PRIVATE_CONDUIT;
	/**List of all conduitables that this circuit needs as per its mode.*/
	private final List<Conduitable> conduitables = new ArrayList<>();
	//the overcurrent protection device owned by the circuit.
	private final OCPD ocdp = new OCPD(this);
	/**The conduit used by this circuit that can be shared with other circuits.
	Never null, but can be empty, default is PVC40, non nipple*/
	private final Conduit privateConduit = new Conduit();
	/**Is null or references an external conduit*/
	private Conduit sharedConduit;
	/**never null, but can be empty*/
	private final Bundle privateBundle = new Bundle(null, 0, 0);
	/**is null or references an external bundle*/
	private Bundle sharedBundle;
	private final Load load;
	private int numberOfSets = 1;
	private int setsPerPrivateConduit = 1;
	private int numberOfPrivateConduits = 1;
	/**meaningful only when using conductors*/
	private int conductorsPerSet;
	private final Conductor phaseAConductor = new Conductor();
	private Conductor phaseBConductor;
	private Conductor phaseCConductor;
	private Conductor neutralConductor;
	private final Conductor groundingConductor = new Conductor().setRole(Conductor.Role.GND);
	private final Cable cable = new Cable();
	private boolean usingCable = false;
	private final VoltDrop voltageDrop = new VoltDrop();
	private TempRating terminationTempRating;
	/**the ampacity of the circuit size
	 under the installation conditions*/
	private double circuitAmpacity;

	private static final Message ERROR210 = new Message(
	"More than one set of conductors or cables cannot be in a shared " +
			"conduit.",-210);
	private static final Message ERROR220 = new Message(
	"More than one set of conductors or cables cannot be in a shared " +
			"bundle.",-220);
	private static final Message ERROR230 = new Message(
	"The provided shared conduit is not valid.",-230);
	private static final Message ERROR240 = new Message(
	"The provided shared bundle is not valid.",-240);
	private static final Message ERROR250 = new Message(
	"Changing the number of conduits is only allowed when in" +
			" private circuitMode.",-250);
	private static final Message ERROR260 = new Message(
	"Ampacity of the load is to high. Increment the number of " +
			"sets, or use less sets per conduit.",-260);
	private static final Message ERROR270 = new Message(
	"Paralleled power conductors in sizes smaller than 1/0 AWG " +
			"are not permitted. NEC-310.10(H)(1)",-270);
	private static final Message ERROR280 = new Message(
	"Private conduit is available only in private conduit " +
			"circuitMode.",-280);
	private static final Message ERROR282 = new Message(
	"Private bundle is available only in private bundle " +
			"circuitMode.",-282);
	private static final Message ERROR284 = new Message(
	"Circuit phase, neutral and grounding insulated conductors " +
			"are available only when using conductors, not when using " +
			"cables.",-284);
	private static final Message ERROR286 = new Message(
	"Circuit cables are available only when using cables, not " +
			"when using conductors", -286);
	private static final Message ERROR290 = new Message(
	"Temperature rating of conductors or cable is not suitable " +
			"for the conditions of use", -290);
	private static final Message WARNN200 = new Message(
	"Insulated conductors are being used in free air. This " +
			"could be considered a bad practice.", 200);
	private static final Message WARNN205 = new Message(
	"Insulated conductors are being used in a bundle. This " +
			"could be considered a bad practice.", 205);
	private static final Message WARNN210 = new Message(
	"Cables are being used in conduit. This could be an " +
			"expensive practice.", 210);
	private static final Message WARNN220 = new Message(
	"Corrected and adjusted ampacity for this conductor/cable " +
			"temp rating exceeds the ampacity for the temperature rating of " +
			"the termination.\nConductor/cable size has been selected per " +
			"the termination temp rating.", 220);
	/**Listener to listen for changes in the neutral conductors.
	This listener is not used if the load doesn't require a neutral.*/
	private final Listener neutralListener;
	/**Listener to listen for changes in the sharedConduit.
	This listener is not used if this circuit is not in shared conduit mode.*/
	private final Listener sharedConduitListener;
	/**Listener to listen for changes in the sharedBundle.
	This listener is not used if this circuit is not in shared bundle mode.*/
	private final Listener sharedBundleListener;
	/**Container for messages resulting from validation of input variables and
	 calculations performed by this class.*/
	private final ResultMessages resultMessages = new ResultMessages();
	/**Indicates that something changed and the circuit needs to be
	 recalculated*/
	private boolean circuitChangedRecalculationNeeded = true;

	/**
	 @return The {@link ROResultMessages} object containing all the error and
	 warning messages of this object.
	 */
	public ROResultMessages getResultMessages(){
		return resultMessages;
	}

	/**
	 Prepares a representing list of conduitables for this this circuit.<br>

	 <p>The number of conduitables in the list depends on the circuit mode:<br>

	 If the circuit is in a private conduit, the list will contain all the
	 conduitables that go inside one of the conduits. For example, for a
	 circuit having 6 sets of 4 conductors each and 2 private conduits, the
	 conduitable list will have 3 sets of conductors, that is, 3x4=12.<br>

	 If the circuit is in a shared conduit or in a private or shared bundle,
	 the list will contain all the conduitables that go inside the
	 shared conduit or that is part of the shared/private bundle. For example,
	 for a circuit having 2 sets of 3 conductors each, the conduitable list
	 will have 2 sets of conductors, that is, 2x3=6.<br>

	 If the circuit is in free air, the list will contain all the
	 conduitables that conform one set of conductors. For example, for a
	 circuit having 3 sets of 4 conductors each, the conduitable list
	 will have 1 set of conductors, that is, 1x4=4.<br>

	 This method is called after the set of conductors is prepared by the
	 {@link #prepareSetOfConductors()} method, whenever the circuit mode
	 changes, whenever the number of private conduits changes or whenever the
	 number of sets changes.<br>
	*/
	private void prepareConduitableList(){
		removeFromSharedMeans();//need to remove them before
		conduitables.clear();   //losing the references with this clear.
		if(usingCable){
			conduitables.add(cable); //add the model cable to index 0
			//add the other cables as clones.
			while (conduitables.size() < getListBound())
				conduitables.add(cable.clone());
		}else{//using conductors. add the set to index 0
			conduitables.add(phaseAConductor);
			if(phaseBConductor != null)
				conduitables.add(phaseBConductor);
			if(phaseCConductor != null)
				conduitables.add(phaseCConductor);
			if(neutralConductor != null)
				conduitables.add(neutralConductor);
			conduitables.add(groundingConductor);
			//add the other sets as clones
			while (conduitables.size()/conductorsPerSet < getListBound()) {
				conduitables.add(phaseAConductor.clone());
				if(phaseBConductor != null)
					conduitables.add(phaseBConductor.clone());
				if(phaseCConductor != null)
					conduitables.add(phaseCConductor.clone());
				if(neutralConductor != null)
					conduitables.add(neutralConductor.clone());
				conduitables.add(groundingConductor.clone());
			}
		}
		setupMode();
	}

	/** @return The number of times a set of conductors or a cable must be
	added to the conduitable list*/
	private int getListBound(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return setsPerPrivateConduit;
		if(circuitMode == CircuitMode.SHARED_CONDUIT ||
		   circuitMode == CircuitMode.PRIVATE_BUNDLE ||
		   circuitMode == CircuitMode.SHARED_BUNDLE
		)
			return numberOfSets;
		//free air mode
		return -1;
	}

	/** Remove the conduitable list items from the shared conduit or
	 shared bundle used by this circuit.
	 */
	private void removeFromSharedMeans(){
		if(sharedConduit != null) {//circuit is in SHARED_CONDUIT mode
			sharedConduit.getNotifier().enable(false);
			conduitables.forEach(conduitable ->	sharedConduit.remove(conduitable));
			sharedConduit.getNotifier().enable(true);
		}
		if(sharedBundle != null) {//circuit is in SHARED_BUNDLE mode
			sharedBundle.getNotifier().enable(false);
			conduitables.forEach(conduitable -> sharedBundle.remove(conduitable));
			sharedBundle.getNotifier().enable(true);
		}
	}

	/**Returns the neutral role based on the load requirements.*/
	private Conductor.Role getNeutralRole(){
		return load.isNeutralCurrentCarrying() ?  Conductor.Role.NEUCC:
				Conductor.Role.NEUNCC;
	}

	/**
	 Creates the cable or the set of conductors to be used as model for one
	 set of conductors for this circuit.<br>
	 The model is represented either by:<br>
	 <dl>
	 <dt>- The field cable, or</dt>
	 <dt>- A set of conductors conformed by:</dt>
	 <dd>- The field phaseAConductor</dd>
	 <dd>- The field phaseBConductor (if required)</dd>
	 <dd>- The field phaseCConductor (if required)</dd>
	 <dd>- The field neutralConductor (if required)</dd>
	 <dd>- The field groundingConductor</dd>
	 </dl>
	*/
	private void prepareSetOfConductors(){
		/*creates the neutral conductor if it doesn't exist, and assigns it
		a role and size*/
		Runnable setupNeutral = () -> {
			if (neutralConductor == null) {
				neutralConductor = new Conductor();
				neutralConductor.getNotifier().addListener(neutralListener);
			}
			neutralConductor.getNotifier().enable(false);
			neutralConductor.setRole(getNeutralRole());
			neutralConductor.setSize(phaseAConductor.getSize());
			neutralConductor.getNotifier().enable(true);
		};
		/*creates the phase B conductor, if it doesn't exist, and assigns it
		a hot role and size.*/
		Runnable setupPhaseB = () -> {
			if (phaseBConductor == null)
				phaseBConductor = new Conductor();
			phaseBConductor.setRole(Conductor.Role.HOT);
			phaseBConductor.setSize(phaseAConductor.getSize());
		};
		/*creates the phase C conductor, if it doesn't exist, and assigns it
		a hot role and size.*/
		Runnable setupPhaseC = () -> {
			if (phaseCConductor == null)
				phaseCConductor = new Conductor();
			phaseCConductor.setRole(Conductor.Role.HOT);
			phaseCConductor.setSize(phaseAConductor.getSize());
		};

		VoltageSystemAC systemVoltage = load.getVoltageSystem();
		if(usingCable) {
			cable.setNeutralCarryingConductor(load.isNeutralCurrentCarrying());
			cable.setSystem(systemVoltage);
		}
		else {//the model uses conductors
			if (systemVoltage.hasHotAndNeutralOnly()) {
				setupNeutral.run();
				phaseBConductor = null;
				phaseCConductor = null;
			}
			else if (systemVoltage.has2HotsOnly()) {
				setupPhaseB.run();
				neutralConductor = null;
				phaseCConductor = null;
			}
			else if (systemVoltage.has2HotsAndNeutralOnly()) {
				setupPhaseB.run();
				setupNeutral.run();
				phaseCConductor = null;
			}
			else {//3 hots + neutral (if required), only
				setupPhaseB.run();
				setupPhaseC.run();
				if (systemVoltage.getWires() == 4) { //neutral is required
					setupNeutral.run();
				}
				else //neutral is not required
					neutralConductor = null;
			}
			//phase A and ground always count, that's why the 2 in the sum
			conductorsPerSet = 2 + (phaseBConductor == null ? 0 : 1)
					+ (phaseCConductor == null ? 0 : 1)
					+ (neutralConductor == null ? 0 : 1);
		}
		prepareConduitableList();
	}

	/**
	 @return The ampacity of the circuit conductors/cables under the
	 circuit's installation conditions; The temperature rating of the
	 terminations and the continuous behavior of the load are accounted for
	 in the result. Other factors are accounted as described in
	 {@link Conduitable#getCorrectedAndAdjustedAmpacity()}.<br>
	 If the returned value is zero it means that the size of the circuit
	 conductors has not being determined. Check for {@link #getResultMessages()}
	 for more information about the causes.
	 */
	public double getCircuitAmpacity(){
		calculateCircuit();
		return circuitAmpacity;
	}

	/**
	 Calculates the size of this circuit cable or conductor under the present
	 conditions, that is able to handle its full load current (per ampacity).
	 It accounts for rules NEC 210.19(A) for branch circuits and 215.2(A) for
	 feeders.
	 @return The size of the conductors/cable calculated per ampacity or null if
	 an error occurred, in which case, check {@link #resultMessages} for errors
	 and warning messages.
	 @param forNeutral If True, the size is calculated for the neutral
	 conductor based on the neutral current, otherwise the size is calculated
	 for the phase conductors. Notice that for 1φ-2w system the neutral
	 current is always equal to the phase current so this method should not
	 be used in such cases. The size calculation for the neutral conductor is
	 meant for 3-phase, 4-wire systems where the load is non-linear and the
	 neutral behaves as a current carrying conductor.
	 */
	public Size getSizePerAmpacity(boolean forNeutral){
		Function<Size, Boolean> checkError260 = (size) ->{
			if(size == null) {
				resultMessages.add(ERROR260);//ampacity too high
				return true;
			}
			return false;
		};
		Function<Size, Boolean> checkError270 = (size) ->{
			if(ConductorProperties.compareSizes(size, Size.AWG_1$0) < 0 && numberOfSets > 1) {
				resultMessages.add(ERROR270);//paralleled conductors < 1/0
				return true;
			}
			return false;
		};
		//messages cleanup
		resultMessages.remove(WARNN220);
		resultMessages.remove(ERROR260);
		resultMessages.remove(ERROR270);
		resultMessages.remove(ERROR290);
		Conduitable conduitable = _getConduitable();
		double factor1;
		if(ocdp.is100PercentRated())
			factor1 = conduitable.getCompoundFactor(); //do not account for 1.25
		else
			factor1 = Math.min(1 / load.getMCAMultiplier(), conduitable.getCompoundFactor());

		if(factor1 == 0) {
			//temp. rating of conductor not suitable for the ambient temperature
			resultMessages.add(ERROR290);
			return null;
		}
		double loadCurrentPerSet = forNeutral ?
				load.getNeutralCurrent() / numberOfSets :
				load.getNominalCurrent() / numberOfSets;
		double lookup_current1 = loadCurrentPerSet / factor1;
		Size size1 = ConductorProperties.getSizeByAmperes(
				lookup_current1,
				conduitable.getMetal(),
				conduitable.getTemperatureRating()
		);
		if(checkError260.apply(size1))
			return null;
		if (terminationTempRating != null) {
			//termination temperature rating is known
			if(terminationTempRating.getValue() >= conduitable.getTemperatureRating().getValue()) {
				if(checkError270.apply(size1))
					return null;
				return size1;
			}
			/*conductor temperature rating is higher than equipment
			temperature rating. Applying rule 310.15(B)*/
			//revised on Dec 2020
			if (
					ConductorProperties.getAmpacity(
							size1,
							conduitable.getMetal(),
							conduitable.getTemperatureRating()
					) * factor1
					<=
					ConductorProperties.getAmpacity(
							size1,
							conduitable.getMetal(),
							terminationTempRating
					)
			) {
				if (checkError270.apply(size1))
					return null;
				return size1;
			}
			size1 = ConductorProperties.getSizeByAmperes(
					lookup_current1,
					conduitable.getMetal(),
					terminationTempRating
			);
		}
		else {
			/*termination temperature rating is unknown*/
			//future: implement 110.14(C)(1)(4) motors design letter B, C or D..
			TempRating t_rating = TempRating.T60;
			if(loadCurrentPerSet > 100 && conduitable.getTemperatureRating().getValue() >= 75)
					t_rating = TempRating.T75;
			lookup_current1 = loadCurrentPerSet / conduitable.getCompoundFactor(t_rating);
			size1 = ConductorProperties.getSizeByAmperes(
					lookup_current1,
					conduitable.getMetal(),
					t_rating
			);
		}
		if(checkError260.apply(size1))
			return null;
		if(checkError270.apply(size1))
			return null;
		return size1;
	}

	/**
	 Returns the number of current carrying conductors (of insulated conductors,
	 not of cables) inside the used raceway/bundle or in free, accordingly.
	 @return The number of current carrying conductors
	 */
	public int getCurrentCarryingNumber() {
		int numberOfCurrentCarrying;
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			numberOfCurrentCarrying = privateConduit.getCurrentCarryingCount();
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			numberOfCurrentCarrying = sharedConduit.getCurrentCarryingCount();
		else if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			numberOfCurrentCarrying = privateBundle.getCurrentCarryingCount();
		else if(circuitMode == CircuitMode.SHARED_BUNDLE)
			numberOfCurrentCarrying = sharedBundle.getCurrentCarryingCount();
		else {//circuit is in free air mode
			numberOfCurrentCarrying = 0;
			for(Conduitable conduitable: conduitables)
				numberOfCurrentCarrying += conduitable.getCurrentCarryingCount();
		}
		return numberOfCurrentCarrying;
	}

	/**
	 @return The size of this circuit conductor/cable calculated per voltage
	 drop, under the preset conditions, that is able to keep its voltage drop
	 below the maximum allowed value.
	 @param forNeutral If True, the size is calculated for the neutral
	 conductor, otherwise the size is calculated for the phase conductors.
	 Notice that for a 1φ-2w system the current of the neutral and the
	 current of the phase are equal.
	 */
	public Size getSizePerVoltageDrop(boolean forNeutral){
		Conduit usedConduit;
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			usedConduit = privateConduit;
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			usedConduit = sharedConduit;
		else
			usedConduit = null;

		if(usingCable) {
			//for the sake of material and length
			voltageDrop.setConductor(cable.getPhaseConductorClone());
			if(usedConduit == null)
				/*cables are in free air or bundled. Raceway type will
				depend on cable type*/
				voltageDrop.setConduitMaterial(cable.getType().getMaterial());
			else
				voltageDrop.setConduitMaterial(ConduitProperties.getMaterial(usedConduit.getType()));
		}
		else {
			//for the sake of material and length
			voltageDrop.setConductor(phaseAConductor);
			if(usedConduit == null)
				//conductors are in free air or bundled
				voltageDrop.setConduitMaterial(Material.PVC);
			else
				voltageDrop.setConduitMaterial(ConduitProperties.getMaterial(usedConduit.getType()));
		}
		voltageDrop.setLoadCurrent(forNeutral ? load.getNeutralCurrent():
				load.getNominalCurrent());
		voltageDrop.setPowerFactor(load.getPowerFactor());
		voltageDrop.setSets(numberOfSets);
		voltageDrop.setSourceVoltage(load.getVoltageSystem());
		return voltageDrop.getCalculatedSizeAC();
	}

	/**Clears all the error and warning messages related to putting the circuit
	 in different modes (private/public conduit/bundle and free air).*/
	private void clearModeMsg(){
		resultMessages.remove(WARNN200);
		resultMessages.remove(WARNN205);
		resultMessages.remove(WARNN210);
		resultMessages.remove(ERROR210);
		resultMessages.remove(ERROR220);
		resultMessages.remove(ERROR230);
		resultMessages.remove(ERROR240);
		resultMessages.remove(ERROR250);
		resultMessages.remove(ERROR280);
		resultMessages.remove(ERROR282);
	}

	/**
	 Constructs a circuit object for the given load.
	 This circuit's default values are as follow:
	 <p>- One set of conductors.
	 <p>- Default conductors. See {@link Conductor#Conductor() Conductor()} for
	 default properties.
	 <p>- One private conduit. See {@link Conduit#Conduit(Type, boolean)}
	 for default properties.
	 @param load The load that will be copied as part of this circuit and that
	 will be served by this circuit.
	 @see Load
	 */
	public Circuit(Load load){
		if(load == null)
			throw new IllegalArgumentException("Load parameter cannot be null.");

		this.load = load;
		//if the load changes, the model of conductors needs to be set up
		this.load.getNotifier().addListener( speaker -> prepareSetOfConductors());
		//when a property of the the phase A conductor changes...
		phaseAConductor.getNotifier().addListener(speaker -> {
			//the other conductors must be updated accordingly...
			conduitables.forEach(conduitable ->
				{
					Conductor conductor = (Conductor) conduitable;
					conductor.getNotifier().enable(false);
					//all hot conductors must mirror the phase A conductor...
					if (conductor.getRole() == Conductor.Role.HOT ||
							conductor.getRole() == Conductor.Role.NCONC)
						conductor.copyFrom(phaseAConductor);
					else {
						/*...while the others (neutral and grounding) update
						some properties only.
						(length, insulation, ambient temp., metal)*/
						conductor.setLength(phaseAConductor.getLength());
						conductor.setInsulation(phaseAConductor.getInsulation());
						conductor.setAmbientTemperatureFSilently(phaseAConductor.getAmbientTemperatureF());
						conductor.setMetal(phaseAConductor.getMetal());
						/*the size property is updated for the neutral
						conductors when only hot and neutral are present.
						Nothing else is updated for the grounding conductors*/
						if ((conductor.getRole() == Conductor.Role.NEUCC ||
							conductor.getRole() == Conductor.Role.NEUNCC) &&
							this.load.getVoltageSystem().hasHotAndNeutralOnly()
						)
							conductor.setSize(phaseAConductor.getSize());
					}
					conductor.getNotifier().enable(true);
				}
			);
		});
		//when a property of the the grounding conductor changes...
		groundingConductor.getNotifier().addListener(speaker -> {
			//the other conductors must be updated accordingly...
			conduitables.forEach(conduitable ->
				{
					Conductor conductor = (Conductor) conduitable;
					conductor.getNotifier().enable(false);
					//all grounding conductors must have the same properties...
					if (conductor.getRole() == Conductor.Role.GND)
						conductor.copyFrom(groundingConductor);
					else {
						/*while the others, only some of them (length,
						insulation and ambient temp.)*/
						conductor.setLength(groundingConductor.getLength());
						conductor.setInsulation(groundingConductor.getInsulation());
						conductor.setAmbientTemperatureFSilently(groundingConductor.getAmbientTemperatureF());
					}
					conductor.getNotifier().enable(true);
				}
			);
		});
		/*the neutral conductor can be null and hence cannot have a
		permanent listener. The listener is assigned to the neutral when the
		neutral is created later on prepareSetOfConductors()*/
		neutralListener = speaker -> {
			conduitables.forEach(conduitable -> {
				/*If a neutral conductor is modified...*/
				Conductor conductor = (Conductor) conduitable;
				conductor.getNotifier().enable(false);
				if(conductor.getRole() == Conductor.Role.NEUCC ||
					conductor.getRole() == Conductor.Role.NEUNCC
				)
					/*the other neutral conductors are updated...*/
					conductor.copyFrom(neutralConductor);
				else {
					/*...while the other conductors (phase and grounding)
					update some properties
					(length, insulation, ambient temperature and metal)*/
					conductor.setLength(neutralConductor.getLength());
					conductor.setInsulation(neutralConductor.getInsulation());
					conductor.setAmbientTemperatureFSilently(neutralConductor.getAmbientTemperatureF());
					conductor.setMetal(neutralConductor.getMetal());
					/*the size property is updated for the phase conductors
					when only hot and neutral are present.
					Nothing else is updated for the grounding conductors*/
					if (conductor.getRole() == Conductor.Role.HOT &&
						load.getVoltageSystem().hasHotAndNeutralOnly()
					)
						conductor.setSize(neutralConductor.getSize());
				}
				conductor.getNotifier().enable(true);
			});
		};
		//recalculation is required when the conduit or bundle length changes
		sharedConduitListener = speaker ->
				circuitChangedRecalculationNeeded = true;
		sharedBundleListener =  speaker ->
				circuitChangedRecalculationNeeded = true;
		prepareSetOfConductors();
		calculateCircuit();
	}

	@Override
	public Circuit clone(){
		//todo implement clone method.
		return null;
	}

	/**
	 @return The voltage drop read-only object used by this circuit for internal
	 calculations.
	 */
	public ROVoltDrop getVoltageDrop(){
		return voltageDrop;
	}

	/**
	 Sets the maximum allowed voltage drop for this circuit. This value is used
	 to compute the size and the maximum length of the circuit conductors
	 that would have a voltage drop less or equal to the specified value.
	 @param maxVoltageDropPercent The maximum voltage drop in percentage.
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setMaxVoltageDropPercent(double maxVoltageDropPercent) {
		if(voltageDrop.getMaxVoltageDropPercent() == maxVoltageDropPercent)
			return;
		voltageDrop.setMaxVoltageDropPercent(maxVoltageDropPercent);
		circuitChangedRecalculationNeeded = true;
	}

	/**Sets the circuit mode*/
	private void setupMode(){
		Runnable detachFromSharedConduit = () -> {
			if(sharedConduit != null) {
				sharedConduit.getNotifier().removeListener(sharedConduitListener);
				sharedConduit = null;
			}
		};
		Runnable detachFromSharedBundle = () -> {
			if(sharedBundle != null) {
				sharedBundle.getNotifier().removeListener(sharedBundleListener);
				sharedBundle = null;
			}
		};
		clearModeMsg();
		privateConduit.empty();
		privateBundle.empty();
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT) {
			detachFromSharedConduit.run();
			detachFromSharedBundle.run();
			conduitables.forEach(conduitable -> privateConduit.add(conduitable));
			if(usingCable) //using cables in conduit, bad practice
				resultMessages.add(WARNN210);
		}
		else if(circuitMode == CircuitMode.SHARED_CONDUIT) {
			detachFromSharedBundle.run();
			conduitables.forEach(conduitable -> sharedConduit.add(conduitable));
			if(usingCable) //using cables in conduit, bad practice
				resultMessages.add(WARNN210);
		}
		else if(circuitMode == CircuitMode.PRIVATE_BUNDLE) {
			detachFromSharedConduit.run();
			detachFromSharedBundle.run();
			conduitables.forEach(conduitable -> privateBundle.add(conduitable));
			if(!usingCable)//using conductors in bundle, bad practice
				resultMessages.add(WARNN205);
		}
		else if(circuitMode == CircuitMode.SHARED_BUNDLE) {
			detachFromSharedConduit.run();
			conduitables.forEach(conduitable -> sharedBundle.add(conduitable));
			if(!usingCable)//using conductors in bundle, bad practice
				resultMessages.add(WARNN205);
		}
		else /*CircuitMode.FREE_AIR)*/ {
			detachFromSharedConduit.run();
			detachFromSharedBundle.run();
			if(!usingCable)//conductors in free air, bad practice
				resultMessages.add(WARNN200);
		}
		circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets the circuit in free air. All conductors and cables are removed from
	 any shared or private conduit or bundle and put in free air. If the circuit
	 is made of insulated conductors (not cables) a warning is generated. After
	 calling this method, the circuit is in free air circuit mode.
	 */
	public void setFreeAirMode(){
		if(circuitMode == CircuitMode.FREE_AIR)
			return;
		circuitMode = CircuitMode.FREE_AIR;
		prepareConduitableList();
	}

	/**
	 Sets the circuit in a private conduit.
	 To change the distribution of sets per conduit or to change the number of
	 used conduits, call the method {@link #morePrivateConduits()} or
	 {@link #lessPrivateConduits()}.
	 */
	public void setConduitMode(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return;
		circuitMode = CircuitMode.PRIVATE_CONDUIT;
		prepareConduitableList();
	}

	/**
	 Sets the circuit in a shared public conduit. This circuit starts
	 listening to changes in that shared conduit, to update this circuit
	 accordingly. Notice that this circuit adds to the shared conduit its own
	 EGC so the shared conduit will contain several EGC, one per each circuit
	 plus any other that is added by the user using
	 {@link Conduit#add(Conduitable)}.
	 @param sharedConduit The public conduit to which all the conduitables go
	 in.
	 */
	public void setConduitMode(Conduit sharedConduit){
		if(sharedConduit == null) {
			clearModeMsg();
			resultMessages.add(ERROR230);
			return;
		}
		if(sharedConduit == this.sharedConduit)
			return;
		circuitMode = CircuitMode.SHARED_CONDUIT;
		sharedConduit.getNotifier().addListener(sharedConduitListener);
		this.sharedConduit = sharedConduit;
		prepareConduitableList();
	}

	/**
	 Sets the circuit in a non shared private bundle.
	 */
	public void setBundleMode(){
		if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			return;
		circuitMode = CircuitMode.PRIVATE_BUNDLE;
		prepareConduitableList();
	}

	/**
	 Sets the circuit in the given shared public bundle. This circuit starts
	 listening to changes in that shared bundle, to update this circuit
	 accordingly.
	 @param sharedBundle The public bundle to which all the conduitables go in.
	 */
	public void setBundleMode(Bundle sharedBundle){
		if(sharedBundle == null) {
			clearModeMsg();
			resultMessages.add(ERROR240);
			return;
		}
		if(sharedBundle == this.sharedBundle)
			return;
		circuitMode = CircuitMode.SHARED_BUNDLE;
		sharedBundle.getNotifier().addListener(sharedBundleListener);
		this.sharedBundle = sharedBundle;
		prepareConduitableList();
	}

	/**
	 Increments the number of conduits used by this circuit, when in private
	 conduit circuit mode. The resulting number of conduits depends on the
	 actual number of sets. The NEC allows to distribute the sets of conductors
	 in parallel in a way that the impedance of each sets is maintained equal on
	 each set.
	 */
	public void morePrivateConduits(){
		resultMessages.remove(ERROR250);
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfPrivateConduits + 1; i <= numberOfSets; i++)
			if(numberOfSets % i == 0) {
				numberOfPrivateConduits = i;
				setsPerPrivateConduit = numberOfSets / numberOfPrivateConduits;
				prepareConduitableList();
				break;
			}
	}

	/**
	 Decrements the number of conduits used by this circuit, when in private
	 conduit circuitMode. The resulting number of conduits depends on the actual
	 number of sets. The NEC allows to distribute the sets of conductors in
	 parallel in a way that the impedance of each sets is maintained equal on
	 each set. Keep in mind that the number of conduits is reset to the same
	 number of sets of conductors (the default) whenever the number of sets
	 changes or the circuit circuitMode changes.
	 */
	public void lessPrivateConduits(){
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfPrivateConduits - 1; i != 0; i--)
			if(numberOfSets % i == 0) {
				numberOfPrivateConduits = i;
				setsPerPrivateConduit = numberOfSets / numberOfPrivateConduits;
				prepareConduitableList();
				break;
			}
	}

	/**Performs all calculations of the circuit components.
	If no error is found it resets the circuitRecalculationNeeded flag and
	returns true. Performs the opposite otherwise.*/
	private boolean calculateCircuit(){
		if(!circuitChangedRecalculationNeeded)
			return true;
		if(!calculatePhase())
			return false;
		if(!calculateCircuitAmpacity())
			return false;
		if(!calculateNeutral())
			return false;
		/*The OCPD object is available by calling getOcpd(). It will provide
		the proper ratings. No calculation is done for the OCPD at the
		circuit level*/
		//calculateOCPD();
		if(!calculateEGC())
			return false;
		//calculateConduit();
		if(!resultMessages.hasErrors()) {
			circuitChangedRecalculationNeeded = false;
			return true;
		}
		return false;
	}

	/**Calculates the size of the phase conductors for the set of insulated
	conductors or the phase conductors in the cable. Update the size for all
	phase conductors.*/
	private boolean calculatePhase(){
		//determine size per ampacity
		Size sizePerAmpacity = getSizePerAmpacity(false);
		if(sizePerAmpacity == null) //reasons on resultMessages
			return false;
		//determine size per voltage drop
		Size sizePerVoltageDrop = getSizePerVoltageDrop(false);
		if(sizePerVoltageDrop == null) //reasons on resultMessages
			return false;
		//choosing the biggest one from these two sizes.
		Size phasesSize = ConductorProperties.getBiggestSize(sizePerAmpacity,
				sizePerVoltageDrop);
		if(phasesSize == sizePerVoltageDrop)
			resultMessages.copyFrom(voltageDrop.getResultMessages());
		//update the size of all phase conductors
		if(usingCable)
			conduitables.forEach(conduitable ->
				((Cable) conduitable).setPhaseConductorSize(phasesSize));
		else
			phaseAConductor.setSize(phasesSize);
		return true;
	}

	/**calculate the ampacity of the selected conductor accounting for all the
	conditions of use. It's for phases only, not for neutral. The calculated
	valued is stored in the circuitAmpacity property.*/
	private boolean calculateCircuitAmpacity(){
		circuitAmpacity = 0;
		Conduitable conduitable = _getConduitable();
		Size size;
		if(usingCable)
			size = cable.getPhaseConductorSize();
		else
			size = phaseAConductor.getSize();
		double factor1;
		if(ocdp.is100PercentRated())
			factor1 = conduitable.getCompoundFactor();
		else
			factor1 = Math.min(1 / load.getMCAMultiplier(), conduitable.getCompoundFactor());
		if(factor1 == 0) //this should never happen
			return false;

		if (terminationTempRating != null) {
			//termination temperature rating is known
			if(terminationTempRating.getValue() >= conduitable.getTemperatureRating().getValue()) {
				circuitAmpacity = ConductorProperties.getAmpacity(
						size,
						conduitable.getMetal(),
						conduitable.getTemperatureRating()
				) * factor1 * numberOfSets;
				return true;
			}
			/*conductor temperature rating is higher than equipment
			temperature rating. Applying rule 310.15(B)*/
			double ampacity1 = ConductorProperties.getAmpacity(
					size,
					conduitable.getMetal(),
					conduitable.getTemperatureRating()
			);
			double corrected_amp1 = ampacity1 * factor1;
			double ampacity2 = ConductorProperties.getAmpacity(
					size,
					conduitable.getMetal(),
					terminationTempRating
			);
			if(corrected_amp1 <= ampacity2){
				circuitAmpacity = ampacity2 * numberOfSets;
				return true;
			}
			circuitAmpacity = ConductorProperties.getAmpacity(
					size,
					conduitable.getMetal(),
					terminationTempRating
			) * numberOfSets;
		}
		else {
			/*termination temperature rating is unknown*/
			//future: implement 110.14(C)(1)(4) motors design letter B, C or D..
			TempRating t_rating;
			double loadCurrentPerSet = load.getNominalCurrent() / numberOfSets;
			if(loadCurrentPerSet <= 100)
				t_rating = TempRating.T60;
			else {
				if(conduitable.getTemperatureRating().getValue() >= 75)
					t_rating = TempRating.T75;
				else
					t_rating = TempRating.T60;
			}
			factor1 = conduitable.getCompoundFactor(t_rating);
			circuitAmpacity = ConductorProperties.getAmpacity(
					size,
					conduitable.getMetal(),
					t_rating
			) * factor1 * numberOfSets;
		}
		return true;
	}

	/**Calculates the size of the neutral conductor if present. Sets all the
	neutral wires to this size if the system has neutrals.
	Calculation is based on:
	-If the load does not have neutral, return.
	-If the load is 3φ-4w and nonlinear, calculate the size of the neutral
	conductor based on the load neutral current, per ampacity and per voltage
	 drop.
	-If the load is linear, the size of the neutral will be the same as the
	one of the phase conductor.
	*/
	private boolean calculateNeutral(){
		if(!load.getVoltageSystem().hasNeutral())
			return true;
		/*Determining the size of the neutral*/
		Size neutralSize;
		if(load.isNonlinear() && load.getVoltageSystem().getPhases() == 3) {
			Size sizePerAmpacity= getSizePerAmpacity(true);
			if(sizePerAmpacity == null)
				return false;
			Size sizePerVoltageDrop = getSizePerVoltageDrop(true);
			if(sizePerVoltageDrop == null)
				return false;
			neutralSize = ConductorProperties.getBiggestSize(sizePerAmpacity,
					sizePerVoltageDrop);
		}
		else
			neutralSize = usingCable ? cable.getPhaseConductorSize():
					phaseAConductor.getSize();

		/*update the size of all neutral conductors*/
		if(usingCable)
			conduitables.forEach(conduitable ->
					((Cable) conduitable).setNeutralConductorSize(neutralSize));
		else
			neutralConductor.setSize(neutralSize);
		return true;
	}

	/**Quedé aquí 1
	 Calculates the size of the Equipment Grounding Conductor (EGC) of this
	 circuit and updates its size, number and requirements, for regular
	 conductors or cables.<br>
	 Notice that in {@link #prepareConduitableList()} this Circuit class
	 always add one ECG to each set of conductors no matter if they are in a
	 private or shared conduit, private or shared bundle or in free air.
	 Notice also that cables will always have one EGC.<br>
	 So, if this circuit has 4 sets in one private conduit, said conduit will
	 have 4 EGC after the call to {@link #prepareConduitableList()}. Notice
	 that the field list {@link #conduitables} always contains the maximum
	 number of conductors in one private/shared conduit or in one
	 private/shared bundle, no matter how many conduits or bundles the
	 circuit actually has, or all the conductors of all combined sets when
	 the circuit is in free air.<br><br>

	 For the calculation of the EGC, this Circuit class will have a method
	 called setOneEGCPerConduit(boolean) which will set the private field
	 flag oneEGCPerConduit (its default value is false).<br>
	 This flag is meaningless when the circuit is in free air or in bundle
	 .<br>
	 Let's define <b>size1</b> as the size of the EGC determined per NEC table
	 250.122, using the rating of this circuit's OCPD.<br>
	 Let's define <b>size2</b> as the size of the EGC determined per NEC table
	 250.122 but using the rating of the OCPD that would be required to
	 protect the conductors in a cable, based on the nominal ampacity of the
	 cable at 75°C. For example, a 1/0-3 AL MC cable will have (3) #1/0 AWG AL
	 conductors. Since they have a nominal ampacity of 120 Amps @ T75, size2
	 would be #6 AWG copper or #4 AWG aluminum. It is assumed that this is
	 the way the industry sizes the EGC in cables.<br><br>

	 <b>Circuit in a private conduit:</b><br>
	 -<u>oneEGCPerConduit is false:</u> each set of conductors will always
	 have its own EGC. There will be as many EGC as sets are in the private
	 conduit. The size of the EGC is size1 for each set of insulated
	 conductors and for each cable.<br>
	 -<u>oneEGCPerConduit is true:</u> the circuit will always add only one EGC
	 of size1 in each conduit when using insulated conductors or when using
	 cables. Additionally, When using cables, the EGC of the cable will be of
	 size2.<br><br>

	 <b>Circuit in a shared conduit:</b><br>
	 -<u>oneEGCPerConduit is false:</u> each set of conductors will always
	 have its own EGC. There will be as many EGC as sets are in the shared
	 conduit. The size of the EGC is size1 for each set of insulated
	 conductors and for each cable.<br>
	 -<u>oneEGCPerConduit is true:</u> the circuit will add one insulated EGC of
	 size1 to the shared conduit if the shared conduit does not have an
	 insulated EGC, or if its biggest existing insulated EGC is smaller than
	 size1. Notice that the circuit could not add any EGC to the shared
	 conduit because there is already one EGC that is big enough to comply
	 with table 250.122 as the EGC for this circuit. If this circuit is
	 using cables, the EGC of the cable will be of size2.<br><br>

	 Notice that in both cases, the requirements for the EGC of the circuit
	 are satisfied. However, when an insulated EGC in a shared conduit
	 changes its size or metal, all circuits using that shared conduit must
	 be notified so they can update their insulated EGC accordingly.
	 Todo: check that events are in place.
	 Notice also that a shared conduit used by several circuits could end up
	 with multiple EGC, because for example one circuit was using one EGC per
	 set or because the existing EGC where not enough for another circuit and
	 this one has to add an EGC of proper size. For situation like this, the
	 class Conduit offers the methods
	 {@link ROConduit#getTradeSizeForOneEGC()} and
	 {@link ROConduit#getOneEGCSize()}. They will provide the proper
	 conduit and EGC sizes when we want to use only one EGC.<br><br>


	 , no matter how many sets are in the conduit and, when
	 using a
	 shared conduit, no matter if an EGC is already present.<br>
	 The Circuit class can properly correct the does not change the size of
	 any existing EGC in
	 that 	 shared conduit.


	 Notice
	 The calculation is as follows: (Method #1)
	 <ol>
	 <li>The size_1 is computed based on the OCPD rating (NEC 250.122).</li>

	 <li>If the size of the conductor was decided because of the
	 voltage drop, the EGC is increased per 250.122(B). Notice that if the
	 size is increased for spare use, the EGC should also be increase per
	 250.122(B), this must be done outside of the circuit class until this
	 feature is implemented by adding a method that overrides the size of the
	 circuit to be bigger than the calculated size</li>Future: to be implemented.

	 <li>For circuit using conductors: the groundingConductor field is updated,
	 with size_1 which in turn will update the rest of EGC via its listener
	 .</li>

	 <li>For circuits using cables: a size_2 is computed based on the nominal
	 ampacity of the phase conductors in the cable; this ampacity is used to
	 request the rating of the OCPD which in turn will define the size
	 size_2 of the EGC through table NEC 250-122.<br>

	 Two cases appear here:<br>
	 (a) The cables are in free air or in bundle: use size_2.<br>
	 (b) The cables are in a conduit:<br>
	 ---(1) if size_1 is smaller than the phase conductors of the cable, use
	 size_1;<br>
	 ---(2) otherwise, use size_2 and:<br>
	 -----(a) if there is no other insulated EGC (not in cable) in the conduit
	 that is equal or bigger than size_1, a warning message must be added
	 and it must indicate that an additional EGC of size_1 is required.<br>

	 Todo: evaluate if at this approach can be done: (Method #2)<br>
	 (this will comply with NEC 250.122(F)(2)(b) and (d))<br>
	 The circuit can have a flag that indicates that only one EGC will be used.<br>
	 If flag is true:<br>
	 Once the size_1 is calculated:<br>
	 -size the EGC in each cable as size_2;<br>
	 -remove all the insulated EGC from the conduitable list in the conduit,
	 leaving only one insulated EGC of size_1<br>
	 -add a warning message that only one EGC complying with 250.122 is in
	 the conduit.<br>

	 If flag is false:<br>
	 proceed as described before in method #1, using several EGC in the conduit.<br>

	 TODO conduct tests
	 <br>
	 </li>
	 </ol>

	 -If the load is a motor, apply particular rule 250.122(D)(2)
	 -When using cord and fixture wire, use rule 250.122(E)
	 -Circuits in parallel: rule 250.122(F)(1) or (2):
	 ****this rule changed in the NEC 2017************
	 -single raceway: single EGC is permitted based on table 250.122.
	 -multiple raceways: each raceway must have an EGC sized per 250.122

	 */
	private boolean calculateEGC(){
		return true;
	}

	/**
	 @return The size of this circuit phase conductors/cables properly
	 calculated per ampacity and voltage drop.
	 <p>After calling this method, all components of this circuit will be
	 calculated: the size of all conductors and cables including neutrals (if
	 present) and groundings; the OCPD rating; the conduit trade size (if
	 present).
	 <p>To get the size of the neutral use
	    <code>getNeutralConductor().getSize()</code>.
	 <p>To get the size of the grounding conductor use
	    <code>getGroundingConductor().getSize()</code>.
	 <p>To get the rating of the OCPD use
	    <code>getOCPD().getStandardRating()</code>.
	 <p>To get the trade size of the conduit (if present) use
	    <code>getPrivateConduit().getTradeSize()</code> or
	    <code>getSharedConduit().getTradeSize()</code> accordingly.
	 */
	public Size getCircuitSize(){
		if(calculateCircuit()/*calculatePhase()*/) {
			if(usingCable)
				return cable.getPhaseConductorSize();
			else
				return phaseAConductor.getSize();
		}
		return null;
	}

	/**
	 @return A multiline string describing this circuit, as follow:<br>
	 <p>- First line, the load description.
	 <p>- Second line, the description of the conductors/cable
	 <p>- Third line, the description of the conduits
	 <p>- Four line, the description of the system voltage, phases, wires and OCPD
	 <p>- Fifth line, the circuit number, including the panel name
	 */
/*
future: implement circuit descriptor string
Circuits should return a string composed of several lines (separated by
returns and line feed), of the form:
First line, circuit description:
	"POOL HEATER"
Second line, configuration:
	"(3) #8 AWG THHN (AL) + #10 AWG THHN (CU)(NEU) + #12 AWG THHN (CU)(GND) IN 2" EMT CONDUIT" or
	"(3) SETS OF (4) 250 KCMIL THHW (CU) + #1/0 AWG THHW (CU)(GND) IN 4" EMT CONDUIT" or
	"MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND) IN FREE AIR or
	2" EMT CONDUIT or IN CABLE TRAY"
Third line, circuit ratings:
	"208 VOLTS 3ⱷ 3W 125 AMPS DPH-24,26,28"
*/
	public String getDescription(){
		return null;
	}

	/**
	 Sets the number of sets (of conductors or cables) in parallel. If the given
	 number of sets is different from the actual value, the new quantity is
	 assigned and the number of conduits is reset to match this quantity. To
	 change the number of conduits call the methods {@link #morePrivateConduits()} or
	 {@link #lessPrivateConduits()}. The default behavior of this class is having one
	 set of conductors per conduit, unless the said methods are called. If the
	 circuit is in shared conduit mode no action is performed.
	 Only one set is allowed when using a shared conduit or shared bundle.
	 @param numberOfSets The new number of sets.
	 */
	public void setNumberOfSets(int numberOfSets){
		if(this.numberOfSets == numberOfSets)
			return;
		this.numberOfSets = numberOfSets;
		//get ready for for when the circuit uses conduit...
		setsPerPrivateConduit = numberOfSets / numberOfPrivateConduits;
		prepareConduitableList();
	}

	/**
	 Returns the number of sets (of conductors or cables) in parallel of this
	 circuit.
	 @return The number of sets in parallel.
	 */
	public int getNumberOfSets(){
		return numberOfSets;
	}

	/**
	 @return The actual number of private conduits if the circuit is in
	 private conduit mode, zero otherwise.
	 Notice that the number or conduits changes by changing the number of sets,
	 or by calling {@link #morePrivateConduits()} or {@link #lessPrivateConduits()} while
	 circuit is in private conduit mode.
	 */
	public int getNumberOfPrivateConduits(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return numberOfPrivateConduits;
		return 0;
	}

	/**
	 Gets the temperature rating of the equipment this circuit serves. This
	 value defines the temperature rating of the circuit itself. It can be
	 60°C, 75°C or unknown (null value). Notice the NEC does not recognize
	 equipment rated for 90°C and so this property doesn't accept that value.
	 <p>If the temperature rating is unknown (the default) the property is null
	 and the circuit ampacity is calculated based on NEC 110.14(C).
	 @return The temperature rating of the equipment that this circuit serves.
	 */
	public TempRating getTerminationTempRating() {
		return terminationTempRating;
	}

	/**
	 Sets the temperature rating of the equipment that this circuit serves. The
	 allowed values as recognized by the NEC are 60°C, 75°C &#38; 90°C and even
	 unknown (for this purpose, null value correspond to unknown).
	 @param terminationTempRating The temperature rating: 60°C, 75°C, 90°C or
	 null for unknown.
	 */
	public void setTerminationTempRating(TempRating terminationTempRating) {
		if(this.terminationTempRating == terminationTempRating)
			return;
		this.terminationTempRating = terminationTempRating;
		circuitChangedRecalculationNeeded = true;
	}

	/**
	 @return A read-only conductor object that represents all the phases
	 conductors in this circuit when this circuit is using insulated
	 conductors (not cables).
	 */
	public RoConductor getPhaseConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		calculateCircuit();
		return phaseAConductor;
	}

	/**
	 @return A conductor object that represents all the neutral conductors in
	 this circuit when this circuit is using insulated conductors (not
	 cables).
	 <p>The returned value can be null which means that this circuit is using
	 cables or that this circuit does not need a neutral conductor.
	 <p>To get the cable neutral conductor when this circuit is using cables
	 refer to {@link #getCable()}
	 */
	public RoConductor getNeutralConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		calculateCircuit();
		return neutralConductor;
	}

	/**
	 @return A conductor object that represents all the grounding conductors in
	 this circuit when this circuit is using insulated conductors (not cables).
	 */
	public RoConductor getGroundingConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		calculateCircuit();
		return groundingConductor;
	}

	/**
	 @return The private conduit of this circuit as a read-only conduit when
	 this circuit is in private conduit circuitMode.
	 */
	public ROConduit getPrivateConduit(){
		resultMessages.remove(ERROR280);
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT) {
			resultMessages.add(ERROR280);
			return null;
		}
		calculateCircuit();
		return privateConduit;
	}

	/**
	 Sets the minimum trade size for this circuit's private conduit.
	 @param minimumTrade The trade size to be set as minimum.
	 @see Trade
	 */
	public void setPrivateConduitMinimumTrade(Trade minimumTrade) {
		if(privateConduit.getMinimumTrade() == minimumTrade)
			return;
		privateConduit.setMinimumTrade(minimumTrade);
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets this circuit's private conduit type.
	 @param type The new private conduit type.
	 @see Type
	 */
	public void setPrivateConduitType(Type type) {
		if(privateConduit.getType() == type)
			return;
		privateConduit.setType(type);
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets/unsets this circuit's private conduit as a nipple.
	 @param isNipple True if it's a nipple, false otherwise.
	 @see Trade
	 */
	public void setPrivateConduitNipple(boolean isNipple) {
		if(privateConduit.isNipple() == isNipple)
			return;
		privateConduit.setNipple(isNipple);
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets this circuit's private conduit rooftop condition. If this
	 circuit is not using a private conduit nothing is changed.
	 @param roofTopDistance The distance in inches above roof to bottom of this
	 circuit's private conduit. If a negative value is indicated, the behavior
	 of this method is the same as when calling resetRoofTop, which
	 eliminates the rooftop condition from the conduit.
	 */
	public void setPrivateConduitRoofTopDistance(double roofTopDistance) {
		privateConduit.setRoofTopDistance(roofTopDistance);
	}

	/**
	 Resets the rooftop condition for this circuit's private conduit, that is,
	 no roof top condition.
	 */
	public void resetPrivateConduitRoofTop() {
		if(privateConduit.getRoofTopDistance() == -1)
			return;
		privateConduit.resetRoofTop();
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitChangedRecalculationNeeded = true;
	}

	/**
	 @return The private bundle of this circuit as a read-only bundle when
	 this circuit is in private bundle circuitMode.
	 */
	public ROBundle getPrivateBundle(){
		resultMessages.remove(ERROR282);
		if(circuitMode != CircuitMode.PRIVATE_BUNDLE){
			resultMessages.add(ERROR282);
			return null;
		}
		calculateCircuit();
		return privateBundle;
	}

	/**
	 Sets the bundling length of the private bundling. If this circuit is not
	 using a private bundling nothing is changed.
	 @see Bundle#setBundlingLength(double)
	 @param length The length of the bundling in inches.
	 */
	public void setPrivateBundleLength(double length){
		if(privateBundle.getBundlingLength() == length)
			return;
		privateBundle.setBundlingLength(length);
		if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			circuitChangedRecalculationNeeded = true;
	}


	/**
	 @return A cable as a shareable object, representing all the cables of this
	 circuit, when this circuit is using cables (not insulated conductors).
	 Any change done through this reference will be applied to all cables used
	 by this circuit.
	 */
	public ROCable getCable(){
		resultMessages.remove(ERROR286);
		if(!usingCable) {
			resultMessages.add(ERROR286);
			return null;
		}
		calculateCircuit();
		return cable;
	}

	/**
	 @return This circuit's load. Use the returned load object to set up the
	 voltage system of this circuit.
	 */
	public Load getLoad(){
		return load;
	}

	/**
	 Indicates if this circuit will use cable or conductors.
	 @param usingCable True if using cables, false if using conductors.
	 */
	public void setUsingCable(boolean usingCable) {
		if(usingCable == this.usingCable)
			return;
		this.usingCable = usingCable;
		prepareSetOfConductors();
	}

	/**
	 @return True if this circuit is using cables, false if using conductors.
	 */
	public boolean isUsingCable(){
		return usingCable;
	}

	/**
	 @return The circuit mode for this circuit.
	 @see CircuitMode
	 */
	public CircuitMode getCircuitMode() {
		return circuitMode;
	}

	/**
	 @return The number of sets of conductors/cables per conduit. This makes
	 sense only when the circuit is using a private or shared conduit.
	 */
	public int getSetsPerPrivateConduit() {
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return setsPerPrivateConduit;
		return 0;
	}

	/**
	 @return The shared conduit used by this circuit.
	 @see #setConduitMode(Conduit)
	 */
	public Conduit getSharedConduit() {
		calculateCircuit();
		return sharedConduit;
	}

	/**
	 @return The shared bundle used by this circuit.
	 @see #setBundleMode(Bundle)
	 */
	public Bundle getSharedBundle() {
		calculateCircuit();
		return sharedBundle;
	}

	/**
	 @return The length of this circuit, in feet.
	 */
	public double getCircuitLength(){
		if(usingCable)
			return cable.getLength();
		return phaseAConductor.getLength();
	}

	/**
	 Sets the length of the conductors or the cables used by this circuit.
	 */
	public void setLength(double length){
		if(_getConduitable().getLength() == length)
			return;
		_getConduitable().setLength(length);
		circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets the ambient temperature for all the conduitables in this circuit.
	 @param temperature The ambient temperature in degrees Fahrenheits.
	 */
	public void setAmbientTemperatureF(int temperature){
		if(_getConduitable().getAmbientTemperatureF() == temperature)
			return;
		_getConduitable().setAmbientTemperatureF(temperature);
		circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets the insulation for all the conduitables in this circuit.
	 @param insul The new insulation
	 @see Insul
	 */
	public void setInsulation(Insul insul){
		if(_getConduitable().getInsulation() == insul)
			return;
		_getConduitable().setInsulation(insul);
		circuitChangedRecalculationNeeded = true;
	}

	/**
	 Sets the metal for all conductors of this circuit.
	 @param metal The new metal.
	 @see Metal
	 */
	public void setMetal(Metal metal){
		if(_getConduitable().getMetal() == metal)
			return;
		_getConduitable().setMetal(metal);
		circuitChangedRecalculationNeeded = true;
	}

	/** Returns Conduitable interface to this circuit's internal cable or
	 phase A conductor.
	 */
	private Conduitable _getConduitable(){
		return usingCable? cable: phaseAConductor;
	}

	/**
	 @return A read-only Conduitable object for this circuit's internal cable or
	 phase A conductor, whichever is in use.
     */
	public ROConduitable getConduitable(){
		calculateCircuit();
		return usingCable? cable: phaseAConductor;
	}

	/**
	 @return The OCPD object that serves this circuit.
	 */
	public OCPD getOcdp() {
		calculateCircuit();
		return ocdp;
	}
}

/*
* Circuit class should account for "class A" type circuits (study about this), since the type or class is also related to restriction for
* combining with other classes.
* */

/*
When designing a circuit, the goal is to determine:
	1-The proper size of the conductors and eventually the number of them, based on the load characteristics, and other parameters.
	2-The size of the conduit(s), to accommodate these conductors, also taking into consideration other parameters.
	3-The type and size of the overcurrent protection device, to protect the conductor and the load against overcurrents, based on the
	above parameter and others (continuous nature of the load, type, special requirements, etc.)

The first things to know are the load characteristics:
	1. Nominal voltage, phases, number of wires.
	2. Its nominal current or apparent power (va)
	3. Its power factor.
	4. The type of load (motor, appliance, receptacle, light, A/C, heater, etc.)
	5. If the load is continuous or not, which result from the combination of the following criteria: application scenario, load type, or
		engineering criteria.
This can be encapsulated in a class "Load".
This object must be able to provide its MCA (minimum conductor ampacity) and MOCPD (maximum overcurrent protection device, to protect the
load only).

A load can take different behaviors and properties:
	1. CableType: as group by the NEC.
	2. Grouping: a load can be made out of one or more "atomic loads". For example, a group of 10 receptacles forms a 1800va load or one
	dedicated receptacle for a specific 600va fridge. One could say also that a load can be "distributed" or "localized".

As the concept of load is very broad, the best approach is to create a base class Load that will have all the common properties and
behaviors of all the loads. By inheriting from that class, one can create more specialized cases.
*/

/*
Class Feeder, Service, Branch and Tap:
--------------------------------------
-These classes are similar. They differ in the fact that the branch circuit directly feeds a load, while a feeder has a OCPD on each end.
A special Feeder is the Service class.
SOme of the properties of these classes are:
-Voltage
-Phases,
-Frequency

The user must put the class CircuitConductor in the context of any of the classes Feeder, Service or Branch.

For instance, the Branch class has a load object. The Feeder has a load intent. One or more branch circuits will always be connected to a
feeder through an OCPD.

Branch circuits can be multiwire, feeders as well.
Loads can be continuous or non continuous.

Other classes must be designed, like for fuses, breakers, loads, motors, appliances, whatever, lights, AC equipment, panel, switchboard,
etc, etc.

A panel is somehow a load. it should also be treated as a load.
*/