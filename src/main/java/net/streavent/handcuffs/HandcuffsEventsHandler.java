
package net.streavent.handcuffs;

import net.streavent.handcuffs.item.KeyItem;
import net.streavent.handcuffs.item.HandcuffsItem;
import net.streavent.handcuffs.config.HandcuffsCommonConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;

import java.util.UUID;
import java.util.Comparator;

@Mod.EventBusSubscriber
public class HandcuffsEventsHandler {
	private static void applyHandcuffedAttribute(PlayerEntity player) {
		player.getAttribute(HandcuffsAttributes.HANDCUFFED.get()).setBaseValue(1.0D);
	}

	@SubscribeEvent
	public static void onEntityHandcuffInteract(PlayerInteractEvent.EntityInteract event) {
		PlayerEntity player = event.getPlayer();
		Entity targetEntity = event.getTarget();
		if (player.isCrouching() && targetEntity instanceof PlayerEntity) {
			PlayerEntity targetPlayer = (PlayerEntity) targetEntity;
			if (player.getPersistentData().contains("HandcuffLinked") || targetPlayer.getPersistentData().contains("HandcuffLinked")) {
				return;
			}
			ItemStack handcuffItemMain = player.getHeldItemMainhand();
			ItemStack handcuffItemOff = player.getHeldItemOffhand();
			if (handcuffItemMain.getItem() instanceof HandcuffsItem && handcuffItemOff.getItem() instanceof HandcuffsItem) {
				String combinedNames = player.getName().getString() + targetPlayer.getName().getString();
				UUID sharedModifierUUID = UUID.nameUUIDFromBytes(combinedNames.getBytes());
				addAttributeModifier(player, Attributes.MOVEMENT_SPEED, sharedModifierUUID, 0.0D, AttributeModifier.Operation.ADDITION);
				addAttributeModifier(targetPlayer, Attributes.MOVEMENT_SPEED, sharedModifierUUID, 0.0D, AttributeModifier.Operation.ADDITION);
				applyHandcuffedAttribute(player);
				applyHandcuffedAttribute(targetPlayer);
				player.getPersistentData().putString("HandcuffUUID", sharedModifierUUID.toString());
				targetPlayer.getPersistentData().putString("HandcuffUUID", sharedModifierUUID.toString());
				player.getPersistentData().putBoolean("HandcuffLinked", true);
				targetPlayer.getPersistentData().putBoolean("HandcuffLinked", true);
				targetPlayer.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
				player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
				player.swingArm(Hand.MAIN_HAND);
				targetPlayer.swingArm(Hand.MAIN_HAND);
				if (!player.abilities.isCreativeMode) {
					player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
					player.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
				}
			}
		}
	}

	private static void addBindAttributeModifier(PlayerEntity player, Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation) {
		AttributeModifier modifier = new AttributeModifier(uuid, "Handcuffed Speed Modifier", amount, operation);
		player.getAttribute(attribute).applyPersistentModifier(modifier);
	}

	public static void removeSharedModifier(PlayerEntity player) {
		if (player.getPersistentData().contains("HandcuffUUID")) {
			String uuidString = player.getPersistentData().getString("HandcuffUUID");
			UUID modifierUUID = UUID.fromString(uuidString);
			ModifiableAttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null && attribute.getModifier(modifierUUID) != null) {
				attribute.removeModifier(modifierUUID);
			}
			player.getPersistentData().remove("HandcuffUUID");
			player.getPersistentData().remove("HandcuffLinked");
		}
	}

	@SubscribeEvent
	public static void onKeyItemeEntityUse(PlayerInteractEvent.EntityInteract event) {
		PlayerEntity player = event.getPlayer();
		ItemStack keyItem = player.getHeldItemMainhand();
		if (event.getTarget() instanceof PlayerEntity) {
			PlayerEntity targetPlayer = (PlayerEntity) event.getTarget();
			ModifiableAttributeInstance handcuffedAttribute = targetPlayer.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
			if (keyItem.getItem() instanceof KeyItem && handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0D) {
				handcuffedAttribute.setBaseValue(0.0D);
				removeSharedModifier(player);
				removeSharedModifier(targetPlayer);
				targetPlayer.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
				generateParticles(player, targetPlayer);
				if (!player.abilities.isCreativeMode) {
					keyItem.shrink(1);
				}
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onKeyItemUse(PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		ItemStack keyItem = player.getHeldItemMainhand();
		if (keyItem.getItem() instanceof KeyItem) {
			ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
			if (handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0D) {
				handcuffedAttribute.setBaseValue(0.0D);
				removeSharedModifier(player);
				player.swingArm(Hand.MAIN_HAND);
				player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
				generateParticles(player, player);
				if (!player.abilities.isCreativeMode) {
					keyItem.shrink(1);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPullPlayerTick(TickEvent.PlayerTickEvent event) {
		PlayerEntity player = event.player;
		if (player.world.isRemote) {
			return;
		}
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (handcuffedAttribute != null && !isPlayerHandcuffed(handcuffedAttribute)) {
			removeSharedModifier(player);
		}
		if (player.getPersistentData().contains("HandcuffUUID")) {
			String handcuffUUID = player.getPersistentData().getString("HandcuffUUID");
			UUID sharedUUID = UUID.fromString(handcuffUUID);
			PlayerEntity linkedPlayer = player.world.getEntitiesWithinAABB(PlayerEntity.class, player.getBoundingBox().grow(50), e -> e != player && handcuffUUID.equals(e.getPersistentData().getString("HandcuffUUID"))).stream()
					.filter(e -> e.isAlive()).min(Comparator.comparingDouble(e -> e.getDistance(player))).orElse(null);
			if (linkedPlayer == null) {
				removeSharedModifier(player);
			} else {
				pullPlayersTogether(player, linkedPlayer);
			}
		}
	}

	private static void pullPlayersTogether(PlayerEntity player, PlayerEntity linkedPlayer) {
		double distance = player.getDistance(linkedPlayer);
		double minDistance = 3.0;
		double maxDistance = 10.0;
		double attractionStrength = 0.1;
		double flightFactor = 1.5;
		if (distance > minDistance) {
			Vector3d direction = new Vector3d(linkedPlayer.getPosX() - player.getPosX(), linkedPlayer.getPosY() - player.getPosY(), linkedPlayer.getPosZ() - player.getPosZ()).normalize();
			if (player.abilities.isFlying || linkedPlayer.abilities.isFlying) {
				attractionStrength *= flightFactor;
			}
			double factor = Math.min(1.0, (distance - minDistance) / (maxDistance - minDistance));
			direction = direction.scale(attractionStrength * factor);
			updatePlayerMotion(player, direction);
			updatePlayerMotion(linkedPlayer, direction.scale(-1));
		}
	}

	private static void updatePlayerMotion(PlayerEntity player, Vector3d motion) {
		Vector3d currentMotion = player.getMotion();
		Vector3d newMotion = currentMotion.add(motion);
		double smoothFactor = 0.5;
		newMotion = currentMotion.add(newMotion.subtract(currentMotion).scale(smoothFactor));
		if (player.abilities.isFlying) {
			newMotion = newMotion.add(0, 0.1, 0);
		}
		player.setMotion(newMotion);
		player.velocityChanged = true;
	}

	private static PlayerEntity getNearestPlayer(PlayerEntity entityIn) {
		return entityIn.world.getEntitiesWithinAABB(PlayerEntity.class, entityIn.getBoundingBox().grow(20), e -> e != entityIn).stream().min(Comparator.comparingDouble(e -> e.getDistance(entityIn))).orElse(null);
	}

	private static boolean isHandcuffedActionInProgress = false;

	@SubscribeEvent
	public static void onHandcuffsEntityUseItem(PlayerInteractEvent.EntityInteract event) {
		if (!HandcuffsCommonConfig.HANDCUFFS_USAGE.get()) {
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack handcuffItem = player.getHeldItemMainhand();
		if (player.getHeldItemOffhand().getItem() instanceof HandcuffsItem || player.getHeldItemOffhand().getItem() instanceof KeyItem) {
			return;
		}
		if (event.getTarget() instanceof PlayerEntity) {
			PlayerEntity targetPlayer = (PlayerEntity) event.getTarget();
			ModifiableAttributeInstance handcuffedAttribute = targetPlayer.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
			if (handcuffItem.getItem() instanceof HandcuffsItem && handcuffedAttribute != null && handcuffedAttribute.getValue() == 0.0D) {
				isHandcuffedActionInProgress = true;
				player.swingArm(Hand.MAIN_HAND);
				targetPlayer.swingArm(Hand.MAIN_HAND);
				targetPlayer.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
				applyHandcuffedAttribute(targetPlayer);
				if (!player.abilities.isCreativeMode) {
					handcuffItem.shrink(1);
				}
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onHandcuffsUseItem(PlayerInteractEvent.RightClickItem event) {
		if (!HandcuffsCommonConfig.HANDCUFFS_USAGE.get()) {
			return;
		}
		if (isHandcuffedActionInProgress) {
			isHandcuffedActionInProgress = false;
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack handcuffItem = player.getHeldItemMainhand();
		if (player.getHeldItemOffhand().getItem() instanceof HandcuffsItem || player.getHeldItemOffhand().getItem() instanceof KeyItem) {
			return;
		}
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (handcuffItem.getItem() instanceof HandcuffsItem && handcuffedAttribute != null && handcuffedAttribute.getValue() == 0.0D) {
			player.swingArm(Hand.MAIN_HAND);
			player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
			applyHandcuffedAttribute(player);
			if (!player.abilities.isCreativeMode) {
				handcuffItem.shrink(1);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTickServer(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START || event.player.world.isRemote || !(event.player instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) event.player;
		if (player.getAttribute(HandcuffsAttributes.HANDCUFFED.get()) != null) {
			double handcuffedValue = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get()).getValue();
			if (handcuffedValue == 1.0) {
				if (HandcuffsCommonConfig.ATTACK_DAMAGE_MODIFIER.get()) {
					UUID ATTACK_DAMAGE_UUID = UUID.fromString("1d9a5d34-1c84-4e4a-8bfc-abb31a7d7913");
					applyAttackDamageModifier(player, ATTACK_DAMAGE_UUID);
				} else {
					UUID ATTACK_DAMAGE_UUID = UUID.fromString("1d9a5d34-1c84-4e4a-8bfc-abb31a7d7913");
					removeAttributeModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
				}
				if (HandcuffsCommonConfig.ATTACK_SPEED_MODIFIER.get()) {
					UUID ATTACK_SPEED_UUID = UUID.fromString("1fa4cb12-dc4a-4e5d-bbc8-e14d376b6d1e");
					applyAttackSpeedModifier(player, ATTACK_SPEED_UUID);
				} else {
					UUID ATTACK_SPEED_UUID = UUID.fromString("1fa4cb12-dc4a-4e5d-bbc8-e14d376b6d1e");
					removeAttributeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
				}
			} else {
				UUID ATTACK_DAMAGE_UUID = UUID.fromString("1d9a5d34-1c84-4e4a-8bfc-abb31a7d7913");
				UUID ATTACK_SPEED_UUID = UUID.fromString("1fa4cb12-dc4a-4e5d-bbc8-e14d376b6d1e");
				if (HandcuffsCommonConfig.ATTACK_DAMAGE_MODIFIER.get()) {
					removeAttributeModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
				}
				if (HandcuffsCommonConfig.ATTACK_SPEED_MODIFIER.get()) {
					removeAttributeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
				}
			}
		} else {
			UUID ATTACK_DAMAGE_UUID = UUID.fromString("1d9a5d34-1c84-4e4a-8bfc-abb31a7d7913");
			UUID ATTACK_SPEED_UUID = UUID.fromString("1fa4cb12-dc4a-4e5d-bbc8-e14d376b6d1e");
			if (HandcuffsCommonConfig.ATTACK_DAMAGE_MODIFIER.get()) {
				removeAttributeModifier(player, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
			}
			if (HandcuffsCommonConfig.ATTACK_SPEED_MODIFIER.get()) {
				removeAttributeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
			}
		}
	}

	private static void applyAttackDamageModifier(PlayerEntity player, UUID attackDamageUUID) {
		if (HandcuffsCommonConfig.ATTACK_DAMAGE_MODIFIER.get()) {
			addAttributeModifier(player, Attributes.ATTACK_DAMAGE, attackDamageUUID, -player.getAttribute(Attributes.ATTACK_DAMAGE).getValue(), AttributeModifier.Operation.ADDITION);
		}
	}

	private static void applyAttackSpeedModifier(PlayerEntity player, UUID attackSpeedUUID) {
		if (HandcuffsCommonConfig.ATTACK_SPEED_MODIFIER.get()) {
			addAttributeModifier(player, Attributes.ATTACK_SPEED, attackSpeedUUID, -player.getAttribute(Attributes.ATTACK_SPEED).getBaseValue(), AttributeModifier.Operation.ADDITION);
		}
	}

	private static void addAttributeModifier(PlayerEntity player, Attribute attribute, UUID uuid, double value, AttributeModifier.Operation operation) {
		String modifierName = "Handcuffs Modifier - " + attribute.getRegistryName();
		AttributeModifier modifier = new AttributeModifier(uuid, modifierName, value, operation);
		if (player.getAttribute(attribute).getModifier(uuid) == null) {
			player.getAttribute(attribute).applyPersistentModifier(modifier);
		}
	}

	private static void removeAttributeModifier(PlayerEntity player, Attribute attribute, UUID uuid) {
		if (player.getAttribute(attribute).getModifier(uuid) != null) {
			player.getAttribute(attribute).removeModifier(uuid);
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute) && HandcuffsCommonConfig.BLOCK_BREAK_RESTRICTION.get()) {
			event.setNewSpeed(0.0f);
		} else {
			event.setNewSpeed(event.getOriginalSpeed());
		}
	}

	@SubscribeEvent
	public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute) && HandcuffsCommonConfig.BLOCK_BREAK_RESTRICTION.get()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute) && HandcuffsCommonConfig.ATTACK_RESTRICTION.get()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (!HandcuffsCommonConfig.BLOCK_INTERACTION_RESTRICTION.get())
			return;
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute)) {
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
			boolean isContainer = block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL || block == Blocks.ENDER_CHEST;
			if (isContainer) {
				if (HandcuffsCommonConfig.CONTAINER_INTERACTION_RESTRICTION.get()) {
					event.setCanceled(true);
				} else {
					event.setCanceled(false);
				}
			} else {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute) && HandcuffsCommonConfig.ENTITY_INTERACTION_RESTRICTION.get()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onHandcuffed(PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute)) {
			if (!(player.getHeldItemMainhand().getItem() instanceof KeyItem)) {
				event.setCanceled(true);
			}
		}
	}

	private static void generateParticles(PlayerEntity player, LivingEntity target) {
		ItemStack handcuffsStack = new ItemStack(Items.CHAIN);
		ItemParticleData particleData = new ItemParticleData(ParticleTypes.ITEM, handcuffsStack);
		World world = player.world;
		double yaw = Math.toRadians(target.rotationYaw);
		double pitch = Math.toRadians(target.rotationPitch);
		double offsetX = -Math.sin(yaw);
		double offsetZ = Math.cos(yaw);
		double offsetY = -Math.sin(pitch);
		int particleCount = 10;
		if (world instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) world;
			for (int i = 0; i < particleCount; i++) {
				double randomOffsetX = (world.rand.nextDouble() - 0.5) * 0.2;
				double randomOffsetY = (world.rand.nextDouble() - 0.5) * 0.2;
				double randomOffsetZ = (world.rand.nextDouble() - 0.5) * 0.2;
				serverWorld.spawnParticle(particleData, target.getPosX() + offsetX * 0.5 + randomOffsetX, target.getPosY() + target.getEyeHeight() * 0.75 + offsetY * 0.5 + randomOffsetY, target.getPosZ() + offsetZ * 0.5 + randomOffsetZ, 0, 0.1, 0, 0,
						0);
			}
		}
	}

	private static boolean isPlayerHandcuffed(ModifiableAttributeInstance handcuffedAttribute) {
		return handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0D;
	}
}
