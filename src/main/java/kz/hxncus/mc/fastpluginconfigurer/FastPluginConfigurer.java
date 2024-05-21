package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FPCCommand;
import kz.hxncus.mc.fastpluginconfigurer.directory.DirectoryManager;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.dupefixer.DupeFixer;
import kz.hxncus.mc.fastpluginconfigurer.language.LanguageManager;
import kz.hxncus.mc.fastpluginconfigurer.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class FastPluginConfigurer extends JavaPlugin {
    @Getter
    private static FastPluginConfigurer instance;
    private static final Map<UUID, FastPlayer> PLAYER_MAP = new ConcurrentHashMap<>();
    private InventoryManager inventoryManager;
    private DirectoryManager directoryManager;
    private LanguageManager languageManager;

    public static FastPlayer getFastPlayer(final UUID uuid) {
        return PLAYER_MAP.computeIfAbsent(uuid, FastPlayer::new);
    }
    public static FastPlayer removePlayer(final UUID uuid) {
        return PLAYER_MAP.remove(uuid);
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
        registerFiles(instance);
        registerManagers(instance);
        registerEvents(Bukkit.getPluginManager(), instance);
        registerCommands(instance);
    }

    @Override
    public void onDisable() {
        inventoryManager.closeAll();
    }

    public void registerManagers(FastPluginConfigurer plugin) {
        directoryManager = new DirectoryManager(plugin);
        languageManager = new LanguageManager(plugin);
        inventoryManager = new InventoryManager(plugin);
    }

    private void registerFiles(FastPluginConfigurer plugin) {
        saveDefaultConfig();
        for (String lang : Constants.SUPPORTED_LANGUAGES) {
            String filePath = String.format("languages\\%s.yml", lang);
            if (!new File(plugin.getDataFolder(), filePath).exists()) {
                saveResource(filePath, false);
            }
        }
    }

    private void registerCommands(FastPluginConfigurer plugin) {
        new FPCCommand(plugin);
    }

    private void registerEvents(PluginManager pluginManager, FastPluginConfigurer plugin) {
        pluginManager.registerEvents(inventoryManager, plugin);
        pluginManager.registerEvents(new DupeFixer(plugin, inventoryManager), plugin);
        pluginManager.registerEvents(new PlayerListener(plugin), plugin);
    }
}
