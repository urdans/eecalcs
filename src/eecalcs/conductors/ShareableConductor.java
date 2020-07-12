package eecalcs.conductors;

import eecalcs.systems.TempRating;

/**
 This interface is intended to be used by the class {@link Conductor} in order
 to hide some of its public methods.
 */
public interface ShareableConductor {
    /*/* Refer to {@link Conductor#clone()} */
    //Conductor clone();

    /** Refer to {@link Conductor#getSize()} */
    Size getSize();

    /** Refer to {@link Conductor#setSize(Size)} */
    Conductor setSize(Size size);

    /*/* Refer to {@link Conductor#getMetal()} */
    //Metal getMetal();

    /** Refer to {@link Conductor#setMetal(Metal)} ()} */
    Conductor setMetal(Metal metal);

    /*/* Refer to {@link Conductor#getInsulation()} */
    Insul getInsulation();

    /** Refer to {@link Conductor#setInsulation(Insul)} ()} */
    /*Conductor*/ void setInsulation(Insul insulation);

    /*/* Refer to {@link Conductor#getLength()} */
    //double getLength();

    /** Refer to {@link Conductor#setLength(double)} ()} */
    void setLength(double length);

    /* Refer to {@link Conductor#getInsulatedAreaIn2()} */
    //double getInsulatedAreaIn2();

    /** Refer to {@link Conductor#getAmpacity()} */
    double getAmpacity();

    /** Refer to {@link Conductor#getCorrectionFactor()} */
    double getCorrectionFactor();

    /** Refer to {@link Conductor#getAdjustmentFactor()} */
    double getAdjustmentFactor();

    /* Refer to {@link Conductor#getAmbientTemperatureF()} */
    //int getAmbientTemperatureF();

    /** Refer to {@link Conduitable#getTemperatureRating()}*/
    TempRating getTemperatureRating();

    /** Refer to {@link Conduitable#getMetal()}*/
    Metal getMetal();

    /** Refer to {@link Conductor#setAmbientTemperatureF(int)} ()} */
    void setAmbientTemperatureF(int ambientTemperatureF);

    /** Refer to {@link Conduitable#getCompoundFactor()} */
    double getCompoundFactor();

    /** Refer to {@link Conduitable#getAmbientTemperatureF()} */
    int getAmbientTemperatureF();

    /* Refer to {@link Conductor#setAmbientTemperatureFSilently(int)} ()} */
    //void setAmbientTemperatureFSilently(int ambientTemperatureF);

    /* Refer to {@link Conductor#isCopperCoated()} */
    //boolean isCopperCoated();

    /* Refer to {@link Conductor#setCopperCoated(Coating)} ()} */
    //Conductor setCopperCoated(Coating copperCoated);

    /* Refer to {@link Conductor#getCopperCoating()} */
    //Coating getCopperCoating();

    /* Refer to {@link Conductor#hasConduit()} */
    //boolean hasConduit();

    /* Refer to {@link Conductor#getTemperatureRating()} */
    //TempRating getTemperatureRating();

    /* Refer to {@link Conductor#getRole()} */
    //Conductor.Role getRole();

    /* Refer to {@link Conductor#setRole(Conductor.Role)} ()} */
    //Conductor setRole(Conductor.Role role);

    /* Refer to {@link Conductor#getCurrentCarryingCount()} */
    //int getCurrentCarryingCount();

    /* Refer to {@link Conductor#getDescription()} */
    //String getDescription();

    /* Refer to {@link Conductor#hasBundle()} */
    //boolean hasBundle();
}
