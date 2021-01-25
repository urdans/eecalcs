package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.circuits.CircuitMode;
import eecalcs.conduits.*;
import eecalcs.loads.Load;
import eecalcs.conductors.*;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;
import test.Tools;
//import tools.ResultMessage;
//import static test.Tools;

import java.text.NumberFormat;

import static org.junit.jupiter.api.Assertions.*;

class CircuitTest {
    private final Circuit circuit = new Circuit(new Load());
    private final Conduit sharedConduit = new Conduit(Type.RMC, false);
    private String getState(){
        Tools.startRecording();
        printState();
        return Tools.retrieveStateAndStopRecording();
    }
    private void printState(){
        Tools.println("\n");
        Tools.println("################################ OldLoad ################################");
        Tools.println("Voltage: " + circuit.getLoad().getVoltageSystem());
        Tools.println("OldLoad current: " + circuit.getLoad().getNominalCurrent());
        Tools.println("OldLoad MCA: " + circuit.getLoad().getMCA());
        Tools.println("OldLoad type: " + /*((GeneralOldLoad) */circuit.getLoad()/*)*/.getLoadType());
        Tools.println("OldLoad non-linear(harmonics): " + (circuit.getLoad()).isNonlinear());
        Tools.println("################################ Voltage Drop ################################");
        Size s = circuit.getSizePerVoltageDrop(false);
        Tools.println("Max voltage drop: " + circuit.getVoltageDrop().getMaxVoltageDropPercent());
        Tools.println("Size per voltage drop: " + s);
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        String vdp = nf.format(0.01*circuit.getVoltageDrop().getActualVoltageDropPercentageAC());
        Tools.println("Actual voltage drop: " + vdp);
        Tools.println("################################ Ampacity ################################");
        s = circuit.getSizePerAmpacity(false);
        Tools.println("Size per ampacity: " + s);
        Tools.println("################################ Circuit Conductor " +
                "Characteristics ################################");
        Tools.println("Number Of Sets: " + circuit.getNumberOfSets());
        Tools.println("Circuit size: " + circuit.getCircuitSize());
        Tools.println("Circuit ampacity: " + circuit.getCircuitAmpacity());
        Tools.println("Circuit length: " + circuit.getCircuitLength());
        Tools.println("Termination Temperature Rating: " + circuit.getTerminationTempRating());
        Tools.println("Using one EGC: " + circuit.isUsingOneEGC());
        if(circuit.isUsingCable()) {
            Tools.println("**** Using a cable ****");
            s = circuit.getCable().getNeutralConductorSize();
            Tools.println("Neutral size: " + (s == null? "No neutral" : s));
            Tools.println("EGC size: " + circuit.getCable().getGroundingConductorSize());
            Tools.println("Insulation: " + circuit.getCable().getInsulation().getName());
            Tools.println("Ambient Temperature: " + circuit.getCable().getAmbientTemperatureF());
            Tools.println("Temperature Rating: " + circuit.getCable().getTemperatureRating());
            Tools.println("Metal: " + circuit.getCable().getMetal());
            Tools.println("Correction Factor: " + circuit.getCable().getCorrectionFactor());
            Tools.println("Adjustment Factor: " + circuit.getCable().getAdjustmentFactor());
            Tools.println("Compound Factor: " + circuit.getCable().getCompoundFactor());
            Tools.println("Corrected and adjusted ampacity: " + circuit.getCable().getCorrectedAndAdjustedAmpacity());
            Tools.println("Nominal Ampacity: " + ConductorProperties.getAmpacity(
                    circuit.getCircuitSize(),
                    circuit.getCable().getMetal(),
                    circuit.getCable().getTemperatureRating()));
        }
        else {
            Tools.println("**** Using conductors ****");
            RoConductor RoC = circuit.getNeutralConductor();
            Tools.println("Neutral size: " + (RoC == null? "No neutral" :
                    RoC.getSize()));
            Tools.println("EGC size: " + circuit.getGroundingConductor().getSize());
            Tools.println("Insulation: " + circuit.getPhaseConductor().getInsulation().getName());
            Tools.println("Ambient Temperature: " + circuit.getPhaseConductor().getAmbientTemperatureF());
            Tools.println("Temperature Rating: " + circuit.getPhaseConductor().getTemperatureRating());
            Tools.println("Metal: " + circuit.getPhaseConductor().getMetal());
            Tools.println("Correction Factor: " + circuit.getPhaseConductor().getCorrectionFactor());
            Tools.println("Adjustment Factor: " + circuit.getPhaseConductor().getAdjustmentFactor());
            Tools.println("Compound Factor: " + circuit.getPhaseConductor().getCompoundFactor());
            Tools.println("Corrected and adjusted ampacity: " + circuit.getPhaseConductor().getCorrectedAndAdjustedAmpacity());
            Tools.println("Nominal Ampacity: " + ConductorProperties.getAmpacity(
                    circuit.getCircuitSize(),circuit.getPhaseConductor().getMetal(),
                    circuit.getPhaseConductor().getTemperatureRating()));
        }
        //PRIVATE_CONDUIT, FREE_AIR, SHARED_CONDUIT, PRIVATE_BUNDLE, SHARED_BUNDLE
        Tools.println("################################ Circuit OCPD ################################");
        Tools.println("OCPD rating: "+ circuit.getOcdp().getRating());
        Tools.println("OCPD is 100% rated: "+ circuit.getOcdp().is100PercentRated());
        Tools.println("################################ Circuit Conduit Characteristics ################################");
        if(circuit.getCircuitMode() == CircuitMode.PRIVATE_CONDUIT) {
            Tools.println("**** PRIVATE_CONDUIT ****");
            Tools.println("Number of conduits in this circuit: " + circuit.getNumberOfPrivateConduits());
            Tools.println("Is a nipple: " + circuit.getPrivateConduit().isNipple());
            Tools.println("Type: " + circuit.getPrivateConduit().getType());
            Tools.println("Minimum size: " + circuit.getPrivateConduit().getMinimumTrade());
            Tools.println("Actual size: " + circuit.getPrivateConduit().getTradeSize());
            Tools.println("Number of filling-counting Conductors: " + circuit.getPrivateConduit().getFillingConductorCount());
            Tools.println("Number of current carrying conductors: " + circuit.getPrivateConduit().getCurrentCarryingCount());
            Tools.println("Rooftop distance: " + circuit.getPrivateConduit().getRoofTopDistance());
            Tools.println("Internal area: " + circuit.getPrivateConduit().getArea());
            Tools.println("Total filled area: " + circuit.getPrivateConduit().getConduitablesArea());
            Tools.println("Max. allowed fill percentage: " + circuit.getPrivateConduit().getMaxAllowedFillPercentage());
            Tools.println("Fill percentage: " + nf.format(0.01*circuit.getPrivateConduit().getFillPercentage()));
        }
        else if(circuit.getCircuitMode() == CircuitMode.SHARED_CONDUIT) {
            Tools.println("**** SHARED_CONDUIT ****");
            Tools.println("Number of conduits in this circuit: " + circuit.getNumberOfPrivateConduits());
            Tools.println("Is a nipple: " + circuit.getSharedConduit().isNipple());
            Tools.println("Type: " + circuit.getSharedConduit().getType());
            Tools.println("Minimum size: " + circuit.getSharedConduit().getMinimumTrade());
            Tools.println("Actual size: " + circuit.getSharedConduit().getTradeSize());
            Tools.println("Number of filling-counting Conductors: " + circuit.getSharedConduit().getFillingConductorCount());
            Tools.println("Number of current carrying conductors: " + circuit.getSharedConduit().getCurrentCarryingCount());
            Tools.println("Rooftop distance: " + circuit.getSharedConduit().getRoofTopDistance());
            Tools.println("Internal area: " + circuit.getSharedConduit().getArea());
            Tools.println("Total filled area: " + circuit.getSharedConduit().getConduitablesArea());
            Tools.println("Max. allowed fill percentage: " + circuit.getSharedConduit().getMaxAllowedFillPercentage());
            Tools.println("Fill percentage: " + nf.format(0.01*circuit.getSharedConduit().getFillPercentage()));
        }
        else if(circuit.getCircuitMode() == CircuitMode.PRIVATE_BUNDLE) {
            Tools.println("**** PRIVATE_BUNDLE ****");
            Tools.println("Number of current-carrying conductors: " + circuit.getPrivateBundle().getCurrentCarryingCount());
            Tools.println("Bundle length: " + circuit.getPrivateBundle().getBundlingLength());

        }
        else if(circuit.getCircuitMode() == CircuitMode.SHARED_BUNDLE) {
            Tools.println("**** SHARED_BUNDLE ****");
            Tools.println("Number of current-carrying conductors: " + circuit.getSharedBundle().getCurrentCarryingCount());
            Tools.println("Bundle length: " + circuit.getSharedBundle().getBundlingLength());
        }
        else
            Tools.println("Free air mode");
        Tools.println("------------------------------ RESULT MESSAGES ------------------------------");
        circuit.getResultMessages().getMessages().forEach(message -> Tools.println(message.message+": "+message.number));
        Tools.println("");
        Tools.println("");
//        return "\n";
    }
    /*
    private String getState(){
        String msg = "\n################################ OldLoad " +
            "################################\n" +
            "Voltage: " + circuit.getLoad().getVoltageSystem()+"\n"+
            "OldLoad current: " + circuit.getLoad().getNominalCurrent()+"\n"+
            "OldLoad MCA: " + circuit.getLoad().getMCA()+"\n"+
            "OldLoad type: " + ((GeneralOldLoad) circuit.getLoad()).getLoadType()+"\n"+
            "OldLoad non-linear(harmonics): " + (circuit.getLoad()).isNonlinear()+"\n"+
            "################################ Voltage Drop " +
                "################################"+"\n";
        Size s = circuit.getSizePerVoltageDrop(false);
        msg = msg + "Max voltage drop: " + circuit.getVoltageDrop().getMaxVoltageDropPercent()+"\n"+
        "Size per voltage drop: " + s+"\n";
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        String vdp = nf.format(0.01*circuit.getVoltageDrop().getActualVoltageDropPercentageAC());
        msg = msg + "Actual voltage drop: " + vdp+"\n"+
        "################################ Ampacity " +
                "################################"+"\n";
        s = circuit.getSizePerAmpacity(false);
        msg = msg + "Size per ampacity: " + s +"\n"+
        "################################ Circuit Conductor " +
        "Characteristics ################################"+"\n"+
        "Number Of Sets: " + circuit.getNumberOfSets()+"\n"+
        "Circuit size: " + circuit.getCircuitSize()+"\n"+
        "Circuit ampacity: " + circuit.getCircuitAmpacity()+"\n"+
        "Circuit length: " + circuit.getCircuitLength()+"\n"+
        "Termination Temperature Rating: " + circuit.getTerminationTempRating()+"\n";
        if(circuit.isUsingCable()) {
            s = circuit.getCable().getNeutralConductorSize();
            msg = msg + "**** Using a cable ****" +"\n"+
            "Neutral size: " + (s == null? "No neutral" : s)+"\n"+
            "Insulation: " + circuit.getCable().getInsulation().getName()+"\n"+
            "Ambient Temperature: " + circuit.getCable().getAmbientTemperatureF()+"\n"+
            "Temperature Rating: " + circuit.getCable().getTemperatureRating()+"\n"+
            "Metal: " + circuit.getCable().getMetal()+"\n"+
            "Correction Factor: " + circuit.getCable().getCorrectionFactor()+"\n"+
            "Adjustment Factor: " + circuit.getCable().getAdjustmentFactor()+"\n"+
            "Compound Factor: " + circuit.getCable().getCompoundFactor()+"\n"+
            "Corrected and adjusted ampacity: " + circuit.getCable().getCorrectedAndAdjustedAmpacity()+"\n"+
            "Nominal Ampacity: " + ConductorProperties.getAmpacity(
                    circuit.getCircuitSize(),
                    circuit.getCable().getMetal(),
                    circuit.getCable().getTemperatureRating())+"\n";
        }
        else {
            RoConductor RoC = circuit.getNeutralConductor();
            msg = msg + "**** Using conductors ****" +"\n"+
            "Neutral size: " + (RoC == null? "No neutral" : RoC.getSize())+"\n"+
            "Insulation: " + circuit.getPhaseConductor().getInsulation().getName()+"\n"+
            "Ambient Temperature: " + circuit.getPhaseConductor().getAmbientTemperatureF()+"\n"+
            "Temperature Rating: " + circuit.getPhaseConductor().getTemperatureRating()+"\n"+
            "Metal: " + circuit.getPhaseConductor().getMetal()+"\n"+
            "Correction Factor: " + circuit.getPhaseConductor().getCorrectionFactor()+"\n"+
            "Adjustment Factor: " + circuit.getPhaseConductor().getAdjustmentFactor()+"\n"+
            "Compound Factor: " + circuit.getPhaseConductor().getCompoundFactor()+"\n"+
            "Corrected and adjusted ampacity: " + circuit.getPhaseConductor().getCorrectedAndAdjustedAmpacity()+"\n"+
            "Nominal Ampacity: " + ConductorProperties.getAmpacity(
                    circuit.getCircuitSize(),circuit.getPhaseConductor().getMetal(),
                    circuit.getPhaseConductor().getTemperatureRating())+"\n";
        }
        //PRIVATE_CONDUIT, FREE_AIR, SHARED_CONDUIT, PRIVATE_BUNDLE, SHARED_BUNDLE
        Tools.println("################################ Circuit Conduit Characteristics ################################");
        if(circuit.getCircuitMode() == CircuitMode.PRIVATE_CONDUIT) {
            Tools.println("**** PRIVATE_CONDUIT ****");
            Tools.println("Number of conduits in this circuit: " + circuit.getNumberOfPrivateConduits());
            Tools.println("Is a nipple: " + circuit.getPrivateConduit().isNipple());
            Tools.println("Type: " + circuit.getPrivateConduit().getType());
            Tools.println("Minimum size: " + circuit.getPrivateConduit().getMinimumTrade());
            Tools.println("Actual size: " + circuit.getPrivateConduit().getTradeSize());
            Tools.println("Number of filling-counting Conductors: " + circuit.getPrivateConduit().getFillingConductorCount());
            Tools.println("Number of current carrying conductors: " + circuit.getPrivateConduit().getCurrentCarryingCount());
            Tools.println("Rooftop distance: " + circuit.getPrivateConduit().getRoofTopDistance());
            Tools.println("Internal area: " + circuit.getPrivateConduit().getArea());
            Tools.println("Total filled area: " + circuit.getPrivateConduit().getConduitablesArea());
            Tools.println("Max. allowed fill percentage: " + circuit.getPrivateConduit().getMaxAllowedFillPercentage());
            Tools.println("Fill percentage: " + nf.format(0.01*circuit.getPrivateConduit().getFillPercentage()));
        }
        else if(circuit.getCircuitMode() == CircuitMode.SHARED_CONDUIT) {
            Tools.println("**** SHARED_CONDUIT ****");
            Tools.println("Number of conduits in this circuit: " + circuit.getNumberOfPrivateConduits());
            Tools.println("Is a nipple: " + circuit.getSharedConduit().isNipple());
            Tools.println("Type: " + circuit.getSharedConduit().getType());
            Tools.println("Minimum size: " + circuit.getSharedConduit().getMinimumTrade());
            Tools.println("Actual size: " + circuit.getSharedConduit().getTradeSize());
            Tools.println("Number of filling-counting Conductors: " + circuit.getSharedConduit().getFillingConductorCount());
            Tools.println("Number of current carrying conductors: " + circuit.getSharedConduit().getCurrentCarryingCount());
            Tools.println("Rooftop distance: " + circuit.getSharedConduit().getRoofTopDistance());
            Tools.println("Internal area: " + circuit.getSharedConduit().getArea());
            Tools.println("Total filled area: " + circuit.getSharedConduit().getConduitablesArea());
            Tools.println("Max. allowed fill percentage: " + circuit.getSharedConduit().getMaxAllowedFillPercentage());
            Tools.println("Fill percentage: " + nf.format(0.01*circuit.getSharedConduit().getFillPercentage()));
        }
        else if(circuit.getCircuitMode() == CircuitMode.PRIVATE_BUNDLE) {
            Tools.println("**** PRIVATE_BUNDLE ****");
            Tools.println("Number of current-carrying conductors: " + circuit.getPrivateBundle().getCurrentCarryingCount());
            Tools.println("Bundle length: " + circuit.getPrivateBundle().getBundlingLength());

        }
        else if(circuit.getCircuitMode() == CircuitMode.SHARED_BUNDLE) {
            Tools.println("**** SHARED_BUNDLE ****");
            Tools.println("Number of current-carrying conductors: " + circuit.getSharedBundle().getCurrentCarryingCount());
            Tools.println("Bundle length: " + circuit.getSharedBundle().getBundlingLength());
        }
        else
            Tools.println("Free air mode");
        Tools.println("------------------------------ RESULT MESSAGES ------------------------------");
        circuit.getResultMessages().getMessages().forEach(message -> System.out.println(message.message+": "+message.number));
        Tools.println("");
        Tools.println("");
        return msg;
    }
     */
/*    private void printResultMessages(){
        for(ResultMessage m: circuit.resultMessages.getMessages()){
            Tools.println(m.number + ": " + m.message);
        }
    }*/
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
        assertEquals(3, circuit.getNumberOfPrivateConduits());
        assertEquals(1, circuitData.setsPerConduit);
        assertEquals(4, circuitData.conductorsPerSet);
        assertEquals(4, circuitData.conduitables.size());

        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v277_1ph_2w);
        circuitData.setupModelConductors();
        circuit.setNumberOfSets(1);
        circuitData.getState();
        assertEquals(1, circuit.getNumberOfPrivateConduits());
        assertEquals(1, circuitData.setsPerConduit);
        assertEquals(3, circuitData.conductorsPerSet);
        assertEquals(3, circuitData.conduitables.size());
    }*/
/*   @Test
    void morePrivateConduits() throws IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Tools.printTitle("CircuitTest.morePrivateConduits");
        circuit.getLoad().setSystemVoltage(VoltageSystemAC.v208_3ph_4w);
        assertEquals(1, circuit.getNumberOfPrivateConduits());
        assertEquals(1, circuit.getNumberOfSets());

        circuit.setNumberOfSets(10); //this keeps the number of sets per conduit to 1
        circuitData.setNumberOfConduits(1);
        circuitData.prepareConduitableList();
        assertEquals(1, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.morePrivateConduits();
        assertEquals(2, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.morePrivateConduits();
        assertEquals(5, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.morePrivateConduits();
        assertEquals(10, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());

        circuit.morePrivateConduits();
        assertEquals(10, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
    }*/

    /*This test is to find out if the all circuit methods are congruent, that
     is, there is consistency between all these methods' returned values*/
    @Test
    void congruencyTest(){
        /*All the objects that belong to the Circuit class should be under
        control of that class. If Circuit uses an object like OldLoad for
        example, it should be able to provide proper state even when the
        state of the load object changes.
        These tests are meant to verify congruency.*/
        //The default load current is 10 amps
        assertEquals(10, circuit.getLoad().getNominalCurrent());

        /*The voltage drop is governing and the proper size is #10.*/
        assertEquals(Size.AWG_10, circuit.getPhaseConductor().getSize());

        /*if I change the load nominal current, the circuit object size state
        must correspond to the load state.*/
        circuit.getLoad().setNominalCurrent(100);
        //the size of the phase conductor must correspond to that current
        assertEquals(Size.AWG_1, circuit.getPhaseConductor().getSize());
        //the same applies for the neutral conductor
        assertEquals(Size.AWG_1, circuit.getNeutralConductor().getSize());
        //if I change the voltage system in the load to 3φ...
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_3w);
        /*there must not be a neutral conductor anymore.*/
        assertNull(circuit.getNeutralConductor());
        //the size of the phase must still be the same.
        assertEquals(Size.AWG_1, circuit.getPhaseConductor().getSize());
        /*todo:
        *  test the OCPD
        *  test the grounding conductor
        *  test the conduit*/
        //OCPD
        /*When the size of the OCPD is selected for protecting the conductor
        (and not because the load is imposing a certain value) the ampacity
        of the conductor must be calculated by considering all the
        installation conditions, including the temperature rating of the
        terminations and the continuous behavior of the load.*/
        /*My challenge now is to program a new method, or modify an existing, to
         determine the ampacity of a circuit conductors per conditions of use.
         The candidate I have in mind in the ROConduitable.getAmpacity()
         method which I should modify to account to the temp of the
         terminations. There is a parameter (I don't recall which one) that
         depends on the circuit and maybe I could not use this method, in
         which case I should create a new one as a member of the class circuit.
         The question is everytime I need to know the ampacity of a
         conductor, do I need to provide all the conditions of use?

         The answer is: the method computing the ampacity under all
         conditions of use was added to the Circuit class, which knows all
         the conditions, including the continuous behavior of the load.

         There were oscillation on the calculation of the size of conductor
         per ampacity. I was addressing that oscillation. Did I finish it?
         How is that oscillation, why is it produced?

         Answer: the oscillation was a fantasy. When computing the size of a
         conductor under conditions of use, the parameter to consider is the
         amperes of the load, since the since itself is not known. After
         computing this size, I was considering if it was bigger than 1 AWG
         and recomputing again, and this brought this oscillation on board.

         But again, since the code requires the amperes of the load OR the
         size of the conductor to be accounted for, the re-computation is not
         required and the oscillation disappeared.
         */
        assertEquals(110, circuit.getOcdp().getRating());

        //Quedé aquí 3
        /*
        I need to implement the Circuit.calculateEGC() method before
        proceeding with this test. The size of the equipment grounding
        conductor depends on the rating of the OCPD and on the way the sets
        are arranged in parallel (all sets in one conduit, single set per
        conduit or multiple sets per conduit). Read about this NEC 250.120.
        Size of the EGC must be increased if voltage drop enforces increasing
        of size of the phase conductors. Other reasons for increasing the
        size of the phase may apply. Do the correction factors apply? No!
        -If one ECG is installed with multiple circuit in the same raceway,
        cable, or cable tray, the size must be selected based on the largest
        OCPD.
        -If the load is a motor, apply particular rule 250.122(D)(2)
        -When using cord and fixture wire, use rule 250.122(E)
        -Circuits in parallel: rule 250.122(F)(1) or (2):
        ****this rule changed in the NEC 2017************
            -single raceway: single EGC is permitted based on table 250.122.
            -multiple raceways: each raceway must have an EGC sized per 250.122

         */
        //grounding
        assertEquals(Size.AWG_6, circuit.getGroundingConductor().getSize(),
                getState());

        //conduit
        assertEquals(Trade.T1_1$2, circuit.getPrivateConduit().getTradeSize(),
                getState());
/*To solve this incongruity, the class circuit must be refactored as follows:
1. Once the class is created, any change to its primitive or simple members
through setters must change the state of the class, including the state of
all its object (non primitive) members.

The following are the primitives to watch:
    -setBundleMode()                                                   -->Tested
    -setBundleMode(Bundle bundle)                                      -->Tested
    -setCircuitAmbientTemperatureF(int temperature)        --> to be more tested
    -setCircuitLength(double length)                       --> to be more tested
    -setConduitMode()                                                  -->Tested
    -setConduitMode(Conduit conduit)                                   -->Tested
    -setFreeAirMode()                                                  -->Tested
    -setNumberOfSets(int numberOfSets)                                 -->Tested
    -setTerminationTempRating(TempRating terminationTempRating)        -->Tested
    -setUsingCable(boolean usingCable)                                 -->Tested

2. The circuit object members that are exposed through getters must be
observed, so if they change state, the circuit must also change state, to
avoid loosing synchrony.

One way to avoid it is to provide a read-only interface to the exposed
objects. Another, is to fully observe the changes to those objects.

The following are the getters that expose such objects:
*   -ROCable getCable()                                   -->To hide more
*       ->setAmbientTemperatureF(int ambientTemperatureF)-->hide&move to Circuit
*    ->use setCircuitAmbientTemperatureF and rename it to setAmbientTemperatureF
*       ->setInsulation(Insul insul)                     -->hide&move to Circuit
*       ->setLength(double length)                       -->hide&move to Circuit
*            ->use setCircuitLength and rename it to setLength
*       ->setMetal(Metal metal)                          -->hide&move to Circuit
*       ->setPhaseConductorSize(Size size)     ->WEIRD->### what is this for?###
*            (why do I need to manually set the size of the circuit conductors?)
*             Try not to use it.
*   -ROConduitable getConduitable()                     -->full read-only
*    -RoConductor getGroundingConductor()                  -->To hide more
*       ->Conductor setSize(Size size)         ->WEIRD->### what is this for?###
*            (why do I need to manually set the size of the circuit conductors?)
*            Try not to use it.
*       ->Conductor setMetal(Metal metal)                -->hide&move to Circuit
*       ->setInsulation(Insul insulation)                -->hide&move to Circuit
*       ->setLength(double length)                       -->hide&move to Circuit
*       ->setAmbientTemperatureF(int ambientTemperatureF)-->hide&move to Circuit
*   -OldLoad getLoad()                                      -->to be fully observed
*       ->setDescription(String description)      ->make it notify all listeners
*   -OCPD getOcdp()                               ->make it notify all listeners
*   -RoConductor getNeutralConductor()                  -->same as before
*   -RoConductor getPhaseConductor()                    -->same as before
*   -ROBundle getPrivateBundle()                          -->To hide more
*       ->setDistance(double distance)         ->WEIRD->### what is this for?###
*               (refactor name to setPrivateBundleLength, hide&move to circuit?)
*   -ROConduit getPrivateConduit()                        -->To hide more
*       ->setMinimumTrade(Trade minimumTrade)            -->hide&move to Circuit
*       ->setType(Type type)                             -->hide&move to Circuit
*       ->setNipple(Conduit.Nipple nipple)               -->hide&move to Circuit
*       ->setRoofTopDistance(double roofTopDistance)     -->hide&move to Circuit
*       ->resetRoofTop()                                 -->hide&move to Circuit
*   -ROBundle getSharedBundle()                         -->same as before
*   -ROConduit getSharedConduit()                       -->same as before
*   -ROVoltDrop getVoltageDrop()                          -->To hide more
*       ->setMaxVoltageDropPercent(double)               -->hide&move to Circuit
    todo:
    -ResultMessages resultMessages  -->add read-only interface, refactor all
    classes using it so they expose the read-only interface to this object.


** Hiding and moving to Circuit is less work to do, less complex.
** Notifying al listeners require more lines of code but the advantage is
that it does not add more complexity (methods) to the circuit class and does
not require to create a read-only interface.

Internal objects belonging to the Circuit class that are exposed:
1. The RO cable object that represents all cables.
2. The RO conductor object that represents all the phase conductors.
3. The RO conductor object that represents all the neutral conductors.
4. The RO conductor object that represents all the ground conductors.
5. The RO conduitable object that represents all cables or all conductors.
6. The OCPD object.
7. The private RO bundle, if used.
8. The private RO conduit, if used.
9. The shared RO bundle, if used.
10.The shared RO conduit, is used.
11.The RO voltageDrop object used for internal calculations.

The calculation of the circuit consists of:
a.setupModelConductors:
  -creates the objects listed in 1~3.
  -calls prepareConduitableList()
  -Depends on:
    -voltage system of the load
    -if using cable or conductors
  ==>call this if:
  *-the voltage system of the load changes (change is only possible through the
    load object, which is now fully observed)
  *-the type of wiring changes, through setUsingCable. Implemented.

b.prepareConduitableList:
  -creates the private conduitable object list that will make up the circuit.
  -calls setMode()
  -depends on:
    -conductorsPerSet (changes only in SetupModelConductors)
    -setsPerConduit
  ==>call this if:
  -don't call it directly, it will be called from SetupModelConductors(),
   morePrivateConduits(), lessPrivateConduits() and setNumberOfSets() (these last 3 change
   the setsPerConduit state)

c.setMode()
  -set up the circuit for the corresponding mode.
  -calls setFreeAirMode(), setConduitMode(), setConduitMode(sharedConduit),
   setBundleMode(), setBundleMode(sharedBundle)
  -calls calculateCircuit().
  -depends on:
    -the circuit mode.
  ==>call this if:
  -don't call it.

Implementations to be done:
-Create a method calculateCircuit() that: (or the existing getCircuitSize)
    .Calculates the biggest hot wire size per both ampacity and voltage drop;
    .Calculates the neutral conductor, if used.
    .Calculates the rating of the OCPD.
    .Calculates the size of the EGC
    .Updates all the conductors of the circuit to this wire size.
    .Calculates the size of the conduit, if used.
    .At this point if no error has been detected, a circuitChanged flag is
    cleared.

This method should be private and called only when a circuitStateChanged flag
is true. All the methods or objects that change the state of the circuit must
set this flag as an indication that the circuit needs to be recalculated.
When the class is created, just after all setup is achieved calculatedCircuit
must be called.

ALl the parameters of the circuit are obtained through the read only objects.

*/
    }

    @Test
    void multipleEGCPerConduitWhenMoreThanOneConduit_PrivateMode(){
        circuit.getLoad().setNominalCurrent(300);
        circuit.setNumberOfSets(2);
//        printState();
        circuit.morePrivateConduits();
//        printState();
//Quedé aquí 2
    }

    @Test
    void lessAndMoreConduits(){
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        assertEquals(1, circuit.getNumberOfPrivateConduits());
        assertEquals(1, circuit.getNumberOfSets());
        assertEquals(1, circuit.getSetsPerPrivateConduit());

        circuit.setNumberOfSets(10);
        assertEquals(1, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(10, circuit.getSetsPerPrivateConduit());


        circuit.morePrivateConduits();
        assertEquals(2, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(5, circuit.getSetsPerPrivateConduit());

        circuit.morePrivateConduits();
        assertEquals(5, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(2, circuit.getSetsPerPrivateConduit());

        circuit.morePrivateConduits();
        assertEquals(10, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(1, circuit.getSetsPerPrivateConduit());

        circuit.lessPrivateConduits();
        assertEquals(5, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(2, circuit.getSetsPerPrivateConduit());

        circuit.lessPrivateConduits();
        assertEquals(2, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(5, circuit.getSetsPerPrivateConduit());

        circuit.lessPrivateConduits();
        assertEquals(1, circuit.getNumberOfPrivateConduits());
        assertEquals(10, circuit.getNumberOfSets());
        assertEquals(10, circuit.getSetsPerPrivateConduit());
    }

    @Test
    void getCircuitSize_Conductor_Private_Conduit(){
        //voltage drop decides
        assertEquals(Size.AWG_10, circuit.getCircuitSize());//selected per voltage drop

        circuit.setMaxVoltageDropPercent(3.4); //selected per voltage drop
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setMaxVoltageDropPercent(5.2);//selected per ampacity and voltage drop
        assertEquals(Size.AWG_14, circuit.getCircuitSize());

        //ampacity decides
        circuit.getLoad().setNominalCurrent(160);
        circuit.setMaxVoltageDropPercent(3);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize()); //using insulation for 75C

        circuit.getLoad().setNominalCurrent(95);
        circuit.setMaxVoltageDropPercent(4.0);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(101);
        circuit.setLength(130);
        circuit.setMetal(Metal.ALUMINUM);
        circuit.setMaxVoltageDropPercent(10.0);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.setMaxVoltageDropPercent(3.0);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        //elaborated tests. Complex scenarios
        //SELECTING CONDUCTOR PER AMPACITY ONLY
        circuit.setMaxVoltageDropPercent(25);
        circuit.setLength(1);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v480_3ph_4w);
        circuit.getLoad().setNominalCurrent(105);
        circuit.getLoad().setNonlinear(true);
        circuit.setAmbientTemperatureF(100);
        circuit.setInsulation(Insul.THHW);
        circuit.setMetal(Metal.COPPER);

        assertEquals(105, circuit.getLoad().getMCA(), 0.01);
        assertEquals(CircuitMode.PRIVATE_CONDUIT, circuit.getCircuitMode());
        assertEquals(4, circuit.getPrivateConduit().getCurrentCarryingCount());
        assertEquals(0.91, circuit.getPhaseConductor().getCorrectionFactor());
        assertEquals(0.8, circuit.getPhaseConductor().getAdjustmentFactor());
        //todo the correct one here is 149.148amps, not 123.76
        //update once getAmpacity is revised to account for the termination
        // temp. ratings.
        assertEquals(/*105.56*//*163.8*/123.76, circuit.getPhaseConductor().getCorrectedAndAdjustedAmpacity(), 0.01);

        //termination temperature rating is unknown
        circuit.setTerminationTempRating(null);
        circuit.getLoad().setNominalCurrent(105);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(100);
        assertEquals(Size.AWG_3$0/*Size.AWG_1$0*/, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(414);
        assertEquals(Size.KCMIL_1250, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(14);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //termination temperature rating is known, T60
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getLoad().setNominalCurrent(83);
        assertEquals(Size.AWG_3, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(353);
        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_900, circuit.getCircuitSize());

        circuit.setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_1750, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(364);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(365);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(468);
        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(469);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(546);
        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(547);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNonlinear(false);
        circuit.getLoad().setNominalCurrent(506);
        assertNull(circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        //termination temperature rating is known, T75
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v240_3ph_4w);
        circuit.setAmbientTemperatureF(110);
        circuit.setInsulation(Insul.TW);
        circuit.getLoad().setNominalCurrent(100);
        circuit.setMaxVoltageDropPercent(25.0);

        circuit.getLoad().setNonlinear(true);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(20);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_8, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.THHW);
        assertEquals(/*Size.AWG_8*/Size.AWG_10, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getLoad().setNominalCurrent(315);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(316);
        circuit.setInsulation(Insul.TW);
        assertNull(circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(437);
        circuit.setInsulation(Insul.THW);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(523);
        circuit.setInsulation(Insul.THHW);
        assertNull(circuit.getCircuitSize());

        //termination temperature rating is known, T90
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v240_3ph_4w);
        circuit.setAmbientTemperatureF(102);
        circuit.setMaxVoltageDropPercent(25.0);
        circuit.getLoad().setNonlinear(true);
        circuit.getLoad().setNominalCurrent(90);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(420);
        circuit.setNumberOfSets(2);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.setAmbientTemperatureF(95);
        circuit.setMetal(Metal.ALUMINUM);
        circuit.getLoad().setNominalCurrent(10);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_no_ampacity_due_to_ambient_temp_equal_conductor_temp_rating(){
        //circuit cannot be sized because the ambient temperature is above or near the
        //temperature rating of the conductor, so TABLE 310.15(B)(2)(a) gives a correction
        //factor of zero.
        circuit.getLoad().setNominalCurrent(10);
        circuit.setInsulation(Insul.TW); //60°C
        circuit.setAmbientTemperatureF(TempRating.getFahrenheit(56));
        assertNull(circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THW); //75°C
        circuit.setAmbientTemperatureF(TempRating.getFahrenheit(70));
        assertNotNull(circuit.getCircuitSize());

        circuit.setAmbientTemperatureF(TempRating.getFahrenheit(71));
        assertNull(circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THHW); //90°C
        circuit.setAmbientTemperatureF(TempRating.getFahrenheit(81));
        assertNotNull(circuit.getCircuitSize());

        circuit.setAmbientTemperatureF(TempRating.getFahrenheit(86));
        assertNull(circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_determined_by_MCA(){
        //size of circuit conductors is decided upon MCA
        circuit.setMaxVoltageDropPercent(5.0);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v480_3ph_4w);
        circuit.getLoad().setNominalCurrent(100);
        circuit.setLength(50);
        circuit.setMetal(Metal.COPPER);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(86);
        circuit.getLoad().setNonlinear(false);
        circuit.setNumberOfSets(1);
        circuit.setConduitMode();
        circuit.setTerminationTempRating(TempRating.T75);
//        ((GeneralOldLoad)circuit.getLoad()).setNonContinuous();
        circuit.getLoad().setNonContinuous();

        //case 1 - standard (default) conditions
        //correction factor = 1
        //adjustment factor = 1
        //load is non continuous
        assertEquals(Size.AWG_3, circuit.getCircuitSize());

        //case 2 - load is continuous, no factors, MCA decides
        /*((GeneralOldLoad)*/circuit.getLoad()/*)*/.setContinuous();
        //correction factor = 1
        //adjustment factor = 1
        //load is continuous
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        //case 3 - load is continuous, factors decides.
        circuit.setAmbientTemperatureF(95); //CorrectionFactor: 0.94
        circuit.getLoad().setNonlinear(true);
        //correction factor = 0.94
        //adjustment factor = 0.8
        //load is continuous
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
        //case 4 - load is continuous, MCA decides.
        circuit.setAmbientTemperatureF(77);
        //correction factor = 1.05
        //adjustment factor = 0.8
        //load is continuous
        assertEquals(Size.AWG_1, circuit.getCircuitSize());
        //case 5 - load is continuous, MCA decides.
        circuit.setAmbientTemperatureF(50);
        //correction factor = 1.20
        //adjustment factor = 0.8
        //load is continuous, but temperature is too high, adj. & correc. factors govern
        //done: this is a good example of how big a conductor could be when
        // accounting all factors in the code
        //A challenge question could be:
        //A 83.14 KVA, 480v 3-phase 4-wire continuous load is located at 450 feet from its feeding panel.
        //The load is mostly non linear and will be installed in a PVC conduit.
        //The design ambient temperature is 100°F.
        //1. Select the proper size for a set of THW copper conductors. A: 1/0 AWG
        //2. Select a 80% rated circuit breaker. A: 125 Amps
        //3. Select the minimum conduit trade size.
        //next level questions...
        //4. What is the voltage drop on this circuit? 1.94%
        //very challenging question...
        //5. How far can this load be from the panel using the selected conductor for a voltage drop of no more than
        // 3%? 694 FT

        //case 6 - load is continuous, factors decides.
        circuit.setAmbientTemperatureF(100);
        circuit.setMaxVoltageDropPercent(3.0);
        circuit.setLength(450);
        //correction factor = 0.88
        //adjustment factor = 0.8
        //load is continuous
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_VDrop_Neutral_OCPD_Grounding_ConductorsUpdated_TradeSize() {
        circuit.setMaxVoltageDropPercent(3);
        circuit.setLength(100);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v480_3ph_4w);
        circuit.getLoad().setNonlinear(true);
        circuit.setAmbientTemperatureF(100);
        circuit.setInsulation(Insul.THHW);
        circuit.setMetal(Metal.COPPER);
        //termination temperature rating is unknown
        circuit.setTerminationTempRating(null);
        circuit.getLoad().setNominalCurrent(100);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
        //check that the VDrop object provides calculations for the calculated size
        assertEquals(1.333, circuit.getVoltageDrop().getACVoltageDrop(), 0.001);
        assertEquals(0.277, circuit.getVoltageDrop().getACVoltageDropPercentage(), 0.001);
        assertEquals(478.666, circuit.getVoltageDrop().getACVoltageAtLoad(), 0.001);
        assertEquals(1084.734, circuit.getVoltageDrop().getMaxLengthACForActualConductor(), 0.001);
        //check that the neural size was also calculated.
        assertEquals(Size.AWG_3$0, circuit.getNeutralConductor().getSize());
    }

    @Test
    void getTradeSize(){
        assertEquals(Trade.T1$2, circuit.getPrivateConduit().getTradeSize(),
                getState());

        circuit.setTerminationTempRating(null);
        circuit.setNumberOfSets(2);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v240_3ph_4w);
        circuit.setAmbientTemperatureF(110);
        circuit.setInsulation(Insul.THWN);
        circuit.getLoad().setNominalCurrent(350);
        circuit.setMaxVoltageDropPercent(5.0);
        circuit.getLoad().setNonlinear(true);

        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
        assertEquals(Size.KCMIL_350, circuit.getNeutralConductor().getSize());
        assertEquals(Size.AWG_3, circuit.getGroundingConductor().getSize(),
                getState());
        assertEquals(8, circuit.getCurrentCarryingNumber());
        assertEquals(8, circuit.getPrivateConduit().getCurrentCarryingCount());
        assertEquals(Type.PVC40, circuit.getPrivateConduit().getType());
        assertEquals(40, circuit.getPrivateConduit().getMaxAllowedFillPercentage());
        assertEquals(4.3882,
                circuit.getPrivateConduit().getConduitablesArea(), getState());
        assertEquals(12.554,
                ConduitProperties.getArea(
                        circuit.getPrivateConduit().getType(),
                        Trade.T4
                )
        );
        assertEquals(Trade.T4, circuit.getPrivateConduit().getTradeSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_01(){
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by voltage drop
        circuit.setFreeAirMode();
        assertEquals(Size.AWG_10, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_02(){
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by ampacity
        circuit.setFreeAirMode();
        circuit.setMaxVoltageDropPercent(6.0);
        assertEquals(Size.AWG_14, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_03(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(100);
        circuit.getLoad().setNonlinear(true);
        //even if there are 4 current-carrying conductor in free air, adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(15.0);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_04(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_05(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_06(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_07(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_08(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
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
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_09(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
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
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_10(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T60 since the conductor temp rating is the least
        //temp rating of the two.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_11(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //The temp rating of the conductor equals the temp rating of the termination (T75). The size of the conductor
        //is selected per column T75.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_12(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
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
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_13(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T75 since the conductor temp rating is the
        //lesser temp rating of the two.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_14(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //The temp rating of the conductor equals the temp rating of the termination (T90). The size of the conductor
        //is selected per column T90.
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Free_Air_Case_15(){
/*testing that the conduitable list is consistent, that is, the number of
conduitables correspond with the circuit configuration */

        /*the model contains 3 conductors: conductors per set = 3.
        Since there is one only set and the circuit is in private conduit,
        there is only one conduit and then conduitables.size=3*/
        Conduit sharedConduit = new Conduit();
        sharedConduit.add(new Cable());
        /*this cable has 2 ccc*/
        sharedConduit.add(new Conductor());
        //the shared conduit has 1 cable and 1 conductor
        circuit.setUsingCable(true);
        //conduitable list has one cable only. conductorsPerSet has no meaning
        circuit.setConduitMode(sharedConduit);
        /*conduitable list has one cable only. sharedConduit has 2 cables +
        1 conductor = 3*/
        circuit.setNumberOfSets(2);
        //conduitable list has 2 cables now. sharedConduit has 3 cables + 1 conductor = 4
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        /*conduitable list has 2 cables. sharedConduit should have 3 cables
        + 1 conductor = 4, but now it has 7: 6 cables + 1 conductor!!!!!!!!!!!
        CORRECTED! sharedConduit has 4 conduitables now.*/

        circuit.getLoad().setNominalCurrent(400);
        /*since there are 2 cables, each is carrying 200 amperes. Each sets
        has 3 ccc*/
        circuit.getLoad().setNonlinear(true);
        /*now each set has 4 ccc now. In the shared conduit there should be 8
         ccc belonging to this circuit + 1 cable with 2 ccc + 1 conductor ccc =
         11, however there are 22!!!!!.
         CORRECTED! there are 11 ccc in the shared conduit.
         Total filling counting conductors should be 2+1+1=4, however, there
         are 8!!!!!
         CORRECTED! shared conduit has 4 filling counting conductors now.*/
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());

        assertEquals(0, circuit.getNumberOfPrivateConduits(), getState());
        /*in the present context numberOfPrivateConduits returns 0 (it has no
         meaning since the circuit is in shared conduit, meaning it uses only
         one conduit which is shared, and not a private conduit.*/

        assertEquals(2, circuit.getNumberOfSets(), getState());
        /*as it was set before*/

        assertEquals(0, circuit.getSetsPerPrivateConduit(), getState());
        /*setsPerPrivateConduit updates when number of sets changes, however
        since it has no meaning in the present context its value is zero.*/

        circuit.setNumberOfSets(1);
        assertEquals(0, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(1, circuit.getNumberOfSets(), getState());
        assertEquals(0, circuit.getSetsPerPrivateConduit(), getState());

        circuit.setUsingCable(false);
        circuit.getLoad().setNonlinear(false);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v120_1ph_2w);
        circuit.getLoad().setNominalCurrent(600);
        circuit.setFreeAirMode();
        //there are 5 conduitables
        assertEquals(Size.KCMIL_1500, circuit.getCircuitSize(), getState());
        assertEquals(1, circuit.getNumberOfSets(), getState());
        assertEquals(0, circuit.getNumberOfPrivateConduits(), getState());

        circuit.setNumberOfSets(2);
        //there are 10 conduitables
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(3);
        //there are 15 conduitables
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize(), getState());
        assertEquals(3, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(4);
        //there are 20 conduitables
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(4, circuit.getNumberOfSets(), getState());
//------------------------------------------------------------------------------
        circuit.setBundleMode();
        circuit.setNumberOfSets(1);
        //there are 5 conduitables
        assertEquals(Size.KCMIL_1500, circuit.getCircuitSize(), getState());
        assertEquals(1, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(2);
        //there are 10 conduitables
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(3);
        //there are 20 conduitables
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize(), getState());
        assertEquals(3, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(4);
        //there are 20 conduitables
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(4, circuit.getNumberOfSets(), getState());
//------------------------------------------------------------------------------
        circuit.setConduitMode();
        assertEquals(1, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(4, circuit.getNumberOfSets(), getState());
        assertEquals(4, circuit.getSetsPerPrivateConduit(), getState());
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize(), getState());

        circuit.morePrivateConduits();
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getNumberOfPrivateConduits(), getState());

        circuit.morePrivateConduits();//4 conduits so far
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(4, circuit.getNumberOfPrivateConduits(), getState());
//------------------------------------------------------------------------------
        circuit.setBundleMode();
        circuit.setConduitMode();
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(4, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(4, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(1);
//        circuit.setFreeAirMode();

        //conduitables should have 5 conductors
        circuit.setNumberOfSets(2);
        //conduitables should still have 5 conductors
        circuit.setBundleMode();
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getNumberOfSets(), getState());

        circuit.setNumberOfSets(3);
        //conduitables should still have 5 conductors
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize(), getState());
        assertEquals(3, circuit.getNumberOfSets(), getState());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_01(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(100);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_3$0/*Size.AWG_1$0*/, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_02(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(101);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_03(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_04(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_05(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_06(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(false);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Conduit_Case_07(){
        Cable cable = new Cable(VoltageSystemAC.v480_3ph_4w, 0.5);
        cable.setNeutralCarryingConductor(true);
        Conductor conductor = new Conductor();
        sharedConduit.add(cable);
        sharedConduit.add(conductor);
        //the shared conduit has 5 CCC
        circuit.setConduitMode(sharedConduit);
        //the shared conduit has now 7 CCC
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //the shared conduit has 8 CCC
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Private_Bundled_Cases(){
        //circuit have 4 ccc.
        circuit.setBundleMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(87);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(18);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        circuit.setLength(30);
        circuit.setPrivateBundleLength(24);


        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        circuit.setPrivateBundleLength(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setNominalCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300,
                circuit.getCircuitSize());

        //case 4
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Conductor_Shared_Bundled_Cases(){
        Bundle bundle = new Bundle();//empty bundle
        //circuit have 4 ccc.
        circuit.setBundleMode(bundle);
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(87);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(18);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        circuit.setLength(30);
        bundle.setBundlingLength(24);

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize(), getState());

        //case 2
        bundle.setBundlingLength(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize(), getState());

        //case 3
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setNominalCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

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
        assertEquals(13, bundle.getCurrentCarryingCount());

        circuit.getLoad().setNonlinear(false);
        assertEquals(12, bundle.getCurrentCarryingCount());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setNominalCurrent(200);
        assertEquals(0.96, circuit.getPhaseConductor().getCorrectionFactor());
        assertEquals(0.50, circuit.getPhaseConductor().getAdjustmentFactor());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

    }

    @Test
    void getCircuitSize_Cable_Private_Conduit(){
        //voltage drop decides
        circuit.setUsingCable(true);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());//selected per voltage drop

        circuit.setMaxVoltageDropPercent(3.4); //selected per voltage drop

        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setMaxVoltageDropPercent(5.2);//selected per ampacity and voltage drop
        assertEquals(Size.AWG_14, circuit.getCircuitSize());

        //ampacity decides
        circuit.getLoad().setNominalCurrent(160);
        circuit.setMaxVoltageDropPercent(3);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize()); //using insulation for 75C

        circuit.getLoad().setNominalCurrent(95);
        circuit.setMaxVoltageDropPercent(4.0);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(101);
        circuit.setLength(130);
        circuit.setMetal(Metal.ALUMINUM);
        circuit.setMaxVoltageDropPercent(10.0);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.setMaxVoltageDropPercent(3.0);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        //elaborated tests. Complex scenarios
        //SELECTING CONDUCTOR PER AMPACITY ONLY
        circuit.setMaxVoltageDropPercent(25);
        circuit.setLength(1);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v480_3ph_4w);
        circuit.getLoad().setNominalCurrent(105);
        circuit.getLoad().setNonlinear(true);
        circuit.setAmbientTemperatureF(100);
        circuit.setInsulation(Insul.THHW);
        circuit.setMetal(Metal.COPPER);
        assertEquals(105, circuit.getLoad().getMCA(), 0.01, getState());
        assertEquals(CircuitMode.PRIVATE_CONDUIT, circuit.getCircuitMode());
        assertEquals(4, circuit.getPrivateConduit().getCurrentCarryingCount());

        assertEquals(0.91, circuit.getCable().getCorrectionFactor());
        assertEquals(0.8, circuit.getCable().getAdjustmentFactor());

        //todo the correct one here is 149.148amps, not 123.76
        //update once getAmpacity is revised to account for the termination
        // temp. ratings.
        assertEquals(/*105.56*//*163.8*/123.76, circuit.getCable().getCorrectedAndAdjustedAmpacity(), 0.01);

        //termination temperature rating is unknown
        circuit.setTerminationTempRating(null);
        circuit.getLoad().setNominalCurrent(105);
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(100);
        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(414);
        assertEquals(Size.KCMIL_1250, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(14);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //termination temperature rating is known, T60
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.getLoad().setNominalCurrent(83);
        assertEquals(Size.AWG_3, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(353);
        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_900, circuit.getCircuitSize());

        circuit.setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_1750, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(364);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(365);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(468);
        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(469);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(546);
        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(547);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNonlinear(false);
        circuit.getLoad().setNominalCurrent(506);
        assertNull(circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        //termination temperature rating is known, T75
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v240_3ph_4w);
        circuit.setAmbientTemperatureF(110);
        circuit.setInsulation(Insul.TW);
        circuit.getLoad().setNominalCurrent(100);
        circuit.setMaxVoltageDropPercent(25.0);

        circuit.getLoad().setNonlinear(true);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(20);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_8, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.THHW);
        assertEquals(/*Size.AWG_8*/Size.AWG_10, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getLoad().setNominalCurrent(315);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(316);
        circuit.setInsulation(Insul.TW);
        assertNull(circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(437);
        circuit.setInsulation(Insul.THW);
        assertNull(circuit.getCircuitSize());

        circuit.getLoad().setNominalCurrent(523);
        circuit.setInsulation(Insul.THHW);
        assertNull(circuit.getCircuitSize());

        //termination temperature rating is known, T90
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v240_3ph_4w);
        circuit.setAmbientTemperatureF(102);
        circuit.setMaxVoltageDropPercent(25.0);

        circuit.getLoad().setNonlinear(true);

        circuit.getLoad().setNominalCurrent(90);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_2, circuit.getCircuitSize());


        circuit.getLoad().setNominalCurrent(420);
        circuit.setNumberOfSets(2);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.KCMIL_700, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());

        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.setAmbientTemperatureF(95);
        circuit.setMetal(Metal.ALUMINUM);
        circuit.getLoad().setNominalCurrent(10);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //using high leg
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setNumberOfSets(1);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_1ph_2wN);
        circuit.setAmbientTemperatureF(95);
        circuit.setMetal(Metal.ALUMINUM);
        circuit.getLoad().setNominalCurrent(10);
        circuit.setInsulation(Insul.TW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        circuit.setInsulation(Insul.THHW);
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setAmbientTemperatureF(110);
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.setMetal(Metal.COPPER);
        circuit.getLoad().setNonlinear(false);
        circuit.getLoad().setNominalCurrent(1957);
        circuit.setNumberOfSets(6);
        assertEquals(1, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(6, circuit.getSetsPerPrivateConduit(), getState());
        assertEquals(Size.KCMIL_2000, circuit.getCircuitSize(), getState());

        circuit.morePrivateConduits();
        assertEquals(2, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(3, circuit.getSetsPerPrivateConduit(), getState());
        assertEquals(Size.KCMIL_800, circuit.getCircuitSize(), getState());

        circuit.morePrivateConduits();
        assertEquals(3, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(2, circuit.getSetsPerPrivateConduit(), getState());
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());

        circuit.morePrivateConduits();
        assertEquals(6, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(1, circuit.getSetsPerPrivateConduit(), getState());
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize(), getState());

        circuit.morePrivateConduits();
        assertEquals(6, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(1, circuit.getSetsPerPrivateConduit(), getState());

        assertEquals(Size.KCMIL_400, circuit.getCircuitSize(), getState());

        circuit.lessPrivateConduits();
        assertEquals(3, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(2, circuit.getSetsPerPrivateConduit(), getState());
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_01(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(100);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        assertEquals(Size.AWG_3$0/*Size.AWG_1$0*/, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_02(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(101);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_03(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_04(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_05(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_06(){
        //Shared conduit is empty
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setConduitMode(sharedConduit);
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(false);
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Conduit_Case_07(){
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
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(102);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //circuit.setNeutralCurrentCarrying(false);
        //the shared conduit has 8 CCC
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Private_Bundled_Cases(){
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setBundleMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(87);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(18);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);
        circuit.setLength(30);
        circuit.setPrivateBundleLength(24);

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        circuit.setPrivateBundleLength(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setNominalCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300,
                circuit.getCircuitSize());

        //case 4
        circuit.setTerminationTempRating(TempRating.T75);
        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Shared_Bundled_Cases(){
        Bundle bundle = new Bundle();//empty bundle
        //circuit have 4 ccc.
        circuit.setUsingCable(true);
        circuit.setBundleMode(bundle);
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(87);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(18);
        circuit.getLoad().setNonlinear(true);
        circuit.setMaxVoltageDropPercent(25.0);

        circuit.setLength(30);
        bundle.setBundlingLength(24);

        //case 1
        assertEquals(Size.AWG_12, circuit.getCircuitSize());

        //case 2
        bundle.setBundlingLength(25);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());

        //case 3
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setNominalCurrent(180);
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize());

        //case 3
        circuit.setTerminationTempRating(TempRating.T60);
        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300,
                circuit.getCircuitSize());

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
        assertEquals(13, bundle.getCurrentCarryingCount());

        circuit.getLoad().setNonlinear(false);
        assertEquals(12, bundle.getCurrentCarryingCount());

        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.getLoad().setNominalCurrent(200);
        assertEquals(0.96, circuit.getCable().getCorrectionFactor());
        assertEquals(0.50, circuit.getCable().getAdjustmentFactor());
        assertEquals(Size.KCMIL_500, circuit.getCircuitSize());

    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_01(){
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by voltage drop
        circuit.setFreeAirMode();
        circuit.setUsingCable(true);
        assertEquals(Size.AWG_10, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_02(){
        //a circuit made of copper THW, L=100 Ft 120v, 1φ, AmbTemp=86, 10 Amps,
        //size decided by ampacity
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setMaxVoltageDropPercent(6.0);

        assertEquals(Size.AWG_14, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_03(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(100);
        circuit.getLoad().setNonlinear(true);
        //even if there are 4 current-carrying conductor in free air, adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(15.0);

        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_04(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_05(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_06(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(null);
        circuit.setUsingCable(true);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //circuit.setNeutralCurrentCarrying(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_07(){
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setUsingCable(true);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //there are 4 current-carrying conductor in free air, so adjustment
        // factor is 1.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_08(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
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
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300,
                circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_09(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T60);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
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
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(/*Size.KCMIL_350*/Size.KCMIL_300,
                circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_10(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.TW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T60 since the conductor temp rating is the least
        //temp rating of the two.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.KCMIL_350, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_11(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //circuit.setNeutralCurrentCarrying(true);
        //The temp rating of the conductor equals the temp rating of the termination (T75). The size of the conductor
        //is selected per column T75.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_12(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T75);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
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
        circuit.setMaxVoltageDropPercent(25.0);


        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_13(){
        circuit.setUsingCable(true);
        circuit.setFreeAirMode();
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);
        //The temp rating of the conductor does not exceeds the temp rating of the termination, in fact, its bellow
        //that value. The size of the conductor is selected per column T75 since the conductor temp rating is the
        //lesser temp rating of the two.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_4$0, circuit.getCircuitSize());
    }

    @Test
    void getCircuitSize_Cable_Free_Air_Case_14(){
        circuit.setFreeAirMode();
        circuit.setUsingCable(true);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.setInsulation(Insul.THHW);
        circuit.setAmbientTemperatureF(96);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(200);
        circuit.getLoad().setNonlinear(true);

        //The temp rating of the conductor equals the temp rating of the termination (T90). The size of the conductor
        //is selected per column T90.
        circuit.setMaxVoltageDropPercent(25.0);

        assertEquals(Size.AWG_3$0, circuit.getCircuitSize());
    }

    @Test
    void setCircuitMode(){
        //set up
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        circuit.getLoad().setNominalCurrent(240);
        circuit.setTerminationTempRating(TempRating.T60);

        Runnable case1 = () -> {
            circuit.setConduitMode();
            circuit.setNumberOfSets(2);
            assertEquals(CircuitMode.PRIVATE_CONDUIT, circuit.getCircuitMode());
        };

        case1.run();

        circuit.setNumberOfSets(2);
        assertEquals(2, circuit.getNumberOfSets()); //was 1
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());//was bigger
        assertNotNull(circuit.getPrivateConduit());
        assertNull(circuit.getPrivateBundle());

        circuit.setFreeAirMode();
        circuit.setNumberOfSets(1);
        assertEquals(1, circuit.getNumberOfSets());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
        assertEquals(2, circuit.getNumberOfSets()); //was 1
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
        assertNull(circuit.getPrivateConduit());
        assertNull(circuit.getPrivateBundle());

        circuit.setBundleMode();
        circuit.setNumberOfSets(1);
        assertEquals(1, circuit.getNumberOfSets());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
        assertEquals(2, circuit.getNumberOfSets());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());
        assertNull(circuit.getPrivateConduit());
        assertNotNull(circuit.getPrivateBundle());

        Conduit sharedConduit = new Conduit();
        circuit.setConduitMode(sharedConduit);
        circuit.setNumberOfSets(1);
        assertEquals(1, circuit.getNumberOfSets());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

        circuit.setNumberOfSets(2); //circuit does not ignore this value //7/11/20
        assertEquals(2, circuit.getNumberOfSets());//was 1
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());//7/11/20 was KCMIL_300
        assertNull(circuit.getPrivateConduit());
        assertNull(circuit.getPrivateBundle());

        Bundle sharedBundle = new Bundle();
        circuit.setBundleMode(sharedBundle);
        circuit.setNumberOfSets(1);
        assertEquals(1, circuit.getNumberOfSets());
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

        circuit.setNumberOfSets(2);
        assertEquals(2, circuit.getNumberOfSets());//was 1
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize());//was KCMIL_300
    }

    @Test
    void sizing_conductors_with_continuous_load(){
        circuit.setLength(1);
        circuit.setTerminationTempRating(TempRating.T90);
        circuit.getLoad().setNominalCurrent(100);
        //conductor sized per MCA
//        ((GeneralOldLoad)circuit.getLoad()).setContinuous();
        circuit.getLoad().setContinuous();
        assertEquals(Size.AWG_1,circuit.getCircuitSize());

        //conductor sized per factors
//        ((GeneralOldLoad)circuit.getLoad()).setNonContinuous();
        circuit.getLoad().setNonContinuous();
        assertEquals(Size.AWG_3, circuit.getCircuitSize());

        //conductor sized per factors
        circuit.getLoad().setNominalCurrent(420);
        circuit.setNumberOfSets(2);
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

        //conductor sized per MCA
        /*((GeneralOldLoad)*/circuit.getLoad()/*)*/.setContinuous();
        circuit.setAmbientTemperatureF(68);
        assertEquals(Size.KCMIL_300, circuit.getCircuitSize());

        //conductor sized per factors
        circuit.setAmbientTemperatureF(105);
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize());

    }

    @Test
    void oneEGCPerOnePrivateConduit(){
        //conduitables contain 3 conductors: A-N-G
        assertFalse(circuit.isUsingOneEGC(), getState());

        circuit.getLoad().setNominalCurrent(400);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());
        assertEquals(3, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.setNumberOfSets(3);
        //conduitables contain 9 conductors: AAA-NNN-GGG
        assertEquals(1, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.morePrivateConduits();
        //conduitables contain 3 conductors: A-N-G
        assertEquals(3, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(3, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.setUsingOneEGC(true);
        //conduitables contain 3 conductors: A-N-G
        assertTrue(circuit.isUsingOneEGC(), getState());
        assertEquals(3, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(3, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.lessPrivateConduits();
        //conduitables contains 7 conductors: AAA-NNN-G
        assertEquals(1, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize(), getState());
        assertEquals(7, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.getLoad().setNominalCurrent(700);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        //conduitables contains 13 conductors: AAA-BBB-CCC-NNN-G
        assertEquals(1, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(3, circuit.getNumberOfSets(), getState());
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize(), getState());
        assertEquals(13, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.morePrivateConduits();
        //conduitables contain 5 conductors: A-B-C-N-G
        assertEquals(3, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize(), getState());
        assertEquals(5, circuit.getPrivateConduit().getFillingConductorCount(), getState());

        circuit.setUsingOneEGC(false);
        circuit.lessPrivateConduits();
        //conduitables contains 15 conductors: AAA-BBB-CCC-NNN-GGG
        assertEquals(1, circuit.getNumberOfPrivateConduits(), getState());
        assertEquals(3, circuit.getNumberOfSets(), getState());
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize(), getState());
        assertEquals(15, circuit.getPrivateConduit().getFillingConductorCount(), getState());
    }

    @Test
    void oneEGCPerOnePrivateBundle(){
        circuit.setBundleMode();
        //conduitables contain 3 conductors: A-N-G
        assertFalse(circuit.isUsingOneEGC(), getState());

        circuit.getLoad().setNominalCurrent(400);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getPrivateBundle().getCurrentCarryingCount(), getState());
        assertEquals(3, circuit.getPrivateBundle().getConductorCount(), getState());

        circuit.setNumberOfSets(3);
        //conduitables contain 9 conductors: AAA-NNN-GGG
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(6, circuit.getPrivateBundle().getCurrentCarryingCount(), getState());
        assertEquals(9, circuit.getPrivateBundle().getConductorCount(), getState());

        circuit.setUsingOneEGC(true);
        //conduitables contains 7 conductors: AAA-NNN-G
        assertTrue(circuit.isUsingOneEGC(), getState());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(6, circuit.getPrivateBundle().getCurrentCarryingCount(), getState());
        assertEquals(7, circuit.getPrivateBundle().getConductorCount(), getState());

        circuit.getLoad().setNominalCurrent(700);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        //conduitables contains 13 conductors: AAA-BBB-CCC-NNN-G
        assertEquals(3, circuit.getNumberOfSets(), getState());
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getPrivateBundle().getCurrentCarryingCount(), getState());
        assertEquals(13, circuit.getPrivateBundle().getConductorCount(), getState());

        circuit.setUsingOneEGC(false);
        //conduitables contains 15 conductors: AAA-BBB-CCC-NNN-GGG
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getPrivateBundle().getCurrentCarryingCount(), getState());
        assertEquals(15, circuit.getPrivateBundle().getConductorCount(), getState());

    }

    @Test
    void oneEGCPerOneSharedBundle(){
        Bundle bundle = new Bundle();
        circuit.setBundleMode(bundle);
        //conduitables contain 3 conductors: A-N-G
        assertFalse(circuit.isUsingOneEGC(), getState());

        circuit.getLoad().setNominalCurrent(400);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getSharedBundle().getCurrentCarryingCount(), getState());
        assertEquals(3, circuit.getSharedBundle().getConductorCount(), getState());

        circuit.setNumberOfSets(3);
        //conduitables contain 9 conductors: AAA-NNN-GGG
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(6, circuit.getSharedBundle().getCurrentCarryingCount(), getState());
        assertEquals(9, circuit.getSharedBundle().getConductorCount(), getState());

        circuit.setUsingOneEGC(true);
        //conduitables contains 7 conductors: AAA-NNN-G
        assertTrue(circuit.isUsingOneEGC(), getState());
        assertEquals(Size.AWG_1$0, circuit.getCircuitSize(), getState());
        assertEquals(6, circuit.getSharedBundle().getCurrentCarryingCount(), getState());
        assertEquals(7, circuit.getSharedBundle().getConductorCount(), getState());

        circuit.getLoad().setNominalCurrent(700);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        //conduitables contains 13 conductors: AAA-BBB-CCC-NNN-G
        assertEquals(3, circuit.getNumberOfSets(), getState());
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getSharedBundle().getCurrentCarryingCount(), getState());
        assertEquals(13, circuit.getSharedBundle().getConductorCount(), getState());

        circuit.setUsingOneEGC(false);
        //conduitables contains 15 conductors: AAA-BBB-CCC-NNN-GGG
        assertEquals(Size.KCMIL_250, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getSharedBundle().getCurrentCarryingCount(), getState());
        assertEquals(15, circuit.getSharedBundle().getConductorCount(), getState());

    }

    @Test
    void oneEGCPerOneSharedConduit(){
        Conduit conduit = new Conduit();
        Conductor ground = new Conductor();
        ground.setRole(Conductor.Role.GND);
        conduit.add(ground);
        conduit.add(ground.clone());

        circuit.setConduitMode(conduit);
        //conduitables contain 3 conductors: A-N-G
        assertFalse(circuit.isUsingOneEGC(), getState());

        circuit.getLoad().setNominalCurrent(400);
        assertEquals(Size.KCMIL_600, circuit.getCircuitSize(), getState());
        assertEquals(2, circuit.getSharedConduit().getCurrentCarryingCount(), getState());
        assertEquals(3+2, circuit.getSharedConduit().getFillingConductorCount(), getState());

        circuit.setNumberOfSets(3);
        //conduitables contain 9 conductors: AAA-NNN-GGG
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize(), getState());
        assertEquals(6, circuit.getSharedConduit().getCurrentCarryingCount(), getState());
        assertEquals(9+2, circuit.getSharedConduit().getFillingConductorCount(), getState());

        circuit.setUsingOneEGC(true);
        //conduitables contains 7 conductors: AAA-NNN-G
        assertTrue(circuit.isUsingOneEGC(), getState());
        assertEquals(Size.AWG_2$0, circuit.getCircuitSize(), getState());
        assertEquals(6, circuit.getSharedConduit().getCurrentCarryingCount(), getState());
        assertEquals(7+2, circuit.getSharedConduit().getFillingConductorCount(), getState());

        circuit.getLoad().setNominalCurrent(700);
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        //conduitables contains 13 conductors: AAA-BBB-CCC-NNN-G
        assertEquals(3, circuit.getNumberOfSets(), getState());
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getSharedConduit().getCurrentCarryingCount(), getState());
        assertEquals(13+2, circuit.getSharedConduit().getFillingConductorCount(), getState());

        circuit.setUsingOneEGC(false);
        //conduitables contains 15 conductors: AAA-BBB-CCC-NNN-GGG
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize(), getState());
        assertEquals(9, circuit.getSharedConduit().getCurrentCarryingCount(), getState());
        assertEquals(15+2, circuit.getSharedConduit().getFillingConductorCount(), getState());

    }

    @Test
    void sizeOfEGC(){
        /*case 1:
        load: 120v 1φ 50A
        circuit Length = 100 ft
        VD max = 3%
        Voltage drops decides the size, #4, rated for 70Amps at 60°C
        The ocpd is for protecting the conductor, 70 Amps
        */
        circuit.getLoad().setVoltageSystem(VoltageSystemAC.v120_1ph_2w);
        circuit.getLoad().setNominalCurrent(50);
        assertEquals(Size.AWG_6, circuit.getSizePerAmpacity(false), getState());
        assertEquals(Size.AWG_4, circuit.getSizePerVoltageDrop(false),  getState());
        assertEquals(70,circuit.getOcdp().getRating(),getState());

        /*case 2:
        Same conditions except the length is 50 ft.
        Ampacity decides the size, #6, rated for 55Amps at 60°C
        The ocpd is for protecting the conductor, 60 Amps, NHSR rule applies.
        */
        circuit.setLength(50);
        assertEquals(Size.AWG_6, circuit.getSizePerAmpacity(false), getState());
        assertEquals(Size.AWG_6, circuit.getSizePerVoltageDrop(false), getState());
        assertEquals(60,circuit.getOcdp().getRating(),getState());

        /*case 3:
        Same as before but the load is continuous.
        50*1.25=62.5Amps ->#4
        Ampacity decides the size, #4, rated for 70Amps at 60°C
        The ocpd is for protecting the conductor, 70 Amps.
        */
        /*GeneralOldLoad*/ Load load = /*(GeneralOldLoad)*/ circuit.getLoad();
        load.setContinuous();
        assertEquals(Size.AWG_4, circuit.getSizePerAmpacity(false), getState());
        assertEquals(Size.AWG_6, circuit.getSizePerVoltageDrop(false), getState());
        assertEquals(70,circuit.getOcdp().getRating(),getState());

        /*case 4:
        load: 208v 3φ 4w 400A, non-linear and continuous
        circuit Length = 100 ft, 2 sets in 2 conduits
        VD max = 3%
        */
        load.setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
        load.setContinuous();
        load.setNonlinear(true);
        load.setNominalCurrent(400);
        circuit.setNumberOfSets(2);
        circuit.morePrivateConduits();
        assertEquals(Size.KCMIL_250, circuit.getSizePerAmpacity(false), getState());
        assertEquals(Size.AWG_3$0, circuit.getSizePerVoltageDrop(false), getState());
        assertEquals(450,circuit.getOcdp().getRating(),getState());
        assertEquals(Metal.COPPER, circuit.getGroundingConductor().getMetal(), getState());
        assertEquals(Size.AWG_2, circuit.getGroundingConductor().getSize(),getState());

        circuit.setMetal(Metal.ALUMINUM);
        assertEquals(400,circuit.getOcdp().getRating(),getState());
        assertEquals(Metal.ALUMINUM, circuit.getGroundingConductor().getMetal(), getState());
        assertEquals(Size.AWG_1, circuit.getGroundingConductor().getSize(), getState());

    }

    @Test
    void messaging(){
        Circuit c = new Circuit(new /*GeneralOldLoad*/Load());
        c.setLength(10);
        assertEquals(15, c.getOcdp().getRating());
        assertEquals(Size.AWG_14, c.getGroundingConductor().getSize(), getState());

    }
}
/*

class CircuitData{
    private Circuit circuit;

    public List<Conduitable> conduitables;
    public OCPD ocdp;
    public Conduit privateConduit;
    public Conduit sharedConduit;
    public Bundle privateBundle;
    public Bundle sharedBundle;
    public OldLoad load;
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
        ocdp = (OCPD) ocdpField.get(circuit);
        privateConduit = (Conduit) privateConduitField.get(circuit);
        sharedConduit = (Conduit) sharedConduitField.get(circuit);
        privateBundle = (Bundle) privateBundleField.get(circuit);
        sharedBundle = (Bundle) sharedBundleField.get(circuit);
        load = (OldLoad) loadField.get(circuit);
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
