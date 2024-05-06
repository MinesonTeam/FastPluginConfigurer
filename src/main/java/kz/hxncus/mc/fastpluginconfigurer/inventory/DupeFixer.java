package kz.hxncus.mc.fastpluginconfigurer.inventory;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.marker.InventoryItemMarker;
import lombok.Getter;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.logging.Logger;

@Getter
public class DupeFixer implements Listener {
    private final Logger logger = Logger.getLogger("FastPluginConfigurer");
    private final FastPluginConfigurer plugin;
    private final InventoryItemMarker inventoryItemMarker;
    public DupeFixer(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        this.inventoryItemMarker = plugin.getInventoryItemMarker();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                  PlayerInventory inventory = event.getPlayer().getInventory();
                  for (ItemStack itemStack : inventory) {
                      if (itemStack != null && inventoryItemMarker.isItemMarked(itemStack)) {
                          logger.info("Player logged in with a Custom Inventory item in their inventory. Removing it.");
                          inventory.remove(itemStack);
                      }
                  }
              }, 10L);
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        Item item = event.getItem();
        if (inventoryItemMarker.isItemMarked(item.getItemStack())) {
            logger.info("Someone picked up a Custom Inventory item. Removing it.");
            event.setCancelled(true);
            item.remove();
        }
    }

    @EventHandler
    public void onEntityDropItemEvent(EntityDropItemEvent event) {
        Item item = event.getItemDrop();
        if (inventoryItemMarker.isItemMarked(item.getItemStack())) {
            logger.info("Someone dropped a Custom Inventory item. Removing it.");
            event.setCancelled(true);
            item.remove();
        }
    }
}
