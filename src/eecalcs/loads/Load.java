package eecalcs.loads;

import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;

/**
 This class represents a generic load.
 The load's properties and their default values are::
 <p>- Voltage: 120V, 1-phase, 2-wires
 <p>- Current: 10A
 <p>- S: 1200VA
 <p>- P: 1200W
 <p>- Power factor is 1.0.
 <p>- Noncontinuous.
 <p>- MCA = Current = 10A
 <p>- Description is "Generic load".
 <p>- Linear
 <br><br>
 From these properties, voltage, and power factor are always independent and can
 be provided by the user in the constructor or in the setters; this means, they
 are never calculated from the other properties.
 <br><br>
 A Load object is constructed from <b>V</b>, <b>S</b> and <b>pf</b> as follow:
 <br>
 <pre>   I = S/(V*SQRT(f)) with f=1 for 1φ and f=3 for 3φ</pre>
 <pre>   P = S*pf</pre>
 <br>
 <p>When the voltage <b>V</b> or the power factor <b>pf</b> are set, the
 apparent power <b>S</b> is assumed to remain.

 The other properties are calculated as follow:
 <br><br>
 - Setting <b>S</b> will define <b>I</b> and <b>P</b>:
 <pre>   I = S/(V*SQRT(f)) with f=1 for 1φ and f=3 for 3φ</pre>
 <pre>   P = S*pf</pre>
 <br>
 - Setting <b>I</b> will define <b>S</b> and <b>P</b>:
 <pre>   S = V*I*SQRT(f)) f=1 for 1φ and f=3 for 3φ</pre>
 <pre>   P = S*pf</pre>
 <br>
 - Setting <b>P</b> will define <b>S</b> and <b>I</b>:
 <pre>   S = P/pf</pre>
 <pre>   I = P/(V*SQRT(f)*pf) with f=1 for 1φ and f=3 for 3φ</pre>
 <br>
 <p>Notice that when the current is calculated or set, the load type is set to be
 NONCONTINUOUS and the MCA gets the same value a the current.
 <p>To set the continuousness of the load, call the corresponding methods.
*/

/* future
    This could be complicated and should eventually be implemented.
    I need a class "LoadGroup" that descends from Load and bundles other single
    loads into one. This class will account for combination of different type of
    loads in one load (motors, general loads, etc.).
    Rules for combination of loads shall be applied. The LoadGroup class shall
    provide a method for requesting if a load (of a particular type) can be
    added to the group. This will account for combinations that might no be
    permitted by code or that are no considered a good practice.
    A load object type receptacle
 */

public class Load {
    private VoltageSystemAC voltageSystem = VoltageSystemAC.v120_1ph_2w;
    private double voltAmperes = 120 * 10;
    private double watts;
    private double current;
    private double MCA;
    private double powerFactor = 1.0;
    private LoadType loadType = LoadType.NONCONTINUOUS;
    private String description = "Generic load";
    private boolean linear = true;
    private NotifierDelegate notifier = new NotifierDelegate(this);

    //This and other computations are related to one of the phase conductors.
    private void computeCurrentAndWatts(){
        current = voltAmperes/(voltageSystem.getVoltage() * voltageSystem.getFactor());
        watts = voltAmperes * powerFactor;
        MCA = current;
        loadType = LoadType.NONCONTINUOUS;
    }

    /**
     Constructs a Load object with the given system voltage and apparent
     power. The power factor is defaulted to 1.

     @param voltageSystem The voltage system that feeds the load
     @param voltAmperes The apparent power in volt-amperes of the load.
     @see VoltageSystemAC
     */
    public Load(VoltageSystemAC voltageSystem, double voltAmperes) {
        this.voltageSystem = voltageSystem;
        this.voltAmperes = voltAmperes;
        computeCurrentAndWatts();
    }

    /**
     Constructs a Load object with the following default values:
     <p>- System AC voltage = 120v, 1 phase, 2 wires.
     <p>- Power S = 1200 va<br>
     */
    public Load(){
        computeCurrentAndWatts();
    }

    /**
    @return A deep copy of this Load object. The NotifierDelegate object is not
     copied.
     */
    @Override
    public Load clone(){
        Load load = new Load();
        load.voltageSystem = voltageSystem;
        load.voltAmperes = voltAmperes;
        load.watts = watts;
        load.current = current;
        load.MCA = MCA;
        load.powerFactor = powerFactor;
        load.loadType = loadType;
        load.description = description;
        load.linear = linear;
        return load;
    }

    /**
     @return The voltage system that feeds this load.
     @see VoltageSystemAC
     */
    public VoltageSystemAC getVoltageSystem() {
        return voltageSystem;
    }

    /**
     Sets the voltage system of this load. Current and real power are
     recalculated. Registered listeners receive notification of these changes.

     @param voltageSystem The new voltage system for this load.
     @see VoltageSystemAC
     */
    public void setVoltageSystem(VoltageSystemAC voltageSystem) {
        if(this.voltageSystem == voltageSystem)
            return;
        notifier.info.addFieldChange("voltageSystem", this.voltageSystem, voltageSystem);
        this.voltageSystem = voltageSystem;
        double oc = this.current;
        double ow = this.watts;
        computeCurrentAndWatts();
        notifier.info.addFieldChange("current", oc, this.current);
        notifier.info.addFieldChange("watts", ow, this.watts);
        notifier.notifyAllListeners();
    }

    /**
     @return The apparent power of this load, in volt-amperes.
     */
    public double getVoltAmperes() {
        return voltAmperes;
    }

    /**
     Sets the apparent power of this load. Current and real power are
     recalculated. Registered listeners receive notification of these changes.

     @param voltAmperes The new apparent power for this load.
     */
    public void setVoltAmperes(double voltAmperes) {
        if(this.voltAmperes == voltAmperes)
            return;
        notifier.info.addFieldChange("voltAmperes", this.voltAmperes, voltAmperes);
        this.voltAmperes = voltAmperes;
        double oc = this.current;
        double ow = this.watts;
        computeCurrentAndWatts();
        notifier.info.addFieldChange("current", oc, this.current);
        notifier.info.addFieldChange("watts", ow, this.watts);
        notifier.notifyAllListeners();
    }

    /**
     @return The real power of this load, in watts.
     */
    public double getWatts() {
        return watts;
    }

    /**
     Sets the real power of this load. Current and apparent power are
     recalculated. Registered listeners receive notification of these changes.

     @param watts The new real power of this load, in watts.
     */
    public void setWatts(double watts) {
        if(this.watts == watts)
            return;
        notifier.info.addFieldChange("watts", this.watts, watts);
        this.watts = watts;
        double nva = watts / powerFactor;
        notifier.info.addFieldChange("voltAmperes", this.voltAmperes, nva);
        voltAmperes = nva;
        double nc = watts/(voltageSystem.getVoltage()* voltageSystem.getFactor()*powerFactor);
        notifier.info.addFieldChange("current", this.current, nc);
        current = nc;
        notifier.notifyAllListeners();
    }

    /**
     @return The nominal current of this load. It's calculated based on the
     voltage system of the load and its apparent power.
     */
    public double getCurrent() {
        return current;
    }

    /**
     Sets a non-zero current value for this load. Apparent and real power are
     recalculated. Registered listeners receive notification of these changes.
     If the load is mixed, setting its current will make it to become
     noncontinuous.

     @param current The new current of the load, in amperes. If this value is
     zero, nothing is set.
     */
    public void setCurrent(double current) {
        if(this.current == current || current == 0)
            return;
        notifier.info.addFieldChange("current", this.current, current);
        this.current = current;
        double nva = voltageSystem.getVoltage() * current * voltageSystem.getFactor();
        notifier.info.addFieldChange("voltAmperes", this.voltAmperes, nva);
        voltAmperes = nva;
        double nw = voltAmperes * powerFactor;
        notifier.info.addFieldChange("watts", this.watts, nw);
        watts = nw;
        notifier.info.addFieldChange("MCA", this.MCA, current);
        if(loadType == LoadType.NONCONTINUOUS)
            MCA = current;
        else if(loadType == LoadType.CONTINUOUS)
            MCA = 1.25 * current;
        else {
            MCA = current;
            loadType = LoadType.NONCONTINUOUS;
        }
        notifier.notifyAllListeners();
    }

    /**
     @return The power factor of the load. A number between 0.7 and 1 inclusive.
     */
    public double getPowerFactor() {
        return powerFactor;
    }

    /**
     Sets the power factor of the load. Current and real power are recalculated.
     Registered listeners receive notification of this changes.

     @param powerFactor A value >= 0.7  and <=1.0 representing the new power
     factor of the load. Any value above or below the acceptable limits will be
     trimmed to the limit value, without notice.
     */
    public void setPowerFactor(double powerFactor) {
        if(this.powerFactor == powerFactor)
            return;
        if(powerFactor < 0.7)
            powerFactor = 0.7;
        else if(powerFactor > 1.0)
            powerFactor = 1.0;
        notifier.info.addFieldChange("powerFactor", this.powerFactor, powerFactor);
        this.powerFactor = powerFactor;
        double oc = this.current;
        double ow = this.watts;
        computeCurrentAndWatts();
        notifier.info.addFieldChange("current", oc, this.current);
        notifier.info.addFieldChange("watts", ow, this.watts);
        notifier.notifyAllListeners();
    }

    /**
     @return The type of the load in regards to its continuousness.
     @see LoadType
     */
    public LoadType getLoadType() {
        return loadType;
    }

    /**
     Makes this load a continuous load.
     */
    public void setContinuous(){
        if(loadType == LoadType.CONTINUOUS)
            return;
        notifier.info.addFieldChange("loadType", this.loadType, LoadType.CONTINUOUS);
        loadType = LoadType.CONTINUOUS;
        double newMCA = 1.25 * current;
        notifier.info.addFieldChange("MCA", MCA, newMCA);
        MCA = newMCA;
        notifier.notifyAllListeners();
    }

    /**
     Makes this load a non continuous load.
     */
    public void setNonContinuous(){
        if(loadType == LoadType.NONCONTINUOUS)
            return;
        notifier.info.addFieldChange("loadType", this.loadType, LoadType.NONCONTINUOUS);
        loadType = LoadType.NONCONTINUOUS;
        notifier.info.addFieldChange("MCA", MCA, current);
        MCA = current;
        notifier.notifyAllListeners();
    }

    /**
     Sets explicitly the MCA for this load and mark this load as a mixed load.
     Notice that MCA should always be equal or greater than the load nominal
     current. An attempt to set an MCA lesser than the load nominal current will
     convert this load to a NONCONTINUOUS one, with an MCA equal to the load
     nominal current.
     Also notice that there is no limitation on how bigger the MCA can be, in
     regards to the load current.
     @param MCA The new minimum circuit ampacity (MCA) for this load.
     */
    public void setMixed(double MCA){
        if(MCA == this.MCA)
            return;
        if(MCA <= current) { //this should never happen
            setNonContinuous();
            return;
        }
        notifier.info.addFieldChange("loadType", this.loadType, LoadType.MIXED);
        notifier.info.addFieldChange("MCA", this.MCA, MCA);
        loadType = LoadType.MIXED;
        this.MCA = MCA;
        notifier.notifyAllListeners();
    }

    /**
     @return The quotient between the MCA and the load current. Notice MCA >=1.
     */
    public double getMCAMultiplier(){
        return MCA/current;
    }
    /**
     @return The description of this load.
     */
    public String getDescription() {
        return description;
    }

    /**
     Sets the description of this load.
     @param description The new description of this load.
     */
    public void setDescription(String description) {
        if(this.description.equals(description))
            return;
        notifier.info.addFieldChange("description", this.description, description);

        this.description = description;
        notifier.notifyAllListeners();
    }

    /**
     @return The minimum circuit ampacity of this load.
     */
    public double getMCA(){
        return MCA;
    }

    /**
     @return The neutral current of this load. The returned value for this base
     class is either 0 or the value of the current of the phases.
     Load classes derived from this class should override this
     method to reflect the neutral current according to the load type.
     */
    public double getNeutralCurrent() {
        if(voltageSystem == VoltageSystemAC.v120_1ph_2w ||
           voltageSystem == VoltageSystemAC.v208_1ph_2wN || //high leg
           voltageSystem == VoltageSystemAC.v277_1ph_2w)
            return current;
        return 0;
    }

    /**
     @return The notifier delegate object for this object.
     */
    public NotifierDelegate getNotifier() {
        return notifier;
    }
}
