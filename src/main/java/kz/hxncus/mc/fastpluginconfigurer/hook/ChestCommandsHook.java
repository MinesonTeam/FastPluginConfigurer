package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.attribute.*;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.util.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
import me.filoghost.chestcommands.api.Icon;
import me.filoghost.chestcommands.fcommons.collection.CaseInsensitiveString;
import me.filoghost.chestcommands.inventory.Grid;
import me.filoghost.chestcommands.menu.BaseMenu;
import me.filoghost.chestcommands.menu.MenuManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class ChestCommandsHook extends AbstractHook {
    public ChestCommandsHook(FastPluginConfigurer plugin) {
        super(plugin);
    }

    @Override
    public void convertFileToInventory(Player player, String fileName) {
        Block targetBlock = VersionUtil.getTargetBlock(player, 5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            Messages.MUST_LOOKING_AT_DOUBLE_CHEST.sendMessage(player);
            return;
        }
        BaseMenu menu = MenuManager.getMenuByFileName(fileName);
        if (menu == null) {
            Messages.MENU_NOT_FOUND.sendMessage(player, fileName);
        } else {
            storeConfigItemsInInventory(player, ((Chest) state).getInventory(), menu.getIcons());
        }
    }

    public static void storeConfigItemsInInventory(Player player, Inventory chestInventory, Grid<Icon> icons) {
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
        Messages.SUCCESSFULLY_STORED_ITEMS_TO_CHEST.sendMessage(player);
    }

    @Override
    public void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.commands", List.of(fileName));
    }

    @Override
    public List<String> getAllFileNames() {
        return MenuManager.getMenuFileNames().stream().map(CaseInsensitiveString::toString).collect(Collectors.toList());
    }

    @Override
    public void storeItemInConfig(FileConfiguration config, ConfigItem configItem, int count) {
        String path = count + ".";
        for (AttributeType attributeType : AttributeType.values()) {
            config.set(path + attributeType.name().replace('_', '-'), attributeType.attribute.apply(configItem));
        }
    }

    public enum AttributeType {
        AMOUNT(new AmountAttribute()),
        DURABILITY(new DurabilityAttribute()),
        LORE(new LoreAttribute()),
        MATERIAL(new MaterialAttribute()),
        NAME(new NameAttribute()),
        POSITION_X(new PositionAttribute(slot -> slot % 9 + 1)),
        POSITION_Y(new PositionAttribute(slot -> slot / 9 + 1)),
//        NBT_DATA("NBT-DATA", NBTDataAttribute::new),
        COLOR(new RGBAttribute(color -> color.getRed() + ", " + color.getGreen() + ", " + color.getBlue())),
        SKULL_OWNER(new SkullOwnerAttribute()),
        BANNER_COLOR(new BannerColorAttribute()),
        BANNER_PATTERNS(new BannerPatternsAttribute(patterns -> patterns.stream()
                                                                        .map(pattern -> pattern.getPattern().name() + ":" + pattern.getColor().name())
                                                                        .collect(Collectors.toList()))),
        ENCHANTMENTS(new EnchantmentsAttribute(map -> map.entrySet()
                                                         .stream()
                                                         .map(entry -> VersionUtil.getEnchantmentName(entry.getKey()) + ", " + entry.getValue())
                                                         .collect(Collectors.toList())));

        final Attribute attribute;

        AttributeType(Attribute attribute) {
            this.attribute = attribute;
        }
    }
}
