package kz.hxncus.mc.fastpluginconfigurer.hook;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.attribute.*;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.util.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.stream.Collectors;

public class ZMenuHook extends AbstractHook {
    public ZMenuHook(final FastPluginConfigurer plugin) {
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
        InventoryManager manager = MenuPlugin.getInstance().getInventoryManager();
        Optional<fr.maxlego08.menu.api.Inventory> inventory = manager.getInventory(fileName);
        if (inventory.isEmpty()) {
            Messages.MENU_NOT_FOUND.sendMessage(player, fileName);
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
        Messages.SUCCESSFULLY_STORED_ITEMS_TO_CHEST.sendMessage(player);
    }

    @Override
    public void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory) {
        config.set("name", fileName);
        config.set("size", chestInventory.getSize());
    }

    @Override
    public void storeItemInConfig(FileConfiguration config, ConfigItem configItem, int count) {
        String path = String.format("items.%s.", count);
        config.set(path + "slot", configItem.getIndex());
        for (ZMenuHook.AttributeType attributeType : ZMenuHook.AttributeType.values()) {
            config.set(path + ".item." + attributeType.name().toLowerCase(Locale.ROOT), attributeType.attribute.apply(configItem));
        }
    }

    @Override
    public List<String> getAllFileNames() {
        return MenuPlugin.getInstance().getInventoryManager().getInventories()
                         .stream()
                         .map(fr.maxlego08.menu.api.Inventory::getFileName)
                         .collect(Collectors.toList());
    }

    public enum AttributeType {
        AMOUNT(new AmountAttribute()),
        DURABILITY(new DurabilityAttribute()),
        LORE(new LoreAttribute()),
        MATERIAL(new MaterialAttribute()),
        NAME(new NameAttribute()),
        //        NBT_DATA("NBT-DATA", NBTDataAttribute::new),
        DATA(new DataAttribute()),
        COLOR(new RGBAttribute(color -> color.getRed() + "," + color.getGreen() + "," + color.getBlue())),
        SKULL_OWNER(new SkullOwnerAttribute()),
        BANNER(new BannerColorAttribute()),
        FLAGS(new ItemFlagsAttribute()),
        POTION_EFFECTS(new PotionEffectsAttribute(potionEffects -> potionEffects.stream()
                                                                                .filter(Objects::nonNull)
                                                                                .map(potionEffect -> potionEffect.getType().getName() + ";" + potionEffect.getDuration() + ";" + potionEffect.getAmplifier())
                                                                                .collect(Collectors.toList()))),
        BANNER_PATTERNS(new BannerPatternsAttribute(patterns -> patterns.stream()
                                                                    .map(pattern -> pattern.getColor().name() + ";" + pattern.getPattern().name())
                                                                    .collect(Collectors.toList()))),
        ENCHANTS(new EnchantmentsAttribute(map -> map.entrySet()
                                                         .stream()
                                                         .map(entry -> VersionUtil.getEnchantmentName(entry.getKey()).toUpperCase(Locale.ROOT) + ";" + entry.getValue())
                                                         .collect(Collectors.toList())));

        final Attribute attribute;

        AttributeType(Attribute attribute) {
            this.attribute = attribute;
        }
    }
}
