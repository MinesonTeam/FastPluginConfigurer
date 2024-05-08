package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {
    private final org.bukkit.inventory.ItemStack itemStack;
    private final ItemMeta itemMeta;
    public ItemBuilder(Material material) {
        this(new org.bukkit.inventory.ItemStack(material));
    }

    public ItemBuilder(@NonNull org.bukkit.inventory.ItemStack item) {
        this.itemStack = item.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) { return meta(meta -> meta.setDisplayName(name)); }

    public ItemBuilder setLore(String lore) {
        return setLoreString(Collections.singletonList(lore));
    }

    public ItemBuilder setLore(String... lore) {
        return setLoreString(Arrays.asList(lore));
    }

    public ItemBuilder setLoreString(List<String> lore) {
        return meta(meta -> meta.setLore(lore));
    }

    public ItemBuilder addLore(String lore) {
        return meta(meta -> {
            List<String> list = meta.getLore();
            if (list == null) {
                meta.setLore(Collections.singletonList(lore));
            } else {
                list.add(lore);
            }
        });
    }

    public ItemBuilder addLore(String... lore) {
        return addLoreString(Arrays.asList(lore));
    }

    public ItemBuilder addLoreString(List<String> lines) {
        return meta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null || lore.isEmpty()) {
                meta.setLore(lines);
                return;
            }
            lore.addAll(lines);
            meta.setLore(lore);
        });
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ItemBuilder data(int data) {
        return durability((short) data);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ItemBuilder durability(short durability) {
        return edit(item -> item.setDurability(durability));
    }

    public ItemBuilder setAmount(int amount) {
        return edit(item -> item.setAmount(amount));
    }
    public ItemBuilder type(Material material) {
        return edit(item -> item.setType(material));
    }

    public ItemBuilder edit(Consumer<org.bukkit.inventory.ItemStack> function) {
        function.accept(this.itemStack);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return meta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        return meta(meta -> meta.removeEnchant(enchantment));
    }

    public ItemBuilder clearEnchants() {
        return meta(meta -> meta.getEnchants().keySet().forEach(meta::removeEnchant));
    }

    public ItemBuilder flags() {
        return flags(ItemFlag.values());
    }

    public ItemBuilder flags(ItemFlag... flags) {
        return meta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder removeFlags() {
        return removeFlags(ItemFlag.values());
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        return meta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        if (itemMeta != null) {
            metaConsumer.accept(itemMeta);
        }
        return this;
    }

    public ItemBuilder skullMeta(Consumer<SkullMeta> consumer) {
        return meta(meta -> {
                    if (meta instanceof SkullMeta) {
                        consumer.accept((SkullMeta) meta);
                    }
                }
        );
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ItemBuilder setSkullOwner(@NonNull String name) {
        return skullMeta(skullMeta -> skullMeta.setOwner(name));
    }

    public ItemBuilder armorColor(Color color) {
        return meta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    }

    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        return meta(meta -> {
            if (metaClass.isInstance(meta)) {
                metaConsumer.accept(metaClass.cast(meta));
            }
        });
    }

    public org.bukkit.inventory.ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
