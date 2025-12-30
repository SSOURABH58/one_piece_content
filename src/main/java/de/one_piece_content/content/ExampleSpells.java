package de.one_piece_content.content;

import de.one_piece_api.server.config.spell.SpellConfig;
import de.one_piece_content.ExampleMod;
import de.one_piece_api.content.builder.SpellFactory;
import de.one_piece_api.content.registry.Entry;
import de.one_piece_api.content.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
import net.spell_power.api.SpellSchools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for spell definitions and configurations.
 * <p>
 * This class defines all spells that can be learned and cast in the game,
 * including
 * combat abilities, Devil Fruit powers, and passive modifiers. Each spell
 * includes
 * complete configuration for casting, delivery, impact, and visual/audio
 * effects.
 *
 * <h2>Spell Types:</h2>
 * <ul>
 * <li><b>Active</b> - Player-triggered abilities with casting, delivery, and
 * impact</li>
 * <li><b>Passive</b> - Automatic abilities triggered by events</li>
 * <li><b>Modifier</b> - Spells that enhance other spells</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * Call {@link #init()} during mod initialization to register all spells.
 *
 * @see SpellConfig
 * @see SpellFactory
 */
public class ExampleSpells {

        /**
         * Initializes all spell definitions.
         * <p>
         * This method should be called during mod initialization to ensure all
         * spells are registered. The actual registration happens during static
         * initialization of the class fields, but calling this method forces
         * the class to load.
         */
        public static void init() {
        }

        // ==================== Swordsman Spells ====================

        /**
         * ShiShi Sonson spell configuration.
         * <p>
         * A powerful dash attack technique that channels briefly before unleashing
         * a rapid forward movement combined with a devastating slash.
         * <p>
         * Uses custom delivery handler for the dash mechanic.
         */
        public static final Entry<SpellConfig> SHISHI_SONSON = Registries.SPELLS.register(
                        ExampleMod.id("shishi_sonson"),
                        shishi_sonson());

        /**
         * Yakkodori spell configuration.
         * <p>
         * Releases a spiritual slash that cuts through obstacles and enemies,
         * launching a projectile of energy forward.
         * <p>
         * Uses custom delivery handler for the energy slash mechanic.
         */
        public static final Entry<SpellConfig> YAKKODORI = Registries.SPELLS.register(
                        ExampleMod.id("yakkodori"),
                        yakkodori());

        // ==================== Devil Fruit Spells ====================

        /**
         * Sandstorm spell configuration.
         */
        public static final Entry<SpellConfig> SANDSTORM = Registries.SPELLS.register(
                        ExampleMod.id("sandstorm"),
                        sandstorm());

        /**
         * Sandstorm Modifier 1 spell configuration.
         * <p>
         * A passive modifier that enhances the Sandstorm ability by reducing
         * its cooldown, allowing for more frequent casts.
         *
         * <h3>Effect:</h3>
         * Reduces Sandstorm cooldown by 5 seconds.
         */
        public static final Entry<SpellConfig> SANDSTORM_MODIFIER_1 = Registries.SPELLS.register(
                        ExampleMod.id("sandstorm_modifier_1"),
                        arcane_spec_b_modifier_1());

        /**
         * Map of dummy spell configurations for testing.
         * <p>
         * Contains placeholder spells used for development and testing purposes
         */
        public static final Entry<SpellConfig> SAND_SPIKES = Registries.SPELLS.register(
                        ExampleMod.id("sand_spikes"),
                        sand_spikes());

        /**
         * Map of dummy spell configurations for testing.
         * <p>
         * Contains placeholder spells used for development and testing purposes
         */
        public static final Map<String, Entry<SpellConfig>> DUMMY_SPELLS = add_dummies(
                        List.of("dummy", "quicksand", "sand_blade", "sand_drain", "sand_hand"));

        // ==================== Spell Configuration Methods ====================

        /**
         * Creates the ShiShi Sonson spell configuration.
         * <p>
         * Configures a channeled dash attack with custom delivery mechanics.
         * The spell uses a brief cast time with particle effects during release,
         * and employs a custom handler for the dash and slash mechanics.
         *
         * @return the complete spell configuration
         */
        private static SpellConfig shishi_sonson() {
                var spell = SpellFactory.active()
                                .school(SpellSchools.getSchool("physical_melee"))
                                .range(0f)
                                .tier(1)
                                .rangeMechanic(Spell.RangeMechanic.MELEE)
                                .casting(cast -> cast
                                                .duration(0.25f)
                                                .channel(1)
                                                .animation("spell_engine:one_handed_throw_charge"))
                                .release(release -> release
                                                .animation("spell_engine:dual_handed_weapon_cross")
                                                .sound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP.getId())
                                                .particles(new ParticleBatch("spell_engine:smoke_medium",
                                                                ParticleBatch.Shape.CIRCLE,
                                                                ParticleBatch.Origin.FEET,
                                                                25.0f, 0.15f, 0.15f).preSpawnTravel(1)))
                                .target(target -> target
                                                .area(area -> area.verticalRange(0.5f)))
                                .delivery(deliver -> deliver
                                                .custom(ExampleMod.id("shishi_sonson").toString()))
                                .impact(impact -> impact
                                                .damage(0, 0) // Damage handled by custom delivery
                                                .particles(new ParticleBatch("spell_engine:smoke_medium",
                                                                ParticleBatch.Shape.CIRCLE,
                                                                ParticleBatch.Origin.FEET,
                                                                25.0f, 0.15f, 0.15f).preSpawnTravel(1)))
                                .cost(cost -> cost
                                                .cooldown(8f)
                                                .stamina(10)
                                                .exhaust(0.5f))
                                .build();
                return new SpellConfig(spell, "Shishi Sonson",
                                "Channels a fast, focused dash for 5 blocks forward, slashing any target in the path for {damage} damage.");
        }

        /**
         * Creates the Yakkodori spell configuration.
         * <p>
         * Configures an energy slash projectile with custom delivery mechanics.
         * The spell fires a spiritual slash that travels in a straight line,
         * cutting through obstacles.
         *
         * @return the complete spell configuration
         */
        private static SpellConfig yakkodori() {
                var spell = SpellFactory.active()
                                .school(SpellSchools.getSchool("physical_melee"))
                                .range(5f)
                                .tier(1)
                                .casting(cast -> cast
                                                .duration(0.5f)
                                                .animation("spell_engine:one_handed_throw_charge"))
                                .release(release -> release
                                                .animation("spell_engine:one_handed_shout_release")
                                                .sound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP.getId()))
                                .target(SpellFactory.TargetBuilder::aim)
                                .delivery(deliver -> deliver
                                                .custom(ExampleMod.id("yakkodori").toString()))
                                .cost(cost -> cost
                                                .cooldown(8f)
                                                .stamina(10)
                                                .exhaust(0.5f))
                                .build();

                return new SpellConfig(spell, "Yakkodori",
                                "Releases a spiritual slash that cuts through obstacles and enemies. Launches a slash of energy 5 blocks forward");
        }

        /**
         * Creates the Sandstorm spell configuration.
         * <p>
         * Configures a powerful homing fireball projectile with multiple particle
         * effects for casting, traveling, and impact phases. The projectile homes
         * in on targets and causes both direct damage and fire damage over time.
         *
         * @return the complete spell configuration
         */
        private static SpellConfig sandstorm() {
                // Casting particle effects (flame pipe from feet)
                var castParticles = new ParticleBatch[] { new ParticleBatch(
                                SpellEngineParticles.flame.id().toString(),
                                ParticleBatch.Shape.WIDE_PIPE,
                                ParticleBatch.Origin.FEET,
                                1, 0.05f, 0.1f) };

                // Travel particle effects (flame and smoke circle)
                var travelParticles = new ParticleBatch[] { new ParticleBatch(
                                SpellEngineParticles.flame.id().toString(),
                                ParticleBatch.Shape.CIRCLE,
                                ParticleBatch.Origin.CENTER,
                                ParticleBatch.Rotation.LOOK, 0, 0, 3,
                                0, 0.1f, 0, 0, 0, false,
                                -1, 1, false, 1F),
                                new ParticleBatch(
                                                SpellEngineParticles.smoke_large.id().toString(),
                                                ParticleBatch.Shape.CIRCLE,
                                                ParticleBatch.Origin.CENTER,
                                                ParticleBatch.Rotation.LOOK, 0, 0, 3,
                                                0, 0.1f, 0, 0, 0, false,
                                                -1, 1, false, 1F) };

                // Impact particle effects (explosion with smoke and flames)
                var impactParticles = new ParticleBatch[] { new ParticleBatch(
                                SpellEngineParticles.smoke_large.id().toString(),
                                ParticleBatch.Shape.SPHERE,
                                ParticleBatch.Origin.CENTER,
                                15, 0.01f, 0.1f),
                                new ParticleBatch(
                                                SpellEngineParticles.flame_medium_b.id().toString(),
                                                ParticleBatch.Shape.CIRCLE,
                                                ParticleBatch.Origin.CENTER,
                                                15, 0.1f, 0.2f) };

                var spell = SpellFactory.active()
                                .school(SpellSchools.getSchool("physical_melee"))
                                .range(30f)
                                .tier(1)
                                .group("primary")
                                .casting(cast -> cast
                                                .duration(1.5f)
                                                .animation("spell_engine:one_handed_projectile_charge")
                                                .sound(SpellEngineSounds.GENERIC_FIRE_CASTING.id())
                                                .particles(castParticles))
                                .release(release -> release
                                                .animation("spell_engine:one_handed_projectile_release"))
                                .target(SpellFactory.TargetBuilder::aim)
                                .delivery(deliver -> deliver
                                                .projectile(proj -> proj
                                                                .velocity(1.0f)
                                                                .homing(1) // Slight homing effect
                                                                .model("one_piece_api:fireball", 0.5f)
                                                                .lightLevel(12)
                                                                .launchSound(SpellEngineSounds.GENERIC_FIRE_RELEASE
                                                                                .id())
                                                                .travelParticles(travelParticles)))
                                .impact(impact -> impact
                                                .damage(0.8f, 0.8f)
                                                .sound(SpellEngineSounds.SIGNAL_SPELL_CRIT.id())
                                                .particles(impactParticles))
                                .impact(impact -> impact
                                                .fire(4)) // Additional fire effect
                                .cost(cost -> cost
                                                .cooldown(8)
                                                .stamina(15)
                                                .exhaust(0.5f))
                                .build();
                return new SpellConfig(spell, "Sandstorm", "...");
        }

        /**
         * Creates the Sand Spikes spell configuration.
         * <p>
         * Summons sand spikes from the ground to impale enemies.
         *
         * @return the complete spell configuration
         */
        private static SpellConfig sand_spikes() {
                var spell = SpellFactory.active()
                                .school(SpellSchools.getSchool("physical_melee"))
                                .range(8f)
                                .tier(1)
                                .casting(cast -> cast
                                                .duration(1.0f)
                                                // .animation("one_piece_api:animation.suna.sand_spike") // Handled by
                                                // custom renderer
                                                .sound(SpellEngineSounds.GENERIC_FIRE_CASTING.id()))
                                .release(release -> release
                                                .animation("spell_engine:two_handed_slam_release")
                                                .sound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG.getId()))
                                .target(SpellFactory.TargetBuilder::aim)
                                .delivery(deliver -> deliver
                                                .custom(ExampleMod.id("sand_spikes").toString()))
                                .impact(impact -> impact
                                                .damage(0.0f, 0.0f)) // Damage handled by custom handler
                                .cost(cost -> cost
                                                .cooldown(12f)
                                                .stamina(20)
                                                .exhaust(1.0f))
                                .build();

                return new SpellConfig(spell, "Sand Spikes",
                                "The ground trembles, then massive sand spikes erupt to impale the target.");
        }

        /**
         * Creates the Sandstorm Modifier 1 spell configuration.
         * <p>
         * Configures a passive modifier spell that reduces the cooldown of
         * the Sandstorm ability, allowing for more frequent use.
         *
         * @return the complete spell configuration
         */
        private static SpellConfig arcane_spec_b_modifier_1() {
                var spell = SpellFactory.modifier()
                                .school(SpellSchools.ARCANE)
                                .modify(mod -> mod
                                                .spellPattern(SANDSTORM.id().toString())
                                                .cooldownDeduct(5)) // Reduces cooldown by 5 seconds
                                .build();

                return new SpellConfig(spell, "Arcane Endurance",
                                "Reduces the cooldown of Sandstorm by {cooldown_duration_deduct} sec.");
        }

        /**
         * Creates multiple dummy spell configurations for testing.
         * <p>
         * Generates basic spell configurations with auto-generated titles and
         * descriptions based on the provided identifiers. These are minimal
         * spells used for development and testing purposes.
         *
         * @param list the list of spell identifiers to create
         * @return a map of spell identifiers to their registered entries
         */
        private static HashMap<String, Entry<SpellConfig>> add_dummies(List<String> list) {
                HashMap<String, Entry<SpellConfig>> map = new HashMap<>();
                list.forEach(id -> {
                        var title = toTitleCase(id);
                        var description = toTitleCase(id) + " Description";

                        var spell = SpellFactory.active()
                                        .school(SpellSchools.getSchool("physical_melee"))
                                        .range(0f)
                                        .tier(1)
                                        .build();
                        var entry = new SpellConfig(spell, title, description);
                        map.put(id, Registries.SPELLS.register(ExampleMod.id(id), entry));
                });
                return map;
        }

        /**
         * Converts a snake_case string to Title Case.
         * <p>
         * Transforms underscored identifiers into human-readable titles.
         * Example: "sand_blade" becomes "Sand Blade"
         *
         * @param input the snake_case string to convert
         * @return the Title Case string
         */
        public static String toTitleCase(String input) {
                String[] parts = input.split("_"); // split by underscore
                StringBuilder result = new StringBuilder();

                for (int i = 0; i < parts.length; i++) {
                        if (parts[i].isEmpty())
                                continue;
                        result.append(Character.toUpperCase(parts[i].charAt(0))) // first letter uppercase
                                        .append(parts[i].substring(1)); // rest as is
                        if (i < parts.length - 1)
                                result.append(" "); // add space between words
                }

                return result.toString();
        }
}