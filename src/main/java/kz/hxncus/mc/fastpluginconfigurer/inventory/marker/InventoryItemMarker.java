package kz.hxncus.mc.fastpluginconfigurer.inventory.marker;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryItemMarker implements ItemMarker {
    private final FastPluginConfigurer instance;
    public InventoryItemMarker(FastPluginConfigurer instance) {
        this.instance = instance;
    }

    @NonNull
    public ItemStack markItem(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack).meta(meta -> meta.getPersistentDataContainer().set(instance.getMarkKey(), PersistentDataType.BYTE, (byte) 1)).build();
    }

    @NonNull
    public ItemStack unmarkItem(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack).meta(meta -> meta.getPersistentDataContainer().remove(instance.getMarkKey())).build();
    }

    public boolean isItemMarked(@NonNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(instance.getMarkKey(), PersistentDataType.BYTE);
    }
}
