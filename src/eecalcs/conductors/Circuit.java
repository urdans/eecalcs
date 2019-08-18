package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.conduits.Magnetic;
import eecalcs.conduits.Material;

public class Circuit extends Conductor {
	protected Material conduitType = Material.PVC;
	//to think about tradesize, it is calculated or imposed? read my notebook notes
	//protected String tradeSize = Conduit.getTradeSizes()[2]; // = 3/4"
//	protected int currentCarryingConductorsPerConduit = 3;
//	protected boolean onRooftop = false;
	protected int numberOfSets = 1; //for sets greater than 1, conduits will be used for the sets only and the current carrying conductor
	// should never be greater than 3. A circuit composed of one set can share the same conduit with other circuits

	//pending taking into account different ampacities in same circuit

	public Circuit(Conductor conductor){
		super(conductor);
	}

	public Circuit(Circuit circuit){
		this((Conductor) circuit);
		this.conduitType = circuit.conduitType;
		this.numberOfSets = circuit.numberOfSets;
	}

	public Material getConduitType() {
		return conduitType;
	}

	public void setConduitType(Material conduitType) {
		this.conduitType = conduitType;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}

	public void setNumberOfSets(int numberOfSets) {
		this.numberOfSets = numberOfSets;
	}

	public double getOneWayACResistance(){
		return CondProp.bySize(size).byMetal(metal).getACResistance(conduitType) * length * 0.001 / numberOfSets;
	}

	public double getOneWayDCResistance(){
		if(metal == Metal.COPPER)
			return CondProp.bySize(size).forCopper().getDCResistance(copperCoated) * length * 0.001 / numberOfSets;
		return CondProp.bySize(size).forAluminum().getDCResistance() * length * 0.001 / numberOfSets;
	}

	public double getOneWayReactance(){
		return CondProp.bySize(size).getReactance(Magnetic.isMagnetic(conduitType))  * length * 0.001 / numberOfSets;
	}

	public double getAmpacity(){
		return super.getAmpacity() * numberOfSets;
	}

	public double getAreaCM(){
		return super.getAreaCM() * numberOfSets;
	}
}
