package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtils;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.lib.core.bukkit.gui.object.BukkitItem;
import me.hsgamer.bettergui.lib.core.minecraft.gui.button.Button;
import me.hsgamer.bettergui.lib.core.minecraft.gui.object.Item;
import me.hsgamer.bettergui.lib.core.minecraft.gui.simple.SimpleButtonMap;
import me.hsgamer.bettergui.menu.SimpleMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BetterGUIHook implements Convertible {
    private final FastPluginConfigurer plugin;

    public BetterGUIHook(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        Menu menu = BetterGUI.getInstance().getMenuManager().getMenu(fileName);
        if (!(menu instanceof SimpleMenu)) {
            player.sendMessage("Menu not found: " + fileName);
            return;
        }
        storeConfigItemsInInventory(player, ((Chest) state).getInventory(), ((SimpleMenu) menu).getButtonMap());
    }

    private void storeConfigItemsInInventory(Player player, Inventory chestInventory, SimpleButtonMap buttonMap) {
        chestInventory.clear();
        for (Map.Entry<Button, Collection<Integer>> entry : buttonMap.getButtonSlotMap().entrySet()) {
            Item item = entry.getKey().getItem(player.getUniqueId());
            if (!(item instanceof BukkitItem)) {
                continue;
            }
            ItemStack itemStack = ((BukkitItem) item).getItemStack();
            for (int i : entry.getValue()) {
                chestInventory.setItem(i, itemStack);
            }

        }
        player.openInventory(chestInventory);
        player.sendMessage("Successfully stored all items to the chest.");
    }

    @Override
    public void inventoryToFile(Player player, String fileName) {
        File file = new File(plugin.getConverterDirectory(), fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        if (file.exists()) {
            player.sendMessage("File is already exists: " + fileName);
            return;
        }
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        Inventory chestInventory = ((Chest) state).getInventory();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configureInventory(fileName, config, chestInventory);
        storeInventoryInConfig(chestInventory, config);
        FileUtils.reload(config, file);
        player.sendMessage("Chest inventory successfully saved into " + fileName);
    }

    private void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.command", fileName);
    }

    private void storeInventoryInConfig(Inventory chestInventory, FileConfiguration config) {
        int count = 0;
        for (int i = 0; i < chestInventory.getSize(); i++) {
            ItemStack item = chestInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            setItemToConfig(item, config, count++, i);
        }
    }

    private void setItemToConfig(ItemStack item, FileConfiguration config, int count, int i) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                config.set(count + ".NAME", itemMeta.getDisplayName());
            }
            if (itemMeta.hasLore()) {
                config.set(count + ".LORE", itemMeta.getLore());
            }
            if (itemMeta.hasEnchants()) {
                config.set(count + ".ENCHANTMENT", itemMeta.getEnchants().entrySet().stream()
                                                           .map(entry -> entry.getKey().getKey().getKey() + ", " + entry.getValue())
                                                           .collect(Collectors.toList()));
            }
        }
        config.set(count + ".ID", item.getType().name() + (item.getDurability() != 0 ? "" : ":" + item.getDurability()));
        config.set(count + ".AMOUNT", item.getAmount());
        config.set(count + ".POSITION-X", i % 9 + 1);
        config.set(count + ".POSITION-Y", i / 9 + 1);
    }

    @Override
    public List<String> getAllFileNames() {
        return new ArrayList<>(BetterGUI.getInstance().getMenuManager().getMenuNames());
    }
}
