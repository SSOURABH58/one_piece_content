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
    private static final int WAVE_INTERVAL = 40; // 2 seconds between spikes
    private static final int DAMAGE_DELAY = 17; // 0.85s
    private static final int TOTAL_WAVES = 3;

    // Attributes
    private static final float DAMAGE_AMOUNT = 4.0f;
    private static final double DAMAGE_RADIUS = 2.5;

    // Track Owner ID
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(SandSpikeEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private Entity owner;
    private int currentWave = 0;

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

    public Entity getOwner() {
        if (owner == null && !this.getWorld().isClient) {
            owner = this.getWorld().getEntityById(this.dataTracker.get(OWNER_ID));
        }
        return owner;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_ID, -1);
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
            // Trembling Sound & Particles at Target
            if (this.age % 5 == 0) {
                this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0f, 0.5f);
                ((ServerWorld) this.getWorld()).spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SAND.getDefaultState()),
                        this.getX(), this.getY(), this.getZ(),
                        5, 0.5, 0.1, 0.5, 0.1);
            }

            // Trail from Owner to Target (Simulate "blocks shifting underfoot" towards
            // target)
            Entity owner = getOwner();
            if (owner != null && this.age % 2 == 0) {
                double progress = (double) this.age / INITIAL_DELAY;
                Vec3d start = owner.getPos();
                Vec3d end = this.getPos();
                Vec3d current = start.lerp(end, progress);

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

                // End of Wave (Tick 38)
                // Hide spike just before next one starts to clear animation state
                if (tickInWave == 38) {
                    this.setInvisible(true);
                    this.triggerAnim("controller", "stop");
                }
            }
        } else if (relativeTick > (TOTAL_WAVES * WAVE_INTERVAL) + 10) {
            // Cleanup after last wave finishes
            this.discard();
        }
    }

    private void triggerWave() {
        currentWave++;

        // Show Entity
        this.setInvisible(false);

        // Force Animation Restart
        this.triggerAnim("controller", "erupt");

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
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            // Default State: Stop (Invisible/Bind Pose)
            return PlayState.STOP;
        })
                .triggerableAnim("erupt", RawAnimation.begin().thenPlay("animation.sand_spike"))
                .triggerableAnim("stop", RawAnimation.begin().thenPlay("null_anim")) // Force reset
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(OWNER_ID, nbt.getInt("OwnerId"));
        this.currentWave = nbt.getInt("Wave");
        this.age = nbt.getInt("Age");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("OwnerId", this.dataTracker.get(OWNER_ID));
        nbt.putInt("Wave", currentWave);
        nbt.putInt("Age", this.age);
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(),
                this.getPitch(), this.getYaw(), this.getType(), 0, this.getVelocity(), this.getHeadYaw());
    }
}
