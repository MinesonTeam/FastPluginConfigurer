package kz.hxncus.mc.fastpluginconfigurer.util;

import com.google.common.primitives.Ints;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class VersionUtil {
    public final int CURRENT_VERSION = getCurrentVersion();
    public final boolean IS_PDC_VERSION = CURRENT_VERSION >= 1140;
    public final boolean IS_HEX_VERSION = CURRENT_VERSION >= 1160;
    public final Material SIGN = getSign();

    private int getCurrentVersion() {
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
        StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            String patch = matcher.group("patch");
            if (patch == null) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append(patch.replace(".", ""));
            }
        }
        Integer version = Ints.tryParse(stringBuilder.toString());
        if (version == null)
            throw new RuntimeException("Could not retrieve server version!");
        return version.intValue();
    }

    private Material getSign() {
        if (VersionUtil.CURRENT_VERSION < 1140) {
            return Material.valueOf("SIGN");
        } else {
            return Material.valueOf("OAK_SIGN");
        }
    }
}
