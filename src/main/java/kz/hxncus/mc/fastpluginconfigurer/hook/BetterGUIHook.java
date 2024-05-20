package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.language.Messages;
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
    public final FastPluginConfigurer plugin;

    public BetterGUIHook(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage(Messages.MUST_LOOKING_AT_DOUBLE_CHEST.getMessage());
            return;
        }
        Menu menu = BetterGUI.getInstance().getMenuManager().getMenu(fileName);
        if (menu instanceof SimpleMenu) {
            storeConfigItemsInInventory(player, ((Chest) state).getInventory(), ((SimpleMenu) menu).getButtonMap());
            return;
        }
        Messages.MENU_NOT_FOUND.sendMessage(player, fileName);
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
        Messages.SUCCESSFULLY_STORED_ITEMS_TO_CHEST.sendMessage(player);
    }

    @Override
    public void inventoryToFile(Player player, String fileName) {
        File file = new File(plugin.getDirectoryManager().getConverterDirectory(), fileName.endsWith(Constants.YML_EXPANSION) ? fileName : fileName + Constants.YML_EXPANSION);
        if (file.exists()) {
            Messages.FILE_ALREADY_EXISTS.sendMessage(player, fileName);
            return;
        }
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (state instanceof Chest) {
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
            Messages.CHEST_SUCCESSFULLY_STORED_INTO_FILE.sendMessage(player, fileName);
            return;
        }
        player.sendMessage(Messages.MUST_LOOKING_AT_DOUBLE_CHEST.getMessage());
    }

    private void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.command", fileName);
    }

    private void storeItemInConfig(ItemStack item, FileConfiguration config, int count, int index) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            String itemNamePath = count + ".NAME";
            if (itemMeta.hasDisplayName()) {
                config.set(itemNamePath, itemMeta.getDisplayName());
            } else {
                config.set(itemNamePath, itemMeta.getLocalizedName());
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
        config.set(count + ".ID", String.format("%s%s", item.getType().name(), item.getDurability() == 0 ? "" : ":" + item.getDurability()));
        config.set(count + ".AMOUNT", item.getAmount());
        config.set(count + ".POSITION-X", index % 9 + 1);
        config.set(count + ".POSITION-Y", index / 9 + 1);
    }

    @Override
    public List<String> getAllFileNames() {
        return new ArrayList<>(BetterGUI.getInstance().getMenuManager().getMenuNames());
    }
}
