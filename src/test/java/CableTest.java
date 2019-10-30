package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.SystemAC;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CableTest {
    Cable cable;

    @Test
    void getInsulatedAreaIn2() {
        double diameter = 2;
        cable = new Cable(SystemAC.Voltage.v277_1ph, SystemAC.Wires.W4, diameter);
        assertEquals(diameter*diameter*0.25*Math.PI, cable.getInsulatedAreaIn2());
        diameter = 0.5; cable.setOuterDiameter(diameter);
        assertEquals(diameter*diameter*0.25*Math.PI, cable.getInsulatedAreaIn2());
        diameter = 0; ; cable.setOuterDiameter(diameter);
        assertEquals(0.25*0.25*0.25*Math.PI, cable.getInsulatedAreaIn2());
    }

    @Test
    void getCurrentCarryingCount() {
        cable = new Cable(SystemAC.Voltage.v480_3ph, SystemAC.Wires.W4, 1);
        assertEquals(3, cable.getCurrentCarryingCount());
        cable.setNeutralCarryingConductor(true);
        assertEquals(4, cable.getCurrentCarryingCount());
        cable.setNeutralCarryingConductor(false);
        assertEquals(3, cable.getCurrentCarryingCount());
        cable.setNeutralCarryingConductor(true);
        cable.setSystem(SystemAC.Voltage.v240_3ph, SystemAC.Wires.W2);
        assertEquals(3, cable.getCurrentCarryingCount());
        cable.setSystem(SystemAC.Voltage.v208_3ph, SystemAC.Wires.W3);
        assertEquals(3, cable.getCurrentCarryingCount());
        cable.setSystem(SystemAC.Voltage.v240_1ph, SystemAC.Wires.W2);
        assertEquals(2, cable.getCurrentCarryingCount());
        cable.setSystem(SystemAC.Voltage.v240_1ph, SystemAC.Wires.W3);
        assertEquals(3, cable.getCurrentCarryingCount());
        cable.setSystem(SystemAC.Voltage.v120_1ph, SystemAC.Wires.W4);
        assertEquals(2, cable.getCurrentCarryingCount());
        cable.setSystem(SystemAC.Voltage.v240_3ph, SystemAC.Wires.W4);
        assertEquals(4, cable.getCurrentCarryingCount());
    }

    @Test
    void getAmpacity() {
        cable = new Cable(SystemAC.Voltage.v277_1ph, SystemAC.Wires.W4, 1);
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(285, cable.getAmpacity());
        cable.setAmbientTemperatureF(100);
        assertEquals(285*0.88, cable.getAmpacity());
        cable.setRoofTopDistance(38);
        assertEquals(285*0.88, cable.getAmpacity());
        cable.setRoofTopDistance(36);
        assertEquals(285*0.67, cable.getAmpacity());
        cable.setRoofTopDistance(6);
        assertEquals(285*0.67, cable.getAmpacity());
        cable.setRoofTopDistance(2);
        assertEquals(285*0.58, cable.getAmpacity());
        cable.setRoofTopDistance(0.25);
        assertEquals(285*0, cable.getAmpacity());
        cable.resetRoofTop();
        assertEquals(285*0.88, cable.getAmpacity());
        Conduit conduit = new Conduit(Type.ENT, Conduit.Nipple.No);
        conduit.add(cable);
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        cable.setAmbientTemperatureF(100);
        cable.setRoofTopDistance(20);
        System.out.println("Number of CCC: " + conduit.getCurrentCarryingNumber());
        System.out.println("Ambient temperature: " + cable.getAmbientTemperatureF());
        assertEquals(285*0.67*0.7, cable.getAmpacity());
        Conduitable fithConductor = conduit.getConduitables().get(5);
        conduit.remove(fithConductor);
        assertEquals(285*0.67*0.8, cable.getAmpacity());
        /*todo test ampacity for when nipple, no adjustment is done*/
        conduit.setNipple(Conduit.Nipple.Yes);
        cable.setMetal(Metal.ALUMINUM);
        cable.setInsulation(Insul.THHN);
        System.out.println("Number of CCC: " + conduit.getCurrentCarryingNumber());
        System.out.println("Ambient temperature: " + cable.getAmbientTemperatureF());
        cable.resetRoofTop();
        assertEquals(260*0.91*1, cable.getAmpacity());
        conduit.add(fithConductor);
        conduit.setNipple(Conduit.Nipple.No);
        assertEquals(260*0.91*0.7, cable.getAmpacity());
        cable.setType(Cable.Type.MC);
        conduit.remove(cable);
        cable.setBundlingExceeds20(true);
        cable.setBundlingDistanceExceeds24(true);
        assertEquals(260*0.91*0.60, cable.getAmpacity());
        cable.setBundlingExceeds20(false);
        cable.setPhaseConductorSize(Size.AWG_12);
        cable.setMetal(Metal.COPPER);
        assertEquals(30*0.91*1.0, cable.getAmpacity());
        /*todo test ampacity for when cable is MC and not jacketed and when jacketed*/
    }

    @Test
    void getPhaseConductorSize() {
        cable = new Cable(SystemAC.Voltage.v277_1ph, SystemAC.Wires.W4, 1);
        cable.setPhaseConductorSize(Size.KCMIL_250);
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.KCMIL_300, cable.getPhaseConductorSize());
        cable.setNeutralConductorSize(Size.AWG_14);
        assertEquals(Size.AWG_14, cable.getPhaseConductorSize());
    }
}