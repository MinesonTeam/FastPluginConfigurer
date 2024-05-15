package kz.hxncus.mc.fastpluginconfigurer.inventory;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public class BasicFastInventory implements FastInventory {
    private final FastPluginConfigurer plugin;
    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> itemClickHandlers = new HashMap<>();
    private final List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private Predicate<Player> closeFilter;
    @Setter
    private boolean marking = true;

    public BasicFastInventory(FastPluginConfigurer plugin, InventoryType type) {
        this(plugin, Bukkit.createInventory(null, type));
    }

    public BasicFastInventory(FastPluginConfigurer plugin, InventoryType type, String title) {
        this(plugin, Bukkit.createInventory(null, type, title));
    }

    public BasicFastInventory(FastPluginConfigurer plugin, int size) {
        this(plugin, Bukkit.createInventory(null, size));
    }

    public BasicFastInventory(FastPluginConfigurer plugin, int size, String title) {
        this(plugin, Bukkit.createInventory(null, size, title));
    }

    public BasicFastInventory(FastPluginConfigurer plugin, @NonNull Inventory inventory) {
        this.plugin = plugin;
        this.inventory = inventory;
        plugin.getInventoryManager().register(inventory, this);
        Bukkit.getScheduler().runTaskLater(plugin, this::onInitialize, 1L);
    }

    public void unregisterInventory() {
        plugin.getInventoryManager().unregister(inventory);
    }

    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    @NonNull
    public int addItem(ItemStack item) {
        int slot = this.inventory.firstEmpty();
        if (slot != -1) {
            return setItem(slot, item);
        }
        return slot;
    }

    @NonNull
    public int addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItemClickHandler(inventory.firstEmpty(), handler);
        return addItem(item);
    }

    public ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }

    public ItemStack @NonNull [] getContents() {
        return this.inventory.getContents();
    }

    public int getSize() {
        return this.inventory.getSize();
    }

    public ItemStack @NonNull [] getItems(int slotFrom, int slotTo) {
        if (slotFrom >= slotTo) {
            throw new IllegalArgumentException("slotFrom must be less than slotTo");
        }
        ItemStack[] items = new ItemStack[slotTo - slotFrom + 1]; // 20
        for (int i = slotFrom ; i <= slotTo; i++) { // 0; i <= 20
            items[i - slotFrom] = getItem(i);
        }
        return items;
    }

    public ItemStack @NonNull [] getItems(int @NonNull... slots) {
        ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = getItem(slots[i]);
        }
        return items;
    }

    @NonNull
    public int setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, marking ? plugin.getItemMarker().markItem(item) : item);
        return slot;
    }

    @NonNull
    public void setItemClickHandler(int slot, Consumer<InventoryClickEvent> handler) {
        if (handler != null) {
            this.itemClickHandlers.put(slot, handler);
        } else {
            this.itemClickHandlers.remove(slot);
        }
    }

    @NonNull
    public BasicFastInventory setItems(int slotFrom, int slotTo, ItemStack item) {
        return setItems(slotFrom, slotTo, item, null);
    }

    @NonNull
    public BasicFastInventory setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
        return this;
    }

    @NonNull
    public BasicFastInventory setItems(ItemStack item, int @NonNull ... slots) {
        return setItems(item, null, slots);
    }

    @NonNull
    public BasicFastInventory setItems(ItemStack item, Consumer<InventoryClickEvent> handler, int @NonNull ... slots) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
        return this;
    }

    @NonNull
    public int setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItemClickHandler(slot, handler);
        return setItem(slot, item);
    }

    @NonNull
    public BasicFastInventory removeItems(int @NonNull ... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
        return this;
    }

    @NonNull
    public BasicFastInventory removeItems(int slotFrom, int slotTo) {
        for (int i = slotFrom; i <= slotTo; i++) {
            removeItem(i);
        }
        return this;
    }

    @NonNull
    public BasicFastInventory removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemClickHandlers.remove(slot);
        return this;
    }

    @NonNull
    public BasicFastInventory clear() {
        this.inventory.clear();
        return this;
    }

    @NonNull
    public BasicFastInventory setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
        return this;
    }

    @Override
    public BasicFastInventory addDragHandler(Consumer<InventoryDragEvent> dragHandler) {
        dragHandlers.add(dragHandler);
        return this;
    }

    @NonNull
    public BasicFastInventory addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        openHandlers.add(openHandler);
        return this;
    }

    @NonNull
    public BasicFastInventory addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        closeHandlers.add(closeHandler);
        return this;
    }

    @NonNull
    public BasicFastInventory addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        clickHandlers.add(clickHandler);
        return this;
    }

    public void open(HumanEntity entity) {
        entity.openInventory(inventory);
    }

    @NonNull
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void onInitialize() {
        // Basic inventory initialization
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
        // Basic inventory drag
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        // Basic inventory click
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // Basic inventory close
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        // Basic inventory open
    }

    public void handleDrag(InventoryDragEvent event) {
        onDrag(event);
        this.dragHandlers.forEach(drag -> drag.accept(event));
    }

    public void handleOpen(InventoryOpenEvent event) {
        onOpen(event);
        this.openHandlers.forEach(open -> open.accept(event));
    }

    public boolean handleClose(InventoryCloseEvent event) {
        onClose(event);
        this.closeHandlers.forEach(close -> close.accept(event));
        return this.closeFilter != null && this.closeFilter.test((Player) event.getPlayer());
    }

    public void handleClick(InventoryClickEvent event) {
        onClick(event);

        this.clickHandlers.forEach(click -> click.accept(event));

        Consumer<InventoryClickEvent> clickConsumer = this.itemClickHandlers.get(event.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(event);
        }
    }
}
