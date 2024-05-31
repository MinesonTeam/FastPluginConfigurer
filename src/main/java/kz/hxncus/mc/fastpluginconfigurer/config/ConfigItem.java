package kz.hxncus.mc.fastpluginconfigurer.config;

import kz.hxncus.mc.fastpluginconfigurer.util.PotionUtil;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private List<String> lore;
    private List<Pattern> bannerPatterns = Collections.emptyList();
    private List<PotionEffect> potionEffects = Collections.emptyList();
    private Map<Enchantment, Integer> enchantments = Collections.emptyMap();

    public ConfigItem(ItemStack item, int index) {
        this.material = item.getType();
        this.amount = item.getAmount();
        this.index = index;
        this.data = item.getData() == null ? null : item.getData().getData();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        this.name = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null;
        this.lore = itemMeta.getLore();
        this.enchantments = itemMeta.getEnchants();
        this.itemFlags = itemMeta.getItemFlags();
        this.durability = item.getDurability();
        if (itemMeta instanceof LeatherArmorMeta) {
            this.RGB = ((LeatherArmorMeta) itemMeta).getColor();
        }
        if (itemMeta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            if (VersionUtil.IS_POTION_COLOR_VERSION) {
                this.RGB = potionMeta.getColor();
            }
            if (potionMeta.hasCustomEffects()) {
                this.potionEffects = potionMeta.getCustomEffects();
            } else if (VersionUtil.IS_POTION_DATA_VERSION) {
                this.potionEffects = Collections.singletonList(PotionUtil.getPotionEffect(potionMeta));
            }
        }
        if (VersionUtil.IS_SPAWN_EGG_META_VERSION && itemMeta instanceof SpawnEggMeta) {
            this.entityType = ((SpawnEggMeta) itemMeta).getSpawnedType();
        }
        if (itemMeta instanceof SkullMeta) {
            this.skullOwner = ((SkullMeta) itemMeta).getOwner();
        }
        if (itemMeta instanceof BannerMeta) {
            BannerMeta bannerMeta = (BannerMeta) itemMeta;
            this.dyeColor = bannerMeta.getBaseColor();
            this.bannerPatterns = bannerMeta.getPatterns();
        }
    }
}
