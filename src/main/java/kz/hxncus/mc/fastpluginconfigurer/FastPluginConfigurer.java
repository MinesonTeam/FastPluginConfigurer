package kz.hxncus.mc.fastpluginconfigurer;

import kz.hxncus.mc.fastpluginconfigurer.command.FastPluginConfigurerCommand;
import kz.hxncus.mc.fastpluginconfigurer.inventory.ChestCommandsConverter;
import kz.hxncus.mc.fastpluginconfigurer.inventory.DeluxeMenusConverter;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class FastPluginConfigurer extends JavaPlugin {
    @Getter
    private static FastPluginConfigurer instance;
    @Override
    public void onLoad() {
        instance = this;
    }
    @Getter
    private DeluxeMenusConverter deluxeMenusConverter;
    @Getter
    private ChestCommandsConverter chestCommandsConverter;
    @Getter
    private File converterDirectory;
    @Override
    public void onEnable() {
        registerPlugins();
        registerConverters();
        registerCommands();
        registerDirectories();
    }

    @Override
    public void onDisable() {

    }

    private void registerPlugins() {
        PluginManager pluginManager = getServer().getPluginManager();
        if (pluginManager.getPlugin("DeluxeMenus") != null) {
            DeluxeMenusConverter.setEnabled(true);
        }
        if (pluginManager.getPlugin("ChestCommands") != null) {
            ChestCommandsConverter.setEnabled(true);
        }
    }

    private void registerConverters() {
        if (DeluxeMenusConverter.isEnabled()) {
            deluxeMenusConverter = new DeluxeMenusConverter();
            Logger.getLogger("FastPluginConfigurer").info("DeluxeMenusConverter is enabled successfully");
        }
        if (ChestCommandsConverter.isEnabled()) {
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
}
