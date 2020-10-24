package test.java;

import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConduitPropertiesTest {

	@Test
	void getArea() {
		assertEquals(0, ConduitProperties.getArea(Type.EMT, Trade.T3$8));
		assertEquals(0.304, ConduitProperties.getArea(Type.EMT, Trade.T1$2));
		assertEquals(0, ConduitProperties.getArea(Type.EMT, Trade.T6));

		assertEquals(0, ConduitProperties.getArea(Type.IMC, Trade.T3$8));
		assertEquals(3.63, ConduitProperties.getArea(Type.IMC, Trade.T2));
		assertEquals(0, ConduitProperties.getArea(Type.IMC, Trade.T6));

		assertEquals(0.192, ConduitProperties.getArea(Type.LFMC, Trade.T3$8));
		assertEquals(12.692, ConduitProperties.getArea(Type.LFMC, Trade.T4));
		assertEquals(0, ConduitProperties.getArea(Type.LFMC, Trade.T5));

		assertEquals(0, ConduitProperties.getArea(Type.PVCEB, Trade.T3$8));
		assertEquals(3.874, ConduitProperties.getArea(Type.PVCEB, Trade.T2));
		assertEquals(31.53, ConduitProperties.getArea(Type.PVCEB, Trade.T6));

		assertEquals(0, ConduitProperties.getArea(null, Trade.T6));
		assertEquals(0, ConduitProperties.getArea(Type.PVCEB, null));
		assertEquals(0, ConduitProperties.getArea(null, null));

	}
}