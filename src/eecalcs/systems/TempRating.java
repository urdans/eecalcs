package eecalcs.systems;

/**
 * Container for standard temperature ratings, in degrees Celsius,  of known insulations
 */
public enum TempRating {
    T60(60), T75(75), T90(90);
    private int value;

    private TempRating(int value){
        this.value = value;
    }

    /**
     * Returns the temperature that this enum represents.
     * @return The temperature in degrees Celsius.
     */
    public int getValue(){
        return value;
    }
}
