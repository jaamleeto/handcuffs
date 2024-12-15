package net.streavent.handcuffs.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.streavent.handcuffs.HandcuffsAttributes;

import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "registerAttributes", at = @At("RETURN"), cancellable = true)
	private static void injectLivingAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
		AttributeModifierMap.MutableAttribute attributes = cir.getReturnValue();
		attributes.createMutableAttribute(HandcuffsAttributes.HANDCUFFED.get(), 0.0D);
		attributes.createMutableAttribute(HandcuffsAttributes.HANDCUFF_HOLDER.get(), 0.0D);
		cir.setReturnValue(attributes);
	}
}
