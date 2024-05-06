package kz.hxncus.mc.fastpluginconfigurer.converter;

import org.bukkit.entity.Player;

public interface InventoryConverter {
    void fileToInventory(Player player, String fileName);
    void inventoryToFile(Player player, String fileName);
}
