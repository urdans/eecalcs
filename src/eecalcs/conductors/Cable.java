package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.SystemAC;
import eecalcs.systems.TempRating;
import tools.Listener;
import tools.Speaker;

/**
 * This class encapsulates the properties of a cable.
 * A cable is a fabricated assembly of insulated conductors embedded into a protective jacket.
 * The NEC recognizes the following group of cables as wiring methods for permanent installations:
 * AC - Armored Cable (round)
 * MC - Metal Clad Cable (round)
 * FC - Flat Cable (flat, to be used in a surface metal raceway, not in conduits), not covered by this software.
 * FCC - Flat Conductor Cable (flat, to be used for branch circuits installed under carpet squares, not in conduits), not covered by this software.
 * IGS - Integrated Gas Spacer Cable (round, uses sulfur hexafluoride SF6 as insulator). Not a building wire, not covered by this software.
 * MV - Medium Voltage Cable (round, rated for 2001-35000 volts), not covered by this software.
 * MI - Mineral Insulated Cable (round, for especial conditions, like gasoline, oils, etc.), not covered by this software.
 * NM - Non metallic jacket
 * NMC - Non metallic corrosion resistant jacket.
 * NMS - Non metallic jacket, insulated power or control with signaling data.
 * TC - Power and Control Tray Cable (rounds, for power, lighting, controls and signaling circuits).
 *
 * Each cable type may have a slightly different method of ampacity calculation, so each cable must be created as one of the types covered by this
 * software (AC, MC, NM, NMC, NMS, TC). MC cable is the default type.
 *
 * The cable this class represents is made of:
 * - 1, 2 or 3 phase conductors. These are always considered current carrying conductors (CCC).
 * - 0 or 1 neutral conductor. If the neutral is present, it can be a CCC depending of the system voltage and number of wires.
 * - 1 Equipment grounding conductor.
 *
 * The constructor of this cable takes as parameters the voltage system, the number of wires, a conductor to use as a phase conductor, and the outer
 * diameter. Based on those parameters, the class determines if the neutral is required and if it is, it will create that conductor as a copy
 * of the hot conductor.
 *
 * The grounding conductor is always created as a copy of the hot conductor as well.
 *
 * The size of all these conductors (hot, neutral and grounding) can be later adjusted if required calling the appropriate method.
 *
 * The outer diameter is used for conduit sizing. The number of CCC is used for determining the adjusted ampacity of this and other cables sharing the
 * same conduit.
 *
 * A cable used for a 240/208/120 volts delta system can be created as using any 3 phase voltage and 4 wires.
 *
 * The voltage system is used only to determined the presence of a neutral conductor and if it is initially a CCC
 *
 * As cables can be used as the conductors in a Circuit object, the size of its conductors can be adjusted by the circuit based on the circuit
 * characteristics. Refer to the {@link Circuit} class for more information.
 */
public class Cable implements Conduitable, Listener {
    private Type cableType = Type.MC;
    private boolean jacketed = false;
    //when true, means that this cable is bundled (os stacked) with others, without maintaining space
//    private boolean bundled = false;
    //when true, means that this cable is bundled (os stacked) with 20 or more other cables. It has meaning if bundle is true.
    private boolean bundlingExceeds20 = false;
    //when true, means that this cable is bundled (os stacked) with others cables for a distance greater than 24".
    private boolean bundlingDistanceExceeds24 = false;
    private SystemAC.Voltage voltage;
    private SystemAC.Wires wires;
    private Conductor phaseAConductor;
    private Conductor phaseBConductor;
    private Conductor phaseCConductor;
    private Conductor neutralConductor;
    private Conductor groundingConductor;
    private double outerDiameter;
    private Conduit conduit = null;
    private double roofTopDistance = -1.0; //means no rooftop condition
    private boolean neutralCarryingConductor = false;

    private int getHotCount(){
        return (phaseAConductor == null ? 0 : 1) + (phaseBConductor == null ? 0 : 1) + (phaseCConductor == null ? 0 : 1);
    }

    private int getNeutralCount(){
        return (neutralConductor == null ? 0 : 1);
    }

    /**
     * Defines the type of cables recognized by the NEC that this software handles. These are cables that can be installed in a conduit. Special cables
     * like flat, medium voltage, gas insulated and mineral insulated cables are not handled by the class.
     */
    public static enum Type{
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

        private Type(String name){
            this.name = name;
        }

        /**
         * Returns the string name that this enum represents.
         * @return The string name.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns an array of the string names that the enum values represent
         * @return An array of strings
         */
        public static String[] getNames(){
            return names;
        }
    }

    /**
     * Creates an MC (default) cable object of the given system voltage, number of wires and outer diameter.
     * The conductors of this cable have default sizes, metal and insulation. Refer to {@link Conductor} default constructor for details.
     * @param voltage The voltage system intended to be used with this cable. It will define the existence of a neutral.
     * @param wires The number of wires of the prior voltage system parameter.
     * @param outerDiameter The outer diameter of this cable in inches. If the cable sectional shape if not a circle, the longest diameter must be passed in.
     *                     Also, if this parameter of less than 0.25 inches, a value of 0.25 inches is assumed.
     */
    public Cable(SystemAC.Voltage voltage, SystemAC.Wires wires, double outerDiameter){
        setSystem(voltage, wires);
        phaseAConductor = new Conductor();
        phaseAConductor.setRole(Conductor.Role.HOT);
        groundingConductor = new Conductor();
        groundingConductor.setRole(Conductor.Role.GND);

        /*if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph) {
            //the number of wires is always 2 for this type of system, hence wires is ignored
            neutralConductor = new Conductor();
            neutralConductor.setRole(Conductor.Role.NEUCC);
        }
        else if(voltage == SystemAC.Voltage.v208_1ph | voltage == SystemAC.Voltage.v240_1ph | voltage == SystemAC.Voltage.v480_1ph){
            phaseBConductor = new Conductor();
            phaseBConductor.setRole(Conductor.Role.HOT);
            if(wires != SystemAC.Wires.W2){//this would account for malformed systems (1-phase, 4w) treating them as 3w
                neutralConductor = new Conductor();
                neutralConductor.setRole(Conductor.Role.NEUCC);
            }
        }
        else {//this account for all 3-phase systems.
            phaseBConductor = new Conductor();
            phaseBConductor.setRole(Conductor.Role.HOT);
            phaseCConductor = new Conductor();
            phaseCConductor.setRole(Conductor.Role.HOT);
            //Any 3-phase system indicated as 2w or 3w will not have a neutral
            if(wires == SystemAC.Wires.W4){
                neutralConductor = new Conductor();
                neutralConductor.setRole(Conductor.Role.NEUNCC);
            }
        }*/
        setOuterDiameter(outerDiameter);
    }

    /**
     * Asks if this cable has an outer jacket. Most cables have. MC & AC cable are commonly non jacketed but versions with an outer jacket are available
     * in the market. This value is false by default and remains unchanged, unless is set by calling the {@link this.setJacketed()} method.
     * @return True if the cable has an outer jacket, false otherwise.
     */
    public boolean isJacketed() {
        return jacketed;
    }

    /**
     * Asks if the condition where this cable is bundled with other 20 cables or more is set.
     * @return True if the condition is set, false otherwise.
     */
    public boolean isBundlingExceeds20() {
        return bundlingExceeds20;
    }

    /**
     * Sets the condition where this cable is bundled with other 20 cables or more
     * @param bundlingExceeds20 True to set this condition.
     */
    public void setBundlingExceeds20(boolean bundlingExceeds20) {
        this.bundlingExceeds20 = bundlingExceeds20;
    }

    /**
     * Ask if this cable is under the condition of bundling with other cables for more than 24"
     * @return True if the condition is set, false otherwise.
     */
    public boolean isBundlingDistanceExceeds24() {
        return bundlingDistanceExceeds24;
    }

    /**
     * Sets the condition where this cable bundling with other cable exceeds 24"
     * @param bundlingDistanceExceeds24 True to set this condition, false otherwise.
     */
    public void setBundlingDistanceExceeds24(boolean bundlingDistanceExceeds24) {
        this.bundlingDistanceExceeds24 = bundlingDistanceExceeds24;
    }

    /**
     * Returns the adjustment factor for ampacity of this cable, accounting for rules 310.15(B)(3)(a)(4) & (5) and table 310.15(B)(3)(a)
     * @return The adjustment factor.
     */
    public double getAdjustmentFactor() {
        double adjustmentFactor = Factors.getAdjustmentFactor(conduit);
        //NEC-310.15(B)(3)(a)(4)
        if(cableType == Type.MC | cableType == Type.AC){ //310.15(B)(3)(a)(4) & (5)
            if(!jacketed //310.15(B)(3)(a)(4)-a.
                    && getCurrentCarryingCount() <= 3 //310.15(B)(3)(a)(4)-b. All the others bundled cables must meet this condition as well.
                    //todo do I need to create a class BundledCables? uhmm....
                    // also I could just indicate these conditions as external condition (not using a class), just flags in the UI.
                    && phaseAConductor.getSize() == Size.AWG_12 && phaseAConductor.getMetal() == Metal.COPPER //310.15(B)(3)(a)(4)-c.
                    && !hasConduit() && !bundlingExceeds20) //310.15(B)(3)(a)(4)-d. Of course, no conduit.
                adjustmentFactor = 1;
            else if(!jacketed //310.15(B)(3)(a)(5)-a.
                    && bundlingExceeds20 //310.15(B)(3)(a)(5)-b.
                    && bundlingDistanceExceeds24) //310.15(B)(3)(a)(5)-c.
                adjustmentFactor = 0.6;
        }
        return adjustmentFactor;
    }

    /**
     * Sets the outer jacket condition for this cable as indicated in the given parameter.
     * @param jacketed The condition for this cable. True: sets this cable as having an outer jacket, False: the opposite case.
     */
    public void setJacketed(boolean jacketed) {
        this.jacketed = jacketed;
    }

    /**
     * Asks if this cable is jacketed.
     * @return True if jacketed, false otherwise.
     */
    public boolean getJacketed() {
        return this.jacketed;
    }

    /**
     * Sets the outer diameter of this cable.
     * @param outerDiameter The outer diameter in inches.
     */
    public void setOuterDiameter(double outerDiameter) {
        if(outerDiameter < 0.25)
            outerDiameter = 0.25;
        this.outerDiameter = outerDiameter;
    }

    /**
     * Returns the outer diameter of the cable.
     * @return The outer diameter of the cable in inches.
     */
    public double getOuterDiameter() {
        return outerDiameter;
    }

    /**
     * Sets the neutral conductor of this cable (if present) as a current carrying conductor for 3-phase 4-wire systems
     * @param neutralCarryingConductor True if the neutral is a current carrying conductor, false otherwise.
     */
    public void setNeutralCarryingConductor(boolean neutralCarryingConductor){
        this.neutralCarryingConductor = neutralCarryingConductor;
        if(neutralConductor != null) {
            if(neutralCarryingConductor)
                neutralConductor.setRole(Conductor.Role.NEUCC);
            else
                neutralConductor.setRole(Conductor.Role.NEUNCC);
        }
    }

    /**
     * Asks if the neutral of this cable (if present) is a current carrying conductor.
     * @return True if it's a current carrying conductor, false otherwise.
     */
    public boolean isNeutralCarryingConductor() {
        return neutralCarryingConductor;
    }

    /**
     * Sets the voltage system for this cable
     * @param voltage The voltage as defined in {@link SystemAC.Voltage}
     * @param wires The wiring system as defined in {@link SystemAC.Wires}
     */
    public void setSystem(SystemAC.Voltage voltage, SystemAC.Wires wires){
        this.voltage = voltage;
        this.wires = wires;

        if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph) {
            //the number of wires is always 2 for this type of system, hence wires is ignored
            if(neutralConductor == null)
                neutralConductor = new Conductor();
            neutralConductor.setRole(Conductor.Role.NEUCC);
            phaseBConductor = null;
            phaseCConductor = null;
        }
        else if(voltage == SystemAC.Voltage.v208_1ph | voltage == SystemAC.Voltage.v240_1ph | voltage == SystemAC.Voltage.v480_1ph){
            if(phaseBConductor == null)
                phaseBConductor = new Conductor();
            phaseBConductor.setRole(Conductor.Role.HOT);
            if(wires == SystemAC.Wires.W2){
                neutralConductor = null;
            }
            else {//Any 2-hot system of this voltage, indicated as 3w or 4w will have a neutral
                if(neutralConductor == null)
                    neutralConductor = new Conductor();
                neutralConductor.setRole(Conductor.Role.NEUCC);
            }
            phaseCConductor = null;
        }
        else {//this account for all 3-phase systems.
            if(phaseBConductor == null)
                phaseBConductor = new Conductor();
            phaseBConductor.setRole(Conductor.Role.HOT);
            if(phaseCConductor == null)
                phaseCConductor = new Conductor();
            phaseCConductor.setRole(Conductor.Role.HOT);
            if(wires == SystemAC.Wires.W4){
                neutralConductor = new Conductor();
                if(neutralCarryingConductor)
                    neutralConductor.setRole(Conductor.Role.NEUCC);
                else
                    neutralConductor.setRole(Conductor.Role.NEUNCC);
            }
            else //Any 3-phase system indicated as 2w or 3w will not have a neutral
                neutralConductor = null;
        }
    }

    /**
     * Returns the total cross sectional area of the cable.
     * @return The are of the cable in square inches.
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

    /*todo implement this*/
    public double getFactors(){
        int adjustedTemp = roofTopDistance <= 0 ? 0 : Factors.getRoofTopTempAdjustment(roofTopDistance);
        return Factors.getTemperatureCorrectionF(phaseAConductor.getAmbientTemperatureF() + adjustedTemp,
                phaseAConductor.getTemperatureRating()) * getAdjustmentFactor();
    }

    /**
     * Returns the ampacity of this cable (for voltages up to 2000v). The result accounts for the ambient temperature, the insulation of
     * the hot conductors, and the number of other conductors that share the same raceway with this cable. That is, the ampacity returned is
     * corrected for ambient temperature (other than 86°F/30°C), and adjusted for the number of conductors (including the cable ones) in the same
     * raceway. This is the ampacity for this cable size, metal and insulation type under the specified conditions (ambient temperature, bundling, rooftop,
     * etc.) condition .
     * The rule allowing the temperature correction and adjustment factors to be applied to the ampacity for the temperature rating of the
     * conductor, if the corrected and adjusted ampacity does not exceed the ampacity for the temperature rating of the terminals in accordance with
     * 110.14(C), is not accounted for in this method. It is accounted for at the {@link Circuit} class level.
     * For example, NM, NMC and NMS cables, their ampacity can be calculated, corrected and adjusted for 75°C or 90°C but that ampacity shall not exceed what
     * would be required for a 60 deg. This rule appears several times throughout the code.
     * A concrete example is as follow:
     * Suppose a load was calculated at 105 AMPS. The installer decides to use a THHW conductors which is rated for 90°C. Let's assume that there are 4
     * current-carrying conductors in the raceway and that the ambient temperature is 100°C:
     * -Temperature correction factor for a 90°C conductor (TABLE 310.15(B)(2)(a)) = 0.91
     * -Adjustment factor for four current-carrying conductors (TABLE 310.15(B)(3)(a)) = 0.8
     * -Ampacity of a # 1 AWG THHW = 145 AMPS
     * -Allowed ampacity under specified conditions = 145*0.91*0.8 = 105.56 AMPS
     * The # 1 AWG THHW wire is good because the ampacity for the same wire at 60°C is 110AMP.
     * The general approach to determine the allowed ampacity is:
     *          AllowedAmpacity*TCF*AF >= Load Amps
     *          AllowedAmpacity >= (Load Amps)/(TCF*AF)
     *          AllowedAmpacity >= (105)/(0.91*0.8)
     *          AllowedAmpacity >= 144.23 AMPS.
     *          Now, a conductor can be selected from table 310.15(B)(16):
     *          It could be a #2/0 AWG TW, or a #1/0 AWG THW or a #1 AWG THHW.
     * This method alone does not calculate the allowed ampacity because the load amps is not known at this level.
     * However, the method {@link this.getFactors()} will provide the (0.91*0.8) value (from the example) that the {@link Circuit} class would need as
     * reversed coefficient to multiply the load amperes (to get the 144.23 AMPS from the example). Then the method
     * {@link ConductorProperties#getAllowedSize(double, Metal, TempRating)} can provide the proper size of the conductor.
     *
     * Adjustment factor exceptions apply to AC and MC cable under the conditions explained in 310.15(B)(3)(a)(4).
     * What this method DOES NOT cover:
     * Ampacity for MC cable:
     *      -for voltages higher than 2000v and sizes 14 AWG and up (table 310.60).
     *      -for sizes 18 AWG and 16 AWG (table 402.5). This software does not cover conductor/cables smaller than 14 AWG.
     *      -when installed in cable tray (392.80)
     * Ampacity for AC cable:
     *      -when installed in thermal insulation ( 320.80(A) ).
     *      -when installed in cable tray (392.80(A) as required by 320.80(B)).
     * Ampacity for NM cable:
     *      -when installed in cable tray (392.80(A) as required by 334.80).
     *      -when installed through wood framing or in contact with thermal insulation as explained in 334.80.
     * Ampacity for NMC, NMS cable:
     *      -when installed in cable tray (392.80(A) as required by 334.80).
     * Ampacity for others cables:
     *      -not covered.
     * @return The ampacity in amperes.
     * @see this#getAdjustmentFactor()
     */
    @Override
    public double getAmpacity() {
        //int adjustedTemp = roofTopDistance <= 0 ? 0 : Factors.getRoofTopTempAdjustment(roofTopDistance);
        return ConductorProperties.getAmpacity(phaseAConductor.getSize(), phaseAConductor.getMetal(), phaseAConductor.getTemperatureRating())
                * getFactors();
                /*Factors.getTemperatureCorrectionF(phaseAConductor.getAmbientTemperatureF() + adjustedTemp, phaseAConductor.getTemperatureRating()) * getAdjustmentFactor();*/
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
        if(conduit.hasConduitable(this))
            this.conduit = conduit;
    }

    @Override
    public void leaveConduit() {
        if(this.conduit == null)
            return;
        if(!this.conduit.hasConduitable(this))
            this.conduit = null;
    }

    /**
     * Returns the size of the phase(hot) conductor, which represents the conductors for phases B and C.
     * @return The phase conductors' size.
     */
    public Size getPhaseConductorSize() {
        return phaseAConductor.getSize();
    }

    /**
     * Sets the size of the phase conductors. If the system voltage and wires are so there is one phase and one neutral, this method will set the size
     * of the neutral as well.
     * @param size The new size.
     */
    public void setPhaseConductorSize(Size size) {
        phaseAConductor.setSize(size);
        if(phaseBConductor != null)
            phaseBConductor.setSize(size);
        if(phaseCConductor != null)
            phaseCConductor.setSize(size);
        if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph)
            neutralConductor.setSize(size);
    }

    /**
         * Returns the size of the neutral conductor if present.
         * @return The size of the neutral conductor if present, null otherwise.
         */
    public Size getNeutralConductorSize() {
        if(neutralConductor == null)
            return null;
        return neutralConductor.getSize();
    }

    /**
     * Sets the size of the neutral conductor if present. If the system voltage and wires are so there is one phase and one neutral, this method will
     * set the size of the phase as well.
     * @param size The new size.
     */
    public void setNeutralConductorSize(Size size) {
        if(neutralConductor != null) {
            neutralConductor.setSize(size);
            if(voltage == SystemAC.Voltage.v120_1ph | voltage == SystemAC.Voltage.v277_1ph)
                phaseAConductor.setSize(size);
        }
    }

    /**
     * Returns the size of the grounding conductor.
     * @return The size of the grounding conductor.
     */
    public Size getGroundingConductorSize() {
        if(neutralConductor == null)
            return null;
        return neutralConductor.getSize();
    }

    /**
     * Sets the size of the grounding conductor.
     * @param size The new size.
     */
    public void setGroundingConductorSize(Size size) {
        groundingConductor.setSize(size);
    }

    /**
     * Returns this cable conductor' metal.
     * @return This cable conductor' metal. Default metal is copper.
     * @see Metal
     */
    public Metal getMetal(){
        return phaseAConductor.getMetal();
    }

    /**
     * Sets this cable conductor' metal.
     * @param metal The new metal
     * @see Metal
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
     * Returns this cable conductor' insulation.
     * @return This cable conductor' insulation. Default insulation is THW.
     * @see Insul
     */
    public Insul getInsulation(){
        return phaseAConductor.getInsulation();
    }

    /**
     * Sets this cable conductor' insulation.
     * @param insul The new insulation
     * @see Insul
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
     * Returns this cable length.
     * @return This cable length. Default is 100 FT
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
     * Returns this cable copper coating if the metal of the conductors is copper.
     * @return This cable copper coating.
     * @see Conductor#getCopperCoating()
     */
    public Coating getCopperCoating(){
        return phaseAConductor.getCopperCoating();
    }

    /**
     * Sets this cable conductor' copper coating if the conductors are copper; otherwise nothing is done.
     * @param coating The new copper coating.
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
     * Returns the temperature rating of this cable.
     * @return The temperature rating of this cable as defined in {@link TempRating}
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

    public void setRoofTopDistance(double roofTopDistance){
        this.roofTopDistance = roofTopDistance;
    }

    public void resetRoofTop(){
        setRoofTopDistance(-1);
    }

    /**
     * Return the cable type as defined in {@link Type}
     * @return The type of cable
     */
    public Type getType() {
        return cableType;
    }

    /**
     * Sets the type of cable for this cable.
     * @param cableType
     * @see Type
     */
    public void setType(Type cableType) {
        this.cableType = cableType;
    }

    //todo quede aqui
    /* todo implement rooftop features to the conduit class as well*/
}
