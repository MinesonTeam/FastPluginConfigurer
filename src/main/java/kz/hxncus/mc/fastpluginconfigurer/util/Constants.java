package kz.hxncus.mc.fastpluginconfigurer.util;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.util.builder.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class Constants {
    public final String CONFIG = "config";
    public final String INVENTORY_TO_FILE = "inventorytofile";
    public final String FILE_TO_INVENTORY = "filetoinventory";
    public final String VERSION = "version";
    public final String YML_EXPANSION = ".yml";

    public final ItemBuilder ARROW_ITEM = new ItemBuilder(Material.ARROW);
    public final ItemBuilder NETHER_STAR = new ItemBuilder(Material.NETHER_STAR);

    public final Set<String> SUPPORTED_LANGUAGES = new HashSet<>();
    public final Set<String> FILES = new HashSet<>(List.of("config.yml"));
    static {
        File[] files = new File(FastPluginConfigurer.getInstance().getDataFolder(), "translations").listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(YML_EXPANSION)) {
                    SUPPORTED_LANGUAGES.add(file.getParentFile().getName() + "\\" + file.getName());
                }
            }
        }
        FILES.addAll(SUPPORTED_LANGUAGES);
    }
}
