package de.one_piece_content.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SandSpikeEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Animation constant to prevent per-tick object creation
    private static final RawAnimation SPIKE_ANIM = RawAnimation.begin().thenPlay("animation.sand_spike");

    // Increased lifespan for debugging and to ensure animation finishes
    private static final int LIFE_TICKS = 100;

    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(SandSpikeEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private Entity owner;

    public SandSpikeEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.noClip = true;
        this.ignoreCameraFrustum = true;
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

        if (this.getWorld().isClient)
            return;

        // Die exactly at the requested frame/tick limit
        if (this.age >= LIFE_TICKS) {
            this.discard();
        }

        // Standard damage peak
        if (this.age == 17) {
            dealDamage();
        }
    }

    private void dealDamage() {
        List<LivingEntity> targets = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(2.5, 2.0, 2.5),
                e -> e != getOwner() && e.isAlive());

        DamageSource source;
        Entity owner = getOwner();
        if (owner instanceof PlayerEntity player) {
            source = this.getWorld().getDamageSources().playerAttack(player);
        } else if (owner instanceof LivingEntity livingOwner) {
            source = this.getWorld().getDamageSources().mobAttack(livingOwner);
        } else {
            source = this.getWorld().getDamageSources().magic();
        }

        for (LivingEntity target : targets) {
            if (target.getY() <= this.getY() + 1.5) {
                target.damage(source, 4.0f);
                target.takeKnockback(0.5, this.getX() - target.getX(), this.getZ() - target.getZ());
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.getController().getAnimationState() != AnimationController.State.STOPPED) {
                return PlayState.CONTINUE;
            }
            return state.setAndContinue(SPIKE_ANIM);
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
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("OwnerId", this.dataTracker.get(OWNER_ID));
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(),
                this.getPitch(), this.getYaw(), this.getType(), 0, this.getVelocity(), this.getHeadYaw());
    }
}
