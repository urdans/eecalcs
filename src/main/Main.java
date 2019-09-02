package main;

import eecalcs.conductors.*;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Magnetic;
import eecalcs.conduits.Material;
import eecalcs.systems.TempRating;
import eecalcs.voltagedrop.VDrop;
import tools.Message;

public class Main {

	public static void main(String[] args) {
		System.out.println("Testing creating a circuit out of an existing conductor object");
		Conductor c1 = new Conductor(Size.S10, Metal.ALUMINUM, Insul.TW, 50);
		Circuit ckt1 = new Circuit(c1);
		ckt1.setConduitMaterial(Material.STEEL);

		Circuit ckt2 = new Circuit(ckt1);

		ckt1.setSize(Size.S8);
		ckt1.setAmbientTemperatureC(40);
		ckt1.setNumberOfSets(2);
		ckt1.setConduitMaterial(Material.ALUMINUM);
		ckt1.setInsulation(Insul.THHW);
		ckt1.setMetal(Metal.COPPER);
		ckt1.setLength(625);

		c1.setLength(112);
		c1.setSize(Size.S14);
		System.out.println(ckt1.getAmbientTemperatureC() + "\t" + ckt1.getAmbientTemperatureF() + "\t" + ckt1.getAmpacity() +
			"\t" + ckt1.getAreaCM() + "\t" + ckt1.getConduitMaterial() + "\t" + ckt1.getInsulatedAreaIn2() +
			"\t" + ckt1.getInsulation() + "\t" + ckt1.getLength() + "\t" + ckt1.getMetal() + "\t" + ckt1.getNumberOfSets() +
			"\t" + ckt1.getOneWayACResistance() + "\t" + ckt1.getOneWayDCResistance() + "\t" + ckt1.getOneWayReactance() +
			"\t" + ckt1.getSize() + "\t" + ckt1.getTemperatureRating());

		System.out.println(ckt2.getAmbientTemperatureC() + "\t" + ckt2.getAmbientTemperatureF() + "\t" + ckt2.getAmpacity() +
				"\t" + ckt2.getAreaCM() + "\t" + ckt2.getConduitMaterial() + "\t" + ckt2.getInsulatedAreaIn2() +
				"\t" + ckt2.getInsulation() + "\t" + ckt2.getLength() + "\t" + ckt2.getMetal() + "\t" + ckt2.getNumberOfSets() +
				"\t" + ckt2.getOneWayACResistance() + "\t" + ckt2.getOneWayDCResistance() + "\t" + ckt2.getOneWayReactance() +
				"\t" + ckt2.getSize() + "\t" + ckt2.getTemperatureRating());


		//if(true) return;//*/


		//region ampacity of conductors and conduit sizes tests
		System.out.println("**************** Ampacity of conductors ****************");
		System.out.println(String.format("%5s","60°C")+String.format("%5s","75°C")+
				String.format("%5s","90°C"));
		for(String s: CondProp.getSizes()) {
			PropertySet propSet = CondProp.bySize(s);
			System.out.println(String.format("%5.0f",propSet.getAmpacity(Metal.COPPER, TempRating.T60))+String.format("%5.0f",
					propSet.getAmpacity(Metal.COPPER,TempRating.T75))+ String.format("%5.0f",propSet.getAmpacity(Metal.COPPER,
					TempRating.T90)));
		}

		System.out.println("**************** Testing an invalid conductor ****************");
		Conductor myc = new Conductor("114", Metal.COPPER, Insul.THW, 100);
		myc.setAmbientTemperatureF(95);
		double incArea = 0;
		double prevArea;// = myc.getAreaCM();
		double incAmp = 0;
		double prevAmp;// = myc.getAmpacity();
		System.out.println(String.format("%5s",myc.getSize()) + ": " + String.format("%5.0f",myc.getAmpacity()) + "\t CM: " + String.format("%7.0f",
				myc.getAreaCM()) +
				"\t Size inc: " + String.format("%4.0f",incArea) + "%\t Ampacity inc: "+ String.format("%7.0f",incAmp)+"% \tArea/Amp:" +
				" " + String.format("%4.0f CM/AMP",myc.getAreaCM()/myc.getAmpacity()));

		System.out.println("\n**************** Testing incremental ampacity and areas of conductors ****************");
		System.out.println("Conductor metal: "+ myc.getMetal() + "\tInsulation: " + myc.getInsulation() + "\tTemp.rating: " + myc.getTemperatureRating() + "°C");
		myc.setSize(Size.S14);
		prevArea = myc.getAreaCM();
		prevAmp = myc.getAmpacity();
		for(String s : CondProp.getSizes()){
			myc.setSize(s);
			double area = myc.getAreaCM();
			double ampacity = myc.getAmpacity();
			incArea = 100 * area/prevArea;
			prevArea = area;
			incAmp = 100 * ampacity/prevAmp;
			prevAmp = ampacity;
			System.out.println(String.format("%5s",s) + ": " + String.format("%5.1f",ampacity) + "\t CM: " + String.format("%7.0f",area) +
					"\t Size inc: " + String.format("%4.0f",incArea) + "%\t Ampacity inc: "+ String.format("%7.0f",incAmp)+"% \tArea/Amp:" +
					" " + String.format("%4.0f CM/AMP",area/ampacity));
		}
		//if (true) return;

		System.out.println("**************** Testing conduit sizes ****************");
		String tradeSize = "2";
		String type = Conduit.TYP_PVC_40;
		System.out.println("Area in inch2 of " + tradeSize + "\" conduit, type " + type + " is: " + Conduit.getArea(type,
				tradeSize));
		System.out.println("isValidType: " + Conduit.isValidType(type));
		System.out.println("hasArea: " + Conduit.hasArea(type,tradeSize));
		//endregion

		//region testing voltage drop
		Conductor conductor = new Conductor();
		Circuit circuit = new Circuit(conductor);
		VDrop vd = new VDrop(circuit);
		System.out.println("**************** TESTING VOLTAGE DROP METHOD ****************");
		System.out.println("     Voltage (v): " + vd.getSourceVoltage());
		System.out.println("  Conductor size: " + vd.getCircuit().getSize());
		System.out.println("          Phases: " + vd.getPhases());
		System.out.println("    Conduit type: " + vd.getCircuit().getConduitMaterial());
		System.out.println("  Conductor type: " + vd.getCircuit().getMetal());
		System.out.println("  Number of sets: " + vd.getCircuit().getNumberOfSets());
		System.out.println("     Lenght (ft): " + vd.getCircuit().getLength());
		System.out.println("     Current (A): " + vd.getLoadCurrent());
		System.out.println("    Power factor: " + vd.getPowerFactor());
		System.out.println("         Coating: " + vd.getCircuit().isCopperCoated());
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
		vd.getCircuit().setSize(Size.Z1);// setConductorSize(Size.Z1);
		vd.setPhases(3);
		vd.getCircuit().setConduitMaterial(Material.ALUMINUM); //setConduitType(Material.ALUMINUM);
		//vd.getCircuit().setMetal(Metal.ALUMINUM);
		vd.getCircuit().setCopperCoated(Coating.COATED); //setCopperCoating(Coating.COATED);
		vd.getCircuit().setNumberOfSets(2);
		vd.setLoadCurrent(130);
		vd.setPowerFactor(0.9);
		vd.getCircuit().setLength(350); //setOneWayLength(350);

		System.out.println("     Voltage (v): " + vd.getSourceVoltage());
		System.out.println("  Conductor size: " + vd.getCircuit().getSize());
		System.out.println("          Phases: " + vd.getPhases());
		System.out.println("    Conduit type: " + vd.getCircuit().getConduitMaterial());
		System.out.println("  Conductor type: " + vd.getCircuit().getMetal());
		System.out.println("  Number of sets: " + vd.getCircuit().getNumberOfSets());
		System.out.println("     Lenght (ft): " + vd.getCircuit().getLength());
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
		//endregion*/

		//region test sizing conductor per voltage drop
		System.out.println("\n\n**************** TESTING SIZING CONDUCTOR PER VOLTAGE DROP ****************");
		System.out.println("         Voltage (v): " + vd.getSourceVoltage());
		System.out.println("              Phases: " + vd.getPhases());
		System.out.println("        Conduit type: " + vd.getCircuit().getConduitMaterial());
		System.out.println("      Conductor type: " + vd.getCircuit().getMetal());
		System.out.println("      Number of sets: " + vd.getCircuit().getNumberOfSets());
		System.out.println("         Lenght (ft): " + vd.getCircuit().getLength());
		System.out.println("         Current (A): " + vd.getLoadCurrent());
		System.out.println("        Power factor: " + vd.getPowerFactor());
		System.out.println("       Cooper coated: " + vd.getCircuit().isCopperCoated());
		System.out.println("Maximum voltage drop: " + vd.getMaxVoltageDropPercent() + "%");
		System.out.println("============== AC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeAC());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageAC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthAC()));
		System.out.println("============== DC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeDC().getFullSizeName());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageDC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthDC()));

		System.out.println("\n******* Changing values *******");
		vd.setSourceVoltage(480);
		vd.setPhases(3);
		vd.getCircuit().setNumberOfSets(2);
		vd.setLoadCurrent(500);
		vd.getCircuit().setLength(250);
		vd.setMaxVoltageDropPercent(2);
		vd.setPowerFactor(0.85);

		System.out.println("         Voltage (v): " + vd.getSourceVoltage());
		System.out.println("              Phases: " + vd.getPhases());
		System.out.println("        Conduit type: " + vd.getCircuit().getConduitMaterial());
		System.out.println("      Conductor type: " + vd.getCircuit().getMetal());
		System.out.println("      Number of sets: " + vd.getCircuit().getNumberOfSets());
		System.out.println("         Lenght (ft): " + vd.getCircuit().getLength());
		System.out.println("         Current (A): " + vd.getLoadCurrent());
		System.out.println("        Power factor: " + vd.getPowerFactor());
		System.out.println("       Cooper coated: " + vd.getCircuit().isCopperCoated());
		System.out.println("Maximum voltage drop: " + vd.getMaxVoltageDropPercent() + "%");
		System.out.println("============== AC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeAC());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageAC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthAC()));
		System.out.println("============== DC Results==============");
		System.out.println("        Conductor size: " + vd.getCalculatedSizeDC().getFullSizeName());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageDC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthDC()));

		if (vd.resultMessages.hasMessages()) {
			System.out.println("The following errors ocurred:");
			for (Message msg : vd.resultMessages.getMessages()) {
				System.out.println(msg.message + " : " + msg.number);
			}
		}
		//if (true) return;
		//endregion */

		//region testing conductor properties
		PropertySet condProp = CondProp.bySize(Size.K1250);
		System.out.println("**************** TESTING CONDUCTOR PROPERTIES ****************");
		System.out.println("Properties of conductor " + condProp.getFullSizeName() + ":\n" +
				"ampacity of copper wires (Amps):\n" +
				"    at 60°C: " + condProp.getAmpacity(Metal.COPPER, TempRating.T60) + "\n" +
				"    at 75°C: " + condProp.getAmpacity(Metal.COPPER, TempRating.T75) + "\n" +
				"    at 90°C: " + condProp.getAmpacity(Metal.COPPER,TempRating.T90) + "\n" +
				"ampacity of aluminum wires (Amps):\n" +
				"    at 60°C: " + condProp.getAmpacity(Metal.ALUMINUM, TempRating.T60) + "\n" +
				"    at 75°C: " + condProp.getAmpacity(Metal.ALUMINUM, TempRating.T75) + "\n" +
				"    at 90°C: " + condProp.getAmpacity(Metal.ALUMINUM, TempRating.T90) + "\n" +
				"reactance all wires (Ω/1000 FT):\n" +
				"  in non magnetic conduit: " + condProp.getReactance(Magnetic.NO) + "\n" +
				"      in magnetic conduit: " + condProp.getReactance(Magnetic.YES) + "\n" +
				"resistance of copper wires (Ω/1000 FT):\n" +
				"     in PVC conduit: " + condProp.getACResistance(Metal.COPPER, Material.PVC) + "\n" +
				"      in AL conduit: " + condProp.getACResistance(Metal.COPPER, Material.ALUMINUM) + "\n" +
				"   in steel conduit: " + condProp.getACResistance(Metal.COPPER, Material.STEEL) + "\n" +
				"        dc uncoated: " + condProp.getDCResistance(Metal.COPPER, Coating.UNCOATED) + "\n" +
				"          dc coated: " + condProp.getDCResistance(Metal.COPPER, Coating.COATED) + "\n" +
				"resistance of aluminum wires (Ω/1000 FT):\n" +
				"     in PVC conduit: " + condProp.getACResistance(Metal.ALUMINUM, Material.PVC) + "\n" +
				"      in AL conduit: " + condProp.getACResistance(Metal.ALUMINUM, Material.ALUMINUM) + "\n" +
				"   in steel conduit: " + condProp.getACResistance(Metal.ALUMINUM, Material.STEEL) + "\n" +
				"                 dc: " + condProp.getDCResistance(Metal.ALUMINUM, Coating.COATED) + "\n" +
				"Area in circular mils: " + condProp.getAreaCM() + "\n");
		System.out.println("isValidSize: " + CondProp.isValidSize(condProp.getSize()));
		System.out.println("isInvalid: " + condProp.isInvalid());

		//if (true) return;
		//endregion

		//region testing tables
		System.out.println("\n**************** TESTING TABLES ****************");
		String insulation = Insul.ZW;
		System.out.println("Temperature of " + insulation + " is: " + CondProp.getInsulationTemperatureCelsius(insulation));
		System.out.println("isValidInsulationName: " + CondProp.isValidInsulationName(insulation));
		String wire = Size.S1;
		condProp = CondProp.bySize(wire);
		//insulation = "FEP";
		System.out.println("\nArea of conductor size '" + condProp.getFullSizeName() + "' and insulation '" + insulation + "' is: " +
				condProp.getInsulatedAreaIn2(insulation) + "\n");
		System.out.println("isValidSize: " + CondProp.isValidSize(wire));
		System.out.println("isValidInsulationName: " + CondProp.isValidInsulationName(insulation));
		System.out.println("hasInsulatedArea: " + condProp.hasInsulatedArea(insulation));
//		System.out.println("hasCompactBareArea: " + CondProp.hasCompactBareArea());

		//if (true) return;
		wire = Size.S8;
		condProp = CondProp.bySize(wire);
		System.out.println("\n\nArea of bare compact conductor size \"" + condProp.getFullSizeName() + "\" is: " +
				condProp.getCompactBareAreaIn2());
		wire = Size.K300;
		condProp = CondProp.bySize(wire);
		System.out.println("Now, area of bare compact conductor size \"" + condProp.getFullSizeName() + "\" is: " +
				condProp.getCompactBareAreaIn2());

		wire = Size.K1000;
		condProp = CondProp.bySize(wire);
		insulation = Insul.RHH;
		System.out.println("Area of compact conductor size \"" + condProp.getFullSizeName() + "\" and " +
				"insulation \"" + insulation + "\" is: " +
				condProp.getCompactAreaIn2(insulation));
		//endregion*/
	}
}
/*
Release notes.

Voltage drop:
-------------
Method: it is assumed that the load behaves as a constant-current load.
Given the magnitude of the current and the power factor of the load, the complex current Icx is:
 Icx = I<-ArcCos(pf).
Since the length, the conductor size and the conduit type are known, the impedance of one way run of the conductor Zw is:
Zw = R + jXL

Where R and XL are the AC resistance and the reactance of the wires for that length (values obtained from table 9 are per 1000 ft, so
those values are divided by 1000 and then multiplied by the conductor length)

The voltage across the wires Vw is:

Vw = k x Icx x Zw, where k = 2 for 1-phase systems and square root of 3 for 3-phase systems.

The voltage at the source is know, and an angle of zero is taken as reference.
Vs = V<0

The voltage at the load is calculated as follow:
VL = Vs - Vw

And the voltage drop per IEEE is:
Vdrop = |Vs| - |VL|

Vdrop% = 100 x Vdrop/|Vs|

Notice that:
NEC table 9 provides reactances and resistances for the following conditions: 3 phases, 60 hz, 75 deg C, 3 single conductors per
conduit. When using several sets, it is imperative that each set be in a separate conduit. That is no more than three conductors per
raceway.

Is these table's values permitted to be used for single phase system? I need to check what IEEE says about it, because I think I read
that using table 9 for the 2-phase condition is ok.

A more advanced version of this program would be by adding calculation of AC and DC resistance per actual conditions, like
temperature, number of conductors in the same conduit, 2-phase scenario, underground, duct bank, and others if any.

------------------------///------------------------

Raceway calculation:
A group of N conductors will go in one raceway. The number of equal size conductors is known for each size within the raceway.
For example:
Qty     Size:
1       10
3       8
3       1/0
1       1

Using the data provided by the Conduit class, the
*/