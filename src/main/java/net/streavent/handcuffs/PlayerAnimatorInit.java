
package net.streavent.handcuffs;

import net.streavent.handcuffs.client.HandcuffsLayer;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.Minecraft;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.IAnimation;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerAnimatorInit {
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new ResourceLocation("handcuffs", "player_animation"), 42, PlayerAnimatorInit::registerPlayerAnimation);
	}

	private static IAnimation registerPlayerAnimation(AbstractClientPlayerEntity player) {
		return new ModifierLayer<>();
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			for (PlayerRenderer renderer : mc.getRenderManager().getSkinMap().values()) {
				renderer.addLayer(new HandcuffsLayer(renderer));
			}
		});
	}
}
