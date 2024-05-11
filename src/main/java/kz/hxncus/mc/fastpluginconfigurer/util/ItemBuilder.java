package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(@NonNull ItemStack item) {
        this.itemStack = item.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        return meta(meta -> meta.setDisplayName(name));
    }

    public ItemBuilder setLore(String lore) {
        return setLore(Collections.singletonList(lore));
    }

    public ItemBuilder setLore(String ... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(List<String> lore) {
        return meta(meta -> meta.setLore(lore));
    }

    public ItemBuilder addLore(String lore) {
        return meta(meta -> {
            List<String> list = meta.getLore();
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(lore);
            meta.setLore(list);
        });
    }

    public ItemBuilder addLore(String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(List<String> lines) {
        return meta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) {
                meta.setLore(lines);
                return;
            }
            lore.addAll(lines);
            meta.setLore(lore);
        });
    }

    public ItemBuilder setAmount(int amount) {
        return edit(item -> item.setAmount(amount));
    }

    public ItemBuilder setType(Material material) {
        return edit(item -> item.setType(material));
    }

    public ItemBuilder edit(Consumer<ItemStack> function) {
        function.accept(this.itemStack);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment) {
        return addEnchant(enchantment, 1);
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        return meta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        return meta(meta -> meta.removeEnchant(enchantment));
    }

    public ItemBuilder clearEnchants() {
        return meta(meta -> meta.getEnchants().keySet().forEach(meta::removeEnchant));
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        return meta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        return meta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder setArmorColor(Color color) {
        return meta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    }

    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        return meta(meta -> {
            if (metaClass.isInstance(meta)) {
                metaConsumer.accept(metaClass.cast(meta));
            }
        });
    }

    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        if (itemMeta != null) {
            metaConsumer.accept(itemMeta);
        }
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
