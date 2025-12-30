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

    // DTO for loading/saving
    private static class ConfigData {
        public float dimensionsWidth = 1f;
        public float dimensionsHeight = 2f;
        public float scale = 2.0f;
        public int lifeTimeTicks = 40;
        public float damage = 10.0f;
        public double spawnOffsetDistance = 2.0;
    }

    public static void load() {
        if (java.nio.file.Files.exists(CONFIG_PATH)) {
            try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(CONFIG_PATH)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    dimensionsWidth = data.dimensionsWidth;
                    dimensionsHeight = data.dimensionsHeight;
                    scale = data.scale;
                    lifeTimeTicks = data.lifeTimeTicks;
                    damage = data.damage;
                    spawnOffsetDistance = data.spawnOffsetDistance;
                }
                de.one_piece_content.ExampleMod.LOGGER.info("Loaded config from file.");
            } catch (Exception e) {
                de.one_piece_content.ExampleMod.LOGGER.error("Failed to load config, resetting to default.", e);
                save(); // Overwrite corrupted file with defaults
            }
        } else {
            save(); // Create default
        }
    }

    public static void save() {
        try (java.io.Writer writer = java.nio.file.Files.newBufferedWriter(CONFIG_PATH)) {
            ConfigData data = new ConfigData();
            data.dimensionsWidth = dimensionsWidth;
            data.dimensionsHeight = dimensionsHeight;
            data.scale = scale;
            data.lifeTimeTicks = lifeTimeTicks;
            data.damage = damage;
            data.spawnOffsetDistance = spawnOffsetDistance;

            GSON.toJson(data, writer);
            de.one_piece_content.ExampleMod.LOGGER.info("Saved config.");
        } catch (java.io.IOException e) {
            de.one_piece_content.ExampleMod.LOGGER.error("Failed to save config", e);
        }
    }
}
