package net.streavent.handcuffs.client;

import net.streavent.handcuffs.init.HandcuffsModItems;
import net.streavent.handcuffs.config.HandcuffsClientConfig;
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
			// Read values from the config
			double rotateX = HandcuffsClientConfig.ROTATE_X.get().floatValue();
			double rotateY = HandcuffsClientConfig.ROTATE_Y.get().floatValue();
			double rotateZ = HandcuffsClientConfig.ROTATE_Z.get().floatValue();
			double translateX = HandcuffsClientConfig.TRANSLATE_X.get().floatValue();
			double translateY = HandcuffsClientConfig.TRANSLATE_Y.get().floatValue();
			double translateZ = HandcuffsClientConfig.TRANSLATE_Z.get().floatValue();
			float scaleX = HandcuffsClientConfig.SCALE_X.get().floatValue();
			float scaleY = HandcuffsClientConfig.SCALE_Y.get().floatValue();
			float scaleZ = HandcuffsClientConfig.SCALE_Z.get().floatValue();
			// Apply transformations
			getEntityModel().bipedBody.translateRotate(matrixStack);
			matrixStack.translate(translateX, translateY, translateZ);
			matrixStack.scale(scaleX, scaleY, scaleZ);
			matrixStack.rotate(Vector3f.YP.rotationDegrees((float) rotateY));
			matrixStack.rotate(Vector3f.XP.rotationDegrees((float) rotateX));
			matrixStack.rotate(Vector3f.ZP.rotationDegrees((float) rotateZ));
			// Render the item
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderItem(handcuffStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, packedLight, 0xF000F0, matrixStack, buffer);
			matrixStack.pop();
		}
	}
}
