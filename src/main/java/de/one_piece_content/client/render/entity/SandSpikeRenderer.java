package de.one_piece_content.client.render.entity;

import de.one_piece_content.entity.SandSpikeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import de.one_piece_content.config.SandSpikeConfig;

public class SandSpikeRenderer extends GeoEntityRenderer<SandSpikeEntity> {
    public SandSpikeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SandSpikeModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(SandSpikeEntity entity, float entityYaw, float partialTick,
            net.minecraft.client.util.math.MatrixStack poseStack,
            net.minecraft.client.render.VertexConsumerProvider bufferSource, int packedLight) {

        poseStack.push();
        // Force the scale from config (default 2.0)
        float s = SandSpikeConfig.scale;
        poseStack.scale(s, s, s);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pop();
    }
}
