package io.github.nicolasgoyon.railhold;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/** The Railhold mod: trains of minecarts with chain links, locomotives and storage minecarts. */
@Mod(Railhold.MOD_ID)
public final class Railhold {
    /** The id of the mod. */
    public static final String MOD_ID = "railhold";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Railhold(IEventBus modEventBus, ModContainer container) {
        LOGGER.debug("{} {} starts", MOD_ID, container.getModInfo().getVersion());
    }
}
