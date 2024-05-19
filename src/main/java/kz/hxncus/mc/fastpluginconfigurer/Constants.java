package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class Constants {
    public final String[] ITEM_LORE = {"",
                                       "§eClick §fto change the current value",
                                       "§eShift + Click §fto copy the current value"};
    public final String[] SECTION_LORE = {"",
                                          "§eClick §fto open the section",
                                          "§eShift + Click §fto edit the section value"};
    public final String CONFIG = "config";
    public final String INVENTORY_TO_FILE = "inventorytofile";
    public final String FILE_TO_INVENTORY = "filetoinventory";
    public final ItemStack NEXT_PAGE_ITEM = new ItemBuilder(Material.ARROW).setDisplayName("Next page").build();
    public final ItemStack PREVIOUS_PAGE_ITEM = new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build();
    public final ItemStack ADD_NEW_KEY_ITEM = new ItemBuilder(Material.NETHER_STAR).setDisplayName("§fClick to add a new key").build();
}
