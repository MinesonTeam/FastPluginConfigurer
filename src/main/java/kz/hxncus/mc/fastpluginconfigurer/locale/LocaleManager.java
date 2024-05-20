package kz.hxncus.mc.fastpluginconfigurer.locale;

import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class LocaleManager {
    @Getter
    private static final Set<String> supportedLanguages = new HashSet<>(List.of("en", "ru", "ua"));
    private final FastPluginConfigurer plugin;
    private FileConfiguration currentLocaleConfig;

    public LocaleManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        register(plugin);
    }

    private void register(FastPluginConfigurer plugin) {
        String lang = plugin.getConfig().getString("lang");
        if (StringUtils.isEmpty(lang) || !supportedLanguages.contains(lang)) {
            lang = "en";
            plugin.getLogger().severe(Messages.UNKNOWN_LANGUAGE.getFormattedMessage(lang));
        }
        File file = new File(plugin.getDirectoryManager().getLangDirectory() + File.separator + lang + Constants.YML_EXPANSION);
        currentLocaleConfig = YamlConfiguration.loadConfiguration(file);
    }
}
