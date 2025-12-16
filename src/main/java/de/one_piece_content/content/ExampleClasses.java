package de.one_piece_content.content;

import de.one_piece_api.common.config.ClassConfig;
import de.one_piece_api.server.config.skill.SkillDefinitionConfig;
import de.one_piece_api.common.mixin_interface.SkillType;
import de.one_piece_api.server.reward.PassiveAbilityReward;
import de.one_piece_api.server.reward.SpellContainerReward;
import de.one_piece_content.ExampleMod;
import de.one_piece_api.content.builder.OnePieceClassBuilder;
import de.one_piece_api.content.data.Icon;
import de.one_piece_api.content.registry.Entry;
import de.one_piece_api.content.registry.Registries;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.puffish.skillsmod.config.skill.SkillRewardConfig;
import net.spell_engine.api.spell.container.SpellContainer;
import net.spell_engine.api.spell.container.SpellContainerHelper;

import java.util.List;

/**
 * Registry for character class definitions.
 * <p>
 * This class defines all available character classes (races) that players can
 * select at the start of the game. Each class has unique skills, colors, and
 * visual assets that define its identity and playstyle.
 *
 * <h2>Class Components:</h2>
 * Each class configuration includes:
 * <ul>
 *     <li>Primary skill - Main active ability</li>
 *     <li>Passive skill - Automatic passive ability</li>
 *     <li>Theme colors - Primary and secondary UI colors</li>
 *     <li>Textures - Background image and name badge</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * Call {@link #init()} during mod initialization to register all classes.
 *
 * @see ClassConfig
 * @see OnePieceClassBuilder
 */
public class ExampleClasses {


    /**
     * Swordsman Skill 2: Yakkodori.
     */
    public static final Entry<SkillDefinitionConfig> MASTER_OF_THE_SEAS = Registries.SKILL_DEFINITION.register(ExampleMod.id("master_of_the_seas"), masterOfTheSeas());
    public static SkillDefinitionConfig masterOfTheSeas() {
        SkillRewardConfig reward = new SkillRewardConfig(PassiveAbilityReward.ID, new PassiveAbilityReward(List.of(ExampleMod.id("master_of_the_seas"))));
        return new SkillDefinitionConfig(Text.literal("Unlock MASTER_OF_THE_SEAS"),
                Text.literal("[MASTER_OF_THE_SEAS Description text]"),
                null,
                Icon.item(Items.TROPICAL_FISH),
                null,
                1,
                List.of(reward),
                0, 0, 0, 0, 0
        );
    }

    public static final Entry<SkillDefinitionConfig> FISHMAN_2 = Registries.SKILL_DEFINITION.register(ExampleMod.id("fishman_active"), fishman_active());

    private static SkillDefinitionConfig fishman_active() {
        Identifier spellId = ExampleMod.id("shishi_sonson");
        SpellContainer container = SpellContainerHelper.createForMeleeWeapon(spellId);
        SkillRewardConfig reward = new SkillRewardConfig(SpellContainerReward.ID, new SpellContainerReward(null, List.of(container)));
        return new SkillDefinitionConfig(Text.literal("Unlock ShiShi Sonson"),
                Text.literal("Channels a fast, focused dash for 1 second, moving 5 blocks forward and slashing any target in the path"),
                null,
                Icon.spell(spellId),
                null,
                1,
                List.of(reward),
                0, 0, 0, 0, 0
        );
    }

    static {
        ExampleSkills.register(MASTER_OF_THE_SEAS.id(), false, 0, 0, SkillType.CLASS);
        ExampleSkills.register(FISHMAN_2.id(), false, 50, 0, SkillType.CLASS);
    }


    /**
     * Fishman class configuration.
     */
    public static final Entry<ClassConfig> FISHMAN = register("fishman", builder -> builder
            .addReward(1, MASTER_OF_THE_SEAS.id())
            .addReward(10, FISHMAN_2.id())
            .background(
                    ExampleMod.id("textures/classes/fishman.png"),
                    ExampleMod.id("textures/classes/fishman_name.png")
            )
    );

    /**
     * Human class configuration.
     */
    public static final Entry<ClassConfig> HUMAN = register("human", builder -> builder
            .background(
                    ExampleMod.id("textures/classes/human.png"),
                    ExampleMod.id("textures/classes/human_name.png")
            )
    );

    /**
     * Mink class configuration.
     */
    public static final Entry<ClassConfig> MINK = register("mink", builder -> builder
            .background(
                    ExampleMod.id("textures/classes/mink.png"),
                    ExampleMod.id("textures/classes/mink_name.png")
            )
    );

    /**
     * Registers a new character class.
     * <p>
     * Creates a class configuration using the builder pattern and registers it
     * with the mod's class registry. The class will be available for selection
     * in the class selection screen.
     *
     * @param name   the class name (used for ID and translation keys)
     * @param config function that configures the class builder
     * @return the registry entry for the configured class
     */
    private static Entry<ClassConfig> register(String name,
                                               java.util.function.Function<OnePieceClassBuilder, OnePieceClassBuilder> config) {
        Identifier id = ExampleMod.id(name);
        OnePieceClassBuilder builder = new OnePieceClassBuilder(id);
        ClassConfig classConfig = config.apply(builder).build();
        return Registries.CLASSES.register(id, classConfig);
    }

    /**
     * Initializes all character classes.
     * <p>
     * This method should be called during mod initialization to ensure all
     * classes are registered. The actual registration happens during static
     * initialization of the class fields, but calling this method forces
     * the class to load.
     */
    public static void init() {
        // Static initialization registers all classes
    }
}