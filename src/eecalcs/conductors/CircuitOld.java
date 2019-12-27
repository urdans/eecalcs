package eecalcs.conductors;

import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;

/**
 This class represents a circuit. A circuit is an object made out of physical
 objects and characteristics.
 The physical objects that conform a circuit are:
 - One or more set of conductors or cables in parallel. Their sizes are calculated based on other characteristics, like the load type, if the load is
 continuous and if the temperature rating of the terminals or enclosures is known.
 - One overcurrent protection device, which is calculated based on the characteristics of the loads and conductors/cables.
 - One or more optional conduits or bundles, that are decided externally, by the user, and can be used also for other circuits.
 - One connected load (which can be "composed" of other loads) which is picked up by the user.

 The characteristics or properties of a circuit are:
 - The length of the circuit: which once assigned, propagates to its conduitables.
 - The one way AC resistance: which depends on the conductor, the length and the bundling or conduit characteristics.
 - The one way DC resistance: which depends on the conductor metal and its coating.
 - The one way reactance:  which depends on the conductor, the length and the magnetic nature of its optional conduit.
 - The role of the circuit: feeder or a branch circuit.
 - The system voltage (number of phases, number of wires and if delta or wye connection for 3 phase systems.)
 - The number of current carrying conductors taking into account the conductors in parallel and the non-linear characteristic of the load.
 - The number of sets of conductors.
 - How are the sets installed in parallel: all sets in a conduit; one set per conduit; an equal number of set per conduit. (Ex. 9 sets can be: in only one
 conduit, in 3 conduits or in 9 conduits; 6 sets can be: in one conduit, in 2, in 3 and in 6...the number of conduits is always the prime factors of the
 number of sets, including 1 and the number of set itself. Notice when more than one set is used, the conduits must not be shared with other
 circuits/conductors/cables. In this case, the conduit or conduits are owned by the circuit and those conduits cannot be accessed externally. This means that
 a circuit must have its own conduit only for parallel sets but it must use a reference to an existing (external) conduit that can be shared with other
 circuits. When a conduit of this type is added more circuits (that is conductors/cables) this should initiate a cascade of updates to force the involved
 circuits to recalculate their conductor sizes (and any other property?)

 - The circuit rating, which is the same as the rating of the OCPD.
 - If the circuit serves one-end load (dedicated) or several sparse/distributed daisy chained loads. This should be defined by the load itself.
 - Conditions: rooftop, wet, damp, etc...
 - The allowed voltage drop (based on if it's a feeder or a branch circuit and based of the load type, like fire pump running, fire pump starting, sensitive
 electronic equipment, etc.).
 - if the ocdp is 100% or 80% rated.

 Some of these properties are calculated by combining those physical objects the properties.
 For example, the resistance or the reactance of the circuit, depends on the type of
 conduit, the metal of the conductor and the length.

 The ultimate goal of a circuit, once provided with all the required properties like length, metal, load type, ambient temperature, conditions (which might
 define the insulation type), locations, etc., is to provide:
 - the correct insulation and size of the conductor calculated based on the ampacity (corrected and adjusted), the voltage drop and other possible conditions.
 - the correct size of the overcurrent protection device (OCPD).
 - the correct size of the conduit when required/optional.
 */
public class CircuitOld extends Conductor {
	protected Material conduitMaterial = Material.PVC;
	//to think about tradesize, it is calculated or imposed? read my notebook notes
	//protected String tradeSize = Conduit.getTradeSizes()[2]; // = 3/4"
	//protected int currentCarryingConductorsPerConduit = 3;
	//protected boolean onRooftop = false;
	protected int numberOfSets = 1; //for sets greater than 1, conduits will be used for the sets only and the current carrying conductor
	// should never be greater than 3. A circuit composed of one set can share the same conduit with other circuits

	//pending taking into account different ampacities in same circuit

	public CircuitOld(Conductor conductor){
		//super(conductor);
	}

	public CircuitOld(CircuitOld circuitOld){
		this((Conductor) circuitOld);
		this.conduitMaterial = circuitOld.conduitMaterial;
		this.numberOfSets = circuitOld.numberOfSets;
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
		return ConductorProperties.getACResistance(getSize(), getMetal(), conduitMaterial, getLength(), numberOfSets);
	}

	public double getOneWayDCResistance(){
		return ConductorProperties.getDCResistance(getSize(), getMetal(), getLength(), numberOfSets, getCopperCoating());
	}

	public double getOneWayReactance(){
		return ConductorProperties.getReactance(getSize(), ConduitProperties.isMagnetic(conduitMaterial), getLength(), numberOfSets);
	}

	public double getAmpacity(){
		return super.getAmpacity() * numberOfSets;
	}

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
	1. CableType: as group by the NEC.
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

*/