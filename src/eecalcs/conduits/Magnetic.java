package eecalcs.conduits;

public class Magnetic {
	public static final boolean YES = true;
	public static final boolean NO = false;

	public static boolean isMagnetic(Material material){
		if(material == Material.PVC | material == Material.ALUMINUM) return NO;
		return YES;
	}
}
