package kz.hxncus.mc.fastpluginconfigurer.config;

import kz.hxncus.mc.fastpluginconfigurer.util.PotionUtil;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
public class ConfigItem {
    private String name;
    private String skullOwner;
    private final Material material;
    private final int amount;
    private final int index;
    private int durability;
    private byte data;
    private Color RGB;
    private DyeColor dyeColor;
    private EntityType entityType;
    private Set<ItemFlag> itemFlags = Collections.emptySet();
    private List<String> lore = Collections.emptyList();
    private List<Pattern> bannerPatterns = Collections.emptyList();
    private List<PotionEffect> potionEffects = Collections.emptyList();
    private Map<Enchantment, Integer> enchantments = Collections.emptyMap();

    public ConfigItem(ItemStack item, int index) {
        this.material = item.getType();
        this.amount = item.getAmount();
        this.index = index;
        MaterialData itemData = item.getData();
        if (itemData != null) {
            this.data = itemData.getData();
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            this.name = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemMeta.getLocalizedName();
            this.lore = itemMeta.getLore();
            this.enchantments = itemMeta.getEnchants();
            this.itemFlags = itemMeta.getItemFlags();
            this.durability = item.getDurability();
            if (itemMeta instanceof LeatherArmorMeta) {
                this.RGB = ((LeatherArmorMeta) itemMeta).getColor();
            }
            if (itemMeta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                PotionEffectType effectType = potionMeta.getBasePotionData().getType().getEffectType();
                this.potionEffects = new ArrayList<>();
                if (effectType == null || potionMeta.hasCustomEffects()) {
                    this.RGB = potionMeta.getColor();
                    this.potionEffects.addAll(potionMeta.getCustomEffects());
                } else {
                    this.RGB = effectType.getColor();
                    this.potionEffects.add(PotionUtil.getPotionEffect(potionMeta));
                }
            }
            if (itemMeta instanceof SpawnEggMeta) {
                this.entityType = ((SpawnEggMeta) itemMeta).getSpawnedType();
            }
            if (itemMeta instanceof SkullMeta) {
                OfflinePlayer owningPlayer = ((SkullMeta) itemMeta).getOwningPlayer();
                this.skullOwner = owningPlayer == null ? null : owningPlayer.getName();
            }
            if (itemMeta instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) itemMeta;
                this.dyeColor = bannerMeta.getBaseColor();
                this.bannerPatterns = bannerMeta.getPatterns();
            }
        }
    }
}
