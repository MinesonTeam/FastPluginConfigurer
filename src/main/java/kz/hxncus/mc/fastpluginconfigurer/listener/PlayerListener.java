package kz.hxncus.mc.fastpluginconfigurer.listener;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.cache.ConfigCache;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtil;
import kz.hxncus.mc.fastpluginconfigurer.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerListener implements Listener {
    private final FastPluginConfigurer plugin;

    public PlayerListener(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    private void openLastClosedInventory(Player player, ConfigCache session, File file) {
        openLastClosedInventory(player, session.getPluginName(), file.getPath());
    }

    private void openLastClosedInventory(Player player, String pluginName, String filePath) {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(player, String.format("fpc config %s %s", pluginName, filePath.replaceFirst(String.format("plugins\\\\%s\\\\", pluginName), ""))));
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        ConfigCache configCache = plugin.getCacheManager().getConfigCache(event.getPlayer().getUniqueId());
        ConfigCache.ChatState chatState = configCache.getChatState();
        if (chatState == ConfigCache.ChatState.NOTHING) {
            return;
        }
        event.setCancelled(true);
        configCache.setChatState(ConfigCache.ChatState.NOTHING);
        openLastClosedInventory(event.getPlayer(), configCache, configCache.getFile());
        if ("cancel".equalsIgnoreCase(event.getMessage())) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configCache.getFile());
        if (chatState == ConfigCache.ChatState.ADDING_NEW_KEY) {
            config.set(configCache.getKeyPath().isEmpty() ? event.getMessage() : configCache.getKeyPath() + "." + event.getMessage(), "");
        } else if (chatState == ConfigCache.ChatState.SETTING_KEY_VALUE) {
            config.set(configCache.getKeyPath(), convert(event.getMessage()));
        }
        FileUtil.save(config, configCache.getFile());
    }

    private Object convert(String message) {
        if ("null".equalsIgnoreCase(message)) {
            return null;
        } else if (message.startsWith("{") && message.endsWith("}")) {
            Map<String, Object> objectMap = new ConcurrentHashMap<>();
            String[] split = message.substring(1, message.length() - 1).split("(?<=^|,)\\s*(?:\\{([^:]+):([^,\\]]+)}|([^:]+):\\[([^]]+)])");
            for (String messages : split) {
                String[] splitted = messages.split(":");
                objectMap.put(splitted[0], convert(splitted[1]));
            }
            return objectMap;
        } else if (message.startsWith("[") && message.endsWith("]")) {
            ArrayList<Object> objects = new ArrayList<>();
            for (String messages : message.substring(1, message.length() - 1).split(", ")) {
                objects.add(convert(messages));
            }
            return objects;
        } else if (isMessageQuoted(message)) {
            return message.substring(1, message.length() - 1);
        } else if (NumberUtil.isCreatable(message)) {
            return NumberUtil.createNumber(message);
        } else if ("true".equalsIgnoreCase(message)) {
            return true;
        } else if ("false".equalsIgnoreCase(message)) {
            return false;
        }
        String[] split = message.split("\\\\n");
        return split.length < 2 ? message : split;
    }

    public boolean isMessageQuoted(String message) {
        String doubleQuotes = "\"";
        return message.startsWith(doubleQuotes) && message.endsWith(doubleQuotes);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getCacheManager().removeCache(event.getPlayer().getUniqueId());
    }
}
