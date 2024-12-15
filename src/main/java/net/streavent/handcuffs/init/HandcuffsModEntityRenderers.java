/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.streavent.handcuffs.init;

import net.streavent.handcuffs.client.renderer.HandpointRenderer;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HandcuffsModEntityRenderers {
	@SubscribeEvent
	public static void render(FMLClientSetupEvent event) {
		HandcuffsModEntityRenderers.renders();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		HandcuffsModEntityRenderers.renders();
	}

	private static void renders() {
		RenderingRegistry.registerEntityRenderingHandler(HandcuffsModEntities.HANDPOINT.get(), HandpointRenderer::new);
	}
}
