
package net.streavent.handcuffs;

import net.streavent.handcuffs.config.HandcuffsCommonConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
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
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		if (HandcuffsCommonConfig.HANDCUFFS_COMMAND_USAGE.get()) {
			registerCommand(dispatcher);
		}
	}

	private static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("handcuffs").requires(source -> source.hasPermissionLevel(2)).then(Commands.argument("player", StringArgumentType.word()).suggests((context, builder) -> {
			context.getSource().getServer().getPlayerList().getPlayers().forEach(player -> builder.suggest(player.getName().getString()));
			return builder.buildFuture();
		}).then(Commands.argument("action", StringArgumentType.word()).suggests((context, builder) -> {
			builder.suggest("handcuff");
			builder.suggest("uncuff");
			return builder.buildFuture();
		}).executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "player"), StringArgumentType.getString(context, "action"))))));
	}

	public static int execute(CommandSource source, String playerName, String action) {
		ServerPlayerEntity player = source.getServer().getPlayerList().getPlayerByUsername(playerName);
		if (player == null) {
			source.sendErrorMessage(new TranslationTextComponent("commands.generic.player.notFound"));
			return Command.SINGLE_SUCCESS;
		}
		switch (action.toLowerCase()) {
			case "handcuff" :
				applyHandcuffedAttribute(player);
				source.sendFeedback(new StringTextComponent(player.getDisplayName().getString() + " has been handcuffed."), true);
				break;
			case "uncuff" :
				removeHandcuffedAttribute(player);
				source.sendFeedback(new StringTextComponent(player.getDisplayName().getString() + " has been uncuffed."), true);
				break;
			default :
				source.sendErrorMessage(new TranslationTextComponent("commands.handcuffs.invalidAction"));
				break;
		}
		return Command.SINGLE_SUCCESS;
	}

	private static void applyHandcuffedAttribute(ServerPlayerEntity player) {
		player.getAttribute(HandcuffsAttributes.HANDCUFFED.get()).setBaseValue(1.0D);
	}

	private static void removeHandcuffedAttribute(ServerPlayerEntity player) {
		ModifiableAttributeInstance attribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (attribute != null) {
			attribute.removeAllModifiers();
		}
		player.getAttribute(HandcuffsAttributes.HANDCUFFED.get()).setBaseValue(0.0D);
	}
}
