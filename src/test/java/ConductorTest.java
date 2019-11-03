package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorTest {

    @Test
    void testClone() {
        System.out.println("╔═══════════╗");
        System.out.println("║ testClone ║");
        System.out.println("╚═══════════╝");
        Conductor cond1 = new Conductor(Size.AWG_8, Metal.ALUMINUM, Insul.XHHW, 123);
        Conduit conduit = new Conduit(Type.EMT, Conduit.Nipple.Yes);
        conduit.add(cond1);
        cond1.setAmbientTemperatureF(105);
        cond1.setCopperCoated(Coating.COATED);
        cond1.setRole(Conductor.Role.GND);
        cond1.setRoofTopDistance(20);
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
        cond2.setRoofTopDistance(25);
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
        System.out.println("╔══════════════════════╗");
        System.out.println("║ getTemperatureRating ║");
        System.out.println("╚══════════════════════╝");
        Conductor conductor = new Conductor();
        assertTrue(conductor.getTemperatureRating()== TempRating.T75);
        conductor.setInsulation(Insul.XHHW2);
        assertTrue(conductor.getTemperatureRating()== TempRating.T90);
        conductor.setInsulation(Insul.TW);
        assertTrue(conductor.getTemperatureRating()== TempRating.T60);
    }

    @Test
    void setAmbientTemperatureF() {
        System.out.println("╔════════════════════════╗");
        System.out.println("║ setAmbientTemperatureF ║");
        System.out.println("╚════════════════════════╝");
        Bundle bundle1=new Bundle(null, 0, 30);
        Cable cable1 = new Cable();
        Cable cable2 = new Cable();
        Cable cable3 = new Cable();
        bundle1.add(cable1);
        bundle1.add(cable2);
        bundle1.add(cable3);
        assertTrue(cable3.getAmbientTemperatureF()==86);

        cable1.setAmbientTemperatureF(100);
        assertTrue(cable1.getAmbientTemperatureF()==100);
        assertTrue(cable2.getAmbientTemperatureF()==100);
        assertTrue(cable3.getAmbientTemperatureF()==100);

        cable2.leaveBundle();
        cable2.setAmbientTemperatureF(95);
        assertTrue(cable1.getAmbientTemperatureF()==100);
        assertTrue(cable2.getAmbientTemperatureF()==95);
        assertTrue(cable3.getAmbientTemperatureF()==100);

    }

}