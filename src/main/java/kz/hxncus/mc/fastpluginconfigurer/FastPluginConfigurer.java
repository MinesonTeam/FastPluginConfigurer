package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FastPluginConfigurerCommand;
import kz.hxncus.mc.fastpluginconfigurer.hook.ChestCommandsHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.DeluxeMenusHook;
import kz.hxncus.mc.fastpluginconfigurer.inventory.DupeFixer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.marker.InventoryItemMarker;
import kz.hxncus.mc.fastpluginconfigurer.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Getter
public final class FastPluginConfigurer extends JavaPlugin {
    private final Logger LOGGER = Logger.getLogger("FastPluginConfigurer");
    private DeluxeMenusHook deluxeMenusHook;
    private ChestCommandsHook chestCommandsHook;
    private File converterDirectory;
    private final InventoryItemMarker inventoryItemMarker = new InventoryItemMarker(this);
    private final PluginManager pluginManager = Bukkit.getPluginManager();
    private static final Map<UUID, FastPlayer> FAST_PLAYER_MAP = new HashMap<>();

    public static FastPlayer getFastPlayer(UUID uuid) {
        return FAST_PLAYER_MAP.computeIfAbsent(uuid, FastPlayer::new);
    }
    public static FastPlayer removePlayer(UUID uuid) {
        return FAST_PLAYER_MAP.remove(uuid);
    }

    @Override
    public void onEnable() {
        registerDirectories();
        registerConverters();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        InventoryManager.getInstance().closeAll();
    }

    private void registerConverters() {
        if (pluginManager.getPlugin("deluxemenus") != null) {
            deluxeMenusHook = new DeluxeMenusHook(this);
            LOGGER.info("Hook DeluxeMenus is enabled successfully.");
        }
        if (pluginManager.getPlugin("chestcommands") != null) {
            chestCommandsHook = new ChestCommandsHook(this);
            LOGGER.info("Hook ChestCommands is enabled successfully.");
        }
    }

    private void registerCommands() {
        new FastPluginConfigurerCommand(this);
    }

    private void registerDirectories() {
        converterDirectory = new File(getDataFolder(), "converters");
        converterDirectory.mkdirs();
    }

    private void registerEvents() {
        pluginManager.registerEvents(InventoryManager.getInstance(), this);
        pluginManager.registerEvents(new DupeFixer(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
    }
}
