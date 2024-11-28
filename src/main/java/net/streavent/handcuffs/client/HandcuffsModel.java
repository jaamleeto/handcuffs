
package net.streavent.handcuffs.client;

import software.bernie.geckolib3.model.AnimatedGeoModel;

import net.streavent.handcuffs.item.HandcuffsItem;

import net.minecraft.util.ResourceLocation;

public class HandcuffsModel extends AnimatedGeoModel<HandcuffsItem> {
	@Override
	public ResourceLocation getModelLocation(HandcuffsItem object) {
		return new ResourceLocation("handcuffs", "geo/handcuffs.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(HandcuffsItem object) {
		return new ResourceLocation("handcuffs", "textures/item/handcuffs.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(HandcuffsItem animatable) {
		return new ResourceLocation("handcuffs", "animations/handcuffs.animation.json");
	}
}
