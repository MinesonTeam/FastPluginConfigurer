package kz.hxncus.mc.fastpluginconfigurer.directory;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Getter;

import java.io.File;

@Getter
public class DirectoryManager {
    private final FastPluginConfigurer plugin;
    private File converterDirectory;
    private File langDirectory;
    public DirectoryManager(FastPluginConfigurer plugin) {
        this.plugin = plugin;
        register(plugin.getDataFolder());
    }

    private void register(File dataFolder) {
        converterDirectory = new File(dataFolder, "converters");
        converterDirectory.mkdirs();
        langDirectory = new File(dataFolder, "languages");
        langDirectory.mkdirs();
    }
}
