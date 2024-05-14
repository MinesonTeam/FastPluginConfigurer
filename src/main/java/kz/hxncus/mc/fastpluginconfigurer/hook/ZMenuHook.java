package kz.hxncus.mc.fastpluginconfigurer.hook;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtils;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class ZMenuHook implements Convertible {
    private final FastPluginConfigurer plugin;

    public ZMenuHook(FastPluginConfigurer plugin) {
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
        Optional<fr.maxlego08.menu.api.Inventory> inventory = MenuPlugin.getInstance().getInventoryManager().getInventory(fileName);
        if (inventory.isEmpty()) {
            player.sendMessage("Menu not found: " + fileName);
            return;
        }
        storeConfigItemsInInventory(player, ((Chest) state).getInventory(), (ZInventory) inventory.get());
    }

    private void storeConfigItemsInInventory(Player player, Inventory chestInventory, ZInventory inventory) {
        chestInventory.clear();
        ArrayList<Button> buttons = new ArrayList<>(inventory.getButtons());
        for (Pattern pattern : inventory.getPatterns()) {
            buttons.addAll(pattern.getButtons());
        }
        for (Button button : buttons) {
            Collection<Integer> slots = button.getSlots();
            if (slots.isEmpty()) {
                slots.add(button.getSlot());
            }
            for (int slot : slots) {
                chestInventory.setItem(slot % 56, button.getItemStack().build(player));
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
        config.set("name", fileName);
        config.set("size", chestInventory.getSize());
    }

    private void storeInventoryInConfig(Inventory chestInventory, FileConfiguration config) {
        int count = 0;
        for (int i = 0; i < chestInventory.getSize(); i++) {
            ItemStack item = chestInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            storeItemInConfig(item, config, count++, i);
        }
    }

    private void storeItemInConfig(ItemStack item, FileConfiguration config, int count, int i) {
        String path = String.format("items.%s.", count);
        ItemMeta itemMeta = item.getItemMeta();
        config.set(path + "item.material", item.getType().name());
        config.set(path + "item.amount", item.getAmount());
        if (itemMeta.hasDisplayName()) {
            config.set(path + "item.name", itemMeta.getDisplayName());
        }
        if (itemMeta.hasLore()) {
            config.set(path + "item.lore", itemMeta.getLore());
        }
        config.set(path + "slot", i);
        if (itemMeta.hasEnchants()) {
            config.set(path + "enchantments", itemMeta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ";" + entry.getValue()).collect(Collectors.toList()));
        }
    }

    @Override
    public List<String> getAllFileNames() {
        return MenuPlugin.getInstance().getInventoryManager().getInventories().stream().map(fr.maxlego08.menu.api.Inventory::getFileName).collect(Collectors.toList());
    }
}
