package kz.hxncus.mc.fastpluginconfigurer.inventory.marker;

import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class InventoryItemMarker implements ItemMarker {
    private final Plugin plugin;
    private final NamespacedKey markKey;

    public InventoryItemMarker(Plugin plugin) {
        this.plugin = plugin;
        markKey = new NamespacedKey(this.plugin, "mark");
    }

    @NonNull
    public org.bukkit.inventory.ItemStack markItem(@NonNull org.bukkit.inventory.ItemStack itemStack) {
        return new ItemBuilder(itemStack).meta(meta -> meta.getPersistentDataContainer().set(markKey, PersistentDataType.BYTE, (byte) 1)).build();
    }

    @NonNull
    public org.bukkit.inventory.ItemStack unmarkItem(@NonNull org.bukkit.inventory.ItemStack itemStack) {
        return new ItemBuilder(itemStack).meta(meta -> meta.getPersistentDataContainer().remove(markKey)).build();
    }

    public boolean isItemMarked(@NonNull org.bukkit.inventory.ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(markKey, PersistentDataType.BYTE);
    }
}
