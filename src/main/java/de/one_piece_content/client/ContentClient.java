package de.one_piece_content.client;

import de.one_piece_content.client.render.entity.SandSpikeRenderer;
import de.one_piece_content.entity.OnePieceEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ContentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("One Piece Content Client Initializing...");
        EntityRendererRegistry.register(
                OnePieceEntities.SAND_SPIKE,
                context -> {
                    System.out.println("Registering SandSpikeRenderer");
                    return new SandSpikeRenderer(context);
                });

        AnimationBridge.init();
        de.one_piece_content.network.ContentNetworking.initClient();
    }
}
