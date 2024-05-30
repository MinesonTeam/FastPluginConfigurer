package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class FileUtil {
    public boolean reload(FileConfiguration config, File file) {
        try {
            config.save(file);
            config.load(file);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            return false;
        }
    }

    public boolean save(FileConfiguration config, File file) {
        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
