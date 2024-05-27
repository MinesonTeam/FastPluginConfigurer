package kz.hxncus.mc.fastpluginconfigurer.manager;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.File;

@Getter
@EqualsAndHashCode
public class DirectoryManager {
    private final FastPluginConfigurer plugin;
    private File convertedDir;
    private File translationsDir;

    public DirectoryManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        convertedDir = new File(plugin.getDataFolder(), "converted");
        if (!convertedDir.exists()) {
            convertedDir.mkdirs();
        }

        translationsDir = new File(plugin.getDataFolder(), "translations");
        if (!translationsDir.exists()) {
            translationsDir.mkdirs();
        }
    }
}
