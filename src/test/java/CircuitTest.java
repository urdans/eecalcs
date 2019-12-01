package test.java;

import eecalcs.circuits.Circuit;
import eecalcs.circuits.Load;
import eecalcs.circuits.Ocpd;
import eecalcs.conductors.Bundle;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.conductors.Conduitable;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.systems.SystemAC;
import org.junit.jupiter.api.Test;
import test.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CircuitTest {
    Load load = new Load();
    Circuit circuit = new Circuit(load);
    CircuitData circuitData = new CircuitData(circuit);

    @Test
    void setupModelConductors() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Tools.printTitle("CircuitTest.setupModelConductors");

        assertNotNull(circuitData.load);
        assertNotNull(circuitData.phaseAConductor);
        assertNull(circuitData.phaseBConductor);
        assertNull(circuitData.phaseCConductor);
        assertNotNull(circuitData.neutralConductor);
        assertNotNull(circuitData.groundingConductor);
        assertNotNull(circuitData.cable);
        assertEquals(0, circuit.resultMessages.getMessages().size());
        assertEquals(SystemAC.Voltage.v120_1ph, circuitData.systemVoltage);
        assertEquals(SystemAC.Wires.W2, circuitData.systemWires);
        assertFalse(circuitData.usingCable);
        assertEquals(3, circuitData.conductorsPerSet);
        assertEquals(3, circuitData.conduitables.size());

        circuitData.load.setSystemVoltage(SystemAC.Voltage.v480_3ph);


//        Method setupModelConductors = circuit.getClass().getDeclaredMethod("setupModelConductors", null);
//        setupModelConductors.setAccessible(true);
//
//
//
//        setupModelConductors.invoke(circuit, null);
//
//        assertNotNull(circuitData.getState().phaseAConductor);

    }
}

class CircuitData{
    private Circuit circuit;

    public List<Conduitable> conduitables = new ArrayList<>();
    public Ocpd ocdp;
    public Conduit privateConduit;
    public Conduit sharedConduit;
    public Bundle privateBundle;
    public Bundle sharedBundle;
    public Load load;
    public int numberOfSets;
    public int setsPerConduit;
    public int conductorsPerSet;
    public SystemAC.Voltage systemVoltage;
    public SystemAC.Wires systemWires;
    public Conductor phaseAConductor;
    public Conductor phaseBConductor;
    public Conductor phaseCConductor;
    public Conductor neutralConductor;
    public Conductor groundingConductor;
    public Cable cable;
    public boolean usingCable;

    public CircuitData(Circuit circuit){
        this.circuit = circuit;
        try {
            getState();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public CircuitData getState() throws NoSuchFieldException, IllegalAccessException {
        Field conduitablesField = circuit.getClass().getDeclaredField("conduitables");
        Field ocdpField = circuit.getClass().getDeclaredField("ocdp");
        Field privateConduitField = circuit.getClass().getDeclaredField("privateConduit");
        Field sharedConduitField = circuit.getClass().getDeclaredField("sharedConduit");
        Field privateBundleField = circuit.getClass().getDeclaredField("privateBundle");
        Field sharedBundleField = circuit.getClass().getDeclaredField("sharedBundle");
        Field loadField = circuit.getClass().getDeclaredField("load");
        Field numberOfSetsField = circuit.getClass().getDeclaredField("numberOfSets");
        Field setsPerConduitField = circuit.getClass().getDeclaredField("setsPerConduit");
        Field conductorsPerSetField = circuit.getClass().getDeclaredField("conductorsPerSet");
        Field systemVoltageField = circuit.getClass().getDeclaredField("systemVoltage");
        Field systemWiresField = circuit.getClass().getDeclaredField("systemWires");
        Field phaseAConductorField = circuit.getClass().getDeclaredField("phaseAConductor");
        Field phaseBConductorField = circuit.getClass().getDeclaredField("phaseBConductor");
        Field phaseCConductorField = circuit.getClass().getDeclaredField("phaseCConductor");
        Field neutralConductorField = circuit.getClass().getDeclaredField("neutralConductor");
        Field groundingConductorField = circuit.getClass().getDeclaredField("groundingConductor");
        Field cableField = circuit.getClass().getDeclaredField("cable");
        Field usingCableField = circuit.getClass().getDeclaredField("usingCable");

        conduitablesField.setAccessible(true);
        ocdpField.setAccessible(true);
        privateConduitField.setAccessible(true);
        sharedConduitField.setAccessible(true);
        privateBundleField.setAccessible(true);
        sharedBundleField.setAccessible(true);
        loadField.setAccessible(true);
        numberOfSetsField.setAccessible(true);
        setsPerConduitField.setAccessible(true);
        conductorsPerSetField.setAccessible(true);
        systemVoltageField.setAccessible(true);
        systemWiresField.setAccessible(true);
        phaseAConductorField.setAccessible(true);
        phaseBConductorField.setAccessible(true);
        phaseCConductorField.setAccessible(true);
        neutralConductorField.setAccessible(true);
        groundingConductorField.setAccessible(true);
        cableField.setAccessible(true);
        usingCableField.setAccessible(true);

        conduitables = (List<Conduitable>) conduitablesField.get(circuit);
        ocdp = (Ocpd) ocdpField.get(circuit);
        privateConduit = (Conduit) privateConduitField.get(circuit);
        sharedConduit = (Conduit) sharedConduitField.get(circuit);
        privateBundle = (Bundle) privateBundleField.get(circuit);
        sharedBundle = (Bundle) sharedBundleField.get(circuit);
        load = (Load) loadField.get(circuit);
        numberOfSets = (int) numberOfSetsField.get(circuit);
        setsPerConduit = (int) setsPerConduitField.get(circuit);
        conductorsPerSet = (int) conductorsPerSetField.get(circuit);
        systemVoltage = (SystemAC.Voltage) systemVoltageField.get(circuit);
        systemWires = (SystemAC.Wires) systemWiresField.get(circuit);
        phaseAConductor = (Conductor) phaseAConductorField.get(circuit);
        phaseBConductor = (Conductor) phaseBConductorField.get(circuit);
        phaseCConductor = (Conductor) phaseCConductorField.get(circuit);
        neutralConductor = (Conductor) neutralConductorField.get(circuit);
        groundingConductor = (Conductor) groundingConductorField.get(circuit);
        cable = (Cable) cableField.get(circuit);
        usingCable = (boolean) usingCableField.get(circuit);

        return this;
    }
}