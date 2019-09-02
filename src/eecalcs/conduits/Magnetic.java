package eecalcs.conduits;

/**
 * Class containing constant for magnetic materials
 */
public class Magnetic {
	public static final boolean YES = true;
	public static final boolean NO = false;

	/**
	 * Asks is the given conduit material is magnetic
	 * @param material The material of the conduit
	 * @return True if the material is magnetic, false otherwise
	 * @see Material
	 */
	public static boolean isMagnetic(Material material){
		if(material == Material.PVC | material == Material.ALUMINUM) return NO;
		return YES;
	}

	private Magnetic(){}
}
