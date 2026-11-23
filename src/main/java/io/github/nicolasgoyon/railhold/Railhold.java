package io.github.nicolasgoyon.railhold;

import com.mojang.logging.LogUtils;
import io.github.nicolasgoyon.railhold.config.RailholdConfig;
import io.github.nicolasgoyon.railhold.network.RailholdNetwork;
import io.github.nicolasgoyon.railhold.rail.ChainUse;
import io.github.nicolasgoyon.railhold.rail.TrainChunks;
import io.github.nicolasgoyon.railhold.rail.Trains;
import io.github.nicolasgoyon.railhold.registry.ModAttachments;
import io.github.nicolasgoyon.railhold.registry.ModEntities;
import io.github.nicolasgoyon.railhold.registry.ModItems;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/** The Railhold mod: trains of minecarts with chain links, locomotives and storage minecarts. */
@Mod(Railhold.MOD_ID)
public final class Railhold {
    /** The id of the mod. */
    public static final String MOD_ID = "railhold";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Railhold(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, RailholdConfig.SPEC);
        ModAttachments.register(modEventBus);
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        modEventBus.addListener(RailholdNetwork::register);
        NeoForge.EVENT_BUS.addListener(ChainUse::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(ChainUse::onLoggedOut);
        NeoForge.EVENT_BUS.addListener(ChainUse::onServerStopped);
        NeoForge.EVENT_BUS.addListener(Trains::onLevelTick);
        NeoForge.EVENT_BUS.addListener(Trains::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(Trains::onEntityLeave);
        // Lowest priority: the links break only when no other listener cancels the travel.
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, Trains::onTravelToDimension);
        NeoForge.EVENT_BUS.addListener(Trains::onStartTracking);
        NeoForge.EVENT_BUS.addListener(Trains::onStopTracking);
        NeoForge.EVENT_BUS.addListener(Trains::onServerStopped);
        NeoForge.EVENT_BUS.addListener(TrainChunks::onServerStopped);
        LOGGER.debug("{} {} starts", MOD_ID, container.getModInfo().getVersion());
    }
}
