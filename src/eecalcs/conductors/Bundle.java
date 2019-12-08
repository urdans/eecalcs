package eecalcs.conductors;

import java.util.ArrayList;
import java.util.List;

/**
 This class represents a conduitable bundle. This bundle is a group of cables or
 a group of insulated conductors, or a group made of a mix of both, that are
 installed in free air (not in a conduit) next to each other (paralleled)
 without maintaining space (staked, bundled, supported on bridled rings or
 simply tied together, along a defined distance).
 <p><br>
 When cables are bundled, the heat produced by the current (joule effect) does
 not dissipate as easy as when they are separated. For this reason, the ampacity
 of the cable must be adjusted. The procedure to adjust the ampacity is
 described in <b>NEC-310.15(B)(3)</b>.
 <p>
 A bundle of insulated conductors are not common. The NEC does not prohibit it
 and rules 310.15(B)(a)(4) and (5) mention conductors as possible members of a
 bundle, therefore recognizing they can also form bundles. But, because of its
 rareness (insulated conductors not in raceway), it is subject to AHJ approval.
 <p><br>
 This class provides the methods to set up a bundle of cables and to calculate
 its ampacity adjustment factor.
 */
public class Bundle {
    private List<Conduitable> conduitables = new ArrayList<>();
    private double distance;

    /**
     Constructs a cable bundle. The cable bundle will contain the given number
     of copies of the given cable. If the given cable is null or if the given
     distance is equal or less than zero, an empty bundle will be created. An
     empty bundle is the container for start bundling insulated conductors.

     @param cable The cable to be copied to create the bundle.
     @param number The number of new cables in the bundle.
     @param distance The distance along which the cables are bundled, in inches.
     */
    public Bundle(Cable cable, int number, double distance)
    {
        if(distance < 0)
            distance = 0;
        this.distance = distance;
        if(cable == null | number <= 0)
            return;

        for(int i = 0; i < number; i++)
            add(cable.clone());
    }

    /**
     Add conduitable to this bundle. If the conduitable already has a conduit,
     it will be removed from it, or if the conduitable is already bundled, it
     will be removed from that bundle.
     <p>
     The ambient temperature of the conduitable will be set to the ambient
     temperature of any of the existing conduitables already in the bundle.

     @param conduitable The conduitable to be added to this bundle.
     */
    public void add(Conduitable conduitable){
        if(conduitable == null)
            return;

        if(conduitables.contains(conduitable))
            return;

        conduitable.leaveBundle();
        conduitable.leaveConduit();

        conduitables.add(conduitable);
        conduitable.setBundle(this);

        //setting the ambient temperature of this conduitable
        if(conduitables.size()>0)
            conduitable.setAmbientTemperatureF(conduitables.get(0).getAmbientTemperatureF());
    }

    /**
     Removes the given conduitable from this bundle.
     @param conduitable The conduitable to be removed from this bundle.
     */
    public void remove(Conduitable conduitable){
        if(conduitable == null)
            return;

        if(conduitables.remove(conduitable))
            conduitable.leaveBundle();
    }

    /**
     Removes all conduitables from this bundle. After a call to this method,
     this bundle will be empty.
     */
    public void empty(){
/*        Conduitable[] conduitableArray = new Conduitable[conduitables.size()];
        conduitableArray = conduitables.toArray(conduitableArray);
        for(Conduitable conduitable: conduitableArray)
            conduitable.leaveBundle();*/
        Object[] c = conduitables.toArray();
        for(Object o:c) ((Conduitable) o).leaveBundle();
    }

    /**
     Asks if this bundle is empty (contains no conduitable)

     @return True if empty.
     */
    public boolean isEmpty(){
        return conduitables.isEmpty();
    }

    /**
     Asks if this bundle already contains the given conduitable.

     @param conduitable The conduitable to check if it is already contained by
     this bundle.
     @return True if this bundle contains it, false otherwise.
     @see Conduitable
     */
    public boolean hasConduitable(Conduitable conduitable){
        return conduitables.contains(conduitable);
    }

    /**
     Returns the number of current-carrying conductors inside this bundle.

     @return The number of current-carrying conductors inside this bundle.
     */
    public int getCurrentCarryingNumber(){
        int currentCarrying = 0;
        for(Conduitable conduitable: conduitables)
            currentCarrying += conduitable.getCurrentCarryingCount();
        return currentCarrying;
    }

    /**
     Returns the list of all conduitable objects that are part of this bundle
     (for instance, conductors and cables).
     @return The list of conduitable objects.
     @see Conduitable
     */
    public List<Conduitable> getConduitables() {
        return conduitables;
    }

    /**
     Asks if all the cables in the bundle comply with the the conditions
     prescribed in <b>310.15(B)(3)(a)(4)</b>, as follow*:
     <i><ol type="a">
     <li>The cables ar MC or AC type.</li>
     <li>The cables do not have an overall outer jacket.</li>
     <li>Each cable has not more than three current-carrying conductors.</li>
     <li>The conductors are 12 AWG copper.</li>
     <li>Not more than 20 current-carrying conductors are bundled.</li>
     </ol></i>
     Since the bundle can have different type of cables and even other single
     conductors, the conditions must be interpreted to account or/and ignore the
     presence of other conduitables in the bundle, as follow:
     <i><ol type="a">
     <li>Single conductors are ignored. All the cables in the bundle are
     evaluated to comply with a.</li>
     <li>Ignore other type of cables and all single conductors, as the only ones
     that can have an outer jacket are MC and AC cables.</li>
     <li>Account for all other cable types, but ignore single conductors.</li>
     <li>Account for all single conductors and conductors forming all cables.</li>
     <li>Account for all single conductors and conductors forming all cables.</li>
     </ol></i>

     @return True if all above conditions are met, false otherwise.
     */
    public boolean complyWith310_15_B_3_a_4() {
        //testing condition e. on all cables and conductors
        if(getCurrentCarryingNumber() > 20)
            return false;
        for(Conduitable conduitable: getConduitables()){
            if(conduitable instanceof Cable){ //testing on cables only
                Cable cable = (Cable) conduitable;
                //testing condition a.
                if(cable.getType() != Cable.Type.AC && cable.getType() != Cable.Type.MC )
                    return false;
                //testing condition b.
                if(cable.isJacketed())
                    return false;
                //testing condition c.
                if(cable.getCurrentCarryingCount() > 3)
                    return false;
                //testing condition d. on cables
                if(cable.getPhaseConductorSize() != Size.AWG_12 | cable.getMetal() != Metal.COPPER)
                    return false;
            }
            else if(conduitable instanceof Conductor){
                Conductor conductor = (Conductor) conduitable;
                //testing condition d. on conductors
                if(conductor.getSize() != Size.AWG_12 | conductor.getMetal() != Metal.COPPER)
                    return false;
            }
        }
        return true;
    }

    /**
     Asks if all the cables in the bundle comply with the the conditions
     prescribed in <b>310.15(B)(3)(a)(5)</b>, as follow*:
     <i><ol type="a">
     <li>The cables ar MC or AC type.</li>
     <li>The cables do not have an overall outer jacket.</li>
     <li>The number of current carrying conductors exceeds 20.</li>
     <li>The bundle is longer than 24 inches.</li>
     </ol></i>
     Since the bundle can have different types of cables and even other single
     conductors, the conditions must be interpreted to account or/and ignore the
     presence of those other conduitables in the bundle, as follow:
     <i><ol type="a">
     <li>Single conductors are ignored. All the cables in the bundle are
     evaluated to comply with a.</li>
     <li>Ignore other type of cables and all single conductors, as the only ones
     that can have an outer jacket are MC and AC cables.</li>
     <li>Account for all single conductors and conductors forming all cables.</li>
     <li>Ignore all conduitables in the bundle.</li>
     </ol></i>

     @return True if all above conditions are met, false otherwise.
     */
    public boolean complyWith310_15_B_3_a_5() {
        //testing condition d.
        if(getDistance() <= 24)
            return false;
        //testing condition c. on all cables and conductors
        if(getCurrentCarryingNumber() <= 20)
            return false;
        //testing condition a and b on cables only.
        for(Conduitable conduitable: getConduitables()){
            if(conduitable instanceof Cable){
                Cable cable = (Cable) conduitable;
                //testing condition a.
                if(cable.getType() != Cable.Type.AC && cable.getType() != Cable.Type.MC )
                    return false;
                //testing condition b.
                if(cable.isJacketed())
                    return false;
            }
        }
        return true;
    }

    /**
     Returns the distance or length of the bundle.
     @return The distance or length of the bundle.
     */
    public double getDistance() {
        return distance;
    }

    /**
     Sets the distance of the bundle.
     @param distance The distance in inches.
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
