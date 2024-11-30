
package net.streavent.handcuffs;

import net.streavent.handcuffs.config.HandcuffsCommonConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.command.Commands;
import net.minecraft.command.CommandSource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Command;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HandcuffsCommand {
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		registerCommand(event.getDispatcher());
	}

	private static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("handcuffs").requires(source -> HandcuffsCommonConfig.HANDCUFFS_COMMAND_USAGE.get() && source.hasPermissionLevel(2)) // Verifica configuración dinámicamente
				.then(Commands.argument("player", StringArgumentType.word()).suggests((context, builder) -> {
					context.getSource().getServer().getPlayerList().getPlayers().forEach(player -> builder.suggest(player.getName().getString()));
					return builder.buildFuture();
				}).then(Commands.argument("action", StringArgumentType.word()).suggests((context, builder) -> {
					builder.suggest("handcuff");
					builder.suggest("uncuff");
					return builder.buildFuture();
				}).executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "player"), StringArgumentType.getString(context, "action"))))));
	}

	public static int execute(CommandSource source, String playerName, String action) {
		if (!HandcuffsCommonConfig.HANDCUFFS_COMMAND_USAGE.get()) {
			source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.disabled"));
			return Command.SINGLE_SUCCESS;
		}
		ServerPlayerEntity player = source.getServer().getPlayerList().getPlayerByUsername(playerName);
		if (player == null) {
			source.sendErrorMessage(new TranslationTextComponent("commands.generic.player.notFound"));
			return Command.SINGLE_SUCCESS;
		}
		switch (action.toLowerCase()) {
			case "handcuff" :
				ModifiableAttributeInstance attribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
				if (attribute != null && attribute.getBaseValue() == 0.0D) {
					if (applyHandcuffedAttribute(player)) {
						source.sendFeedback(new TranslationTextComponent("commands.handcuffs.success.handcuffed", player.getDisplayName().getString()), true);
						playHandcuffSound(player);
					}
				} else {
					source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.alreadyHandcuffed", player.getDisplayName().getString()));
				}
				break;
			case "uncuff" :
				ModifiableAttributeInstance atttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
				if (atttribute != null && atttribute.getBaseValue() == 1.0D) {
					if (removeHandcuffedAttribute(player)) {
						source.sendFeedback(new TranslationTextComponent("commands.handcuffs.success.uncuffed", player.getDisplayName().getString()), true);
						playUncuffSound(player);
						generateParticles(player);
					}
				} else {
					source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.notHandcuffed", player.getDisplayName().getString()));
				}
				break;
			default :
				source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.invalidAction"));
				break;
		}
		return Command.SINGLE_SUCCESS;
	}

	private static boolean applyHandcuffedAttribute(ServerPlayerEntity player) {
		ModifiableAttributeInstance attribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (attribute == null) {
			return false;
		}
		attribute.setBaseValue(1.0D);
		return true;
	}

	private static boolean removeHandcuffedAttribute(ServerPlayerEntity player) {
		ModifiableAttributeInstance attribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (attribute == null) {
			return false;
		}
		attribute.setBaseValue(0.0D);
		return true;
	}

	private static void generateParticles(ServerPlayerEntity target) {
		ItemStack handcuffsStack = new ItemStack(Items.CHAIN);
		ItemParticleData particleData = new ItemParticleData(ParticleTypes.ITEM, handcuffsStack);
		World world = target.world;
		int particleCount = 30;
		if (world instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) world;
			for (int i = 0; i < particleCount; i++) {
				double randomOffsetX = (world.rand.nextDouble() - 0.5) * 0.4;
				double randomOffsetY = (world.rand.nextDouble() * 1.5);
				double randomOffsetZ = (world.rand.nextDouble() - 0.5) * 0.4;
				serverWorld.spawnParticle(particleData, target.getPosX() + randomOffsetX, target.getPosY() + randomOffsetY, target.getPosZ() + randomOffsetZ, 1, 0, 0, 0, 0);
			}
		}
	}

	private static void playHandcuffSound(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	private static void playUncuffSound(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
}
