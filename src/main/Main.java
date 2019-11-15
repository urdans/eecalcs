package main;

import eecalcs.conductors.*;
import eecalcs.conduits.*;
import eecalcs.systems.SystemAC;
import eecalcs.systems.TempRating;
import eecalcs.voltagedrop.VDrop;
import tools.Message;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//import Size;
public class Main {

	public static void main(String[] args) {
		//testing cloning a cable
		Cable cab1 = new Cable(SystemAC.Voltage.v120_1ph, SystemAC.Wires.W2, 1.23);
		cab1.setJacketed(true);
		cab1.setLength(125);
		cab1.setMetal(Metal.ALUMINUM);
		cab1.setPhaseConductorSize(Size.AWG_1);
		System.out.println("cab1: " + cab1.getDescription()+" "+cab1.getLength());
		Cable cab2 = cab1.clone();
		System.out.println("cab2: " + cab2.getDescription()+" "+cab2.getLength());
		cab2.setPhaseConductorSize(Size.AWG_2);
		cab2.setGroundingConductorSize(Size.AWG_8);
		System.out.println("cab1: " + cab1.getDescription()+" "+cab1.getLength());
		System.out.println("cab2: " + cab2.getDescription()+" "+cab2.getLength());

/*		if(true)
			return;*/






		Trade tradeSize;
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
//		df.set
		//region testing conduit sizes
		TempRating tr = TempRating.T60;
		System.out.println(tr + ": " + tr.getValue());

		System.out.println("**************** Testing conduit sizes ****************");
		System.out.println("Conduit type string names:");
		System.out.println(String.join("|", Type.getNames()));
		System.out.println("Conduit string trade sizes:");
		System.out.println(String.join("|", Trade.getNames()));

		System.out.println();
		String tradeSizeS = " 2\" ";
		tradeSize = Trade.T2;

		String typeS = " PVC-40  ";
		Type type = Type.PVC40;

		System.out.println("Conduit type : " + typeS + " | Trade size : " + tradeSizeS);
		System.out.println("          getTypeByString(typeS) : " + ConduitProperties.getTypeByString(typeS));
		System.out.println("getTradeSizeByString(tradeSizeS) : " + ConduitProperties.getTradeSizeByString(tradeSizeS) + "\n");

		System.out.println("1. isValidType  : " + ConduitProperties.isValidType(typeS));
		System.out.println("2. isValidTrade : " + ConduitProperties.isValidTrade(tradeSizeS));
		System.out.println("3. hasArea      : " + ConduitProperties.hasArea(type,tradeSize));
		System.out.println("4. getArea      : " + ConduitProperties.getArea(type,tradeSize) + "\n");//*/
		//endregion

		//region testing conduit calculations
		System.out.println("**************** Testing conduit calculations ****************");

		Conduit conduit = new Conduit(Type.EMT, Conduit.Nipple.No);
		Conductor conductor1 = new Conductor();
		Conductor conductor2 = new Conductor();
		Conductor conductor3 = new Conductor();
		Conductor conductor4 = new Conductor();
		Cable cable1 = new Cable(SystemAC.Voltage.v120_1ph, SystemAC.Wires.W4, 0.8);

		conductor1.setInsulation(Insul.THHW); conductor1.setSize(Size.AWG_1$0);
		conductor2.setInsulation(Insul.THHW); conductor2.setSize(Size.AWG_1$0);
		conductor3.setInsulation(Insul.THHW); conductor3.setSize(Size.AWG_1$0);
		conductor4.setInsulation(Insul.THHW); conductor4.setSize(Size.AWG_2);

		conduit.add(conductor2);
		conduit.add(cable1);

		System.out.println("Minimum allowed trade size: " + conduit.getMinimumTrade().getName());
		System.out.println("              Conduit type: " + conduit.getType().getName());
		System.out.println("                Is nipple?: " + conduit.isNipple());
		System.out.println("         Conductors number: " + conduit.getConductorsNumber());
		System.out.println("    Conductor descriptions: ");
		conduit.getConduitables().forEach(conduitable -> System.out.println("\t\t\t\t\t\t\t" + conduitable.getDescription()));
		/*{
			List<String> sizes = new ArrayList<>();
			for (Conduitable conduitable : conduit.getConduitables()) {
				if(conduitable instanceof Conductor)
					sizes.add(((Conductor) conduitable).getSize().getName() + "-" + ((Conductor) conduitable).getInsulation().getName());
			}
			System.out.println("                     Sizes: " + String.join(", ", sizes));
		}*/
		System.out.println("     Calculated trade size: " +	conduit.getTradeSize().getName());
		System.out.println("     Conduit internal area: " + ConduitProperties.getArea(conduit.getType(), conduit.getTradeSize()));
		System.out.println("   Allowed fill percentage: " + conduit.getAllowedFillPercentage()+"%");
		System.out.println("     Total conductors area: " + String.format("%.4f",conduit.getConduitablesArea()));
		double fill = 100 * conduit.getConduitablesArea()/ConduitProperties.getArea(conduit.getType(), conduit.getTradeSize());
		System.out.println(" Actual filling percentage: " + String.format("%.1f", fill) + "%\n");

		System.out.println("********after changes and adding more conduitables***********");
		conduit.setNipple(Conduit.Nipple.Yes);
		conduit.setType(Type.PVC80);
		conduit.add(conductor3);
		conduit.add(conductor4);
		conduit.add((new Conductor(Size.KCMIL_300, Metal.COPPER, Insul.RHW2, 150)));
		conduit.add((new Conductor(Size.KCMIL_300, Metal.COPPER, Insul.RHW2, 150)));

		tradeSize = conduit.getTradeSize();
		System.out.println("Minimum allowed trade size: " + conduit.getMinimumTrade().getName());
		System.out.println("              Conduit type: " + conduit.getType().getName());
		System.out.println("                Is nipple?: " + conduit.isNipple());
		System.out.println("         Conductors number: " + conduit.getConductorsNumber());
		System.out.println("    Conductor descriptions: ");
		conduit.getConduitables().forEach(conduitable -> System.out.println("\t\t\t\t\t\t\t" + conduitable.getDescription()));
/*		{
			List<String> sizes = new ArrayList<>();
			for (Conduitable conduitable : conduit.getConduitables()) {
				if(conduitable instanceof Conductor)
					sizes.add(((Conductor) conduitable).getSize().getName() + "-" + ((Conductor) conduitable).getInsulation().getName());
			}
			System.out.println("                     Sizes: " + String.join(", ", sizes));
		}*/
		System.out.println("     Calculated trade size: " +	tradeSize.getName());
		System.out.println("     Conduit internal area: " + ConduitProperties.getArea(conduit.getType(), tradeSize));
		System.out.println("   Allowed fill percentage: " + conduit.getAllowedFillPercentage()+"%");
		System.out.println("     Total conductors area: " + String.format("%.4f", conduit.getConduitablesArea()));
		fill = 100 * conduit.getConduitablesArea()/ConduitProperties.getArea(conduit.getType(), tradeSize);
		System.out.println(" Actual filling percentage: " + String.format("%.1f", fill) + "%\n");
		//endregion

		//region testing conduitables and ampacities
		//all these four conductors are in the same conduit
		conductor1.setAmbientTemperatureF(150);
		conductor2.setRole(Conductor.Role.HOT);
		conductor3.setRole(Conductor.Role.HOT);
		conductor4.setRole(Conductor.Role.HOT);
//		conduit.add(new Conductor(conductor4));
		conduit.add(conductor4.clone());

/*		System.out.println("conductor 1 ambient temperature: " + conductor1.getAmbientTemperatureF());
		System.out.println("conductor 2 ambient temperature: " + conductor2.getAmbientTemperatureF());
		System.out.println("conductor 3 ambient temperature: " + conductor3.getAmbientTemperatureF());
		System.out.println("conductor 4 ambient temperature: " + conductor4.getAmbientTemperatureF());
		System.out.println("      cable ambient temperature: " + cable1.getAmbientTemperatureF());
		System.out.println("************* adding conductor 1 to conduit ***************");
		conduit.add(conductor1);
		System.out.println("conductor 1 ambient temperature: " + conductor1.getAmbientTemperatureF());
		System.out.println("conductor 2 ambient temperature: " + conductor2.getAmbientTemperatureF());
		System.out.println("conductor 3 ambient temperature: " + conductor3.getAmbientTemperatureF());
		System.out.println("conductor 4 ambient temperature: " + conductor4.getAmbientTemperatureF());
		System.out.println("      cable ambient temperature: " + cable1.getAmbientTemperatureF());*/


//		if(true) return;
		conduit.add(conductor1);

		conductor1.setLength(25);
		conductor1.setSize(Size.AWG_12);
		conduit.setNipple(Conduit.Nipple.No);

		System.out.println("**************** TESTING CONDUITABLES PROPERTIES ****************");
		System.out.println("**************** CONDUCTORS ****************");
		System.out.println("Conductor to be analized: conductor1");
		System.out.println("     Ambient temperature: " + conductor1.getAmbientTemperatureF() + " °F");
		System.out.println("              Insulation: " + conductor1.getInsulation().getName());
		System.out.println("      Temperature rating: " + conductor1.getTemperatureRating().getValue() + " °C");
		System.out.println("                    Size: " + conductor1.getSize().getName());
		System.out.println("                   Metal: " + conductor1.getMetal());
		System.out.println("                  Length: " + conductor1.getLength() + " FT");
		System.out.println("             Has conduit: " + conductor1.hasConduit());
		System.out.println("          Rated ampacity: " + ConductorProperties.getAmpacity(conductor1.getSize(), conductor1.getMetal(),
				conductor1.getTemperatureRating()));
		System.out.println("       Correction factor: " + Factors.getTemperatureCorrectionF(conductor1.getAmbientTemperatureF(),
				conductor1.getTemperatureRating()));
		System.out.println("       Adjustment factor: " + Factors.getAdjustmentFactor(conductor1.getConduit().getCurrentCarryingNumber(),
				conductor1.getConduit().isNipple()));
		//todo I should use here conductor1.getAdjustmentFactor()
		System.out.println("          Final Ampacity: " + df.format(conductor1.getAmpacity())/*String.format("%.2f", conductor1.getAmpacity())*/ + " AMPS");

		System.out.println("**************** CABLES ****************");

//		Cable cable1 = new Cable(SystemAC.Voltage.v120_1ph, SystemAC.Wires.W4, 0.8);

		System.out.println("    Cable to be analized: cable1");
		System.out.println("     Ambient temperature: " + cable1.getAmbientTemperatureF() + " °F");
		System.out.println("              Insulation: " + cable1.getInsulation().getName());
		System.out.println("      Temperature rating: " + cable1.getTemperatureRating().getValue() + " °C");
		System.out.println("    Hot conductors' size: " + cable1.getPhaseConductorSize().getName());
		System.out.println("Neutral conductors' size: " + cable1.getNeutralConductorSize().getName());
		System.out.println(" Grounding conduct. size: " + cable1.getGroundingConductorSize().getName());
		System.out.println("    Cross sectional area: " + String.format("%.4f in2",cable1.getInsulatedAreaIn2()));
		System.out.println("                   Metal: " + cable1.getMetal());
		System.out.println("                  Length: " + cable1.getLength() + " FT");
		System.out.println("             Has conduit: " + cable1.hasConduit());
		/*System.out.println("          Rated ampacity: " + cable1.getAmpacity()/(Factors.getTemperatureCorrectionF(cable1.getAmbientTemperatureF(),
				cable1.getTemperatureRating())*Factors.getAdjustmentFactor(cable1.getConduit())));*/
		System.out.println("       Correction factor: " + Factors.getTemperatureCorrectionF(cable1.getAmbientTemperatureF(),
				cable1.getTemperatureRating()));
		/*System.out.println("       Adjustment factor: " + Factors.getAdjustmentFactor(cable1.getConduit()));*/
		System.out.println("          Final Ampacity: " + df.format(cable1.getAmpacity())/*String.format("%.2f", cable1.getAmpacity())*/ + " AMPS");
		System.out.println("**************** CONTAINER CONDUIT ****************");
		if(conductor1.hasConduit()) {
			System.out.println("       Conduit is nipple: " + conductor1.getConduit().isNipple());
			System.out.println("            Conduit type: " + conductor1.getConduit().getType().getName());
			System.out.println("    Number of conductors: " + conductor1.getConduit().getConductorsNumber());
			System.out.println("           Number of CCC: " + conductor1.getConduit().getCurrentCarryingNumber());
			conduit.getConduitables().forEach(conduitable -> System.out.println("\t\t\t\t\t\t\t" + conduitable.getDescription()));
/*			{
				List<String> sizes = new ArrayList<>();
				for (Conduitable conduitable : conduit.getConduitables()) {
					if(conduitable instanceof Conductor)
						sizes.add(((Conductor) conduitable).getSize().getName() + "-" + ((Conductor) conduitable).getInsulation().getName() + "-" + ((Conductor) conduitable).getRole());
				}
				System.out.println("                     Sizes: " + String.join(", ", sizes));
			}*/
			System.out.println(" Conduit calculated size: " + conductor1.getConduit().getTradeSize().getName());
			System.out.println(" Allowed fill percentage: " + conductor1.getConduit().getAllowedFillPercentage());
		}//*/

		if(true) return;
		//endregion

		//region testing conductor properties
		System.out.println("**************** TESTING CONDUCTOR PROPERTIES ****************");
		System.out.println("Conductor string names:");
		System.out.println(String.join("|", Size.getNames()));
		System.out.println("Conductor insulation names:");
		System.out.println(String.join("|",  Insul.getNames()));
		System.out.println();

		Size size = Size.KCMIL_1250;
		System.out.println("Properties of conductor " + size.getName() + ":\n" +
				"ampacity of copper wires (Amps):\n" +
				"    at 60°C: " + ConductorProperties.getAmpacity(size, Metal.COPPER, TempRating.T60) + "\n" +
				"    at 75°C: " + ConductorProperties.getAmpacity(size, Metal.COPPER, TempRating.T75) + "\n" +
				"    at 90°C: " + ConductorProperties.getAmpacity(size, Metal.COPPER,TempRating.T90) + "\n" +
				"ampacity of aluminum wires (Amps):\n" +
				"    at 60°C: " + ConductorProperties.getAmpacity(size, Metal.ALUMINUM, TempRating.T60) + "\n" +
				"    at 75°C: " + ConductorProperties.getAmpacity(size, Metal.ALUMINUM, TempRating.T75) + "\n" +
				"    at 90°C: " + ConductorProperties.getAmpacity(size, Metal.ALUMINUM, TempRating.T90) + "\n" +
				"reactance all wires (Ω/1000 FT):\n" +
				"  in non magnetic conduit: " + ConductorProperties.getReactance(size, ConduitProperties.nonMagnetic) + "\n" +
				"      in magnetic conduit: " + ConductorProperties.getReactance(size, ConduitProperties.Magnetic) + "\n" +
				"resistance of copper wires (Ω/1000 FT):\n" +
				"     in PVC conduit: " + ConductorProperties.getACResistance(size, Metal.COPPER, Material.PVC) + "\n" +
				"      in AL conduit: " + ConductorProperties.getACResistance(size, Metal.COPPER, Material.ALUMINUM) + "\n" +
				"   in steel conduit: " + ConductorProperties.getACResistance(size, Metal.COPPER, Material.STEEL) + "\n" +
				"        dc uncoated: " + ConductorProperties.getDCResistance(size, Metal.COPPER, Coating.UNCOATED) + "\n" +
				"          dc coated: " + ConductorProperties.getDCResistance(size, Metal.COPPER, Coating.COATED) + "\n" +
				"resistance of aluminum wires (Ω/1000 FT):\n" +
				"     in PVC conduit: " + ConductorProperties.getACResistance(size, Metal.ALUMINUM, Material.PVC) + "\n" +
				"      in AL conduit: " + ConductorProperties.getACResistance(size, Metal.ALUMINUM, Material.ALUMINUM) + "\n" +
				"   in steel conduit: " + ConductorProperties.getACResistance(size, Metal.ALUMINUM, Material.STEEL) + "\n" +
				"                 dc: " + ConductorProperties.getDCResistance(size, Metal.ALUMINUM, Coating.COATED) + "\n" +
				"Area in circular mils: " + ConductorProperties.getAreaCM(size) + "\n");
		System.out.println("isValidFullName(\" AGW 4/0 \"): " + ConductorProperties.isValidFullName(" AWG 4/0 "));
		System.out.println("isValidInsulStringName(\"  XHHW-2 \"): " + ConductorProperties.isValidInsulStringName("  XHHW-2 ") );
		//endregion

/*		System.out.println("Testing creating a circuit out of an existing conductor object");
		Conductor c1 = new Conductor(Size.S10, Metal.ALUMINUM, Insul.TW, 50);
		Circuit ckt1 = new Circuit(c1);
		ckt1.setConduitMaterial(ConduitProperties.Material.STEEL);

		Circuit ckt2 = new Circuit(ckt1);

		ckt1.setSize(Size.S8);
		ckt1.setAmbientTemperatureC(40);
		ckt1.setNumberOfSets(2);
		ckt1.setConduitMaterial(ConduitProperties.Material.ALUMINUM);
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
				"\t" + ckt2.getSize() + "\t" + ckt2.getTemperatureRating());*/

		//region ampacity of conductors and conduit sizes tests
		System.out.println("\n**************** Ampacity of conductors ****************");
		System.out.println(String.format("%5s","60°C")+String.format("%5s","75°C")+
				String.format("%5s","90°C"));
		for(Size conductorSize: Size.values()) {
			System.out.println(String.format("%5.0f", ConductorProperties.getAmpacity(conductorSize, Metal.COPPER, TempRating.T60)) +
					String.format("%5.0f", ConductorProperties.getAmpacity(conductorSize, Metal.COPPER,TempRating.T75))+ String.format(
							"%5.0f", ConductorProperties.getAmpacity(conductorSize, Metal.COPPER, TempRating.T90)));
		}

		System.out.println("**************** Testing a conductor ****************");
		Conductor myc = new Conductor(Size.KCMIL_250, Metal.COPPER, Insul.THW, 100);
		myc.setAmbientTemperatureF(95);
		double incArea = 0;
		double prevArea;
		double incAmp = 0;
		double prevAmp;
		System.out.println(String.format("%5s", myc.getSize()) + ": " + String.format("%5.0f", myc.getAmpacity()) + "\t CM: " +
				String.format("%7d", ConductorProperties.getAreaCM(myc.getSize())) + "\t Size inc: " + String.format("%4.0f", incArea) + "%\t Ampacity" +
				"inc: " + String.format("%7.0f", incAmp) + "% \tArea/Amp: " + String.format("%4.0f CM/AMP",
				ConductorProperties.getAreaCM(myc.getSize())/myc.getAmpacity()));

		System.out.println("\n**************** Testing incremental ampacity and areas of conductors ****************");
		System.out.println("Conductor metal: "+ myc.getMetal() + "\tInsulation: " + myc.getInsulation() + "\tTemp.rating: " +
				ConductorProperties.getTempRating(myc.getInsulation()) + "°C");
		myc.setSize(Size.AWG_14);
		prevArea = ConductorProperties.getAreaCM(myc.getSize());
		prevAmp = myc.getAmpacity();
		for(Size conductorSize : Size.values()){
			myc.setSize(conductorSize);
			double area = ConductorProperties.getAreaCM(myc.getSize());
			double ampacity = myc.getAmpacity();
			incArea = 100 * area/prevArea;
			prevArea = area;
			incAmp = 100 * ampacity/prevAmp;
			prevAmp = ampacity;
			System.out.println(String.format("%5s", conductorSize) + ": " + String.format("%5.1f", ampacity) + "\t CM: " +
					String.format("%7.0f", area) + "\t Size inc: " + String.format("%4.0f", incArea) + "%\t Ampacity inc: " +
					String.format("%7.0f", incAmp)+"% \tArea/Amp:" +
					" " + String.format("%4.0f CM/AMP", area/ampacity));
		}//*/
		//endregion

		//region testing voltage drop
		Conductor conductor = new Conductor();
		Circuit circuit = new Circuit(conductor);
		VDrop vd = new VDrop(circuit);
		System.out.println("\n**************** TESTING VOLTAGE DROP METHOD ****************");
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
		vd.getCircuit().setSize(Size.AWG_1$0);
		vd.setPhases(3);
		vd.getCircuit().setConduitMaterial(Material.ALUMINUM);
		//vd.getCircuit().setMetal(Metal.ALUMINUM);
		vd.getCircuit().setCopperCoated(Coating.COATED);
		vd.getCircuit().setNumberOfSets(2);
		vd.setLoadCurrent(130);
		vd.setPowerFactor(0.9);
		vd.getCircuit().setLength(350);

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
		}//*/
		//endregion

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
		System.out.println("        Conductor size: " + vd.getCalculatedSizeDC());
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
		System.out.println("        Conductor size: " + vd.getCalculatedSizeDC());
		System.out.println("   Actual voltage drop: " + String.format("%.2f", vd.getActualVoltageDropPercentageDC()) + "%");
		System.out.println("        Maximum length: " + String.format("%.0f", vd.getMaxLengthDC()));

		if (vd.resultMessages.hasMessages()) {
			System.out.println("The following errors ocurred:");
			for (Message msg : vd.resultMessages.getMessages()) {
				System.out.println(msg.message + " : " + msg.number);
			}
		}//*/
		//if (true) return;
		//endregion */



		//region testing tables
/*		System.out.println("\n**************** TESTING CONDUCTOR TABLES ****************");
		String insulation = Insul.ZW;
		System.out.println("Temperature of " + insulation + " is: " + ConductorProperties.getInsulationTemperatureCelsius(insulation));
		System.out.println("isValidInsulationName: " + ConductorProperties.isValidInsulationName(insulation));
		String wire = Size.S1;
		//condProp = CondProp.bySize(wire);
		//insulation = "FEP";
		System.out.println("\nArea of conductor size '" + ConductorProperties.getFullSizeName(wire) + "' and insulation '" + insulation +
				"' is: " + ConductorProperties.getInsulatedAreaIn2(wire, insulation) + "\n");
		System.out.println("isValidSize: " + ConductorProperties.isValidSize(wire));
		System.out.println("isValidInsulationName: " + ConductorProperties.isValidInsulationName(insulation));
		System.out.println("hasInsulatedArea: " + ConductorProperties.hasInsulatedArea(wire, insulation));
//		System.out.println("hasCompactBareArea: " + CondProp.hasCompactBareArea());

		//if (true) return;
		wire = Size.S8;
		//condProp = CondProp.bySize(wire);
		System.out.println("\n\nArea of bare compact conductor size \"" + ConductorProperties.getFullSizeName(wire) + "\" is: " +
				ConductorProperties.getCompactBareAreaIn2(wire));
		wire = Size.K300;
		//condProp = CondProp.bySize(wire);
		System.out.println("Now, area of bare compact conductor size \"" + ConductorProperties.getFullSizeName(wire) + "\" is: " +
				ConductorProperties.getCompactBareAreaIn2(wire));

		wire = Size.K1000;
		//condProp = CondProp.bySize(wire);
		insulation = Insul.RHH;
		System.out.println("Area of compact conductor size \"" + ConductorProperties.getFullSizeName(wire) + "\" and " +
				"insulation \"" + insulation + "\" is: " +
				ConductorProperties.getCompactAreaIn2(wire, insulation));*/
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