package kz.hxncus.mc.fastpluginconfigurer.listener;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
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
import java.util.HashMap;
import java.util.Map;


public class PlayerListener implements Listener {
    private final FastPluginConfigurer plugin;

    public PlayerListener(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    private void openLastClosedInventory(FastPlayer fastPlayer, Player player, File file) {
        String pluginName = fastPlayer.getLastPluginName();
        if (pluginName != null) {
            Bukkit.getScheduler().runTask(plugin,
                    () -> Bukkit.dispatchCommand(player, String.format("fpc config %s %s", pluginName, file.getName())));
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FastPlayer fastPlayer = FastPluginConfigurer.getFastPlayer(player.getUniqueId());
        String message = event.getMessage();
        if (!fastPlayer.isChatAddKey() && !fastPlayer.isChatSetKey()) {
            return;
        }
        File file = fastPlayer.getFile();
        if (!file.exists()) {
            try {
                new File(file.getParentFile().getPath()).mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (message.equalsIgnoreCase("cancel")) {
            fastPlayer.setChatSetKey(false);
            openLastClosedInventory(fastPlayer, player, file);
        }
        String path = fastPlayer.getPath();
        if (path == null) {
            player.sendMessage("Invalid path.");
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (fastPlayer.isChatAddKey()) {
            config.set(path.isEmpty() ? message : path + "." + message, "");
        } else if (fastPlayer.isChatSetKey()) {
            config.set(path, convert(message));
        }
        FileUtils.reload(config, file);
        fastPlayer.setChatSetKey(false);
        openLastClosedInventory(fastPlayer, player, file);
        event.setCancelled(true);
    }

    private Object convert(String message) {
        if (message.equals("null")) {
            return null;
        }
        if (message.startsWith("{") && message.endsWith("}")) {
            Map<String, Object> objectMap = new HashMap<>();
            String[] split = message.substring(1, message.length() - 1).split("(?<=^|,)\\s*(?:\\{([^:]+):([^,\\]]+)}|([^:]+):\\[([^]]+)])");
            for (String messages : split) {
                String[] splitted = messages.split(":");
                objectMap.put(splitted[0], convert(splitted[1]));
            }
            return objectMap;
        }
        if (message.startsWith("[") && message.endsWith("]")) {
            ArrayList<Object> objects = new ArrayList<>();
            for (String messages : message.substring(1, message.length() - 1).split(", ")) {
                objects.add(convert(messages));
            }
            return objects;
        }
        if ((message.startsWith("\"") && message.endsWith("\"")) || (message.startsWith("'") && message.endsWith("'"))) {
            return message.substring(1, message.length() - 1);
        }
        if (NumberUtils.isNumber(message)) {
            return NumberUtils.createNumber(message);
        }
        if (message.equalsIgnoreCase("true")) {
            return true;
        }
        if (message.equalsIgnoreCase("false")) {
            return false;
        }
        return message;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FastPluginConfigurer.removePlayer(event.getPlayer().getUniqueId());
    }
}
