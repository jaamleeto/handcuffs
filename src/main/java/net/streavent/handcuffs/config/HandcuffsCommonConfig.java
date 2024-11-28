package net.streavent.handcuffs.config;

import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;

public class HandcuffsCommonConfig {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.BooleanValue ENABLE_HANDCUFFS_USAGE;
	public static final ForgeConfigSpec.BooleanValue ENABLE_ATTACK_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue ENABLE_BLOCK_INTERACTION_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_INTERACTION_RESTRICTION;
	public static final ForgeConfigSpec.BooleanValue ENABLE_ITEM_BREAKING_EFFECTS;
	public static final ForgeConfigSpec.BooleanValue ENABLE_BLOCK_BREAK_RESTRICTION;
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("Common configuration for Handcuffs Mod").push("handcuffs");
		ENABLE_HANDCUFFS_USAGE = builder.comment("Enable or disable the general usage of handcuffs").define("enableHandcuffsUsage", true);
		ENABLE_ATTACK_RESTRICTION = builder.comment("Restrict attacking while holding handcuffs in the offhand").define("enableAttackRestriction", true);
		ENABLE_BLOCK_INTERACTION_RESTRICTION = builder.comment("Restrict block interactions while holding handcuffs in the offhand").define("enableBlockInteractionRestriction", true);
		ENABLE_ENTITY_INTERACTION_RESTRICTION = builder.comment("Restrict entity interactions while holding handcuffs in the offhand").define("enableEntityInteractionRestriction", true);
		ENABLE_ITEM_BREAKING_EFFECTS = builder.comment("Enable or disable breaking effects when using a key to break handcuffs").define("enableItemBreakingEffects", true);
		ENABLE_BLOCK_BREAK_RESTRICTION = builder.comment("Restrict block breaking while holding handcuffs in the offhand").define("enableBlockBreakRestriction", true);
		builder.pop();
		COMMON_CONFIG = builder.build();
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG, "handcuffs-common.toml");
	}
}
