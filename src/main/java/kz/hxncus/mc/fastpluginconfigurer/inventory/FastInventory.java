package kz.hxncus.mc.fastpluginconfigurer.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface FastInventory extends Handleable {
    void onInitialize();
    void onDrag(InventoryDragEvent event);
    void onClick(InventoryClickEvent event);
    void onClose(InventoryCloseEvent event);
    void onOpen(InventoryOpenEvent event);
    int addItem(ItemStack item);
    int addItem(ItemStack item, Consumer<InventoryClickEvent> handler);
    void setItem(int slot, ItemStack item);
    void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler);
    Inventory getInventory();
}
