package kz.hxncus.mc.fastpluginconfigurer.hook;

import org.bukkit.entity.Player;

public interface Convertible {
    void fileToInventory(Player player, String fileName);
    void inventoryToFile(Player player, String fileName);
}
