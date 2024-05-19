package kz.hxncus.mc.fastpluginconfigurer.inventory.dupefixer;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.marker.InventoryItemMarker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
public class DupeFixer implements Listener {
    private final FastPluginConfigurer plugin;
    private final InventoryItemMarker inventoryItemMarker;
    public DupeFixer(FastPluginConfigurer plugin, InventoryManager inventoryManager) {
        this.plugin = plugin;
        this.inventoryItemMarker = inventoryManager.getItemMarker();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PlayerInventory inventory = event.getPlayer().getInventory();
            for (ItemStack itemStack : inventory) {
                if (itemStack == null || inventoryItemMarker.isItemMarked(itemStack)) {
                    continue;
                }
                inventory.remove(itemStack);
                plugin.getLogger().info("Player logged in with a Custom Inventory item in their inventory. Removing it.");
            }
        }, 10L);
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        Item item = event.getItem();
        if (inventoryItemMarker.isItemMarked(item.getItemStack())) {
            item.remove();
            event.setCancelled(true);
            plugin.getLogger().info("Someone picked up a Custom Inventory item. Removing it.");
        }
    }

    @EventHandler
    public void onEntityDropItemEvent(EntityDropItemEvent event) {
        Item item = event.getItemDrop();
        if (inventoryItemMarker.isItemMarked(item.getItemStack())) {
            plugin.getLogger().info("Someone dropped a Custom Inventory item. Removing it.");
            event.setCancelled(true);
            item.remove();
        }
    }
}
