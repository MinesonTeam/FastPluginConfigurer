package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtils;
import me.filoghost.chestcommands.api.Icon;
import me.filoghost.chestcommands.fcommons.collection.CaseInsensitiveString;
import me.filoghost.chestcommands.inventory.Grid;
import me.filoghost.chestcommands.menu.BaseMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ChestCommandsHook implements Convertible {
    public final FastPluginConfigurer plugin;

    public ChestCommandsHook(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state;
        if (targetBlock == null) {
            state = null;
        } else {
            state = targetBlock.getState();
        }
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        BaseMenu menu = MenuManager.getMenuByFileName(fileName);
        if (menu == null) {
            player.sendMessage("Menu not found: " + fileName);
        } else {
            storeConfigItemsInInventory(player, ((Chest) state).getInventory(), menu.getIcons());
        }
    }

    private void storeConfigItemsInInventory(Player player, Inventory chestInventory, Grid<Icon> icons) {
        chestInventory.clear();
        for (int i = 0; i < icons.getRows(); i++) {
            for (int j = 0; j < icons.getColumns(); j++) {
                Icon icon = icons.get(i, j);
                if (icon == null) {
                    continue;
                }
                chestInventory.setItem(i * 9 + j, icon.render(player));
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
        int count = 0;
        for (int i = 0; i < chestInventory.getSize(); i++) {
            ItemStack item = chestInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            storeItemInConfig(item, config, count++, i);
        }
        FileUtils.reload(config, file);
        player.sendMessage("Chest inventory successfully saved into " + fileName);
    }

    private void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.commands", List.of(fileName));
    }

    @Override
    public List<String> getAllFileNames() {
        return MenuManager.getMenuFileNames().stream().map(CaseInsensitiveString::toString).collect(Collectors.toList());
    }

    private void storeItemInConfig(ItemStack item, FileConfiguration config, int count, int index) {
        ItemMeta itemMeta = item.getItemMeta();
        config.set(count + ".ACTIONS", List.of(""));
        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                config.set(count + ".NAME", itemMeta.getDisplayName());
            } else {
                config.set(count + ".NAME", itemMeta.getLocalizedName());
            }
            if (itemMeta.hasLore()) {
                config.set(count + ".LORE", itemMeta.getLore());
            }
            if (itemMeta instanceof Damageable && ((Damageable) itemMeta).hasDamage()) {
                config.set(count + ".DURATION", ((Damageable) itemMeta).getDamage());
            }
            if (itemMeta.hasEnchants()) {
                config.set(count + ".ENCHANTMENTS", itemMeta.getEnchants()
                                                            .entrySet()
                                                            .stream()
                                                            .map(entry -> entry.getKey()
                                                                               .getName() + ", " + entry.getValue())
                                                            .collect(Collectors.toList()));
            }
        }
        config.set(count + ".AMOUNT", item.getAmount());
        config.set(count + ".MATERIAL", item.getType().name());
        config.set(count + ".KEEP-OPEN", true);
        config.set(count + ".POSITION-X", index % 9 + 1);
        config.set(count + ".POSITION-Y", index / 9 + 1);
    }
}
