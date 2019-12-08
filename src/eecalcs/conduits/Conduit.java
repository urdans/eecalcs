package eecalcs.conduits;

import eecalcs.conductors.CircuitOld;
import eecalcs.conductors.Conductor;
import eecalcs.conductors.Conduitable;
import tools.Message;
import tools.ResultMessages;

import java.util.*;


/**
 This class represents an electrical conduit object.
 Conduit objects have a type as defined by {@link Type} and a trade size as
 defined by {@link Trade}. The conduit trade size is automatically increased to
 accommodate the conductors/cables it contains, in order to meet the NEC chapter
 9 requirements for conduit sizing. Likewise, its size is decreased
 automatically when removing conductor/cables from it. A minimum size can be set
 to avoid the conduit to decrease too much; it's a way to account for spare use.

 <p>When a conductor or cable inside a conduit object changes one of its
 properties and that property affects the conduit size (like the size and number
 of the conductors or the outer diameter of the cable) the size of the conduit
 is updated accordingly.

 */
public class Conduit {
    /**
     Indicates if a conduit is a nipple or not  (nipple=length less than 24").
     <br>
     <ul>
     <li><b>Yes</b></li>
     <li><b>No</b></li>
     </ul>
     */
    public enum Nipple{Yes, No }

    private Trade minimumTrade = Trade.T1$2;
    //the length of the conduit might no be of interest at this stage.
    private Nipple nipple;
    private Type type;
    private int allowedFillPercentage;
//    private List<CircuitOld> circuitOlds = new ArrayList<>();
//    private List<Conductor> conductors = new ArrayList<>();
    private List<Conduitable> conduitables = new ArrayList<>();
    private double roofTopDistance = -1.0; //means no rooftop condition

    private static Message ERROR100	= new Message("The calculated trade size for this conduit is not recognized by" +
            " NEC Table 4 (not available.", -100);
    private static Message ERROR110	= new Message("The minimum conduit trade size is not valid.", -110);
    private static Message ERROR120	= new Message("The type of this conduit is not valid.", -120);
    private static Message ERROR130	= new Message("The nipple condition of this conduit is not valid.", -130);

    private boolean checkInput(){
        resultMessages.clearMessages();
        if(minimumTrade == null)
            resultMessages.add(ERROR110);
        if(type == null)
            resultMessages.add(ERROR120);
        if(nipple == null)
            resultMessages.add(ERROR130);
        return !resultMessages.hasErrors();
    }

    /**
     Container for messages resulting from validation of input variables and
     calculations performed by this class.

     @see ResultMessages
     */
    public ResultMessages resultMessages = new ResultMessages();

    /**
     Returns the list of all conduitable objects that are inside this conduit
     (for instance, conductors and cables).

     @return The list of conduitable objects.
     @see Conduitable
     */
    public List<Conduitable> getConduitables() {
        return conduitables;
    }

    /**
     Creates a Conduit object of the type specified and indicating if it's a
     nipple or not. The trade size of this conduit is initially 1/2".

     @param type The type of the conduit to be created.
     @param nipple Indicates if the conduit is a nipple or not.
     @see Type
     @see Nipple
     */
    public Conduit(Type type, Nipple nipple) {
        this.type = type;
        setNipple(nipple);
    }

    /**
     Adds a conduitable object to this conduit.

     If the conduitable was inside another conduit, it will be removed from it.
     If the conduitable was part of a bundle, it will be removed from it.
     <p>The ambient temperature of the given conduitable will be set to the
     ambient temperature of any of the existing conduitables already in the
     conduit.

     @param conduitable The conduitable object to be added.
     @see Conduitable
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
     Removes the given conduitable from this conduit.

     @param conduitable The conduitable object to be removed.
     @see Conduitable
     */
    public void remove(Conduitable conduitable){
        if(conduitable == null)
            return;

        if(conduitables.remove(conduitable))
            conduitable.leaveConduit();
    }

    /**
     Removes all conduitables from this conduit. After a call to this method,
     this conduit will be empty.
     */
    public void empty() {
/*      Conduitable[] conduitableArray = new Conduitable[conduitables.size()];
        conduitableArray = conduitables.toArray(conduitableArray);
        for(Conduitable conduitable: conduitableArray) conduitable.leaveConduit();*/
        Object[] c = conduitables.toArray();
        for(Object o:c) ((Conduitable) o).leaveConduit();
    }

    /**
     Asks if this conduit is empty (contains no conduitable)

     @return True if empty.
     */
    public boolean isEmpty(){
        return conduitables.isEmpty();
    }

    /**
     Asks if this conduit already contains the given conduitable.

     @param conduitable The conduitable to check if it is already contained by
     this conduit.
     @return True if this conduit contains it, false otherwise.
     */
    public boolean hasConduitable(Conduitable conduitable){
        return conduitables.contains(conduitable);
    }

    /**
     Returns the minimum allowable trade size for this conduit.

     @return The minimum allowable trade size for this conduit.
     */
    public Trade getMinimumTrade() {
        return minimumTrade;
    }

    /**
     Sets the minimum trade size this conduit can reach.

     @param minimumTrade The trade size to be set as minimum for this conduit.
     @see Trade
     */
    public void setMinimumTrade(Trade minimumTrade) {
        this.minimumTrade = minimumTrade;
    }

    /**
     Returns the number of conductor that fills this conduit as set forth in
     NEC chapter 9, Table 1. Cables always count as one conductor.
     The returned number is used to compute the percentage of
     filling area in this conduit.

     @return The number of conductors.
     */
    public int getFillingConductorCount(){
/*        int conductorsNumber = 0;
        for(Conduitable conduitable: conduitables)
            conductorsNumber += conduitable.getConductorCount();
        return conductorsNumber;*/
        return conduitables.size();
    }

    /**
     Returns the number of current-carrying conductors inside this conduit.

     @return The number of current-carrying conductors inside this conduit.
     */
    public int getCurrentCarryingNumber(){
        int currentCarrying = 0;
        for(Conduitable conduitable: conduitables)
            currentCarrying += conduitable.getCurrentCarryingCount();
        return currentCarrying;
    }

    /**
     Returns the total area of the conductors filling this conduit, including
     the ones within any cables.

     @return The total area in square inches.
     */
    public double getConduitablesArea(){
        double conduitablesArea = 0;
        for(Conduitable conduitable: conduitables)
            conduitablesArea += conduitable.getInsulatedAreaIn2();
        return conduitablesArea;
    }

    /**
     Calculates the trade size of this conduit to accommodate all its
     conductors and cables. The calculation will depend on if this conduit
     is a nipple or not, and on the minimum trade size it can be.

     @return The calculated trade size of this conduit.
     */
    public Trade getTradeSize() {
        if(!checkInput())
            return null;
        double conduitableAreas = getConduitablesArea();
        int conductorsNumber = getFillingConductorCount();
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
        resultMessages.add(ERROR100);
        return null;
    }

    /**
     Asks for the allowed fill percentage of this conduit as defined by NEC
     Table 1 and note to table # 4.

     @return The allowed fill percentage of this conduit.
     */
    public int getAllowedFillPercentage() {
        getTradeSize();
        return allowedFillPercentage;
    }

    /**
     Asks for the type of this conduit.

     @return The type of this conduit.
     @see Type
     */
    public Type getType() {
        return type;
    }

    /**
     Sets the type of this conduit.

     @param type The new type of this conduit.
     @see Type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     Asks if this conduit is a nipple, that is, its length is equal or less
     than 24".

     @return True if it's a nipple, false otherwise.
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
