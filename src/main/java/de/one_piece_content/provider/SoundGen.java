package de.one_piece_content.provider;

import de.one_piece_content.ExampleMod;
import de.one_piece_content.registries.MySounds;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;

import java.util.concurrent.CompletableFuture;

public class SoundGen extends SimpleSoundGeneratorV2 {
    public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateSounds(Builder builder) {
        builder.entries.add(new Entry(ExampleMod.MOD_ID,
                        MySounds.entries.stream()
                                .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                                .toList()
                )
        );
    }
}
