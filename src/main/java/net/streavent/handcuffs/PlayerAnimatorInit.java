
package net.streavent.handcuffs;

import net.streavent.handcuffs.client.HandcuffsLayer;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.Minecraft;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.IAnimation;

@Mod.EventBusSubscriber(modid = "handcuffs", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlayerAnimatorInit {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new ResourceLocation("handcuffs", "player_animation"), 42, PlayerAnimatorInit::registerPlayerAnimation);
			Minecraft mc = Minecraft.getInstance();
			for (PlayerRenderer renderer : mc.getRenderManager().getSkinMap().values()) {
				renderer.addLayer(new HandcuffsLayer(renderer));
			}
		});
	}

	private static IAnimation registerPlayerAnimation(AbstractClientPlayerEntity player) {
		return new ModifierLayer<>();
	}
}
