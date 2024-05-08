package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import me.filoghost.chestcommands.api.Icon;
import me.filoghost.chestcommands.inventory.Grid;
import me.filoghost.chestcommands.menu.InternalMenu;
import me.filoghost.chestcommands.menu.MenuManager;
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
import java.util.stream.Collectors;

public class ChestCommandsHook implements Convertible {
    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        InternalMenu menu = MenuManager.getMenuByFileName(fileName);
        if (menu == null) {
            player.sendMessage("Menu not found: " + fileName);
            return;
        }
        Inventory chestInventory = ((Chest) state).getInventory();
        chestInventory.clear();
        Grid<Icon> icons = menu.getIcons();
        for (int i = 0; i < icons.getRows(); i++) {
            for (int j = 0; j < icons.getColumns(); j++) {
                Icon icon = icons.get(i, j);
                if (icon == null) {
                    continue;
                }
                chestInventory.setItem(i * 9 + j, icon.render(player));
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
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.commands", List.of(fileName));
        int count = 0;
        for (int i = 0; i < chestInventory.getSize(); i++) {
            ItemStack item = chestInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            ItemMeta itemMeta = item.getItemMeta();
            config.set(count + ".ACTIONS", List.of(""));
            if (itemMeta.hasDisplayName()) {
                config.set(count + ".NAME", itemMeta.getDisplayName());
            }
            if (itemMeta.hasLore()) {
                config.set(count + ".LORE", itemMeta.getLore());
            }
            if (item.getDurability() != 0) {
                config.set(count + ".DURATION", item.getDurability());
            }
            if (itemMeta.hasEnchants()) {
                config.set(count + ".ENCHANTMENTS", itemMeta.getEnchants().entrySet().stream()
                      .map(entry -> entry.getKey().getName() + ", " + entry.getValue())
                      .collect(Collectors.toList()));
            }
            config.set(count + ".AMOUNT", item.getAmount());
            config.set(count + ".MATERIAL", item.getType().name());
            config.set(count + ".KEEP-OPEN", true);
            config.set(count + ".POSITION-X", i % 9 + 1);
            config.set(count + ".POSITION-Y", i / 9 + 1);
            count++;
        }
        try {
            config.save(file);
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }
        player.sendMessage("Chest inventory successfully saved into " + fileName);
    }
}
