package kz.hxncus.mc.fastpluginconfigurer.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface FastInventory {
    void onInitialize();
    void onDrag(InventoryDragEvent event);
    void onClick(InventoryClickEvent event);
    void onClose(InventoryCloseEvent event);
    void onOpen(InventoryOpenEvent event);
    void handleDrag(InventoryDragEvent event);
    void handleClick(InventoryClickEvent event);
    boolean handleClose(InventoryCloseEvent event);
    void handleOpen(InventoryOpenEvent event);
    FastInventory addDragHandler(Consumer<InventoryDragEvent> dragHandler);
    FastInventory addClickHandler(Consumer<InventoryClickEvent> clickHandler);
    FastInventory addCloseHandler(Consumer<InventoryCloseEvent> closeHandler);
    FastInventory addOpenHandler(Consumer<InventoryOpenEvent> openHandler);
    void registerInventory();
    void unregisterInventory();
    void open(HumanEntity entity);
    int addItem(ItemStack item);
    int addItem(ItemStack item, Consumer<InventoryClickEvent> handler);
    int setItem(int slot, ItemStack item);
    int setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler);
    int firstEmpty();
    ItemStack getItem(int slot);
}
