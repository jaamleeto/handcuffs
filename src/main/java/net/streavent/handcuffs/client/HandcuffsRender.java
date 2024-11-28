
package net.streavent.handcuffs.client;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import net.streavent.handcuffs.item.HandcuffsItem;

public class HandcuffsRender extends GeoItemRenderer<HandcuffsItem> {
	public HandcuffsRender() {
		super(new HandcuffsModel());
	}
}
