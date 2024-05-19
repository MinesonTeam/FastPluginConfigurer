package kz.hxncus.mc.fastpluginconfigurer.inventory.dupefixer.marker;

import org.bukkit.inventory.ItemStack;

public interface ItemMarker {
    ItemStack markItem(ItemStack paramItemStack);

    ItemStack unmarkItem(ItemStack paramItemStack);

    boolean isItemMarked(ItemStack paramItemStack);
}
