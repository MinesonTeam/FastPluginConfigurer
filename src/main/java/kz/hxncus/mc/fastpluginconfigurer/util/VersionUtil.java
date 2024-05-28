package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class VersionUtil {
    public final int CURRENT_VERSION = getCurrentVersion();
    public final boolean IS_PDC_VERSION = CURRENT_VERSION >= 1140;
    public final boolean IS_HEX_VERSION = CURRENT_VERSION >= 1160;
    public final boolean IS_TARGET_BLOCK_VERSION = CURRENT_VERSION >= 1140;
    public final boolean IS_NAMESPACED_KEY_VERSION = CURRENT_VERSION >= 1120;
    public final boolean IS_SPAWN_EGG_META_VERSION = CURRENT_VERSION >= 1110;
    public final boolean IS_POTION_COLOR_VERSION = CURRENT_VERSION >= 1110;
    public final boolean IS_POTION_DATA_VERSION = CURRENT_VERSION >= 190;
    public final Material SIGN = getSign();

    private int getCurrentVersion() {
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
        StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            String patch = matcher.group("patch");
            if (patch == null) {
                stringBuilder.append('0');
            } else {
                stringBuilder.append(patch.replace(".", ""));
            }
        }
        Integer version = NumberUtils.createInteger(stringBuilder.toString());
        if (version == null) {
            throw new RuntimeException("Could not retrieve server version!");
        }
        return version;
    }

    private Material getSign() {
        if (VersionUtil.CURRENT_VERSION < 1140) {
            return Material.valueOf("SIGN");
        } else {
            return Material.valueOf("OAK_SIGN");
        }
    }

    public Block getTargetBlock(Player player, int distance) {
        if (IS_TARGET_BLOCK_VERSION) {
            return player.getTargetBlockExact(distance);
        } else {
            BlockIterator iterator = new BlockIterator(player, distance);
            while (iterator.hasNext()) {
                Block block = iterator.next();
                if (block.getType() != Material.AIR) {
                    return block;
                }
            }
        }
        return null;
    }

    public String getEnchantmentName(Enchantment enchantment) {
        if (IS_NAMESPACED_KEY_VERSION) {
            return enchantment.getKey().getKey();
        } else {
            return enchantment.getName();
        }
    }
}
