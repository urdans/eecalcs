package test.java;

import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class EecalcsTestTest {
    Trade tradeSize;

    @Test
    void testConduitSizes(){
        //region testing conduit sizes
        System.out.println("**************** Testing conduit sizes ****************");
        System.out.println("Conduit type string names:");
        System.out.println(String.join("|", Type.getNames()));
        System.out.println("\nConduit string trade sizes:");
        System.out.println(String.join("|", Trade.getNames()));

        String tradeSizeS = " 5\" ";
        tradeSize = Trade.T5;

        String typeS = " EMT  ";
        Type type = Type.EMT;

        System.out.println("\nConduit type (strings): " + typeS + " | Trade size : " + tradeSizeS);
        System.out.println("          getTypeByString(typeS) : " + ConduitProperties.getTypeByString(typeS));
        assertEquals(type, ConduitProperties.getTypeByString(typeS));

        System.out.println("getTradeSizeByString(tradeSizeS) : " + ConduitProperties.getTradeSizeByString(tradeSizeS) + "\n");
        assertEquals(tradeSize, ConduitProperties.getTradeSizeByString(tradeSizeS));

        System.out.println("1. isValidType  : " + ConduitProperties.isValidType(typeS));
        assertTrue(ConduitProperties.isValidType(typeS));

        System.out.println("2. isValidTrade : " + ConduitProperties.isValidTrade(tradeSizeS));
        assertTrue(ConduitProperties.isValidTrade(tradeSizeS));

        System.out.println("3. hasArea      : " + ConduitProperties.hasArea(type,tradeSize));
        assertFalse(ConduitProperties.hasArea(type,tradeSize));

        System.out.println("4. getArea      : " + String.format("%.3f", ConduitProperties.getArea(type,tradeSize)));
        assertEquals(0, ConduitProperties.getArea(type,tradeSize));

        assertFalse(ConduitProperties.hasArea(Type.PVCEB,Trade.T2_1$2));
        assertFalse(ConduitProperties.hasArea(Type.PVCEB,Trade.T3$8));
        assertTrue(ConduitProperties.hasArea(Type.PVCEB,Trade.T2));
        assertEquals(3.874, ConduitProperties.getArea(Type.PVCEB,Trade.T2));
        assertFalse(ConduitProperties.isMagnetic(Type.PVCEB));
        assertTrue(ConduitProperties.isMagnetic(Material.STEEL));
        assertEquals(Material.ALUMINUM, ConduitProperties.getMaterial(Type.FMCAL));
        assertEquals(Material.PVC, ConduitProperties.getMaterial(Type.LFNCB));
        assertEquals(Material.STEEL, ConduitProperties.getMaterial(Type.RMC));
    }

}