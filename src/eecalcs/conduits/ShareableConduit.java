package eecalcs.conduits;

import eecalcs.conductors.Conduitable;

/**
 This interface is intended to be used by the class {@link Conduit} in order
 to hide some public methods in the <i>Conduit</i> class; specifically, the
 following methods are hidden:<br><br>

 <p><code>List<Conduitable> getConduitables()</code>
 <p><code>void add(Conduitable conduitable)</code>
 <p><code>void remove(Conduitable conduitable)</code>
 <p><code>void empty()</code>
 <p><code>Trade getTradeSize()()</code>
 */
public interface ShareableConduit {
    /** Refer to {@link Conduit#isEmpty()} */
    boolean isEmpty();

    /** Refer to {@link Conduit#hasConduitable(Conduitable)} ()} */
    boolean hasConduitable(Conduitable conduitable);

    /** Refer to {@link Conduit#getMinimumTrade()} */
    Trade getMinimumTrade();

    /** Refer to {@link Conduit#setMinimumTrade(Trade)} ()} */
    void setMinimumTrade(Trade minimumTrade);

    /** Refer to {@link Conduit#getFillingConductorCount()} */
    int getFillingConductorCount();

    /** Refer to {@link Conduit#getCurrentCarryingNumber()} */
    int getCurrentCarryingNumber();

    /** Refer to {@link Conduit#getConduitablesArea()} */
    double getConduitablesArea();

    /** Refer to {@link Conduit#getAllowedFillPercentage()} */
    int getAllowedFillPercentage();

    /** Refer to {@link Conduit#getType()} */
    Type getType();

    /** Refer to {@link Conduit#setType(Type)} ()} */
    void setType(Type type);

    /** Refer to {@link Conduit#isNipple()} */
    boolean isNipple();

    /** Refer to {@link Conduit#setNipple(Conduit.Nipple)} ()} */
    void setNipple(Conduit.Nipple nipple);

    /** Refer to {@link Conduit#setRoofTopDistance(double)} ()} */
    void setRoofTopDistance(double roofTopDistance);

    /** Refer to {@link Conduit#resetRoofTop()} */
    void resetRoofTop();

    /** Refer to {@link Conduit#isRooftopCondition()} */
    boolean isRooftopCondition();

    /** Refer to {@link Conduit#getRoofTopDistance()} */
    double getRoofTopDistance();
}
