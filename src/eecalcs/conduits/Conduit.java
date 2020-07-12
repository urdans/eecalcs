package eecalcs.conduits;

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
 automatically when removing conductor/cables from it. A minimum trade size can
 be set to avoid the conduit to decrease below a desired valued; it's a way to
 account for spare use.

 <p>When a conductor or cable inside a conduit object changes one of its
 properties and that property affects the conduit size (like the size and number
 of the conductors or the outer diameter of the cable) the size of the conduit
 is updated accordingly.

 */
public class Conduit implements ShareableConduit {
    /**
     Indicates if a conduit is a nipple or not  (nipple: length < 24").
     <br>
     <ul>
     <li><b>Yes</b></li>
     <li><b>No</b></li>
     </ul>
     */
    public enum Nipple{Yes, No }

    private Trade minimumTrade = Trade.T1$2; //1/2"
    //the length of the conduit might no be of interest at this stage.
    private Nipple nipple;
    private Type type;
    private int allowedFillPercentage;
    private List<Conduitable> conduitables = new ArrayList<>();
    private double roofTopDistance = -1.0; //means no rooftop condition
    private static Message ERROR100	= new Message("The calculated trade size for this conduit is not recognized by" +
            " NEC Table 4 (not available.", -100);
    private static Message ERROR110	= new Message("The minimum conduit trade size is not valid.", -110);
    private static Message ERROR120	= new Message("The type of this conduit is not valid.", -120);
    private static Message ERROR130	= new Message("The nipple condition of this conduit is not valid.", -130);

    /*
    Check that the input data is valid (minimum size, conduit type and nipple
    condition.
     */
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
     Creates a default conduit, type PVC40, non nipple.
     */
    public Conduit() {
        this.type = Type.PVC40;
        setNipple(Nipple.No);
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
        if(conduitables.size() > 0)
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

    public boolean isEmpty(){
        return conduitables.isEmpty();
    }

    public boolean hasConduitable(Conduitable conduitable){
        return conduitables.contains(conduitable);
    }

    public Trade getMinimumTrade() {
        return minimumTrade;
    }

    public void setMinimumTrade(Trade minimumTrade) {
        this.minimumTrade = minimumTrade;
    }

    public int getFillingConductorCount(){
        return conduitables.size();
    }

    public int getCurrentCarryingNumber(){
        int currentCarrying = 0;
        for(Conduitable conduitable: conduitables)
            currentCarrying += conduitable.getCurrentCarryingCount();
        return currentCarrying;
    }

    public double getConduitablesArea(){
        double conduitablesArea = 0;
        for(Conduitable conduitable: conduitables)
            conduitablesArea += conduitable.getInsulatedAreaIn2();
        return conduitablesArea;
    }

    public Trade getTradeSize() {
        if(!checkInput())
            return null;
        double conduitableAreas = getConduitablesArea();
        int conductorsNumber = getFillingConductorCount();
        if(isNipple()) {
            allowedFillPercentage = 60;
        }
        else
        /*if(!isNipple())*/{
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

    public int getAllowedFillPercentage() {
        getTradeSize();
        return allowedFillPercentage;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isNipple() {
        return nipple == Nipple.Yes;
    }

    public void setNipple(Nipple nipple) {
        this.nipple = nipple;
/*        if(isNipple())
            allowedFillPercentage = 60;
        else
            allowedFillPercentage = 53;*/
    }

    public void setRoofTopDistance(double roofTopDistance){
        this.roofTopDistance = roofTopDistance;
    }

    public void resetRoofTop(){
        setRoofTopDistance(-1);
    }

    public boolean isRooftopCondition(){
        return (roofTopDistance > 0 && roofTopDistance <= 36) ;
    }

    public double getRoofTopDistance(){
        return roofTopDistance;
    }
}
