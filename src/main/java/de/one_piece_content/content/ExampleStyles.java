package de.one_piece_content.content;

import de.one_piece_api.common.config.StyleConfig;
import de.one_piece_content.ExampleMod;
import de.one_piece_api.content.registry.Entry;
import de.one_piece_api.content.registry.Registries;

/**
 * Registry for visual style definitions.
 * <p>
 * This class defines visual styles used to color-code skill tree connections
 * based on their associated combat discipline. Styles help players visually
 * identify which path or fighting style a connection belongs to.

 * <h2>Usage:</h2>
 * Styles are applied to skill tree connections to visually distinguish different
 * combat paths. When defining connections in {@link ExampleConnections}, specify
 * a style to color the connection line accordingly.
 *
 * <h2>Initialization:</h2>
 * Call {@link #init()} during mod initialization to register all styles.
 *
 * @see StyleConfig
 * @see ExampleConnections
 */
public class ExampleStyles {

    /**
     * Swordsman style configuration.
     * <p>
     * Defines the visual style for swordsman-related skill connections.
     */
    public static Entry<StyleConfig> SWORDSMEN_STYLE = Registries.STYLES.register(
            ExampleMod.id("swordsmen_style"), new StyleConfig(0x009900)
    );

    /**
     * Brawler style configuration.
     * <p>
     * Defines the visual style for brawler-related skill connections.
     */
    public static Entry<StyleConfig> BRAWLER_STYLE = Registries.STYLES.register(
            ExampleMod.id("brawler_style"), new StyleConfig(0x990000)
    );

    /**
     * Sniper style configuration.
     * <p>
     * Defines the visual style for sniper-related skill connections.
     */
    public static Entry<StyleConfig> SNIPER_STYLE = Registries.STYLES.register(
            ExampleMod.id("sniper_style"), new StyleConfig(0x000099)
    );

    /**
     * Initializes all style definitions.
     * <p>
     * This method should be called during mod initialization to ensure all
     * styles are registered. The actual registration happens during static
     * initialization of the class fields, but calling this method forces
     * the class to load.
     */
    public static void init() {
        // Static initialization registers all styles
    }
}