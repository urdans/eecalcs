package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;
import test.Tools;

import static org.junit.jupiter.api.Assertions.*;

class ConductorPropertiesTest {

    @Test
    void getAllowedSize() {
        Tools.printTitle("ConductorPropertiesTest.getAllowedSize");
        assertEquals(Size.AWG_1, ConductorProperties.getAllowedSize(144.23, Metal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_1$0, ConductorProperties.getAllowedSize(144.23, Metal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_3$0, ConductorProperties.getAllowedSize(144.23, Metal.ALUMINUM, TempRating.T75));
    }

    @Test
    void hasInsulatedArea() {
        Tools.printTitle("ConductorPropertiesTest.hasInsulatedArea");
        assertFalse(ConductorProperties.hasInsulatedArea(null, null));
    }


    @Test
    void getInsulatedAreaIn2() {
        Tools.printTitle("ConductorPropertiesTest.getInsulatedAreaIn2");
        assertFalse(ConductorProperties.hasInsulatedArea(null, null));
    }

    @Test
    void getCompactAreaIn2() {
        Tools.printTitle("ConductorPropertiesTest.getCompactAreaIn2");
        assertEquals(0.0, ConductorProperties.getCompactAreaIn2(null, null),0.0001);
        assertEquals(0.0531, ConductorProperties.getCompactAreaIn2(Size.AWG_8, Insul.RHH),0.0001);
        assertEquals(0.0, ConductorProperties.getCompactAreaIn2(Size.AWG_3, Insul.RHH),0.0001);
    }

    @Test
    void getCompactBareAreaIn2() {
        Tools.printTitle("ConductorPropertiesTest.getCompactBareAreaIn2");
        assertEquals(0.0, ConductorProperties.getCompactBareAreaIn2(null),0.0001);
        assertEquals(0.0141026094219646, ConductorProperties.getCompactBareAreaIn2(Size.AWG_8),0.0001);
        assertEquals(0.0, ConductorProperties.getCompactBareAreaIn2(Size.AWG_3),0.0001);
    }

    @Test
    void hasCompactArea() {
        Tools.printTitle("ConductorPropertiesTest.hasCompactArea");
        assertFalse(ConductorProperties.hasCompactArea(null, null));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_3, Insul.USE));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_8, Insul.THHN));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_10, Insul.THW));
        assertFalse(ConductorProperties.hasCompactArea(Size.KCMIL_2000, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactArea(Size.AWG_8, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactArea(Size.KCMIL_1000, Insul.RHH));
    }

    @Test
    void hasCompactBareArea() {
        Tools.printTitle("ConductorPropertiesTest.hasCompactBareArea");
        assertFalse(ConductorProperties.hasCompactArea(null, null));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_3, Insul.USE));
        assertTrue(ConductorProperties.hasCompactArea(Size.KCMIL_750, Insul.THW));
    }

    @Test
    void getTempRating() {
        Tools.printTitle("ConductorPropertiesTest.getTempRating");
        assertNull(ConductorProperties.getTempRating(null));
        assertEquals(TempRating.T90,ConductorProperties.getTempRating(Insul.THHN));
    }

    @Test
    void compareSizes() {
        Tools.printTitle("ConductorPropertiesTest.compareSizes");
        assertEquals(0, ConductorProperties.compareSizes(Size.AWG_1, Size.AWG_1));
        assertTrue(0 >= ConductorProperties.compareSizes(Size.AWG_10, Size.AWG_1));
        assertTrue(0 <= ConductorProperties.compareSizes(Size.AWG_1$0, Size.AWG_1));
    }

    @Test
    void getReactance() {
        Tools.printTitle("ConductorPropertiesTest.getReactance");
        assertEquals(0, ConductorProperties.getReactance(null, ConduitProperties.Magnetic),0.0001);
        assertEquals(0.057000, ConductorProperties.getReactance(Size.AWG_1, ConduitProperties.Magnetic),0.0001);
        assertEquals(0.054, ConductorProperties.getReactance(Size.AWG_10, ConduitProperties.nonMagnetic), 0.0001);
    }

    @Test
    void getAreaCM() {
        Tools.printTitle("ConductorPropertiesTest.getAreaCM");
        assertEquals(4110, ConductorProperties.getAreaCM(Size.AWG_14));
        assertEquals(2000000, ConductorProperties.getAreaCM(Size.KCMIL_2000));
        assertEquals(0, ConductorProperties.getAreaCM(null));
    }

    @Test
    void getDCResistance() {
        Tools.printTitle("ConductorPropertiesTest.getDCResistance");
        assertEquals(3.19, ConductorProperties.getDCResistance(Size.AWG_14, Metal.COPPER, Coating.COATED),0.001);
        assertEquals(0.0106, ConductorProperties.getDCResistance(Size.KCMIL_2000, Metal.ALUMINUM, Coating.COATED),0.0001);
        assertEquals(0, ConductorProperties.getDCResistance(null, null, null),0.0001);

        assertEquals(0.0106*100*0.001/3, ConductorProperties.getDCResistance(Size.KCMIL_2000, Metal.ALUMINUM, 100, 3, Coating.COATED), 0.0001);
        assertEquals(0, ConductorProperties.getDCResistance(null, null, 0, 0,null),0.0001);
    }

    @Test
    void getACResistance() {
        Tools.printTitle("ConductorPropertiesTest.getACResistance");
        assertEquals(3.1, ConductorProperties.getACResistance(Size.AWG_14, Metal.COPPER, Material.PVC),0.001);
        assertEquals(0.0166, ConductorProperties.getACResistance(Size.KCMIL_2000, Metal.ALUMINUM, Material.STEEL),0.0001);
        assertEquals(0, ConductorProperties.getACResistance(null, null, null),0.0001);
    }

    @Test
    void getAmpacity() {
        Tools.printTitle("ConductorPropertiesTest.getAmpacity");
        assertEquals(15, ConductorProperties.getAmpacity(Size.AWG_14, Metal.COPPER, TempRating.T60));
        assertEquals(630, ConductorProperties.getAmpacity(Size.KCMIL_2000, Metal.ALUMINUM, TempRating.T90));
        assertEquals(0, ConductorProperties.getAmpacity(null, null, null));
    }

    @Test
    void isValidFullName() {
        Tools.printTitle("ConductorPropertiesTest.isValidFullName");
        assertTrue(ConductorProperties.isValidFullName("14 AWG"));
        assertTrue(ConductorProperties.isValidFullName("2000 KCMIL"));
        assertFalse(ConductorProperties.isValidFullName("2"));
    }

    @Test
    void getSizeByStringFullName() {
        Tools.printTitle("ConductorPropertiesTest.getSizeByStringFullName");
        assertEquals(Size.AWG_14, ConductorProperties.getSizeByStringFullName("14 AWG"));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizeByStringFullName("2000 KCMIL"));
        assertNull(ConductorProperties.getSizeByStringFullName("2"));
    }

    @Test
    void isValidInsulStringName() {
        Tools.printTitle("ConductorPropertiesTest.isValidInsulStringName");
        assertTrue(ConductorProperties.isValidInsulStringName("TW"));
        assertTrue(ConductorProperties.isValidInsulStringName("ZW-2"));
        assertFalse(ConductorProperties.isValidInsulStringName("2"));
    }

    @Test
    void getInsulByStringName() {
        Tools.printTitle("ConductorPropertiesTest.getInsulByStringName");
        assertEquals(Insul.TW, ConductorProperties.getInsulByStringName("TW"));
        assertEquals(Insul.ZW2, ConductorProperties.getInsulByStringName("ZW-2"));
        assertNull(ConductorProperties.getInsulByStringName("2"));
    }
}