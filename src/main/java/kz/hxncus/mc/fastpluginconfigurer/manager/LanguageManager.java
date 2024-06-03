package kz.hxncus.mc.fastpluginconfigurer.manager;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.util.Constants;
import kz.hxncus.mc.fastpluginconfigurer.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@EqualsAndHashCode
public class LanguageManager {
    private final FastPluginConfigurer plugin;
    private final FileConfiguration langConfig;
    private String lang;

    @SneakyThrows
    public LanguageManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        lang = plugin.getConfig().getString("lang");
        if (StringUtils.isEmpty(lang) || !Constants.SUPPORTED_LANGUAGES.contains("translations\\" + lang + Constants.YML_EXPANSION)) {
            plugin.getLogger().warning(() -> String.format("Unknown language '%s'. Selected 'en' as the default language.", lang));
            lang = "en";
        }
        File file = new File(plugin.getDirectoryManager().getTranslationsDir(), lang + Constants.YML_EXPANSION);
        this.langConfig = YamlConfiguration.loadConfiguration(file);

        langConfig.load(file);
        plugin.getLogger().info(() -> String.format("Selected '%s' as the default language.", lang));
    }
}
