package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import eecalcs.voltagedrop.VoltDrop;
import org.junit.jupiter.api.Test;
import test.Tools;
import tools.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VoltDropTest {
    Conductor conductor = new Conductor();
    VoltDrop voltDrop = new VoltDrop(conductor);

    void change1(){
        voltDrop.setSourceVoltage(VoltageSystemAC.v208_3ph_3w);
        voltDrop.setConduitMaterial(Material.ALUMINUM);
        voltDrop.setSets(2);
        voltDrop.setLoadCurrent(130);
        voltDrop.setPowerFactor(0.9);
        voltDrop.setMaxVoltageDropPercent(3);
        conductor.setCopperCoated(Coating.COATED);
        conductor.setSize(Size.AWG_1$0);
        conductor.setLength(350);
    }

    void change2(){
        voltDrop.setSourceVoltage(VoltageSystemAC.v480_3ph_4w);
        voltDrop.setConduitMaterial(Material.ALUMINUM);
        voltDrop.setSets(2);
        voltDrop.setLoadCurrent(460);
        voltDrop.setMaxVoltageDropPercent(2);
        voltDrop.setPowerFactor(0.85);

        conductor.setLength(250);
        conductor.setCopperCoated(Coating.COATED);
        conductor.setSize(Size.AWG_4$0);
    }

    @Test
    void getACVoltageDrop() {
        Tools.printTitle("VoltDropTest.getACVoltageDrop");
        assertEquals(3.9999, voltDrop.getACVoltageDrop(), 0.0001);

        change1();
        assertEquals(5.3649, voltDrop.getACVoltageDrop(), 0.0001);

        change2();
        assertEquals(7.8228, voltDrop.getACVoltageDrop(), 0.0001);
        assertEquals(480-7.8228, voltDrop.getACVoltageAtLoad(), 0.0001);
        assertEquals(100*7.8228/480, voltDrop.getACVoltageDropPercentage(), 0.0001);
        voltDrop.resultMessages.getMessages().forEach(message -> System.out.println(message.message+": "+message.number));
    }

    @Test
    void getDCVoltageDrop() {
        Tools.printTitle("VoltDropTest.getDCVoltageDrop");
        assertEquals(3.8600, voltDrop.getDCVoltageDrop(), 0.0001);

        change1();
        assertEquals(5.7785, voltDrop.getDCVoltageDrop(), 0.0001);

        change2();
        assertEquals(7.1990, voltDrop.getDCVoltageDrop(), 0.0001);
        assertEquals(480-7.1990, voltDrop.getDCVoltageAtLoad(), 0.0001);
        assertEquals(100*7.1990/480, voltDrop.getDCVoltageDropPercentage(), 0.0001);
        voltDrop.resultMessages.getMessages().forEach(message -> System.out.println(message.message+": "+message.number));
    }

    @Test
    void getCalculatedSizeAC() {
        Tools.printTitle("VoltDropTest.getCalculatedSizeAC");
        assertEquals(Size.AWG_10, voltDrop.getCalculatedSizeAC());

        change1();
        assertEquals(Size.AWG_1$0, voltDrop.getCalculatedSizeAC());

        change2();
        assertEquals(Size.AWG_4$0, voltDrop.getCalculatedSizeAC());
    }

    @Test
    void getCalculatedSizeDC() {
        Tools.printTitle("VoltDropTest.getCalculatedSizeDC");
        assertEquals(Size.AWG_10, voltDrop.getCalculatedSizeDC());

        change1();
        assertEquals(Size.AWG_1$0, voltDrop.getCalculatedSizeDC());

        change2();
        assertEquals(Size.AWG_3$0, voltDrop.getCalculatedSizeDC());
    }

    @Test
    void getMaxLengthAC(){
        Tools.printTitle("VoltDropTest.getMaxLengthAC");
        assertEquals(150.0046, voltDrop.getMaxLengthACForCalculatedSize(), 0.0001);

        change1();
        assertEquals(407.1041, voltDrop.getMaxLengthACForCalculatedSize(), 0.0001);

        change2();
        assertEquals(306.7943, voltDrop.getMaxLengthACForCalculatedSize(), 0.0001);
    }

    @Test
    void getMaxLengthDC() {
        Tools.printTitle("VoltDropTest.getMaxLengthDC");
        assertEquals(148.7603, voltDrop.getMaxLengthDCForCalculatedSize(), 0.0001);

        change1();
        assertEquals(377.9528, voltDrop.getMaxLengthDCForCalculatedSize(), 0.0001);

        change2();
        assertEquals(261.8515, voltDrop.getMaxLengthDCForCalculatedSize(), 0.0001);
    }

    @Test
    void getActualVoltageDropPercentageAC(){
        Tools.printTitle("VoltDropTest.getActualVoltageDropPercentageAC");
        assertEquals(2.0000, voltDrop.getActualVoltageDropPercentageAC(), 0.0001);

        change1();
        assertEquals(2.5793, voltDrop.getActualVoltageDropPercentageAC(), 0.0001);

        change2();
        assertEquals(1.6298, voltDrop.getActualVoltageDropPercentageAC(), 0.0001);
    }

    @Test
    void getActualVoltageDropPercentageDC(){
        Tools.printTitle("VoltDropTest.getActualVoltageDropPercentageDC");
        assertEquals(2.0167, voltDrop.getActualVoltageDropPercentageDC(), 0.0001);

        change1();
        assertEquals(2.7781, voltDrop.getActualVoltageDropPercentageDC(), 0.0001);

        change2();
        assertEquals(1.9095, voltDrop.getActualVoltageDropPercentageDC(), 0.0001);
    }
    @Test
    void getErrorMessages() {
        Tools.printTitle("VoltDropTest.getErrorMessages");
        VoltDrop voltDrop2 = new VoltDrop(null);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-9));

        voltDrop2 = new VoltDrop(new Conductor());
        assertFalse(voltDrop2.resultMessages.containsMessage(-9));

        Conductor conductor2 = new Conductor(null, Metal.COPPER, Insul.THW, 0);
        voltDrop2 = new VoltDrop(conductor2);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-3));
        assertTrue(voltDrop2.resultMessages.containsMessage(-5));

        conductor2 = new Conductor(Size.AWG_3, Metal.COPPER, Insul.THW, 80);
        voltDrop2 = new VoltDrop(conductor2);
        assertFalse(voltDrop2.resultMessages.containsMessage(-3));
        assertFalse(voltDrop2.resultMessages.containsMessage(-5));

        voltDrop2.setSourceVoltage(null);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-1));

        voltDrop2.setSourceVoltage(VoltageSystemAC.v277_1ph_2w);
        assertEquals(0.3999, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-1));

        voltDrop2.setSets(11);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-4));

        voltDrop2.setSets(8);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-4));

        conductor2 = new Conductor();
        voltDrop2 = new VoltDrop(conductor2);
        voltDrop2.setSets(2);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-21));

        voltDrop2.setSets(1);
        assertEquals(3.9999, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-21));

        voltDrop2.setLoadCurrent(0);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-6));

        voltDrop2.setLoadCurrent(35);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-20));

        voltDrop2.setLoadCurrent(20);
        assertEquals(7.9997, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-20));

        voltDrop2.setPowerFactor(0.69);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-7));

        voltDrop2.setPowerFactor(1.1);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-7));

        voltDrop2.setPowerFactor(0.9);
        assertEquals(7.2460, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-7));

        voltDrop2.setMaxVoltageDropPercent(0.4);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-8));

        voltDrop2.setMaxVoltageDropPercent(26);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-8));

        voltDrop2.setMaxVoltageDropPercent(6);
        assertEquals(7.2460, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-8));

        voltDrop2.setConduitMaterial(null);
        assertEquals(0, voltDrop2.getACVoltageDrop(), 0.0001);
        assertTrue(voltDrop2.resultMessages.containsMessage(-2));

        voltDrop2.setConduitMaterial(Material.STEEL);
        assertEquals(7.2719, voltDrop2.getACVoltageDrop(), 0.0001);
        assertFalse(voltDrop2.resultMessages.containsMessage(-2));

        voltDrop2.setSourceVoltage(VoltageSystemAC.v277_1ph_2w);
        assertFalse(voltDrop2.resultMessages.hasErrors());
        assertEquals(7.2991, voltDrop2.getACVoltageDrop(), 0.0001);
        assertEquals(100*7.2991/277, voltDrop2.getACVoltageDropPercentage(), 0.0001);
    }

}