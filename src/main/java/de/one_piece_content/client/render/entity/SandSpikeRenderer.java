package de.one_piece_content.client.render.entity;

import de.one_piece_content.entity.SandSpikeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SandSpikeRenderer extends GeoEntityRenderer<SandSpikeEntity> {
    public SandSpikeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SandSpikeModel());
    }

    private static final java.util.Set<Integer> triggeredIds = new java.util.HashSet<>();

    @Override
    public void render(SandSpikeEntity entity, float entityYaw, float partialTick,
            net.minecraft.client.util.math.MatrixStack poseStack,
            net.minecraft.client.render.VertexConsumerProvider bufferSource, int packedLight) {

        // Strict Visibility: Only render if we are in an active wave (Wave > 0)
        // This prevents "frozen rest pose" artifacts during the wait periods.
        if (entity.getWaveCounter() == 0) {
            return;
        }

        // Trigger animation exactly once per entity instance
        if (!triggeredIds.contains(entity.getId())) {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player != null && entity.getOwnerId() == client.player.getId()) {
                System.out.println("Renderer: MATCH! Triggering Animation. Age: " + entity.age);
                // 2.29s ~= 46 ticks
                de.one_piece_content.client.AnimationBridge.triggerSandSpikeAnimation(46);
                triggeredIds.add(entity.getId());

                // Cleanup old IDs occasionally (simple heuristic)
                if (triggeredIds.size() > 100)
                    triggeredIds.clear();
            }
        }

        poseStack.push();
        // Removed manual scaling override (was 2.0x). Now renders at 1.0x native size.

        // Rotate to match entity facing
        poseStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-entityYaw));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
