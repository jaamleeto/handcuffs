
package net.streavent.handcuffs.item;

import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.AnimationState;

import net.streavent.handcuffs.client.HandcuffsRender;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.World;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.Rarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.concurrent.Callable;
import java.util.Properties;
import java.util.List;

public class HandcuffsItem extends Item implements IAnimatable, ISyncable {
	private static final int ACTIVATE_ANIM_STATE = 0;
	private static final AnimationBuilder ACTIVATE_ANIM = new AnimationBuilder().addAnimation("misc.activate", false);
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public HandcuffsItem() {
		super(new Properties().group(ItemGroup.TOOLS).maxStackSize(1).rarity(Rarity.COMMON).setISTER(() -> getISTER()));
		GeckoLibNetwork.registerSyncable(this);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> getISTER() {
		return HandcuffsRender::new;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "Activation", 20, this::predicate));
	}

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("open", true));
		event.getController().setAnimation(new AnimationBuilder().addAnimation("close", true));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ACTIVATE_ANIM_STATE) {
			final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, "Activation");
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload();
				controller.setAnimation(ACTIVATE_ANIM);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		list.add(new TranslationTextComponent("item.handcuffs.tooltip"));
	}
}
