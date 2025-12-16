package de.one_piece_content.content;

import de.one_piece_api.common.mixin_interface.SkillType;
import de.one_piece_content.ExampleMod;
import de.one_piece_api.server.config.skill.SkillTreeEntryConfig;
import de.one_piece_api.content.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for skill tree layout and positioning.
 * <p>
 * This class defines the visual layout of the skill tree by specifying the
 * position and root status of each skill. Skills are arranged in a coordinate
 * system where (0, 0) represents the center of the skill tree viewport.
 *
 * <h2>Coordinate System:</h2>
 * <ul>
 *     <li>Positive X - Right side of the tree</li>
 *     <li>Negative X - Left side of the tree</li>
 *     <li>Positive Y - Lower portion of the tree</li>
 *     <li>Negative Y - Upper portion of the tree</li>
 *     <li>Origin (0, 0) - Center of the viewport</li>
 * </ul>
 *
 * <h2>Root Skills:</h2>
 * Root skills are the starting points for each skill path and are marked
 * as such to enable special behavior (e.g., different visual styling, or
 * being immediately available).
 *
 * <h2>Usage:</h2>
 * Call {@link #init()} during mod initialization to register the skill tree layout.
 *
 * @see SkillTreeEntryConfig
 * @see ExampleSkillDefinitions
 */
public class ExampleSkills {

    /**
     * List of all skill tree entry configurations.
     * <p>
     * Contains the position and root status for every skill in the tree.
     * This list is populated in the static initializer and registered as
     * a complete skill tree layout.
     */
    public static List<SkillTreeEntryConfig> SKILL_TREE_ENTRIES = new ArrayList<>();

    static {
        // Swordsman path (left side, upper area)
        register(ExampleSkillDefinitions.SWORDSMEN_SKILL_1.id(), true, -70, -40);   // Root: Foundational swordsman skill
        register(ExampleSkillDefinitions.SWORDSMEN_SKILL_2.id(), false, -138, 0);   // Branch: Advanced sword technique
        register(ExampleSkillDefinitions.SWORDSMEN_SKILL_3.id(), false, -70, -120); // Branch: Attack damage boost

        // Brawler path (right side, upper area)
        register(ExampleSkillDefinitions.BRAWLER_SKILL_1.id(), true, 70, -40);      // Root: Foundational brawler skill
        register(ExampleSkillDefinitions.BRAWLER_SKILL_2.id(), false, 130, 0);      // Branch: Attack speed boost
        register(ExampleSkillDefinitions.BRAWLER_SKILL_3.id(), false, 70, -120);    // Branch: Armor boost

        // Sniper path (center, lower area)
        register(ExampleSkillDefinitions.SNIPER_SKILL_1.id(), true, 0, 80);         // Root: Foundational sniper skill
        register(ExampleSkillDefinitions.SNIPER_SKILL_2.id(), false, 70, 120);      // Branch: Focused aim
        register(ExampleSkillDefinitions.SNIPER_SKILL_3.id(), false, -70, 120);     // Branch: Ranged power

        // Hybrid skill (center, upper area - requires multiple paths)
        register(ExampleSkillDefinitions.REQUIRES_BOTH.id(), false, 0, -70);        // Convergence: Requires both Swordsman and Brawler

        // Register the complete skill tree layout
        Registries.SKILL_TREE_ENTRIES.register(ExampleMod.id("skills"), SKILL_TREE_ENTRIES);
    }

    /**
     * Registers a skill's position in the skill tree with default type.
     * <p>
     * This is a convenience method that calls {@link #register(Identifier, boolean, int, int, SkillType)}
     * with the default type {@link SkillType#SKILL_TREE}.
     * <p>
     * The skill will be rendered at the specified coordinates in the skill
     * tree viewport. Root skills serve as starting points for skill paths,
     * while non-root skills require prerequisites to unlock.
     *
     * @param skillId the skill definition identifier
     * @param isRoot {@code true} if this is a root skill (starting point of a path),
     *               {@code false} if it requires prerequisites
     * @param x the x-coordinate in the skill tree viewport (negative = left, positive = right)
     * @param y the y-coordinate in the skill tree viewport (negative = up, positive = down)
     */
    public static void register(Identifier skillId, boolean isRoot, int x, int y) {
        register(skillId, isRoot, x, y, SkillType.SKILL_TREE);
    }


    /**
     * Registers a skill's position in the skill tree with a specified type.
     * <p>
     * Adds a new skill entry to the tree with its position, root status, and entry type.
     * The skill will be rendered at the specified coordinates in the skill tree viewport.
     * <p>
     * The entry type determines how the skill is displayed and behaves within the skill tree UI.
     *
     * @param skillId the skill definition identifier
     * @param isRoot {@code true} if this is a root skill (starting point of a path),
     *               {@code false} if it requires prerequisites
     * @param x the x-coordinate in the skill tree viewport (negative = left, positive = right)
     * @param y the y-coordinate in the skill tree viewport (negative = up, positive = down)
     * @param type the entry type defining how this skill is displayed in the tree
     * @see SkillType
     */
    public static void register(Identifier skillId, boolean isRoot, int x, int y, SkillType type) {
        SKILL_TREE_ENTRIES.add(new SkillTreeEntryConfig(skillId, isRoot, x, y, type));
    }

    /**
     * Initializes the skill tree layout.
     * <p>
     * This method should be called during mod initialization to ensure the
     * skill tree is registered. The actual registration happens during static
     * initialization of the class, but calling this method forces the class
     * to load.
     */
    public static void init() {
        // Static initialization registers the skill tree
    }
}