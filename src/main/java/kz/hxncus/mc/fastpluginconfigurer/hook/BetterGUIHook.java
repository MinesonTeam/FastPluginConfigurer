package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.attribute.*;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.util.Constants;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtil;
import kz.hxncus.mc.fastpluginconfigurer.util.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
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

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class BetterGUIHook implements Convertible {
    public final FastPluginConfigurer plugin;

    public BetterGUIHook(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void convertFileToInventory(Player player, String fileName) {
        Block targetBlock = VersionUtil.getTargetBlock(player, 5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            Messages.MUST_LOOKING_AT_DOUBLE_CHEST.sendMessage(player);
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
    public void convertInventoryToFile(Player player, String fileName) {
        File file = new File(plugin.getDirectoryManager().getConvertedDir(), fileName.endsWith(Constants.YML_EXPANSION) ? fileName : fileName + Constants.YML_EXPANSION);
        if (file.exists()) {
            Messages.FILE_ALREADY_EXISTS.sendMessage(player, fileName);
            return;
        }
        Block targetBlock = VersionUtil.getTargetBlock(player, 5);
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
            FileUtil.reload(config, file);
            Messages.CHEST_SUCCESSFULLY_STORED_INTO_FILE.sendMessage(player, fileName);
            return;
        }
        Messages.MUST_LOOKING_AT_DOUBLE_CHEST.sendMessage(player);
    }

    private void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.command", fileName);
    }

    private void storeItemInConfig(ItemStack item, FileConfiguration config, int count, int index) {
        ConfigItem configItem = new ConfigItem(item, index);
        for (BetterGUIHook.AttributeType attributeType : BetterGUIHook.AttributeType.values()) {
            config.set(count + "." + attributeType.name().replace('_', '-'), attributeType.attribute.apply(configItem));
        }
    }

    @Override
    public List<String> getAllFileNames() {
        return new ArrayList<>(BetterGUI.getInstance().getMenuManager().getMenuNames());
    }

    public enum AttributeType {
        AMOUNT(new AmountAttribute()),
        DURABILITY(new DurabilityAttribute()),
        LORE(new LoreAttribute()),
        MATERIAL(new MaterialAttribute()),
        NAME(new NameAttribute()),
        SLOT(new PositionAttribute(slot -> slot)),
        POSITION_X(new PositionAttribute(slot -> slot % 9 + 1)),
        POSITION_Y(new PositionAttribute(slot -> slot / 9 + 1)),
        //        NBT_DATA("NBT-DATA", NBTDataAttribute::new),
        DATA(new DataAttribute()),
        SKULL_OWNER(new SkullOwnerAttribute()),
        BASE_COLOR(new BannerColorAttribute()),
        ITEM_FLAGS(new ItemFlagsAttribute()),
        POTION(new PotionEffectsAttribute(potionEffects -> potionEffects.stream()
                                                                                .filter(Objects::nonNull)
                                                                                .map(potionEffect -> potionEffect.getType().getName() + ", " + potionEffect.getDuration() / 20 + ", " + potionEffect.getAmplifier())
                                                                                .collect(Collectors.toList()))),
        BANNER_META(new BannerPatternsAttribute(patterns -> patterns.stream()
                                                                    .map(pattern -> pattern.getColor().name() + ";" + pattern.getPattern().name())
                                                                    .collect(Collectors.toList()))),
        ENCHANTMENT(new EnchantmentsAttribute(map -> map.entrySet()
                                                         .stream()
                                                         .map(entry -> VersionUtil.getEnchantmentName(entry.getKey()) + ", " + entry.getValue())
                                                         .collect(Collectors.toList())));

        final Attribute attribute;

        AttributeType(Attribute attribute) {
            this.attribute = attribute;
        }
    }
}
