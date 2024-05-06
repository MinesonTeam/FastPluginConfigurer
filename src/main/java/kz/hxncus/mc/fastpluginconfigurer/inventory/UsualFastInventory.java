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
public class UsualFastInventory implements FastInventory {
    private final FastPluginConfigurer instance = FastPluginConfigurer.getInstance();
    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> itemClickHandlers = new HashMap<>();
    private final List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private Predicate<Player> closeFilter;
    @Setter
    private boolean marking = true;

    public UsualFastInventory(InventoryType type) {
        this(Bukkit.createInventory(null, type));
    }

    public UsualFastInventory(InventoryType type, String title) {
        this(Bukkit.createInventory(null, type, title));
    }

    public UsualFastInventory(int size) {
        this(Bukkit.createInventory(null, size));
    }

    public UsualFastInventory(int size, String title) {
        this(Bukkit.createInventory(null, size, title));
    }

    public UsualFastInventory(@NonNull Inventory inventory) {
        this.inventory = inventory;
        registerInventory();
        Bukkit.getScheduler().runTaskLater(instance, this::onInitialize, 1L);
    }

    public void registerInventory() {
        instance.getInventoryManager().register(inventory, this);
    }

    public void unregisterInventory() {
        instance.getInventoryManager().unregister(inventory);
    }

    @Override
    public void onInitialize() {
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
    }

    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    @NonNull
    public UsualFastInventory addItem(ItemStack item) {
        int slot = this.inventory.firstEmpty();
        if (slot != -1) {
            return setItem(slot, item);
        }
        return this;
    }

    @NonNull
    public UsualFastInventory addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
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
        if (slotFrom == slotTo || slotFrom > slotTo) {
            throw new IllegalArgumentException("slotFrom must be less than slotTo");
        }
        ItemStack[] items = new ItemStack[slotTo - slotFrom + 1]; // 20
        for (int i = slotFrom ; i <= slotTo; i++) { // 0; i <= 20
            items[i - slotFrom] = this.inventory.getItem(i);
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
    public UsualFastInventory setItemIf(int slot, ItemStack item, Predicate<UsualFastInventory> predicate) {
        if (predicate.test(this)) {
            return setItem(slot, item);
        }
        return this;
    }

    @NonNull
    public UsualFastInventory setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, marking ? instance.getInventoryItemMarker().markItem(item) : item);
        return this;
    }

    @NonNull
    public UsualFastInventory setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItemClickHandler(slot, handler);
        return setItem(slot, item);
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
    public UsualFastInventory setItems(int slotFrom, int slotTo, ItemStack item) {
        return setItems(slotFrom, slotTo, item, null);
    }

    @NonNull
    public UsualFastInventory setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
        return this;
    }
    @NonNull
    public UsualFastInventory setItems(ItemStack item, int @NonNull ... slots) {
        return setItems(item, null, slots);
    }

    @NonNull
    public UsualFastInventory setItems(ItemStack item, Consumer<InventoryClickEvent> handler, int @NonNull ... slots) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
        return this;
    }

    @NonNull
    public UsualFastInventory removeItems(int @NonNull ... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
        return this;
    }

    @NonNull
    public UsualFastInventory removeItems(int slotFrom, int slotTo) {
        for (int i = slotFrom; i <= slotTo; i++) {
            removeItem(i);
        }
        return this;
    }

    @NonNull
    public UsualFastInventory removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemClickHandlers.remove(slot);
        return this;
    }

    @NonNull
    public UsualFastInventory clear() {
        this.inventory.clear();
        return this;
    }

    @NonNull
    public UsualFastInventory setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
        return this;
    }

    @Override
    public UsualFastInventory addDragHandler(Consumer<InventoryDragEvent> dragHandler) {
        dragHandlers.add(dragHandler);
        return this;
    }

    @NonNull
    public UsualFastInventory addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        openHandlers.add(openHandler);
        return this;
    }

    @NonNull
    public UsualFastInventory addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        closeHandlers.add(closeHandler);
        return this;
    }

    @NonNull
    public UsualFastInventory addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        clickHandlers.add(clickHandler);
        return this;
    }

    public void open(HumanEntity entity) {
        entity.openInventory(inventory);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @NonNull
    public Inventory getInventory() {
        return this.inventory;
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
