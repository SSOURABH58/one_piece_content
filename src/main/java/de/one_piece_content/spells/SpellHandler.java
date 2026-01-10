package de.one_piece_content.spells;

import de.one_piece_content.ExampleMod;
import de.one_piece_content.registries.MySounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.internals.SpellHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class SpellHandler {

        private static final HashMap<PlayerEntity, Vec3d> FORWARD = new HashMap<>();

        public static void register() {

                ExampleMod.LOGGER.info("Registering Spell Handlers");
                SpellHandlers.registerCustomDelivery(
                                ExampleMod.id("shishi_sonson"),
                                SpellHandler::onShishiSonson);

                SpellHandlers.registerCustomDelivery(
                                ExampleMod.id("yakkodori"),
                                SpellHandler::onYakkodori);

                SpellHandlers.registerCustomDelivery(
                                ExampleMod.id("sand_spikes"),
                                SpellHandler::onSandSpikes);
        }

        public static boolean onShishiSonson(
                        World world,
                        RegistryEntry<Spell> spellEntry,
                        PlayerEntity player,
                        List<SpellHelper.DeliveryTarget> deliveryTargets,
                        SpellHelper.ImpactContext context,
                        @Nullable Vec3d vec3d) {
                ExampleMod.LOGGER.info("ShishiSonson");
                if (!(world instanceof ServerWorld serverWorld))
                        return false;
                RegistryEntry<DamageType> player_attack = world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(DamageTypes.PLAYER_ATTACK)
                                .orElseThrow();
                int tick = context.channelTickIndex();
                deliveryTargets.forEach(target -> {
                        var entity = target.entity();
                        entity.damage(new DamageSource(player_attack, player), 6.0F);
                        serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                                        entity.getX(), entity.getEyeY(), entity.getZ(),
                                        5, 0, 0, 0, 0);
                });
                if (tick == 0) {
                        world.playSound(null, player.getBlockPos(), MySounds.SHISHI_SONSON.soundEvent(),
                                        SoundCategory.PLAYERS, 1.0F, 1.0F);
                        Vec3d dir = player.getRotationVec(1.0F);
                        Vec3d forward = new Vec3d(dir.x, 0, dir.z)
                                        .normalize()
                                        .multiply(1);
                        FORWARD.put(player, forward);
                }
                player.setVelocity(FORWARD.get(player));
                player.velocityModified = true;
                return true;
        }

        public static boolean onYakkodori(World world,
                        RegistryEntry<Spell> spellRegistryEntry,
                        PlayerEntity playerEntity,
                        List<SpellHelper.DeliveryTarget> deliveryTargets,
                        SpellHelper.ImpactContext impactContext,
                        @Nullable Vec3d vec3d) {
                ExampleMod.LOGGER.info("Yakkodori");
                if (!(world instanceof ServerWorld serverWorld))
                        return false;
                world.playSound(null, playerEntity.getBlockPos(), MySounds.YAKKODORI.soundEvent(),
                                SoundCategory.PLAYERS, 1.0F, 1.0F);
                Vec3d direction = playerEntity.getRotationVec(1.0F).normalize();
                Vec3d start = playerEntity.getPos().add(0, playerEntity.getStandingEyeHeight(), 0);
                Vec3d end = start.add(direction.multiply(5.0));
                Box box = new Box(
                                Math.min(start.x, end.x) - 1, Math.min(start.y, end.y) - 1,
                                Math.min(start.z, end.z) - 1,
                                Math.max(start.x, end.x) + 1, Math.max(start.y, end.y) + 1,
                                Math.max(start.z, end.z) + 1);
                List<LivingEntity> entities = world.getEntitiesByClass(
                                LivingEntity.class,
                                box,
                                e -> e != playerEntity);
                RegistryEntry<DamageType> yakkodoriDamageType = world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .getEntry(DamageTypes.PLAYER_ATTACK)
                                .orElseThrow();
                for (LivingEntity entity : entities) {
                        Vec3d toEntity = entity.getPos().add(0, entity.getStandingEyeHeight() / 2, 0).subtract(start)
                                        .normalize();
                        double angle = Math.acos(direction.dotProduct(toEntity));
                        if (angle < Math.PI / 6) { // 60° cone
                                entity.damage(new DamageSource(yakkodoriDamageType, playerEntity), 6.0F);
                                serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                                                entity.getX(), entity.getEyeY(), entity.getZ(),
                                                5, 0, 0, 0, 0);
                        }
                }

                world.playSound(null, playerEntity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                                SoundCategory.PLAYERS, 1.0F, 1.0F);

                return true;
        }

        public static boolean onSandSpikes(World world,
                        RegistryEntry<Spell> spellRegistryEntry,
                        PlayerEntity playerEntity,
                        List<SpellHelper.DeliveryTarget> deliveryTargets,
                        SpellHelper.ImpactContext impactContext,
                        @Nullable Vec3d vec3d) {
                if (!(world instanceof ServerWorld serverWorld))
                        return false;

                // 1. Use Target Position from Spell Engine (Raycast result)
                Vec3d center;
                if (vec3d != null) {
                        center = vec3d;
                } else {
                        // Fallback: 3 blocks in front
                        Vec3d dir = playerEntity.getRotationVec(1.0F).normalize().multiply(3);
                        center = playerEntity.getPos().add(dir.x, 0, dir.z);
                }

                // Ground Alignment Logic
                // Scan down from the target Y to find the nearest solid block (max 20 blocks
                // down)
                // This prevents floating spikes if the raycast hits air or foliage
                int startY = (int) Math.floor(center.y);
                int groundY = startY;

                // If vec3d is derived from air-aim, we might need to scan down
                for (int y = startY; y > Math.max(world.getBottomY(), startY - 20); y--) {
                        BlockPos pos = new BlockPos((int) center.x, y, (int) center.z);
                        if (!world.getBlockState(pos).isAir()) {
                                // Found solid block, spawn on top (y + 1)
                                // But if startY was already inside a block (e.g. side hit), we might want y +
                                // 1?
                                // If we hit top face of block at 64.0 -> floor is 64. 64 is solid. groundY =
                                // 65.
                                // Standard: floor is the block coordinate.
                                groundY = y + 1;
                                break;
                        }
                }

                // If vec3d was null (fallback mode), force surface check using proper world
                // height
                if (vec3d == null) {
                        groundY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                        (int) center.x, (int) center.z);
                }

                // Set center to the top of the found block
                center = new Vec3d(center.x, groundY, center.z);

                // 2. Play initial "Rumbling" sounds and particles
                world.playSound(null, center.getX(), center.getY(), center.getZ(),
                                SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.PLAYERS, 1.0F, 0.5F);

                // Spawn trembling particles (Campfire smoke + Block crack)
                serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                center.getX(), center.getY() + 0.1, center.getZ(),
                                10, 0.5, 0.0, 0.5, 0.05);
                serverWorld.spawnParticles(
                                new net.minecraft.particle.BlockStateParticleEffect(ParticleTypes.BLOCK,
                                                net.minecraft.block.Blocks.SAND.getDefaultState()),
                                center.getX(), center.getY() + 0.1, center.getZ(),
                                20, 1.0, 0.1, 1.0, 0.1);

                ExampleMod.LOGGER.info("Spawning Sand Spike at: " + center);

                // 3. Spawn Custom Sand Spike Entity
                var sandSpike = new de.one_piece_content.entity.SandSpikeEntity(
                                de.one_piece_content.entity.OnePieceEntities.SAND_SPIKE, world);
                sandSpike.refreshPositionAndAngles(center.getX(), center.getY(), center.getZ(), playerEntity.getYaw(),
                                0);
                sandSpike.setOwner(playerEntity);
                boolean spawned = world.spawnEntity(sandSpike);
                ExampleMod.LOGGER.info("Sand Spike Spawned: " + spawned + ", Entity ID: " + sandSpike.getId());

                // Trigger animation on client for caster and observers
                de.one_piece_content.network.ContentNetworking.sendToTrackersAndSelf(playerEntity);

                return true;
        }

}