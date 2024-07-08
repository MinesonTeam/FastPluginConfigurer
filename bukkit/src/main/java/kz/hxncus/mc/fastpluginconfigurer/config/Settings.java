package kz.hxncus.mc.fastpluginconfigurer.config;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Settings {
    VERSION("version"), PLUGIN_LANGUAGE("lang"), DEBUG("debug"), METRICS("metrics");
    private final String path;

    Settings(String path) {
        this.path = path;
    }

    public Object getValue() {
        return FastPluginConfigurer.get().getConfigManager().getSettings().get(path);
    }

    public Object getValue(Object def) {
        return FastPluginConfigurer.get().getConfigManager().getSettings().get(path, def);
    }

    private void setValue(Object value) {
        setValue(value, true);
    }

    public void setValue(Object value, boolean save) {
        FileConfiguration settings = FastPluginConfigurer.get().getConfigManager().getSettings();
        settings.set(path, value);
        if (save) {
            try {
                settings.save(FastPluginConfigurer.get().getDataFolder().toPath().resolve("settings.yml").toFile());
            } catch (Exception e) {
                FastPluginConfigurer.get().getLogger().severe("Failed to apply changes to settings.yml");
            }
        }
    }

    public String toPath() {
        return path;
    }

    @Override
    public String toString() {
        return (String) getValue();
    }

    public Boolean toBool() {
        return (Boolean) getValue();
    }

    public Boolean toBool(Boolean def) {
        return (Boolean) getValue(def);
    }

    public Number toNumber() {
        return (Number) getValue();
    }

    public Number toNumber(Number def) {
        return (Number) getValue(def);
    }

    public List<String> toStringList() {
        return FastPluginConfigurer.get().getConfigManager().getSettings().getStringList(path);
    }

    public ConfigurationSection toConfigSection() {
        return FastPluginConfigurer.get().getConfigManager().getSettings().getConfigurationSection(path);
    }
}
