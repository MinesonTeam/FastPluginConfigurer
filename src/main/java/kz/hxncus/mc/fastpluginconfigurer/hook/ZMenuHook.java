package kz.hxncus.mc.fastpluginconfigurer.hook;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.InventoryManager;
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
import java.util.stream.Stream;

public class ZMenuHook implements Convertible {
    private final FastPluginConfigurer plugin;

    public ZMenuHook(final FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fileToInventory(final Player player, final String fileName) {
        final Block targetBlock = player.getTargetBlockExact(5);
        final BlockState state;
        if (targetBlock == null) {
            state = null;
        } else {
            state = targetBlock.getState();
        }
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        InventoryManager manager = MenuPlugin.getInstance().getInventoryManager();
        Optional<fr.maxlego08.menu.api.Inventory> inventory = manager.getInventory(fileName);
        if (inventory.isEmpty()) {
            player.sendMessage(String.format("Menu not found: %s", fileName));
        } else {
            Inventory chestInventory = ((Chest) state).getInventory();
            storeConfigInInventory(player, chestInventory, (ZInventory) inventory.get());
        }
    }

    private void storeConfigInInventory(final Player player, final Inventory chestInventory, final ZInventory inventory) {
        chestInventory.clear();
        final ArrayList<Button> buttons = new ArrayList<>(inventory.getButtons());
        for (Pattern pattern : inventory.getPatterns()) {
            buttons.addAll(pattern.getButtons());
        }
        for (Button button : buttons) {
            final Collection<Integer> slots = button.getSlots();
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
    public void inventoryToFile(final Player player, final String fileName) {
        final File file = new File(plugin.getConverterDirectory(), fileName.endsWith(".yml") ? fileName : fileName + ".yml");
        if (file.exists()) {
            player.sendMessage("File is already exists: " + fileName);
            return;
        }
        final Block targetBlock = player.getTargetBlockExact(5);
        final BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
        } else {
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
    }
    private void configureInventory(final String fileName, final FileConfiguration config, final Inventory chestInventory) {
        config.set("name", fileName);
        config.set("size", chestInventory.getSize());
    }

    private void storeItemInConfig(final ItemStack item, final FileConfiguration config, final int count, final int i) {
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
            Stream<String> stringStream = itemMeta.getEnchants().entrySet().stream()
                                                  .map(entry -> String.format("%s;%s",
                                                                entry.getKey().getKey().getKey(),
                                                                entry.getValue()));
            config.set(path + "enchantments", stringStream.collect(Collectors.toList()));
        }
    }

    @Override
    public List<String> getAllFileNames() {
        return MenuPlugin.getInstance().getInventoryManager().getInventories()
                         .stream()
                         .map(fr.maxlego08.menu.api.Inventory::getFileName)
                         .collect(Collectors.toList());
    }
}
