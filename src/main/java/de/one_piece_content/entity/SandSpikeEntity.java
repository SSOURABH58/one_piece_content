package de.one_piece_content.entity;

import de.one_piece_content.ExampleMod;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;

public class SandSpikeEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Timing Constants (in ticks)
    private static final int INITIAL_DELAY = 40; // 2 seconds trembling
    // Cycle: Animation (46 ticks) + Wait = 90 ticks
    private static final int WAVE_INTERVAL = 90;
    private static final int ANIMATION_DURATION = 47; // 46 ticks animation + 1 tick buffer
    private static final int DAMAGE_DELAY = 17; // 0.85s point
    private static final int TOTAL_WAVES = 3;

    // Attributes
    private static final float DAMAGE_AMOUNT = 4.0f;
    private static final double DAMAGE_RADIUS = 2.5;

    // Track Owner ID
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(SandSpikeEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    // Animation Wave Counter (0 = Idle, 1, 2, 3... = Wave Start)
    private static final TrackedData<Integer> WAVE_COUNTER = DataTracker.registerData(SandSpikeEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private Entity owner;
    private int currentWave = 0;

    // Client-side tracking
    private int lastRenderedWave = -1;
    private int waveStartTick = 0;

    public SandSpikeEntity(EntityType<?> type, World world) {
        super(type, world);
        // Start invisible (trembling phase)
        this.setInvisible(true);
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
        this.dataTracker.set(OWNER_ID, owner.getId());
    }

    public int getOwnerId() {
        return this.dataTracker.get(OWNER_ID);
    }

    public int getWaveCounter() {
        return this.dataTracker.get(WAVE_COUNTER);
    }

    public Entity getOwner() {
        if (owner == null && !this.getWorld().isClient) {
            owner = this.getWorld().getEntityById(this.dataTracker.get(OWNER_ID));
        }
        return owner;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_ID, -1);
        builder.add(WAVE_COUNTER, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            return;
        }

        // --- SERVER SIDE LOGIC ---

        // 1. Initial Phase: Trembling & Trail (Ticks 0-40)
        if (this.age < INITIAL_DELAY) {
            // Trembling particles...
            if (this.age % 5 == 0) {
                this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0f, 0.5f);
                ((ServerWorld) this.getWorld()).spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SAND.getDefaultState()),
                        this.getX(), this.getY(), this.getZ(),
                        5, 0.5, 0.1, 0.5, 0.1);
            }
            // Trail logic
            Entity owner = getOwner();
            if (owner != null && this.age % 2 == 0) {
                double progress = (double) this.age / INITIAL_DELAY;
                Vec3d current = owner.getPos().lerp(this.getPos(), progress);
                ((ServerWorld) this.getWorld()).spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SAND.getDefaultState()),
                        current.x, current.y + 0.1, current.z,
                        3, 0.2, 0.1, 0.2, 0.05);
            }
            return;
        }

        // 2. Attack Waves Logic
        int relativeTick = this.age - INITIAL_DELAY;

        if (currentWave < TOTAL_WAVES) {
            if (relativeTick >= 0) {
                int tickInWave = relativeTick % WAVE_INTERVAL;

                // Start of Wave (Tick 0)
                if (tickInWave == 0) {
                    triggerWave();
                }

                // Damage Frame
                if (tickInWave == DAMAGE_DELAY) {
                    dealDamage();
                }

                // End of Animation (Tick 46) - Hide and Wait
                if (tickInWave == ANIMATION_DURATION) {
                    this.dataTracker.set(WAVE_COUNTER, 0); // Reset to 0 (Idle)
                    this.setInvisible(true);
                }
            }
        } else if (relativeTick > (TOTAL_WAVES * WAVE_INTERVAL) + 10) {
            // Cleanup after last wave finishes
            this.discard();
        }
    }

    private void triggerWave() {
        // Increment wave count
        currentWave++;

        // Show Entity & Set Anim State
        this.setInvisible(false);
        this.dataTracker.set(WAVE_COUNTER, currentWave); // 1, 2, 3...

        // Sound
        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 0.5f, 0.5f + (currentWave * 0.1f));
        this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0f, 0.1f);

        // Visual Explosion
        ((ServerWorld) this.getWorld()).spawnParticles(
                ParticleTypes.EXPLOSION,
                this.getX(), this.getY() + 1, this.getZ(),
                1, 0, 0, 0, 0);
        ((ServerWorld) this.getWorld()).spawnParticles(
                ParticleTypes.LARGE_SMOKE,
                this.getX(), this.getY(), this.getZ(),
                10, 1.0, 1.0, 1.0, 0.1);
    }

    private void dealDamage() {
        // Damage logic...
        List<LivingEntity> targets = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(DAMAGE_RADIUS, 2.0, DAMAGE_RADIUS),
                e -> e != getOwner() && e.isAlive());

        DamageSource source;
        Entity owner = getOwner();
        if (owner instanceof net.minecraft.entity.player.PlayerEntity player) {
            source = this.getWorld().getDamageSources().playerAttack(player);
        } else if (owner instanceof LivingEntity livingOwner) {
            source = this.getWorld().getDamageSources().mobAttack(livingOwner);
        } else {
            source = this.getWorld().getDamageSources().magic();
        }

        for (LivingEntity target : targets) {
            if (target.getY() > this.getY() + 1.5) {
                continue;
            }

            target.damage(source, DAMAGE_AMOUNT);
            target.takeKnockback(0.5, this.getX() - target.getX(), this.getZ() - target.getZ());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Precise state-based control
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            int wave = this.getWaveCounter();

            // If wave > 0, we play "start". The controller resets automatically if
            // setAnimation is called?
            // setAndContinue will NOT reset if it thinks it's the same animation.
            // We need to detect "New Wave".

            if (wave > 0) {
                // 1. Detect Wave Start
                if (wave != lastRenderedWave) {
                    lastRenderedWave = wave;
                    waveStartTick = this.age;
                    event.getController().forceAnimationReset();
                }

                // 2. Client-Side Duration Cap
                // Animation is exactly 46 ticks long. Stop immediately after to prevent
                // holding/looping.
                // Explicitly prevent looping by stopping exactly at animation end.
                if (this.age - waveStartTick > 46) {
                    return PlayState.STOP;
                }

                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.sand_spike"));
            }

            // If wave == 0 (Idle wait), Stop.
            lastRenderedWave = 0;
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("OwnerId")) {
            this.dataTracker.set(OWNER_ID, nbt.getInt("OwnerId"));
        }
        if (nbt.contains("WaveCounter")) {
            this.dataTracker.set(WAVE_COUNTER, nbt.getInt("WaveCounter"));
        }
        this.currentWave = nbt.getInt("Wave");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("OwnerId", this.dataTracker.get(OWNER_ID));
        nbt.putInt("WaveCounter", this.dataTracker.get(WAVE_COUNTER));
        nbt.putInt("Wave", currentWave);
    }

    // Packet method
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(),
                this.getPitch(), this.getYaw(), this.getType(), 0, this.getVelocity(), this.getHeadYaw());
    }
}
