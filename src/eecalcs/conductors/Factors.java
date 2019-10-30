package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;

/**
 * This class encapsulates static methods to provide temperature correction factors for conductors and applies to ampacities defined in NEC
 * "TABLE 310.15(B)(2)(a) Ambient Temperature Correction Factors Based on 30°C (86°F)", as well as to provide adjustment factor for number of current
 * carrying conductors in the same conduit.
 */
public class Factors {
    private int minTF;
    private int maxTF;
    private double correctionFactor60;
    private double correctionFactor75;
    private double correctionFactor90;

    private final static Factors[] tempCorrectionFactors;

    private Factors(int minTF, int maxTF, double correctionFactor60, double correctionFactor75, double correctionFactor90) {
        this.minTF = minTF;
        this.maxTF = maxTF;
        this.correctionFactor60 = correctionFactor60;
        this.correctionFactor75 = correctionFactor75;
        this.correctionFactor90 = correctionFactor90;
    }

    private double getCorrectionFactor(int tempRating){
        if(tempRating == 60) return correctionFactor60;
        if(tempRating == 75) return correctionFactor75;
        if(tempRating == 90) return correctionFactor90;
        return 0;
    }

    private boolean inRangeF(int ambientTempF){
        return ambientTempF >= minTF & ambientTempF <= maxTF;
    }

    /**
     * Returns the temperature correction factor that applies to conductors' ampacities specified in table 310.15(B)(16) and corresponding to the given
     * ambient temperature (in degrees Fahrenheits), for the given conductor's temperature rating (60, 75 or 90 degrees Celsius). This correction factor is a
     * multiplier of the conductor's ampacity.
     * @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
     * @param temperatureRating The temperature rating of the conductor for which the ampacity is being corrected.
     * @return The temperature correction factor. If the ambient temperature exceeds the conductor temperature ratings, the conductor cannot be used and
     * hence the returned value is zero.
     */
    public static double getTemperatureCorrectionF(int ambientTemperatureF, TempRating temperatureRating){
        for(Factors tcf: tempCorrectionFactors){
            if(tcf.inRangeF(ambientTemperatureF))
                return tcf.getCorrectionFactor(temperatureRating.getValue());
        }
        return 0;
    }

    /**
     * Returns the adjustment factor for all current carrying conductors inside the given conduit, as defined in NEC table 310.15(B)(3)(a). This method
     * complies with 310.15(B)(3)(a)(2) which mandates that for nipple conduits, conductors are not applied adjustment factor.
     * @param conduit The conduit that contains the conductor. If this value is null, the returned value is one, assuming conductors in free air.
     * @return he adjustment factor.
     */
    public static double getAdjustmentFactor(Conduit conduit){
        if(conduit != null && !conduit.isNipple()){
            int totalCurrentCarrying = 0;
            for(Conduitable conduitable: conduit.getConduitables())
                totalCurrentCarrying += conduitable.getCurrentCarryingCount();
            //******************** Applying NEC table 310.15(B)(3)(a) ************************
            if(totalCurrentCarrying <= 3)
                return 1;
            else if(totalCurrentCarrying >= 4 & totalCurrentCarrying <= 6)
                return 0.8;
            else if(totalCurrentCarrying >= 7 & totalCurrentCarrying <= 9)
                return 0.7;
            else if(totalCurrentCarrying >= 10 & totalCurrentCarrying <= 20)
                return 0.5;
            else if(totalCurrentCarrying >= 21 & totalCurrentCarrying <= 30)
                return 0.45;
            else if(totalCurrentCarrying >= 31 & totalCurrentCarrying <= 40)
                return 0.4;
            else // totalCurrentCarrying >= 41
                return 0.35;
        }
        //this conduitable is not in a conduit
        return 1;
    }

    /**
     * Return the ambient temperature adjustment for conduits or cables exposes to sunlight on or above rooftops (NEC table 310.15(B)(3)(c).
     * @param distanceABoveRoof The distance above rooftop in inches.
     * @return The temperature adjustment in degrees Fahrenheits.
     */
    public static int getRoofTopTempAdjustment(double distanceABoveRoof){
        if(distanceABoveRoof < 0)
            return 0;
        if(distanceABoveRoof >= 0 & distanceABoveRoof < 0.5)
            return 60;
        if(distanceABoveRoof > 0.5 & distanceABoveRoof <= 3.5)
            return 40;
        if(distanceABoveRoof > 3.5 & distanceABoveRoof <= 12)
            return 30;
        if(distanceABoveRoof > 12 & distanceABoveRoof <= 36)
            return 25;
        return 0;
    }

    static {
        tempCorrectionFactors = new Factors[]{
                new Factors(5,   50,  1.29, 1.2,  1.15),
                new Factors(51,  59,  1.22, 1.15, 1.12),
                new Factors(60,  68,  1.15, 1.11, 1.08),
                new Factors(69,  77,  1.08, 1.05, 1.04),
                new Factors(78,  86,  1,    1,    1),
                new Factors(87,  95,  0.91, 0.94, 0.96),
                new Factors(96,  104, 0.82, 0.88, 0.91),
                new Factors(105, 113, 0.71, 0.82, 0.87),
                new Factors(114, 122, 0.58, 0.75, 0.82),
                new Factors(123, 131, 0.41, 0.67, 0.76),
                new Factors(132, 140, 0,    0.58, 0.71),
                new Factors(141, 149, 0,    0.47, 0.65),
                new Factors(150, 158, 0,    0.33, 0.58),
                new Factors(159, 167, 0,    0,    0.5),
                new Factors(168, 176, 0,    0,    0.41),
                new Factors(177, 185, 0,    0,    0.29),
        };
    }

}
