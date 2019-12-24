package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.circuits.Load;
import eecalcs.circuits.Mode;
import eecalcs.circuits.Ocpd;
import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;
import test.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CircuitTest {
//    Load load = new Load();
    Circuit circuit = new Circuit(new Load());
    CircuitData circuitData;
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
    }

    //@Test
    void setupModelConductors() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
    }

    @Test
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
    }

    @Test
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
    }

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
    void getCircuitSize(){
        Tools.printTitle("CircuitTest.getCircuitSize");
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

        //termination temperature rating is known
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

//        circuit.getLoad().setCurrent(506);
        circuit.setNumberOfSets(2);
        assertEquals(Size.KCMIL_400, circuit.getCircuitSize());

//quede aqui
//        hacer pruebas a T75 y T90

        if(true)
            return;


        circuit.setTerminationTempRating(TempRating.T75);
        circuit.getPhaseConductor().setLength(155);
        circuit.getLoad().setCurrent(100);
        circuit.getVoltageDrop().setMaxVoltageDropPercent(8.0);
        assertEquals(Size.AWG_1, circuit.getCircuitSize());
        //continue here. implement for other scenarios, when termination ratings are known.
    }
}

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
}