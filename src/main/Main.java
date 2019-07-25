package main;

import eecalcs.*;

public class Main {

	public static void main(String[] args) {
		//region testing voltage drop
		System.out.println("**************** TESTING VOLTAGE DROP METHOD ****************");
		VDropCalculator vd = new VDropCalculator();
		System.out.println("     Voltage (v): " + vd.getSourceVoltage());
		System.out.println("  Conductor size: " + vd.getSize());
		System.out.println("          Phases: " + vd.getPhases());
		System.out.println("    Conduit type: " + vd.getConduitMaterial());
		System.out.println("  Conductor type: " + vd.getConductorMetal());
		System.out.println("  Number of sets: " + vd.getNumberOfSets());
		System.out.println("     Lenght (ft): " + vd.getOneWayLength());
		System.out.println("     Current (A): " + vd.getLoadCurrent());
		System.out.println("    Power factor: " + vd.getPowerFactor());
		System.out.println("         Coating: " + vd.getCopperCoating());
		System.out.println("==============AC Results==============");
		System.out.println(" AC Voltage at load: " + String.format("%.2f", vd.getVoltageAtLoadAC()));
		System.out.println("AC Voltage drop (v): " + String.format("%.2f", vd.getVoltageDropVoltsAC())+"V");
		System.out.println("AC Voltage drop (%): " + String.format("%.2f", vd.getVoltageDropPercentageAC())+"%");
		System.out.println("==============DC Results==============");
		System.out.println(" DC Voltage at load: " + String.format("%.2f", vd.getVoltageAtLoadDC()));
		System.out.println("DC Voltage drop (v): " + String.format("%.2f", vd.getVoltageDropVoltsDC())+"V");
		System.out.println("DC Voltage drop (%): " + String.format("%.2f", vd.getVoltageDropPercentageDC())+"%");

		System.out.println("\n******* Changing values *******");
		vd.setSourceVoltage(208);
		vd.setConductorSize("1/0");
		vd.setPhases(3);
		vd.setConduitMaterial(Conduit.Material.ALUMINUM);
		vd.setCopperCoating(Conductor.CopperCoating.COATED);
		vd.setNumberOfSets(2);
		vd.setLoadCurrent(130);
		vd.setPowerFactor(0.9);
		vd.setOneWayLength(350);

		System.out.println("     Voltage (v): " + vd.getSourceVoltage());
		System.out.println("  Conductor size: " + vd.getSize());
		System.out.println("          Phases: " + vd.getPhases());
		System.out.println("    Conduit type: " + vd.getConduitMaterial());
		System.out.println("  Conductor type: " + vd.getConductorMetal());
		System.out.println("  Number of sets: " + vd.getNumberOfSets());
		System.out.println("     Lenght (ft): " + vd.getOneWayLength());
		System.out.println("     Current (A): " + vd.getLoadCurrent());
		System.out.println("    Power factor: " + vd.getPowerFactor());
		System.out.println("==============AC Results==============");
		System.out.println(" AC Voltage at load: " + String.format("%.2f", vd.getVoltageAtLoadAC()));
		System.out.println("AC Voltage drop (v): " + String.format("%.2f", vd.getVoltageDropVoltsAC())+"V");
		System.out.println("AC Voltage drop (%): " + String.format("%.2f", vd.getVoltageDropPercentageAC())+"%");
		System.out.println("==============DC Results==============");
		System.out.println(" DC Voltage at load: " + String.format("%.2f", vd.getVoltageAtLoadDC()));
		System.out.println("DC Voltage drop (v): " + String.format("%.2f", vd.getVoltageDropVoltsDC())+"V");
		System.out.println("DC Voltage drop (%): " + String.format("%.2f", vd.getVoltageDropPercentageDC())+"%");

		if (vd.resultMessages.hasErrors()) {
			System.out.println("The following errors ocurred:");
			for (Message msg : vd.resultMessages.getMessages()) {
				System.out.println(msg.message + " : " + msg.number);
			}
		}
		//if (true) return;
		//endregion

		//region test sizing conductor per voltage drop
		System.out.println("\n\n**************** TESTING SIZING CONDUCTOR PER VOLTAGE DROP ****************");
		System.out.println("         Voltage (v): " + vd.getSourceVoltage());
		System.out.println("              Phases: " + vd.getPhases());
		System.out.println("        Conduit type: " + vd.getConduitMaterial());
		System.out.println("      Conductor type: " + vd.getConductorMetal());
		System.out.println("      Number of sets: " + vd.getNumberOfSets());
		System.out.println("         Lenght (ft): " + vd.getOneWayLength());
		System.out.println("         Current (A): " + vd.getLoadCurrent());
		System.out.println("        Power factor: " + vd.getPowerFactor());
		System.out.println("       Cooper coated: " + vd.getCopperCoating());
		System.out.println("Maximum voltage drop: " + vd.getMaxVoltageDropPercent() + "%");
		System.out.println("============== AC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeAC().getSizeFullName());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageAC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthAC()));
		System.out.println("============== DC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeDC().getSizeFullName());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageDC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthDC()));

		System.out.println("\n******* Changing values *******");
		vd.setSourceVoltage(480);
		vd.setPhases(3);
		vd.setNumberOfSets(2);
		vd.setLoadCurrent(500);
		vd.setOneWayLength(250);
		vd.setMaxVoltageDropPercent(2);
		vd.setPowerFactor(0.85);

		System.out.println("         Voltage (v): " + vd.getSourceVoltage());
		System.out.println("              Phases: " + vd.getPhases());
		System.out.println("        Conduit type: " + vd.getConduitMaterial());
		System.out.println("      Conductor type: " + vd.getConductorMetal());
		System.out.println("      Number of sets: " + vd.getNumberOfSets());
		System.out.println("         Lenght (ft): " + vd.getOneWayLength());
		System.out.println("         Current (A): " + vd.getLoadCurrent());
		System.out.println("        Power factor: " + vd.getPowerFactor());
		System.out.println("       Cooper coated: " + vd.getCopperCoating());
		System.out.println("Maximum voltage drop: " + vd.getMaxVoltageDropPercent() + "%");
		System.out.println("============== AC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeAC().getSizeFullName());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageAC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthAC()));
		System.out.println("============== DC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeDC().getSizeFullName());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageDC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthDC()));

		if (vd.resultMessages.hasMessages()) {
			System.out.println("The following errors ocurred:");
			for (Message msg : vd.resultMessages.getMessages()) {
				System.out.println(msg.message + " : " + msg.number);
			}
		}
		if (true) return;
		//endregion

		/*
		Release notes.

		NEC table 9 provides reactances and resistances for the following conditions: 3 phases, 60 hz, 75 deg C, 3 single conductors per
		conduit. When using several sets, it is imperative that the sets be in separate conduits each set.
		is this table permitted to be used for single phase system? check what IEEE says about it, because I think I read that using
		table 9 for the 2-phase condition is ok.
		A more advanced version of this program would be by adding calculation of AC and DC resistance per actual conditions, like
		temperature, number of conductors in the same conduit, 2-phase scenario, and others if any.
		*/


		if (true) return;

		//region testing conductor properties
		String wireTest = "1250";
		Conductor conductor = Conductor.getConductorBySize(wireTest);
		System.out.println("**************** TESTING CONDUCTOR PROPERTIES ****************");
		System.out.println("Properties of conductor " + Conductor.getSizeFullName(wireTest) + ":\n\n" +
				"ampacity of copper wires (Amps):\n" +
				"    at 60°C: " + conductor.copper.ampacity.t60 + "\n" +
				"    at 75°C: " + conductor.copper.ampacity.t75 + "\n" +
				"    at 90°C: " + conductor.copper.ampacity.t90 + "\n" +
				"ampacity of aluminum wires (Amps):\n" +
				"    at 60°C: " + conductor.aluminum.ampacity.t60 + "\n" +
				"    at 75°C: " + conductor.aluminum.ampacity.t75 + "\n" +
				"    at 90°C: " + conductor.aluminum.ampacity.t90 + "\n" +
				"reactance all wires (Ω/1000 FT):\n" +
				"  in non magnetic conduit: " + conductor.reactance.inNonMagCond + "\n" +
				"      in magnetic conduit: " + conductor.reactance.inMagCond + "\n" +
				"resistance of copper wires (Ω/1000 FT):\n" +
				"     in PVC conduit: " + conductor.copper.resistance.ac.inPVCCond + "\n" +
				"      in AL conduit: " + conductor.copper.resistance.ac.inALCond + "\n" +
				"   in steel conduit: " + conductor.copper.resistance.ac.inSteelCond + "\n" +
				"        dc uncoated: " + conductor.copper.resistance.dc.uncoated + "\n" +
				"          dc coated: " + conductor.copper.resistance.dc.coated + "\n" +
				"resistance of aluminum wires (Ω/1000 FT):\n" +
				"     in PVC conduit: " + conductor.aluminum.resistance.ac.inPVCCond + "\n" +
				"      in AL conduit: " + conductor.aluminum.resistance.ac.inALCond + "\n" +
				"   in steel conduit: " + conductor.aluminum.resistance.ac.inSteelCond + "\n" +
				"                 dc: " + conductor.aluminum.resistance.dc + "\n" +
				"Area in circular mils: " + conductor.areaCM + "\n");
		//endregion

		//region testing tables
		System.out.println("\n**************** TESTING TABLES ****************");
		String insulation = "XHHW-2";
		System.out.println("Temperature of " + insulation + " is: " + Conductor.getInsulationTemperatureCelsius(insulation));

		String wire = "1500";
		//insulation = "FEP";
		System.out.println("Area of conductor size '" + Conductor.getSizeFullName(wire) + "' and insulation '" + insulation + "' is: " +
				Conductor.getAreaIn2(wire, insulation) + "\n");

		wire = "1000";
		insulation = "XHHW";
		System.out.println("Area of bare compact conductor size \"" + Conductor.getSizeFullName(wire) + "\" is: " +
				CompactConductorProperties.getAreaOfBareConductor(wire));

		System.out.println("Area of compact conductor size \"" + Conductor.getSizeFullName(wire) + "\" and " +
				"insulation \"" + insulation + "\" is: " +
				CompactConductorProperties.getConductorWireAreaIn2(wire, insulation));
		//endregion
	}
}
