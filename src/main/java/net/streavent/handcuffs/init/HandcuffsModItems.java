
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.streavent.handcuffs.init;

import net.streavent.handcuffs.item.KeyItem;
import net.streavent.handcuffs.item.HandcuffsItem;
import net.streavent.handcuffs.HandcuffsMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.item.Item;

public class HandcuffsModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffsMod.MODID);
	public static final RegistryObject<Item> HANDCUFFS = REGISTRY.register("handcuffs", () -> new HandcuffsItem());
	public static final RegistryObject<Item> KEY = REGISTRY.register("key", () -> new KeyItem());
	public static final RegistryObject<Item> HANDPOINT_SPAWN_EGG = REGISTRY.register("handpoint_spawn_egg", () -> new ForgeSpawnEggItem(HandcuffsModEntities.HANDPOINT, -1, -1, new Item.Properties().group(null)));
	// Start of user code block custom items
	// End of user code block custom items
}
