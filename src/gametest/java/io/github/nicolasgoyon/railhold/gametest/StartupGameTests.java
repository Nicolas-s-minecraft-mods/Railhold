package io.github.nicolasgoyon.railhold.gametest;

import io.github.nicolasgoyon.railhold.Railhold;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** In-game tests for the start of the mod. */
@GameTestHolder(Railhold.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StartupGameTests {
    private StartupGameTests() {
    }

    /** The server loads the mod. */
    @GameTest(template = "empty", timeoutTicks = 20)
    public static void modIsLoaded(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded(Railhold.MOD_ID), "The mod must be loaded");
        helper.succeed();
    }
}
