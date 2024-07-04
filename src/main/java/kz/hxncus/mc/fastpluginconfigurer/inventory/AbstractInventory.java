package kz.hxncus.mc.fastpluginconfigurer.inventory;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@EqualsAndHashCode
public abstract class AbstractInventory implements IInventory {
    private final FastPluginConfigurer plugin = FastPluginConfigurer.get();
    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> itemClickHandlers = new ConcurrentHashMap<>();
    private final List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private Predicate<Player> closeFilter;
    @Setter
    private boolean marking = true;

    protected AbstractInventory(InventoryType type) {
        this(Bukkit.createInventory(null, type));
    }

    protected AbstractInventory(InventoryType type, String title) {
        this(Bukkit.createInventory(null, type, title));
    }

    protected AbstractInventory(int size) {
        this(Bukkit.createInventory(null, size));
    }

    protected AbstractInventory(int size, String title) {
        this(Bukkit.createInventory(null, size, title));
    }

    protected AbstractInventory(@NonNull Inventory inventory) {
        this.inventory = inventory;
        plugin.getInventoryManager().register(inventory, this);
        Bukkit.getScheduler().runTaskLater(plugin, this::onInitialize, 1L);
    }

    public void openInventory(HumanEntity entity) {
        entity.openInventory(inventory);
    }

    public int addItem(ItemStack item) {
        int slot = this.inventory.firstEmpty();
        if (slot != -1) {
            setItem(slot, item);
        }
        return slot;
    }

    public int addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItem(inventory.firstEmpty(), handler);
        return addItem(item);
    }

    public ItemStack @NonNull[] getItems(int slotFrom, int slotTo) {
        if (slotFrom >= slotTo) {
            throw new IllegalArgumentException("slotFrom must be less than slotTo");
        }
        ItemStack[] items = new ItemStack[slotTo - slotFrom + 1];
        for (int i = slotFrom; i <= slotTo; i++) {
            items[i - slotFrom] = inventory.getItem(i);
        }
        return items;
    }

    public ItemStack @NonNull[] getItems(int @NonNull... slots) {
        ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = inventory.getItem(slots[i]);
        }
        return items;
    }

    public void setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, marking ? plugin.getInventoryManager().getItemMarker().markItem(item) : item);
    }

    public void setItem(int slot, Consumer<InventoryClickEvent> handler) {
        if (handler != null) {
            this.itemClickHandlers.put(slot, handler);
        } else {
            this.itemClickHandlers.remove(slot);
        }
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItem(slot, handler);
        setItem(slot, item);
    }

    @NonNull
    public IInventory setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
        return this;
    }

    @NonNull
    public IInventory setItems(int slotFrom, int slotTo, ItemStack item) {
        return setItems(slotFrom, slotTo, item, null);
    }

    @NonNull
    public IInventory setItems(ItemStack item, Consumer<InventoryClickEvent> handler, int @NonNull... slots) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
        return this;
    }

    @NonNull
    public IInventory setItems(ItemStack item, int @NonNull... slots) {
        return setItems(item, null, slots);
    }

    @NonNull
    public IInventory removeItems(int @NonNull... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
        return this;
    }

    @NonNull
    public IInventory removeItems(int slotFrom, int slotTo) {
        for (int i = slotFrom; i <= slotTo; i++) {
            removeItem(i);
        }
        return this;
    }

    @NonNull
    public IInventory removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemClickHandlers.remove(slot);
        return this;
    }

    @NonNull
    public IInventory setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
        return this;
    }

    @Override
    public IInventory addDragHandler(Consumer<InventoryDragEvent> dragHandler) {
        dragHandlers.add(dragHandler);
        return this;
    }

    @NonNull
    public IInventory addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        openHandlers.add(openHandler);
        return this;
    }

    @NonNull
    public IInventory addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        closeHandlers.add(closeHandler);
        return this;
    }

    @NonNull
    public IInventory addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        clickHandlers.add(clickHandler);
        return this;
    }

    public void handleDrag(InventoryDragEvent event) {
        onDrag(event);
        dragHandlers.forEach(handler -> handler.accept(event));
    }

    public void handleOpen(InventoryOpenEvent event) {
        onOpen(event);
        openHandlers.forEach(handler -> handler.accept(event));
    }

    public boolean handleClose(InventoryCloseEvent event) {
        onClose(event);
        closeHandlers.forEach(handler -> handler.accept(event));
        return this.closeFilter != null && this.closeFilter.test((Player) event.getPlayer());
    }

    public void handleClick(InventoryClickEvent event) {
        onClick(event);
        clickHandlers.forEach(handler -> handler.accept(event));
        Consumer<InventoryClickEvent> clickConsumer = itemClickHandlers.get(event.getRawSlot());
        if (clickConsumer != null) {
            clickConsumer.accept(event);
        }
    }
}
