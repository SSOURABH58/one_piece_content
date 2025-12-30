package de.one_piece_content.client.render.entity;

import de.one_piece_content.entity.SandSpikeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SandSpikeRenderer extends GeoEntityRenderer<SandSpikeEntity> {
    public SandSpikeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SandSpikeModel());
    }

    @Override
    public void render(SandSpikeEntity entity, float entityYaw, float partialTick,
            net.minecraft.client.util.math.MatrixStack poseStack,
            net.minecraft.client.render.VertexConsumerProvider bufferSource, int packedLight) {

        // Trigger Player Animation if this spike belongs to the local player
        // Check for first 20 ticks to allow sync-up
        // Check for first 20 ticks to allow sync-up
        if (entity.age < 100) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player != null) {
                System.out.println("Renderer: Spike Age: " + entity.age + ", OwnerID: " + entity.getOwnerId()
                        + ", PlayerID: " + client.player.getId());
                if (entity.getOwnerId() == client.player.getId()) {
                    System.out.println("Renderer: MATCH! Triggering Animation.");
                    de.one_piece_content.client.AnimationBridge.triggerSandSpikeAnimation(20);
                }
            }
        }

        poseStack.push();
        float scale = de.one_piece_content.config.SandSpikeConfig.scale;
        poseStack.scale(scale, scale, scale);
        // Rotate to match entity facing
        poseStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-entityYaw));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
