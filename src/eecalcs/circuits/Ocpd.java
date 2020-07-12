package eecalcs.circuits;

import eecalcs.conductors.Conduitable;
import eecalcs.loads.Load;

/**
 This class represents a generic overcurrent protection device.
 */
//todo learn about protection and how to choose the ocpd, when the rule of one size up applies. Maybe, the ocpd needs
// to know about the load and the wire used to feed the load, and maybe other conditions....

public class Ocpd {
    private static int[] standardRatings = {15, 20, 25, 30, 35, 40, 45, 50,
            60, 70, 80, 90, 100, 110, 125, 150, 175, 200, 225, 250, 300, 350,
            400, 450, 500, 600, 700, 800, 1000, 1200, 1600, 2000, 2500, 3000,
            4000, 5000, 6000};

    private boolean _100PercentRated = false; //it's 80% rated by default.
    public static int getRatingFor(Conduitable conduitable, Load load){
        if(conduitable == null)
            return 0;
        return getRatingFor(conduitable.getAmpacity());
/*        for(int i = standardRatings.length - 1; i > 0; i--)
            if(standardRatings[i] <= ampacity)
                return standardRatings[i];
        return 0;*/
    }

    public static int getRatingFor(double ampacity){
//        double ampacity = conduitable.getAmpacity();
        for(int i = standardRatings.length - 1; i > 0; i--)
            if(standardRatings[i] <= ampacity)
                return standardRatings[i];
        return 0;
    }

    public boolean is100PercentRated(){
        return _100PercentRated;
    }

    public void set100PercentRated(boolean flag) {
        this._100PercentRated = flag;
    }
}
