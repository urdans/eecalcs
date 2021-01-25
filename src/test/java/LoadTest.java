package test.java;

import eecalcs.loads.*;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadTest {
	Load generalLoad = new Load();

	@Test
	void setNonContinuous() {
		generalLoad.setNominalCurrent(200);
		generalLoad.setNonContinuous();
		assertEquals(200, generalLoad.getNominalCurrent());
		assertEquals(200, generalLoad.getMCA());
		assertEquals(1.0, generalLoad.getMCAMultiplier());
		assertEquals(LoadType.NONCONTINUOUS, generalLoad.getLoadType());
	}

	@Test
	void setContinuous() {
		generalLoad.setNominalCurrent(100);
		generalLoad.setContinuous();
		assertEquals(100, generalLoad.getNominalCurrent());
		assertEquals(125, generalLoad.getMCA());
		assertEquals(125/100.0, generalLoad.getMCAMultiplier());
		assertEquals(LoadType.CONTINUOUS, generalLoad.getLoadType());
	}

	@Test
	void setMixed() {
		generalLoad.setMixed(321.0);
		assertEquals(10, generalLoad.getNominalCurrent());
		assertEquals(321, generalLoad.getMCA());
		assertEquals(321/10.0, generalLoad.getMCAMultiplier());
		assertEquals(LoadType.MIXED, generalLoad.getLoadType());
	}

	@Test
	void setNominalCurrent() {
		assertEquals(10, generalLoad.getNominalCurrent());
		assertEquals(LoadType.NONCONTINUOUS, generalLoad.getLoadType());
		assertEquals(0, generalLoad.getOverloadRating());
		assertEquals(0, generalLoad.getDSRating());
		assertEquals(10, generalLoad.getMCA());

		generalLoad.setNominalCurrent(123);
		assertEquals(123, generalLoad.getNominalCurrent());
	}

	/*Testing features of the base OldLoad class*/
	@Test
	void loadConstructor(){
		Load load = new Load();
		assertEquals(VoltageSystemAC.v120_1ph_2w, load.getVoltageSystem());
		assertEquals(10.0, load.getNominalCurrent());
		assertEquals(10.0, load.getNeutralCurrent());
		assertEquals(1200.0, load.getVoltAmperes());
		assertEquals(1.0, load.getPowerFactor());
		assertEquals(1200.0, load.getWatts());
		assertEquals(10, load.getMCA());
		assertEquals(1, load.getMCAMultiplier());
		assertEquals(0.0, load.getMaxOCPDRating(false));
		assertEquals(0, load.getDSRating());
		assertEquals(0.0, load.getOverloadRating());
		assertNull(load.getDescription());

		load.setDescription("Induction heater");
		load.setVoltageSystem(VoltageSystemAC.v208_3ph_4w);
		load.setNominalCurrent(-20);
		load.setPowerFactor(-0.8);
		assertEquals(20.0, load.getNominalCurrent());
		assertEquals(20.0, load.getNeutralCurrent());
		assertEquals(208*20*Math.sqrt(3), load.getVoltAmperes());
		assertEquals(0.8, load.getPowerFactor());
		assertEquals(208*20*Math.sqrt(3)*0.8, load.getWatts());
		assertEquals(20, load.getMCA());
		assertEquals(1, load.getMCAMultiplier());
		assertEquals(0.0, load.getMaxOCPDRating(false));
		assertEquals(0.0, load.getDSRating());
		assertEquals(0.0, load.getOverloadRating());
		assertEquals("Induction heater", load.getDescription());

		load.setContinuous();
		assertEquals(20.0*1.25, load.getMCA());
		assertEquals(1.25, load.getMCAMultiplier());

		Load load2 = new Load(VoltageSystemAC.v240_1ph_3w, 25);
		assertEquals( LoadType.NONCONTINUOUS,
				load2.getLoadType());
		assertEquals(25.0, load2.getNominalCurrent());
		assertEquals(25.0, load2.getNeutralCurrent());
		assertEquals(240*25.0, load2.getVoltAmperes());
		assertEquals(1.0, load2.getPowerFactor());
		assertEquals(240*25.0, load2.getWatts());
		assertEquals(25.0, load2.getMCA());
		assertEquals(1, load2.getMCAMultiplier());
		assertEquals(0.0, load2.getMaxOCPDRating(false));
		assertEquals(0, load2.getDSRating());
		assertEquals(0.0, load2.getOverloadRating());
	}
}