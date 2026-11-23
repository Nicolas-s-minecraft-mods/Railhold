package io.github.nicolasgoyon.railhold;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RailholdTest {
    @Test
    void modIdIsRailhold() {
        assertEquals("railhold", Railhold.MOD_ID);
    }
}
