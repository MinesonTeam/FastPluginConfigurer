package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.attribute.*;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.config.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.lib.core.bukkit.gui.object.BukkitItem;
import me.hsgamer.bettergui.lib.core.minecraft.gui.button.Button;
import me.hsgamer.bettergui.lib.core.minecraft.gui.object.Item;
import me.hsgamer.bettergui.lib.core.minecraft.gui.simple.SimpleButtonMap;
import me.hsgamer.bettergui.menu.SimpleMenu;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class BetterGUIHook extends AbstractHook {
    public BetterGUIHook(FastPluginConfigurer plugin) {
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
        Menu menu = BetterGUI.getInstance().getMenuManager().getMenu(fileName);
        if (menu instanceof SimpleMenu) {
            storeConfigItemsInInventory(player, ((Chest) state).getInventory(), ((SimpleMenu) menu).getButtonMap());
            return;
        }
        Messages.MENU_NOT_FOUND.send(player, fileName);
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
        Messages.SUCCESSFULLY_STORED_ITEMS_TO_CHEST.send(player);
    }

    @Override
    public void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("menu-settings.name", fileName);
        config.set("menu-settings.rows", chestInventory.getSize() / 9);
        config.set("menu-settings.command", fileName);
    }

    @Override
    public void storeItemInConfig(FileConfiguration config, ConfigItem configItem, int count) {
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
        //NBT_DATA("NBT-DATA", NBTDataAttribute::new),
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
