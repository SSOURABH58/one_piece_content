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
        poseStack.push();
        float scale = de.one_piece_content.config.SandSpikeConfig.scale;
        poseStack.scale(scale, scale, scale);
        // Rotate to match entity facing
        poseStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-entityYaw));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
