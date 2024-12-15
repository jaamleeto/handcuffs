
package net.streavent.handcuffs;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.RegistryObject;

import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.attributes.Attribute;

public class HandcuffsAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "handcuffs");
	public static final RegistryObject<Attribute> HANDCUFFED = ATTRIBUTES.register("handcuffed", () -> new RangedAttribute("attribute.name.handcuffs.handcuffed", 0.0D, 0.0D, 1.0D).setShouldWatch(true));
	public static final RegistryObject<Attribute> HANDCUFF_HOLDER = ATTRIBUTES.register("handcuff_holder", () -> new RangedAttribute("attribute.name.handcuffs.handcuff_holder", 0.0D, 0.0D, 1000.0D).setShouldWatch(true));

	public static void registerAttributes() {
		ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
