package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.SystemAC;
import eecalcs.systems.TempRating;
import tools.Listener;

/**
 This class encapsulates the properties of a cable.
 <p>
 A cable is a fabricated assembly of insulated conductors embedded into a
 protective jacket.
 <p>
 The NEC recognizes the following group of cables as wiring methods for
 permanent installations:
 <p>AC - Armored Cable (round)
 <p>MC - Metal Clad Cable (round)
 <p>FC - Flat Cable (flat, to be used in a surface metal raceway, not in
 conduits), <b>not covered by this software.</b>
 <p>FCC - Flat Conductor Cable (flat, to be used for branch circuits installed
 under carpet squares, not in conduits), <b>not covered by this software.</b>
 <p>IGS - Integrated Gas Spacer Cable (round, uses sulfur hexafluoride SF6 as
 insulator). Not a building wire, <b>not covered by this software.</b>
 <p>MV - Medium Voltage Cable (round, rated for 2001-35000 volts), <b>not
 covered by this software.</b>
 <p>MI - Mineral Insulated Cable (round, for especial conditions, like gasoline,
 oils, etc.), <b>not covered by this software.</b>
 <p>NM - Non metallic jacket
 <p>NMC - Non metallic corrosion resistant jacket.
 <p>NMS - Non metallic jacket, insulated power or control with signaling data.
 <p>TC - Power and Control Tray Cable (rounds, for power, lighting, controls and
 signaling circuits), <b>not covered by this software.</b>.
 <p>
 Each cable type may have a slightly different method of ampacity calculation,
 so each cable must be created as one of the types covered by this
 software (AC, MC, NM, NMC, NMS). MC cable is the default type.
 <p>
 The cable this class represents is made of:
 <ol>
 <li>- 1, 2 or 3 phase conductors. These are always considered current-carrying
 conductors (CCC).</li>
 <li>- 0 or 1 neutral conductor. If the neutral is present, it can be a CCC
 depending of the system voltage and number of wires.</li>
 <li>- 1 Equipment grounding conductor.</li>
 </ol>
 <p>
 The constructor of this cable takes as parameters the voltage system, the
 number of wires, and the outer diameter. Based on those parameters, the class
 determines if the neutral is required and if it is, it will create it as a copy
 of the hot conductor.
 <p>
 The grounding conductor is always created as a copy of the hot conductor as
 well.
 <p>
 The size of all these conductors (hot, neutral and grounding) can be later
 adjusted if required calling the appropriate method.
 <p>
 The outer diameter is used for conduit sizing. The number of CCC is used for
 determining the adjusted ampacity of this and other cables sharing the
 same conduit.
 <p>
 A cable used for a 240/208/120 volts delta system can be created as using any 3
 phase voltage and 4 wires.
 <p>
 The voltage system is used only to determined the presence of a neutral
 conductor and if it is initially a CCC.
 <p>
 As cables can be used as the conductors in a Circuit object, the size of its
 conductors can be adjusted by the circuit based on the circuit
 characteristics. Refer to the {@link Circuit} class for more information.
 */
public class Cable implements Conduitable, Listener{
    private Type cableType = Type.MC;
    private boolean jacketed = false;
    private SystemAC.Voltage voltage = SystemAC.Voltage.v120_1ph;
    private SystemAC.Wires wires = SystemAC.Wires.W2;
    private Conductor phaseAConductor = new Conductor();
    private Conductor phaseBConductor;
    private Conductor phaseCConductor;
    private Conductor neutralConductor;
    private Conductor groundingConductor = new Conductor();
    private double outerDiameter = 0.25;
    private Conduit conduit;
    private double roofTopDistance = -1.0; //means no rooftop condition
    private Bundle bundle;

    private int getHotCount(){
        return (phaseAConductor == null ? 0 : 1) + (phaseBConductor == null ? 0 : 1) + (phaseCConductor == null ? 0 : 1);
    }

    private int getNeutralCount(){
        return (neutralConductor == null ? 0 : 1);
    }

    /**
     Defines the type of cables recognized by the NEC that this software
     handles. These are cables that could be installed in a conduit. Special
     cables like flat, medium voltage, gas insulated and mineral insulated
     cables are not handled by the class.
     */
    public enum Type{
        AC("Armored Cable"),
        MC("Metal Clad Cable"),
        NM("Non Metallic Jacket Cable"),
        NMC("Non Metallic Jacket Corrosion Resistant Cable"),
        NMS("Non Metallic Jacket Cable with Power or Signaling Data Conductors");
        //TC("Power and Control Tray Cable"); Not covered for now
        private String name;
        private static String[] names;

        static{
            names = new String[values().length];
            for(int i=0; i<values().length; i++)
                names[i] = values()[i].getName();
        }

        Type(String name){
            this.name = name;
        }

        /**
         Returns the string name that this enum represents.

         @return The string name.
         */
        public String getName() {
            return name;
        }

        /**
         Returns an array of the string names that the enum values represent.

         @return An array of strings
         */
        public static String[] getNames(){
            return names;
        }
    }

    /**
     Creates an MC (default) cable object of the given system voltage, number of
     wires and outer diameter. The conductors of this cable have default sizes,
     metal and insulation. Refer to {@link Conductor#Conductor()} default
     constructor for details.

     @param voltage The voltage system intended to be used with this cable. It
     will define the existence of a neutral.
     @param wires The number of wires of the prior voltage system parameter.
     @param outerDiameter The outer diameter of this cable in inches. If the
     cable sectional shape if not a circle, the longest diameter must be passed
     in. Also, if this parameter is less than 0.25 inches, a value of 0.25
     inches is assumed.
     */
    public Cable(SystemAC.Voltage voltage, SystemAC.Wires wires, double outerDiameter){
        setSystem(voltage, wires);
        phaseAConductor.setRole(Conductor.Role.HOT);
        groundingConductor.setRole(Conductor.Role.GND);
        setOuterDiameter(outerDiameter);
    }

    /**
     * Constructs a default cable:
     * <p>MC type.
     * <p>Non jacketed.
     * <p>Voltage system 120V 1ph 2 wires.
     * <p>One conductor for phase A, one for neutral and one for ground.
     * <p>The neutral is CCC
     * <p>Outer diameter = 0.25"
     * <p>No conduit, no bundle.
     * <p>No roof top condition
     */
    public Cable(){
        phaseAConductor.setRole(Conductor.Role.HOT);
        groundingConductor.setRole(Conductor.Role.GND);
    }

    /**
     Returns a deep copy of this Cable object. The new copy is exactly the same
     as this cable, except that it does not copy the conduit property, that is,
     the new clone is assumed in free air (not in a conduit).
     */
    @Override
    public Cable clone(){
        Cable cable = new Cable();
        cable.cableType = this.cableType;
        cable.jacketed = this.jacketed;
        cable.voltage = this.voltage;
        cable.wires = this.wires;
        cable.phaseAConductor = this.phaseAConductor.clone();
        cable.phaseBConductor = this.phaseBConductor == null ? null : phaseBConductor.clone();
        cable.phaseCConductor = this.phaseCConductor == null ? null : phaseCConductor.clone();
        cable.neutralConductor = this.neutralConductor == null ? null : neutralConductor.clone();
        cable.groundingConductor = this.groundingConductor.clone();
        cable.outerDiameter = this.outerDiameter;
        cable.conduit = null;
        cable.roofTopDistance = this.roofTopDistance;
        return cable;
    }

    /**
     Returns the string representation of this cable object.

     @return The string representation of this cable object.
     */
    @Override
    public String toString(){
        return cableType+", "+jacketed+", "+voltage+", "+wires+", "+phaseAConductor+", "+phaseBConductor+", "+phaseCConductor+", "+
                neutralConductor+", "+groundingConductor+", "+outerDiameter+", "+conduit+", "+roofTopDistance;
    }

    /**
     Asks if this cable has an outer jacket. Most cables don't have it. MC and
     AC cable are commonly non jacketed but versions with an outer PVC jacket
     are available in the market. This value is false by default and remains
     unchanged, unless is set by calling the
     {@link #setJacketed(boolean jacketed)} method.

     @return True if the cable has an outer jacket, false otherwise.
     */
    public boolean isJacketed() {
        return jacketed;
    }

    /**
     Returns the adjustment factor for ampacity of this cable, as per
     <b>NEC 310.15(B)(3)</b>; it specifically accounts for rules
     <b>310.15(B)(3)(a)(4) {@literal &} (5)</b> and <b>Table 310.15(B)(3)(a).</b>

     @return The adjustment factor.
     */
    public double getAdjustmentFactor() {
        if(hasConduit())
            return Factors.getAdjustmentFactor(conduit.getCurrentCarryingNumber(), conduit.isNipple());
        if(hasBundle()){
            if(bundle.complyWith310_15_B_3_a_4())
                return 1;

            if(bundle.complyWith310_15_B_3_a_5())
                return 0.6;
            return Factors.getAdjustmentFactor(bundle.getCurrentCarryingNumber(), bundle.getDistance());
        }
        return 1;
    }

    /**
     Sets the outer jacket condition for this cable as indicated in the given
     parameter. Jacketed condition is meaningful only for AC or MC type cables;
     that is, if the cable is non AC or MC type, the jacketed condition is
     always false; but if the cable is AC or MC type, the cable can be either
     jacketed or non-jacketed as assigned by this method. In other words,
     calling this method for cables other than AC or MC type will always set the
     non jacketed condition even if called with a True value parameter.

     @param jacketed The condition for this cable. True: sets this cable as
     having an outer jacket, False: the opposite case.
     */
    public void setJacketed(boolean jacketed) {
        if(cableType == Type.AC | cableType == Type.MC)
            this.jacketed = jacketed;
        else
            this.jacketed = false;
    }

    /**
     Asks if this cable is jacketed.

     @return True if jacketed, false otherwise.
     */
    public boolean getJacketed() {
        return this.jacketed;
    }

    /**
     Sets the outer diameter of this cable.

     @param outerDiameter The outer diameter in inches.
     */
    public void setOuterDiameter(double outerDiameter) {
        if(outerDiameter < 0.25)
            outerDiameter = 0.25;
        this.outerDiameter = outerDiameter;
    }

    /**
     Returns the outer diameter of the cable.

     @return The outer diameter of the cable in inches.
     */
    public double getOuterDiameter() {
        return outerDiameter;
    }

    /**
     Sets the neutral conductor of this cable (if present) as a current-carrying
     conductor for 3-phase 4-wire systems.

     @param neutralCarryingConductor True if the neutral is a current-carrying
     conductor, false otherwise.
     */
    public void setNeutralCarryingConductor(boolean neutralCarryingConductor){
        if(neutralConductor != null){
            if(neutralCarryingConductor)
                neutralConductor.setRole(Conductor.Role.NEUCC);
            else
                neutralConductor.setRole(Conductor.Role.NEUNCC);
        }
    }

    /**
     Asks if the neutral of this cable (if present) is a current-carrying
     conductor.

     @return True if it's a current carrying conductor, false otherwise.
     */
    public boolean isNeutralCarryingConductor() {
        if(neutralConductor != null)
            return neutralConductor.getRole() == Conductor.Role.NEUCC;
        return false;
    }

    /**
     Sets the voltage system for this cable. Setting this property may change
     the existence of the phase and neutral conductor or their properties. In
     particular, the size of the neutral can be changed to the size of the
     phases; its role can be changed as well.

     @param voltage The voltage as defined in {@link SystemAC.Voltage}
     @param wires The wiring system as defined in {@link SystemAC.Wires}
     */
    public void setSystem(SystemAC.Voltage voltage, SystemAC.Wires wires){
        this.voltage = voltage;
        this.wires = wires;
        if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph) {
            //the number of wires is always 2 for this type of system, hence wires is ignored
            this.wires = SystemAC.Wires.W2;
            if(neutralConductor == null)
                neutralConductor = new Conductor();
            neutralConductor.setRole(Conductor.Role.NEUCC);
            neutralConductor.setSize(phaseAConductor.getSize());
            phaseBConductor = null;
            phaseCConductor = null;
        }
        else if(voltage == SystemAC.Voltage.v208_1ph | voltage == SystemAC.Voltage.v240_1ph | voltage == SystemAC.Voltage.v480_1ph){
            if(phaseBConductor == null)
                phaseBConductor = new Conductor();
            phaseBConductor.setRole(Conductor.Role.HOT);
            phaseBConductor.setSize(phaseAConductor.getSize());
            if(wires == SystemAC.Wires.W2){
                neutralConductor = null;
            }
            else {//Any 2-hot system of this voltage, indicated as w3 or w4 will have a neutral and will be assumed as a w3
                this.wires = SystemAC.Wires.W3;
                if(neutralConductor == null)
                    neutralConductor = new Conductor();
                neutralConductor.setRole(Conductor.Role.NEUCC);
                neutralConductor.setSize(phaseAConductor.getSize());
            }
            phaseCConductor = null;
        }
        else {//this account for all 3-phase systems.
            if(phaseBConductor == null)
                phaseBConductor = new Conductor();
            phaseBConductor.setRole(Conductor.Role.HOT);
            phaseBConductor.setSize(phaseAConductor.getSize());
            if(phaseCConductor == null)
                phaseCConductor = new Conductor();
            phaseCConductor.setRole(Conductor.Role.HOT);
            phaseCConductor.setSize(phaseAConductor.getSize());
            if(wires == SystemAC.Wires.W4){
                neutralConductor = new Conductor();
                neutralConductor.setRole(Conductor.Role.NEUNCC);
                neutralConductor.setSize(phaseAConductor.getSize());
            }
            else {//Any 3-phase system indicated as w2 or w3 will not have a neutral
                neutralConductor = null;
                this.wires = SystemAC.Wires.W3;
            }
        }
    }

    /**
     Returns the total cross sectional area of the cable.

     @return The are of the cable in square inches.
     */
    public double getInsulatedAreaIn2(){
        return Math.PI * 0.25 * outerDiameter * outerDiameter;
    }

    @Override
    public int getCurrentCarryingCount() {
        int ccc = 1; //Phase A counts always as 1
        if(phaseBConductor != null)
            ccc += phaseBConductor.getCurrentCarryingCount();
        if(phaseCConductor != null)
            ccc += phaseCConductor.getCurrentCarryingCount();
        if(neutralConductor != null)
            ccc += neutralConductor.getCurrentCarryingCount();
        return ccc;
    }

    /**
     Returns the temperature correction factor to be applied to the ampacity.

     @return The temperature correction factor.
     */
    public double getCorrectionFactor(){
        int adjustedTemp;
        if(hasConduit())
            adjustedTemp = Factors.getRoofTopTempAdjustment(conduit.getRoofTopDistance());
        else
            adjustedTemp = Factors.getRoofTopTempAdjustment(roofTopDistance);

        return Factors.getTemperatureCorrectionF(phaseAConductor.getAmbientTemperatureF() + adjustedTemp,
                phaseAConductor.getTemperatureRating());
    }

    /**
     Returns the ampacity of this cable (for voltages up to 2000v).
     <p><br>
     The result accounts for the ambient temperature, the insulation of the hot
     conductors, and the number of other conductors or cables that share the
     same raceway or bundle with this cable. That is, the ampacity returned is
     corrected for ambient temperature (other than 86°F/30°C), and adjusted for
     the number of cables (including any insulated conductor) in the same
     raceway or bundle. This is the ampacity for this cable size, metal and
     insulation type under the specified conditions (ambient temperature,
     bundling, rooftop, etc.)
     <p><br>
     The rule allowing the temperature correction and adjustment factors to be
     applied to the ampacity for the temperature rating of the conductor, if the
     corrected and adjusted ampacity does not exceed the ampacity for the
     temperature rating of the terminals in accordance with 110.14(C), is not
     accounted for in this method. It is accounted for at the {@link Circuit}
     class level.
     <p><br>
     If no correction factor is required ({@link #getCorrectionFactor()} returns 1), the
     cable hot conductors should be sized per 110.14(C), that is:
     <p>&emsp;
     -per the 60°C column for conductors 14AWG thru 1AWG or circuits up to
     100AMPS, UNLESS it's known the terminals are rated for 75°C.
     <p>&emsp;
     -per the 75°C column for conductors larger than 1AWG or circuits above
     100AMPS.
     <p>
     In both cases, conductors with temperature ratings higher than specified
     for terminations shall be permitted to be used for ampacity adjustment,
     correction, or both. This is the reason why the rating of the terminals are
     specified at a different level.
     <p><br>
     For example, NM, NMC and NMS cables, their ampacity can be calculated,
     corrected and adjusted for 75°C or 90°C but that ampacity shall not exceed
     what would be required for a 60°C. This rule appears several times
     throughout the code.
     <p><br><br>
     A concrete example is as follow:
     Suppose a load was calculated at 105 AMPS. The installer decides to use
     THHW conductors which are rated for 90°C. Let's assume that there are 4
     current-carrying conductors in the raceway and that the ambient temperature
     is 100°C:
     <p>&emsp;
     -T emperature correction factor for a 90°C conductor
     (TABLE 310.15(B)(2)(a)) = 0.91
     <p>&emsp;
     - Adjustment factor for four current-carrying conductors
     (TABLE 310.15(B)(3)(a)) = 0.8
     <p>&emsp;
     - Ampacity of a # 1 AWG THHW = 145 AMPS
     <p>&emsp;
     - Allowed ampacity under specified conditions = 145*0.91*0.8 = 105.56 AMPS
     <p>
     The # 1 AWG THHW wire is good because the ampacity for the same wire at
     60°C is 110AMP.
     <p><br>
     The general approach to determine the allowed ampacity is:
     <p>&emsp;&emsp;
     AllowedAmpacity*TCF*AF {@literal >}= Load Amps
     <p>&emsp;&emsp;
     AllowedAmpacity {@literal >}= (Load Amps)/(TCF*AF)
     <p>&emsp;&emsp;
     AllowedAmpacity {@literal >}= (105)/(0.91*0.8)
     <p>&emsp;&emsp;
     AllowedAmpacity {@literal >}= 144.23 AMPS.
     <p>&emsp;&emsp;
     Now, a conductor can be selected from table 310.15(B)(16):
     <p>&emsp;&emsp;
     It could be a #2/0 AWG TW, or a #1/0 AWG THW or a #1 AWG THHW.
     <p><br>
     This method alone does not calculate the allowed ampacity because the load
     amps is not known at this level.
     However, the method {@link #getCorrectionFactor()} will provide the (0.91*0.8) value
     (from the example) that the {@link Circuit} class would need as reversed
     coefficient to multiply the load amperes (to get the 144.23 AMPS from the
     example). Then the method
     {@link ConductorProperties#getAllowedSize(double, Metal, TempRating)} can
     provide the proper size of the conductor.
     p><br>
     Adjustment factor exceptions apply to AC and MC cable under the conditions
     explained in 310.15(B)(3)(a)(4).
     <p>What this method DOES NOT cover:
     <p><br>
     Ampacity for MC cable:
     <p>&emsp;&emsp;
     -for voltages higher than 2000v and sizes 14 AWG and up (table 310.60).
     <p>&emsp;&emsp;
     -for sizes 18 AWG and 16 AWG (table 402.5). This software does not cover
     conductor/cables smaller than 14 AWG.
     <p>&emsp;&emsp;
     -when installed in cable tray (392.80)
     <p>
     Ampacity for AC cable:
     <p>&emsp;&emsp;
     -when installed in thermal insulation ( 320.80(A) ).
     <p>&emsp;&emsp;
     -when installed in cable tray (392.80(A) as required by 320.80(B)).
     <p>
     Ampacity for NM cable:
     <p>&emsp;&emsp;
     -when installed in cable tray (392.80(A) as required by 334.80).
     <p>&emsp;&emsp;
     -when installed through wood framing or in contact with thermal insulation
     as explained in 334.80.
     <p>
     Ampacity for NMC, NMS cable:
     <p>&emsp;&emsp;
     -when installed in cable tray (392.80(A) as required by 334.80).
     <p>
     Ampacity for others cables:
     <p>&emsp;&emsp;
     -not covered.

     @return The ampacity in amperes.
     @see #getAdjustmentFactor()
     */
    @Override
    public double getAmpacity() {
        //int adjustedTemp = roofTopDistance <= 0 ? 0 : Factors.getRoofTopTempAdjustment(roofTopDistance);
        return ConductorProperties.getAmpacity(phaseAConductor.getSize(), phaseAConductor.getMetal(), phaseAConductor.getTemperatureRating())
                * getCorrectionFactor() * getAdjustmentFactor();
    }

    @Override
    public boolean hasConduit() {
        return conduit != null;
    }

    @Override
    public Conduit getConduit() {
        return conduit;
    }

    @Override
    public void setConduit(Conduit conduit) {
        if(conduit == null)
            return;
        if(conduit == this.conduit)
            return;
        leaveBundle();
        leaveConduit();
        conduit.add(this);
        this.conduit = conduit;
    }

    @Override
    public void leaveConduit() {
        if(conduit == null)
            return;
        conduit.remove(this);
        conduit = null;
    }

    @Override
    public boolean hasBundle() {
        return bundle != null;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void setBundle(Bundle bundle) {
        if(bundle == null)
            return;
        if(bundle == this.bundle)
            return;
        leaveConduit();
        leaveBundle();
        bundle.add(this);
        this.bundle = bundle;
    }

    @Override
    public void leaveBundle() {
        if(bundle == null)
            return;
        bundle.remove(this);
        bundle = null;
    }

    /**
     Returns the size of the phase(hot) conductors. All phases conductor are the
     same size.

     @return The phase conductors' size.
     */
    public Size getPhaseConductorSize() {
        return phaseAConductor.getSize();
    }

    /**
     Sets the size of the phase conductors. If the system voltage and wires are
     so there is one phase and one neutral, this method will set the size of the
     neutral as well.

     @param size The new size.
     */
    public void setPhaseConductorSize(Size size) {
        if(size == null)
            return;
        phaseAConductor.setSize(size);
        if(phaseBConductor != null)
            phaseBConductor.setSize(size);
        if(phaseCConductor != null)
            phaseCConductor.setSize(size);
        if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph)
            neutralConductor.setSize(size);
    }

    /**
     Returns the size of the neutral conductor if present.

     @return The size of the neutral conductor if present, null otherwise.
     */
    public Size getNeutralConductorSize() {
        if(neutralConductor == null)
            return null;
        return neutralConductor.getSize();
    }

    /**
     Sets the size of the neutral conductor if present. If the system voltage
     and wires are so there is one phase and one neutral, this method will set
     the size of the phase as well.

     @param size The new size.
     */
    public void setNeutralConductorSize(Size size) {
        if(neutralConductor != null) {
            neutralConductor.setSize(size);
            if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph)
                phaseAConductor.setSize(size);
        }
    }

    /**
     Returns the size of the grounding conductor.

     @return The size of the grounding conductor.
     */
    public Size getGroundingConductorSize() {
        return groundingConductor.getSize();
    }

    /**
     Sets the size of the grounding conductor.

     @param size The new size.
     */
    public void setGroundingConductorSize(Size size) {
        groundingConductor.setSize(size);
    }

    /**
     Returns this cable conductor' metal.

     @return This cable conductor' metal. Default metal is copper.
     @see Metal
     */
    public Metal getMetal(){
        return phaseAConductor.getMetal();
    }

    /**
     Sets this cable conductor' metal.

     @param metal The new metal
     @see Metal
     */
    public void setMetal(Metal metal){
        phaseAConductor.setMetal(metal);
        if(phaseBConductor != null)
            phaseBConductor.setMetal(metal);
        if(phaseCConductor != null)
            phaseCConductor.setMetal(metal);
        if(neutralConductor != null)
            neutralConductor.setMetal(metal);
        groundingConductor.setMetal(metal);
    }

    /**
     Returns this cable conductor' insulation.

     @return This cable conductor' insulation. Default insulation is THW.
     @see Insul
     */
    public Insul getInsulation(){
        return phaseAConductor.getInsulation();
    }

    /**
     Sets this cable conductor' insulation.

     @param insul The new insulation
     @see Insul
     */
    public void setInsulation(Insul insul){
        phaseAConductor.setInsulation(insul);
        if(phaseBConductor != null)
            phaseBConductor.setInsulation(insul);
        if(phaseCConductor != null)
            phaseCConductor.setInsulation(insul);
        if(neutralConductor != null)
            neutralConductor.setInsulation(insul);
        groundingConductor.setInsulation(insul);
    }

    /**
     Returns this cable length.

     @return This cable length. Default is 100 FT
     */
    public double getLength(){
        return phaseAConductor.getLength();
    }

    public void setLength(double length){
        phaseAConductor.setLength(length);
        if(phaseBConductor != null)
            phaseBConductor.setLength(length);
        if(phaseCConductor != null)
            phaseCConductor.setLength(length);
        if(neutralConductor != null)
            neutralConductor.setLength(length);
        groundingConductor.setLength(length);
    }

    @Override
    public int getAmbientTemperatureF(){
        return phaseAConductor.getAmbientTemperatureF();
    }

    public void setAmbientTemperatureF(int ambientTemperatureF){
        if(conduit != null)
            conduit.getConduitables().forEach(conduitable -> conduitable.setAmbientTemperatureFSilently(ambientTemperatureF));
        else if(bundle != null)
            bundle.getConduitables().forEach(conduitable -> conduitable.setAmbientTemperatureFSilently(ambientTemperatureF));
        else
            setAmbientTemperatureFSilently(ambientTemperatureF);
    }

    @Override
    public void setAmbientTemperatureFSilently(int ambientTemperatureF) {
        phaseAConductor.setAmbientTemperatureFSilently(ambientTemperatureF);
        if(phaseBConductor != null)
            phaseBConductor.setAmbientTemperatureFSilently(ambientTemperatureF);
        if(phaseCConductor != null)
            phaseCConductor.setAmbientTemperatureFSilently(ambientTemperatureF);
        if(neutralConductor != null)
            neutralConductor.setAmbientTemperatureFSilently(ambientTemperatureF);
        groundingConductor.setAmbientTemperatureFSilently(ambientTemperatureF);
    }

    /**
     Returns this cable copper coating if the metal of the conductors is copper.

     @return This cable copper coating.
     @see Conductor#getCopperCoating()
     */
    public Coating getCopperCoating(){
        return phaseAConductor.getCopperCoating();
    }

    /**
     Sets this cable conductor' copper coating if the conductors are copper,
     otherwise nothing is done.

     @param coating The new copper coating.
     */
    public void setCopperCoating(Coating coating){
        phaseAConductor.setCopperCoated(coating);
        if(phaseBConductor != null)
            phaseBConductor.setCopperCoated(coating);
        if(phaseCConductor != null)
            phaseCConductor.setCopperCoated(coating);
        if(neutralConductor != null)
            neutralConductor.setCopperCoated(coating);
        groundingConductor.setCopperCoated(coating);
    }

    /**
     Returns the temperature rating of this cable.

     @return The temperature rating of this cable as defined in
     {@link TempRating}
     */
    public TempRating getTemperatureRating(){
        return phaseAConductor.getTemperatureRating();
    }

    @Override
    public String getDescription() {
        String s1, s2, s3;
        s1 = cableType + " Cable: (" + getHotCount() + ") " + phaseAConductor.getDescription();
        s2 = "";
        if(neutralConductor != null)
            s2 = " + (1) " + neutralConductor.getDescription();
        s3 = " + (1) " + groundingConductor.getDescription();
        return  s1 + s2 + s3;
    }

    /**
     Sets the rooftop condition for this cable.

     @param roofTopDistance The distance in inches above roof to bottom of this
     cable. If a negative value is indicated, the behavior of this method is the
     same as when calling resetRoofTop, which eliminates the roof top condition
     from this cable.
     */
    public void setRoofTopDistance(double roofTopDistance){
        this.roofTopDistance = roofTopDistance;
    }

    /**
     Resets the rooftop condition for this cable, if the cable is not inside a
     conduit.

     */
    public void resetRoofTop(){
        setRoofTopDistance(-1);
    }

    /**
     Asks if this cable is in a rooftop condition as defined by 310.15(B)(3)(c).
     If a cable is inside a conduit, the rooftop condition of the cable will be
     the rooftop condition of the conduit. This means that, if this
     cable is inside a conduit that is in a rooftop condition, the cable will
     remain in a rooftop condition until the condition is reset in the conduit
     or the cable if pulled from the conduit.
     <p>Setting a rooftop distance of a cable or resetting its rooftop condition
     is only meaningful when the cable is not inside a conduit.

     @return True if this cable has a rooftop condition, false otherwise.
     */
    public boolean isRooftopCondition(){
        if(conduit != null)
            return conduit.isRooftopCondition();
        return (roofTopDistance > 0 && roofTopDistance <= 36) ;
    }

    /**
     Returns the rooftop distance of this cable.

     @return The rooftop distance of this cable.
     */
    public double getRoofTopDistance(){
        return roofTopDistance;
    }

    /**
     Return the cable type as defined in {@link Type}

     @return The type of cable
     */
    public Type getType() {
        return cableType;
    }

    /**
     Sets the type of cable for this cable. This method may change the value of
     the jacketed property as follow: if the cable changes from AC or MC type to
     a different type, the jacketed property is set to false; changing from
     other type of cable to MC or AC type will not change the value of the
     jacketed property.

     @param cableType The new cable type.
     @see Type
     */
    public void setType(Type cableType) {
        if((this.cableType == Type.AC | this.cableType == Type.MC) & cableType != Type.AC & cableType != Type.MC)
            jacketed = false;
        this.cableType = cableType;
    }

    /**
     Returns the voltage system of this cable.

     @return The voltage system of this cable.
     @see eecalcs.systems.SystemAC.Voltage
     */
    public SystemAC.Voltage getVoltage() {
        return voltage;
    }

    /**
     Returns the wire system of this cable.

     @return The wire system of this cable.
     */
    public SystemAC.Wires getWires() {
        return wires;
    }
}
