package kz.hxncus.mc.fastpluginconfigurer.inventory.marker;

import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PDCItemMarker implements ItemMarker {
    private final NamespacedKey pdcKey;

    public PDCItemMarker(Plugin plugin) {
        pdcKey = new NamespacedKey(plugin, "pdcKey");
    }

    @NonNull
    public ItemStack markItem(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack).meta(meta -> meta.getPersistentDataContainer().set(pdcKey, PersistentDataType.BYTE, (byte) 1)).build();
    }

    @NonNull
    public ItemStack unmarkItem(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack).meta(meta -> meta.getPersistentDataContainer().remove(pdcKey)).build();
    }

    public boolean isItemMarked(@NonNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.getPersistentDataContainer().has(pdcKey, PersistentDataType.BYTE);
    }
}
