package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FPCCommand;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigSession;
import kz.hxncus.mc.fastpluginconfigurer.listener.DupeFixerListener;
import kz.hxncus.mc.fastpluginconfigurer.listener.PlayerListener;
import kz.hxncus.mc.fastpluginconfigurer.manager.DirectoryManager;
import kz.hxncus.mc.fastpluginconfigurer.manager.FilesManager;
import kz.hxncus.mc.fastpluginconfigurer.manager.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.manager.LanguageManager;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class FastPluginConfigurer extends JavaPlugin {
    @Getter
    private static FastPluginConfigurer instance;
    private static final Map<UUID, ConfigSession> CONFIG_SESSION_MAP = new ConcurrentHashMap<>();
    private InventoryManager inventoryManager;
    private DirectoryManager directoryManager;
    private LanguageManager languageManager;
    private FilesManager filesManager;

    public static ConfigSession getConfigSession(final UUID uuid) {
        return CONFIG_SESSION_MAP.computeIfAbsent(uuid, ConfigSession::new);
    }

    public static ConfigSession removeSession(final UUID uuid) {
        return CONFIG_SESSION_MAP.remove(uuid);
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        registerStaff();
    }

    public void registerStaff() {
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
        filesManager = new FilesManager(plugin);
        directoryManager = new DirectoryManager(plugin);
        languageManager = new LanguageManager(plugin);
        inventoryManager = new InventoryManager(plugin);
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
        if (!getConfig().getBoolean("metrics")) return;

        Metrics metrics = new Metrics(plugin, 22084);
        metrics.addCustomChart(new SimplePie("used_language", () -> languageManager.getLang()));
    }
}
