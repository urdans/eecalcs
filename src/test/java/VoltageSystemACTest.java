package test.java;

import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoltageSystemACTest {
    VoltageSystemAC v120_1ph_2w = VoltageSystemAC.v120_1ph_2w;
    VoltageSystemAC v208_1ph_2w = VoltageSystemAC.v208_1ph_2w;
    VoltageSystemAC v240_1ph_3w = VoltageSystemAC.v240_1ph_3w;
    VoltageSystemAC v480_3ph_3w = VoltageSystemAC.v480_3ph_3w;
    VoltageSystemAC v480_3ph_4w = VoltageSystemAC.v480_3ph_4w;

    @Test
    void getName() {
        assertEquals("120v 1Ø 2W", v120_1ph_2w.getName());
        assertEquals("208v 1Ø 2W", v208_1ph_2w.getName());
        assertEquals("240v 1Ø 3W", v240_1ph_3w.getName());
        assertEquals("480v 3Ø 3W", v480_3ph_3w.getName());
        assertEquals("480v 3Ø 4W", v480_3ph_4w.getName());
    }

    @Test
    void getNames() {
        assertEquals("120v 1Ø 2W",  VoltageSystemAC.getNames()[0]);
        assertEquals("208v 1Ø 2W",  VoltageSystemAC.getNames()[1]);
        assertEquals("240v 1Ø 3W",  VoltageSystemAC.getNames()[7]);
        assertEquals("480v 3Ø 3W",  VoltageSystemAC.getNames()[13]);
        assertEquals("480v 3Ø 4W",  VoltageSystemAC.getNames()[14]);
    }

    @Test
    void getVoltage() {
        assertEquals(120, v120_1ph_2w.getVoltage());
        assertEquals(208, v208_1ph_2w.getVoltage());
        assertEquals(240, v240_1ph_3w.getVoltage());
        assertEquals(480, v480_3ph_3w.getVoltage());
        assertEquals(480, v480_3ph_4w.getVoltage());
    }

    @Test
    void getPhases() {
        assertEquals(1, v120_1ph_2w.getPhases());
        assertEquals(1, v208_1ph_2w.getPhases());
        assertEquals(1, v240_1ph_3w.getPhases());
        assertEquals(3, v480_3ph_3w.getPhases());
        assertEquals(3, v480_3ph_4w.getPhases());
    }

    @Test
    void getWires() {
        assertEquals(2, v120_1ph_2w.getWires());
        assertEquals(2, v208_1ph_2w.getWires());
        assertEquals(3, v240_1ph_3w.getWires());
        assertEquals(3, v480_3ph_3w.getWires());
        assertEquals(4, v480_3ph_4w.getWires());
    }

    @Test
    void getFactor() {
        assertEquals(1,   v120_1ph_2w.getFactor());
        assertEquals(1,   v208_1ph_2w.getFactor());
        assertEquals(1,   v240_1ph_3w.getFactor());
        assertEquals(Math.sqrt(3), v480_3ph_3w.getFactor());
        assertEquals(Math.sqrt(3), v480_3ph_4w.getFactor());
    }

	@Test
	void setCustom() {
        VoltageSystemAC custom = VoltageSystemAC.v_other.setCustom(575,3,3);
        assertEquals("575v 3Ø 3W",custom.getName());

        VoltageSystemAC v120 = VoltageSystemAC.v120_1ph_2w.setCustom(140,5,-2);
        assertEquals("120v 1Ø 2W", v120.getName());
        //voltage system does not change because wrong parameters
        assertEquals("575v 3Ø 3W", custom.setCustom(0,0,0).getName());
        assertEquals("575v 3Ø 3W", custom.setCustom(120,1,1).getName());
        assertEquals("575v 3Ø 3W", custom.setCustom(120,1,6).getName());
        //voltage system changes, correct parameters
        assertEquals("120v 1Ø 3W", custom.setCustom(120,1,3).getName());
        //voltage system does not change because wrong parameters
        assertEquals("120v 1Ø 3W", custom.setCustom(120,2,3).getName());
        assertEquals("120v 1Ø 3W", custom.setCustom(120,4,3).getName());
        //voltage system changes, correct parameters
        assertEquals("600v 3Ø 3W", custom.setCustom(600,3,3).getName());
    }
}