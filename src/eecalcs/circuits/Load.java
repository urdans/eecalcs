package eecalcs.circuits;

import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;

/**
 This class represents a generic load.
 The defaults properties of a generic loads are:
 <p>-Noncontinuous.
 <p>-Power factor is 1.0.
 <p>-Description is "Generic load".
 <br><br>
 <p>A load object keeps five electrical properties as follow:
 <p>─ <b>V</b> voltage in volts
 <p>─ <b>I</b> current in amperes
 <p>─ <b>S</b> apparent power in volt-amperes or VA
 <p>─ <b>P</b> real power in watts or W
 <p>─ <b>pf</b> power factor (non-dimensional)

 <br><br>
 From these properties, voltage, and power factor are always independent and are
 provided by the user in the constructor and in the setters; this means, they
 are never calculated from the other properties.
 <br><br>
 A Load object is constructed from <b>V</b> and <b>S</b> (<b>pf</b> defaults
 to 1), as follow:
 <br>
 <p><pre>   I = S/(V*SQRT(f)) with f=1 for 1φ and f=3 for 3φ</pre>
 <p><pre>   P = S*pf</pre>
 <br>
 <p>When the voltage <b>V</b> or the power factor <b>pf</b> are set, the
 apparent power <b>S</b> is assumed to remain.

 The other properties are calculated as follow:
 <br><br>
 ─ Setting <b>S</b> will define <b>I</b> and <b>P</b>:
 <p><pre>   I = S/(V*SQRT(f)) with f=1 for 1φ and f=3 for 3φ</pre>
 <p><pre>   P = S*pf</pre>
 <br>
 ─ Setting <b>I</b> will define <b>S</b> and <b>P</b>:
 <p><pre>   S = V*I*SQRT(f)) f=1 for 1φ and f=3 for 3φ</pre>
 <p><pre>   P = S*pf</pre>
 <br>
 ─ Setting <b>P</b> will define <b>S</b> and <b>I</b>:
 <p><pre>   S = P/pf</pre>
 <p><pre>   I = P/(V*SQRT(f)*pf) with f=1 for 1φ and f=3 for 3φ</pre>


 */
//todo to javadoc
public class Load /*implements Speaker*/ {
    private VoltageSystemAC systemVoltage = VoltageSystemAC.v120_1ph_2w;
    private double voltAmperes = 120 * 10;
    private double watts;
    private double current;
    private double powerFactor = 1.0;
    private boolean continuous = false;
    private String description = "Generic load";

    public NotifierDelegate notifier = new NotifierDelegate(this);

    //todo what about the load is mostly non linear?
    private void computeCurrentAndWatts(){
        current = voltAmperes/(systemVoltage.getVoltage() * systemVoltage.getFactor());
        watts = voltAmperes * powerFactor;
    }

    public Load(VoltageSystemAC systemVoltage, double voltAmperes) {
        this.systemVoltage = systemVoltage;
        this.voltAmperes = voltAmperes;
        computeCurrentAndWatts();
    }

    /**
     Constructs a Load object with the following default values:<br>
     - System AC voltage = 120v, 1 phase, 2 wires<br>
     - Power S = 1200 va<br>
     */
    public Load(){
        computeCurrentAndWatts();
    }

    @Override
    public Load clone(){
        Load load = new Load();
        load.voltAmperes = voltAmperes;
        load.watts = watts;
        load.current = current;
        load.powerFactor = powerFactor;
        load.continuous = continuous;
        load.description = description;
        return load;
    }

    public VoltageSystemAC getSystemVoltage() {
        return systemVoltage;
    }

    public void setSystemVoltage(VoltageSystemAC systemVoltage) {
        this.systemVoltage = systemVoltage;
        computeCurrentAndWatts();
        notifier.notifyAllListeners();
    }

    public double getVoltAmperes() {
        return voltAmperes;
    }

    public void setVoltAmperes(double voltAmperes) {
        this.voltAmperes = voltAmperes;
        computeCurrentAndWatts();
        notifier.notifyAllListeners();
    }

    public double getWatts() {
        return watts;
    }

    public void setWatts(double watts) {
        this.watts = watts;
        voltAmperes = watts / powerFactor;
        current = watts/(systemVoltage.getVoltage()*systemVoltage.getFactor()*powerFactor);
        notifier.notifyAllListeners();
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
        voltAmperes = systemVoltage.getVoltage() * current * systemVoltage.getFactor();
        watts = voltAmperes * powerFactor;
        notifier.notifyAllListeners();
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
        computeCurrentAndWatts();
        notifier.notifyAllListeners();
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
        notifier.notifyAllListeners();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifier.notifyAllListeners();
    }

    /**
     Returns the minimum current ampacity of the load. For this load, it
     accounts only for continuousness.

     @return
     */
    //todo learn more about mca and if this value depends only on the load and not on other external factors
    public double getMCA(){
        return continuous ? 1.25 * current : current;
    }

/*    @Override
    public void notifyAllListeners() {

    }

    @Override
    public void addListener(Listener listener) {

    }

    @Override
    public void removeListener(Listener listener) {

    }*/
}
