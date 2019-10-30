package eecalcs.conductors;

import eecalcs.conduits.Conduit;

/**
 * A Conduitable object is a conductor or a circuit (made out of a sets of conductors) or a cable. Any of these objects can be installed inside a Conduit
 * object, therefore the name Conduitable. This interface is intended to provided a common contract fro all the classes that implement it.
 */
public interface Conduitable {
    /**
     * Returns the cross sectional area of the Conduitable object. This area includes the outer insulation and the conductive material. For a conductor,
     * it's the area of that conductor, again, including its insulation. For a circuit, it's the sum of all the cross sectional areas of all its conductors
     * (or its unique cable). For a cable, it's the cross sectional area of the assembly, including the any filling and conductive material inside the outer
     * jacket.
     * @return The cross sectional area in square inches.
     */
    double getInsulatedAreaIn2();

    /**
     * Returns the number of conductors that are defined per NEC as current carrying. It does not include any grounding conductor and in some scenarios
     * it does not include the neutral either. For one conductor, the returned value is always 1.
     * @return The number of current carrying conductors.
     */
    int getCurrentCarryingCount();

    /**
     * Returns the number of conductors. This includes all types, hots, neutral and ground. For a conductor and for a cable this value is always 1. For a
     * circuit, it's the number of conductors that will be installed in a conduit. Each parallel conductors for the same phase, neutral or ground in the
     * same raceway shall count as one for the purpose of ampacity adjustment. For example, a circuit can have 3 sets of 4 conductors in parallel
     * for a total of 15 conductors (3x4+3 grounds = 15); if these sets are installed in separate conduits, the returned number of conductors
     * is 5 but is they are all installed in the same raceway the returned value is 15.
     * Notice that installing all the sets of conductor in one big conduit is allowed but the ampacity of all conductors must be adjusted by counting all
     * current carrying conductors in the raceway, even the ones in parallel. For this example, 12 conductors will count a current carrying and an
     * adjustment. The total number of conductor however, will count for raceway calculation. So, if the 15 conductor of the prior example are installed
     * in one conduit, this method should return 15 and not 5.
     * @return The number of conductors of this conduitable.
     */
    default int getConductorCount(){
        return 1; //valid for a cable and a conductor.
    }

    /**
     * Returns the ampacity of the conduitable once adjusted and corrected. The ampacity is calculated at the conductor or cable level and it accounts
     * for the ambient temperature and if installed in rooftop (for cables not in a conduit); however, the ampacity returned by this conduitable's method
     * does adjustments for the number of current carrying conductors in a conduit.
     * @return The adjusted final ampacity of the conduitable accounting for all adjustment and correction factors.
     */
    double getAmpacity();

    /**
     * Sets this conduitable's conduit reference to the conduit object that registered this conduitable.
     * Never call this method directly. It is called from a conduit object when this conduitable is being added to that conduit.
     * Particularly, if the given conduit parameter doesn't already contain this conduitable in advance, nothing happen, and it's an
     * indication that this method was called directly (from outside a conduit object).
     * @param conduit The Conduit object that owns this conduitable.
     */
    void setConduit(Conduit conduit);

    /**
     * Never call this method directly. It is called by a conduit object when unregistering this conduitable.
     * This methods sets this conduitable's conduit reference to null only if the referenced conduit does not contain this conduitable.
     */
    void leaveConduit();

    /**
     * Returns the conduit that contains this conduitable.
     * @return A conduit object if this conduitable has one or null if not.
     */
    Conduit getConduit();

    /**
     * Asks if this conduitable object is contained inside a conduit object.
     * @return True if it's contained in a conduit, false otherwise.
     */
    boolean hasConduit();

    /**
     * Sets the length to this conduitable.
     * @param length The length of the conduitable in feet.
     */
    void setLength(double length);

    /**
     * Sets the ambient temperature to this conduitable. If this conduitable is inside a conduit, all the other conduitables inside that conduit are set
     * the same ambient temperature.
     * @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
     */
    void setAmbientTemperatureF(int ambientTemperatureF);

    /**
     * Gets the ambient temperature of this conduitable.
     * @return The ambient temperature in degrees Fahrenheits.
     */
    public int getAmbientTemperatureF();

    /**
     * Sets the ambient temperature to this conduitable. This method does not set the ambient temperature of any other conduitable that might be in the
     * same conduit as this conduitable.
     * @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
     */
    void setAmbientTemperatureFSilently(int ambientTemperatureF);

    /**
     * Returns the description of this conduitable.
     * Conductors should return their size-metal-insulation description, like:
     *      - "#12 AWG THW (CU)"
     * Cables should return a description of the form:
     *      - "MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND)"
     * Circuits should return a string composed of several lines (separated by returns and line feed), of the form:
     *      - First line, circuit description:  "POOL HEATER"
     *        Second line, configuration:       "(3) #8 AWG THHN (AL) + #10 AWG THHN (CU)(NEU) + #12 AWG THHN (CU)(GND) IN 2" EMT CONDUIT" or
     *                                          "(3) SETS OF (4) 250 KCMIL THHW (CU) + #1/0 AWG THHW (CU)(GND) IN 4" EMT CONDUIT" or
     *                                          "MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND) IN FREE AIR or 2" EMT CONDUIT or IN CABLE TRAY"
     *        Third line, circuit ratings:      "208 VOLTS 3ⱷ 3W 125 AMPS DPH-24,26,28"
     * @return The description string as explained above
     */
    String getDescription();

    /**
     * Sets the rooftop condition for this conduitable.
     * @param roofTopDistance The distance in inches above roof to bottom of raceway or cable. If a negative value is indicated, the behavior of this
     *                        method is the same as when calling resetRoofTop, which eliminates the roof top condition for this conduitable.
     */
    void setRoofTopDistance(double roofTopDistance);

    /**
     * Resets the rooftop condition for this conduitable, that is, no roof top condition.
     */
    void resetRoofTop();


/* todo quede aqui
run test of ampacity of cable and conductors when sharing same conduit and after removing/adding more from/to conduit
    done for cables
    quede aqui: do it for conductors

add cases to cable (depending on cable type, how ampacity is calculated). Refer to my notes about what I read on the NEC.
add cases to conduit (depending on type, how sizes are calculated, min sizes, restriction, etc, read the code)

implement circuit, load, OCDP, Panel
circuit is a conduitable, but a circuit might be composed of conductors or cables, not both.
Access to this cable or conductors is restricted from outside.
The size of the conductor is calculated based on conditions, locations, temperature, ccc, metal, etc.
number of individual conductors or the number of conductors in the cable must match the system voltage and wiring

Another way of implementing this is:
    Circuit uses a cable or conductors, not both.
    what is conduitable is
*/
}
