package de.one_piece_content.entity;

import de.one_piece_content.ExampleMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class OnePieceEntities {

    public static EntityType<SandSpikeEntity> SAND_SPIKE;

    public static void register() {
        SAND_SPIKE = Registry.register(
                Registries.ENTITY_TYPE,
                ExampleMod.id("sand_spike"),
                EntityType.Builder.create(SandSpikeEntity::new, SpawnGroup.MISC)
                        .dimensions(1f, 2f)
                        .build());
    }
}
