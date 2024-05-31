package kz.hxncus.mc.fastpluginconfigurer.manager;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.util.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FilesManager {
    private final FastPluginConfigurer plugin;

    public FilesManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        registerFiles();
        if (isConfigIsOutdated()) {
            updateFiles();
        }
    }

    private void registerFiles() {
        for (String filePath : Constants.FILES) {
            if (!new File(plugin.getDataFolder(), filePath).exists()) {
                plugin.saveResource(filePath, false);
            }
        }
    }

    private boolean isConfigIsOutdated() {
        FileConfiguration embeddedConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
        return plugin.getConfig().getInt(Constants.VERSION, 0) != embeddedConfig.getInt(Constants.VERSION, 0);
    }

    private void updateFiles() {
        plugin.getLogger().warning("Plugin version is outdated! Updating...");
        for (String filePath : Constants.FILES) {
            File file = new File(plugin.getDataFolder(), filePath);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            try (InputStream resource = plugin.getResource(filePath.replace('\\', '/'))) {
                if (resource == null) {
                    continue;
                }
                FileConfiguration embeddedConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
                config.options().copyDefaults(true);
                config.setDefaults(embeddedConfig);
                if ("config.yml".equals(filePath)) {
                    config.set(Constants.VERSION, embeddedConfig.getInt(Constants.VERSION, 0));
                }
                removeNonexistentKeys(config, embeddedConfig);
                saveConfig(config, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        plugin.getLogger().info("Files has been updated!");
    }

    private static void removeNonexistentKeys(FileConfiguration config, FileConfiguration embeddedConfig) {
        for (String key : config.getKeys(true)) {
            if (embeddedConfig.get(key) == null) {
                config.set(key, null);
            }
        }
    }

    private static void saveConfig(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
