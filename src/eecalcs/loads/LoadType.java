package eecalcs.loads;

/**
 This enum represents the load types in regards to its continuousness.
 For a CONTINUOUS load, load MCA = current * 1.25;
 For a NONCONTINUOUS load, load MCA = current;
 For a MIXED load, load MCA > current;
 */
public enum LoadType {
    CONTINUOUS,
    NONCONTINUOUS,
    MIXED
}
