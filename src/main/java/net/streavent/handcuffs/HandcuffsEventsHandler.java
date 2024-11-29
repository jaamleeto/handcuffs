
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
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;

import java.util.UUID;

@Mod.EventBusSubscriber
public class HandcuffsEventsHandler {
	private static void applyHandcuffedAttribute(PlayerEntity player) {
		player.getAttribute(HandcuffsAttributes.HANDCUFFED.get()).setBaseValue(1.0D);
	}

	@SubscribeEvent
	public static void onHandcuffsUseItem(PlayerInteractEvent.EntityInteract event) {
		if (!HandcuffsCommonConfig.HANDCUFFS_USAGE.get())
			return;
		PlayerEntity player = event.getPlayer();
		ItemStack handcuffItem = player.getHeldItemMainhand();
		if (event.getTarget() instanceof PlayerEntity) {
			PlayerEntity targetPlayer = (PlayerEntity) event.getTarget();
			ModifiableAttributeInstance handcuffedAttribute = targetPlayer.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
			if (handcuffItem.getItem() instanceof HandcuffsItem && handcuffedAttribute != null && handcuffedAttribute.getValue() == 0.0D) {
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
	public static void onKeyItemUse(PlayerInteractEvent.EntityInteract event) {
		PlayerEntity player = event.getPlayer();
		ItemStack keyItem = player.getHeldItemMainhand();
		if (event.getTarget() instanceof PlayerEntity) {
			PlayerEntity targetPlayer = (PlayerEntity) event.getTarget();
			ModifiableAttributeInstance handcuffedAttribute = targetPlayer.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
			if (keyItem.getItem() instanceof KeyItem && handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0D) {
				handcuffedAttribute.setBaseValue(0.0D);
				targetPlayer.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
				generateParticles(player, targetPlayer);
				if (!player.abilities.isCreativeMode) {
					keyItem.shrink(1);
				}
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

	@SubscribeEvent
	public static void onHandcuffsUseItem(PlayerInteractEvent.RightClickItem event) {
		if (!HandcuffsCommonConfig.HANDCUFFS_USAGE.get())
			return;
		PlayerEntity player = event.getPlayer();
		ItemStack handcuffItem = player.getHeldItemMainhand();
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
	public static void onKeyItemUse(PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		ItemStack keyItem = player.getHeldItemMainhand();
		if (keyItem.getItem() instanceof KeyItem) {
			ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
			if (handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0D) {
				handcuffedAttribute.setBaseValue(0.0D);
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
		if (event.getPlayer().world.isRemote)
			return;
		if (!HandcuffsCommonConfig.BLOCK_BREAK_RESTRICTION.get())
			return;
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute)) {
			event.setNewSpeed(0.0f);
		} else {
			event.setNewSpeed(event.getOriginalSpeed());
		}
	}

	@SubscribeEvent
	public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (!HandcuffsCommonConfig.BLOCK_BREAK_RESTRICTION.get())
			return;
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		if (!HandcuffsCommonConfig.ATTACK_RESTRICTION.get())
			return;
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute)) {
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
			boolean isContainer = block instanceof IInventory || block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL || block == Blocks.ENDER_CHEST;
			if (HandcuffsCommonConfig.CONTAINER_INTERACTION_RESTRICTION.get() && isContainer) {
				event.setCanceled(true);
			} else if (!HandcuffsCommonConfig.CONTAINER_INTERACTION_RESTRICTION.get()) {
				event.setCanceled(false);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (!HandcuffsCommonConfig.ENTITY_INTERACTION_RESTRICTION.get())
			return;
		PlayerEntity player = event.getPlayer();
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (isPlayerHandcuffed(handcuffedAttribute)) {
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

	private static boolean isPlayerHandcuffed(ModifiableAttributeInstance handcuffedAttribute) {
		return handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0D;
	}
}
