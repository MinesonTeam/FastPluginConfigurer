package kz.hxncus.mc.fastpluginconfigurer.hook;

import com.extendedclip.deluxemenus.menu.Menu;
import com.extendedclip.deluxemenus.menu.MenuHolder;
import com.extendedclip.deluxemenus.menu.MenuItem;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DeluxeMenusHook implements Convertible {
    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        Menu menu = Menu.getMenu(fileName);
        if (menu == null) {
            player.sendMessage("Menu not found: " + fileName);
            return;
        }
        Inventory chestInventory = ((Chest) state).getInventory();
        chestInventory.clear();
        MenuHolder holder = new MenuHolder(player);
        for (Map.Entry<Integer, TreeMap<Integer, MenuItem>> entry : menu.getMenuItems().entrySet()) {
            for (MenuItem item : (entry.getValue()).values()) {
                chestInventory.setItem(item.getSlot(), item.getItemStack(holder));
            }
        }
        player.sendMessage("Successfully stored all items to chest.");
    }

    @Override
    public void inventoryToFile(Player player, String fileName) {
        File file = new File(FastPluginConfigurer.getInstance().getConverterDirectory(), fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        if (file.exists()) {
            player.sendMessage("File is already exists: " + fileName);
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        Inventory chestInventory = ((Chest) state).getInventory();
        config.set("menu_title", fileName);
        config.set("register_command", true);
        config.set("open_command", List.of(fileName));
        config.set("size", chestInventory.getSize());
        int count = 0;
        for (int i = 0; i < chestInventory.getSize(); i++) {
            ItemStack item = chestInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            String path = String.format("items.%s.", count);
            ItemMeta itemMeta = item.getItemMeta();
            config.set(path + "material", item.getType().name());
            if (item.getData().getData() != 0) {
                config.set(path + "data", item.getData().getData());
            }
            config.set(path + "amount", item.getAmount());
            if (itemMeta.hasDisplayName()) {
                config.set(path + "display_name", itemMeta.getDisplayName());
            }
            if (itemMeta.hasLore()) {
                config.set(path + "lore", itemMeta.getLore());
            }
            config.set(path + "slot", i);
            if (itemMeta.hasEnchants()) {
                config.set(path + "enchantments", itemMeta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ";" + entry.getValue()).collect(java.util.stream.Collectors.toList()));
            }
            count++;
        }
        try {
            config.save(file);
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            player.sendMessage("Error while trying to save the configuration");
            return;
        }
        player.sendMessage("Chest inventory successfully saved into " + fileName);
    }
}
