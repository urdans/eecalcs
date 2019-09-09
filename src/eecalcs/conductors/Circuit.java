package eecalcs.conductors;

import eecalcs.conduits.Magnetic;
import eecalcs.conduits.Material;
/**
 * This class represents a set of conductors inside a conduit.
*/
public class Circuit extends Conductor {
	protected Material conduitMaterial = Material.PVC;
	//to think about tradesize, it is calculated or imposed? read my notebook notes
	//protected String tradeSize = Conduit.getTradeSizes()[2]; // = 3/4"
	//protected int currentCarryingConductorsPerConduit = 3;
	//protected boolean onRooftop = false;
	protected int numberOfSets = 1; //for sets greater than 1, conduits will be used for the sets only and the current carrying conductor
	// should never be greater than 3. A circuit composed of one set can share the same conduit with other circuits

	//pending taking into account different ampacities in same circuit

	public Circuit(Conductor conductor){
		super(conductor);
	}

	public Circuit(Circuit circuit){
		this((Conductor) circuit);
		this.conduitMaterial = circuit.conduitMaterial;
		this.numberOfSets = circuit.numberOfSets;
	}

	public Material getConduitMaterial() {
		return conduitMaterial;
	}

	public void setConduitMaterial(Material conduitMaterial) {
		this.conduitMaterial = conduitMaterial;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}

	public void setNumberOfSets(int numberOfSets) {
		this.numberOfSets = numberOfSets;
	}

	public double getOneWayACResistance(){
		return ConductorProperties.getACResistance(size, metal, conduitMaterial, length, numberOfSets);
	}

	public double getOneWayDCResistance(){
		return ConductorProperties.getDCResistance(size, metal, length, numberOfSets, copperCoated);
	}

	public double getOneWayReactance(){
		return ConductorProperties.getReactance(size, Magnetic.isMagnetic(conduitMaterial), length, numberOfSets);
	}

	public double getAmpacity(){
		return super.getAmpacity() * numberOfSets;
	}

	/*this doesnt make any sense unles the number of phases is known
	public double getAreaCM(){
		return super.getAreaCM() * numberOfSets;
	}*/
}

/*quede aqui
* All classes have been refactored to the perfection except class Circuit which the more I think about it the more I believe should not
* inherit from conductor but instead uses a conductor object (a copy of it).
* This is because of the concept of circuit, which needs to also use other objects like Load and Conduit
* I need to think about the fact that a conduit can have different circuits in it. That brings the idea that a circuit does not depend on
* the conduit, that it may or may not exist in a conduit (like in free air). If a circuit uses a conduit, then that conduit is a
* separate object that is referenced by a circuit. I would need then to rename the class conduit as ConduitProperties and develop the
* class Conduit in the same sense I did for the class conductor. If a circuit references a conduit object, that conduit object should
* also reference all the circuits that are registered with it. Like every time a new circuit is added or an existing one is removed
* from a conduit, the conduit size is updated (if the user indicates so, like allowUp, allowDown, or noChange). The conduit should also
* have a state to indicate that it is locked and not accepting more conductors. This locking mechanism can be activated by the circuit
* that is being added to the conduit. It's like "add me and lock to prevent other circuits to be added". Once a circuit or conductors are
* added to a Conduit, the conduit will update its trade size (going bigger or going smaller, as requested) or issue warning in case
* noChange is specified and the actual conduit size is too small to accommodate those conductors per NEC. The conduit can be asked if its
* size is compliant or not, if it is locked, the number and size of conductors, etc. It can be instructed to upgrade or downgrade its
* size to minimal code requirements, or to force goUp or goDown or just to set a specific trade size. After any instruction the Conduit
* will always respond with messages (errors and warnings). The caller should always be aware of the messages. Some instructions with
* certain parameter are more likely than others to not throw any message. Some instructions should return a boolean indicating that the
* instruction was successful or not (if not, the user can know the reasons by consulting the message response).
*
* Notice that a conduit can also accept individual conductor circuit like grounding conductors (0 volt, 0 phase circuit). This allow for
* applying certain rules that restrict having circuits of different voltages in the same raceway.
*
* Circuit class should account for "class A" type circuits (study about this), since the type or class is also related to restriction for
* combining with other classes.
* A circuit has a set of conductor that supplies only one load. A circuit should have a description and a number and eventually reference
* the panel (and spaces in the panel) it belongs to.
*
* Pending to develop (in this order):
* The class Conduit, after renaming the existing one to ConduitProperties
* The class load
* Amend correct the class circuit
* The service to determine the conductor size giving the load MCA, ambient temp, temp ratings, number of conductor in a conduit, etc.
*
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
	5. The application scenario: residential, commercial, industrial.
	5. If the load if continuous or not, which result from the combination of the following criteria: application scenario, load type, or
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


The class Circuit should encapsulate a Load object. There are special cases, where a Circuit is feeding a group of loads




So, the purpose of the class circuit is to calculate these incognitas when creating a circuit, the object load has to be fully defined
(known)

The circuit object represents the set of electrical equipment composed of:
	-A load.
	-A raceway.
	-A set of conductors inside the conduit.
	-An overcurrent protection device.
 */