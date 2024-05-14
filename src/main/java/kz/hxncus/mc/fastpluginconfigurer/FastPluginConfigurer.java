package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FPCCommand;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.hook.BetterGUIHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.ChestCommandsHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.DeluxeMenusHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.ZMenuHook;
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

@Getter
public final class FastPluginConfigurer extends JavaPlugin {
    @Getter
    private static FastPluginConfigurer instance;
    private DeluxeMenusHook deluxeMenusHook;
    private ChestCommandsHook chestCommandsHook;
    private BetterGUIHook betterguiHook;
    private ZMenuHook zMenuHook;
    private File converterDirectory;
    private final InventoryItemMarker itemMarker = new InventoryItemMarker(this);
    private static final Map<UUID, FastPlayer> PLAYER_MAP = new HashMap<>();

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
        registerConverters(Bukkit.getPluginManager());
        registerEvents(Bukkit.getPluginManager());
        registerCommands();
    }

    @Override
    public void onDisable() {
        InventoryManager.getInstance().closeAll();
    }

    private void registerConverters(PluginManager pluginManager) {
        if (pluginManager.getPlugin("deluxemenus") != null) {
            deluxeMenusHook = new DeluxeMenusHook(this);
            getLogger().info("Hook DeluxeMenus is enabled successfully.");
        }
        if (pluginManager.getPlugin("chestcommands") != null) {
            chestCommandsHook = new ChestCommandsHook(this);
            getLogger().info("Hook ChestCommands is enabled successfully.");
        }
        if (pluginManager.getPlugin("bettergui") != null) {
            betterguiHook = new BetterGUIHook(this);
            getLogger().info("Hook BetterGUI is enabled successfully.");
        }
        if (pluginManager.getPlugin("zmenu") != null) {
            zMenuHook = new ZMenuHook(this);
            getLogger().info("Hook zMenu is enabled successfully.");
        }
    }

    private void registerCommands() {
        new FPCCommand(this);
    }

    private void registerDirectories() {
        converterDirectory = new File(getDataFolder(), "converters");
        converterDirectory.mkdirs();
    }

    private void registerEvents(PluginManager pluginManager) {
        pluginManager.registerEvents(InventoryManager.getInstance(), this);
        pluginManager.registerEvents(new DupeFixer(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
    }
}
