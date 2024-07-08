package kz.hxncus.mc.fastpluginconfigurer.inventory.marker;

import org.bukkit.inventory.ItemStack;

public class UnavailableItemMarker implements ItemMarker {

    @Override
    public ItemStack markItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public ItemStack unmarkItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public boolean isItemMarked(ItemStack itemStack) {
        return false;
    }
}
