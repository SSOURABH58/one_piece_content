package de.one_piece_content.network;

import de.one_piece_content.client.AnimationBridge;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;

public class ContentNetworking {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(SandSpikePayload.ID, SandSpikePayload.CODEC);
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(SandSpikePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // System.out.println("DEBUG: ContentNetworking Client Received SandSpikePayload
                // for Entity: " + payload.entityId());
                if (context.player() != null && payload.entityId() == context.player().getId()) {
                    System.out.println("DEBUG: ContentNetworking - Triggering Animation for Local Player!");
                    AnimationBridge.triggerSandSpikeAnimation(20);
                }
            });
        });
    }

    public static void sendToTrackersAndSelf(Entity entity) {
        if (entity.getWorld().isClient)
            return;
        SandSpikePayload payload = new SandSpikePayload(entity.getId());

        // Send to tracking players
        for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, payload);
        }

        // Send to self if player
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }
}
