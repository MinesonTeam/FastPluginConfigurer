package kz.hxncus.mc.fastpluginconfigurer;

import lombok.experimental.UtilityClass;

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
}
