package net.streavent.handcuffs.config;

import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;

public class HandcuffsCommonConfig {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	// Configurations for Handcuffs behavior
	public static final ForgeConfigSpec.BooleanValue HANDCUFFS_USAGE;
	public static final ForgeConfigSpec.BooleanValue ATTACK_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue BLOCK_INTERACTION_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue ENTITY_INTERACTION_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue ITEM_BREAKING_EFFECTS;
	public static final ForgeConfigSpec.BooleanValue BLOCK_BREAK_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue CONTAINER_INTERACTION_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue HANDCUFFS_COMMAND_USAGE;
	public static final ForgeConfigSpec.BooleanValue ATTACK_MODIFIERS;
	public static final ForgeConfigSpec.BooleanValue ATTACK_SPEED_MODIFIERS;
	static {
		BUILDER.push("Handcuffs Behavior Settings");
		// Handcuffs Usage
		HANDCUFFS_USAGE = BUILDER.comment("Enable or disable the general usage of handcuffs.").define("Handcuffs Usage", true);
		// Attack Restrictions
		ATTACK_RESTRICTION = BUILDER.comment("Restrict attacking while the player is handcuffed.").define("Attack Restriction", true);
		// Block Interaction Restrictions
		BLOCK_INTERACTION_RESTRICTION = BUILDER.comment("Restrict block interactions (placing or breaking blocks) while the player is handcuffed.").define("Block Interaction Restriction", true);
		// Entity Interaction Restrictions
		ENTITY_INTERACTION_RESTRICTION = BUILDER.comment("Restrict entity interactions (e.g., right-clicking entities) while the player is handcuffed.").define("Entity Interaction Restriction", true);
		// Item Breaking Effects
		ITEM_BREAKING_EFFECTS = BUILDER.comment("Enable or disable effects when using a key to break the handcuffs.").define("Item Breaking Effects", true);
		// Block Breaking Restrictions
		BLOCK_BREAK_RESTRICTION = BUILDER.comment("Restrict block breaking while the player is handcuffed.").define("Block Breaking Restriction", true);
		// Container Interaction Restrictions (e.g., chests, barrels, etc.)
		CONTAINER_INTERACTION_RESTRICTION = BUILDER.comment("Restrict interactions with containers (such as chests, barrels, etc.) while the player is handcuffed.").define("Container Interaction Restriction", true);
		// Handcuffs Command Usage
		HANDCUFFS_COMMAND_USAGE = BUILDER.comment("Enable or disable the handcuffs command.").define("Handcuffs Command Usage", true);
		// Attack Modifiers
		ATTACK_MODIFIERS = BUILDER.comment("Enable or disable attack damage modifier when the player is handcuffed.").define("Attack Damage Modifier", true);
		// Attack Speed Modifiers
		ATTACK_SPEED_MODIFIERS = BUILDER.comment("Enable or disable attack speed modifier when the player is handcuffed.").define("Attack Speed Modifier", true);
		BUILDER.pop();
		COMMON_CONFIG = BUILDER.build();
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG, "handcuffs-common.toml");
	}
}
