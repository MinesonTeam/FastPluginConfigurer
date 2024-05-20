package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class Constants {
    public final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(List.of("en", "ru", "ua"));

    public final String CONFIG = "config";
    public final String RELOAD = "reload";
    public final String INVENTORY_TO_FILE = "inventorytofile";
    public final String FILE_TO_INVENTORY = "filetoinventory";

    public final String YML_EXPANSION = ".yml";

    public final ItemBuilder ARROW_ITEM = new ItemBuilder(Material.ARROW);
    public final ItemBuilder NETHER_STAR = new ItemBuilder(Material.NETHER_STAR);
}
