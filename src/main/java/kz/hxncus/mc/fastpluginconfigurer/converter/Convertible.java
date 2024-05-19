package kz.hxncus.mc.fastpluginconfigurer.converter;

import org.bukkit.entity.Player;

import java.util.List;

public interface Convertible {
    public void fileToInventory(Player player, String fileName);
    public void inventoryToFile(Player player, String fileName);
    public List<String> getAllFileNames();
}
