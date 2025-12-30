package de.one_piece_content.entity;

import de.one_piece_content.ExampleMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

import de.one_piece_content.config.SandSpikeConfig;

public class SandSpikeEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int lifetime = SandSpikeConfig.lifeTimeTicks;

    public SandSpikeEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    private net.minecraft.entity.Entity owner;
    private boolean hasDamaged = false;
    private int damageDelay = 10; // Delay damage slightly to match animation

    public void setOwner(net.minecraft.entity.Entity owner) {
        this.owner = owner;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.age > lifetime) {
                this.discard();
            }

            if (this.age >= damageDelay && !hasDamaged) {
                java.util.List<net.minecraft.entity.LivingEntity> targets = this.getWorld().getEntitiesByClass(
                        net.minecraft.entity.LivingEntity.class,
                        this.getBoundingBox().expand(0.5), // Slightly larger hit area
                        e -> e != owner && e.isAlive());

                for (net.minecraft.entity.LivingEntity target : targets) {
                    net.minecraft.entity.damage.DamageSource source;
                    if (owner instanceof net.minecraft.entity.player.PlayerEntity player) {
                        source = this.getWorld().getDamageSources().playerAttack(player);
                    } else if (owner instanceof net.minecraft.entity.LivingEntity livingOwner) {
                        source = this.getWorld().getDamageSources().mobAttack(livingOwner);
                    } else {
                        source = this.getWorld().getDamageSources().magic();
                    }
                    target.damage(source, SandSpikeConfig.damage);
                }
                hasDamaged = true;
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.sand_spike"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void initDataTracker(net.minecraft.entity.data.DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(),
                this.getPitch(), this.getYaw(), this.getType(), 0, this.getVelocity(), this.getHeadYaw());
    }
}
