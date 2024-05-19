package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FPCCommand;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.hook.HookManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryManager;
import kz.hxncus.mc.fastpluginconfigurer.inventory.dupefixer.DupeFixer;
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
    private final InventoryManager inventoryManager = new InventoryManager(this);
    private final HookManager hookManager = new HookManager(this);
    private File converterDirectory;

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
        registerDirectories();
        hookManager.registerHooks(Bukkit.getPluginManager());
        registerEvents(Bukkit.getPluginManager());
        registerCommands();
    }

    @Override
    public void onDisable() {
        inventoryManager.closeAll();
    }

    private void registerCommands() {
        new FPCCommand(instance);
    }

    private void registerDirectories() {
        converterDirectory = new File(getDataFolder(), "converters");
        converterDirectory.mkdirs();
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(inventoryManager, instance);
        pluginManager.registerEvents(new DupeFixer(instance, inventoryManager), instance);
        pluginManager.registerEvents(new PlayerListener(instance), instance);
    }
}
