package eecalcs.circuits;

import eecalcs.conductors.*;
import eecalcs.conduits.*;
import eecalcs.loads.Load;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import eecalcs.voltagedrop.ShareableVoltDrop;
import eecalcs.voltagedrop.VoltDrop;
import tools.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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

 <li><b>Help determining the correct insulation of the conductor:</b> based on
 the location condition (dry, wet, damp), it will suggest an appropriate
 conductor insulation.</li>
 </ol>

 <u>The physical objects that conform a circuit object are:</u>
 <ul>
 <li>One or more set of conductors or cables in parallel (conductors only or
 cables only, not a mix of them).</li>

 <li>One overcurrent protection device (OCPD).</li>

 <li>One connected load which is selected by the user.</li>

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
	private static final int maxNumberOfSets = 10;
	//list of all conduitables owned by this circuit.
	private List<Conduitable> conduitables = new ArrayList<>();
	//the overcurrent protection device owned by the circuit.
	private Ocpd ocdp = new Ocpd();
	//the conduit used by this circuit that can be shared with other circuits.
	private Conduit privateConduit = new Conduit();//never null, but can be empty, default CableType.PVC, Conduit.Nipple.No
	private Conduit sharedConduit; //is null or references an external conduit
	private Bundle privateBundle = new Bundle(null, 0, 0);//never null, but can be empty
	private Bundle sharedBundle;   //is null or references an external bundle
	private Load load;
	private int numberOfSets = 1;
	private int setsPerConduit = 1; //also means sets per bundle
	private int numberOfConduits = 1;
	private int conductorsPerSet; //meaningful only when using conductors
	private Conductor phaseAConductor = new Conductor();
	private Conductor phaseBConductor;
	private Conductor phaseCConductor;
	private Conductor neutralConductor;
	private Conductor groundingConductor = new Conductor().setRole(Conductor.Role.GND);
	private Cable cable = new Cable();
	private boolean usingCable = false;
	private VoltDrop voltageDrop = new VoltDrop();
	private TempRating terminationTempRating;
	private boolean neutralCurrentCarrying = false;
	//private static Message ERROR200	= new Message("The provided load is not valid.", -200);
	private static Message ERROR210	= new Message("More than one set of conductors or cables cannot"
			+ " be in a shared conduit.",-210);
	private static Message ERROR220	= new Message("More than one set of conductors or cables cannot"
			+ " be in a shared bundle.",-220);
	private static Message ERROR230	= new Message("The provided shared conduit is not valid.",-230);
	private static Message ERROR240	= new Message("The provided shared bundle is not valid.",-240);
	private static Message ERROR250	= new Message("Changing the number of conduits is only allowed when in"
			+ " private circuitMode.",-250);
	private static Message ERROR260	= new Message("Ampacity of the load is to high. Increment the number of " +
			"sets, or use less sets per conduit.",-260);
	private static Message ERROR270	= new Message("Paralleled power conductors in sizes smaller than 1/0 AWG"
			+ " are not permitted. NEC-310.10(H)(1)",-270);
	private static Message ERROR280	= new Message("Private conduit is available only in private conduit " +
			"circuitMode.",-280);
	private static Message ERROR282	= new Message("Private bundle is available only in private bundle " +
			"circuitMode.",-282);
	private static Message ERROR284	= new Message("Circuit phase, neutral and grounding insulated conductors " +
			"are available only when using conductors, not when using cables.",-284);
	private static Message ERROR286	= new Message("Circuit cables are available only when using cables, not " +
			"when using conductors", -286);
	private static Message ERROR290	= new Message("Temperature rating of conductors or cable is not suitable " +
			"for the conditions of use", -290);
	private static Message WARNN200	= new Message("Insulated conductors are being used in free air. This"
			+ " could be considered a bad practice.", 200);
	private static Message WARNN205	= new Message("Insulated conductors are being used in a bundle. This"
			+ " could be considered a bad practice.", 205);
	private static Message WARNN210	= new Message("Cables are being used in conduit. This could be an"
			+ " expensive practice.", 210);
	private static Message WARNN220	= new Message("Corrected and adjusted ampacity for this conductor/cable" +
			" temp rating exceeds the ampacity for the temperature rating of the termination.\n" +
			"Conductor/cable size has been selected per the termination temp rating.", 220);
	private static Message WARNN230	= new Message("Size of conductors/cable selected by the load MCA, to " +
			"satisfy NEC 219(A)(1) and 215.2(A)(1)", 230);

	//creates a listener to listen for changes in the neutral conductors. This listener is not used if the
	//load doesn't require a neutral.
	private Listener neutralListener = speaker -> {
		conduitables.forEach(conduitable ->
			{
				Conductor conductor = (Conductor) conduitable;
				conductor.getNotifier().enable(false);
				if(conductor.getRole() == Conductor.Role.NEUCC ||
						conductor.getRole() == Conductor.Role.NEUNCC)
					conductor.copyFrom(neutralConductor);
				else {
					conductor.setLength(neutralConductor.getLength());
					conductor.setInsulation(neutralConductor.getInsulation());
					conductor.setAmbientTemperatureFSilently(neutralConductor.getAmbientTemperatureF());
					conductor.setMetal(neutralConductor.getMetal());
				}
				conductor.getNotifier().enable(true);
			}
		);
	};

	/**
	 Container for messages resulting from validation of input variables and
	 calculations performed by this class.

	 @see ResultMessages
	 */
	public ResultMessages resultMessages = new ResultMessages();

	/**
	 Prepares the list of conduitables representing all the conduitables that
	 this circuit will have:<br>
	 - inside a conduit (private or shared), or<br>
	 - bundled together (private os shared), or<br>
	 - in free air.<br>
	 <p>The number of conduitables in the list will match the number of sets of
	 cables (when using cables), or the number of conductors per set (for
	 conductors).<br>
	 This method is called after the model conductor is updated by the
	 setupModelConductor() method, or whenever any of the following properties
	 changes:<br>
	 - numberOfSets & setsPerConduit from setNumberOfSets()<br>
	*/
	private void prepareConduitableList(){
		if(usingCable){
			conduitables.clear();
			conduitables.add(cable); //add the model cable to index 0
			while (conduitables.size() < setsPerConduit)//add the rest.
				conduitables.add(cable.clone());
		}else{//using conductors
			if(circuitMode == CircuitMode.SHARED_CONDUIT) {
				conduitables.forEach(conduitable -> {
					if(sharedConduit.hasConduitable(conduitable))
						sharedConduit.remove(conduitable);
				});
			}
			if(circuitMode == CircuitMode.SHARED_BUNDLE) {
				conduitables.forEach(conduitable -> {
					if(!sharedBundle.hasConduitable(conduitable))
						sharedBundle.remove(conduitable);
				});
			}
			conduitables.clear();
			conduitables.add(phaseAConductor);
			if(phaseBConductor != null)
				conduitables.add(phaseBConductor);
			if(phaseCConductor != null)
				conduitables.add(phaseCConductor);
			if(neutralConductor != null)
				conduitables.add(neutralConductor);
			conduitables.add(groundingConductor);
			while (conduitables.size()/conductorsPerSet < setsPerConduit){
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
		setMode();
	}

	/**
	 Creates the cable or the conductors to be used as the one cable of one set
	 of conductors for this circuit, or to be used as a model for two or more
	 sets for this circuit.<br>
	 The model is represented either by:<br>
	 <dl>
	 <dt>- The cable field, or</dt>
	 <dt>- A set of conductors conformed by:</dt>
	 <dd>- The phaseAConductor field</dd>
	 <dd>- The phaseBConductor field (if required)</dd>
	 <dd>- The phaseCConductor field (if required)</dd>
	 <dd>- The neutralConductor field (if required)</dd>
	 <dd>- The groundingConductor field</dd>
	 </dl>
	*/
	private void setupModelConductors(){
		//creates the neutral conductor if it doesn't exist, with the given role
		Consumer<Conductor.Role> setupNeutral = role -> {
			if (neutralConductor == null) {
				neutralConductor = new Conductor();
				neutralConductor.getNotifier().addListener(neutralListener);
			}
			neutralConductor.setRole(role).setSize(phaseAConductor.getSize());
		};
		//creates the phase B conductor, if it doesn't exist, as a HOT.
		Runnable setupPhaseB = () -> {
			if (phaseBConductor == null)
				phaseBConductor = new Conductor();
			phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
		};
		//creates the phase C conductor, if it doesn't exist, as a HOT.
		Runnable setupPhaseC = () -> {
			if (phaseCConductor == null)
				phaseCConductor = new Conductor();
			phaseCConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
		};
/*		//this code will never run, since no circuit is created with a a null load
		if(load == null || load.getVoltageSystem() == null) {
			resultMessages.add(ERROR200);
			return;
		}
		resultMessages.remove(ERROR200);*/
		VoltageSystemAC systemVoltage = load.getVoltageSystem();
		if(usingCable) { //the model uses a cable
			cable.setNeutralCarryingConductor(neutralCurrentCarrying);
			cable.setSystem(systemVoltage);
		}
		else { //the model uses conductors
			//1 hot + neutral, only
			if (systemVoltage == VoltageSystemAC.v120_1ph_2w
					|| systemVoltage == VoltageSystemAC.v277_1ph_2w
					|| systemVoltage == VoltageSystemAC.v208_1ph_2wN) { //208 high leg
				setupNeutral.accept(Conductor.Role.NEUCC);
				phaseBConductor = null;
				phaseCConductor = null;
			}
			//2 hots only
			else if (systemVoltage == VoltageSystemAC.v208_1ph_2w
					|| systemVoltage == VoltageSystemAC.v240_1ph_2w
					|| systemVoltage == VoltageSystemAC.v480_1ph_2w) {
				setupPhaseB.run();
				neutralConductor = null;
				phaseCConductor = null;
			}
			//2 hots + neutral, only
			else if (systemVoltage == VoltageSystemAC.v208_1ph_3w
					|| systemVoltage == VoltageSystemAC.v240_1ph_3w
					|| systemVoltage == VoltageSystemAC.v480_1ph_3w) {
				setupPhaseB.run();
				setupNeutral.accept(Conductor.Role.NEUCC);
				phaseCConductor = null;
			}
			//3 hots + neutral (if required), only
			else {
				setupPhaseB.run();
				setupPhaseC.run();
				if (systemVoltage.getWires() == 4) { //neutral is required
					setupNeutral.accept(neutralCurrentCarrying ?
							Conductor.Role.NEUCC : Conductor.Role.NEUNCC);
				}
				else //neutral is not required
					neutralConductor = null;
			}
			//phase A and ground always count, that why the 2 in the sum
			conductorsPerSet = 2 + (phaseBConductor == null ? 0 : 1)
					+ (phaseCConductor == null ? 0 : 1)
					+ (neutralConductor == null ? 0 : 1);
		}
		prepareConduitableList();
	}

	/**
	 Calculates the size of this circuit cable or conductor under the preset
	 conditions, that is able to handle its full load current (per ampacity).
	 It accounts for rules NEC 210.19(A) for branch circuits and 215.2(A) for
	 feeders.
	 @return The size of the conductors/cable calculated per ampacity or null if
	 an error occurred, in which case, check ResultMessages for errors and
	 warning messages.
	 */
	public Size getSizePerAmpacity(){ //96 lines of code
		Function<Size, Boolean> checkError260 = (size) ->{
			if(size == null) {
				resultMessages.add(ERROR260);
				return true;
			}
			return false;
		};
		Function<Size, Boolean> checkError270 = (size) ->{
			if(ConductorProperties.compareSizes(size, Size.AWG_1$0) < 0 && numberOfSets > 1) {
				resultMessages.add(ERROR270);
				return true;
			}
			return false;
		};
		//messages cleanup
		resultMessages.remove(WARNN220);
		resultMessages.remove(ERROR260);
		resultMessages.remove(ERROR270);
		resultMessages.remove(ERROR290);
		resultMessages.remove(WARNN230);
		Conduitable conduitable = getConduitable();//usingCable? cable: phaseAConductor;
		double factor1;
		if(ocdp.is100PercentRated())
			factor1 = conduitable.getCompoundFactor();
		else
			factor1 = Math.min(1 / load.getMCAMultiplier(), conduitable.getCompoundFactor());

		if(factor1 == 0) {//temp. rating of conductor not suitable for conditions of use.
			resultMessages.add(ERROR290);
			return null;
		}
		double loadCurrentPerSet = load.getCurrent() / numberOfSets;
		double lookup_current1 = loadCurrentPerSet / factor1;
		Size size1 = ConductorProperties.getSizeByAmperes(
				lookup_current1,
				conduitable.getMetal(),
				conduitable.getTemperatureRating()
		);
		if(checkError260.apply(size1))
			return null;
		if(terminationTempRating != null){ //termination temperature rating is known
			if(terminationTempRating.getValue() >= conduitable.getTemperatureRating().getValue()) {
				if(checkError270.apply(size1))
					return null;
				return size1;
			}
			//conductor temperature rating is higher than equipment temperature rating
			double ampacity1 = ConductorProperties.getAmpacity(
					size1,
					conduitable.getMetal(),
					conduitable.getTemperatureRating()
			);
			double corrected_amp1 = ampacity1 * factor1;
			double ampacity2 = ConductorProperties.getAmpacity(
					size1,
					conduitable.getMetal(),
					terminationTempRating
			);
			if(corrected_amp1 <= ampacity2){
				if(checkError270.apply(size1))
					return null;
				return size1;
			}

			double factor2 = conduitable.getCompoundFactor(terminationTempRating);
			double lookup_current2 = loadCurrentPerSet / factor2;
			Size size2 = ConductorProperties.getSizeByAmperes(
					lookup_current2,
					conduitable.getMetal(),
					terminationTempRating
			);
			if(checkError260.apply(size2))
				return null;
			if(checkError270.apply(size2))
				return null;
			return size2;
		}
		else {//termination temperature rating is unknown
			//todo implement: 110.14(C)(1)(4) motors design letter B, C or D...
			TempRating t_rating;
			if(loadCurrentPerSet <= 100)
				t_rating = TempRating.T60;
			else {
				if(conduitable.getTemperatureRating().getValue() >= 75)
					t_rating = TempRating.T75;
				else
					t_rating = TempRating.T60;
			}
			factor1 = conduitable.getCompoundFactor(t_rating);
			lookup_current1 = loadCurrentPerSet / factor1;
			size1 = ConductorProperties.getSizeByAmperes(
					lookup_current1,
					conduitable.getMetal(),
					t_rating
			);
			if(checkError260.apply(size1))
				return null;
			if(checkError270.apply(size1))
				return null;
			return size1;
		}
	}

	/**
	 Calculates the size of this circuit cable or conductor under the preset
	 conditions, that is able to handle its full load current (per ampacity).
	 It compares the ampacity of the calculated conductor size with the load MCA
	 and increases the size of the conductor to satisfy rules NEC 210.19(A)
	 for branch circuits and 215.2(A) for feeders.
	 If the number of sets is greater than one, and the resulting size is
	 smaller than 1/0 AWG , an error message will be issued.

	 @return The size of the conductors/cable calculated per ampacity or null if
	 an error is encountered.
	 */
	@Deprecated
	private Size getSizePerAmpacity_old(){
		//messages cleanup
		resultMessages.remove(WARNN220);
		resultMessages.remove(ERROR260);
		resultMessages.remove(ERROR270);
		resultMessages.remove(ERROR290);
		resultMessages.remove(WARNN230);

		//double loadCurrent = Math.max(load.getMCA(), load.getCurrent());;
		double currentPerSet = load.getCurrent() / numberOfSets;
		double compoundFactor;
		Metal conductorMetal;
		TempRating conductorTempRating;
		Function<Size, Boolean> checkError270 = (size) ->{
			if(ConductorProperties.compareSizes(size,Size.AWG_1$0) < 0
					&& numberOfSets > 1) {
				resultMessages.add(ERROR270);
				return true;
			}
			return false;
		};
		Function<Size, Boolean> checkError260 = (size) ->{
			if(size == null) {
				resultMessages.add(ERROR260);
				return true;
			}
			return false;
		};
		Function<Conduitable, Pair<TempRating, Double>> get_TR_and_Factor = (conduitable) -> {
			Pair<TempRating, Double> tempRFactor = new Pair<>();
			Insul previousInsul = conduitable.getInsulation();
			conduitable.getNotifier().enable(false);
			if(currentPerSet <= 100){
				conduitable.setInsulation(Insul.TW);
				tempRFactor.value1 = TempRating.T60;
			}else{ //>100 amps
				if(conduitable.getTemperatureRating().getValue() == 60)
					tempRFactor.value1 = TempRating.T60;
				else {
					conduitable.setInsulation(Insul.THW);
					tempRFactor.value1 = TempRating.T75;
				}
			}
			tempRFactor.value2 = conduitable.getCorrectionFactor() *
					conduitable.getAdjustmentFactor();
			conduitable.setInsulation(previousInsul);
			conduitable.getNotifier().enable(true);
			return tempRFactor;
		};
		Function<Conduitable, Double> getCompoundFactor = (conduitable) ->{
			Insul previousInsul = conduitable.getInsulation();
			conduitable.getNotifier().enable(false);
			if (terminationTempRating == TempRating.T60)
				conduitable.setInsulation(Insul.TW);
			else
				conduitable.setInsulation(Insul.THW);
			double cpFactor = conduitable.getCorrectionFactor() *
					conduitable.getAdjustmentFactor();
			conduitable.setInsulation(previousInsul);
			conduitable.getNotifier().enable(true);
			return cpFactor;
		};

	    if(usingCable){
			compoundFactor = cable.getCorrectionFactor() *
					cable.getAdjustmentFactor();
			conductorMetal = cable.getMetal();
			conductorTempRating = cable.getTemperatureRating();
		}
	    else {
			compoundFactor = phaseAConductor.getCorrectionFactor() *
					phaseAConductor.getAdjustmentFactor();
	    	conductorMetal = phaseAConductor.getMetal();
	    	conductorTempRating = phaseAConductor.getTemperatureRating();
		}

	    if(compoundFactor == 0) {//temp. rating of conductor not suitable for conditions of use.
			resultMessages.add(ERROR290);
			return null;
		}

		TempRating chosenTempRating;
		Size proposedSize;
		double ampacityOfProposed;
		double ampacityPerTerminationRating;
		double lookupAmperes = currentPerSet / compoundFactor;
		double loadMCAPerSet = load.getMCA() / numberOfSets;
	    if(loadMCAPerSet > lookupAmperes) {
			proposedSize = ConductorProperties.getSizeByAmperes(
					loadMCAPerSet,
					conductorMetal,
					conductorTempRating
			);
			if(checkError260.apply(proposedSize))
				return null;
			if(checkError270.apply(proposedSize))
				return null;
			resultMessages.add(WARNN230);
			return proposedSize;
		}

        if(terminationTempRating != null) { //temperature rating of the terminations is known
			if(compoundFactor != 1.0){ //310.15(B) and 110.14(C)
				proposedSize = ConductorProperties.getSizeByAmperes(
						lookupAmperes,
						conductorMetal,
						conductorTempRating
				);
				if(checkError260.apply(proposedSize))
					return null;
				ampacityOfProposed = ConductorProperties.getAmpacity(
						proposedSize,
						conductorMetal,
						conductorTempRating
				);
				ampacityPerTerminationRating = ConductorProperties.getAmpacity(
						proposedSize,
						conductorMetal,
						terminationTempRating
				);
				double correctedAdAdjustedAmpacity = ampacityOfProposed *
						compoundFactor;
				if(correctedAdAdjustedAmpacity <= ampacityPerTerminationRating) {
					if(checkError270.apply(proposedSize))
						return null;
					if(checkError260.apply(proposedSize))
						return null;
					return proposedSize;
				}
				//corrected and adjusted ampacity exceeds the ampacity for the termination rating
				if(usingCable){
					compoundFactor = getCompoundFactor.apply(cable);
				}
				else {
					compoundFactor = getCompoundFactor.apply(phaseAConductor);
				}
				lookupAmperes = currentPerSet / compoundFactor;
				proposedSize = ConductorProperties.getSizeByAmperes(
						lookupAmperes,
						conductorMetal,
						terminationTempRating
				);
				if(checkError260.apply(proposedSize))
					return null;
				if(checkError270.apply(proposedSize))
					return null;
				resultMessages.add(WARNN220);
				return proposedSize;
			}
			//no correction/adjustment factor required at this point
			if (terminationTempRating.getValue() >= conductorTempRating.getValue())
				chosenTempRating = conductorTempRating;
			else
				chosenTempRating = terminationTempRating;
			proposedSize = ConductorProperties.getSizeByAmperes(
					lookupAmperes,
					conductorMetal,
					chosenTempRating
			);
			if(checkError260.apply(proposedSize))
				return null;
			if(checkError270.apply(proposedSize))
				return null;
			return proposedSize;
        }
		//temperature rating of the terminations is not known at this point
		//implement: 110.14(C)(1)(4) motors design letter B, C or D...
		Pair<TempRating, Double> tempRFactor;

		if(usingCable){
			tempRFactor = get_TR_and_Factor.apply(cable);
		}
		else {
			tempRFactor = get_TR_and_Factor.apply(phaseAConductor);
		}
		chosenTempRating = tempRFactor.value1;//(TempRating) tempRFactor[0];
		compoundFactor = tempRFactor.value2;//(double) tempRFactor[1];
		if(compoundFactor == 0) {//temp. rating of conductor not suitable for conditions of use.
			resultMessages.add(ERROR290);
			return null;
		}
		lookupAmperes = currentPerSet / compoundFactor;
		proposedSize = ConductorProperties.getSizeByAmperes(
				lookupAmperes,
				conductorMetal,
				chosenTempRating
		);
		if(checkError260.apply(proposedSize))
			return null;
		if(checkError270.apply(proposedSize))
			return null;
		return proposedSize;
	}

	/**
	 Returns the number of current carrying conductors (of insulated conductors,
	 not of cables) inside the used raceway/bundle or in free, accordingly.

	 @return The number of current carrying conductors
	 */
	private int getCurrentCarryingNumber() {
		int numberOfCurrentCarrying;
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			numberOfCurrentCarrying = privateConduit.getCurrentCarryingNumber();
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			numberOfCurrentCarrying = sharedConduit.getCurrentCarryingNumber();
		else if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			numberOfCurrentCarrying = privateBundle.getCurrentCarryingNumber();
		else if(circuitMode == CircuitMode.SHARED_BUNDLE)
			numberOfCurrentCarrying = sharedBundle.getCurrentCarryingNumber();
		else {//it's in free air circuitMode
			numberOfCurrentCarrying = 0;
			for(Conduitable conduitable: conduitables)
				numberOfCurrentCarrying += conduitable.getCurrentCarryingCount();
		}
		return numberOfCurrentCarrying;
	}

	/**
	 Calculates the size of this circuit cable or conductor under the preset
	 conditions, that is able to keep its voltage drop below the maximum allowed
	 value.

	 @return The size of this circuit conductor/cable calculated per voltage
	 drop.
	 */
	public Size getSizePerVoltageDrop(){
		Conduit usedConduit;
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			usedConduit = privateConduit;
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			usedConduit = sharedConduit;
		else
			usedConduit = null;

		if(usingCable) {
			voltageDrop.setConductor(cable.getPhaseConductorClone());//for the sake of material and length
			if(usedConduit == null) //cables are in free air or bundled. Raceway type will depend on cable type
				voltageDrop.setConduitMaterial(cable.getType().getMaterial());
			else
				voltageDrop.setConduitMaterial(ConduitProperties.getMaterial(usedConduit.getType()));
		}
		else {
			voltageDrop.setConductor(phaseAConductor);//for the sake of material and length
			if(usedConduit == null) //conductors are in free air or bundled
				voltageDrop.setConduitMaterial(Material.PVC);
			else
				voltageDrop.setConduitMaterial(ConduitProperties.getMaterial(usedConduit.getType()));
		}
		voltageDrop.setLoadCurrent(load.getCurrent());
		voltageDrop.setPowerFactor(load.getPowerFactor());
		voltageDrop.setSets(numberOfSets);
		voltageDrop.setSourceVoltage(load.getVoltageSystem());
		return voltageDrop.getCalculatedSizeAC();
	}

	/**
	 Clears all the error and warning messages related to putting the circuit
	 in different modes (private/public conduit/bundle and free air).
	 */
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
	 Constructs a circuit object for the given load. This circuit creates and
	 holds a copy of the given load. This circuit's default values are as
	 follow:
	 <p>- One set of conductors.
	 <p>- Default conductors. See {@link Conductor#Conductor() Conductor()} for
	 default properties.
	 <p>- One private conduit. See {@link Conduit#Conduit(Type, Conduit.Nipple)}
	 for default properties.

	 @param load The load that will be copied as part of this circuit and that
	 will be served by this circuit.
	 @see Load
	 */
	public Circuit(Load load){
		if(load == null)
			throw new IllegalArgumentException("Load parameter cannot be null");

		this.load = load.clone();
		//if the load changes, the model of conductors needs to be set up
		this.load.getNotifier().addListener(
				speaker -> setupModelConductors()
		);
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
						//...while the others update some properties only (length, insulation, ambient temp., metal)
						conductor.setLength(phaseAConductor.getLength());
						conductor.setInsulation(phaseAConductor.getInsulation());
						conductor.setAmbientTemperatureFSilently(phaseAConductor.getAmbientTemperatureF());
						conductor.setMetal(phaseAConductor.getMetal());
						//the size property is updated for the neutral conductors but not updated for the
						//grounding conductors
						if (conductor.getRole() == Conductor.Role.NEUCC ||
								conductor.getRole() == Conductor.Role.NEUNCC)
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
					else {//while the others, only some of them (length, insulation and ambient temp.)
						conductor.setLength(groundingConductor.getLength());
						conductor.setInsulation(groundingConductor.getInsulation());
						conductor.setAmbientTemperatureFSilently(groundingConductor.getAmbientTemperatureF());
					}
					conductor.getNotifier().enable(true);
				}
			);
		});
		setupModelConductors();
	}

	@Override
	public Circuit clone(){
		//todo to be implemented.
		return null;
	}

	/**
	 Returns the voltage drop object used by this circuit for internal
	 calculations.
	 @return This circuit's voltage drop readonly object
	 */
	public ShareableVoltDrop getVoltageDrop(){
		return voltageDrop;
	}

	/* Sets the circuit mode */
	private void setMode(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			setConduitMode();
		else if(circuitMode == CircuitMode.FREE_AIR)
			setFreeAirMode();
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			setConduitMode(sharedConduit);
		else if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			setBundleMode();
		else //if(circuitMode == CircuitMode.SHARED_BUNDLE)
			setBundleMode(sharedBundle);
	}

	/**
	 Sets the circuit in free air. All conductors and cables are removed from
	 any shared or private conduit or bundle and put in free air. If the circuit
	 is made of insulated conductors (not cables) a warning is generated. After
	 calling this method, the circuit is in free air circuit mode.
	 */
	public void setFreeAirMode(){
		//This method must always run from top to bottom since the conduitable list might have changed.
		clearModeMsg();
		privateConduit.empty();
		privateBundle.empty();
		sharedConduit = null;
		sharedBundle = null;
		if(!usingCable) //conductors in free air
			resultMessages.add(WARNN200);
		circuitMode = CircuitMode.FREE_AIR;
	}

	/**
	 Sets the circuit in a non shared private conduit.
	 To change the distribution of sets per conduit or to change the number of
	 used conduits, call the method {@link #moreConduits()} or
	 {@link #lessConduits()}.
	 */
	public void setConduitMode(){
		//This method must always run from top to bottom since the conduitable list might have changed.
		clearModeMsg();
		privateConduit.empty();
		privateBundle.empty();
		sharedConduit = null;
		sharedBundle = null;
		conduitables.forEach(conduitable -> privateConduit.add(conduitable));
		if(usingCable) //using cables in conduit
			resultMessages.add(WARNN210); //cables in conduit
		circuitMode = CircuitMode.PRIVATE_CONDUIT;
	}

	/**
	 Sets the circuit in a shared public conduit.
	 Calling this method resets the number of sets to 1.

	 @param conduit The public conduit to which all the conduitables go in.
	 */
	public void setConduitMode(Conduit conduit){
		//even if the conduit is the same as the existing, this method must run from top to bottom since other
		//properties like neutralCurrentCarrying or numberOfSets or others might have change the conduitable list.
		clearModeMsg();
		if(conduit == null) {
			resultMessages.add(ERROR230);
			return;
		}
		circuitMode = CircuitMode.SHARED_CONDUIT;
		privateConduit.empty();
		privateBundle.empty();
		sharedConduit = conduit;
		sharedBundle = null;
		conduitables.forEach(conduitable -> sharedConduit.add(conduitable));
		if(usingCable) //using cables in conduit
			resultMessages.add(WARNN210); //cables in conduit
	}

	/**
	 Sets the circuit in a non shared private bundle.
	 */
	public void setBundleMode(){
		//This method must always run from top to bottom since the conduitable list might have changed.
		clearModeMsg();
		privateConduit.empty();
		privateBundle.empty();
		sharedConduit = null;
		sharedBundle = null;
		conduitables.forEach(conduitable -> privateBundle.add(conduitable));
		if(!usingCable) //using conductors in bundle
			resultMessages.add(WARNN205);
		circuitMode = CircuitMode.PRIVATE_BUNDLE;
	}

	/**
	 Sets the circuit in the given shared public bundle.

	 @param bundle The public bundle to which all the conduitables go in.
	 */
	public void setBundleMode(Bundle bundle){
		//This method must always run from top to bottom since the conduitable list might have changed.
		//properties like neutralCurrentCarrying or numberOfSets or others might have change the conduitable list.
		clearModeMsg();
		if(bundle == null) {
			resultMessages.add(ERROR240);
			return;
		}
		circuitMode = CircuitMode.SHARED_BUNDLE;
		privateConduit.empty();
		privateBundle.empty();
		sharedConduit = null;
		sharedBundle = bundle;
		conduitables.forEach(conduitable -> sharedBundle.add(conduitable));
		if(!usingCable) //using conductors in bundle
			resultMessages.add(WARNN205);
	}

	/**
	 Increments the number of conduits used by this circuit, when in private
	 conduit circuit mode. The resulting number of conduits depends on the
	 actual number of sets. The NEC allows to distribute the sets of conductors
	 in parallel in a way that the impedance of each sets is maintained equal on
	 each set.
	 */
	public void moreConduits(){
		resultMessages.remove(ERROR250);
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfConduits + 1; i <= numberOfSets; i++)
			if(numberOfSets % i == 0) {
				numberOfConduits = i;
				setsPerConduit = numberOfSets / numberOfConduits;
				prepareConduitableList();
				break;
			}
	}

	/**
	 Decrements the number of conduits used by this circuit, when in private
	 conduit circuitMode. The resulting number of conduits depends on the actual number
	 of sets. The NEC allows to distribute the sets of conductors in parallel in
	 a way that the impedance of each sets is maintained equal on each set. Keep
	 in mind that the number of conduits is reset to the same number of sets of
	 conductors (the default) whenever the number of sets changes or the circuit
	 circuitMode changes.
	 */
	public void lessConduits(){
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfConduits - 1; i != 0; i--)
			if(numberOfSets % i == 0) {
				numberOfConduits = i;
				setsPerConduit = numberOfSets / numberOfConduits;
				prepareConduitableList();
				break;
			}
	}

	/**
	 Returns the size of the circuit conductors/cables properly calculated per
	 ampacity and voltage drop. After calling this method, the size of all the
	 conductors conforming this circuit will be updated to match this returned
	 size. This is the size for the phase conductors and depending on the system
	 voltage (if the neutral is used) and the load (if it's linear or not) this
	 size will also correspond to the size of the neutral conductor.
	 The size of the neutral can be in some rare scenarios bigger than the size
	 of the phase conductors <b>to be confirmed, investigate about this</b>.
	 <p>To particularly obtain the size of the neutral use
	 <code>getNeutralConductor().getSize()</code>.
	 <p>To get the size of the
	 grounding conductor <code>getGroundingConductor().getSize()</code>.

	 @return The size of the conductors/cables.
	 */
	public Size getCircuitSize(){
		//1. Calculate phase conductors'size
		Size sizePerAmpacity = getSizePerAmpacity();
		if(sizePerAmpacity == null)
			return null;

		Size sizePerVoltageDrop = getSizePerVoltageDrop();
		if(sizePerVoltageDrop == null)
			return null;

		Size phasesSize;
		if(ConductorProperties.compareSizes(sizePerAmpacity, sizePerVoltageDrop) < 0) //VD size is bigger
			phasesSize = sizePerVoltageDrop;
		else
			phasesSize = sizePerAmpacity;
		//2. todo Calculate neutral conductor size if required
//		if (neutralConductor != null)
//			neutralConductor.setSize(phasesSize);
		//3. todo Calculate the size of the OCPD
		/*To keep in mind: once the circuit size has been calculated, the ocdp is determined to protect the
		* conductor (except for motors and other exceptions). The size of the ocpd is chosen per standard sizes
		* and the due to rule 240.4(D) and the type of load, the size of the conductor might be adjusted.
		*
		* For example: A continuous load of 16 amps will use a THHW (90°C) copper conductor, protected by an ocpd
		* rated for 75°C. The ambient temperature is 43°C. There are a total of 6 current-carrying conductors in the
		* raceway.
		* Question: What is the minimum size of conductor to be selected?
		* Answer: 14 AWG.
		* Now, per rue 210.20(A), the size of the ocpd must be 16*1.25 = 20 amps.
		* Because of the 240.4(D)(3) rule, the #14 conductor cannot protected by an ocpd larger than 15 amps. This
		* means the size of the conductor must be increased to #12.
		* */
		//set up the ocdp object with the size and the load
		//4. todo Calculate the size of the grounding conductor: keep in mind the rule that says that the EGC
		//    conductor must be increased if the hot conductors had to be increase for other reasons. rule 250.122(B)

//		Size groundingSize = ...
		//5. todo Update the size of all conductors
		voltageDrop.getConductor().setSize(phasesSize);
		if(usingCable) {
			conduitables.forEach(conduitable -> {
				Cable cable = (Cable) conduitable;
				cable.setPhaseConductorSize(phasesSize); //todo to be tested
//				cable.setNeutralConductorSize(neutralSize);
//				cable.setGroundingConductorSize(groundingSize);
			});
		}
		else {
			phaseAConductor.setSize(phasesSize);
			//todo implement
//			neutralConductor.setSize(neutralSize);
//			groundingConductor.setSize(groundingSize);
		}
		return phasesSize;
	}

/*	//**
	 Returns the size of the conduit(s) used by this circuit, if any.

	 @return The trade size of the conduit.
	 *//*
	public Trade getConduitTradeSize(){
		if(circuitMode == CircuitMode.FREE_AIR || circuitMode == CircuitMode.PRIVATE_BUNDLE || circuitMode == CircuitMode.SHARED_BUNDLE)
			return null;
		//the circuit here is in shared or public conduit circuitMode
		if(getCircuitSize() == null)
			return null;

		// todo refactor ShareableConductor interface to use only the necessary methods
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return privateConduit.getTradeSize();
		else
			return sharedConduit.getTradeSize();
	}*/

	/**
	 Returns a multiline string describing this circuit, as follow:<br>
	 <p>- First line, the load description.
	 <p>- Second line, the description of the conductors/cable
	 <p>- Third line, the description of the conduits
	 <p>- Four line, the description of the system voltage, phases, wires and OCPD
	 <p>- Fifth line, the circuit number, including the panel name

	 @return The string describing this circuit.
	 */
	//todo implement
	public String getDescription(){
		return null;
	}

	/**
	 Sets the number of sets (of conductors or cables) in parallel. If the given
	 number of sets is different from the actual value, the new quantity is
	 assigned and the number of conduits is reset to match this quantity. To
	 change the number of conduits call the methods {@link #moreConduits()} or
	 {@link #lessConduits()}. The default behavior of this class is having one
	 set of conductors per conduit, unless the said methods are called. If the
	 circuit is in shared conduit mode no action is performed.
	 Only one set is allowed when using a shared conduit or shared bundle.
	 @param numberOfSets The new number of sets.
	 */
	public void setNumberOfSets(int numberOfSets){
		if(this.numberOfSets == numberOfSets)
			return;
		this.numberOfSets = numberOfSets;
		setsPerConduit = numberOfSets / numberOfConduits;
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
	 Returns the actual number of conduits. Notice that the number or conduits
	 changes by changing the number of sets, or  by calling
	 {@link #moreConduits()} or {@link #lessConduits()} while in private conduit
	 circuitMode. Other methods that changes the number of conduits are the methods
	 {@link #setConduitMode()}, {@link #setConduitMode(Conduit)},
	{@link #setBundleMode()} or {@link #setBundleMode(Bundle)}.

	 @return The actual number of conduits used in private conduit circuitMode.
	 */
	public int getNumberOfConduits(){
		return numberOfConduits;
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
		this.terminationTempRating = terminationTempRating;
	}

	/**
	 Returns a conductor object that represents all the phases conductors in
	 this circuit when this circuit is using insulated conductors (not cables).
	 Any change done through this reference will be applied to all
	 phases conductors of this circuit. Any change to the properties:
	 insulation, length, ambient temperature and metal, will propagate to the
	 neutral conductor as well. The same will happen to the grounding conductor
	 except for the metal.

	 @return A phase conductor if the circuit is not using cables,
	 null otherwise.
	 */
	public ShareableConductor getPhaseConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		return phaseAConductor;
	}

	/**
	 Returns a conductor object that represents all the neutral conductors in
	 this circuit when this circuit is using insulated conductors (not cables).
	 Any change done through this reference will be applied to all
	 neutral conductors of this circuit. Any change to the properties:
	 insulation, length, ambient temperature and metal, will propagate to the
	 phase conductors as well. The same will happen to the grounding conductor
	 except for the metal.

	 @return A neutral conductor if the circuit is not using cables,
	 null otherwise.
	 */
	public ShareableConductor getNeutralConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		return neutralConductor;
	}

	/**
	 Returns a conductor object that represents all the grounding conductors in
	 this circuit when this circuit is using insulated conductors (not cables).
	 Any change done through this reference will be applied to all
	 grounding conductors of this circuit. Any change to the properties:
	 insulation, length and ambient temperature, will propagate to the phase and
	 neutral conductors as well.

	 @return A grounding conductor if the circuit is not using cables,
	 null otherwise.
	 */
	public ShareableConductor getGroundingConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		return groundingConductor;
	}

	/**
	 Returns the private conduit of this circuit as a shareable conduit when
	 this circuit is in private conduit circuitMode.

	 @return This circuit's private conduit.
	 */
	public ShareableConduit getPrivateConduit(){
		resultMessages.remove(ERROR280);
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT) {
			resultMessages.add(ERROR280);
			return null;
		}
		return privateConduit;
	}


	/**
	 Returns the private bundle of this circuit as a shareable bundle when
	 this circuit is in private bundle circuitMode.

	 @return This circuit's private conduit.
	 */
	public ShareableBundle getPrivateBundle(){
		resultMessages.remove(ERROR282);
		if(circuitMode != CircuitMode.PRIVATE_BUNDLE){
			resultMessages.add(ERROR282);
			return null;
		}
		return privateBundle;
	}

	/**
	 Returns a cable as a shareable object, representing all the cables of this
	 circuit, when this circuit is using cables (not insulated conductors).
	 Any change done through this reference will be applied to all cables used
	 by this circuit.

	 @return This circuit's cable if this circuit is using cables, null
	 otherwise.
	 */
	public ShareableCable getCable(){
		resultMessages.remove(ERROR286);
		if(!usingCable) {
			resultMessages.add(ERROR286);
			return null;
		}
		return cable;
	}

	/**
	 Sets the neutral conductor of this circuit (if present) as a
	 current-carrying conductor for 3-phase 4-wire systems.

	 @param flag True if the neutral is a current-carrying conductor,
	 false otherwise.
	 */
	public void setNeutralCurrentCarrying(boolean flag){
		if(flag == neutralCurrentCarrying)
			return;
		neutralCurrentCarrying = flag;
		if(load.getVoltageSystem().getPhases() == 3)
			setupModelConductors();
	}

	/**
	 Returns this circuit's load. Use the returned load object to set up the
	 voltage system of this circuit.

	 @return This circuit's load.
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
		setupModelConductors();
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
	public int getSetsPerConduit() {
		return setsPerConduit;
	}

	/**
	 @return The shared conduit used by this circuit. Keep in mind that the
	 conduit returned by this method is actually a ShareableConduit which is
	 mostly read-only. To gain full access to the share conduit, use the conduit
	 object passed to setConduitMode(Conduit) method.
	 @see #setConduitMode(Conduit)
	 */
	public ShareableConduit getSharedConduit() {
		return sharedConduit;
	}

	/**
	 @return The shared bundle used by this circuit. Keep in mind that the
	 bundled returned by this method is actually a ShareableBundle which is
	 mostly read-only. To gain full access to the share bundle, use the bundle
	 object passed to setBundleMode(Bundle) method.
	 @see #setBundleMode(Bundle)
	 */
	public ShareableBundle getSharedBundle() {
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
	 @return A Conduitable interface to this circuit's internal cable or phase A
	 conductor.
	 */
	public Conduitable getConduitable(){
		return usingCable? cable: phaseAConductor;
	}
}

/*
future Circuits should return a string composed of several lines (separated by returns and line feed), of the form:
     <p>&emsp;&emsp; First line, circuit description:  "POOL HEATER"
     <p>&emsp;&emsp; Second line, configuration:       "(3) #8 AWG THHN (AL) + #10 AWG THHN (CU)(NEU) + #12 AWG THHN (CU)(GND) IN 2" EMT CONDUIT" or
     <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;           "(3) SETS OF (4) 250 KCMIL THHW (CU) + #1/0 AWG THHW (CU)(GND) IN 4" EMT CONDUIT" or
     <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;           "MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND) IN FREE AIR or
     <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;           2" EMT CONDUIT or IN CABLE TRAY"
     <p>&emsp;&emsp; Third line, circuit ratings:      "208 VOLTS 3ⱷ 3W 125 AMPS DPH-24,26,28"

 */


/*
* CircuitOld class should account for "class A" type circuits (study about this), since the type or class is also related to restriction for
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