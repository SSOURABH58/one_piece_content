package de.one_piece_content.content;

import de.one_piece_api.server.reward.SpellContainerReward;
import de.one_piece_content.ExampleMod;
import de.one_piece_api.content.data.Icon;
import de.one_piece_api.server.config.skill.SkillDefinitionConfig;
import de.one_piece_api.server.config.spell.SpellConfig;
import de.one_piece_api.content.registry.Entry;
import de.one_piece_api.content.registry.Registries;
import de.one_piece_api.content.rewards.AttributeReward;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.puffish.skillsmod.config.skill.SkillRewardConfig;
import net.spell_engine.api.spell.container.SpellContainer;
import net.spell_engine.api.spell.container.SpellContainerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registry for skill definitions in the skill tree.
 * <p>
 * This class defines all skills that can be unlocked in the skill tree,
 * including
 * their rewards, icons, costs, and requirements. Skills can grant various
 * benefits
 * such as spells, attribute modifiers, or other passive effects.
 *
 * <h2>Skill Types:</h2>
 * <ul>
 * <li><b>Spell Skills</b> - Grant new active or passive spells</li>
 * <li><b>Attribute Skills</b> - Provide permanent stat increases</li>
 * <li><b>Dummy Skills</b> - Test/placeholder skills for development</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * Call {@link #init()} during mod initialization to register all skill
 * definitions.
 *
 * @see SkillDefinitionConfig
 * @see SpellContainerReward
 * @see AttributeReward
 */
public class ExampleSkillDefinitions {

        /**
         * Initializes all skill definitions.
         * <p>
         * This method should be called during mod initialization to ensure all
         * skills are registered. The actual registration happens during static
         * initialization of the class fields, but calling this method forces
         * the class to load.
         */
        public static void init() {
        }

        /**
         * List of dummy skill entries for testing and development.
         * <p>
         * Creates placeholder skills using Minecraft item icons. These are useful
         * for testing skill tree layouts and progression systems without requiring
         * fully implemented abilities.
         * <p>
         * Contains 34 test skills using various Minecraft items as icons.
         */
        public static List<Entry<SkillDefinitionConfig>> ITEM_ENTRIES = Stream
                        .of(Items.SPYGLASS, Items.APPLE, Items.SWEET_BERRIES, Items.STONE_SWORD, Items.SPONGE,
                                        Items.SADDLE, Items.BEEHIVE,
                                        Items.DIAMOND_SWORD, Items.STONE_HOE, Items.DIAMOND_SHOVEL,
                                        Items.ACACIA_HANGING_SIGN, Items.ARMADILLO_SPAWN_EGG, Items.GOLD_ORE,
                                        Items.GREEN_GLAZED_TERRACOTTA,
                                        Items.GREEN_DYE, Items.CHERRY_LEAVES, Items.COOKED_RABBIT,
                                        Items.CREEPER_BANNER_PATTERN, Items.CUT_RED_SANDSTONE, Items.BUBBLE_CORAL_BLOCK,
                                        Items.CUT_COPPER_STAIRS,
                                        Items.BARRIER, Items.STONE_AXE, Items.ACACIA_BOAT, Items.NETHERITE_AXE,
                                        Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.LEVER, Items.SCULK_VEIN,
                                        Items.GLASS, Items.GLASS_BOTTLE, Items.GHAST_TEAR, Items.GLASS_PANE,
                                        Items.GLOW_BERRIES, Items.GLOW_LICHEN, Items.GOLD_BLOCK)
                        .map(net.minecraft.registry.Registries.ITEM::getId)
                        .map(ExampleSkillDefinitions::dummySkill).toList();

        /**
         * Creates a dummy skill definition using an item as the icon.
         * <p>
         * This is a utility method for creating placeholder skills during development.
         * The skill has no rewards and uses the item's name and a generic description.
         *
         * @param id the item identifier to use for the skill
         * @return the registered skill definition entry
         */
        public static Entry<SkillDefinitionConfig> dummySkill(Identifier id) {
                Item item = net.minecraft.registry.Registries.ITEM.get(id);
                String description = item.getName().getString() + " description text";
                var config = new SkillDefinitionConfig(
                                item.getName(),
                                Text.literal(description),
                                null,
                                Icon.item(item),
                                null,
                                1,
                                List.of(),
                                1, 1, 0, 0, 1);
                return Registries.SKILL_DEFINITION.register(ExampleMod.id(id.getPath()), config);
        }

        /**
         * Creates a skill definition that grants a spell.
         * <p>
         * This method looks up the spell configuration and creates a skill that
         * unlocks the spell for use in melee weapons. The skill's title and
         * description are derived from the spell's metadata.
         *
         * @param spellId the spell identifier
         * @return the registered skill definition entry
         */
        public static Entry<SkillDefinitionConfig> dummySpell(Identifier spellId) {
                SpellContainer container = SpellContainerHelper.createForMeleeWeapon(spellId);
                SkillRewardConfig reward = new SkillRewardConfig(SpellContainerReward.ID,
                                new SpellContainerReward(null, List.of(container)));

                var spell = Registries.SPELLS.entries().entrySet().stream().filter(e -> e.getKey().equals(spellId))
                                .findFirst().map(Map.Entry::getValue);
                String description = spell.map(SpellConfig::description).orElse("...");
                String title = spell.map(SpellConfig::title).orElse("Unlock " + spellId);

                var config = new SkillDefinitionConfig(
                                Text.literal(title),
                                Text.literal(description),
                                null,
                                Icon.spell(spellId),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
                return Registries.SKILL_DEFINITION.register(spellId, config);
        }

        /**
         * Map of dummy spell skill definitions.
         * <p>
         * Automatically generates skill definitions for all dummy spells defined
         * in {@link ExampleSpells}. Each spell becomes a skill that can be
         * unlocked in the skill tree.
         */
        public static final HashMap<String, Entry<SkillDefinitionConfig>> DUMMY_DEFINITIONS = ExampleSpells.DUMMY_SPELLS
                        .entrySet().stream()
                        .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> dummySpell(entry.getValue().id()),
                                        (a, b) -> a, // merge function (in case of duplicates)
                                        HashMap::new));

        /**
         * Sandstorm skill definition.
         * <p>
         * Grants the Sandstorm spell, a powerful area-of-effect sand ability
         * from the Suna Suna no Mi Devil Fruit.
         */
        public static final Entry<SkillDefinitionConfig> SANDSTORM = dummySpell(ExampleSpells.SANDSTORM.id());

        /**
         * Sandstorm Modifier 1 skill definition.
         * <p>
         * Enhances the Sandstorm ability with improved range or damage.
         * This is an upgrade skill that modifies the base Sandstorm spell.
         */
        public static final Entry<SkillDefinitionConfig> SANDSTORM_MODIFIER_1 = dummySpell(
                        ExampleSpells.SANDSTORM_MODIFIER_1.id());

        /**
         * Sand Spikes skill definition.
         * <p>
         * Grants the Sand Spikes spell.
         */
        public static final Entry<SkillDefinitionConfig> SAND_SPIKES = dummySpell(ExampleSpells.SAND_SPIKES.id());

        // ==================== Swordsman Skills ====================

        /**
         * Swordsman Skill 1: ShiShi Sonson.
         */
        public static final Entry<SkillDefinitionConfig> SWORDSMEN_SKILL_1 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("swordsmen_skill_1"), swordsmenSkill1());

        /**
         * Creates the Swordsman Skill 1 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig swordsmenSkill1() {
                Identifier spellId = ExampleMod.id("shishi_sonson");
                SpellContainer container = SpellContainerHelper.createForMeleeWeapon(spellId);
                SkillRewardConfig reward = new SkillRewardConfig(SpellContainerReward.ID,
                                new SpellContainerReward(null, List.of(container)));
                return new SkillDefinitionConfig(Text.literal("Unlock ShiShi Sonson"),
                                Text.literal("Channels a fast, focused dash for 1 second, moving 5 blocks forward and slashing any target in the path"),
                                null,
                                Icon.spell(spellId),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        /**
         * Swordsman Skill 2: Yakkodori.
         */
        public static final Entry<SkillDefinitionConfig> SWORDSMEN_SKILL_2 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("swordsmen_skill_2"), swordsmenSkill2());

        /**
         * Creates the Swordsman Skill 2 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig swordsmenSkill2() {
                Identifier spellId = ExampleMod.id("yakkodori");
                SpellContainer container = SpellContainerHelper.createForMeleeWeapon(spellId);
                SkillRewardConfig reward = new SkillRewardConfig(SpellContainerReward.ID,
                                new SpellContainerReward(null, List.of(container)));
                return new SkillDefinitionConfig(Text.literal("Unlock Yakkodori"),
                                Text.literal("[Yakkodori Description text]"),
                                null,
                                Icon.spell(spellId),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        /**
         * Swordsman Skill 3
         */
        public static final Entry<SkillDefinitionConfig> SWORDSMEN_SKILL_3 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("swordsmen_skill_3"), swordsmenSkill3());

        /**
         * Creates the Swordsman Skill 3 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig swordsmenSkill3() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.08f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.item(Items.DIAMOND_SWORD),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        // ==================== Hybrid Skills ====================

        /**
         * Hybrid Skill
         */
        public static final Entry<SkillDefinitionConfig> REQUIRES_BOTH = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("requires_both"), requiresBoth());

        /**
         * Creates the Requires Both skill configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig requiresBoth() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.08f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.item(Items.DIAMOND),
                                null,
                                1,
                                List.of(reward),
                                1, 2, 0, 0, 1);
        }

        // ==================== Brawler Skills ====================

        /**
         * Brawler Skill 1
         */
        public static final Entry<SkillDefinitionConfig> BRAWLER_SKILL_1 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("brawler_skill_1"), brawlerSkill1());

        /**
         * Creates the Brawler Skill 1 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig brawlerSkill1() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.08f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.item(Items.IRON_INGOT),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        /**
         * Brawler Skill 2
         */
        public static final Entry<SkillDefinitionConfig> BRAWLER_SKILL_2 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("brawler_skill_2"), brawlerSkill2());

        /**
         * Creates the Brawler Skill 2 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig brawlerSkill2() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_ATTACK_SPEED,
                                0.15f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.effect(StatusEffects.STRENGTH.value()),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        /**
         * Brawler Skill 3
         */
        public static final Entry<SkillDefinitionConfig> BRAWLER_SKILL_3 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("brawler_skill_3"), brawlerSkill3());

        /**
         * Creates the Brawler Skill 3 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig brawlerSkill3() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_ARMOR,
                                2.0f,
                                EntityAttributeModifier.Operation.ADD_VALUE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.effect(StatusEffects.STRENGTH.value()),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        // ==================== Sniper Skills ====================

        /**
         * Sniper Skill 1
         */
        public static final Entry<SkillDefinitionConfig> SNIPER_SKILL_1 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("sniper_skill_1"), sniperSkill1());

        /**
         * Creates the Sniper Skill 1 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig sniperSkill1() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_LUCK,
                                0.25f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.item(Items.SPYGLASS),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        /**
         * Sniper Skill 2
         */
        public static final Entry<SkillDefinitionConfig> SNIPER_SKILL_2 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("sniper_skill_2"), sniperSkill2());

        /**
         * Creates the Sniper Skill 2 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig sniperSkill2() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                                -0.05f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.item(Items.SPYGLASS),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }

        /**
         * Sniper Skill 3
         */
        public static final Entry<SkillDefinitionConfig> SNIPER_SKILL_3 = Registries.SKILL_DEFINITION
                        .register(ExampleMod.id("sniper_skill_3"), sniperSkill3());

        /**
         * Creates the Sniper Skill 3 configuration.
         *
         * @return the skill configuration
         */
        public static SkillDefinitionConfig sniperSkill3() {
                SkillRewardConfig reward = new SkillRewardConfig(AttributeReward.ID, new AttributeReward(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.15f,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                return new SkillDefinitionConfig(Text.literal("..."),
                                Text.literal("..."),
                                null,
                                Icon.item(Items.CROSSBOW),
                                null,
                                1,
                                List.of(reward),
                                1, 1, 0, 0, 1);
        }
}