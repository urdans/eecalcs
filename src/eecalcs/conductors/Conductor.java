package eecalcs.conductors;
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

import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import tools.Listener;
import tools.Speaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the properties and methods for a single conductor as a physical real life object.
 */
public class Conductor implements Conduitable, Speaker {
	public enum Role{
		HOT, NEUCC, NEUNCC, GND, NCONC
	  /*yes,        yes,         no,     no,            no*/
/* Neutral counting as current carrying conductor
"120v 1Ø: => 2w:yes
"208v 1Ø: => 2w:no neutral present; 3w:yes
"208v 3Ø: => 3w:no; 4w:no; >50% harmonic:yes
"240v 1Ø: => 2w:no neutral present; 3w:yes
"240v 3Ø: => 3w:no; 4w:no; >50% harmonic:yes
"277v 1Ø: => 2w:yes
"480v 1Ø: => 2w:no neutral present; 3w:yes
"480v 3Ø: => 3w:no; 4w:no; >50% harmonic:yes*/
	}

	private List<Listener> listeners = new ArrayList<>();
	private Size size = Size.AWG_12;
	private Metal metal = Metal.COPPER;
	private Insul insulation = Insul.THW;
	private double length = 100;
	private int ambientTemperatureF = 86;
	private Coating copperCoated = Coating.UNCOATED;
	private Conduit conduit = null;
	private Role role = Role.HOT;
	private Bundle bundle;

	/**
	 * Constructs a conductor from the given characteristics
	 * @param size The size of the conductor as defined by {@link Size}
	 * @param metal The conductor metal as defined by {@link Metal}
	 * @param insulation The conductor's insulation type as defined by {@link Insul}
	 * @param length The length of the conductor in feet
	 * //@param role
	 */
	public Conductor(Size size, Metal metal, Insul insulation, double length) {
		this.size = size;
		this.metal = metal;
		this.insulation = insulation;
		this.length = Math.abs(length);
	}

	/* OLD CODE
	 * Constructs a Conductor object as a deep copy of the given conductor object. The new copy is exactly the same as the existing passed in
	 * conductor except:
	 * <p>-it does not copy the conduit property, that is, the new conductor is assumed in free air (not in a conduit).
	 * <p>-it does not copy the listener list.
	 * @param conductorToCopy The existing conductor to be copied.
	 *
	public Conductor(Conductor conductorToCopy) {
		this.size = conductorToCopy.size;
		this.metal = conductorToCopy.metal;
		this.insulation = conductorToCopy.insulation;
		this.length = conductorToCopy.length;
		this.temperatureRating = conductorToCopy.temperatureRating;
		this.ambientTemperatureF = conductorToCopy.ambientTemperatureF;
		this.copperCoated =  conductorToCopy.copperCoated;
		this.role = conductorToCopy.role;
	}*/

	/**
	 * Returns a deep copy of this Conductor object. The new copy is exactly the same as this conductor, except:
	 * <p>-it does not copy the conduit property, that is, the new clone is assumed in free air (not in a conduit).
	 * <p>-it does not copy the listener list of this Conductor.
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
	 * Constructs a default conductor object: size 12, copper, insulation type THW and length 100
	 */
	public Conductor(){}

	/**
	 * Gets the size of this conductor
	 * @return The size of this conductor
	 */
	public Size getSize() {
		return size;
	}

	/**
	 * Sets the size to this conductor
	 * @param size The size of the conductor as defined by {@link Size}
	 */
	public void setSize(Size size) {
		this.size = size;
	}

	/**
	 * Gets the metal of this conductor
	 * @return The metal of this conductor as defined by {@link Metal}
	 */
	public Metal getMetal() {
		return metal;
	}

	/**
	 * Sets the metal to this conductor
	 * @param metal The conductor metal as defined by {@link Metal}
	 */
	public void setMetal(Metal metal) {
		this.metal = metal;
	}

	/**
	 * Gets the insulation type of this conductor
	 * @return The insulation type of this conductor as defined by {@link Insul}
	 */
	public Insul getInsulation() {
		return insulation;
	}

	/**
	 * Sets the insulation type to this conductor
	 * @param insulation The conductor's insulation type as defined by {@link Insul}
	 */
	public void setInsulation(Insul insulation) {
		this.insulation = insulation;
	}

	/**
	 * Gets the length of this conductor
	 * @return The length of this conductor in feet
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
	 * Returns the ampacity of this conductor. The result accounts for the ambient temperature, the insulation of this conductor, and the number of
	 * other conductors that share the same raceway with this conductor. That is, the ampacity returned is corrected for ambient temperature (other
	 * than 86F/30C), and adjusted for the number of conductors in the same raceway.
	 * @return The ampacity in amperes.
	 */
	public double getAmpacity(){
		return ConductorProperties.getAmpacity(size, metal, ConductorProperties.getTempRating(insulation))
				* Factors.getTemperatureCorrectionF(ambientTemperatureF, ConductorProperties.getTempRating(insulation))
				* Factors.getAdjustmentFactor(conduit);
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
	 * Indicates if this copper conductor is coated.
	 * Notice that this property has no meaning when this conductor metal is aluminum.
	 * @return True if coated, false otherwise
	 */
	public boolean isCopperCoated(){
		if(metal == Metal.ALUMINUM)
			return false;
		return copperCoated.isCoated();
	}

	/**
	 * Sets the coating to this copper conductor.
	 * Setting this property does not have any effect if this conductor metal is aluminum.
	 * @param copperCoated Indicates if the conductor is coated
	 */
	public void setCopperCoated(Coating copperCoated) {
		this.copperCoated = copperCoated;
	}

	/**
	 * Return the coating of this copper conductor. The returned value has meaning only when the metal of this conductor is copper.
	 * @return The coating of this copper conductor.
	 */
	public Coating getCopperCoating(){
		return copperCoated;
	}

	@Override
	public void setConduit(Conduit conduit) {
		if(conduit == null)
			return;
		if(conduit.hasConduitable(this))
			this.conduit = conduit;
	}

	@Override
	public void leaveConduit(){
		if(this.conduit == null)
			return;
		if(!this.conduit.hasConduitable(this))
			this.conduit = null;
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
	 * Returns the temperature rating of this conductor which is defined by its insulator.
	 * @return The enum temperature rating.
	 * @see TempRating
	 */
	public TempRating getTemperatureRating() {
		return ConductorProperties.getTempRating(insulation);
	}

	/**
	 * Returns the role of this conductor as defined by {@link Role}.
	 * @return The role of this conductor.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Sets the role of this conductor as defined by {@link Role}.
	 * @param role The role of this conductor. Notice the default role if HOT.
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public int getCurrentCarryingCount() {
		if(role == Role.GND | role == Role.NEUNCC | role == Role.NCONC)
			return 0;
		return 1; //this is considering the neutral is a current carrying conductor.
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

	//todo to be implemented
	@Override
	public void setRoofTopDistance(double roofTopDistance) {

	}

	@Override
	public void resetRoofTop() {

	}

	@Override
	public void setBundle(Bundle bundle) {

	}

	@Override
	public void leaveBundle() {

	}

	@Override
	public Bundle getBundle() {
		return null;
	}

	@Override
	public boolean hasBundle() {
		return false;
	}
}
