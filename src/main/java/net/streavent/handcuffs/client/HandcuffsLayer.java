package net.streavent.handcuffs.client;

import net.streavent.handcuffs.init.HandcuffsModItems;
import net.streavent.handcuffs.HandcuffsAttributes;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.matrix.MatrixStack;

@OnlyIn(Dist.CLIENT)
public class HandcuffsLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	private final ItemStack handcuffStack;

	public HandcuffsLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer) {
		super(entityRenderer);
		this.handcuffStack = new ItemStack(HandcuffsModItems.HANDCUFFS.get());
	}

	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ModifiableAttributeInstance handcuffedAttribute = player.getAttribute(HandcuffsAttributes.HANDCUFFED.get());
		if (handcuffedAttribute != null && handcuffedAttribute.getValue() == 1.0) {
			matrixStack.push();
			// Define default values
			float rotateX = -90.0f;
			float rotateY = 0.0f;
			float rotateZ = 180.0f;
			float translateX = 0.085f;
			float translateY = 0.71f;
			float translateZ = -0.15f;
			float scale = 1.0f;
			// Adjust values if the player is crouching
			if (player.isCrouching()) {
				rotateX = -120.0f;
				rotateY = 0.0f;
				rotateZ = 180.0f;
				translateX = 0.085f;
				translateY = 0.55f;
				translateZ = -0.5f;
				scale = 1.0f;
			}
			// Apply transformations
			getEntityModel().bipedBody.translateRotate(matrixStack);
			matrixStack.translate(translateX, translateY, translateZ);
			matrixStack.scale(scale, scale, scale);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(rotateY));
			matrixStack.rotate(Vector3f.XP.rotationDegrees(rotateX));
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(rotateZ));
			// Render the item
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderItem(handcuffStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, packedLight, 0xF000F0, matrixStack, buffer);
			matrixStack.pop();
		}
	}
}
