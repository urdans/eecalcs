package eecalcs.conduits;

import eecalcs.conductors.Conduitable;

/**
 This interface is intended to be used by the class {@link Conduit} in order
 to hide some public methods in the <i>Conduit</i> class; specifically, the
 following methods are hidden:<br><br>

 <p><code>List&#60;Conduitable&#62; getConduitables()</code>
 <p><code>void add(Conduitable conduitable)</code>
 <p><code>void remove(Conduitable conduitable)</code>
 <p><code>void empty()</code>
 <p><code>Trade getTradeSize()</code>
 */
public interface ShareableConduit {
    /**
     * Asks if this conduit is empty (contains no conduitable)

     @return True if empty.
     */
    boolean isEmpty();

    /**
     * Asks if this conduit already contains the given conduitable.

     @param conduitable The conduitable to check if it is already contained by
     this conduit.
     @return True if this conduit contains it, false otherwise.
     */
    boolean hasConduitable(Conduitable conduitable);

    /**
     * Returns the minimum allowable trade size for this conduit.

     @return The minimum allowable trade size for this conduit.
     */
    Trade getMinimumTrade();

    /**
     * Sets the minimum trade size this conduit can reach.

     @param minimumTrade The trade size to be set as minimum for this conduit.
     @see Trade
     */
    void setMinimumTrade(Trade minimumTrade);

    /**
     * Returns the number of conductor that fills this conduit as set forth in
     NEC chapter 9, Table 1. Cables always count as one conductor.
     The returned number is used to compute the percentage of
     filling area in this conduit.

     @return The number of conductors.
     */
    int getFillingConductorCount();

    /**
     * Returns the number of current-carrying conductors inside this conduit.

     @return The number of current-carrying conductors inside this conduit.
     */
    int getCurrentCarryingNumber();

    /**
     * Returns the total area of the conductors filling this conduit, including
     the ones within any cables.

     @return The total area in square inches.
     */
    double getConduitablesArea();

    /**
     * Asks for the allowed fill percentage of this conduit as defined by NEC
     Table 1 and notes to tables in Chapter 9.

     @return The allowed fill percentage of this conduit.
     */
    int getAllowedFillPercentage();

    /**
     * Asks for the type of this conduit.
     @return The type of this conduit.
     @see Type
     */
    Type getType();

    /**
     * Sets the type of this conduit.

     @param type The new type of this conduit.
     @see Type
     */
    void setType(Type type);

    /**
     * Asks if this conduit is a nipple, that is, its length is equal or less
     than 24".

     @return True if it's a nipple, false otherwise.
     */
    boolean isNipple();

    /**
     * Marks/unmark this conduit has a nipple, that is, its length is 24" or
     * less.

     @param nipple The nipple value as defined in {@link Conduit.Nipple}.
     */
    void setNipple(Conduit.Nipple nipple);

    /**
     * Sets the rooftop condition for this conduit.

     @param roofTopDistance The distance in inches above roof to bottom of this
     conduit. If a negative value is indicated, the behavior of this method is
     the same as when calling resetRoofTop, which eliminates the roof top
     condition from this conduit.
     */
    void setRoofTopDistance(double roofTopDistance);

    /**
     * Resets the rooftop condition for this conduit, that is, no roof top
     condition.
     */
    void resetRoofTop();

    /**
     * Asks if this conduit is in a rooftop condition as defined by
     <b>310.15(B)(3)(c)</b>.

     @return True if this conduit has a rooftop condition, false otherwise.
     */
    boolean isRooftopCondition();

    /**
     * Returns the rooftop distance of this conduit.

     @return The rooftop distance of this conduit.
     */
    double getRoofTopDistance();

    /**
     * Calculates the trade size of this conduit to accommodate all its
     conductors and cables. The calculation will depend on if this conduit
     is a nipple or not, and on the minimum trade size it can be.

     @return The calculated trade size of this conduit.
     */
    Trade getTradeSize();
}
