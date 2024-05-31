package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Convertible {
    void convertFileToInventory(Player player, String fileName);
    void convertInventoryToFile(Player player, String fileName);
    void configureInventory(String fileName, FileConfiguration config, Inventory chestInventory);
    void storeItemInConfig(FileConfiguration config, ConfigItem configItem, int count);
    List<String> getAllFileNames();

    @Getter
    enum Converters {
        BETTER_GUI("BetterGUI", BetterGUIHook.class),
        CHEST_COMMANDS("ChestCommands", ChestCommandsHook.class),
        DELUXE_MENUS("DeluxeMenus", DeluxeMenusHook.class),
        ZMENU("zMenu", ZMenuHook.class);

        private final String name;
        private final Class<? extends Convertible> clazz;
        private Convertible converter;
        private boolean isEnabled;

        Converters(String name, Class<? extends Convertible> clazz) {
            this.name = name;
            this.clazz = clazz;
            setConverter();
        }

        public void setConverter() {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
            if (plugin == null || !plugin.isEnabled()) {
                return;
            }
            try {
                converter = clazz.getConstructor(FastPluginConfigurer.class).newInstance(FastPluginConfigurer.getInstance());
                isEnabled = true;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                converter = null;
                isEnabled = false;
                throw new RuntimeException(e);
            }
            FastPluginConfigurer.getInstance().getLogger().info("Hook " + clazz.getSimpleName() + " is enabled successfully.");
        }

        public static Converters valueOfIgnoreCase(String name) {
            for (Convertible.Converters converters : values()) {
                if (converters.getName().equalsIgnoreCase(name)) {
                    return converters;
                }
            }
            return null;
        }

//        public static void updateAllConverters() {
//            for (Converters converter : values()) {
//                converter.setConverter();
//            }
//
//        }
    }
}
