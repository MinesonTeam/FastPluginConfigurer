package kz.hxncus.mc.fastpluginconfigurer.hook;

import com.extendedclip.deluxemenus.menu.Menu;
import com.extendedclip.deluxemenus.menu.MenuHolder;
import com.extendedclip.deluxemenus.menu.MenuItem;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.attribute.*;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.config.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.stream.Collectors;

public class DeluxeMenusHook extends AbstractHook {
    public DeluxeMenusHook(FastPluginConfigurer plugin) {
        super(plugin);
    }

    @Override
    public void convertFileToInventory(Player player, String fileName) {
        Block targetBlock = VersionUtil.getTargetBlock(player, 5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            Messages.MUST_LOOKING_AT_DOUBLE_CHEST.send(player);
            return;
        }
        Menu menu = Menu.getMenu(fileName);
        if (menu == null) {
            Messages.MENU_NOT_FOUND.send(player, fileName);
        } else {
            storeConfigItemsInInventory(player, ((Chest) state).getInventory(), menu);
        }
    }

    private void storeConfigItemsInInventory(Player player, Inventory chestInventory, Menu menu) {
        chestInventory.clear();
        MenuHolder holder = new MenuHolder(player);
        for (Map.Entry<Integer, TreeMap<Integer, MenuItem>> entry : menu.getMenuItems().entrySet()) {
            for (MenuItem item : entry.getValue().values()) {
                chestInventory.setItem(item.getSlot(), item.getItemStack(holder));
            }
        }
        player.openInventory(chestInventory);
        Messages.SUCCESSFULLY_STORED_ITEMS_TO_CHEST.send(player);
    }

    @Override
    public List<String> getAllFileNames() {
        return Menu.getAllMenus().stream().map(Menu::getMenuName).collect(Collectors.toList());
    }

    @Override
    public void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu_title", fileName);
        config.set("register_command", true);
        config.set("open_command", Collections.singletonList(fileName));
        config.set("size", chestInventory.getSize());
    }

    @Override
    public void storeItemInConfig(FileConfiguration config, ConfigItem configItem, int count) {
        String path = String.format("items.%s.", count);
        for (AttributeType attributeType : AttributeType.values()) {
            config.set(path + attributeType.name().toLowerCase(Locale.ROOT), attributeType.attribute.apply(configItem));
        }
    }

    public enum AttributeType {
        AMOUNT(new AmountAttribute()),
        DURABILITY(new DurabilityAttribute()),
        LORE(new LoreAttribute()),
        MATERIAL(new MaterialAttribute()),
        DISPLAY_NAME(new NameAttribute()),
        SLOT(new PositionAttribute(slot -> slot)),
        //        NBT_DATA("NBT-DATA", NBTDataAttribute::new),
        DATA(new DataAttribute()),
        RGB(new RGBAttribute(color -> color.getRed() + ", " + color.getGreen() + ", " + color.getBlue())),
        SKULL_OWNER(new SkullOwnerAttribute()),
        BASE_COLOR(new BannerColorAttribute()),
        ITEM_FLAGS(new ItemFlagsAttribute()),
        POTION_EFFECTS(new PotionEffectsAttribute(potionEffects -> potionEffects.stream()
                                                                                .filter(Objects::nonNull)
                                                                                .map(potionEffect -> potionEffect.getType().getName() + ";" + potionEffect.getDuration() + ";" + potionEffect.getAmplifier())
                                                                                .collect(Collectors.toList()))),
        BANNER_META(new BannerPatternsAttribute(patterns -> patterns.stream()
                                                                    .map(pattern -> pattern.getColor().name() + ";" + pattern.getPattern().name())
                                                                    .collect(Collectors.toList()))),
        ENCHANTMENTS(new EnchantmentsAttribute(map -> map.entrySet()
                                                         .stream()
                                                         .map(entry -> VersionUtil.getEnchantmentName(entry.getKey()) + ";" + entry.getValue())
                                                         .collect(Collectors.toList())));

        final Attribute attribute;

        AttributeType(Attribute attribute) {
            this.attribute = attribute;
        }
    }
}
