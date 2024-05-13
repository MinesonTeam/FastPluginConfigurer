package kz.hxncus.mc.fastpluginconfigurer.converter;

import org.bukkit.entity.Player;

import java.util.List;

public interface Convertible {
    void fileToInventory(Player player, String fileName);
    void inventoryToFile(Player player, String fileName);
    List<String> getAllFileNames();
}
