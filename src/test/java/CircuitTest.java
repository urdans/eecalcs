package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.circuits.Load;
import eecalcs.circuits.Mode;
import eecalcs.circuits.Ocpd;
import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;
import test.Tools;
//import static test.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CircuitTest {
//    Load load = new Load();
    private Circuit circuit = new Circuit(new Load());
    private Conduit sharedConduit = new Conduit(Type.RMC, Conduit.Nipple.No);
/*    CircuitData circuitData;
    {
        try {
            circuitData = new CircuitData(circuit);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }*/

    //@Test
/*    void setupModelConductors() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Tools.printTitle("CircuitTest.setupModelConductors");

        assertEquals(0, circuit.resultMessages.getMessages().size());
        assertNotNull(circuitData.ocdp);
        assertEquals(circuit.getLoad(), circuitData.load);
        assertNotNull(circuitData.privateConduit);
        assertNull(circuitData.sharedConduit);
        assertNotNull(circuitData.privateBundle);
        assertNull(circuitData.sharedBundle);
        assertNotNull(circuitData.phaseAConductor);
        assertNull(circuitData.phaseBConductor);
        assertNull(circuitData.phaseCConductor);
        assertNotNull(circuitData.neutralConductor);
        assertNotNull(circuitData.groundingConductor);
        assertNotNull(circuitData.cable);
//        assertEquals(VoltageSystemAC.v120_1ph_2w, circuitData.systemVoltage);
        assertFalse(circuitData.usingCable);
        assertEquals(1, circuitData.numberOfSets);
        assertEquals(1, circuitData.setsPerConduit);
        assertEquals(3, circuitData.conductorsPerSet);
        assertEquals(3, circuitData.conduitables.size());

        circuitData.load.setSystemVoltage(VoltageSystemAC.v480_3ph_3w);
        circuitData.setupModelConductors();
        assertEquals(0, circuit.resultMessages.getMessages().size());
        assertNotNull(circuitData.phaseAConductor);
        assertNotNull(circuitData.phaseBConductor);
        assertNotNull(circuitData.phaseCConductor);
        assertNull(circuitData.neutralConductor);
        assertNotNull(circuitData.groundingConductor);
        assertEquals(4, circuitData.conductorsPerSet);
        assertEquals(4, circuitData.conduitables.size());

        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v480_3ph_4w);
        circuitData.setupModelConductors();
        assertEquals(0, circuit.resultMessages.getMessages().size());
        assertNotNull(circuitData.phaseAConductor);
        assertNotNull(circuitData.phaseBConductor);
        assertNotNull(circuitData.phaseCConductor);
        assertNotNull(circuitData.neutralConductor);
        assertNotNull(circuitData.groundingConductor);
        assertEquals(5, circuitData.conductorsPerSet);
        assertEquals(5, circuitData.conduitables.size());

        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v240_1ph_3w);
        circuitData.setupModelConductors();
        assertEquals(0, circuit.resultMessages.getMessages().size());
        assertNotNull(circuitData.phaseAConductor);
        assertNotNull(circuitData.phaseBConductor);
        assertNull(circuitData.phaseCConductor);
        assertNotNull(circuitData.neutralConductor);
        assertNotNull(circuitData.groundingConductor);
        assertEquals(4, circuitData.conductorsPerSet);
        assertEquals(4, circuitData.conduitables.size());
    }*/

/*    @Test
    void setNumberOfSets() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Tools.printTitle("CircuitTest.setNumberOfSets");
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuitData.setupModelConductors();
        assertEquals(1, circuitData.setsPerConduit);
        assertEquals(1, circuitData.numberOfSets);
        circuit.setNumberOfSets(2); //this keeps the number of sets per conduit to 1
        circuitData.getState();
        assertEquals(2, circuitData.numberOfSets);
        assertEquals(5, circuitData.conductorsPerSet);
        assertEquals(5, circuitData.conduitables.size());

        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v480_1ph_3w);
        circuitData.setupModelConductors();
        circuit.setNumberOfSets(3);
        circuitData.getState();
        assertEquals(3, circuit.getNumberOfConduits());
        assertEquals(1, circuitData.setsPerConduit);
        assertEquals(4, circuitData.conductorsPerSet);
        assertEquals(4, circuitData.conduitables.size());

        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v277_1ph_2w);
        circuitData.setupModelConductors();
        circuit.setNumberOfSets(1);
        circuitData.getState();
        assertEquals(1, circuit.getNumberOfConduits());
        assertEquals(1, circuitData.setsPerConduit);
        assertEquals(3, circuitData.conductorsPerSet);
        assertEquals(3, circuitData.conduitables.size());
    }*/

 /*   @Test
    void moreConduits() throws IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Tools.printTitle("CircuitTest.moreConduits");
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        assertEquals(1, circuit.getNumberOfConduits());
        assertEquals(1, circuit.getNumberOfSets());

        circuit.setNumberOfSets(10); //this keeps the number of sets per conduit to 1
        circuitData.setNumberOfConduits(1);
        circuitData.prepareConduitableList();
        assertEquals(1, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.moreConduits();
        assertEquals(2, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.moreConduits();
        assertEquals(5, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.moreConduits();
        assertEquals(10, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.moreConduits();
        assertEquals(10, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());
    }*/

    @Test
    void lessConduits(){
        Tools.printTitle("CircuitTest.lessConduits");
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        assertEquals(1, circuit.getNumberOfConduits());
        assertEquals(1, circuit.getNumberOfSets());

        circuit.setNumberOfSets(10); //this keeps the number of sets per conduit to 1
        assertEquals(10, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.lessConduits();
        assertEquals(5, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.lessConduits();
        assertEquals(2, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.lessConduits();
        assertEquals(1, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.lessConduits();
        assertEquals(1, circuit.getNumberOfConduits());
        assertEquals(10, circuit.getNumberOfSets());
    }

    @Test
    void getCircuitSize_Conductor_Private_Conduit(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Private_Conduit");
        //voltage drop decides
        assertEquals(Size.AWG_10, circuit.getCircuitSize());//selected per voltage drop

        circuit.getVoltageDrop().setMaxVoltageDropPercent(3.4); //selected per voltage drop
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.getVoltageDrop().setMaxVoltageDropPercent(5.2);//selected per ampacity and voltage drop
        assertEquals(Size.AWG_14, circuit.getCircuitSize());

        //ampacity decides
        circuit.getLoad().setCurrent(160);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(3);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize()); //using insulation for 75C

        circuit.getLoad().setCurrent(95);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(4.0);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(101);
        circuit.getPhaseConductor().setLength(130);
        circuit.getPhaseConductor().setMetal(Metal.ALUMINUM);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(10.0);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.getVoltageDrop().setMaxVoltageDropPercent(3.0);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        //elaborated tests. Complex scenarios
        //SELECTING CONDUCTOR PER AMPACITY ONLY
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25);
        circuit.getPhaseConductor().setLength(1);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v480_3ph_4w);
        circuit.getLoad().setVoltAmperes(87295.3607014714);
        circuit.setNeutralCurrentCarrying(true); //4 current-carrying
        circuit.getPhaseConductor().setAmbientTemperatureF(100);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setMetal(Metal.COPPER);
        assertEquals(105, circuit.getLoad().getMCA(), 0.01);
        assertEquals(Mode.PRIVATE_CONDUIT, circuit.getMode());
        assertEquals(4, circuit.getPrivateConduit().getCurrentCarryingNumber());

        circuit.getPhaseConductor().setSize(Size.AWG_1);
        assertEquals(0.91, circuit.getPhaseConductor().getCorrectionFactor());
        assertEquals(0.8, circuit.getPhaseConductor().getAdjustmentFactor());
        assertEquals(105.56, circuit.getPhaseConductor().getAmpacity(), 0.01);

        //termination temperature rating is unknown
        circuit.setTerminationTempRating(null);
        circuit.getLoad().setCurrent(105);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(100);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(414);
        assertEquals(Size.KCMIL_1250, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(14);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //termination temperature rating is known, T60
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getLoad().setCurrent(83);
        assertEquals(Size.AWG_3, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(353);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_900, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_1750, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(364);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(365);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setCurrent(468);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(469);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setCurrent(546);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(547);
        assertNull(circuit.getCircuitSize());

        circuit.setNeutralCurrentCarrying(false); //3 current-carrying
        circuit.getLoad().setCurrent(506);
        assertNull(circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
//        Tools.println("#CCC: " + circuit.getPrivateConduit().getCurrentCarryingNumber());
//        Tools.println("#conduits: " + circuit.getNumberOfConduits());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

        //termination temperature rating is known, T75
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v240_3ph_4w);
        circuit.getPhaseConductor().setAmbientTemperatureF(110);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getLoad().setCurrent(100);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.setNeutralCurrentCarrying(true);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(20);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.AWG_8, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_8, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getLoad().setCurrent(315);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(316);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertNull(circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(437);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setCurrent(523);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertNull(circuit.getCircuitSize());

        //termination temperature rating is known, T90
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v240_3ph_4w);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.setNeutralCurrentCarrying(true);

        circuit.getLoad().setCurrent(90);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(420);
        circuit.setNumberOfSets(2);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getPhaseConductor().setAmbientTemperatureF(95);
        circuit.getPhaseConductor().setMetal(Metal.ALUMINUM);

        circuit.getLoad().setCurrent(10);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        if(true)
            return;

        circuit.getCircuitSize();
        circuit.resultMessages.getMessages().forEach(x -> System.out.println(x.number + ": " + x.message));
    }

    @Test
    void getTradeSize(){
        Tools.printTitle("CircuitTest.getTradeSize");
        assertEquals(Trade.T1$2,circuit.getPrivateConduit().getTradeSize());

        circuit.setTerminationTempRating(null);
        circuit.setNumberOfSets(2);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v240_3ph_4w);
        circuit.getPhaseConductor().setAmbientTemperatureF(110);
        circuit.getPhaseConductor().setInsulation(Insul.THWN);
        circuit.getLoad().setCurrent(350);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(5.0);
        circuit.setNeutralCurrentCarrying(true);
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
        assertEquals(Size.KCMIL_300, circuit.getNeutralConductor().getSize());
        assertEquals(Size.AWG_12, circuit.getGroundingConductor().getSize());
        assertEquals(4, circuit.getPrivateConduit().getCurrentCarryingNumber());
        assertEquals(Type.PVC40, circuit.getPrivateConduit().getType());
        assertEquals(40, circuit.getPrivateConduit().getAllowedFillPercentage());
        assertEquals(1.8565, circuit.getPrivateConduit().getConduitablesArea());
        assertEquals(
                4.695,
                ConduitProperties.getArea(
                        circuit.getPrivateConduit().getType(),
                        Trade.T2_1$2
                )
        );
        assertEquals(Trade.T2_1$2, circuit.getPrivateConduit().getTradeSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_01(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_01");
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by voltage drop
        circuit.setFreeAirMode();
        assertEquals(Size.AWG_10, circuit.getCircuitSize());
//        Tools.println(circuit.getVoltageDrop().getACVoltageDropPercentage());
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_02(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_02");
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by ampacity
        circuit.setFreeAirMode();
        circuit.getVoltageDrop().setMaxVoltageDropPercent(6.0);
        assertEquals(Size.AWG_14, circuit.getCircuitSize());
//        Tools.println(circuit.getVoltageDrop().getACVoltageDropPercentage());
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_03(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_03");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(100);
        circuit.setNeutralCurrentCarrying(true);
        //even if there are 4 current-carrying conductor in free air, adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(15.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_04(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_04");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_05(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_05");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_06(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_06");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_07(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_07");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_08(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_08");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment  factor is 1.
        //This conductor is rated for 75°C but is permitted to be used for ampacity correction, adjustment, or both
        //(110.14(C), if the corrected and adjusted ampacity does not exceeds the ampacity for the temperature rating
        //of the termination.
        //lookup ampacity: 200/0.88=227.27
        //proposed size= 4/0
        //ampacity of proposed = 230
        //Corrected and adjusted ampacity of proposed: 230*0.88=202.40
        //Ampacity of a 4/0 at 60°C: 195
        //Since 202.40 exceeds 195, the proposed sized cannot be used. Therefore, the size of this THW conductor
        //is selected as if it was a TW (per column T60) after applying correction and adjustment factors.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_09(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_09");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment  factor is 1.
        //This conductor is rated for 90°C but is permitted to be used for ampacity correction, adjustment, or both
        //(110.14(C), if the corrected and adjusted ampacity does not exceeds the ampacity for the temperature rating
        //of the termination.
        //lookup ampacity: 200/0.91=219.78
        //proposed size= 3/0
        //ampacity of proposed = 225
        //Corrected and adjusted ampacity of proposed: 225*0.91=204.75
        //Ampacity of a 3/0 at 60°C: 165
        //Since 204.75 exceeds 165, the proposed sized cannot be used. Therefore, the size of this THHW conductor
        //is selected as if it was a TW (per column T60) after applying correction and adjustment factors.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_10(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_10");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T60 since the conductor temp rating is the least
        //temp rating of the two.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_11(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_11");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor equals the temp rating of the termination (T75). The size of the conductor
        //is selected per column T75.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_12(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_12");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment  factor is 1.
        //This conductor is rated for 90°C but is permitted to be used for ampacity correction, adjustment, or both
        //(110.14(C), if the corrected and adjusted ampacity does not exceeds the ampacity for the temperature rating
        //of the termination.
        //lookup ampacity: 200/0.91=219.78
        //proposed size= 3/0
        //ampacity of proposed = 225
        //Corrected and adjusted ampacity of proposed: 225*0.91=204.75
        //Ampacity of a 3/0 at 75°C: 200
        //Since 204.75 exceeds 200, the proposed sized cannot be used. Therefore, the size of this THHW conductor
        //is selected as if it was a THW (per column T75) after applying correction and adjustment factors.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_13(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_13");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T75 since the conductor temp rating is the
        //lesser temp rating of the two.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_14(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Free_Air_Case_14");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor equals the temp rating of the termination (T90). The size of the conductor
        //is selected per column T90.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_01(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_01");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(100);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_02(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_02");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(101);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_03(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_03");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_04(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_04");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_05(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_05");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getPhaseConductor().setInsulation(Insul.THW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_06(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_06");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(false);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_07(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Conduit_Case_07");
        Cable cable = new Cable(VoltageSystemAC.v480_3ph_4w, 0.5);
        cable.setNeutralCarryingConductor(true);
        Conductor conductor = new Conductor();
        sharedConduit.add(cable);
        sharedConduit.add(conductor);
        //the shared conduit has 5 CCC
        circuit.setConduitMode(sharedConduit);
        //the shared conduit has now 7 CCC
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getPhaseConductor().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(false);
        //the shared conduit has 8 CCC
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Private_Bundled_Cases(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Private_Bundled_Cases");
        //circuit have 4 ccc.
        circuit.setBundleMode();
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(87);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(18);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.getPhaseConductor().setLength(30);
        circuit.getPrivateBundle().setDistance(24);

//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + circuit.getPrivateBundle().getCurrentCarryingNumber());
//        Tools.println("        Distance: " + circuit.getPrivateBundle().getDistance());
//        Tools.println("complyWith310_15_B_3_a_4: " + circuit.getPrivateBundle().complyWith310_15_B_3_a_4());
//        Tools.println("complyWith310_15_B_3_a_5: " + circuit.getPrivateBundle().complyWith310_15_B_3_a_5());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasBundle());
//        Tools.println("            Size: " + size.getName());

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        circuit.getPrivateBundle().setDistance(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getLoad().setCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        //case 4
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Bundled_Cases(){
        Tools.printTitle("CircuitTest.getCircuitSize_Conductor_Shared_Bundled_Cases");
        Bundle bundle = new Bundle();//empty bundle
        //circuit have 4 ccc.
        circuit.setBundleMode(bundle);
        circuit.setTerminationTempRating(null);
        circuit.getPhaseConductor().setInsulation(Insul.TW);
        circuit.getPhaseConductor().setAmbientTemperatureF(87);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(18);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.getPhaseConductor().setLength(30);
        bundle.setDistance(24);

//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + bundle.getCurrentCarryingNumber());
//        Tools.println("        Distance: " + bundle.getDistance());
//        Tools.println("complyWith310_15_B_3_a_4: " + bundle.complyWith310_15_B_3_a_4());
//        Tools.println("complyWith310_15_B_3_a_5: " + bundle.complyWith310_15_B_3_a_5());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasBundle());
//        Tools.println("            Size: " + size.getName());

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        bundle.setDistance(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getLoad().setCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        //case 4
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());

        //case 5
        //bundle has other cables/conductors
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        cable.setSystem(VoltageSystemAC.v480_3ph_4w);//3ccc
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        //#ccc=9+4=13
        assertEquals(13, bundle.getCurrentCarryingNumber());

        circuit.setNeutralCurrentCarrying(false);
        assertEquals(12, bundle.getCurrentCarryingNumber());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setInsulation(Insul.THHW);
        circuit.getLoad().setCurrent(200);
        assertEquals(0.96, circuit.getPhaseConductor().getCorrectionFactor());
        assertEquals(0.50, circuit.getPhaseConductor().getAdjustmentFactor());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

    }

    @Test
    void getCircuitSize_Cable_Private_Conduit(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Private_Conduit");
        //voltage drop decides
        circuit.setUsingCable(true);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());//selected per voltage drop

        circuit.getVoltageDrop().setMaxVoltageDropPercent(3.4); //selected per voltage drop
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.getVoltageDrop().setMaxVoltageDropPercent(5.2);//selected per ampacity and voltage drop
        assertEquals(Size.AWG_14, circuit.getCircuitSize());

        //ampacity decides
        circuit.getLoad().setCurrent(160);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(3);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize()); //using insulation for 75C

        circuit.getLoad().setCurrent(95);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(4.0);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(101);
        circuit.getCable().setLength(130);
        circuit.getCable().setMetal(Metal.ALUMINUM);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(10.0);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.getVoltageDrop().setMaxVoltageDropPercent(3.0);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        //elaborated tests. Complex scenarios
        //SELECTING CONDUCTOR PER AMPACITY ONLY
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25);
        circuit.getCable().setLength(1);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v480_3ph_4w);
        circuit.getLoad().setVoltAmperes(87295.3607014714);
        circuit.setNeutralCurrentCarrying(true); //4 current-carrying
        circuit.getCable().setAmbientTemperatureF(100);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setMetal(Metal.COPPER);
        assertEquals(105, circuit.getLoad().getMCA(), 0.01);
        assertEquals(Mode.PRIVATE_CONDUIT, circuit.getMode());
        assertEquals(4, circuit.getPrivateConduit().getCurrentCarryingNumber());

        circuit.getCable().setPhaseConductorSize(Size.AWG_1);
        assertEquals(0.91, circuit.getCable().getCorrectionFactor());
        assertEquals(0.8, circuit.getCable().getAdjustmentFactor());
        assertEquals(105.56, circuit.getCable().getAmpacity(), 0.01);

        //termination temperature rating is unknown
        circuit.setTerminationTempRating(null);
        circuit.getLoad().setCurrent(105);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(100);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(414);
        assertEquals(Size.KCMIL_1250, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(14);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //termination temperature rating is known, T60
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getLoad().setCurrent(83);
        assertEquals(Size.AWG_3, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(353);
        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_900, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_1750, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(364);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(365);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setCurrent(468);
        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(469);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setCurrent(546);
        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(547);
        assertNull(circuit.getCircuitSize());

        circuit.setNeutralCurrentCarrying(false); //3 current-carrying
        circuit.getLoad().setCurrent(506);
        assertNull(circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

        //termination temperature rating is known, T75
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v240_3ph_4w);
        circuit.getCable().setAmbientTemperatureF(110);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getLoad().setCurrent(100);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.setNeutralCurrentCarrying(true);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(20);
        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.AWG_8, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_8, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getLoad().setCurrent(315);
        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(316);
        circuit.getCable().setInsulation(Insul.TW);
        assertNull(circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(437);
        circuit.getCable().setInsulation(Insul.THW);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setCurrent(523);
        circuit.getCable().setInsulation(Insul.THHW);
        assertNull(circuit.getCircuitSize());

        //termination temperature rating is known, T90
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v240_3ph_4w);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.setNeutralCurrentCarrying(true);

        circuit.getLoad().setCurrent(90);
        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setCurrent(420);
        circuit.setNumberOfSets(2);
        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getCable().setAmbientTemperatureF(95);
        circuit.getCable().setMetal(Metal.ALUMINUM);

        circuit.getLoad().setCurrent(10);
        circuit.getCable().setInsulation(Insul.TW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.getCable().setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setAmbientTemperatureF(110);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getCable().setMetal(Metal.COPPER);
        circuit.setNeutralCurrentCarrying(false);
        circuit.getLoad().setCurrent(2502);
        circuit.setNumberOfSets(6);
        assertEquals(6,circuit.getNumberOfConduits());
        assertEquals(1,circuit.getSetsPerConduit());
        Tools.println("NumberOfConduits: "+ circuit.getNumberOfConduits());
        Tools.println("SetsPerConduit: "+ circuit.getSetsPerConduit());
        Tools.println("CurrentCarryingNumber: "+ circuit.getPrivateConduit().getCurrentCarryingNumber());
        Tools.println("--------lessConduits----------");
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.lessConduits();
        assertEquals(3,circuit.getNumberOfConduits());
        assertEquals(2,circuit.getSetsPerConduit());
        Tools.println("NumberOfConduits: "+ circuit.getNumberOfConduits());
        Tools.println("SetsPerConduit: "+ circuit.getSetsPerConduit());
        Tools.println("CurrentCarryingNumber: "+ circuit.getPrivateConduit().getCurrentCarryingNumber());
        Tools.println("--------lessConduits----------");
        assertEquals(Size.KCMIL_1000, circuit.getCircuitSize());

        circuit.lessConduits();
        assertEquals(2,circuit.getNumberOfConduits());
        assertEquals(3,circuit.getSetsPerConduit());
        Tools.println("NumberOfConduits: "+ circuit.getNumberOfConduits());
        Tools.println("SetsPerConduit: "+ circuit.getSetsPerConduit());
        Tools.println("CurrentCarryingNumber: "+ circuit.getPrivateConduit().getCurrentCarryingNumber());
        Tools.println("--------lessConduits----------");
        assertEquals(Size.KCMIL_1500, circuit.getCircuitSize());


        circuit.lessConduits();
        assertEquals(1,circuit.getNumberOfConduits());
        assertEquals(6,circuit.getSetsPerConduit());
        assertNull(circuit.getCircuitSize());
        Tools.println("NumberOfConduits: "+ circuit.getNumberOfConduits());
        Tools.println("SetsPerConduit: "+ circuit.getSetsPerConduit());
        Tools.println("CurrentCarryingNumber: "+ circuit.getPrivateConduit().getCurrentCarryingNumber());
        Tools.println("--------moreConduits----------");

        circuit.moreConduits();
        assertEquals(2,circuit.getNumberOfConduits());
        assertEquals(3,circuit.getSetsPerConduit());
        Tools.println("NumberOfConduits: "+ circuit.getNumberOfConduits());
        Tools.println("SetsPerConduit: "+ circuit.getSetsPerConduit());
        Tools.println("CurrentCarryingNumber: "+ circuit.getPrivateConduit().getCurrentCarryingNumber());
        Tools.println("--------moreConduits----------");

        assertEquals(Size.KCMIL_1500, circuit.getCircuitSize());

        circuit.moreConduits();
        assertEquals(3,circuit.getNumberOfConduits());
        assertEquals(2,circuit.getSetsPerConduit());
        Tools.println("NumberOfConduits: "+ circuit.getNumberOfConduits());
        Tools.println("SetsPerConduit: "+ circuit.getSetsPerConduit());
        Tools.println("CurrentCarryingNumber: "+ circuit.getPrivateConduit().getCurrentCarryingNumber());
        Tools.println("------------------");
        assertEquals(Size.KCMIL_1000, circuit.getCircuitSize());

        circuit.getCircuitSize();
        circuit.resultMessages.getMessages().forEach(x -> System.out.println(x.number + ": " + x.message));
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_01(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_01");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(100);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_02(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_02");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(101);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_03(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_03");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_04(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_04");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_05(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_05");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getCable().setInsulation(Insul.THW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_06(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_06");
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(false);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_07(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Conduit_Case_07");
        Cable cable = new Cable(VoltageSystemAC.v480_3ph_4w, 0.5);
        cable.setNeutralCarryingConductor(true);
        Conductor conductor = new Conductor();
        sharedConduit.add(cable);
        sharedConduit.add(conductor);
        //the shared conduit has 5 CCC
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        //the shared conduit has now 7 CCC
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(102);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(false);
        //the shared conduit has 8 CCC
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + sharedConduit.getCurrentCarryingNumber());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Private_Bundled_Cases(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Private_Bundled_Cases");
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setBundleMode();
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(87);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(18);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.getCable().setLength(30);
        circuit.getPrivateBundle().setDistance(24);

//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + circuit.getPrivateBundle().getCurrentCarryingNumber());
//        Tools.println("        Distance: " + circuit.getPrivateBundle().getDistance());
//        Tools.println("complyWith310_15_B_3_a_4: " + circuit.getPrivateBundle().complyWith310_15_B_3_a_4());
//        Tools.println("complyWith310_15_B_3_a_5: " + circuit.getPrivateBundle().complyWith310_15_B_3_a_5());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasBundle());
//        Tools.println("            Size: " + size.getName());

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        circuit.getPrivateBundle().setDistance(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getLoad().setCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        //case 4
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Bundled_Cases(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Shared_Bundled_Cases");
        Bundle bundle = new Bundle();//empty bundle
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setBundleMode(bundle);
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(87);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(18);
        circuit.setNeutralCurrentCarrying(true);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
        circuit.getCable().setLength(30);
        bundle.setDistance(24);

//        Size size = circuit.getCircuitSize();
//        Tools.println("   Number of CCC: " + bundle.getCurrentCarryingNumber());
//        Tools.println("        Distance: " + bundle.getDistance());
//        Tools.println("complyWith310_15_B_3_a_4: " + bundle.complyWith310_15_B_3_a_4());
//        Tools.println("complyWith310_15_B_3_a_5: " + bundle.complyWith310_15_B_3_a_5());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasBundle());
//        Tools.println("            Size: " + size.getName());

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        bundle.setDistance(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getLoad().setCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        //case 4
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());

        //case 5
        //bundle has other cables/conductors
        Conductor conductor = new Conductor();
        Cable cable = new Cable();
        cable.setSystem(VoltageSystemAC.v480_3ph_4w);//3ccc
        bundle.add(conductor);
        bundle.add(conductor.clone());
        bundle.add(conductor.clone());
        bundle.add(cable);
        bundle.add(cable.clone());
        //#ccc=9+4=13
        assertEquals(13, bundle.getCurrentCarryingNumber());

        circuit.setNeutralCurrentCarrying(false);
        assertEquals(12, bundle.getCurrentCarryingNumber());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getLoad().setCurrent(200);
        assertEquals(0.96, circuit.getCable().getCorrectionFactor());
        assertEquals(0.50, circuit.getCable().getAdjustmentFactor());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_01(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_01");
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by voltage drop
        circuit.setFreeAirMode();
        circuit.setUsingCable(true);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());
//        Tools.println(circuit.getVoltageDrop().getACVoltageDropPercentage());
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_02(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_02");
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by ampacity
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.getVoltageDrop().setMaxVoltageDropPercent(6.0);
        assertEquals(Size.AWG_14, circuit.getCircuitSize());
//        Tools.println(circuit.getVoltageDrop().getACVoltageDropPercentage());
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_03(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_03");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(100);
        circuit.setNeutralCurrentCarrying(true);
        //even if there are 4 current-carrying conductor in free air, adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(15.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_04(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_04");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_05(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_05");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.getCable().setInsulation(Insul.THW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_06(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_06");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setUsingCable(true);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_07(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_07");
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setUsingCable(true);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println(circuit.getPhaseConductor().getAmpacity());
//        Tools.println(circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println(circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println(circuit.getPhaseConductor().hasConduit());
//        Tools.println(size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_08(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_08");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getCable().setInsulation(Insul.THW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment  factor is 1.
        //This conductor is rated for 75°C but is permitted to be used for ampacity correction, adjustment, or both
        //(110.14(C), if the corrected and adjusted ampacity does not exceeds the ampacity for the temperature rating
        //of the termination.
        //lookup ampacity: 200/0.88=227.27
        //proposed size= 4/0
        //ampacity of proposed = 230
        //Corrected and adjusted ampacity of proposed: 230*0.88=202.40
        //Ampacity of a 4/0 at 60°C: 195
        //Since 202.40 exceeds 195, the proposed sized cannot be used. Therefore, the size of this THW conductor
        //is selected as if it was a TW (per column T60) after applying correction and adjustment factors.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_09(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_09");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment  factor is 1.
        //This conductor is rated for 90°C but is permitted to be used for ampacity correction, adjustment, or both
        //(110.14(C), if the corrected and adjusted ampacity does not exceeds the ampacity for the temperature rating
        //of the termination.
        //lookup ampacity: 200/0.91=219.78
        //proposed size= 3/0
        //ampacity of proposed = 225
        //Corrected and adjusted ampacity of proposed: 225*0.91=204.75
        //Ampacity of a 3/0 at 60°C: 165
        //Since 204.75 exceeds 165, the proposed sized cannot be used. Therefore, the size of this THHW conductor
        //is selected as if it was a TW (per column T60) after applying correction and adjustment factors.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_10(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_10");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.TW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T60 since the conductor temp rating is the least
        //temp rating of the two.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_11(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_11");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.THW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor equals the temp rating of the termination (T75). The size of the conductor
        //is selected per column T75.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_12(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_12");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment  factor is 1.
        //This conductor is rated for 90°C but is permitted to be used for ampacity correction, adjustment, or both
        //(110.14(C), if the corrected and adjusted ampacity does not exceeds the ampacity for the temperature rating
        //of the termination.
        //lookup ampacity: 200/0.91=219.78
        //proposed size= 3/0
        //ampacity of proposed = 225
        //Corrected and adjusted ampacity of proposed: 225*0.91=204.75
        //Ampacity of a 3/0 at 75°C: 200
        //Since 204.75 exceeds 200, the proposed sized cannot be used. Therefore, the size of this THHW conductor
        //is selected as if it was a THW (per column T75) after applying correction and adjustment factors.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_13(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_13");
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getCable().setInsulation(Insul.THW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T75 since the conductor temp rating is the
        //lesser temp rating of the two.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_14(){
        Tools.printTitle("CircuitTest.getCircuitSize_Cable_Free_Air_Case_14");
        circuit.setFreeAirMode();
        circuit.setUsingCable(true);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getCable().setInsulation(Insul.THHW);
        circuit.getCable().setAmbientTemperatureF(96);
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setCurrent(200);
        circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor equals the temp rating of the termination (T90). The size of the conductor
        //is selected per column T90.
        circuit.getVoltageDrop().setMaxVoltageDropPercent(25.0);
//        Size size = circuit.getCircuitSize();
//        Tools.println("        Ampacity: " + circuit.getPhaseConductor().getAmpacity());
//        Tools.println("CorrectionFactor: " + circuit.getPhaseConductor().getCorrectionFactor());
//        Tools.println("AdjustmentFactor: " + circuit.getPhaseConductor().getAdjustmentFactor());
//        Tools.println("      hasConduit: " + circuit.getPhaseConductor().hasConduit());
//        Tools.println("            Size: " + size.getName());
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
    }







}
/*

class CircuitData{
    private Circuit circuit;

    public List<Conduitable> conduitables;
    public Ocpd ocdp;
    public Conduit privateConduit;
    public Conduit sharedConduit;
    public Bundle privateBundle;
    public Bundle sharedBundle;
    public Load load;
    public int numberOfSets;
    public int setsPerConduit;
    public int numberOfConduits;
    public int conductorsPerSet;
    //public VoltageSystemAC systemVoltage;
    public Conductor phaseAConductor;
    public Conductor phaseBConductor;
    public Conductor phaseCConductor;
    public Conductor neutralConductor;
    public Conductor groundingConductor;
    public Cable cable;
    public boolean usingCable;

    private Field conduitablesField;
    private Field ocdpField;
    private Field privateConduitField;
    private Field sharedConduitField;
    private Field privateBundleField;
    private Field sharedBundleField;
    private Field loadField;
    private Field numberOfSetsField;
    private Field setsPerConduitField;
    private Field numberOfConduitsField;
    private Field conductorsPerSetField;
//    private Field systemVoltageField;
    private Field phaseAConductorField;
    private Field phaseBConductorField;
    private Field phaseCConductorField;
    private Field neutralConductorField;
    private Field groundingConductorField;
    private Field cableField;
    private Field usingCableField;

    private Method setupModelConductors;
    private Method addTo1;
    private Method addTo2;
    private Method clearModeMsg;
    private Method getSizePerAmpacity;
    private Method getSizePerVoltageDrop;
    private Method leaveFrom1;
    private Method leaveFrom2;
    private Method prepareConduitableList;

    public CircuitData(Circuit circuit) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        this.circuit = circuit;

        conduitablesField = circuit.getClass().getDeclaredField("conduitables");
        ocdpField = circuit.getClass().getDeclaredField("ocdp");
        privateConduitField = circuit.getClass().getDeclaredField("privateConduit");
        sharedConduitField = circuit.getClass().getDeclaredField("sharedConduit");
        privateBundleField = circuit.getClass().getDeclaredField("privateBundle");
        sharedBundleField = circuit.getClass().getDeclaredField("sharedBundle");
        loadField = circuit.getClass().getDeclaredField("load");
        numberOfSetsField = circuit.getClass().getDeclaredField("numberOfSets");
        setsPerConduitField = circuit.getClass().getDeclaredField("setsPerConduit");
        numberOfConduitsField = circuit.getClass().getDeclaredField("numberOfConduits");
        conductorsPerSetField = circuit.getClass().getDeclaredField("conductorsPerSet");
//        systemVoltageField = circuit.getClass().getDeclaredField("systemVoltage");
        phaseAConductorField = circuit.getClass().getDeclaredField("phaseAConductor");
        phaseBConductorField = circuit.getClass().getDeclaredField("phaseBConductor");
        phaseCConductorField = circuit.getClass().getDeclaredField("phaseCConductor");
        neutralConductorField = circuit.getClass().getDeclaredField("neutralConductor");
        groundingConductorField = circuit.getClass().getDeclaredField("groundingConductor");
        cableField = circuit.getClass().getDeclaredField("cable");
        usingCableField = circuit.getClass().getDeclaredField("usingCable");

        conduitablesField.setAccessible(true);
        ocdpField.setAccessible(true);
        privateConduitField.setAccessible(true);
        sharedConduitField.setAccessible(true);
        privateBundleField.setAccessible(true);
        sharedBundleField.setAccessible(true);
        loadField.setAccessible(true);
        numberOfSetsField.setAccessible(true);
        setsPerConduitField.setAccessible(true);
        numberOfConduitsField.setAccessible(true);
        conductorsPerSetField.setAccessible(true);
//        systemVoltageField.setAccessible(true);
        phaseAConductorField.setAccessible(true);
        phaseBConductorField.setAccessible(true);
        phaseCConductorField.setAccessible(true);
        neutralConductorField.setAccessible(true);
        groundingConductorField.setAccessible(true);
        cableField.setAccessible(true);
        usingCableField.setAccessible(true);

        addTo1 = circuit.getClass().getDeclaredMethod("moveTo", Conduit.class);
        addTo2 = circuit.getClass().getDeclaredMethod("moveTo", Bundle.class);
        clearModeMsg = circuit.getClass().getDeclaredMethod("clearModeMsg", null);
        getSizePerAmpacity = circuit.getClass().getDeclaredMethod("getSizePerAmpacity", null);
        getSizePerVoltageDrop = circuit.getClass().getDeclaredMethod("getSizePerVoltageDrop", null);
        leaveFrom1 = circuit.getClass().getDeclaredMethod("leaveFrom", Conduit.class);
        leaveFrom2 = circuit.getClass().getDeclaredMethod("leaveFrom", Bundle.class);
        prepareConduitableList = circuit.getClass().getDeclaredMethod("prepareConduitableList", null);
        setupModelConductors = circuit.getClass().getDeclaredMethod("setupModelConductors", null);

        addTo1.setAccessible(true);
        addTo2.setAccessible(true);
        clearModeMsg.setAccessible(true);
        getSizePerAmpacity.setAccessible(true);
        getSizePerVoltageDrop.setAccessible(true);
        leaveFrom1.setAccessible(true);
        leaveFrom2.setAccessible(true);
        prepareConduitableList.setAccessible(true);
        setupModelConductors.setAccessible(true);

        getState();
    }

    public CircuitData getState() throws NoSuchFieldException, IllegalAccessException {
        conduitables = (List<Conduitable>) conduitablesField.get(circuit);
        ocdp = (Ocpd) ocdpField.get(circuit);
        privateConduit = (Conduit) privateConduitField.get(circuit);
        sharedConduit = (Conduit) sharedConduitField.get(circuit);
        privateBundle = (Bundle) privateBundleField.get(circuit);
        sharedBundle = (Bundle) sharedBundleField.get(circuit);
        load = (Load) loadField.get(circuit);
        numberOfSets = (int) numberOfSetsField.get(circuit);
        setsPerConduit = (int) setsPerConduitField.get(circuit);
        numberOfConduits = (int) numberOfConduitsField.get(circuit);
        conductorsPerSet = (int) conductorsPerSetField.get(circuit);
//        systemVoltage = (VoltageSystemAC) systemVoltageField.get(circuit);
        phaseAConductor = (Conductor) phaseAConductorField.get(circuit);
        phaseBConductor = (Conductor) phaseBConductorField.get(circuit);
        phaseCConductor = (Conductor) phaseCConductorField.get(circuit);
        neutralConductor = (Conductor) neutralConductorField.get(circuit);
        groundingConductor = (Conductor) groundingConductorField.get(circuit);
        cable = (Cable) cableField.get(circuit);
        usingCable = (boolean) usingCableField.get(circuit);
        return this;
    }

    public void setupModelConductors() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        setupModelConductors.invoke(circuit);
        getState();
    }

    public void addTo(Conduit conduit) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        addTo1.invoke(circuit, conduit);
        getState();
    }

    public void addTo(Bundle bundle) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        addTo2.invoke(circuit, bundle);
        getState();
    }

    public void clearModeMsg() throws InvocationTargetException, IllegalAccessException {
        clearModeMsg.invoke(circuit);
    }

    public void getSizePerAmpacity() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        getSizePerAmpacity.invoke(circuit);
        getState();
    }

    public void getSizePerVoltageDrop() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        getSizePerVoltageDrop.invoke(circuit);
        getState();
    }

    public void leaveFrom1() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        leaveFrom1.invoke(circuit);
        getState();
    }

    public void leaveFrom2() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        leaveFrom2.invoke(circuit);
        getState();
    }

    public void prepareConduitableList() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        prepareConduitableList.invoke(circuit);
        getState();
    }

    public void setNumberOfConduits(int numberOfConduits) throws IllegalAccessException {
        numberOfConduitsField.set(circuit, numberOfConduits);
    }
}*/
