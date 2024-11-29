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
	public static final ForgeConfigSpec.BooleanValue ATTACK_DAMAGE_MODIFIER;
	public static final ForgeConfigSpec.BooleanValue ATTACK_SPEED_MODIFIER;
	static {
		BUILDER.push("Handcuffs Behavior Settings");
		// General Handcuffs Usage
		HANDCUFFS_USAGE = BUILDER.comment("Allow or disable the general usage of handcuffs.").define("Allow Handcuffs Usage", true);
		// Interaction Restrictions
		BUILDER.push("Interaction Restrictions");
		BLOCK_INTERACTION_RESTRICTION = BUILDER.comment("Restrict block interactions (placing or breaking blocks) while the player is handcuffed.").define("Restrict Block Interaction", true);
		ENTITY_INTERACTION_RESTRICTION = BUILDER.comment("Restrict entity interactions (e.g., right-clicking entities) while the player is handcuffed.").define("Restrict Entity Interaction", true);
		CONTAINER_INTERACTION_RESTRICTION = BUILDER.comment("Restrict interactions with containers (such as chests, barrels, etc.) while the player is handcuffed.").define("Restrict Container Interaction", true);
		BUILDER.pop();
		// Attack Restrictions
		ATTACK_RESTRICTION = BUILDER.comment("Restrict attacking while the player is handcuffed.").define("Restrict Attack While Handcuffed", true);
		// Effects and Restrictions for Item Breaking and Block Breaking
		ITEM_BREAKING_EFFECTS = BUILDER.comment("Enable or disable effects when using a key to break the handcuffs.").define("Enable Item Breaking Effects", true);
		BLOCK_BREAK_RESTRICTION = BUILDER.comment("Restrict block breaking while the player is handcuffed.").define("Restrict Block Breaking", true);
		// Command and Modifier Settings
		HANDCUFFS_COMMAND_USAGE = BUILDER.comment("Enable or disable the handcuffs command.").define("Enable Handcuffs Command Usage", true);
		ATTACK_DAMAGE_MODIFIER = BUILDER.comment("Enable or disable attack damage modifier when the player is handcuffed.").define("Enable Attack Damage Modifier", true);
		ATTACK_SPEED_MODIFIER = BUILDER.comment("Enable or disable attack speed modifier when the player is handcuffed.").define("Enable Attack Speed Modifier", true);
		BUILDER.pop();
		COMMON_CONFIG = BUILDER.build();
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG, "handcuffs-common.toml");
	}
}
