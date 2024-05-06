package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FastPluginConfigurerCommand;
import kz.hxncus.mc.fastpluginconfigurer.converter.ChestCommandsConverter;
import kz.hxncus.mc.fastpluginconfigurer.converter.DeluxeMenusConverter;
import kz.hxncus.mc.fastpluginconfigurer.inventory.DupeFixer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.marker.InventoryItemMarker;
import kz.hxncus.mc.fastpluginconfigurer.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Getter
public final class FastPluginConfigurer extends JavaPlugin {
    @Getter
    private static FastPluginConfigurer instance;
    @Getter
    private static final Map<String, Plugin> plugins = new HashMap<>();
    private DeluxeMenusConverter deluxeMenusConverter;
    private ChestCommandsConverter chestCommandsConverter;
    private File converterDirectory;
    private final InventoryManager inventoryManager = new InventoryManager();
    private final InventoryItemMarker inventoryItemMarker = new InventoryItemMarker(this);
    private final NamespacedKey markKey = new NamespacedKey(this, "mark");

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        registerStaff();
    }

    @Override
    public void onDisable() {
        inventoryManager.closeAll();
    }

    private void registerStaff() {
        registerPlugins();
        registerConverters();
        registerCommands();
        registerDirectories();
        registerEvents(Bukkit.getPluginManager());
    }

    private void registerPlugins() {
        PluginManager pluginManager = getServer().getPluginManager();
        for (Plugin plugin : pluginManager.getPlugins()) {
            plugins.put(plugin.getName().toLowerCase(), plugin);
        }
    }

    private void registerConverters() {
        if (plugins.containsKey("deluxemenus")) {
            deluxeMenusConverter = new DeluxeMenusConverter();
            Logger.getLogger("FastPluginConfigurer").info("DeluxeMenusConverter is enabled successfully");
        }
        if (plugins.containsKey("chestcommands")) {
            chestCommandsConverter = new ChestCommandsConverter();
            Logger.getLogger("FastPluginConfigurer").info("ChestCommandsConverter is enabled successfully");
        }
    }

    private void registerCommands() {
        new FastPluginConfigurerCommand();
    }

    private void registerDirectories() {
        converterDirectory = new File(getDataFolder(), "converters");
        converterDirectory.mkdirs();
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(inventoryManager, this);
        pluginManager.registerEvents(new DupeFixer(this), this);
        pluginManager.registerEvents(new PlayerListener(), this);
    }
}
