/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.streavent.handcuffs.init;

import net.streavent.handcuffs.entity.HandpointEntity;
import net.streavent.handcuffs.HandcuffsMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.Entity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HandcuffsModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, HandcuffsMod.MODID);
	public static final RegistryObject<EntityType<HandpointEntity>> HANDPOINT = register("handpoint", EntityType.Builder.<HandpointEntity>create(HandpointEntity::new, EntityClassification.CREATURE).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(HandpointEntity::new).immuneToFire().size(0.1f, 0.1f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			HandpointEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(HANDPOINT.get(), HandpointEntity.createAttributes().create());
	}
}
