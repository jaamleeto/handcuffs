package net.streavent.handcuffs.client.renderer;

import net.streavent.handcuffs.entity.HandpointEntity;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.model.CodModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

import com.mojang.blaze3d.matrix.MatrixStack;

@OnlyIn(Dist.CLIENT)
public class HandpointRenderer extends MobRenderer<HandpointEntity, CodModel<HandpointEntity>> {
	public HandpointRenderer(EntityRendererManager context) {
		super(context, new CodModel(), 0.1f);
	}

	@Override
	protected void preRenderCallback(HandpointEntity entity, MatrixStack poseStack, float f) {
		poseStack.scale(0.1f, 0.1f, 0.1f);
	}

	@Override
	public ResourceLocation getEntityTexture(HandpointEntity entity) {
		return new ResourceLocation("handcuffs:textures/entities/placeholder_texture.png");
	}

	@Override
	protected boolean isVisible(HandpointEntity entity) {
		return false;
	}
}
