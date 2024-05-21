package kz.hxncus.mc.fastpluginconfigurer.converter;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.hook.BetterGUIHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.ChestCommandsHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.DeluxeMenusHook;
import kz.hxncus.mc.fastpluginconfigurer.hook.ZMenuHook;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Convertible {
    void fileToInventory(Player player, String fileName);
    void inventoryToFile(Player player, String fileName);
    List<String> getAllFileNames();

    @Getter
    enum Converters {
        BETTER_GUI("BetterGUI", BetterGUIHook.class),
        CHEST_COMMANDS("ChestCommands", ChestCommandsHook.class),
        DELUXE_MENUS("DeluxeMenus", DeluxeMenusHook.class),
        ZMENU("ZMenu", ZMenuHook.class);

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
            FastPluginConfigurer instance = FastPluginConfigurer.getInstance();
            if (Bukkit.getPluginManager().getPlugin(name) == null) {
                return;
            }
            try {
                converter = clazz.getConstructor(FastPluginConfigurer.class).newInstance(instance);
                isEnabled = true;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                converter = null;
                isEnabled = false;
                throw new RuntimeException(e);
            }
            instance.getLogger().info("Hook " + clazz.getName() + " is enabled successfully.");
        }
    }
}
