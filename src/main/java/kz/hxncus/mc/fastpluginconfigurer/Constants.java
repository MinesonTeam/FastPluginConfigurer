package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

@UtilityClass
public class Constants {
    public final String CONFIG = "config";
    public final String INVENTORY_TO_FILE = "inventorytofile";
    public final String FILE_TO_INVENTORY = "filetoinventory";

    public final String YML_EXPANSION = ".yml";

    public final ItemBuilder ARROW_ITEM = new ItemBuilder(Material.ARROW);
    public final ItemBuilder NETHER_STAR = new ItemBuilder(Material.NETHER_STAR);
}
