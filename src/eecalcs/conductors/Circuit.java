package eecalcs.conductors;

import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;

/**
 * This class represents a circuit. A circuit is a concept made out of physical objects and characteristics.
 * The physical objects that make a circuit are:
 * - One or more set of conductors or one cable.
 * - One overcurrent protection device.
 * - One optional conduit.
 * - One connected load.
 * The characteristics that make a circuit are:
 * - The one way AC resistance.
 * - The one way DC resistance.
 * - The one way reactance.
 * - The nominal voltage.
 * - The system (number of phases, number of wires and if delta or wye connection for 3 phase systems.
 * - The number of current carrying conductors.
 * - The number of sets (for parallel sets).
 * - How are the sets installed in parallel: all sets in a conduit or same number of sets per separate conduit.
 * Some properties of these properties are calculated as a result of combining the physical objects that make a circuit and their properties.
 * For example, the ampacity of a circuit is a property that depends on the conductors but also the number of current carrying conductors inside the conduit
 * when the circuit is inside a conduit (not in free air). Another example is the resistance or the reactance of the circuit, which depends on the type of
 * conduit and the metal of the conductor. The current of a circuit depends on the load; the rating of a circuit depends on the OCPD...
 * The ultimate goal of a circuit, once provided with all the required properties like length, metal, load type, ambient temperature, conditions (which might
 * define the insulation type), locations, etc., is to provide:
 * - the correct size of the conductor calculated based on the ampacity (corrected and adjusted), the voltage drop and other possible conditions.
 * - the correct size of the overcurrent protection device (OCPD).
 * - the correct size of the conduit when required/optional. This will always be computed, even if the installation is in free air.
 *
 * The classes involved in this concept are: Conductor, Cable, Conduit, Circuit, Load, Ocdp and probably Conditions (to think about this).
 * The classes Conductor, Cable and Circuit must implement the interface Conduitable with the following methods:
 * - getInsulatedAreaIn2(): returns the total area occupied by the cable, or the sum of all the conductors contained in a circuit or the area of a conductor.
 * - getCurrentCarryingCount(): returns the number of current carrying conductors. A conductor object always returns 1. Cable and circuit will return the
 * number accordingly.
 * - getConductorCount(): Cable and Conductor objects return 1; circuit returns the count accordingly.
 * - getAmpacity(): return the ampacity of the circuit, conductor or cable, accounting for all corrections and adjustments.
 * - setConduit(): sets the container conduit of the Conduitable object. if the object already had a conduit, it will be replaced with no warning.
 * - hasConduit(): asks if the Conduitable object is inside a conduit. A false response means the Conduitable is in free air.
 *
 * The area and conductor count is used to compute conduit fill. Current carrying count is used to compute ampacity.
 *
 * A Conduit object maintain a list af Conduitable objects. To compute the trade size, the Conduit object retrieves and get the total the area of all members
 * of the list.
 * As a circuit can be made out of conductors or out of one cable, it will use the current carrying count from either one of those to adjust the
 * ampacity of the circuit. Conductor and cable will do the same. These three classes will always ask its container conduit for the number of other current
 * carrying conductors to compute the adjusted ampacity of themselves. However, this adjustment is not required when conductors are not installed in a
 * raceway or cable, like in free air or in a cable tray (only cables).
 *
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
		//super(conductor);
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

	/*this doesnt make any sense unles the number of phases is known
	public double getAreaCM(){
		return super.getAreaCM() * numberOfSets;
	}*/
}
    /*future 110.14(C) to be implemented outside like this:
    if getFactor == 1 then
        if LoadAmpacity <= 100 and unknownRating then
            getAmpacity(conductorSize, metal, TempRating.T60)
        else
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
/*TODO
   Some ideas:
		* Class Circuit should inherit from Conductor
		Class Feeder, Branch and Service should inherit from class Circuit
 Class Load is for generic loads. Other load types should inherit from class Load and get specialized.
  Class Conductor:
 ----------------
 The independent properties of a conductor are:
 -size
 -metal (CU or AL)
 -insulation (if any)
 -length (one way length)
  Class Circuit:
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
  Branch circuits can be multiwire
 Loads can be continuous or non continuous.
  Other classes must be designed, like for fuses, breakers, loads, motors, appliances, whatever, lights, AC equipment, panel, switchboard,
 etc, etc.

 */
/*
The class encapsulates only static data and static methods. This class is to be used in composition by the
class Conductor.
Class Circuit should inherit from Conductor
Class Feeder, Branch and Service should inherit from class Circuit
Class Load is for generic loads. Other load types should inherit from class Load and get specialized.

This class groups all the properties related to a conductor of a defined size. The class is able to build and return any conductor with
all these properties (as defined in NEC2014) and also build a conductor object (as defined below) that encapsulates only the
properties that pertain to a particular set of conditions.

Other classes should be defined separately; Composition will be preferred above inheritance, unless abstraction is necessary (which will
probably be the case for the class load and its descendant)

A conductor is an entity that encapsulates all the properties of a single conductor, when it is isolated, that is, the
represented characteristics don't depend upon other conditions, like ambient temperature, number of conductor per raceway, type of
raceway, voltage type (AC or DC), number of phases, special locations, load types, etc.

Class Conductor:
----------------
The independent properties of a conductor are:
-size
-metal (CU or AL)
-insulation (if any)
-length (one way length)

Class Circuit:
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

Branch circuits can be multiwire
Loads can be continuous or non continuous.

Other classes must be designed, like for fuses, breakers, loads, motors, appliances, whatever, lights, AC equipment, panel, switchboard,
etc, etc.

*/