package net.streavent.handcuffs.entity;

import net.streavent.handcuffs.init.HandcuffsModEntities;

import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.network.IPacket;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.AreaEffectCloudEntity;

public class HandpointEntity extends CreatureEntity {
	public HandpointEntity(FMLPlayMessages.SpawnEntity packet, World world) {
		this(HandcuffsModEntities.HANDPOINT.get(), world);
	}

	public HandpointEntity(EntityType<HandpointEntity> type, World world) {
		super(type, world);
		stepHeight = 0.6f;
		experienceValue = 0;
		setNoAI(false);
		this.noClip = true;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.attacker.getWidth() * this.attacker.getWidth() + entity.getWidth();
			}
		});
		this.goalSelector.addGoal(2, new RandomWalkingGoal(this, 1));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
		this.goalSelector.addGoal(5, new SwimGoal(this));
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEFINED;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isRemote) {
			int linkedPlayerID = this.getPersistentData().getInt("TargetPosID");
			Entity linkedPlayer = ((ServerWorld) this.world).getEntityByID(linkedPlayerID);
			if (linkedPlayer instanceof PlayerEntity) {
				this.setPosition(linkedPlayer.getPosX(), linkedPlayer.getPosY() + 0.8, linkedPlayer.getPosZ());
				this.rotationYaw = linkedPlayer.rotationYaw;
				this.rotationPitch = linkedPlayer.rotationPitch;
			} else {
				this.remove();
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float amount) {
		if (damagesource.getImmediateSource() instanceof AbstractArrowEntity)
			return false;
		if (damagesource.getImmediateSource() instanceof PlayerEntity)
			return false;
		if (damagesource.getImmediateSource() instanceof PotionEntity || damagesource.getImmediateSource() instanceof AreaEffectCloudEntity)
			return false;
		if (damagesource == DamageSource.FALL)
			return false;
		if (damagesource == DamageSource.CACTUS)
			return false;
		if (damagesource == DamageSource.DROWN)
			return false;
		if (damagesource == DamageSource.LIGHTNING_BOLT)
			return false;
		if (damagesource.isExplosion())
			return false;
		if (damagesource.getDamageType().equals("trident"))
			return false;
		if (damagesource == DamageSource.ANVIL)
			return false;
		if (damagesource == DamageSource.DRAGON_BREATH)
			return false;
		if (damagesource == DamageSource.WITHER || damagesource.getDamageType().equals("witherSkull"))
			return false;
		return super.attackEntityFrom(damagesource, amount);
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	public boolean isImmuneToFire() {
		return true;
	}

	@Override
	public boolean canBreatheUnderwater() {
		double x = this.getPosX();
		double y = this.getPosY();
		double z = this.getPosZ();
		World world = this.world;
		Entity entity = this;
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) {
	}

	@Override
	protected void collideWithNearbyEntities() {
	}

	public static void init() {
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		AttributeModifierMap.MutableAttribute builder = MobEntity.func_233666_p_();
		builder = builder.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.createMutableAttribute(Attributes.MAX_HEALTH, 10);
		builder = builder.createMutableAttribute(Attributes.ARMOR, 0);
		builder = builder.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.createMutableAttribute(Attributes.FOLLOW_RANGE, 16);
		return builder;
	}
}
