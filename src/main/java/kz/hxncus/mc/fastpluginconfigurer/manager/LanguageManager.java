package kz.hxncus.mc.fastpluginconfigurer.manager;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.util.Constants;
import kz.hxncus.mc.fastpluginconfigurer.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@EqualsAndHashCode
public class LanguageManager {
    private final FastPluginConfigurer plugin;
    private final FileConfiguration langConfig;

    public LanguageManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        String lang = plugin.getConfig().getString("lang");
        if (StringUtils.isEmpty(lang) || !Constants.SUPPORTED_LANGUAGES.contains("translations\\" + lang + Constants.YML_EXPANSION)) {
            lang = "en";
            plugin.getLogger().warning(() -> String.format("Unknown language '%s'. Selected 'en' as the default language.", plugin.getConfig().getString("lang")));
        }
        File file = new File(plugin.getDirectoryManager().getTranslationsDir() + File.separator + lang + Constants.YML_EXPANSION);
        this.langConfig = YamlConfiguration.loadConfiguration(file);
    }
}
