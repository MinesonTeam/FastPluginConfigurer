package kz.hxncus.mc.fastpluginconfigurer.inventory.dupefixer;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.marker.PDCItemMarker;
import kz.hxncus.mc.fastpluginconfigurer.language.Messages;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
@EqualsAndHashCode
public class DupeFixer implements Listener {
    private final FastPluginConfigurer plugin;
    private final PDCItemMarker PDCItemMarker;
    public DupeFixer(FastPluginConfigurer plugin, InventoryManager inventoryManager) {
        this.plugin = plugin;
        this.PDCItemMarker = inventoryManager.getItemMarker();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = event.getPlayer();
            PlayerInventory inventory = player.getInventory();
            for (ItemStack itemStack : inventory) {
                if (PDCItemMarker.isItemMarked(itemStack)) {
                    inventory.remove(itemStack);
                    plugin.getLogger().info(Messages.PLAYER_LOGGED_WITH_CUSTOM_ITEM.getFormattedMessage(player.getName()));
                }
            }
        }, 10L);
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        Item item = event.getItem();
        if (PDCItemMarker.isItemMarked(item.getItemStack())) {
            event.setCancelled(true);
            item.remove();
            plugin.getLogger().info(() -> Messages.SOMEONE_PICKED_CUSTOM_ITEM.getFormattedMessage(item.getLocation()));
        }
    }

    @EventHandler
    public void onEntityDropItemEvent(EntityDropItemEvent event) {
        Item item = event.getItemDrop();
        if (PDCItemMarker.isItemMarked(item.getItemStack())) {
            event.setCancelled(true);
            item.remove();
            plugin.getLogger().info(() -> Messages.SOMEONE_DROPPED_CUSTOM_ITEM.getFormattedMessage(item.getLocation()));
        }
    }
}
