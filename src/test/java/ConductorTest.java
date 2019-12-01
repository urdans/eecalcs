package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;
import test.Tools;

import static org.junit.jupiter.api.Assertions.*;

class ConductorTest {
    Conductor conductor;
    Bundle bundle;
    Conduit conduit;

    @Test
    void testClone() {
        Tools.printTitle("ConductorTest.testClone");
        Conductor cond1 = new Conductor(Size.AWG_8, Metal.ALUMINUM, Insul.XHHW, 123);
        Conduit conduit = new Conduit(Type.EMT, Conduit.Nipple.Yes);
        conduit.add(cond1);
        cond1.setAmbientTemperatureF(105);
        cond1.setCopperCoated(Coating.COATED);
        cond1.setRole(Conductor.Role.GND);
        System.out.println("Before changes");
        System.out.print("cond1:");
        String cond1S = cond1.getConduit()+", "+cond1.getCopperCoating()+", "+cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        System.out.println(cond1S);
        //--cloning
        Conductor cond2 = cond1.clone();
        System.out.print("cond2:");
        String cond2S = cond2.getConduit()+", "+cond2.getCopperCoating()+", "+cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();
        System.out.println(cond2S);

        cond2.setAmbientTemperatureF(155);
        cond2.setCopperCoated(Coating.UNCOATED);
        cond2.setRole(Conductor.Role.HOT);
        cond2.setSize(Size.AWG_4$0);
        cond2.setMetal(Metal.COPPER);
        cond2.setInsulation(Insul.TW);
        cond2.setLength(78);
        System.out.println();
        System.out.println("After changes to cond2");
        System.out.print("cond1:");
        String cond1SS = cond1.getConduit()+", "+cond1.getCopperCoating()+", "+cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        System.out.println(cond1SS);
        System.out.print("cond2:");
        String cond2SS = cond2.getConduit()+", "+cond2.getCopperCoating()+", "+cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();
        System.out.println(cond2SS);

        assertEquals(cond1S, cond1SS);
        assertNotEquals(cond2S, cond2SS);
    }

    @Test
    void getTemperatureRating() {
        Tools.printTitle("ConductorTest.getTemperatureRating");
        Conductor conductor = new Conductor();
        assertSame(conductor.getTemperatureRating(), TempRating.T75);
        conductor.setInsulation(Insul.XHHW2);
        assertSame(conductor.getTemperatureRating(), TempRating.T90);
        conductor.setInsulation(Insul.TW);
        assertSame(conductor.getTemperatureRating(), TempRating.T60);
    }

    @Test
    void setAmbientTemperatureF() {
        Tools.printTitle("ConductorTest.setAmbientTemperatureF");
        Bundle bundle1=new Bundle(null, 0, 30);
        Cable cable1 = new Cable();
        Cable cable2 = new Cable();
        Cable cable3 = new Cable();
        bundle1.add(cable1);
        bundle1.add(cable2);
        bundle1.add(cable3);
        assertEquals(86, cable3.getAmbientTemperatureF());

        cable1.setAmbientTemperatureF(100);
        assertEquals(100, cable1.getAmbientTemperatureF());
        assertEquals(100, cable2.getAmbientTemperatureF());
        assertEquals(100, cable3.getAmbientTemperatureF());

        cable2.leaveBundle();
        cable2.setAmbientTemperatureF(95);
        assertEquals(100, cable1.getAmbientTemperatureF());
        assertEquals(95, cable2.getAmbientTemperatureF());
        assertEquals(100, cable3.getAmbientTemperatureF());

    }

    @Test
    void getAmpacity() {
        Tools.printTitle("ConductorTest.getAmpacity");
        conductor = new Conductor(Size.AWG_12, Metal.COPPER, Insul.THHN, 125);
        conduit = new Conduit(Type.PVC80, Conduit.Nipple.No);
        conduit.add(conductor);
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        conductor.setAmbientTemperatureF(100);
        assertEquals(30*0.91*0.8, conductor.getAmpacity());

        bundle = new Bundle(null, 0, 25);
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        assertEquals(30*0.91*0.7, conductor.getAmpacity());
    }

    @Test
    void getCorrectionFactor() {
        Tools.printTitle("ConductorTest.getCorrectionFactor");
        conductor = new Conductor(Size.KCMIL_250, Metal.ALUMINUM, Insul.TW, 125);
        assertEquals(1, conductor.getCorrectionFactor());

        conductor.setAmbientTemperatureF(100);
        assertEquals(0.82, conductor.getCorrectionFactor(), 0.001);

        conduit = new Conduit(Type.PVC80, Conduit.Nipple.No);
        conduit.add(conductor);
        conduit.setRoofTopDistance(10);
        assertEquals(0.41, conductor.getCorrectionFactor(), 0.001);

        conductor.setInsulation(Insul.XHHW2);
        assertEquals(0.91, conductor.getCorrectionFactor(), 0.001);
    }

    @Test
    void getAdjustmentFactor() {
        Tools.printTitle("ConductorTest.getAdjustmentFactor");
        conductor = new Conductor(Size.AWG_4, Metal.COPPER, Insul.THW, 70);
        conduit = new Conduit(Type.EMT, Conduit.Nipple.No);
        conduit.add(conductor);
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        conduit.add(conductor.clone());
        assertEquals(0.8, conductor.getAdjustmentFactor());
        assertEquals(0.8, ((Conductor)conduit.getConduitables().get(2)).getAdjustmentFactor());

        conduit.setNipple(Conduit.Nipple.Yes);
        assertEquals(1, conductor.getAdjustmentFactor());

        conduit.setNipple(Conduit.Nipple.No);
        assertEquals(0.8, conductor.getAdjustmentFactor());

        conduit.getConduitables().get(2).leaveConduit();
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(3, conduit.getCurrentCarryingNumber());

        Conductor conductor1 = conductor.clone();
        conductor1.setConduit(conduit);
        conduit.add(conductor1.clone());
        conduit.add(conductor1.clone());
        conduit.add(conductor1.clone());
        assertEquals(7, conduit.getCurrentCarryingNumber());
        assertEquals(0.7, conductor.getAdjustmentFactor());

        conductor.leaveConduit();
        assertEquals(6, conduit.getCurrentCarryingNumber());
        assertEquals(0.8, conductor1.getAdjustmentFactor());

        conduit.empty();
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(1, conductor1.getAdjustmentFactor());
        assertFalse(conductor.hasConduit());
        assertFalse(conductor1.hasConduit());

        bundle = new Bundle(null, 0, 25);
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        assertEquals(25, bundle.getDistance());
        assertEquals(0.8, conductor.getAdjustmentFactor());

        conductor.leaveBundle();
        assertEquals(3, bundle.getCurrentCarryingNumber());
        assertEquals(1, conductor.getAdjustmentFactor());

        bundle.setDistance(24);
        assertEquals(1, conductor.getAdjustmentFactor());

        conduit.add(conductor1);
        assertTrue(conductor1.hasConduit());
        assertFalse(conductor1.hasBundle());
    }
}