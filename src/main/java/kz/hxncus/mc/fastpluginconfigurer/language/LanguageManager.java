package kz.hxncus.mc.fastpluginconfigurer.language;

import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@EqualsAndHashCode
public class LanguageManager {
    private final FastPluginConfigurer plugin;
    private FileConfiguration langConfig;

    public LanguageManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        register(plugin);
    }

    private void register(FastPluginConfigurer plugin) {
        String lang = plugin.getConfig().getString("lang");
        if (StringUtils.isEmpty(lang) || !Constants.SUPPORTED_LANGUAGES.contains(lang)) {
            lang = "en";
            plugin.getLogger().severe(Messages.UNKNOWN_LANGUAGE.getFormattedMessage(lang));
        }
        File file = new File(plugin.getDirectoryManager().getLangDirectory() + File.separator + lang + Constants.YML_EXPANSION);
        langConfig = YamlConfiguration.loadConfiguration(file);
    }
}
