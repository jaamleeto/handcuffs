
package net.streavent.handcuffs;

import net.streavent.handcuffs.config.HandcuffsCommonConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.command.Commands;
import net.minecraft.command.CommandSource;

import java.util.UUID;

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
				if (applyHandcuffedAttribute(player)) {
					source.sendFeedback(new TranslationTextComponent("commands.handcuffs.success.handcuffed", player.getDisplayName().getString()), true);
					playHandcuffSound(player);
				} else {
					source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.attributeNotFound"));
				}
				break;
			case "uncuff" :
				if (removeHandcuffedAttribute(player)) {
					source.sendFeedback(new TranslationTextComponent("commands.handcuffs.success.uncuffed", player.getDisplayName().getString()), true);
					playUncuffSound(player);
					removeSharedModifier(player);
				} else {
					source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.attributeNotFound"));
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

	public static void removeSharedModifier(ServerPlayerEntity player) {
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

	private static void playHandcuffSound(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	private static void playUncuffSound(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
}
