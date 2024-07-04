package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.cache.CacheManager;
import kz.hxncus.mc.fastpluginconfigurer.command.FPCCommand;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigManager;
import kz.hxncus.mc.fastpluginconfigurer.config.Settings;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.listener.DupeFixerListener;
import kz.hxncus.mc.fastpluginconfigurer.listener.PlayerListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class FastPluginConfigurer extends JavaPlugin {
    private static FastPluginConfigurer instance;
    private InventoryManager inventoryManager;
    private ConfigManager configManager;
    private CacheManager cacheManager;

    public FastPluginConfigurer() {
        instance = this;
    }

    public static FastPluginConfigurer get() {
        return instance;
    }

    @Override
    public void onEnable() {
        registerManagers(this);
        registerEvents(this, Bukkit.getPluginManager());
        registerCommands(this);
        registerMetrics(this);
    }

    @Override
    public void onDisable() {
        inventoryManager.closeAll();
    }

    public void registerManagers(FastPluginConfigurer plugin) {
        configManager = new ConfigManager(plugin);
        inventoryManager = new InventoryManager(plugin);
        cacheManager = new CacheManager();
    }

    private void registerCommands(FastPluginConfigurer plugin) {
        new FPCCommand(plugin);
    }

    private void registerEvents(FastPluginConfigurer plugin, PluginManager pluginManager) {
        pluginManager.registerEvents(inventoryManager, plugin);
        pluginManager.registerEvents(new DupeFixerListener(plugin, inventoryManager), plugin);
        pluginManager.registerEvents(new PlayerListener(plugin), plugin);
    }

    private void registerMetrics(FastPluginConfigurer plugin) {
        if (!Settings.METRICS.toBool(true)) {
            return;
        }
        Metrics metrics = new Metrics(plugin, 22084);
        metrics.addCustomChart(new SimplePie("used_language", Settings.PLUGIN_LANGUAGE::toString));
    }
}
