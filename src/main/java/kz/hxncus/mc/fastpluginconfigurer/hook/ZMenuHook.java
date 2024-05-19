package kz.hxncus.mc.fastpluginconfigurer.hook;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import kz.hxncus.mc.fastpluginconfigurer.Constants;
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
    public final FastPluginConfigurer plugin;

    public ZMenuHook(final FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage(Constants.MUST_LOOKING_AT_DOUBLE_CHEST);
            return;
        }
        InventoryManager manager = MenuPlugin.getInstance().getInventoryManager();
        Optional<fr.maxlego08.menu.api.Inventory> inventory = manager.getInventory(fileName);
        if (inventory.isEmpty()) {
            player.sendMessage(String.format("Menu not found: %s", fileName));
        } else {
            storeConfigInInventory(player, ((Chest) state).getInventory(), (ZInventory) inventory.get());
        }
    }

    private void storeConfigInInventory(Player player, Inventory chestInventory, ZInventory inventory) {
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
    public void inventoryToFile(Player player, String fileName) {
        String expansion = ".yml";
        File file = new File(plugin.getConverterDirectory(), fileName.endsWith(expansion) ? fileName : fileName + expansion);
        if (file.exists()) {
            player.sendMessage("File is already exists: " + fileName);
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
            player.sendMessage("Chest inventory successfully saved into " + fileName);
            return;
        }
        player.sendMessage(Constants.MUST_LOOKING_AT_DOUBLE_CHEST);
    }
    private void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("name", fileName);
        config.set("size", chestInventory.getSize());
    }

    private void storeItemInConfig(ItemStack item, FileConfiguration config, int count, int index) {
        String path = String.format("items.%s.", count);
        config.set(path + "item.material", item.getType().name());
        config.set(path + "item.amount", item.getAmount());
        config.set(path + "slot", index);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        String itemNamePath = "item.name";
        if (itemMeta.hasDisplayName()) {
            config.set(path + itemNamePath, itemMeta.getDisplayName());
        } else {
            config.set(path + itemNamePath, itemMeta.getLocalizedName());
        }
        if (itemMeta.hasLore()) {
            config.set(path + "item.lore", itemMeta.getLore());
        }
        if (itemMeta.hasEnchants()) {
            Stream<String> stringStream = itemMeta.getEnchants()
                                                  .entrySet()
                                                  .stream()
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
