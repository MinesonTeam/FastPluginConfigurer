package kz.hxncus.mc.fastpluginconfigurer.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.function.Consumer;

public interface Handleable {
    void handleDrag(InventoryDragEvent event);
    void handleClick(InventoryClickEvent event);
    boolean handleClose(InventoryCloseEvent event);
    void handleOpen(InventoryOpenEvent event);
    Handleable addDragHandler(Consumer<InventoryDragEvent> dragHandler);
    Handleable addClickHandler(Consumer<InventoryClickEvent> clickHandler);
    Handleable addCloseHandler(Consumer<InventoryCloseEvent> closeHandler);
    Handleable addOpenHandler(Consumer<InventoryOpenEvent> openHandler);
}
