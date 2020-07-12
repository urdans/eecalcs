package test.java;

import eecalcs.circuits.Ocpd;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OcpdTest {

    @Test
    void getRatingFor() {
        assertEquals(6000, Ocpd.getRatingFor(6001));
        assertEquals(200, Ocpd.getRatingFor(224));

    }
}