package kz.hxncus.mc.fastpluginconfigurer.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
    UsualFastInventory addDragHandler(Consumer<InventoryDragEvent> dragHandler);
    UsualFastInventory addClickHandler(Consumer<InventoryClickEvent> clickHandler);
    UsualFastInventory addCloseHandler(Consumer<InventoryCloseEvent> closeHandler);
    UsualFastInventory addOpenHandler(Consumer<InventoryOpenEvent> openHandler);
    void registerInventory();
    void unregisterInventory();
    void open(HumanEntity entity);
    void open(Player player);
    FastInventory addItem(ItemStack item);
    FastInventory addItem(ItemStack item, Consumer<InventoryClickEvent> handler);
    FastInventory setItem(int slot, ItemStack item);
    FastInventory setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler);
    void setMarking(boolean mark);
    int firstEmpty();
    ItemStack getItem(int slot);
}
