package eecalcs.conductors;

public interface ShareableBundle {
    /** Refer to {@link Bundle#complyWith310_15_B_3_a_4()} */
    boolean complyWith310_15_B_3_a_4();

    /** Refer to {@link Bundle#complyWith310_15_B_3_a_5()} */
    boolean complyWith310_15_B_3_a_5();

    /** Refer to {@link Bundle#getCurrentCarryingNumber()} */
    int getCurrentCarryingNumber();

    /** Refer to {@link Bundle#getDistance()} */
    double getDistance();

    /** Refer to {@link Bundle#hasConduitable(Conduitable)} ()} */
    boolean hasConduitable(Conduitable conduitable);

    /** Refer to {@link Bundle#isEmpty()} */
    boolean isEmpty();

    /** Refer to {@link Bundle#setDistance(double)} ()} */
    void setDistance(double distance);
}
