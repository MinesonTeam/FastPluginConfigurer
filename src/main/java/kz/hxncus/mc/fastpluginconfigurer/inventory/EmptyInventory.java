package kz.hxncus.mc.fastpluginconfigurer.inventory;

import lombok.NonNull;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class EmptyInventory extends AbstractInventory {
    public EmptyInventory(InventoryType type) {
        super(type);
    }

    public EmptyInventory(InventoryType type, String title) {
        super(type, title);
    }

    public EmptyInventory(int size) {
        super(size);
    }

    public EmptyInventory(int size, String title) {
        super(size, title);
    }

    public EmptyInventory(@NonNull Inventory inventory) {
        super(inventory);
    }

    @Override
    public void onInitialize() {
        // Empty inventory
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        // Empty inventory
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        // Empty inventory
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // Empty inventory
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        // Empty inventory
    }
}
