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
        System.out.println("╔═════════════════════╗");
        System.out.println("║ getInsulatedAreaIn2 ║");
        System.out.println("╚═════════════════════╝");
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
        System.out.println("╔═════════════════════════╗");
        System.out.println("║ getCurrentCarryingCount ║");
        System.out.println("╚═════════════════════════╝");
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
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    @Test
    void getAmpacity() {
        System.out.println("╔═════════════╗");
        System.out.println("║ getAmpacity ║");
        System.out.println("╚═════════════╝");
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
        assertEquals(260*0.91*0.7, cable.getAmpacity(),0.01);
        cable.setType(Cable.Type.MC);
        conduit.remove(cable);
        cable.setBundlingExceeds20(true);
        cable.setBundlingDistanceExceeds24(true);
        assertEquals(260*0.91*0.60, cable.getAmpacity(),0.01);
        cable.setBundlingExceeds20(false);
        cable.setPhaseConductorSize(Size.AWG_12);
        cable.setMetal(Metal.COPPER);
        assertEquals(30*0.91*1.0, cable.getAmpacity());

        cable.setBundlingDistanceExceeds24(false);
        cable.setType(Cable.Type.NM);
        cable.setAmbientTemperatureF(65);
        cable.setInsulation(Insul.RHW);
        cable.setJacketed(true);
        assertEquals(25*1.11*1.0, cable.getAmpacity());
        cable.setBundlingExceeds20(true);
        cable.setBundlingDistanceExceeds24(true);
        assertEquals(25*1.11*1.0, cable.getAmpacity());//because it's not in a conduit
        cable.setJacketed(false);
        cable.setPhaseConductorSize(Size.AWG_12);
        assertEquals(25*1.11*1.0, cable.getAmpacity());//because it's #12awg
        /*todo test ampacity for when cable is MC and not jacketed and when jacketed*/
    }

    @Test
    void getPhaseConductorSize() {
        System.out.println("╔═══════════════════════╗");
        System.out.println("║ getPhaseConductorSize ║");
        System.out.println("╚═══════════════════════╝");
        cable = new Cable(SystemAC.Voltage.v277_1ph, SystemAC.Wires.W4, 1);
        cable.setPhaseConductorSize(Size.KCMIL_250);
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.KCMIL_300, cable.getPhaseConductorSize());
        cable.setNeutralConductorSize(Size.AWG_14);
        assertEquals(Size.AWG_14, cable.getPhaseConductorSize());
    }

    @Test
    void testClone() {
        System.out.println("╔═══════════╗");
        System.out.println("║ testClone ║");
        System.out.println("╚═══════════╝");
        Cable cable1 = new Cable(SystemAC.Voltage.v208_3ph, SystemAC.Wires.W4, 1.5);
        Conduit conduit = new Conduit(Type.EMT, Conduit.Nipple.Yes);
        conduit.add(cable1);
        cable1.setType(Cable.Type.NMS);
        cable1.setJacketed(true);
        cable1.setBundlingExceeds20(true);
        cable1.setBundlingDistanceExceeds24(true);
        cable1.setRoofTopDistance(20);
        cable1.setNeutralCarryingConductor(true);
        System.out.println("Before changes");
        System.out.print("cable1:");
        String cable1S = cable1.toString();
        System.out.println(cable1S);
        //--cloning
        Cable cable2 = cable1.clone();
        System.out.print("cable2:");
        String cable2S = cable2.toString();
        System.out.println(cable2S);

        cable2.setType(Cable.Type.AC);
        cable2.setJacketed(false);
        cable2.setBundlingExceeds20(false);
        cable2.setBundlingDistanceExceeds24(false);
        cable2.setRoofTopDistance(25);
        cable2.setNeutralCarryingConductor(false);
        cable2.setSystem(SystemAC.Voltage.v277_1ph, SystemAC.Wires.W4); //it should assume it as W2
        cable2.setOuterDiameter(0.5);

        System.out.println();
        System.out.println("After changes to cable2");
        System.out.print("cable1:");
        String cable1SS = cable1.toString();
        System.out.println(cable1SS);
        System.out.print("cable2:");
        String cable2SS = cable2.toString();
        System.out.println(cable2SS);

        assertEquals(cable1S, cable1SS);
        assertNotEquals(cable2S, cable2SS);
    }

    @Test
    void bundleAndConduit(){
        System.out.println("╔══════════════════╗");
        System.out.println("║ bundleAndConduit ║");
        System.out.println("╚══════════════════╝");
        Cable cable1 = new Cable();
        Bundle bundle1 = new Bundle(cable1, 5, 60);
        assertEquals(5, bundle1.getConduitables().size());
        assertFalse(bundle1.getConduitables().contains(cable1));

        bundle1.add(cable1);          //works with this one or
        //cable1.setBundle(bundle1);  //with this one
        assertEquals(6, bundle1.getConduitables().size());
        assertTrue(bundle1.getConduitables().contains(cable1));

        //bundle1.remove(cable1);   //works with this one or
        cable1.leaveBundle();       //with this one
        assertEquals(5, bundle1.getConduitables().size());
        assertFalse(bundle1.getConduitables().contains(cable1));

        cable1.setBundle(bundle1);
        assertEquals(6, bundle1.getConduitables().size());

        Cable cable2 = new Cable(SystemAC.Voltage.v480_3ph, SystemAC.Wires.W4, 5);
        Bundle bundle2 = new Bundle(cable2, 3, 35);
        bundle2.add(cable2);
        assertEquals(4, bundle2.getConduitables().size());

        //moving cable2 to bundle1
        bundle1.add(cable2);
        assertEquals(7, bundle1.getConduitables().size());
        assertTrue(bundle1.getConduitables().contains(cable2));
        assertFalse(bundle2.getConduitables().contains(cable2));
        assertTrue(cable2.getBundle()==bundle1);
        assertEquals(3, bundle2.getConduitables().size());

        //moving cable1 to bundle2
        cable1.setBundle(bundle2);
        assertEquals(6, bundle1.getConduitables().size());
        assertEquals(4, bundle2.getConduitables().size());
        assertTrue(bundle2.getConduitables().contains(cable1));
        assertFalse(bundle1.getConduitables().contains(cable1));
        assertTrue(cable1.getBundle()==bundle2);

        Conduit conduit1 = new Conduit(Type.PVC40, Conduit.Nipple.No);
        //moving cable1 to conduit1
        conduit1.add(cable1);
        assertTrue(conduit1.getConduitables().contains(cable1));
        assertFalse(bundle2.getConduitables().contains(cable1));
        assertTrue(cable1.hasConduit());
        assertFalse(cable1.hasBundle());
        assertTrue(cable1.getBundle()==null);
        assertTrue(cable1.getConduit()==conduit1);

        //moving cable2 to conduit1
        cable2.setConduit(conduit1);
        assertTrue(conduit1.getConduitables().contains(cable2));
        assertFalse(bundle1.getConduitables().contains(cable2));
        assertEquals(2, conduit1.getConduitables().size());
        assertEquals(5, bundle1.getConduitables().size());
        assertEquals(3, bundle2.getConduitables().size());
        assertTrue(cable2.hasConduit());
        assertFalse(cable2.hasBundle());
        assertTrue(cable2.getBundle()==null);
        assertTrue(cable2.getConduit()==conduit1);

        //putting cable1, cable2 and cable3 (first of bundle1) in free air.
        Cable cable3 = (Cable) bundle1.getConduitables().get(0);
        cable1.leaveConduit();
        cable2.leaveConduit();
        cable3.leaveBundle();
        //bundle1.remove(cable3);
        assertFalse(conduit1.getConduitables().contains(cable1));
        assertFalse(conduit1.getConduitables().contains(cable2));

        assertTrue(cable1.getBundle()==null);
        assertTrue(cable1.getConduit()==null);

        assertTrue(cable2.getBundle()==null);
        assertTrue(cable2.getConduit()==null);

        assertTrue(cable3.getBundle()==null);
        assertTrue(cable3.getConduit()==null);

        assertFalse(bundle1.getConduitables().contains(cable3));
        assertEquals(0, conduit1.getConduitables().size());
        assertEquals(4, bundle1.getConduitables().size());
        assertEquals(3, bundle2.getConduitables().size());

    }

}