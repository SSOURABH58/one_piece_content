package de.one_piece_content.content;

import de.one_piece_api.common.config.DevilFruitConfig;
import de.one_piece_content.ExampleMod;
import de.one_piece_api.content.registry.Entry;
import de.one_piece_api.content.builder.DevilFruitBuilder;
import de.one_piece_api.content.registry.Registries;
import net.minecraft.util.Identifier;

import static de.one_piece_content.content.ExampleSkillDefinitions.*;

/**
 * Registry for Devil Fruit definitions.
 * <p>
 * This class defines all available Devil Fruits that players can consume to gain
 * supernatural abilities. Each Devil Fruit contains multiple skill paths that
 * players unlock sequentially as they progress.
 *
 * <h2>Devil Fruit Structure:</h2>
 * Each Devil Fruit consists of:
 * <ul>
 *     <li><b>Multiple paths</b> - Independent progression chains of skills</li>
 *     <li><b>Sequential unlocking</b> - Skills in each path unlock one after another</li>
 *     <li><b>3D model</b> - Visual representation of the fruit item</li>
 *     <li><b>Optional instant passives</b> - Skills granted immediately upon consumption</li>
 * </ul>
 *
 * <h2>Progression System:</h2>
 * Players progress through Devil Fruit paths by:
 * <ul>
 *     <li>Gaining experience and leveling up</li>
 *     <li>Unlocking skills in order within each path</li>
 *     <li>Advancing multiple paths simultaneously</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * Call {@link #init()} during mod initialization to register all Devil Fruits.
 *
 * @see DevilFruitConfig
 * @see DevilFruitBuilder
 */
public class ExampleDevilFruits {

    /**
     * Suna Suna no Mi (Sand-Sand Fruit) configuration.
     * <p>
     * A Logia-type Devil Fruit that grants the user the ability to create,
     * control, and transform into sand at will.
     */
    public static final Entry<DevilFruitConfig> SUNA_SUNA_NO_MI = Registries.DEVIL_FRUITS.register(
            ExampleMod.id("suna_suna_no_mi"),
            new DevilFruitBuilder()
                    // Path 1: Sandstorm offensive abilities
                    .newPath(builder -> builder
                            .add(
                                    SANDSTORM.id(),
                                    SANDSTORM_MODIFIER_1.id())
                    )
                    // Path 2: Sand manipulation and control
                    .newPath(builder -> builder
                            .add(
                                    DUMMY_DEFINITIONS.get("sand_hand").id(),
                                    DUMMY_DEFINITIONS.get("sand_drain").id())
                    )
                    // Path 3: Sand weapon techniques
                    .newPath(builder -> builder
                            .add(
                                    DUMMY_DEFINITIONS.get("sand_blade").id(),
                                    DUMMY_DEFINITIONS.get("sand_spikes").id())
                    )
                    // Path 4: Tactical utility abilities
                    .newPath(builder -> builder
                            .add(
                                    DUMMY_DEFINITIONS.get("quicksand").id())
                    )
                    .modelId(Identifier.of(ExampleMod.MOD_ID, "devil_fruit/suna_suna_no_mi.json"))
                    .build()
    );

    /**
     * Test Devil Fruit 1 configuration.
     * <p>
     * A test Devil Fruit used for development and demonstration purposes.
     * Contains 5 parallel paths with 4 skills each, totaling 20 abilities.
     * <p>>
     * This fruit uses the same 3D model as Suna Suna no Mi for testing purposes.
     */
    public static final Entry<DevilFruitConfig> DEVIL_FRUIT_1 = Registries.DEVIL_FRUITS.register(
            ExampleMod.id("test_fruit_1"),
            new DevilFruitBuilder()
                    // Path 1: First set of test skills
                    .newPath(builder -> builder
                            .add(
                                    ITEM_ENTRIES.subList(0, 4).stream().map(Entry::id).toArray(Identifier[]::new)
                            )
                    )
                    // Path 2: Second set of test skills
                    .newPath(builder -> builder
                            .add(
                                    ITEM_ENTRIES.subList(4, 8).stream().map(Entry::id).toArray(Identifier[]::new)
                            )
                    )
                    // Path 3: Third set of test skills
                    .newPath(builder -> builder
                            .add(
                                    ITEM_ENTRIES.subList(8, 12).stream().map(Entry::id).toArray(Identifier[]::new)
                            )
                    )
                    // Path 4: Fourth set of test skills
                    .newPath(builder -> builder
                            .add(
                                    ITEM_ENTRIES.subList(12, 16).stream().map(Entry::id).toArray(Identifier[]::new)
                            )
                    )
                    // Path 5: Fifth set of test skills
                    .newPath(builder -> builder
                            .add(
                                    ITEM_ENTRIES.subList(16, 20).stream().map(Entry::id).toArray(Identifier[]::new)
                            )
                    )
                    .modelId(Identifier.of(ExampleMod.MOD_ID, "devil_fruit/suna_suna_no_mi.json"))
                    .build()
    );

    /**
     * Initializes all Devil Fruits.
     * <p>
     * This method should be called during mod initialization to ensure all
     * Devil Fruits are registered. The actual registration happens during static
     * initialization of the class fields, but calling this method forces
     * the class to load.
     */
    public static void init() {
        // Static initialization registers all Devil Fruits
    }
}