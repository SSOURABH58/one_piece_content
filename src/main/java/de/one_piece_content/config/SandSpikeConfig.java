package de.one_piece_content.config;

import net.minecraft.util.math.Vec3d;

public class SandSpikeConfig {
    public static float dimensionsWidth = 1f;
    public static float dimensionsHeight = 2f;
    public static float scale = 2.0f;
    public static int lifeTimeTicks = 40;
    public static float damage = 10.0f;
    public static double spawnOffsetDistance = 2.0;

    private static final com.google.gson.Gson GSON = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
    private static final java.nio.file.Path CONFIG_PATH = net.fabricmc.loader.api.FabricLoader.getInstance()
            .getConfigDir().resolve("one_piece_content.json");

    public static void load() {
        if (java.nio.file.Files.exists(CONFIG_PATH)) {
            try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(CONFIG_PATH)) {
                SandSpikeConfig config = GSON.fromJson(reader, SandSpikeConfig.class);
                dimensionsWidth = config.dimensionsWidth;
                dimensionsHeight = config.dimensionsHeight;
                scale = config.scale;
                lifeTimeTicks = config.lifeTimeTicks;
                damage = config.damage;
                spawnOffsetDistance = config.spawnOffsetDistance;
                de.one_piece_content.ExampleMod.LOGGER.info("Loaded config from file.");
            } catch (java.io.IOException e) {
                de.one_piece_content.ExampleMod.LOGGER.error("Failed to load config", e);
            }
        } else {
            save(); // Create default
        }
    }

    public static void save() {
        try (java.io.Writer writer = java.nio.file.Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(new SandSpikeConfig(), writer); // This will save static fields if instance is used? No, GSON
                                                        // doesn't serialize static fields by default on instance.
            // Actually, we need a POJO to save/load cleanly or register type adapter.
            // Simplified: We will construct a map or simple object to save.
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("dimensionsWidth", dimensionsWidth);
            map.put("dimensionsHeight", dimensionsHeight);
            map.put("scale", scale);
            map.put("lifeTimeTicks", lifeTimeTicks);
            map.put("damage", damage);
            map.put("spawnOffsetDistance", spawnOffsetDistance);
            GSON.toJson(map, writer);
            de.one_piece_content.ExampleMod.LOGGER.info("Saved default config.");
        } catch (java.io.IOException e) {
            de.one_piece_content.ExampleMod.LOGGER.error("Failed to save config", e);
        }
    }

    // Static block to auto-load on class access? Or call from ModInit.
    // Better to call from ModInit.
}
