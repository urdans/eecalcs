package eecalcs.conductors;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     This class represents a cable bundle. A cable bundle is a group of cables installed in free air (not in a conduit)
 * next to each other (paralleled) without maintaining space (staked, bundled, supported on bridled rings or simply tied together,
 * along a defined distance.
 * <p><br>
 *     When cables are bundled, the heat produced by the current (joule effect) does not dissipate as easy as when they are separated.
 *     For this reason, the ampacity of the cable must be adjusted. The procedure to adjust the ampacity is described in <b>NEC-310.15(B)(3)</b>.
 * <p><br>
 *     This class provides the methods to set up a bundle of cables and to calculate the ampacity adjustment factor.
 */
public class Bundle {
    private List<Conduitable> conduitables = new ArrayList<>();
    private double distance;

    /**
     * Constructs a cable bundle. The cable bundle will contain the given number of copies of the given cable.
     * If the given cable is null or if the distance is equal or less than zero, an empty bundle will be created.
     * @param cable The cable to be copied to create the bundle.
     * @param number The number of new cables in the bundle.
     * @param distance The distance along which the cables are bundled, in inches.
     */
    public Bundle(Cable cable, int number, double distance)
    {
        this.distance = distance;
        if(cable == null | number <= 0)
            return;

        for(int i = 0; i<number; i++)
            add(cable.clone());
    }

    /**
     * Add an conduitable to this bundle.
     * If the conduitable has a conduit, it will be removed from it;
     * if the conduitable is already bundled, it will be removed from that bundle.
     * <p>The ambient temperature of the conduitable will be set to the ambient temperature
     * of any of the existing conduitables already in the bundle.
     * @param conduitable The conduitable to be added to this bundle.
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
     * Removes the given conduitable from this bundle.
     * @param conduitable The conduitable to be removed from this bundle.
     */
    public void remove(Conduitable conduitable){
        if(conduitable == null)
            return;

        if(conduitables.remove(conduitable))
            conduitable.leaveBundle();
    }

    /**
     * Asks if this bundle already contains the given conduitable.
     * @param conduitable The conduitable to check if it is already contained by this bundle.
     * @return True if this bundle contains it, false otherwise.
     */
    public boolean hasConduitable(Conduitable conduitable){
        return conduitables.contains(conduitable);
    }

    /**
     * Returns the number of current-carrying conductors inside this bundle.
     * @return The number of current-carrying conductors inside this bundle.
     */
    public int getCurrentCarryingNumber(){
        int currentCarrying = 0;
        for(Conduitable conduitable: conduitables)
            currentCarrying += conduitable.getCurrentCarryingCount();
        return currentCarrying;
    }

    /**
     * Returns the list of all conduitable objects that are part of this bundle (for instance, conductors and cables).
     * @return The list of conduitable objects.
     * @see Conduitable
     */
    public List<Conduitable> getConduitables() {
        return conduitables;
    }
}
