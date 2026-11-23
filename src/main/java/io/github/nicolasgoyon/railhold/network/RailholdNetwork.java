package io.github.nicolasgoyon.railhold.network;

import io.github.nicolasgoyon.railhold.client.ClientCartLinks;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Network payloads of Railhold. The handlers of the client payloads run only on the client.
 * {@link ClientCartLinks} has no client types, so this class also loads on a dedicated server.
 */
public final class RailholdNetwork {
    private static final String VERSION = "1";

    private RailholdNetwork() {
    }

    /**
     * Sends a payload to a player. A connection with no channel for the payload (a mock player of the GameTests)
     * gets nothing: the payload is dropped.
     *
     * <p>{@link PacketDistributor#sendToPlayersTrackingEntity} does not do this check: a connection with no channel
     * makes it throw an exception.
     *
     * @param player  the player
     * @param payload the payload
     */
    public static void send(ServerPlayer player, CustomPacketPayload payload) {
        if (player.connection != null && player.connection.hasChannel(payload)) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToClient(CartLinkPayload.TYPE, CartLinkPayload.STREAM_CODEC,
                (payload, context) -> ClientCartLinks.set(payload.cartId(), payload.frontId(), payload.backId()));
    }
}
