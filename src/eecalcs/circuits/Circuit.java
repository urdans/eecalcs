package eecalcs.circuits;

import eecalcs.conductors.*;
import eecalcs.conduits.*;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import eecalcs.voltagedrop.VoltDrop;
import tools.Listener;
import tools.Message;
import tools.ResultMessages;

import java.util.ArrayList;
import java.util.List;

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

//<li>The role of the circuit: feeder or a branch circuit.</li>

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

 <li>If the ocdp is 100% or 80% rated.</li>
 </ul>

 <p><b>Conduit sharing mechanism:</b></p>
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

 <b>Even more rare</b>:

 <li>One or more sets of insulated conductors in free air (no conduit; bundled
 or not; even though this is allowed, it would be considered a bad practice).
 </li>
 <li>More than one cable in parallel, using one cable per conduit.</li>
 <li>More than one cable in parallel, using one large conduit.</li>
 <li>More than one cable in parallel, using equal number of cables per
 conduit.</li>
 (Cables in conduits are permitted by code but are not common, are very
 expensive and could be considered a bad practice)
 </ol>

 <p>Out of this mechanism, there are three ways conduits are used by this class:
 <p>- Single conduit, (default) (applies for one set of conductors only).
 This conduit is owned by this class and can't be accessed from outside.
 <p>- Several conduits (applies for more than one set of conductors only).
 This conduit is owned by this class and can't be accessed from outside.
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
 The circuit is said to be in "conduit mode". The user can decide to switch back
 and forth from conductors to cables at any time.<br><br>
 ■ The user can increase and later decrease the number of sets. This is
 allowed only when in private conduit mode.<br><br>
 ■ The user can put the circuit in free air mode, or in bundle mode or back
 in conduit mode by calling the appropriated methods:
 <p>&nbsp - <em>SetFreeAirMode()</em> -> No conduit, no bundle, just in free air.
 No restriction. If insulated conductor are used, a warning message is
 generated.

 <p>&nbsp - <em>SetConduitMode()</em> -> Private conduit mode. No restriction. If
 cables are used, a warning message is generated.

 <p>&nbsp - <em>SetConduitMode(Conduit sharedConduit)</em> -> Shared conduit
 mode. sharedConduit must be a valid conduit and the number of sets must be one.
 If cables are used, a warning message is generated. If there are more than one
 set an error message is generated.

 <p>&nbsp - <em>SetBundleMode()</em> -> Bundle mode. No restriction. If
 insulated conductors are used, a warning message is generated.

 <p>&nbsp - <em>SetBundleMode(Bundle sharedBundle)</em> -> Shared bundle mode.
 No restriction. If insulated conductors are used, a warning message is
 generated.

 </pre>
 <br><br>
 ■ When in shared conduit mode the user increases the number of sets to more
 than one, it will switch to private conduit mode automatically. To return to
 shared mode, first the number of sets must return to one and then the method
 <em>SetConduitMode(Conduit sharedConduit)</em> must be called.<br><br>
 ■ Keep in mind that some actions are not accomplished due to code rules,
 for example trying to use conductors smaller thant 1/0 AWG in parallel. In
 that case, the returned value makes no sense, like zero or null. This is an
 indication of error. There can also be warnings. In all cases, the user should
 ask the resultMessage field for the presence of messages. Refer to
 {@link tools.ResultMessages ResultMessages} for details.<br><br>
 */
public class Circuit {
	private Mode mode = Mode.PRIVATE_CONDUIT;
	private static final int maxNumberOfSets = 10;
	//list of conduitables owned by this circuit.
	private List<Conduitable> conduitables = new ArrayList<>();
	//the overcurrent protection device owned by the circuit.
	private Ocpd ocdp = new Ocpd();
	//the conduit used by this circuit that can be shared with other circuits.
	private Conduit privateConduit = new Conduit();//never null, but can be empty, default Type.PVC, Conduit.Nipple.No
	private Conduit sharedConduit; //is null or references an external conduit
	private Bundle privateBundle = new Bundle(null, 0, 0);//never null, but can be empty
	private Bundle sharedBundle;   //is null or references an external bundle
	private Load load;
	private int numberOfSets = 1;
	private int setsPerConduit = 1;
	private int numberOfConduits = 1;
	private int conductorsPerSet; //meaningful only when using conductors
//	private VoltageSystemAC systemVoltage;
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
	//Output values
	private Size size;
//	private Trade tradeSize;
	private static Message ERROR200	= new Message("The provided load is not valid.", -200);
	private static Message ERROR210	= new Message("More than one set of conductors or cables cannot"
			+ " be in a shared conduit.",-210);
	private static Message ERROR220	= new Message("More than one set of conductors or cables cannot"
			+ " be in a shared bundle.",-220);
	private static Message ERROR230	= new Message("The provided shared conduit is not valid.",-230);
	private static Message ERROR240	= new Message("The provided shared bundle is not valid.",-240);
	private static Message ERROR250	= new Message("Changing the number of conduits is only allowed when in"
			+ " private mode.",-250);
	private static Message ERROR260	= new Message("Ampacity of the load is to high. Increment the number of sets.",-260);

	private static Message ERROR270	= new Message("Paralleled power conductors in sizes smaller than 1/0 AWG"
			+ " are not permitted. NEC-310.10(H)(1)",-270);
	private static Message WARNN200	= new Message("Insulated conductors are being used in free air. This"
			+ " could be considered a bad practice.", 200);
	private static Message WARNN205	= new Message("Insulated conductors are being used in a bundle. This"
			+ " could be considered a bad practice.", 205);
	private static Message WARNN210	= new Message("Cables are being used in conduit. This could be an"
			+ " expensive practice.", 210);
	private static Message WARNN220	= new Message("Corrected and adjusted ampacity for this conductor/cable" +
			" temp rating exceeds the ampacity for the temperature rating of the termination.\n" +
			"Conductor/cable size has been selected per the termination temp rating.", 220);

	private Listener phaseListener = speaker -> {
		conduitables.forEach(conduitable ->
			{
				Conductor conductor = (Conductor) conduitable;
				conductor.notifier.enabled(false);
				if(conductor.getRole() == Conductor.Role.HOT ||
						conductor.getRole() == Conductor.Role.NCONC)
					conductor.copyFrom(phaseAConductor);
				else {
					conductor.setLength(phaseAConductor.getLength());
					conductor.setInsulation(phaseAConductor.getInsulation());
					conductor.setAmbientTemperatureFSilently(phaseAConductor.getAmbientTemperatureF());
					conductor.setMetal(phaseAConductor.getMetal());
					if(conductor.getRole() == Conductor.Role.NEUCC ||
							conductor.getRole() == Conductor.Role.NEUNCC)
						conductor.setSize(phaseAConductor.getSize());
				}
				conductor.notifier.enabled(true);
			}
		);
	};
	private Listener neutralListener = speaker -> {
		conduitables.forEach(conduitable ->
			{
				Conductor conductor = (Conductor) conduitable;
				conductor.notifier.enabled(false);
				if(conductor.getRole() == Conductor.Role.NEUCC ||
						conductor.getRole() == Conductor.Role.NEUNCC)
					conductor.copyFrom(neutralConductor);
				else {
					conductor.setLength(neutralConductor.getLength());
					conductor.setInsulation(neutralConductor.getInsulation());
					conductor.setAmbientTemperatureFSilently(neutralConductor.getAmbientTemperatureF());
					conductor.setMetal(neutralConductor.getMetal());
				}
				conductor.notifier.enabled(true);
			}
		);
	};
	private Listener groundingListener = speaker -> {
		conduitables.forEach(conduitable ->
			{
				Conductor conductor = (Conductor) conduitable;
				conductor.notifier.enabled(false);
				if(conductor.getRole() == Conductor.Role.GND)
					conductor.copyFrom(groundingConductor);
				else {
					conductor.setLength(groundingConductor.getLength());
					conductor.setInsulation(groundingConductor.getInsulation());
					conductor.setAmbientTemperatureFSilently(groundingConductor.getAmbientTemperatureF());
				}
				conductor.notifier.enabled(true);
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
	 Removes all the conduitables of this circuit from the given conduit.
	 */
	private void leaveFrom(Conduit conduit){
		if(conduit == null)
			return;
		for(Conduitable conduitable: conduitables)
			conduit.remove(conduitable);
	}

	/**
	 Removes all the conduitables of this circuit from the given bundle.
	 */
	private void leaveFrom(Bundle bundle){
		if(bundle == null)
			return;
		for(Conduitable conduitable: conduitables)
			bundle.remove(conduitable);
	}

	/**
	 Moves all the conduitables of this circuit to the given conduit.
	 */
	private void moveTo(Conduit conduit){
		if(conduit == null)
			return;
		for(Conduitable conduitable: conduitables)
			conduit.add(conduitable);
	}

	/**
	 Moves all the conduitables of this circuit to the given bundle.
	 */
	private void moveTo(Bundle bundle){
		if(bundle == null)
			return;
		for(Conduitable conduitable: conduitables)
			bundle.add(conduitable);
	}

	/**
	Prepares the list of conduitables to match the number of sets (for cables)
	or the number of sets per conduit (for conductors). This assumes that the
	model conductors/cables have already been created. This method is called
	whenever the following properties changes:<br>
	-The load system voltage and/or system wires, from setupConductors().<br>
	-numberOfSets, from setNumberOfSets()<br>
	-conductorsPerSet, from moreConduits(), lessConduits()
	 */
	private void prepareConduitableList(){
		if(usingCable){
			if (!conduitables.isEmpty()
					&& (conduitables.get(0) instanceof Cable)
					&& (conduitables.size() == numberOfSets))
				return;
			conduitables.clear();
			conduitables.add(cable); //add the model cable to index 0
			while (conduitables.size() < numberOfSets)//add the rest.
				conduitables.add(cable.clone());
		}else{//using conductors
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
//		setConduitMode();
	}

	/**
	Creates the conductors or the cable to be used as the one set of conductors
	or one cable for this circuit, or to be used as a models for two or more
	sets for this circuit.
//	 This method is called automatically from the
//	calculation methods and checks if the system voltage and wires of the load
//	have changed, in which case it will update the conductors or the cable
//	models.
	*/
	private void setupModelConductors(){
		if(load == null || load.getSystemVoltage() == null) {
			resultMessages.add(ERROR200);
			return;
		}
		resultMessages.remove(ERROR200);
//		this would not let pass thru when usingCable changes
//		if(systemVoltage == load.getSystemVoltage())
//			return; //no change
		VoltageSystemAC systemVoltage;
		systemVoltage = load.getSystemVoltage();
		if(usingCable)
			cable.setSystem(systemVoltage);
		else {
			if (systemVoltage == VoltageSystemAC.v120_1ph_2w
					|| systemVoltage == VoltageSystemAC.v277_1ph_2w) {
				if (neutralConductor == null) {
					neutralConductor = new Conductor();
					neutralConductor.notifier.addListener(neutralListener);
				}
				neutralConductor.setRole(Conductor.Role.NEUCC).setSize(phaseAConductor.getSize());
				phaseBConductor = null;
				phaseCConductor = null;
			}
			else if (systemVoltage == VoltageSystemAC.v208_1ph_2w
					|| systemVoltage == VoltageSystemAC.v240_1ph_2w
					|| systemVoltage == VoltageSystemAC.v480_1ph_2w) {
				if (phaseBConductor == null)
					phaseBConductor = new Conductor();
				phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
				neutralConductor = null;
				phaseCConductor = null;
			}
			else if (systemVoltage == VoltageSystemAC.v208_1ph_3w
					|| systemVoltage == VoltageSystemAC.v240_1ph_3w
					|| systemVoltage == VoltageSystemAC.v480_1ph_3w) {
				if (phaseBConductor == null)
					phaseBConductor = new Conductor();
				phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
				if (neutralConductor == null) {
					neutralConductor = new Conductor();
					neutralConductor.notifier.addListener(neutralListener);
				}
				neutralConductor.setRole(Conductor.Role.NEUCC).setSize(phaseAConductor.getSize());
				phaseCConductor = null;
			}
			else {//this account for all 3-phase systems.
				if (phaseBConductor == null)
					phaseBConductor = new Conductor();
				phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
				if (phaseCConductor == null)
					phaseCConductor = new Conductor();
				phaseCConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
				if (systemVoltage.getWires() == 4) {
					if (neutralConductor == null) {
						neutralConductor = new Conductor();
						neutralConductor.notifier.addListener(neutralListener);
					}
					Conductor.Role role = neutralCurrentCarrying ?
							Conductor.Role.NEUCC : Conductor.Role.NEUNCC;
					neutralConductor.setRole(role).setSize(phaseAConductor.getSize());
				}
				else //3 wires
					neutralConductor = null;
			}
			conductorsPerSet = 2 + (phaseBConductor == null ? 0 : 1)
					+ (phaseCConductor == null ? 0 : 1)
					+ (neutralConductor == null ? 0 : 1);
		}
		prepareConduitableList();
	}

	/**
	 Calculates the size of this circuit cable or conductor under the preset
	 conditions, that is able to handle its full load current (per ampacity).
	 If the number of sets is greater than one and the resulting size is smaller
	 than 1/0 an error message will be issued.

	 Steps:
	 0. numberOfSets, setsPerConduit & numberOfConduits are known.
	 1. Request the mcaPerSet from the load object = loadMCA/numberOfSets
	 2. Get the correction factor cf from te conduitable.
	 3. Get the adjustment factor af from te conduitable.
	 4. Calculate the compound factor cpf = cf x af
	 5. Calculate the lookupAmperes = mcaPerSet / cpf
	 	5.1. conductorMetal = get the metal from the cable or conductor
	 	5.2. conductorTempRating = get the temperature rating of the cable or conductor
	 6. terminationTempRating and conduitable tempRating are known.
	 ******************* for when terminationTempRating is known **********************
	 if cpf == 1{
	 	if terminationTempRating>=ConductorTempRating
	 		return ConductorProperties.getSizeByAmperes(
	 					lookupAmperes,
	 					conductorMetal,
	 					ConductorTempRating
	 				)
	 	else
			return ConductorProperties.getSizeByAmperes(
						lookupAmperes,
						conductorMetal,
						terminationTempRating
					)
	 }
	 else{
	 7. proposedSize =
	 		ConductorProperties.getSizeByAmperes(
				lookupAmperes,
				conductorMetal,
				conductorTempRating
	 		)
	 8. ampacityOfProposed =
	 		ConductorProperties.getAmpacity(
				proposedSize,
	 			conductorMetal,
				conductorTempRating
			)
	 9. ampacityPerTerminationRating =
	 		ConductorProperties.getAmpacity(
				proposedSize,
				conductorMetal,
				terminationTempRating
			)
	 10. correctedAndAdjustedAmpacity =
	 		ampacityOfProposed * cpf
	 11. if(correctedAndAdjustedAmpacity <= ampacityPerTerminationRating)
	 		return proposedSize
	 12. proposedSize =
			ConductorProperties.getSizeByAmperes(
				lookupAmperes,
				conductorMetal,
				terminationTempRating
			)
	 13. if proposedSize == null{
	 		add a message indicating the conductor could not be selected for the given load amperes.
	 		return null
		 }
	 14. add a message:
	 	"Corrected and adjusted ampacity for this conductor/cable temp rating
	 	exceeds the ampacity for the temperature rating of the termination.
	 	Conductor sized has been selected per the termination temp rating."
	 	lookupAmperes = ampacityOfProposed + 1
	 12. return proposedSize
	 }
	 ******************* for when terminationTempRating is not known **********************
	 <= 100 Amp:
		return ConductorProperties.getSizeByAmperes(
					lookupAmperes,
					conductorMetal,
					T60
				)
	 else
		return ConductorProperties.getSizeByAmperes(
					lookupAmperes,
					conductorMetal,
					T75
				)

	 @return The size of the conductors/cable calculated per ampacity.
	 */
	private Size getSizePerAmpacity(){
		double mcaPerSet = load.getMCA()/numberOfSets;
	    double compoundFactor = phaseAConductor.getCorrectionFactor() *
                phaseAConductor.getAdjustmentFactor();
	    double lookupAmperes = mcaPerSet / compoundFactor;
		Metal conductorMetal;
		TempRating conductorTempRating;
	    if(usingCable){
			conductorMetal = cable.getMetal();
			conductorTempRating = cable.getTemperatureRating();
		}
	    else {
	    	conductorMetal = phaseAConductor.getMetal();
	    	conductorTempRating = phaseAConductor.getTemperatureRating();
		}
	    TempRating chosenTempRating;
	    Size proposedSize;
		double ampacityOfProposed;
		double ampacityPerTerminationRating;
        if(terminationTempRating != null) { //temperature rating of the terminations is known
			if(compoundFactor != 1.0){ //310.15(B) and 110.14(C)
				//ampacity for when AL and 14 AWG is handled.
				//todo test for AL and 14 AWG
				proposedSize = ConductorProperties.getSizeByAmperes(
						lookupAmperes,
						conductorMetal,
						conductorTempRating
				);
				if(proposedSize == null) {
					resultMessages.add(ERROR260);
					return null;
				}
				resultMessages.remove(ERROR260);
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
					if(ConductorProperties.compareSizes(proposedSize,Size.AWG_1$0) < 0
							&& numberOfSets > 1) {
						resultMessages.add(ERROR270);
						return null;
					}
					resultMessages.remove(ERROR270);
					return proposedSize;
				}
				proposedSize = ConductorProperties.getSizeByAmperes(
						lookupAmperes,
						conductorMetal,
						terminationTempRating
				);
				if(proposedSize == null) {
					resultMessages.add(ERROR260);
					return null;
				}
//				else {
				resultMessages.remove(ERROR260);
				if(ConductorProperties.compareSizes(proposedSize,Size.AWG_1$0) < 0
						&& numberOfSets > 1) {
					resultMessages.add(ERROR270);
					return null;
				}
				resultMessages.remove(ERROR270);
				resultMessages.add(WARNN220);
				return proposedSize;
//				}
			}
//			else {
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
			if (proposedSize == null) {
				resultMessages.add(ERROR260);
				return null;
			}
//			else {
			resultMessages.remove(ERROR260);
			if(ConductorProperties.compareSizes(proposedSize,Size.AWG_1$0) < 0
					&& numberOfSets > 1) {
				resultMessages.add(ERROR270);
				return null;
			}
			resultMessages.remove(ERROR270);
			return proposedSize;
//			}
//			}
        }
//        else{
		//temperature rating of the terminations is not known at this point
		//todo implement: (4) motors design letter B, C or D...
		Insul previousInsul = phaseAConductor.getInsulation();
		phaseAConductor.notifier.enabled(false);
		if(mcaPerSet <= 100.0) {
			phaseAConductor.setInsulation(Insul.TW);
			chosenTempRating = TempRating.T60;
		}
		else {
			phaseAConductor.setInsulation(Insul.THW);
			chosenTempRating = TempRating.T75;
		}
		compoundFactor = phaseAConductor.getCorrectionFactor() *
				phaseAConductor.getAdjustmentFactor();
		phaseAConductor.setInsulation(previousInsul);
		phaseAConductor.notifier.enabled(true);
		lookupAmperes = mcaPerSet / compoundFactor;
		proposedSize = ConductorProperties.getSizeByAmperes(
				lookupAmperes,
				conductorMetal,
				chosenTempRating
		);
		if (proposedSize == null) {
			resultMessages.add(ERROR260);
			return null;
		}
//		else {
		resultMessages.remove(ERROR260);
		if(ConductorProperties.compareSizes(proposedSize,Size.AWG_1$0) < 0
				&& numberOfSets > 1) {
			resultMessages.add(ERROR270);
			return null;
		}
		resultMessages.remove(ERROR270);
		return proposedSize;
//		}
//        }
	}

	/**
	 Returns the number of current carrying conductors (of insulated conductors,
	 not of cables) inside the used raceway/bundle or in free, accordingly.

	 @return The number of current carrying conductors
	 */
	private int getCurrentCarryingNumber() {
		int numberOfCurrentCarrying;
		if(mode == Mode.PRIVATE_CONDUIT)
			numberOfCurrentCarrying = privateConduit.getCurrentCarryingNumber();
		else if(mode == Mode.SHARED_CONDUIT)
			numberOfCurrentCarrying = sharedConduit.getCurrentCarryingNumber();
		else if(mode == Mode.PRIVATE_BUNDLE)
			numberOfCurrentCarrying = privateBundle.getCurrentCarryingNumber();
		else if(mode == Mode.SHARED_BUNDLE)
			numberOfCurrentCarrying = sharedBundle.getCurrentCarryingNumber();
		else {//it's in free air mode
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
	private Size getSizePerVoltageDrop(){
		Conduit usedConduit;
		if(mode == Mode.PRIVATE_CONDUIT)
			usedConduit = privateConduit;
		else if(mode == Mode.SHARED_CONDUIT)
			usedConduit = sharedConduit;
		else
			usedConduit = null;

		if(usingCable) {
			//todo to be implemented
			return null;
		}
		else {
			voltageDrop.setConductor(phaseAConductor);//for the sake of material and length
			if(usedConduit == null) //conductors are in free air or bundled
				voltageDrop.setConduitMaterial(Material.PVC);
			else
				voltageDrop.setConduitMaterial(ConduitProperties.getMaterial(usedConduit.getType()));
			voltageDrop.setLoadCurrent(load.getCurrent());
			voltageDrop.setPowerFactor(load.getPowerFactor());
			voltageDrop.setSets(numberOfSets);
			voltageDrop.setSourceVoltage(load.getSystemVoltage());
			return voltageDrop.getCalculatedSizeAC();
		}
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
		resultMessages.remove(ERROR230);
		resultMessages.remove(ERROR240);
		resultMessages.remove(ERROR250);
	}

/*	*//**
	 Asks if this circuit is in free air mode.

	 @return True if it's in free air, false otherwise.
	 *//*
	public boolean isFreeAirMode(){
		return (privateConduit.isEmpty()
				&& privateBundle.isEmpty()
				&& sharedConduit == null
				&& sharedBundle == null);
	}*/

/*	*//**
	 Asks if this circuit is in private conduit mode.

	 @return True if it's in private conduit mode.
	 *//*
	public boolean isPrivateConduitMode(){
		return !privateConduit.isEmpty();
	}*/

/*	*//**
	 Asks if this circuit is in shared conduit mode.

	 @return True if it's in shared conduit mode.
	 *//*
	public boolean isSharedConduitMode(){
		return sharedConduit != null;
	}*/

/*	*//**
	 Asks if this circuit is in private bundle mode.

	 @return True if it's in private bundle mode.
	 *//*
	public boolean isPrivateBundleMode(){
		return !privateBundle.isEmpty();
	}*/

/*	*//**
	 Asks if this circuit is in shared bundle mode.

	 @return True if it's in shared bundle mode.
	 *//*
	public boolean isSharedBundleMode(){
		return sharedBundle != null;
	}*/

	/**
	 Performs the following steps:
	 <p>1. Calculates the size of the circuit conductors, considering the
	 ampacity and the maximum allowed voltage drop as set forth in
	 <i>voltageDrop.setMaxVoltageDropPercentage()</i>;
	 <p>2. Calculates the size of the OCPD;
	 <p>3. Calculates the size of the grounding conductor;
	 <p>4. Sets the sizes of the hot, neutral and grounding conductors per the
	 values previously calculated.
	 */
	private void calculateCircuitConductors(){
		//setupModelConductors();
		//1.
		size = null;
//		if(resultMessages.hasErrors())
//			return;
		Size sizePerAmpacity = getSizePerAmpacity(); //todo shall return null if something is wrong
		if(sizePerAmpacity == null)
			return;

		Size sizePerVoltageDrop = getSizePerVoltageDrop(); //todo shall return null if something is wrong
		if(sizePerVoltageDrop == null)
			return;

		Size circuitSize;
		if(ConductorProperties.compareSizes(sizePerAmpacity, sizePerVoltageDrop) < 0) //VD size is bigger
			size = sizePerVoltageDrop;
		else
			size = sizePerAmpacity;
		//2. todo implement, save in the ocpd field
		//3. todo implement, save in an internal field
		Size groundingSize = Size.AWG_14;
		//4.
		//todo pending. This should take advantage of the fact that when updating the phaseA, neutral and grounding
		// everything gets updated.
		/*if(usingCable){
			Cable _cable;
			for(Conduitable conduitable: conduitables) {
				_cable = (Cable) conduitable;
				_cable.setPhaseConductorSize(size);
				_cable.setNeutralConductorSize(size);
				_cable.setGroundingConductorSize(groundingSize);
			}
		}
		else {
			Conductor _conductor;
			for(Conduitable conduitable: conduitables) {
				_conductor = (Conductor) conduitable;
				if(_conductor.getRole() == Conductor.Role.HOT
						|| _conductor.getRole() == Conductor.Role.NEUCC
						|| _conductor.getRole() == Conductor.Role.NEUNCC
						|| _conductor.getRole() == Conductor.Role.NCONC)
					_conductor.setSize(size);
				else
					_conductor.setSize(groundingSize);
			}
		}*/
	}

	/**
	 Constructs a circuit object for the given load. This circuit creates and
	 holds a copy of the given load. This circuit's default values are as
	 follow:
	 <p>- One set of conductors.
	 <p>- Default conductors. See {@link Conductor#Conductor() Conductor()} for
	 default properties.
	 <p>- One private conduit. See {@link Conduit#Conduit(Type, Conduit.Nipple)
	 Conduit(Type, Conduit.Nipple)} for default properties.

	 @param load The load that will be copied as part of this circuit and that
	 will be served by this circuit.
	 @see Load
	 */

	public Circuit(Load load){
		this.load = load.clone();
		this.load.notifier.addListener(
				speaker -> setupModelConductors()
		);
		phaseAConductor.notifier.addListener(phaseListener);
		groundingConductor.notifier.addListener(groundingListener);
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
	 */
	public VoltDrop getVoltageDrop(){
		return voltageDrop;
	}

	//todo javadoc
	private void setMode(){
		if(mode == Mode.PRIVATE_CONDUIT)
			setConduitMode();
		else if(mode == Mode.FREE_AIR)
			setFreeAirMode();
		else if(mode == Mode.SHARED_CONDUIT)
			setConduitMode(sharedConduit);
		else if(mode == Mode.PRIVATE_BUNDLE)
			setBundleMode();
		else //if(mode == Mode.SHARED_BUNDLE)
			setBundleMode(sharedBundle);
//		else
//			throw new RuntimeException("No clear mode of arranging the wires has been set!.");
	}

	/**
	 Sets the circuit in free air. All conductors and cables are removed from
	 any shared or private conduit or bundle and put in free air. If the circuit
	 is made of insulated conductors (not cables) a warning is generated. After
	 calling this method, the circuit is in free air mode.
	 */
	public void setFreeAirMode(){
		clearModeMsg();
		moveTo(privateConduit);//concentrate all conduitables here
		leaveFrom(privateConduit);//then, remove them from here.
		sharedConduit = null;
		sharedBundle = null;
		if(!usingCable) //conductors in free air
			resultMessages.add(WARNN200);
	}

	/**
	 Sets the circuit in a non shared private conduit.
	 To change the distribution of sets per conduit or to change the number of
	 used conduits, call the method {@link #moreConduits()} or
	 {@link #lessConduits()}.
	 */
	public void setConduitMode(){
		clearModeMsg();
		moveTo(privateConduit); //removes the conduitables from all other conduits and bundles and put them here.
		sharedConduit = null;
		sharedBundle = null;
		if(usingCable) //using cables in conduit
			resultMessages.add(WARNN210); //cables in conduit
	}

	/**
	 Sets the circuit in a shared public conduit.

	 @param conduit The public conduit to which all the conduitables go in.
	 */
	public void setConduitMode(Conduit conduit){
		clearModeMsg();
		if(conduit == null || numberOfSets > 1) {
			setFreeAirMode();
			if(conduit == null)
				resultMessages.add(ERROR230);
			else
				resultMessages.add(ERROR210);
//			//the circuit goes into free air mode
//			moveTo(privateConduit);//concentrate all conduitables here
//			leaveFrom(privateConduit);//then, remove them from here.
//			sharedConduit = null;
//			sharedBundle = null;
			return;
		}
		sharedConduit = conduit;
		moveTo(sharedConduit);
		sharedBundle = null;
		if(usingCable) //using cables in conduit
			resultMessages.add(WARNN210); //cables in conduit
	}

	/**
	 Sets the circuit in a non shared private bundle.
	 */
	public void setBundleMode(){
		clearModeMsg();
		moveTo(privateBundle); //removes the conduitables from all other conduits and bundles and put them here.
		sharedConduit = null;
		sharedBundle = null;
		if(!usingCable) //using conductors in bundle
			resultMessages.add(WARNN205);
	}

	/**
	 Sets the circuit in the given shared public bundle.

	 @param bundle The public bundle to which all the conduitables go in.
	 */
	public void setBundleMode(Bundle bundle){
		clearModeMsg();
		if(bundle == null) {
			setFreeAirMode();
			resultMessages.add(ERROR240);
//			//the circuit goes into free air mode
//			moveTo(privateConduit);//concentrate all conduitables here
//			leaveFrom(privateConduit);//then, remove them from here.
//			sharedConduit = null;
//			sharedBundle = null;
			return;
		}
		sharedBundle = bundle;
		moveTo(sharedBundle);
		sharedConduit = null;
		if(!usingCable) //using conductors in bundle
			resultMessages.add(WARNN205);
	}

	/**
	 Increments the number of conduits used by this circuit, when in private
	 conduit mode. The resulting number of conduits depends on the actual number
	 of sets. The NEC allows to distribute the sets of conductors in parallel in
	 a way that the impedance of each sets is maintained equal on each set. Keep
	 in mind that the number of conduits is reset to the same number of sets of
	 conductors (the default) whenever the number of sets changes or the circuit
	 mode changes.
	 */
	public void moreConduits(){
		if(mode != Mode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfConduits + 1; i <= numberOfSets; i++)
			if(numberOfSets % i == 0) {
				numberOfConduits = i;
				prepareConduitableList();
				break;
			}
	}

	/**
	 Decrements the number of conduits used by this circuit, when in private
	 conduit mode. The resulting number of conduits depends on the actual number
	 of sets. The NEC allows to distribute the sets of conductors in parallel in
	 a way that the impedance of each sets is maintained equal on each set. Keep
	 in mind that the number of conduits is reset to the same number of sets of
	 conductors (the default) whenever the number of sets changes or the circuit
	 mode changes.
	 */
	public void lessConduits(){
		if(mode != Mode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfConduits - 1; i != 0; i--)
			if(numberOfSets % i == 0) {
				numberOfConduits = i;
				prepareConduitableList();
				break;
			}
	}

	/**
	 Returns the size of the circuit conductors/cables properly calculated per
	 ampacity and voltage drop. This is the size for hot and neutral conductors
	 (if any).

	 @return The size of the conductors/cables.
	 */
	//todo this should return the sizes of all hot and neutral + ground?
	public Size getCircuitSize(){
		//resultMessages.remove(ERROR260);
		//resultMessages.remove(ERROR270);
		calculateCircuitConductors();
		return size;
	}

	/**
	 Returns the size of the conduit(s) used by this circuit, if any.

	 @return The trade size of the conduit.
	 */
	public Trade getConduitTradeSize(){
		if(mode == Mode.FREE_AIR || mode == Mode.PRIVATE_BUNDLE || mode == Mode.SHARED_BUNDLE)
			return null;
		//the circuit here is in shared or public conduit.
		calculateCircuitConductors();

		/*
				phaseAConductor.addListener(new Listener() {
			@Override
			public void notify(Speaker speaker) {
				conduitables.forEach(conduitable -> {
					Conductor conductor = (Conductor) conduitable;
					if(conductor.getRole() == Conductor.Role.HOT ||
							conductor.getRole() == Conductor.Role.NCONC)
						conductor.copyFrom(phaseAConductor);
				});
			}
		});
		neutralConductor.addListener(new Listener() {
			@Override
			public void notify(Speaker speaker) {
				conduitables.forEach(conduitable -> {
					Conductor conductor = (Conductor) conduitable;
					if(conductor.getRole() == Conductor.Role.NEUCC ||
							conductor.getRole() == Conductor.Role.NEUNCC)
						conductor.copyFrom(neutralConductor);
				});
			}
		});
		groundingConductor.addListener(new Listener() {
			@Override
			public void notify(Speaker speaker) {
				conduitables.forEach(conduitable -> {
					Conductor conductor = (Conductor) conduitable;
					if(conductor.getRole() == Conductor.Role.GND)
						conductor.copyFrom(neutralConductor);
				});
			}
		});
		//starting from now, any change to the phase A conductor will propagate
		//to all hot and non concurrent conductor forming this circuit. Same for
		//the neutral and the grounding conductor.
		 */
		//todo homogenize here all conductors in the conduit per the model conductors
		// refactor ShareableConductor interface to use only the necessary methods
		if(mode == Mode.PRIVATE_CONDUIT)
			return privateConduit.getTradeSize();
		else
			return sharedConduit.getTradeSize();
	}

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
	 set of conductors per conduit, unless the said methods are called.

	 @param numberOfSets The new number of sets.
	 */
	public void setNumberOfSets(int numberOfSets){
		if(this.numberOfSets == numberOfSets)
			return;
		this.numberOfSets = numberOfSets;
		numberOfConduits = numberOfSets;
		setsPerConduit = 1;
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
	 mode. Other methods that changes the number of conduits are the methods
	 {@link #setConduitMode()}, {@link #setConduitMode(Conduit)},
	{@link #setBundleMode()} or {@link #setBundleMode(Bundle)}.

	 @return The actual number of conduits used in private conduit mode.
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
	 allowed values as recognized by the NEC are 60°C, 75°C or even unknown (for
	 this purpose, null value correspond to unknown). If the user attempt to set
	 90°C, the actual temperature rating will be set as 75°C.
	 <p>If the temperature rating is unknown (the default) the property is null
	 and the circuit ampacity is calculated based on NEC 110.14(C).

	 @param terminationTempRating The temperature rating: 60°C, 75°C or null for
	 unknown.
	 */
	public void setTerminationTempRating(TempRating terminationTempRating) {
		if(terminationTempRating == TempRating.T90)
			terminationTempRating = TempRating.T75;
		this.terminationTempRating = terminationTempRating;
	}

	/**
	 Returns a conductor object that represents all the phases conductors in
	 this circuit. Any change done through this reference will be applied to all
	 phases conductors of this circuit.

	 @return A phase conductor.
	 */
	public ShareableConductor getPhaseConductor(){
		return phaseAConductor;
	}

	/**
	 Returns a conductor object that represents all the neutral conductors in
	 this circuit. Any change done through this reference will be applied to all
	 neutral conductors of this circuit.

	 @return A neutral conductor.
	 */
	public ShareableConductor getNeutralConductor(){
		return neutralConductor;
	}

	/**
	 Returns a conductor object that represents all the grounding conductors in
	 this circuit. Any change done through this reference will be applied to all
	 grounding conductors of this circuit.

	 @return A grounding conductor.
	 */
	public ShareableConductor getGroundingConductor(){
		return groundingConductor;
	}

	/**
	 Returns the private conduit of this circuit as a shareable conduit.

	 @return This circuit's private conduit.
	 */
	public ShareableConduit getPrivateConduit(){
		return privateConduit;
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
		if(load.getSystemVoltage().getPhases() == 3)
			setupModelConductors();
	}

	/**
	 Returns this circuit's load.

	 @return This circuit's load.
	 */
	public Load getLoad(){
		return load;
	}

	//todo javadoc
	public void setUsingCable(boolean usingCable) {
		if(usingCable == this.usingCable)
			return;
		this.usingCable = usingCable;
		setupModelConductors();
	}

	//todo javadoc
	public boolean isUsingCable(){
		return usingCable;
	}

	//todo javadoc
	public Mode getMode() {
		return mode;
	}
	/*
todo:
 -implement methods to set the necessary properties of the private conduit, the private bundle, and the conductors/cable of the circuit.
 -implement circuit number, panel name


*/

}
/*future 110.14(C) to be implemented outside like this: to be deeply thought.
if getFactor == 1 then
	if (LoadAmpacity <= 100 and unknownRating) or (conductor.size>=14AWG and conductor.size<=1AWG) then
		getAmpacity(conductorSize, metal, TempRating.T60)
	else if (LoadAmpacity > 100) or conductor.size>=1AWG
		getAmpacity(conductorSize, metal, TempRating.T75)
 */
/*
future Circuits should return a string composed of several lines (separated by returns and line feed), of the form:
     <p>&emsp;&emsp; First line, circuit description:  "POOL HEATER"
     <p>&emsp;&emsp; Second line, configuration:       "(3) #8 AWG THHN (AL) + #10 AWG THHN (CU)(NEU) + #12 AWG THHN (CU)(GND) IN 2" EMT CONDUIT" or
     <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;           "(3) SETS OF (4) 250 KCMIL THHW (CU) + #1/0 AWG THHW (CU)(GND) IN 4" EMT CONDUIT" or
     <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;           "MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND) IN FREE AIR or
     <p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;           2" EMT CONDUIT or IN CABLE TRAY"
     <p>&emsp;&emsp; Third line, circuit ratings:      "208 VOLTS 3ⱷ 3W 125 AMPS DPH-24,26,28"

 */


/*quede aqui
* All classes have been refactored to the perfection except class CircuitOld which the more I think about it the more I believe should not
* inherit from conductor but instead uses a reference to a conductor.
* This is because of the concept of circuit, which needs to also use other objects like Load, Conduit/Bundle, OCPD
* I need to think about the fact that a conduit can have different circuits in it. That brings the idea that a circuit does not depend on
* the conduit, that it may or may not exist in a conduit (like in free air or in a bundle). If a circuit uses a conduit, then that conduit is a
* separate object that is referenced by a circuit. I would need then to rename the class conduit as ConduitProperties and develop the
* class Conduit in the same sense I did for the class conductor (This has been done). If a circuit references a conduit object, that conduit object should
* also reference all the circuits that are registered with it (at the conductor level). Like every time a new circuit is added or an existing one is removed
* from a conduit, the conduit size is updated.
* The conduit should also
* have a state to indicate that it is locked and not accepting more conductors. This locking mechanism can be activated by the circuit
* that is being added to the conduit. It's like "add me and lock to prevent other circuits to be added" (to be implemented by making the conduits for the
* paralleled sets hidden from outside)
*  Once a circuit or conductors are
* added to a Conduit, the conduit will update its trade size (going bigger or going smaller, as requested) (done).
*
* *
* CircuitOld class should account for "class A" type circuits (study about this), since the type or class is also related to restriction for
* combining with other classes.
* A circuit has a set of conductor that supplies only one load. A circuit should have a description and a number and eventually reference
* the panel (and ckt # in the panel) it belongs to (this is maybe not a good idea)
*
* Pending to develop (in this order):
* Amend correct the class circuit
* The class load
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
	5. The application scenario: residential, commercial, industrial (the load doesnt know about this. it must be accounted for externally)
	5. If the load is continuous or not, which result from the combination of the following criteria: application scenario, load type, or
		engineering criteria.
This can be encapsulated in a class "Load".
This object must be able to provide its MCA (minimum conductor ampacity) and MOCPD (maximum overcurrent protection device, to protect the
load only).

A load can take different behaviors and properties:
	1. Type: as group by the NEC.
	2. Grouping: a load can be made out of one or more "atomic loads". For example, a group of 10 receptacles forms a 1800va load or one
	dedicated receptacle for a specific 600va fridge. One could say also that a load can be "distributed" or "localized".

As the concept of load is very broad, the best approach is to create a base class Load that will have all the common properties and
behaviors of all the loads. By inheriting from that class, one can create more specialized cases.

The class CircuitOld should encapsulate a Load object. There are special cases, where a CircuitOld is feeding a group of loads

So, the purpose of the class circuit is to calculate these incognitos when creating a circuit, the object load has to be fully defined
(known)

The circuit object represents the set of electrical equipment composed of:
	-A load.
	-A raceway.
	-A set of conductors inside the conduit.
	-An overcurrent protection device.
 */

/*

Class CircuitOld:
-----------------------
The resistance and reactance of the conductor will depend on the raceway type and arrangement:
-If conductors are in free air or tray
-If they are inside a conduit and then, the metal of the conduit
-The number of conductors inside the raceway that are not in parallel (this should affect the resistance and the reactance of the
conductor but I haven't found a formulae or method that correlates these characteristics)
-The number of conductors inside the conduit that are in parallel (same note as before; table 9 is based on the assumption the system
voltage is three phase, 75°C, 60Hz, three single conductors in conduit; so, unless more information is found, I will always use the
values of table 9 but will leave room for improvement once the method that considers different scenarios is found).

-The ampacity of the conductor will depend mainly on all the above listed variables but also on the location of the conductor, like when
it is in the rooftop (and the distance from the floor)

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