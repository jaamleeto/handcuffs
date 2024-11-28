package net.streavent.handcuffs.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import com.mojang.blaze3d.matrix.MatrixStack;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
	protected PlayerRendererMixin(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
		super(rendererManager, entityModelIn, shadowSizeIn);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void onRender(T entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int lightIn, CallbackInfo ci) {
	}
}
