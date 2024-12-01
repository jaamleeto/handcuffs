package net.streavent.handcuffs.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.LightType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;

import java.util.UUID;
import java.util.Comparator;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends LivingRenderer {
	protected PlayerRendererMixin(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
		super(rendererManager, entityModelIn, shadowSizeIn);
	}

	private boolean applyLayer = false;

	private static void renderSide(IVertexBuilder bufferIn, Matrix4f matrixIn, float p_229119_2_, float p_229119_3_, float p_229119_4_, int blockLight, int holderBlockLight, int skyLight, int holderSkyLight, float p_229119_9_, float p_229119_10_,
			float p_229119_11_, float p_229119_12_) {
		int i = 24;
		for (int j = 0; j < 24; ++j) {
			float f = (float) j / 23.0F;
			int k = (int) MathHelper.lerp(f, (float) blockLight, (float) holderBlockLight);
			int l = (int) MathHelper.lerp(f, (float) skyLight, (float) holderSkyLight);
			int i1 = LightTexture.packLight(k, l);
			addVertexPair(bufferIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
			addVertexPair(bufferIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
		}
	}

	private static void addVertexPair(IVertexBuilder bufferIn, Matrix4f matrixIn, int packedLight, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_,
			float p_229120_11_, float p_229120_12_) {
		float f = 0.3F;
		float f1 = 0.3F;
		float f2 = 0.3F;
		if (p_229120_9_ % 2 == 0) {
			f *= 0.7F;
			f1 *= 0.7F;
			f2 *= 0.7F;
		}
		float f3 = (float) p_229120_9_ / (float) p_229120_8_;
		float f4 = p_229120_3_ * f3;
		float f5 = p_229120_4_ > 0.0F ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0F - f3) * (1.0F - f3);
		float f6 = p_229120_5_ * f3;
		if (!p_229120_10_) {
			bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).lightmap(packedLight).endVertex();
		}
		bufferIn.pos(matrixIn, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(f, f1, f2, 1.0F).lightmap(packedLight).endVertex();
		if (p_229120_10_) {
			bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).lightmap(packedLight).endVertex();
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void render(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
		PlayerEntity nearestEntity = getNearestPlayer(entityIn);
		if (nearestEntity != null) {
			String combinedNames = entityIn.getName().getString() + nearestEntity.getName().getString();
			UUID sharedModifierUUID = UUID.nameUUIDFromBytes(combinedNames.getBytes());
			if (hasSharedModifier(entityIn, sharedModifierUUID) && hasSharedModifier(nearestEntity, sharedModifierUUID)) {
				matrixStackIn.push();
				Vector3d vector3d = nearestEntity.getLeashPosition(partialTicks);
				vector3d.add(0, 0, nearestEntity.getWidth() / 2);
				double d0 = (double) (MathHelper.lerp(partialTicks, entityIn.renderYawOffset, entityIn.prevRenderYawOffset) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
				Vector3d vector3d1 = new Vector3d(0.0D, (double) entityIn.getEyeHeight() * 0.5D, (double) entityIn.getWidth() / 2);
				double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
				double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
				double d3 = MathHelper.lerp((double) partialTicks, entityIn.prevPosX, entityIn.getPosX()) + d1;
				double d4 = MathHelper.lerp((double) partialTicks, entityIn.prevPosY, entityIn.getPosY()) + vector3d1.y;
				double d5 = MathHelper.lerp((double) partialTicks, entityIn.prevPosZ, entityIn.getPosZ()) + d2;
				matrixStackIn.translate(d1, vector3d1.y, d2);
				float f = (float) (vector3d.x - d3);
				float f1 = (float) (vector3d.y - d4);
				float f2 = (float) (vector3d.z - d5);
				float f3 = 0.025F;
				IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLeash());
				Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
				float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
				float f5 = f2 * f4;
				float f6 = f * f4;
				BlockPos blockpos = new BlockPos(entityIn.getEyePosition(partialTicks));
				BlockPos blockpos1 = new BlockPos(nearestEntity.getEyePosition(partialTicks));
				int i = this.getBlockLight(entityIn, blockpos);
				int j = i;
				int k = entityIn.world.getLightFor(LightType.SKY, blockpos);
				int l = entityIn.world.getLightFor(LightType.SKY, blockpos1);
				renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6);
				renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6);
				matrixStackIn.pop();
			}
		}
	}

	private PlayerEntity getNearestPlayer(AbstractClientPlayerEntity entityIn) {
		return entityIn.world.getEntitiesWithinAABB(PlayerEntity.class, entityIn.getBoundingBox().grow(50), e -> e != entityIn).stream().min(Comparator.comparingDouble(e -> e.getDistance(entityIn))).orElse(null);
	}

	private boolean hasSharedModifier(LivingEntity entity, UUID sharedModifierUUID) {
		AttributeModifierManager manager = ((PlayerEntity) entity).getAttributeManager();
		return manager.hasModifier(Attributes.MOVEMENT_SPEED, sharedModifierUUID);
	}
}
