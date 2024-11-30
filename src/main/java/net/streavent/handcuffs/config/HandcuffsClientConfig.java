package net.streavent.handcuffs.config;

import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;

public class HandcuffsClientConfig {
	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	// Configurations for Handcuffs Layer
	public static final ForgeConfigSpec.DoubleValue ROTATE_X;
	public static final ForgeConfigSpec.DoubleValue ROTATE_Y;
	public static final ForgeConfigSpec.DoubleValue ROTATE_Z;
	public static final ForgeConfigSpec.DoubleValue TRANSLATE_X;
	public static final ForgeConfigSpec.DoubleValue TRANSLATE_Y;
	public static final ForgeConfigSpec.DoubleValue TRANSLATE_Z;
	// Tooltip Handler category
	public static final ForgeConfigSpec.BooleanValue ENABLE_TOOLTIPS;
	public static final ForgeConfigSpec.BooleanValue ENABLE_HANDCUFFS_TOOLTIP;
	public static final ForgeConfigSpec.BooleanValue ENABLE_KEY_TOOLTIP;
	static {
		BUILDER.push("Handcuffs Layer Handler");
		ROTATE_X = BUILDER.comment("Rotation on the X-axis").defineInRange("rotateX", 0.0, -180.0, 180.0);
		ROTATE_Y = BUILDER.comment("Rotation on the Y-axis").defineInRange("rotateY", 0.0, -180.0, 180.0);
		ROTATE_Z = BUILDER.comment("Rotation on the Z-axis").defineInRange("rotateZ", 0.0, -180.0, 180.0);
		TRANSLATE_X = BUILDER.comment("Translation on the X-axis").defineInRange("translateX", 0.0, -1.0, 1.0);
		TRANSLATE_Y = BUILDER.comment("Translation on the Y-axis").defineInRange("translateY", 0.0, -1.0, 1.0);
		TRANSLATE_Z = BUILDER.comment("Translation on the Z-axis").defineInRange("translateZ", 0.0, -1.0, 1.0);
		BUILDER.pop();
		// Tooltip Handler category
		BUILDER.push("Tooltip Handler");
		ENABLE_TOOLTIPS = BUILDER.comment("Enable or disable handcuffs tooltips").define("Tooltips", true);
		ENABLE_HANDCUFFS_TOOLTIP = BUILDER.comment("Enable or disable the handcuffs tooltip").define("Handcuffs", true);
		ENABLE_KEY_TOOLTIP = BUILDER.comment("Enable or disable the key tooltip").define("Key", true);
		BUILDER.pop();
		CLIENT_CONFIG = BUILDER.build();
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG, "handcuffs-client.toml");
	}
}
