package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

@Getter
public class HookManager {
    private final FastPluginConfigurer plugin;
    private DeluxeMenusHook deluxeMenusHook;
    private ChestCommandsHook chestCommandsHook;
    private BetterGUIHook betterGUIHook;
    private ZMenuHook zMenuHook;

    public HookManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        register(Bukkit.getPluginManager());
    }

    public void register(PluginManager pluginManager) {
        Logger logger = plugin.getLogger();
        if (pluginManager.getPlugin("deluxemenus") != null) {
            deluxeMenusHook = new DeluxeMenusHook(plugin);
            logger.info("Hook DeluxeMenus is enabled successfully.");
        }
        if (pluginManager.getPlugin("chestcommands") != null) {
            chestCommandsHook = new ChestCommandsHook(plugin);
            logger.info("Hook ChestCommands is enabled successfully.");
        }
        if (pluginManager.getPlugin("bettergui") != null) {
            betterGUIHook = new BetterGUIHook(plugin);
            logger.info("Hook BetterGUI is enabled successfully.");
        }
        if (pluginManager.getPlugin("zmenu") != null) {
            zMenuHook = new ZMenuHook(plugin);
            logger.info("Hook zMenu is enabled successfully.");
        }
    }
}
