
package net.streavent.handcuffs;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.entity.LivingEntity;

@Mod.EventBusSubscriber(modid = "handcuffs", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HandcuffRenderer {
	@SubscribeEvent
	public static void onPostChainRenderLiving(RenderLivingEvent.Post event) {
		LivingEntity entity = event.getEntity();
		RenderChain.render(entity, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
	}
}
