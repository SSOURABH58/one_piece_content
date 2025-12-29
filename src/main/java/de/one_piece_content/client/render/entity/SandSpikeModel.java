package de.one_piece_content.client.render.entity;

import de.one_piece_content.entity.SandSpikeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SandSpikeModel extends GeoModel<SandSpikeEntity> {
    @Override
    public Identifier getModelResource(SandSpikeEntity object) {
        return Identifier.of("one_piece_content", "geo/sand_spike.geo.json");
    }

    @Override
    public Identifier getTextureResource(SandSpikeEntity object) {
        return Identifier.of("one_piece_content", "textures/spell/sand_spike.png");
    }

    @Override
    public Identifier getAnimationResource(SandSpikeEntity object) {
        return Identifier.of("one_piece_content", "animations/sand_spike.animation.json");
    }
}
