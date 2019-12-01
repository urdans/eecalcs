package test.java;

import eecalcs.conductors.CircuitOld;
import eecalcs.conductors.Coating;
import eecalcs.conductors.Conductor;
import eecalcs.conductors.Size;
import eecalcs.conduits.Material;
import eecalcs.voltagedrop.VDrop;
import org.junit.jupiter.api.Test;
import test.Tools;

import static org.junit.jupiter.api.Assertions.*;

class VDropTest {
    Conductor conductor = new Conductor();
    CircuitOld circuitOld = new CircuitOld(conductor);
    VDrop vd = new VDrop(circuitOld);

    void change1(){
        vd.setSourceVoltage(208);
        vd.getCircuitOld().setSize(Size.AWG_1$0);
        vd.setPhases(3);
        vd.getCircuitOld().setConduitMaterial(Material.ALUMINUM);
        vd.getCircuitOld().setCopperCoated(Coating.COATED);
        vd.getCircuitOld().setNumberOfSets(2);
        vd.setLoadCurrent(130);
        vd.setPowerFactor(0.9);
        vd.getCircuitOld().setLength(350);
    }

    void change2(){
//        vd.getCircuitOld().setSize(Size.AWG_1$0);
//        vd.getCircuitOld().setConduitMaterial(Material.ALUMINUM);
//        vd.getCircuitOld().setCopperCoated(Coating.COATED);
        vd.setSourceVoltage(480);
        vd.setPhases(3);
        vd.getCircuitOld().setNumberOfSets(2);
        vd.setLoadCurrent(500);
        vd.getCircuitOld().setLength(250);
        vd.setMaxVoltageDropPercent(2);
        vd.setPowerFactor(0.85);
    }

    @Test
    void setSourceVoltage() {
        Tools.printTitle("VDropTest.setSourceVoltage");
        assertEquals(120, vd.getSourceVoltage());

        vd.setSourceVoltage(208);
        assertEquals(208, vd.getSourceVoltage());
    }

    @Test
    void setPhases() {
        Tools.printTitle("VDropTest.setPhases");
        assertEquals(1, vd.getPhases());

        vd.setPhases(3);
        assertEquals(3, vd.getPhases());
    }

    @Test
    void setLoadCurrent() {
        Tools.printTitle("VDropTest.setLoadCurrent");
        assertEquals(10, vd.getLoadCurrent());

        vd.setLoadCurrent(15);
        assertEquals(15, vd.getLoadCurrent());
    }

    @Test
    void setPowerFactor() {
        Tools.printTitle("VDropTest.setPowerFactor");
        assertEquals(1, vd.getPowerFactor());

        vd.setPowerFactor(0.8);
        assertEquals(0.8, vd.getPowerFactor());
    }

    @Test
    void setMaxVoltageDropPercent() {
        Tools.printTitle("VDropTest.setMaxVoltageDropPercent");
        assertEquals(3, vd.getMaxVoltageDropPercent());

        vd.setMaxVoltageDropPercent(5.5);
        assertEquals(5.5, vd.getMaxVoltageDropPercent());
    }

    @Test
    void getVoltageAtLoadAC() {
        Tools.printTitle("VDropTest.getVoltageAtLoadAC");
        assertEquals(116.0001, vd.getVoltageAtLoadAC(), 0.0001);

        change1();
        assertEquals(202.6351, vd.getVoltageAtLoadAC(), 0.0001);
    }

    @Test
    void getVoltageDropVoltsAC() {
        Tools.printTitle("VDropTest.getVoltageAtLoadAC");
        assertEquals(3.9999, vd.getVoltageDropVoltsAC(), 0.001);

        change1();
        assertEquals(5.3649, vd.getVoltageDropVoltsAC(), 0.0001);
    }

    @Test
    void getVoltageDropPercentageAC() {
        Tools.printTitle("VDropTest.getVoltageDropPercentageAC");
        assertEquals(3.3333, vd.getVoltageDropPercentageAC(), 0.0001);

        change1();
        assertEquals(2.5793, vd.getVoltageDropPercentageAC(), 0.0001);
    }

    @Test
    void getVoltageAtLoadDC() {
        Tools.printTitle("VDropTest.getVoltageAtLoadDC");
        assertEquals(116.1400, vd.getVoltageAtLoadDC(), 0.0001);

        change1();
        assertEquals(202.2215, vd.getVoltageAtLoadDC(), 0.0001);
    }

    @Test
    void getVoltageDropVoltsDC() {
        Tools.printTitle("VDropTest.getVoltageDropVoltsDC");
        assertEquals(3.8600, vd.getVoltageDropVoltsDC(), 0.0001);

        change1();
        assertEquals(5.7785, vd.getVoltageDropVoltsDC(), 0.0001);
    }

    @Test
    void getVoltageDropPercentageDC() {
        Tools.printTitle("VDropTest.getVoltageDropPercentageDC");
        assertEquals(3.2167, vd.getVoltageDropPercentageDC(), 0.0001);

        change1();
        assertEquals(2.7781, vd.getVoltageDropPercentageDC(), 0.0001);
    }

    @Test
    void getCalculatedSizeAC() {
        Tools.printTitle("VDropTest.getCalculatedSizeAC");
        assertEquals(Size.AWG_10, vd.getCalculatedSizeAC());

        change1();
        assertEquals(Size.AWG_1$0, vd.getCalculatedSizeAC());

        change2();
        assertEquals(Size.AWG_4$0, vd.getCalculatedSizeAC());
    }

    @Test
    void getActualVoltageDropPercentageAC() {
        Tools.printTitle("VDropTest.getActualVoltageDropPercentageAC");
        assertEquals(1.9999, vd.getActualVoltageDropPercentageAC(), 0.0001);

        change1();
        assertEquals(2.5793, vd.getActualVoltageDropPercentageAC(), 0.0001);

        change2();
        assertEquals(1.7715, vd.getActualVoltageDropPercentageAC(), 0.0001);
    }

    @Test
    void getMaxLengthAC() {
        Tools.printTitle("VDropTest.getMaxLengthAC");
        assertEquals(150.0046, vd.getMaxLengthAC(), 0.0001);

        change1();
        assertEquals(407.1041, vd.getMaxLengthAC(), 0.0001);

        change2();
        assertEquals(282.2508, vd.getMaxLengthAC(), 0.0001);
    }

    @Test
    void getCalculatedSizeDC() {
        Tools.printTitle("VDropTest.getCalculatedSizeDC");
        assertEquals(Size.AWG_10, vd.getCalculatedSizeDC());

        change1();
        assertEquals(Size.AWG_1$0, vd.getCalculatedSizeDC());

        change2();
        assertEquals(Size.AWG_4$0, vd.getCalculatedSizeDC());
    }

    @Test
    void getActualVoltageDropPercentageDC() {
        Tools.printTitle("VDropTest.getActualVoltageDropPercentageDC");
        assertEquals(2.0166, vd.getActualVoltageDropPercentageDC(), 0.0001);

        change1();
        assertEquals(2.7781, vd.getActualVoltageDropPercentageDC(), 0.0001);

        change2();
        assertEquals(1.6302, vd.getActualVoltageDropPercentageDC(), 0.0001);
    }

    @Test
    void getMaxLengthDC() {
        Tools.printTitle("VDropTest.getMaxLengthDC");
        assertEquals(148.7603, vd.getMaxLengthDC(), 0.0001);

        change1();
        assertEquals(377.9528, vd.getMaxLengthDC(), 0.0001);

        change2();
        assertEquals(306.7093, vd.getMaxLengthDC(), 0.0001);
    }
}