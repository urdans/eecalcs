/*This class encapsulates conductor properties defined in table 310.15(B)(16) and in tables 8 & 9*/
package eecalcs.conductors;

import eecalcs.conduits.Material;

/**
 * This class encapsulates a set of all the electrical properties of a conductor as defined in NEC 2014 tables 310.15(B)(16), 8 and 9
 */
public class PropertySet {
	/**
	 * This class encapsulates the common properties of aluminum and copper conductors
	 * This class is to be supper class of metal-specific conductors like {@link CopperProp} and {@link AluminumProp} and should be use when
	 * a generic scenario is required a much better approach. For example, when requesting the ampacity or the AC resistance property of a
	 * conductor no matter if it's copper or aluminum the correct property will be returned.
	 */
	private class MetalProp {
		protected int amp60;
		protected int amp75;
		protected int amp90;
		protected double resInPVCCond;
		protected double resInALCond;
		protected double resInSteelCond;

		/**
		 * Constructs a generic conductor object with common to aluminum and copper conductors
		 * @param amp60 The ampacity of this generic conductor with an insulation rated for 60°C
		 * @param amp75 The ampacity of this generic conductor with an insulation rated for 75°C
		 * @param amp90 The ampacity of this generic conductor with an insulation rated for 90°C
		 * @param resInPVCCond The AC resistance of this generic conductor when used in a PVC conduit
		 * @param resInALCond The AC resistance of this generic conductor when used in an aluminum conduit
		 * @param resInSteelCond The AC resistance of this generic conductor when used in a steel conduit
		 */
		public MetalProp(int amp60, int amp75, int amp90, double resInPVCCond, double resInALCond, double resInSteelCond) {
			this.amp60 = amp60;
			this.amp75 = amp75;
			this.amp90 = amp90;
			this.resInPVCCond = resInPVCCond;
			this.resInALCond = resInALCond;
			this.resInSteelCond = resInSteelCond;
		}

		/**
		 * Returns the ampacity of this generic conductor for the given temperature rating
		 * @param temperature The temperature rating of the conductor's insulation
		 * @return The ampacity as per NEC 2014 table 310.15(B)(16) or zero if the temperature is not 60 or 75 or 90 degrees Celsius
		 */
		public int getAmpacity(int temperature){
			if(temperature == 60) return amp60;
			if(temperature == 75) return amp75;
			if(temperature == 90) return amp90;
			return 0;
		}

		/**
		 * Returns the AC resistance of this generic conductor when used in a conduit given's material
		 * @param material The material of the conduit
		 * @return The AC resistance in ohms per 1000 feet
		 */
		public double getACResistance(Material material){
			if(material == Material.PVC) return resInPVCCond;
			if(material == Material.ALUMINUM) return resInALCond;
			if(material == Material.STEEL) return resInSteelCond;
			return 0;
		}

		/**
		 * Returns the total resistance of this generic conductor for the given conduit material, length and number of parallel conductors
		 * @param material Conductor material as defined by {@link Material}
		 * @param oneWayLength The length of the conductor in feet
		 * @param numberOfSets The number of conductors in parallel
		 * @return The total AC resistance
		 */
		public double getACResistance(Material material, double oneWayLength, int numberOfSets){
			return getACResistance(material) * 0.001 * oneWayLength / numberOfSets;
		}
	}

	/**
	 * This class encapsulates the unique properties of a copper conductor. It is used when abstraction cannot be used for requesting copper
	 * specific properties; for example, if a conductor is know to be copper, the DC resistance for copper coated conductor can be requested
	 * (this could not be requested to its parent MetalProp object because it doesn't know about metal specific properties).
	 * @see MetalProp
	 * @see AluminumProp
	 */
	private class CopperProp extends MetalProp {
		private double resDCUncoated;
		private double resDCCoated;

		/**
		 * Constructs an aluminum conductor object with properties unique to aluminum conductors
		 *
		 * @param amp60           The ampacity of this copper conductor with an insulation rated for 60°C
		 * @param amp75           The ampacity of this copper conductor with an insulation rated for 75°C
		 * @param amp90           The ampacity of this copper conductor with an insulation rated for 90°C
		 * @param resInPVCCond    The AC resistance of this copper conductor when used in a PVC conduit
		 * @param resInALCond     The AC resistance of this copper conductor when used in an aluminum conduit
		 * @param resInSteelCond  The AC resistance of this copper conductor when used in a steel conduit
		 * @param cuResDCUncoated The DC resistance of this copper uncoated conductor
		 * @param cuResDCCoated   The DC resistance of this copper coated conductor
		 */
		public CopperProp(int amp60, int amp75, int amp90, double resInPVCCond, double resInALCond, double resInSteelCond,
		                  double cuResDCUncoated, double cuResDCCoated) {
			super(amp60, amp75, amp90, resInPVCCond, resInALCond, resInSteelCond);
			resDCUncoated = cuResDCUncoated;
			resDCCoated = cuResDCCoated;
		}

		/**
		 * Returns the DC resistance of this copper conductor
		 *
		 * @param isCoated Indicates if the desired resistance is for coated copper conductors
		 * @return The DC resistance in ohms per 1000 feet
		 */
		public double getDCResistance(Boolean isCoated) {
			if (isCoated) return resDCCoated;
			return resDCUncoated;
		}

		/**
		 * Returns the total resistance of this copper conductor for the given coating, length and number of parallel conductors
		 *
		 * @param isCoated     Indicates if the desired resistance is for coated copper conductors
		 * @param oneWayLength The length in feet of this conductor
		 * @param numberOfSets The number of conductors in parallel
		 * @return The total resistance under the specified conditions
		 */
		public double getDCResistance(Boolean isCoated, double oneWayLength, int numberOfSets) {
			return getDCResistance(isCoated) * 0.001 * oneWayLength / numberOfSets;
		}
	}

	/**
	 * This class encapsulates the unique properties of a aluminum conductor. It is used when abstraction cannot be used for requesting
	 * aluminum specific properties; for example, if a conductor is know to be aluminum, the DC resistance can be requested
	 * (this could not be requested to its parent MetalProp object because it doesn't know about metal specific properties).
	 * @see MetalProp
	 * @see CopperProp
	 */
	private class AluminumProp extends MetalProp {
		private double resDC;

		/**
		 * Constructs an aluminum conductor object with properties unique to aluminum conductors
		 * @param amp60 The ampacity of this aluminum conductor with an insulation rated for 60°C
		 * @param amp75 The ampacity of this aluminum conductor with an insulation rated for 75°C
		 * @param amp90 The ampacity of this aluminum conductor with an insulation rated for 90°C
		 * @param resInPVCCond The AC resistance of this aluminum conductor when used in a PVC conduit
		 * @param resInALCond The AC resistance of this aluminum conductor when used in an aluminum conduit
		 * @param resInSteelCond The AC resistance of this aluminum conductor when used in a steel conduit
		 * @param resDC The DC resistance of this aluminum conductor
		 */
		public AluminumProp(int amp60, int amp75, int amp90, double resInPVCCond, double resInALCond, double resInSteelCond, double resDC) {
			super(amp60, amp75, amp90, resInPVCCond, resInALCond, resInSteelCond);
			this.resDC = resDC;
		}

		/**
		 * Returns the DC resistance of this aluminum conductor
		 * @return The DC resistance in ohms per 1000 feet
		 */
		public double getDCResistance() {
			return resDC;
		}

		/**
		 * Returns the total resistance of this aluminum conductor for the given length and number of parallel conductors
		 * @param oneWayLength The length in feet of this conductor
		 * @param numberOfSets The number of conductors in parallel
		 * @return The total resistance under the specified conditions
		 */
		public double getDCResistance(double oneWayLength, int numberOfSets) {
			return getDCResistance() * 0.001 * oneWayLength / numberOfSets;
		}
	}

	private String size;
	private int areaCM;
	private double nonMagXL;
	private double magXL;
	private CopperProp copperCond;
	private AluminumProp aluminumCond;

	/**
	 * Constructs a PropertySet object. This constructor is used by the class {@link CondProp} to populate its internal table with
	 * data for all recognized conductor sizes as per table NEC 2014 310.15(B)(16), providing a set of electrical properties for each
	 * conductor size. It should not be used unless the user wants to build a special conductor (non listed in the NEC table).
	 * The constructor also constructs the associated {@link CopperProp} and {@link AluminumProp}.
	 * {@link AluminumProp} objects that encapsulate the correspondent properties according to the conductor metal type (copper or
	 * aluminum)
	 * @param size The size of this conductor as defined by {@link Size}
	 * @param CuAmp60 The ampacity of this copper conductor with an insulation rated for 60°C
	 * @param CuAmp75 The ampacity of this copper conductor with an insulation rated for 75°C
	 * @param CuAmp90 The ampacity of this copper conductor with an insulation rated for 90°C
	 * @param AlAmp60 The ampacity of this aluminum conductor with an insulation rated for 60°C
	 * @param AlAmp75 The ampacity of this aluminum conductor with an insulation rated for 75°C
	 * @param AlAmp90 The ampacity of this aluminum conductor with an insulation rated for 90°C
	 * @param nonMagXL The inductive reactance of this conductor when used in a non magnetic conduit
	 * @param magXL The inductive reactance of this conductor when used in a magnetic conduit
	 * @param CuResInPVCCond The AC resistance of this copper conductor when used in a PVC conduit
	 * @param CuResInALCond The AC resistance of this copper conductor when used in an aluminum conduit
	 * @param CuResInSteelCond The AC resistance of this copper conductor when used in a steel conduit
	 * @param ALResInPVCCond The AC resistance of this aluminum conductor when used in a PVC conduit
	 * @param ALResInALCond The AC resistance of this aluminum conductor when used in an aluminum conduit
	 * @param ALResInSteelCond The AC resistance of this aluminum conductor when used in a steel conduit
	 * @param areaCM The area in Circular Mil of this conductor
	 * @param CuResDCUncoated The DC resistance of this uncoated copper conductor
	 * @param CuResDCCoated The DC resistance of this coated copper conductor
	 * @param ALResDC The DC resistance of this aluminum conductor
	 */
	public PropertySet(String size, int CuAmp60, int CuAmp75, int CuAmp90, int AlAmp60, int AlAmp75, int AlAmp90, double nonMagXL,
	                   double magXL, double CuResInPVCCond, double CuResInALCond, double CuResInSteelCond, double ALResInPVCCond,
	                   double ALResInALCond, double ALResInSteelCond, int areaCM, double CuResDCUncoated, double CuResDCCoated,
	                   double ALResDC){
		this.size = size;
		this.areaCM = areaCM;
		this.nonMagXL = nonMagXL;
		this.magXL = magXL;

		copperCond = new CopperProp(CuAmp60, CuAmp75, CuAmp90, CuResInPVCCond, CuResInALCond, CuResInSteelCond,
				CuResDCUncoated, CuResDCCoated);
		aluminumCond = new AluminumProp(AlAmp60, AlAmp75, AlAmp90, ALResInPVCCond, ALResInALCond, ALResInSteelCond, ALResDC);
	}

	/**
	 * Returns the reactance property of this conductor under the given magnetic conduit condition
	 * @param magneticConduit Indicates if the conduit is magnetic or not
	 * @return The reactance of this conductor in ohms per 1000 feet
	 */
	public double getReactance(boolean magneticConduit){
		if(magneticConduit) return magXL;
		return nonMagXL;
	}

	/**
	 * Returns the total reactance of this conductor under the given magnetic conduit condition, for the given length and number of
	 * parallel conductors
	 * @param magneticConduit Indicates if the conduit is magnetic or not
	 * @param oneWayLength The length in feet of this conductor
	 * @param numberOfSets The number of conductors in parallel
	 * @return The total reactance under the specified conditions
	 */
	public double getReactance(boolean magneticConduit, double oneWayLength, int numberOfSets){
		return getReactance(magneticConduit) * 0.001 * oneWayLength / numberOfSets;
	}

	/**
	 * Returns the area property, in Circular Mils, of this conductor
	 * @return Returns the area in Circular Mils of this conductor
	 */
	public int getAreaCM() {
		return areaCM;
	}

	/**
	 * Returns the size of this conductor as defined by {@link Size}
	 * @return The size of this conductor
	 */
	public String getSize() {
		return size;
	}

	/**
	 * Returns the full size name of this conductor
	 * @return The full size name of this conductor including the prefix for AWG or KCMIL. If the size is not valid an empty string is
	 * returned.
	 * @see CondProp#getFullSizeName(String)
	 */
	public String getFullSizeName() {
		return CondProp.getFullSizeName(size);
	}

	/**
	 * Asks if the current object is an invalid PropertySet object
	 * @return True if invalid, false if valid
	 * @see CondProp#getInvalidPropertySet()
	 */
	public boolean isInvalid(){
		return this == CondProp.getInvalidPropertySet();
	}

	/**
	 * Returns the area property, in square inches, of this insulated conductor (conductor and insulation altogether) with the given
	 * insulation
	 * @param insulationName The insulation type of this conductor as defined by {@link Insul}
	 * @return The insulation type of this conductor as defined by {@link Insul}
	 * @see CondProp#getInsulatedAreaIn2(String, String)
	 */
	public double getInsulatedAreaIn2(String insulationName){
		return CondProp.getInsulatedAreaIn2(size, insulationName);
	}

	/**
	 * Returns the area property, in square inches, of this compact conductor (Table 5A) with the given insulation
	 * @param insulationName The insulation type of the conductor as defined by {@link Insul}
	 * @return The area of this compact conductor or zero if the insulation is invalid or the area is not defined in table 5.
	 * @see CondProp#getCompactAreaIn2(String, String)
	 */
	public double getCompactAreaIn2(String insulationName){
		return CondProp.getCompactAreaIn2(size, insulationName);
	}

	/**
	 * Returns the area property, in square inches, of this bare compact conductor (Table 5A)
	 * @return The area of this bare compact conductor or zero if the area is not defined in table 5A.
	 * @see CondProp#getCompactBareAreaIn2(String)
	 */
	public double getCompactBareAreaIn2(){
		return CondProp.getCompactBareAreaIn2(size);
	}

	/**
	 * Returns true if this insulated conductor with the given insulation has its area defined in table 5
	 * @param insulationName The insulation type of this conductor as defined by {@link Insul}
	 * @return True if the area is defined in table 5, false otherwise or the parameter is not valid.
	 * @see CondProp#hasInsulatedArea(String, String)
	 */
	public boolean hasInsulatedArea(String insulationName){
		return CondProp.hasInsulatedArea(size, insulationName);
	}

	/**
	 * Returns true if this compact conductor with the given insulation has its area defined in table 5A
	 * @param insulationName The insulation type of this conductor as defined by {@link Insul}
	 * @return True if the area is defined in table 5A, false otherwise or the parameter is not valid or if this is an invalidPropertySet
	 * @see CondProp#hasCompactArea(String, String)
	 */
	public boolean hasCompactArea(String insulationName){
		return CondProp.hasCompactArea(size, insulationName);
	}

	/**
	 * Returns true if this compact bare conductor has its area defined in table 5A
	 * @return True if the area is defined in table 5A, false otherwise or if this is an invalidPropertySet
	 * @see CondProp#hasCompactBareArea(String)
	 */
	public boolean hasCompactBareArea(){
		return CondProp.hasCompactBareArea(size);
	}

	/**
	 * Returns the DC resistance of this conductor size for the given metal, length, sets, etc. If the specified metal is aluminum, the
	 * copperCoated parameter is ignored.
	 * @param metal The metal of the conductor.
	 * @param length The length of the conductor.
	 * @param numberOfSets The number of conductors in parallel.
	 * @param copperCoated Indicates for a copper conductor if it is coated or not.
	 * @return The DC resistance in ohms of this conductor size under the given conditions.
	 */
	public double getDCResistance(Metal metal, double length, int numberOfSets, boolean copperCoated) {
		if(metal == Metal.COPPER)
			return copperCond.getDCResistance(copperCoated, length, numberOfSets);
		return aluminumCond.getDCResistance(length,numberOfSets);
	}

	/**
	 * Returns the DC resistance of this conductor size for the given metal. If the specified metal is aluminum, the
	 * copperCoated parameter is ignored.
	 * @param metal The metal of the conductor.
	 * @param copperCoated Indicates for a copper conductor if it is coated or not.
	 * @return The DC resistance of this conductor in ohms per 1000 feet.
	 */
	public double getDCResistance(Metal metal, boolean copperCoated) {
		if(metal == Metal.COPPER)
			return copperCond.getDCResistance(copperCoated);
		return aluminumCond.getDCResistance();
	}

	/**
	 * Returns the AC resistance of this conductor size for the given metal and conduit material.
	 * @param metal The metal of the conductor.
	 * @param conduitMaterial The material type of the conduit as specified in {@link Material}.
	 * @return The AC resistance in ohms per 1000 feet.
	 */
	public double getACResistance(Metal metal, Material conduitMaterial) {
		if(metal == Metal.COPPER)
			return copperCond.getACResistance(conduitMaterial);
		return aluminumCond.getACResistance(conduitMaterial);
	}

	/**
	 * Returns the AC resistance of this conductor size for the given metal, conduit material, length and number of sets.
	 * @param metal The metal of the conductor.
	 * @param conduitMaterial The material type of the conduit as specified in {@link Material}.
	 * @param length The length of the conductor in feet.
	 * @param numberOfSets The number of sets (conductors in parallel per phase)
	 * @return The AC resistance in ohms of this conductor size under the given conditions.
	 */
	public double getACResistance(Metal metal, Material conduitMaterial, double length, int numberOfSets) {
		if(metal == Metal.COPPER)
			return copperCond.getACResistance(conduitMaterial, length, numberOfSets);
		return aluminumCond.getACResistance(conduitMaterial, length, numberOfSets);
	}

	/**
	 * Returns the ampacity of this conductor size for the given metal and temperature rating.
	 * @param metal The metal of the conductor as defined in {@link Metal}.
	 * @param temperatureRating The temperature rating as defined in {@link eecalcs.systems.TempRating}
	 * @return The ampacity of this conductor size in amperes.
	 */
	public double getAmpacity(Metal metal, int temperatureRating) {
		if(metal == Metal.COPPER)
			return copperCond.getAmpacity(temperatureRating);
		return aluminumCond.getAmpacity(temperatureRating);
	}
}

