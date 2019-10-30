package eecalcs.conduits;

import eecalcs.conductors.Circuit;
import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Conduitable;
import tools.EEToolsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * Returns the list of all conduitable objects that are inside this conduit (for instance, conductors, cables and circuits).
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

    /*todo work with messages; this method should add a result message to its result message list (when implemented)*/

    /**
     * Adds a conduitable object to this conduit.
     * @param conduitable The conduitable object to be added.
     * @return True if the given conduitable was added in this call. False if any of the following conditions exist:
     * - The given conduitable is null.
     * - The given conduitable is already registered with this conduit.
     * - The given conduitable is already registered with another conduit.
     * @see Conduitable
     */
    public boolean add(Conduitable conduitable){
        if(conduitable == null)
            return false;
        if(conduitables.contains(conduitable))
            return false;
        if(conduitable.hasConduit())
            return false;

        conduitables.add(conduitable);
        conduitable.setConduit(this);
        conduitable.setAmbientTemperatureF(conduitable.getAmbientTemperatureF());
        return true;
    }

    /**
     * Removes the conduitable from this conduit.
     * @param conduitable The conduitable object to be removed.
     * @return True if the given conduitable was removed in this call. False if any of the following conditions exist:
     * - The given conduitable is null.
     * - The given conduitable is registered with this conduit.
     * @see Conduitable
     */
    public boolean remove(Conduitable conduitable){
        if(conduitable == null)
            return false;
        if(conduitables.remove(conduitable)) {
            conduitable.leaveConduit();
            return true;
        }
        return false;
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
     * Returns the number of conductor inside this conduit, including the ones within any circuit. Cables always count as one conductor. The returned
     * number is used to compute the percentage of filling area in this conduit.
     * @return The number of conductors.
     */
    public int getConductorsNumber(){
        int conductorsNumber = 0;
        for(Conduitable conduitable: conduitables)
            conductorsNumber += conduitable.getConductorCount();
        return conductorsNumber;
    }

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

    public void setNipple(Nipple nipple) {
        this.nipple = nipple;
        if(isNipple())
            allowedFillPercentage = 60;
        else
            allowedFillPercentage = 53;
    }
}
