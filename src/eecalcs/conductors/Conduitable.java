package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import tools.NotifierDelegate;

/**
 A Conduitable object is a conductor or a cable. Any of these objects can be
 installed inside a Conduit object, therefore the name Conduitable. This
 interface is intended to provided a common contract for all the classes that
 implement it.
 */
public interface Conduitable {
    /**
     Returns the cross sectional area of this Conduitable object. This area
     includes the outer insulation and the conductive material. For a
     conductor, it's the area of that conductor including its insulation. For
     a cable, it's the cross sectional area of the assembly, including any
     filling and conductive material inside the outer jacket.

     @return The cross sectional area in square inches.
     */
    double getInsulatedAreaIn2();

    /**
     Returns the number of conductors in this conduitable that are defined as
     current-carrying conductors as per NEC. It does not include any grounding
     conductor and, depending on the voltage system, it might not include the
     neutral conductor either.

     @return The number of current carrying conductors.
     */
    int getCurrentCarryingCount();

    /**
     Returns the ampacity of the conduitable once adjusted and corrected. The
     ampacity is calculated at the conductor or cable level and it accounts for
     the ambient temperature and if installed in rooftop. This method does
     adjustments for the number of current carrying conductors in a conduit.

     @return The adjusted final ampacity of the conduitable accounting for all
     adjustment and correction factors.
     */
    double getAmpacity();

    /**
     Puts this conduitable inside the given conduit. After calling this method,
     the given conduit will contain this conduitable and this conduitable will
     have a reference to that conduit.

     @param conduit The Conduit object that owns this conduitable.
     */
    void setConduit(Conduit conduit);

    /**
     Removes this conduitable from its conduit (if any).
     */
    void leaveConduit();

    /**
     Returns the conduit that contains this conduitable.

     @return A conduit object if this conduitable has one or null if not.
     */
    Conduit getConduit();

    /**
     Asks if this conduitable object is contained inside a conduit object.

     @return True if it's contained in a conduit, false otherwise.
     */
    boolean hasConduit();

    /**
     Puts this conduitable within the given bundle. After calling this method,
     the given bundle will "contain" this conduitable and this conduitable will
     have a reference to that bundle.

     @param bundle The Bundle object that owns this conduitable.
     @see Bundle
     */
    void setBundle(Bundle bundle);

    /**
     Removes this conduitable from its bundle (if any).
     */
    void leaveBundle();

    /**
     Returns the bundle that contains this conduitable.

     @return A Bundle object if this conduitable has one or null if not.
     */
    Bundle getBundle();

    /**
     Asks if this conduitable object is part of a bundle object.

     @return True if it's contained in a bundle, false otherwise.
     */
    boolean hasBundle();

    /**
     Sets the length to this conduitable.
     @param length The length of the conduitable in feet.
     */
    void setLength(double length);

    /**
     Sets the ambient temperature to this conduitable. If this conduitable is
     inside a conduit or is part of a bundle, all the other conduitables in that
     conduit or bundle are set the same ambient temperature.

     @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
     */
    void setAmbientTemperatureF(int ambientTemperatureF);

    /**
     Gets the ambient temperature of this conduitable.

     @return The ambient temperature in degrees Fahrenheits.
     */
    int getAmbientTemperatureF();

    /**
     Sets the ambient temperature of this conduitable. This method does not set
     the ambient temperature of any other conduitable that might be in the same
     conduit or bundle as this conduitable.

     @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
     */
    void setAmbientTemperatureFSilently(int ambientTemperatureF);

    /**
     Returns the description of this conduitable.
     Conductors should return their size-metal-insulation description, like:
     <p>- "#12 AWG THW (CU)"
     <p>Cables should return a description of the form:
     <p>- "MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND)"

     @return The description string as explained above
     */
    String getDescription();

    /**
     Enables or disables the notification broadcast when the conduitable state
     changes.

     @param flag true to enable the broadcasting, false otherwise.
     */
    void notifierEnabled(boolean flag);

    /**
     Sets this conduitable's insulation.
     @param insulation The new insulation
     @see Insul
     */
    void setInsulation(Insul insulation);

    /**
     @return This conduitable's insulation.
     @see Insul
     */
    Insul getInsulation();

    /**
     Returns the temperature correction factor to be applied to the ampacity.

     @return The temperature correction factor.
     */
    double getCorrectionFactor();

    /**
     Returns the adjustment factor for ampacity of this conduitable, as per
     <b>NEC 310.15(B)(3)</b>; it specifically accounts for rules
     <b>310.15(B)(3)(a)(4) {@literal &} (5)</b> and <b>Table 310.15(B)(3)(a).</b>

     @return The adjustment factor.
     */
    double getAdjustmentFactor();

    /**
     @return The product of the correction and adjustment factor of this
     conduitable.
     */
    double getCompoundFactor();

    /**
     @param terminationTempRating The temperature rating of the termination.
     @return The product of the correction and adjustment factor of this
     conduitable when calculated based on the given termination temperature
     rating. If terminationTempRating is zero the returned value is 1.
     */
    double getCompoundFactor(TempRating terminationTempRating);

    /**
     @return The NotifierDelegate for this conduitable
     @see NotifierDelegate
     */
    NotifierDelegate getNotifier();

    /**
     @return The temperature rating of this conduitable per its insulator, as
     defined in {@link TempRating}
     */
    TempRating getTemperatureRating();

    /**
     @return The metal of this conduitable as defined by {@link Metal}
     */
    Metal getMetal();

}
