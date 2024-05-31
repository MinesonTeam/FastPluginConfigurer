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

    public final Set<String> EMBEDDED_LANGUAGES = new HashSet<>(List.of("translations\\ar.yml", "translations\\bn.yml", "translations\\da.yml", "translations\\de.yml",
            "translations\\en.yml", "translations\\eo.yml", "translations\\es.yml", "translations\\fr.yml", "translations\\hi.yml", "translations\\id.yml",
            "translations\\id.yml", "translations\\ja.yml", "translations\\nl.yml", "translations\\no.yml", "translations\\pt.yml", "translations\\ru.yml",
            "translations\\sv.yml", "translations\\ua.yml", "translations\\zh.yml"));
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
            SUPPORTED_LANGUAGES.addAll(EMBEDDED_LANGUAGES);
        }
        FILES.addAll(SUPPORTED_LANGUAGES);
    }
}
