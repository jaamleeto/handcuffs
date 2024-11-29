
package net.streavent.handcuffs;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.Minecraft;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.IAnimation;

@Mod.EventBusSubscriber(modid = "handcuffs", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HandcuffsClientHandler {
	private static Minecraft minecraft = Minecraft.getInstance();

	@SubscribeEvent
	public static void onPlayerTickClient(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.START || !event.player.world.isRemote || !(event.player instanceof AbstractClientPlayerEntity)) {
			return;
		}
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.player;
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (handcuffedAttribute == null || handcuffedAttribute.getValue() != 1.0) {
			resetAnimation(player);
			return;
		}
		ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(new ResourceLocation("handcuffs", "player_animation"));
		if (animation != null && animation.getAnimation() == null) {
			animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation("handcuffs", "handcuffsfront"))));
		}
	}

	private static void resetAnimation(AbstractClientPlayerEntity player) {
		ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(new ResourceLocation("handcuffs", "player_animation"));
		if (animation != null && animation.getAnimation() != null) {
			animation.setAnimation(null);
		}
	}
}
