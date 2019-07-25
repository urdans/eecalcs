package eecalcs;

public class Conduit {

	//region enums
	public static enum Material {
		PVC, ALUMINUM, STEEL
	}
	//endregion

	public static Material getConduitMaterialPerIndex(int conduitTypeIndex){
		if(conduitTypeIndex==0)
			return Material.PVC;
		else if(conduitTypeIndex==1)
			return Material.ALUMINUM;
		else
			return Material.STEEL;
	}
}
