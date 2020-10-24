package test.java;

import eecalcs.conductors.Factors;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;
import test.Tools;

import static org.junit.jupiter.api.Assertions.*;


class FactorsTest {
    private static Conduit conduit;

    @Test
    void getTemperatureCorrectionF() {
        Tools.printTitle("FactorsTest.getTemperatureCorrectionF");
        assertEquals(0.91, Factors.getTemperatureCorrectionF(100, TempRating.T90));
        assertEquals(0.29, Factors.getTemperatureCorrectionF(185, TempRating.T90));
        assertEquals(0.0,  Factors.getTemperatureCorrectionF(185, TempRating.T60));
        assertEquals(0.0,  Factors.getTemperatureCorrectionF(185, TempRating.T75));
        assertEquals(1.29, Factors.getTemperatureCorrectionF(50,  TempRating.T60));
        assertEquals(1.20, Factors.getTemperatureCorrectionF(50,  TempRating.T75));
        assertEquals(1.15, Factors.getTemperatureCorrectionF(50,  TempRating.T90));
    }

    @Test
    void getAdjustmentFactor() {
        conduit = new Conduit(Type.EMT, false);
        Tools.printTitle("FactorsTest.getAdjustmentFactor");
        assertEquals(1.0, Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount(), conduit.isNipple()));
    }
}