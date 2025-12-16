package de.one_piece_content;

import de.one_piece_api.content.OnePieceDatagen;
import de.one_piece_content.content.*;
import de.one_piece_content.provider.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleDatagen extends OnePieceDatagen {
    public static final String MOD_ID = "one_piece_content_datagen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        LOGGER.info("Example Content datagen initializing");

        // Initialize your content
        ExampleClasses.init();
        ExampleStyles.init();
        ExampleSpells.init();
        ExampleDevilFruits.init();
        ExampleSkillDefinitions.init();
        ExampleSkills.init();
        ExampleConnections.init();

        // Create pack first
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // Add your custom providers
        pack.addProvider(LangGen::new);
        pack.addProvider(SoundGen::new);

        // Call super to add OnePiece providers
        addOnePieceProviders(pack);
    }
}