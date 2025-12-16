package de.one_piece_content.content;

import de.one_piece_content.ExampleMod;
import de.one_piece_api.content.builder.ConnectionsBuilder;
import de.one_piece_api.server.config.skill.ConnectionsConfig;
import de.one_piece_api.content.registry.Entry;
import de.one_piece_api.content.registry.Registries;

/**
 * Registry for skill tree connection definitions.
 * <p>
 * This class defines the visual and logical connections between skills in the
 * skill tree. Connections show progression paths, prerequisites, and mutually
 * exclusive choices.
 *
 * <h2>Connection Types:</h2>
 * <ul>
 *     <li><b>Normal connections</b> - Always visible; indicate prerequisites (one skill requires the other)</li>
 *     <li><b>Exclusive connections</b> - Only visible on hover; indicate mutual exclusivity (both cannot be active simultaneously)</li>
 * </ul>
 *
 * <h2>Connection Directions:</h2>
 * <ul>
 *     <li><b>Unidirectional</b> - Arrow points one way (A requires B, or A excludes B)</li>
 *     <li><b>Bidirectional</b> - Arrow points both ways (A and B require each other, or both exclude each other)</li>
 * </ul>
 *
 * Each style has a root skill that branches into multiple specializations,
 * with some advanced skills requiring progress in multiple paths.
 *
 * <h2>Usage:</h2>
 * Call {@link #init()} during mod initialization to register all connections.
 *
 * @see ConnectionsConfig
 * @see ConnectionsBuilder
 */
public class ExampleConnections {

    /**
     * Class-based skill connections configuration.
     * <p>
     * Defines the skill tree structure for class-based abilities with prerequisite
     * relationships between skills:
     * <p>
     * All connections are styled according to their combat style for visual clarity.
     * All connections are <b>normal</b> (prerequisite) and <b>unidirectional</b>
     * (pointing from prerequisite to dependent skill).
     */
    public static Entry<ConnectionsConfig> CLASS_CONNECTIONS = Registries.CONNECTIONS.register(
            ExampleMod.id("class_connections"),
            createClassConnections()
    );

    /**
     * Creates the class connections configuration.
     * <p>
     * Builds a network of prerequisite connections representing the class-based skill tree.
     * All connections are unidirectional arrows showing which skills are required before
     * others can be unlocked.
     *
     * @return the configured prerequisite connections
     */
    public static ConnectionsConfig createClassConnections() {
        return new ConnectionsBuilder()
                .normal(e -> e
                        // Swordsman progression paths (SKILL_1 is prerequisite for both branches)
                        .addUnidirectional(
                                ExampleSkillDefinitions.SWORDSMEN_SKILL_1.id(),
                                ExampleSkillDefinitions.SWORDSMEN_SKILL_2.id(),
                                ExampleStyles.SWORDSMEN_STYLE.id())
                        .addUnidirectional(
                                ExampleSkillDefinitions.SWORDSMEN_SKILL_1.id(),
                                ExampleSkillDefinitions.SWORDSMEN_SKILL_3.id(),
                                ExampleStyles.SWORDSMEN_STYLE.id())

                        // Brawler progression paths (SKILL_1 is prerequisite for both branches)
                        .addUnidirectional(
                                ExampleSkillDefinitions.BRAWLER_SKILL_1.id(),
                                ExampleSkillDefinitions.BRAWLER_SKILL_2.id(),
                                ExampleStyles.BRAWLER_STYLE.id())
                        .addUnidirectional(
                                ExampleSkillDefinitions.BRAWLER_SKILL_1.id(),
                                ExampleSkillDefinitions.BRAWLER_SKILL_3.id(),
                                ExampleStyles.BRAWLER_STYLE.id())

                        // Sniper progression paths (SKILL_1 is prerequisite for both branches)
                        .addUnidirectional(
                                ExampleSkillDefinitions.SNIPER_SKILL_1.id(),
                                ExampleSkillDefinitions.SNIPER_SKILL_2.id(),
                                ExampleStyles.SNIPER_STYLE.id())
                        .addUnidirectional(
                                ExampleSkillDefinitions.SNIPER_SKILL_1.id(),
                                ExampleSkillDefinitions.SNIPER_SKILL_3.id(),
                                ExampleStyles.SNIPER_STYLE.id())

                        // Hybrid skill connections (requires prerequisites from multiple paths)
                        .addUnidirectional(
                                ExampleSkillDefinitions.SWORDSMEN_SKILL_1.id(),
                                ExampleSkillDefinitions.REQUIRES_BOTH.id(),
                                ExampleStyles.SWORDSMEN_STYLE.id())
                        .addUnidirectional(
                                ExampleSkillDefinitions.BRAWLER_SKILL_1.id(),
                                ExampleSkillDefinitions.REQUIRES_BOTH.id(),
                                ExampleStyles.BRAWLER_STYLE.id()))
                .build();
    }

    /**
     * Initializes all skill connections.
     * <p>
     * This method should be called during mod initialization to ensure all
     * connections are registered. The actual registration happens during static
     * initialization of the class fields, but calling this method forces
     * the class to load.
     */
    public static void init() {
        // Static initialization registers all connections
    }
}