package test.java;

import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorPropertiesTest {

    @Test
    void getAllowedSize() {
        assertEquals(Size.AWG_1, ConductorProperties.getAllowedSize(144.23, Metal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_1$0, ConductorProperties.getAllowedSize(144.23, Metal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_2$0, ConductorProperties.getAllowedSize(144.23, Metal.COPPER, TempRating.T60));
    }
}