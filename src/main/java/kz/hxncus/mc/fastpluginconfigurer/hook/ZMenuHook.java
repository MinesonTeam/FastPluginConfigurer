package kz.hxncus.mc.fastpluginconfigurer.hook;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZMenuHook implements Convertible {
    private final FastPluginConfigurer plugin;

    public ZMenuHook(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void fileToInventory(Player player, String fileName) {
        Block targetBlock = player.getTargetBlockExact(5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            player.sendMessage("You must be looking at a double chest to execute this command.");
            return;
        }
        Optional<fr.maxlego08.menu.api.Inventory> inventory = MenuPlugin.getInstance().getInventoryManager().getInventory(fileName);
        if (inventory.isEmpty()) {
            player.sendMessage("Menu not found: " + fileName);
            return;
        }
        storeConfigItemsInInventory(player, ((Chest) state).getInventory(), (ZInventory) inventory.get());
    }

    private void storeConfigItemsInInventory(Player player, Inventory chestInventory, ZInventory inventory) {
        chestInventory.clear();
        ArrayList<Button> buttons = new ArrayList<>(inventory.getButtons());
        for (Pattern pattern : inventory.getPatterns()) {
            buttons.addAll(pattern.getButtons());
        }
        for (Button button : buttons) {
            Collection<Integer> slots = button.getSlots();
            if (slots.isEmpty()) {
                slots.add(button.getSlot());
            }
            for (int slot : slots) {
                chestInventory.setItem(slot % 56, button.getItemStack().build(player));
            }
        }
        player.openInventory(chestInventory);
        player.sendMessage("Successfully stored all items to the chest.");
    }

    @Override
    public void inventoryToFile(Player player, String fileName) {

    }

    @Override
    public List<String> getAllFileNames() {
        return MenuPlugin.getInstance().getInventoryManager().getInventories().stream().map(fr.maxlego08.menu.api.Inventory::getFileName).collect(Collectors.toList());
    }
}
