package eecalcs.circuits;

import eecalcs.systems.SystemAC;

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
public class Load {
    private SystemAC.Voltage systemVoltage = SystemAC.Voltage.v120_1ph;
    private SystemAC.Wires wires = SystemAC.Wires.W2;
    private double voltAmperes = 120 * 10;
    private double watts;
    private double current;
    private double powerFactor = 1.0;
    private boolean continuous = false;
    private String description = "Generic load";
    //todo what about the load is mostly non linear?
    private void computeCurrentAndWatts(){
        current = voltAmperes/(systemVoltage.getVoltage() * systemVoltage.getFactor());
        watts = voltAmperes * powerFactor;
    }

    public Load(SystemAC.Voltage systemVoltage, double voltAmperes, SystemAC.Wires wires) {
        this.systemVoltage = systemVoltage;
        this.voltAmperes = voltAmperes;
        this.wires = wires;
        computeCurrentAndWatts();
    }

    /**
     Constructs a Load object with the following default values:<br>
     - System AC voltage = 120v, 1 phase<br>
     - Wires = W2<br>
     - Power S = 1200 va<br>
     */
    public Load(){
        computeCurrentAndWatts();
    }

    public SystemAC.Voltage getSystemVoltage() {
        return systemVoltage;
    }

    public void setSystemVoltage(SystemAC.Voltage systemVoltage) {
        this.systemVoltage = systemVoltage;
        computeCurrentAndWatts();
    }

    public SystemAC.Wires getWires(){
        return wires;
    }

    public void setWires(SystemAC.Wires wires){
        this.wires = wires;
        computeCurrentAndWatts();
    }

    public double getVoltAmperes() {
        return voltAmperes;
    }

    public void setVoltAmperes(double voltAmperes) {
        this.voltAmperes = voltAmperes;
        computeCurrentAndWatts();
    }

    public double getWatts() {
        return watts;
    }

    public void setWatts(double watts) {
        this.watts = watts;
        voltAmperes = watts / powerFactor;
        current = watts/(systemVoltage.getVoltage()*systemVoltage.getFactor()*powerFactor);
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
        voltAmperes = systemVoltage.getVoltage() * current * systemVoltage.getFactor();
        watts = voltAmperes * powerFactor;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(double powerFactor) {
        this.powerFactor = powerFactor;
        computeCurrentAndWatts();
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
