package kz.hxncus.mc.fastpluginconfigurer.config;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Getter
@EqualsAndHashCode
public class ConfigManager {
    private final FastPluginConfigurer plugin;
    private final YamlConfiguration defaultSettings;
    private final YamlConfiguration defaultLanguages;
    private YamlConfiguration settings;
    private YamlConfiguration languages;
    private File convertedFolder;
    private File languagesFolder;

    public ConfigManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        this.defaultSettings = extractDefault("settings.yml");
        this.defaultLanguages = extractDefault("languages/en.yml");
        this.validateConfigs();
    }

    public YamlConfiguration getSettings() {
        return settings == null ? defaultSettings : settings;
    }

    public File getSettingsFile() {
        return new File(plugin.getDataFolder(), "settings.yml");
    }

    public YamlConfiguration getLanguages() {
        return languages == null ? defaultLanguages : languages;
    }

    private YamlConfiguration extractDefault(String source) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(plugin.getResource(source))) {
            return YamlConfiguration.loadConfiguration(inputStreamReader);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to extract default file: " + source);
            if (Settings.DEBUG.toBool()) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }
    }

    public void validateConfigs() {
        settings = validate("settings.yml", defaultSettings);
        languagesFolder = new File(plugin.getDataFolder(), "languages");
        languagesFolder.mkdir();
        convertedFolder = new File(plugin.getDataFolder(), "converted");
        convertedFolder.mkdir();
        String languageFile = "languages/" + settings.getString(Settings.PLUGIN_LANGUAGE.toPath()) + ".yml";
        languages = validate(languageFile, defaultLanguages);
    }

    private YamlConfiguration validate(String configName, YamlConfiguration defaultConfiguration) {
        File file = extractConfiguration(configName);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        boolean updated = false;
        for (String key : defaultConfiguration.getKeys(true)) {
            if (configuration.get(key) == null) {
                updated = true;
                plugin.getServer().getConsoleSender().sendMessage(getLanguages().getString(Messages.UPDATING_CONFIG_KEY.toPath()));
                configuration.set(key, defaultConfiguration.get(key));
            }
        }
        for (String key : configuration.getKeys(false)) {
            if (defaultConfiguration.get(key) == null) {
                updated = true;
                plugin.getServer().getConsoleSender().sendMessage(getLanguages().getString(Messages.REMOVING_CONFIG_KEY.toPath()));
                configuration.set(key, null);
            }
        }

        if (updated) {
            try {
                configuration.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save updated configuration file: " + file.getName());
                if (Settings.DEBUG.toBool()) {
                    e.printStackTrace();
                }
            }
        }
        return configuration;
    }

    public File extractConfiguration(String fileName) {
        File file = new File(this.plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
        return file;
    }
}
