package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import tools.Listener;
import tools.Speaker;

import java.util.ArrayList;
import java.util.List;

/**
 Encapsulates the properties and methods for a single conductor in the context
 of its ambient temperature, its length, its {@link Conduit conduit} or
 {@link Bundle bundle} and its {@link Role role} or usage.
 <p>Generally, conductors are installed in raceways, that is, in conduits,
 boxes, wireways and cellular concrete floor raceways; busways and cablebuses
 are special cases of conductor-raceway combos. The raceway used in this class
 is the conduit. Boxes and wireways are treated by its corresponding classes.
 <p>In some rare conditions, conductor are installed grouped in bundles; that
 condition is accounted for by this class.
 <p>This class implements the class Speaker which allows it to forecast messages
 to its registered Listeners.
 */
public class Conductor implements Conduitable, Speaker {

	/**
	 Defines the role of a conductor.
	 <ul>
	 <li><b>HOT</b>: means the conductor is a phase conductor (an ungrounded
	 conductor as defined by the NEC)</li>
	 <li><b>NEUCC</b>: means the conductor is a neutral conductor (a grounded
	 conductor as defined by the NEC) that is also a current-carrying
	 conductor</li>
	 <li><b>NEUNCC</b>: means the conductor is a neutral conductor that is also
	 a non current-carrying conductor</li>
	 <li><b>GND</b>: means the conductor is used for grounding and bonding (EGC,
	 GEC, bonding jumpers, etc.)</li>
	 <li><b>NCONC</b>: means the conductor is a phase conductor that is not used
	 concurrently with other conductors, like the travelers in a 3-way circuit
	 for illumination.</li>
	 </ul>
	 The default role of a new created conductor is <b>HOT</b>, but this class
	 provides method to change it later, where required.
	 <p>The role of a conductor is determined outside of this class, by
	 evaluating other conditions. For example, to determine if a neutral
	 conductor is a current-carrying conductor, the system voltages can be
	 considered as follow:
	 <ol>
	 <li>120v 1Ø: => always 2w. Neutral is CCC.</li>
	 <li>208v 1Ø: => 2w:no neutral; 3w: always CCC.</li>
	 <li>208v 3Ø: => 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 <li>240v 1Ø: => 2w:no neutral; 3w: always CCC.</li>
	 <li>240v 3Ø: => 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 <li>277v 1Ø: => always 2w. Neutral is CCC.</li>
	 <li>480v 1Ø: => 2w:no neutral; 3w: always CCC.</li>
	 <li>480v 3Ø: => 3w:no neutral; 4w:neutral present but no CCC unless more
	 than 50% of the load is non-linear (harmonics).</li>
	 </ol>
	 */
	public enum Role{
		HOT("Hot, ungrounded conductor"),
		NEUCC("Neutral, grounded current-carrying conductor"),
		NEUNCC("Neutral, grounded non current-carrying conductor"),
		GND("Grounding and bonding conductor"),
		NCONC("Hot, ungrounded non concurrent conductor");

		private String description;
		private static String[] descriptions;

		static{
			descriptions = new String[values().length];
			for(int i=0; i<values().length; i++)
				descriptions[i] = values()[i].getDescription();
		}

		Role(String description){
			this.description = description;
		}

		/**
		 Returns the string description of the role this enum represents.

		 @return The description string.
		 */
		public String getDescription(){
			return description;
		}

		/**
		 Returns an array with the role description strings that the enum values
		 represent.

		 @return An array of description strings.
		 */
		public static String[] getDescriptions(){
			return descriptions;
		}
	}

	private List<Listener> listeners = new ArrayList<>();
	private Size size = Size.AWG_12;
	private Metal metal = Metal.COPPER;
	private Insul insulation = Insul.THW;
	private double length = 100;
	private int ambientTemperatureF = 86;
	private Coating copperCoated = Coating.UNCOATED;
	private Role role = Role.HOT;
	private Conduit conduit;
	private Bundle bundle;

	/**
	 Constructs a conductor with the given characteristics. The other properties
	 values default to:
	 <p>- Ambient temperature = 86°F.
	 <p>- Coating = no coating (meaningful for CU conductors only).
	 <p>- Conduit = null (conductor is in free air).
	 <p>- Bundle = null (conductor not grouped or bundled).
	 <p>- Role = HOT.

	 @param size The size of the conductor as defined by {@link Size}
	 @param metal The conductor metal as defined by {@link Metal}
	 @param insulation The conductor's insulation type as defined by
	 {@link Insul}
	 @param length The length of the conductor in feet (from the source to the
	 load, one way length).
	 */
	public Conductor(Size size, Metal metal, Insul insulation, double length) {
		this.size = size;
		this.metal = metal;
		this.insulation = insulation;
		this.length = Math.abs(length);
	}

	/**
	 Returns a deep and convenient copy of this Conductor object. The new copy
	 is exactly the same as this conductor, except: (convenience)
	 <p>- it does not copy the conduit property, that is, the new clone is
	 assumed in free air (not in a conduit).
	 <p>-it does not copy the listener list of this Conductor.
	 */
	@Override
	public Conductor clone(){
		Conductor conductorClone = new Conductor();
		conductorClone.size = this.size;
		conductorClone.metal = this.metal;
		conductorClone.insulation =this.insulation;
		conductorClone.length = this.length;
		conductorClone.ambientTemperatureF = this.ambientTemperatureF;
		conductorClone.copperCoated = this.copperCoated;
		conductorClone.role = this.role;
		return conductorClone;
	}

	/**
	 Constructs a default conductor object:
	 <p>- Size: 12 AWG
	 <p>- Metal: Copper
	 <p>- Insulation: THW
	 <p>- Length: 100 feet.
	 <p>- Ambient temperature = 86°F.
	 <p>- Coating = no coating (meaningful for CU conductors only).
	 <p>- Conduit = null (conductor is in free air).
	 <p>- Bundle = null (conductor not grouped or bundled).
	 <p>- Role = HOT.
	 */
	public Conductor(){}

	/**
	 Gets the size of this conductor.

	 @return The size of this conductor
	 */
	public Size getSize() {
		return size;
	}

	/**
	 Sets the size to this conductor.

	 @param size The size of the conductor as defined by {@link Size}
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	/**
	 Gets the metal of this conductor.

	 @return The metal of this conductor as defined by {@link Metal}
	 */
	public Metal getMetal() {
		return metal;
	}

	/**
	 Sets the metal to this conductor.

	 @param metal The conductor metal as defined by {@link Metal}
	 */
	public void setMetal(Metal metal) {
		this.metal = metal;
	}

	/**
	 Gets the insulation type of this conductor.

	 @return The insulation type of this conductor as defined by {@link Insul}
	 */
	public Insul getInsulation() {
		return insulation;
	}

	/**
	 Sets the insulation type to this conductor.

	 @param insulation The conductor's insulation type as defined by
	 {@link Insul}
	 */
	public void setInsulation(Insul insulation) {
		this.insulation = insulation;
	}

	/**
	 Gets the length of this conductor.

	 @return The length of this conductor in feet (on way length).
	 */
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = Math.abs(length);
	}

	@Override
	public double getInsulatedAreaIn2() {
		return ConductorProperties.getInsulatedAreaIn2(size, insulation);
	}

	/**
	 Returns the ampacity of this cable (for voltages up to 2000v).
	 <p><br>
	 The result accounts for the ambient temperature, the insulation of this
	 conductor, and the number of other conductors or cables that share the
	 same raceway or bundle with this conductor. That is, the ampacity returned
	 is corrected for ambient temperature (other than 86°F/30°C), and adjusted
	 for the number of conductors (including any cable) in the same raceway or
	 bundle. This is the ampacity for this conductor size, metal and
	 insulation type under the specified conditions (ambient temperature,
	 conduit, bundling, rooftop, etc.).
	 <p><br>
	 The rule allowing the temperature correction and adjustment factors to be
	 applied to the ampacity for the temperature rating of the conductor, if the
	 corrected and adjusted ampacity does not exceed the ampacity for the
	 temperature rating of the terminals in accordance with 110.14(C), is not
	 accounted for in this method. It is accounted for at the {@link Circuit}
	 class level.
	 <p><br>
	 If no correction factor is required ({@link #getCorrectionFactor()} returns 1), the
	 conductor should be sized per 110.14(C), that is:
	 <p>&emsp;
	 -per the 60°C column for conductors 14AWG thru 1AWG or circuits up to
	 100AMPS, UNLESS it's known the terminals are rated for 75°C.
	 <p>&emsp;
	 -per the 75°C column for conductors larger than 1AWG or circuits above
	 100AMPS.
	 <p>
	 In both cases, conductors with temperature ratings higher than specified
	 for terminations shall be permitted to be used for ampacity adjustment,
	 correction, or both. This is the reason why the rating of the terminals are
	 specified at a different level.
	 <p><br>
	 The ampacity of insulated conductor can be calculated, corrected and
	 adjusted for insulation rated for 75°C or 90°C but that ampacity shall not
	 exceed what would be required for a 60°C insulation. This rule appears
	 several times throughout the code.
	 <p><br><br>
	 A concrete example is as follow:
	 Suppose a load was calculated at 105 AMPS. The installer decides to use
	 THHW conductors which are rated for 90°C. Let's assume that there are 4
	 current-carrying conductors in the raceway and that the ambient temperature
	 is 100°C:
	 <p>&emsp;
	 - Temperature correction factor for a 90°C conductor
	 (TABLE 310.15(B)(2)(a)) = 0.91
	 <p>&emsp;
	 - Adjustment factor for four current-carrying conductors
	 (TABLE 310.15(B)(3)(a)) = 0.8
	 <p>&emsp;
	 -Ampacity of a # 1 AWG THHW = 145 AMPS
	 <p>&emsp;-
	 Allowed ampacity under specified conditions = 145*0.91*0.8 = 105.56 AMPS
	 <p>
	 The # 1 AWG THHW wire is good because the ampacity for the same wire at
	 60°C is 110AMP.
	 <p><br>
	 The general approach to determine the allowed ampacity is:
	 <p>&emsp;&emsp;
	 AllowedAmpacity*TCF*AF {@literal >}= Load Amps
	 <p>&emsp;&emsp;
	 AllowedAmpacity {@literal >}= (Load Amps)/(TCF*AF)
	 <p>&emsp;&emsp;
	 AllowedAmpacity {@literal >}= (105)/(0.91*0.8)
	 <p>&emsp;&emsp;
	 AllowedAmpacity {@literal >}= 144.23 AMPS.
	 <p>&emsp;&emsp;
	 Now, a conductor can be selected from table 310.15(B)(16):
	 <p>&emsp;&emsp;
	 It could be a #2/0 AWG TW, or a #1/0 AWG THW or a #1 AWG THHW.
	 <p><br>
	 This method alone does not calculate the allowed ampacity because the load
	 amps is not known at this level.
	 However, the method {@link #getCorrectionFactor()} will provide the (0.91*0.8) value
	 (from the example) that the {@link Circuit} class would need as reversed
	 coefficient to multiply the load amperes (to get the 144.23 AMPS from the
	 example). Then the method
	 {@link ConductorProperties#getAllowedSize(double, Metal, TempRating)} can
	 provide the proper size of the conductor.
	 p><br>

	 @return The ampacity in amperes.
	 */
	public double getAmpacity(){
		return ConductorProperties.getAmpacity(size, metal, ConductorProperties.getTempRating(insulation))
				* getCorrectionFactor()
				* getAdjustmentFactor();
	}

	/**
	 Returns the temperature correction factor to be applied to the ampacity.

	 @return The temperature correction factor.
	 */
	public double getCorrectionFactor(){
		int adjustedTemp = 0;
		if(hasConduit())
			adjustedTemp = Factors.getRoofTopTempAdjustment(conduit.getRoofTopDistance());
		if(insulation == Insul.XHHW2)
			adjustedTemp = 0;
		return Factors.getTemperatureCorrectionF(getAmbientTemperatureF() + adjustedTemp,
				getTemperatureRating());
	}

	/**
	 Returns the adjustment factor for ampacity of this cable, as per
	 <b>NEC 310.15(B)(3)</b>; it specifically accounts for rules
	 <b>310.15(B)(3)(a)(4) {@literal &} (5)</b> and <b>Table 310.15(B)(3)(a).</b>

	 @return The adjustment factor.
	 */
	public double getAdjustmentFactor() {
		if(hasConduit())
			return Factors.getAdjustmentFactor(conduit.getCurrentCarryingNumber(), conduit.isNipple());
		if(hasBundle()){
			return Factors.getAdjustmentFactor(bundle.getCurrentCarryingNumber(), bundle.getDistance());
		}
		return 1;
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}

	public void setAmbientTemperatureF(int ambientTemperatureF) {
		if(conduit != null)
			conduit.getConduitables().forEach(conduitable -> conduitable.setAmbientTemperatureFSilently(ambientTemperatureF));
		else if(bundle != null)
			bundle.getConduitables().forEach(conduitable -> conduitable.setAmbientTemperatureFSilently(ambientTemperatureF));
		else
			setAmbientTemperatureFSilently(ambientTemperatureF);
	}

	@Override
	public void setAmbientTemperatureFSilently(int ambientTemperatureF) {
		this.ambientTemperatureF = ambientTemperatureF;
	}

	/**
	 Asks if this copper conductor is coated.
	 Notice that this property has no meaning when this conductor metal is
	 aluminum.

	 @return True if coated, false otherwise.
	 */
	public boolean isCopperCoated(){
		if(metal == Metal.ALUMINUM)
			return false;
		return copperCoated.isCoated();
	}

	/**
	 Sets the coating to this copper conductor.
	 Setting this property does not have any effect if this conductor metal is
	 aluminum.

	 @param copperCoated Indicates if the conductor is coated
	 */
	public void setCopperCoated(Coating copperCoated) {
		this.copperCoated = copperCoated;
	}

	/**
	 Returns the coating of this copper conductor. The returned value has
	 meaning only when the metal of this conductor is copper.

	 @return The coating of this copper conductor.
	 */
	public Coating getCopperCoating(){
		return copperCoated;
	}

	@Override
	public void setConduit(Conduit conduit) {
		if(conduit == null)
			return;
		if(conduit == this.conduit)
			return;
		leaveBundle();
		leaveConduit();
		conduit.add(this);
		this.conduit = conduit;
	}

	@Override
	public void leaveConduit(){
		if(conduit == null)
			return;
		conduit.remove(this);
		conduit = null;
	}

	@Override
	public Conduit getConduit() {
		return conduit;
	}

	@Override
	public boolean hasConduit() {
		return conduit != null;
	}

	/**
	 Returns the temperature rating of this conductor which is defined by its
	 insulator.

	 @return The enum temperature rating.
	 @see TempRating
	 */
	public TempRating getTemperatureRating() {
		return ConductorProperties.getTempRating(insulation);
	}

	/**
	 Returns the role of this conductor as defined by {@link Role}.

	 @return The role of this conductor.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 Sets the role of this conductor as defined by {@link Role}.

	 @param role The role of this conductor. Notice the default role if HOT.
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public int getCurrentCarryingCount() {
		if(role == Role.GND | role == Role.NEUNCC | role == Role.NCONC)
			return 0;
		return 1; //this considers hot and neutral as current carrying conductor.
	}

	@Override
	public void addListener(Listener listener) {
		if(listeners.contains(listener))
			return;
		listeners.add(listener);
	}

	@Override
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public void notifyAllListeners() {
		for(Listener listener: listeners)
			listener.notify(this);
	}

	@Override
	public String getDescription() {
		//"#12 AWG THW (CU)(HOT)"
		return "#" + size.getName() + " " + insulation.getName()+ " (" + getMetal().getSymbol() + ")(" + role + ")";
	}

	@Override
	public void setBundle(Bundle bundle) {
		if(bundle == null)
			return;
		if(bundle == this.bundle)
			return;
		leaveConduit();
		leaveBundle();
		bundle.add(this);
		this.bundle = bundle;
	}

	@Override
	public void leaveBundle() {
		if(bundle == null)
			return;
		bundle.remove(this);
		bundle = null;
	}

	@Override
	public Bundle getBundle() {
		return bundle;
	}

	@Override
	public boolean hasBundle() {
		return bundle != null;
	}
}
