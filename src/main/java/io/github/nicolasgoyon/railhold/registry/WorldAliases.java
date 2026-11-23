package io.github.nicolasgoyon.railhold.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The ids of the worlds of Folkhold Balance. A world of Folkhold Balance saves the items, the entities and the chain
 * links of the trains with the namespace {@code folkhold_balance}. The registries of Railhold read each of these ids as
 * the id of Railhold with the same path. Thus such a world opens with Railhold and keeps its trains.
 *
 * <p>An alias applies only when the registry has no entry with the id. When Folkhold Balance is also loaded, its
 * entries keep their ids.
 */
public final class WorldAliases {
    /** The namespace of the ids of Folkhold Balance. */
    public static final String FOLKHOLD_BALANCE = "folkhold_balance";

    private WorldAliases() {
    }

    /**
     * Adds an alias {@code folkhold_balance:<path>} for each entry of a register. Call this method before the
     * registration of the register on the mod event bus.
     *
     * @param register the register
     */
    public static void add(DeferredRegister<?> register) {
        for (DeferredHolder<?, ?> entry : register.getEntries()) {
            register.addAlias(ResourceLocation.fromNamespaceAndPath(FOLKHOLD_BALANCE, entry.getId().getPath()),
                    entry.getId());
        }
    }
}
