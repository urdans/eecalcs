package eecalcs.conduits;

import eecalcs.conductors.Circuit;
import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Conduitable;
import tools.EEToolsException;

import java.util.*;


/**
 * This class represents an electrical conduit object.
 * Conduit objects have a type as defined by {@link Type} and a trade size as defined by {@link Trade}. The conduit trade size can be increased to
 * accommodate the circuits and/or conductors it contains, in order to meet the NEC chapter 9 requirements for conduit sizing. Likewise, its size can be
 * decreased when removing circuits or conductor from it. A minimum size can be set to avoid the conduit to decrease too much and to account for spare use.
 *
 * When a conductor or circuit, inside a conduit object, changes one of its properties and that property can affect the conduit size, like the size of the
 * conductors or the number of conductors in a circuit, the conduit object gets notified by that conductor or circuit object.
 * So, the circuit and conductor objects can fire a change event that its container conduit can catch and then update its size accordingly.
 * This means, the classes Conductor and Circuit know which conduit they belong to. However, a conductor/circuit can be installed in free air, not belonging to
 * any conduit.
 *
 * To achieve this purpose, this class Conduit implements the interface Listener; any object that wants to notify the conduit about its change, must
 * implement the interface Speaker. For instance, Conductor and Circuit classes do implement Speaker.
 */
public class Conduit {
    /**
     * Indicates if a conduit is a nipple or not  (nipple=length less than 24").
     */
    public enum Nipple{Yes, No }

    private Trade minimumTrade = Trade.T1$2;
    //the length of the conduit might no be of interest at this stage.
    private Nipple nipple;
    private Type type;
    private int allowedFillPercentage;
    private List<Circuit> circuits = new ArrayList<>();
    private List<Conductor> conductors = new ArrayList<>();
    private List<Conduitable> conduitables = new ArrayList<>();
    private double roofTopDistance = -1.0; //means no rooftop condition

    /**
     * Returns the list of all conduitable objects that are inside this conduit (for instance, conductors and cables).
     * @return The list of conduitable objects.
     * @see Conduitable
     */
    public List<Conduitable> getConduitables() {
        return conduitables;
    }

    /**
     * Creates a Conduit object of the type specified and indicating if it's a nipple or not. The trade size of this conduit is initially 1/2".
     * @param type The type of the conduit to be created.
     * @param nipple Indicates if the conduit is a nipple or not.
     * @see Type
     * @see Nipple
     */
    public Conduit(Type type, Nipple nipple) {
        this.type = type;
        setNipple(nipple);
    }

    /*todo work with messages; this method should add a result message to its result message list (when implemented)
    *  really?
    *  instead of this list, why not just implement the explain why list....or both..?*/

    /**
     * Adds a conduitable object to this conduit.
     * If the conduitable was inside another conduit, it will be removed from it.
     * If the conduitable was part of a bundle, it will be removed from it.
     * <p>The ambient temperature of the conduitable will be set to the ambient temperature
     * of any of the existing conduitables already in the conduit.
     * @param conduitable The conduitable object to be added.
     * @see Conduitable
     */
    public void add(Conduitable conduitable){
        if(conduitable == null)
            return;

        if(conduitables.contains(conduitable))
            return;

        conduitable.leaveConduit();
        conduitable.leaveBundle();

        conduitables.add(conduitable);
        conduitable.setConduit(this);

        //setting the ambient temperature of this conduitable
        if(conduitables.size()>0)
            conduitable.setAmbientTemperatureF(conduitables.get(0).getAmbientTemperatureF());
    }

    /**
     * Removes the given conduitable from this conduit.
     * @param conduitable The conduitable object to be removed.
     * @see Conduitable
     */
    public void remove(Conduitable conduitable){
        if(conduitable == null)
            return;

        if(conduitables.remove(conduitable))
            conduitable.leaveConduit();
    }

    /**
     * Removes all conduitables from this conduit. After a call to this method, this conduit will be empty.
     */
    public void empty() {
/*        Conduitable[] conduitableArray = new Conduitable[conduitables.size()];
        conduitableArray = conduitables.toArray(conduitableArray);
        for(Conduitable conduitable: conduitableArray) conduitable.leaveConduit();*/
        Object[] c = conduitables.toArray();
        for(Object o:c) ((Conduitable) o).leaveConduit();
    }

    /**
     * Asks if this conduit already contains the given conduitable.
     * @param conduitable The conduitable to check if it is already contained by this conduit.
     * @return True if this conduit contains it, false otherwise.
     */
    public boolean hasConduitable(Conduitable conduitable){
        return conduitables.contains(conduitable);
    }

    /**
     * Returns the minimum allowable trade size for this conduit.
     * @return The minimum allowable trade size for this conduit.
     */
    public Trade getMinimumTrade() {
        return minimumTrade;
    }

    /**
     * Sets the minimum trade size this conduit can reach.
     * @param minimumTrade The trade size to be set as minimum for this conduit.
     * @see Trade
     */
    public void setMinimumTrade(Trade minimumTrade) {
        this.minimumTrade = minimumTrade;
    }

    /**
     * Returns the number of conductor inside this conduit. Cables always count as one conductor. The returned
     * number is used to compute the percentage of filling area in this conduit.
     * @return The number of conductors.
     */
    public int getConductorsNumber(){
/*        int conductorsNumber = 0;
        for(Conduitable conduitable: conduitables)
            conductorsNumber += conduitable.getConductorCount();
        return conductorsNumber;*/
        return conduitables.size();
    }

    /**
     * Returns the number of current-carrying conductors inside this conduit.
     * @return The number of current-carrying conductors inside this conduit.
     */
    public int getCurrentCarryingNumber(){
        int currentCarrying = 0;
        for(Conduitable conduitable: conduitables)
            currentCarrying += conduitable.getCurrentCarryingCount();
        return currentCarrying;
    }

    /**
     * Returns the total area of the conductors filling this conduit, including the ones within any circuit and cables.
     * @return The total area in square inches.
     */
    public double getConduitablesArea(){
        double conduitablesArea = 0;
        for(Conduitable conduitable: conduitables)
            conduitablesArea += conduitable.getInsulatedAreaIn2();
        return conduitablesArea;
    }

    /**
     * Calculates the trade size of this conduit to accommodate all its conductors, cables and circuit. The calculation will depend on if this conduit
     * is a nipple or not, and on the minimum trade size it can be.
     * @return The calculated trade size of this conduit.
     */
    public Trade getTradeSize() {
        double conduitableAreas = getConduitablesArea();
        int conductorsNumber = getConductorsNumber();
        if(!isNipple()){
            if(conductorsNumber <= 1)
                allowedFillPercentage = 53;
            else if(conductorsNumber == 2)
                allowedFillPercentage = 31;
            else
                allowedFillPercentage = 40;
        }
        conduitableAreas /= (allowedFillPercentage * 0.01);
        Map<Trade, Double> areasForType = ConduitProperties.getAreasForType(type);
        for (int i = minimumTrade.ordinal(); i < Trade.values().length; i++)
            if(ConduitProperties.hasArea(type, Trade.values()[i])){
                if(areasForType.get(Trade.values()[i]) >= conduitableAreas)
                    return Trade.values()[i];
            }
        return null;
    }

    /**
     * Asks for the allowed fill percentage of this conduit as defined by NEC Table 1 and note to table # 4.
     * @return The allowed fill percentage of this conduit.
     */
    public int getAllowedFillPercentage() {
        getTradeSize();
        return allowedFillPercentage;
    }

    /**
     * Asks for the type of this conduit.
     * @return The type of this conduit.
     * @see Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of this conduit
     * @param type The new type of this conduit
     * @see Type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Asks if this conduit is a nipple, that is, its length is equal or less than 24".
     * @return True if it's a nipple, false otherwise.
     */
    public boolean isNipple() {
        return nipple == Nipple.Yes;
    }

    /**
     Marks this conduit has a nipple, that is, its length is 24" or less.

     @param nipple The nipple value as defined in {@link Nipple}.
     */
    public void setNipple(Nipple nipple) {
        this.nipple = nipple;
        if(isNipple())
            allowedFillPercentage = 60;
        else
            allowedFillPercentage = 53;
    }

    /**
     Sets the rooftop condition for this conduit.

     @param roofTopDistance The distance in inches above roof to bottom of this
     conduit. If a negative value is indicated, the behavior of this method is
     the same as when calling resetRoofTop, which eliminates the roof top
     condition from this conduit.
     */
    public void setRoofTopDistance(double roofTopDistance){
        this.roofTopDistance = roofTopDistance;
    }

    /**
     Resets the rooftop condition for this conduit, that is, no roof top
     condition.
     */
    public void resetRoofTop(){
        setRoofTopDistance(-1);
    }

    /**
     Asks if this conduit is in a rooftop condition as defined by
     <b>310.15(B)(3)(c)</b>.

     @return True if this conduit has a rooftop condition, false otherwise.
     */
    public boolean isRooftopCondition(){
        return (roofTopDistance > 0 && roofTopDistance <= 36) ;
    }

    /**
     Returns the rooftop distance of this conduit.

     @return The rooftop distance of this conduit.
     */
    public double getRoofTopDistance(){
        return roofTopDistance;
    }
}
