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

	public double getAreaCM(){
		return super.getAreaCM() * numberOfSets;
	}
}
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