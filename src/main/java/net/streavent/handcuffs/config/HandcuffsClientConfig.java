package net.streavent.handcuffs.config;

import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.common.ForgeConfigSpec;

public class HandcuffsClientConfig {
	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ForgeConfigSpec.DoubleValue ROTATE_X;
	public static final ForgeConfigSpec.DoubleValue ROTATE_Y;
	public static final ForgeConfigSpec.DoubleValue ROTATE_Z;
	public static final ForgeConfigSpec.DoubleValue TRANSLATE_X;
	public static final ForgeConfigSpec.DoubleValue TRANSLATE_Y;
	public static final ForgeConfigSpec.DoubleValue TRANSLATE_Z;
	public static final ForgeConfigSpec.DoubleValue SCALE_X;
	public static final ForgeConfigSpec.DoubleValue SCALE_Y;
	public static final ForgeConfigSpec.DoubleValue SCALE_Z;
	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("Client configuration for Handcuffs Mod").push("handcuffs");
		ROTATE_X = builder.comment("Rotation on the X-axis").defineInRange("rotateX", -45.0, -180.0, 180.0);
		ROTATE_Y = builder.comment("Rotation on the Y-axis").defineInRange("rotateY", 0.0, -180.0, 180.0);
		ROTATE_Z = builder.comment("Rotation on the Z-axis").defineInRange("rotateZ", 0.0, -180.0, 180.0);
		TRANSLATE_X = builder.comment("Translation on the X-axis").defineInRange("translateX", 0.0, -1.0, 1.0);
		TRANSLATE_Y = builder.comment("Translation on the Y-axis").defineInRange("translateY", 0.4, -1.0, 1.0);
		TRANSLATE_Z = builder.comment("Translation on the Z-axis").defineInRange("translateZ", -0.1, -1.0, 1.0);
		SCALE_X = builder.comment("Scale factor on the X-axis").defineInRange("scaleX", 1.0, 0.1, 5.0);
		SCALE_Y = builder.comment("Scale factor on the Y-axis").defineInRange("scaleY", 1.0, 0.1, 5.0);
		SCALE_Z = builder.comment("Scale factor on the Z-axis").defineInRange("scaleZ", 1.0, 0.1, 5.0);
		builder.pop();
		CLIENT_CONFIG = builder.build();
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG, "handcuffs-client.toml");
	}
}
