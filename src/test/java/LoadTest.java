package test.java;

import eecalcs.circuits.Load;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;
import test.Tools;

import static org.junit.jupiter.api.Assertions.*;

class LoadTest {
    Load load = new Load(VoltageSystemAC.v120_1ph_2w, 1200);

    @Test
    void setSystemVoltage(){
        Tools.printTitle("LoadTest.setSystemVoltage");
        assertEquals(1200, load.getWatts());
        assertEquals(1200, load.getVoltAmperes());
        assertEquals(1200/120, load.getCurrent());
        assertEquals(1, load.getPowerFactor());

        load.setSystemVoltage(VoltageSystemAC.v208_3ph_3w);
        assertEquals(1200, load.getWatts());
        assertEquals(1200, load.getVoltAmperes());
        assertEquals(1200/(208*Math.sqrt(3)), load.getCurrent());
        assertEquals(1, load.getPowerFactor());
    }

    @Test
    void setVoltAmperes(){
        Tools.printTitle("LoadTest.setVoltAmperes");
        load.setVoltAmperes(1800);
        assertEquals(1800, load.getWatts());
        assertEquals(1800, load.getVoltAmperes());
        assertEquals(1800.0/120, load.getCurrent());
        assertEquals(1, load.getPowerFactor());

        load.setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        load.setVoltAmperes(1500);
        assertEquals(1500, load.getWatts());
        assertEquals(1500, load.getVoltAmperes());
        assertEquals(1500/(208*Math.sqrt(3)), load.getCurrent());
        assertEquals(1, load.getPowerFactor());
    }

    @Test
    void setWatts(){
        Tools.printTitle("LoadTest.setWatts");
        load.setWatts(800);
        assertEquals(800, load.getWatts());
        assertEquals(800, load.getVoltAmperes());
        assertEquals(800.0/120, load.getCurrent());
        assertEquals(1, load.getPowerFactor());

        load.setSystemVoltage(VoltageSystemAC.v240_3ph_3w);
        load.setWatts(1000);
        assertEquals(1000, load.getWatts());
        assertEquals(1000, load.getVoltAmperes());
        assertEquals(1000/(240*Math.sqrt(3)), load.getCurrent());
        assertEquals(1, load.getPowerFactor());
    }

    @Test
    void setCurrent(){
        Tools.printTitle("LoadTest.setCurrent");
        load.setCurrent(8);
        assertEquals(120*8.0, load.getWatts());
        assertEquals(120*8.0, load.getVoltAmperes());
        assertEquals(8.0, load.getCurrent());
        assertEquals(1, load.getPowerFactor());

        load.setSystemVoltage(VoltageSystemAC.v480_3ph_3w);
        load.setCurrent(5);
        assertEquals(480*5*Math.sqrt(3), load.getWatts());
        assertEquals(480*5*Math.sqrt(3), load.getVoltAmperes());
        assertEquals(5.0, load.getCurrent());
        assertEquals(1, load.getPowerFactor());
    }

    @Test
    void setPowerFactor(){
        Tools.printTitle("LoadTest.setPowerFactor");
        load.setPowerFactor(0.8);
        assertEquals(1200*0.8, load.getWatts());
        assertEquals(1200, load.getVoltAmperes());
        assertEquals(10, load.getCurrent());
        assertEquals(0.8, load.getPowerFactor());

        load.setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        load.setPowerFactor(0.85);
        assertEquals(1200*0.85, load.getWatts());
        assertEquals(1200, load.getVoltAmperes());
        assertEquals(1200/(208*Math.sqrt(3)), load.getCurrent());
        assertEquals(0.85, load.getPowerFactor());
    }

}