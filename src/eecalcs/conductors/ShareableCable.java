package eecalcs.conductors;

import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;

/**
 This interface is intended to be implemented by the class {@link Cable} in order
 to hide some public methods in the <i>Cable</i> class; specifically, the
 following methods are hidden:<br><br>

 <p><code>void setConduit(Conduit conduit)</code>
 <p><code>void leaveConduit()</code>
 <p><code>Conduit getConduit()</code>
 <p><code>void setBundle(Bundle bundle)</code>
 <p><code>void leaveBundle()</code>
 <p><code>Bundle getBundle()</code>
 */
public interface ShareableCable {
    /** Refer to {@link Cable#clone()} */
    Cable clone();

    /** Refer to {@link Cable#getAdjustmentFactor()} */
    double getAdjustmentFactor();

    /** Refer to {@link Cable#getAmbientTemperatureF()} */
    int getAmbientTemperatureF();

    /** Refer to {@link Cable#getAmpacity()} */
    double getAmpacity();

    /** Refer to {@link Cable#getCopperCoating()} */
    Coating getCopperCoating();

    /** Refer to {@link Cable#getCorrectionFactor()} */
    double getCorrectionFactor();

    /** Refer to {@link Cable#getCurrentCarryingCount()} */
    int getCurrentCarryingCount();

    /** Refer to {@link Cable#getDescription()} */
    String getDescription();

    /** Refer to {@link Cable#getGroundingConductorClone()} */
    Conductor getGroundingConductorClone();

    /** Refer to {@link Cable#getGroundingConductorSize()} */
    Size getGroundingConductorSize();

    /** Refer to {@link Cable#getInsulatedAreaIn2()} */
    double getInsulatedAreaIn2();

    /** Refer to {@link Cable#getInsulation()} */
    Insul getInsulation();

    /** Refer to {@link Cable#getJacketed()} */
    boolean getJacketed();

    /** Refer to {@link Cable#getLength()} */
    double getLength();

    /** Refer to {@link Cable#getMetal()} */
    Metal getMetal();

    /** Refer to {@link Cable#getNeutralConductorClone()} */
    Conductor getNeutralConductorClone();

    /** Refer to {@link Cable#getNeutralConductorSize()} */
    Size getNeutralConductorSize();

    /** Refer to {@link Cable#getOuterDiameter()} */
    double getOuterDiameter();

    /** Refer to {@link Cable#getPhaseConductorClone()} */
    Conductor getPhaseConductorClone();

    /** Refer to {@link Cable#getPhaseConductorSize()} */
    Size getPhaseConductorSize();

    /** Refer to {@link Cable#getRoofTopDistance()} */
    double getRoofTopDistance();

    /** Refer to {@link Cable#getTemperatureRating()} */
    TempRating getTemperatureRating();

    /** Refer to {@link Cable#getType()} */
    CableType getType();

    /** Refer to {@link Cable#getVoltageSystemAC()} */
    VoltageSystemAC getVoltageSystemAC();

    /** Refer to {@link Cable#hasBundle()} */
    boolean hasBundle();

    /** Refer to {@link Cable#hasConduit()} */
    boolean hasConduit();

    /** Refer to {@link Cable#isJacketed()} */
    boolean isJacketed();

    /** Refer to {@link Cable#isNeutralCarryingConductor()} */
    boolean isNeutralCarryingConductor();

    /** Refer to {@link Cable#isRooftopCondition()} */
    boolean isRooftopCondition();

    /** Refer to {@link Cable#notifierEnabled(boolean)} } */
    void notifierEnabled(boolean flag);

    /** Refer to {@link Cable#resetRoofTop()} */
    void resetRoofTop();

    /** Refer to {@link Cable#setAmbientTemperatureF(int)} */
    void setAmbientTemperatureF(int ambientTemperatureF);

    /** Refer to {@link Cable#setAmbientTemperatureFSilently(int)} */
    void setAmbientTemperatureFSilently(int ambientTemperatureF);

    /** Refer to {@link Cable#setCopperCoating(Coating)} */
    void setCopperCoating(Coating coating);

    /** Refer to {@link Cable#setGroundingConductorSize(Size)} */
    void setGroundingConductorSize(Size size);

    /** Refer to {@link Cable#setInsulation(Insul)} */
    void setInsulation(Insul insul);

    /** Refer to {@link Cable#setJacketed(boolean)} */
    void setJacketed(boolean jacketed);

    /** Refer to {@link Cable#setLength(double)} */
    void setLength(double length);

    /** Refer to {@link Cable#setMetal(Metal)} */
    void setMetal(Metal metal);

    /** Refer to {@link Cable#setNeutralCarryingConductor(boolean)} */
    void setNeutralCarryingConductor(boolean flag);

    /** Refer to {@link Cable#setNeutralConductorSize(Size)} */
    void setNeutralConductorSize(Size size);

    /** Refer to {@link Cable#setOuterDiameter(double)} */
    void setOuterDiameter(double outerDiameter);

    /** Refer to {@link Cable#setPhaseConductorSize(Size)} */
    void setPhaseConductorSize(Size size);

    /** Refer to {@link Cable#setRoofTopDistance(double)} */
    void setRoofTopDistance(double distance);

    /** Refer to {@link Cable#setSystem(VoltageSystemAC)} */
    void setSystem(VoltageSystemAC voltage);

    /** Refer to {@link Cable#setType(CableType)} */
    void setType(CableType cableType);

    /** Refer to {@link Cable#toString()} */
    String toString();


}
