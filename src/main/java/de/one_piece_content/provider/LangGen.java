package de.one_piece_content.provider;

import de.one_piece_content.ExampleMod;

import de.one_piece_api.server.config.skill.SkillDefinitionConfig;
import de.one_piece_api.server.config.spell.SpellConfig;
import de.one_piece_api.content.registry.Registries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class LangGen extends FabricLanguageProvider {
    public LangGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }



    public static String titleTranslationKey(Identifier id) {
        return "skill." + id.getPath() + "." + id.getNamespace() + ".title";
    }

    public static String descriptionTranslationKey(Identifier id) {
        return "skill." + id.getPath() + "." + id.getNamespace() + ".description";
    }


    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        for (var entry :  Registries.SKILL_DEFINITION.entries().entrySet()) {
            Identifier id = entry.getKey();
            if (!id.getNamespace().equals(ExampleMod.MOD_ID)) {
                SkillDefinitionConfig skill = entry.getValue();

                Text title = skill.title();
                Text description = skill.description();
                if (title != null && !title.toString().isEmpty()) {
                    builder.add(titleTranslationKey(id), title.getString());
                }
                if (description != null && !description.toString().isEmpty()) {
                    builder.add(descriptionTranslationKey(id), description.getString());
                }
            }
        }
        for (var spell :  Registries.SPELLS.entries().entrySet()) {
            Identifier id = spell.getKey();
            if (id.getNamespace().equals(ExampleMod.MOD_ID)) {
                SpellConfig spellEntry = spell.getValue();
                String langId = "spell."+ExampleMod.MOD_ID+"."+id.getPath();
                builder.add(langId+".name", spellEntry.title());
                builder.add(langId+".description", spellEntry.description());
            }
        }
        Registries.CLASSES.entries().forEach((id, config) -> {
            if (id.getNamespace().equals(ExampleMod.MOD_ID)) {
                String className = id.getPath();
                String displayName = StringUtil.capitalize(className);
                String description = "Auto-generated description for " + displayName + " class";

                builder.add("class."+ExampleMod.MOD_ID+"." + className + ".name", displayName);
                builder.add("class."+ExampleMod.MOD_ID+"." + className + ".description", description);
            }
        });


    }
}
