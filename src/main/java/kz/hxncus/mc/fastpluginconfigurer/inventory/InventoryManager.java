package kz.hxncus.mc.fastpluginconfigurer.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager implements Listener {
    private final Map<Inventory, FastInventory> inventories = new HashMap<>();

    public void register(Inventory inventory, FastInventory handler) {
        inventories.put(inventory, handler);
    }

    public void unregister(Inventory inventory) {
        inventories.remove(inventory);
    }

    @EventHandler
    public void handleDrag(InventoryDragEvent event) {
        FastInventory handler = inventories.get(event.getInventory());
        if (handler != null) {
            handler.handleDrag(event);
        }
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        FastInventory handler = inventories.get(event.getInventory());
        if (handler != null) {
            handler.handleClick(event);
        }
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        FastInventory handler = inventories.get(event.getInventory());
        if (handler != null && handler.handleClose(event)) {
            handler.open(event.getPlayer());
        }
    }

    @EventHandler
    public void handleOpen(InventoryOpenEvent event) {
        FastInventory handler = inventories.get(event.getInventory());
        if (handler != null) {
            handler.handleOpen(event);
        }
    }

    public void closeAll() {
        inventories.keySet().forEach(inventory -> inventory.getViewers().forEach(HumanEntity::closeInventory));
    }
}
