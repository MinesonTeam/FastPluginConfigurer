package kz.hxncus.mc.fastpluginconfigurer.listener;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.language.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerListener implements Listener {
    private final FastPluginConfigurer plugin;

    public PlayerListener(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    private void openLastClosedInventory(FastPlayer fastPlayer, Player player, File file) {
        String pluginName = fastPlayer.getLastPluginName();
        if (pluginName != null) {
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(player, String.format("fpc config %s %s", pluginName, file.getName())));
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FastPlayer fastPlayer = FastPluginConfigurer.getFastPlayer(player.getUniqueId());
        if (!fastPlayer.isChatAddKey() && !fastPlayer.isChatSetKey()) {
            return;
        }
        File file = fastPlayer.getFile();
        if (!file.exists()) {
            try {
                if (file.getParentFile().mkdirs()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String message = event.getMessage();
        if ("cancel".equalsIgnoreCase(message)) {
            fastPlayer.setChatSetKey(false);
            openLastClosedInventory(fastPlayer, player, file);
        }
        String path = fastPlayer.getPath();
        if (path == null) {
            player.sendMessage(Messages.INVALID_PATH.getMessage());
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (fastPlayer.isChatAddKey()) {
            config.set(path.isEmpty() ? message : path + "." + message, "");
        } else if (fastPlayer.isChatSetKey()) {
            config.set(path, convert(message));
        }
        FileUtils.reload(config, file);
        fastPlayer.setChatAddKey(false);
        fastPlayer.setChatSetKey(false);
        openLastClosedInventory(fastPlayer, player, file);
        event.setCancelled(true);
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
        } else if (isMessageQuotes(message)) {
            return message.substring(1, message.length() - 1);
        } else if (NumberUtils.isNumber(message)) {
            return NumberUtils.createNumber(message);
        } else if ("true".equalsIgnoreCase(message)) {
            return true;
        } else if ("false".equalsIgnoreCase(message)) {
            return false;
        }
        return message;
    }

    public boolean isMessageQuotes(String message) {
        String doubleQuotes = "\"";
        String quotes = "'";
        return message.startsWith(doubleQuotes) && message.endsWith(doubleQuotes) || message.startsWith(quotes) && message.endsWith(quotes);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FastPluginConfigurer.removePlayer(event.getPlayer().getUniqueId());
    }
}
