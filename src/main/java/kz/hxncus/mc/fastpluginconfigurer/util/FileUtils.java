package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class FileUtils {
    public boolean reload(FileConfiguration config, File file) {
        try {
            config.save(file);
            config.load(file);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
