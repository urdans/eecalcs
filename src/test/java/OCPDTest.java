package test.java;

import eecalcs.circuits.OCPD;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OCPDTest {

    @Test
    void getRatingFor() {
        //case for upper extreme limit
        assertEquals(6000, OCPD.getRatingFor(6001, true));
        assertEquals(6000, OCPD.getRatingFor(6001, false));

        //case for an ampacity value even greater than the critical value of 800
        assertEquals(1000, OCPD.getRatingFor(1001, true));
        assertEquals(1000, OCPD.getRatingFor(1001, false));

        //case for an ampacity value greater than the critical value of 800
        assertEquals(800, OCPD.getRatingFor(900, true));
        assertEquals(800, OCPD.getRatingFor(900, false));

        //case for an ampacity value equal to the critical value of 800
        assertEquals(800, OCPD.getRatingFor(800, true));
        assertEquals(800, OCPD.getRatingFor(800, false));

        //case for an ampacity value lesser than the critical value of 800
        assertEquals(700, OCPD.getRatingFor(750, false));
        assertEquals(800, OCPD.getRatingFor(750, true));

        //case for an ampacity value even lesser than the critical value of 800
        assertEquals(200, OCPD.getRatingFor(224, false));
        assertEquals(225, OCPD.getRatingFor(224, true));

        //lower extreme limit case
        assertEquals(15, OCPD.getRatingFor(14, true));
        assertEquals(15, OCPD.getRatingFor(14, false));
    }
}