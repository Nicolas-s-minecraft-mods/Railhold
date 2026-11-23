package io.github.nicolasgoyon.railhold.registry;

import io.github.nicolasgoyon.railhold.Railhold;
import io.github.nicolasgoyon.railhold.rail.CartLink;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Data attachments of Railhold. */
public final class ModAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Railhold.MOD_ID);

    /**
     * The chain links of a minecart. The minecart saves them with its other data. The client does not get them: the
     * server sends the links in its own payload.
     */
    public static final Supplier<AttachmentType<CartLink>> CART_LINK = ATTACHMENT_TYPES.register("cart_link",
            () -> AttachmentType.builder(() -> CartLink.NONE)
                    .serialize(CartLink.CODEC, link -> !link.isEmpty())
                    .build());

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        WorldAliases.add(ATTACHMENT_TYPES);
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
