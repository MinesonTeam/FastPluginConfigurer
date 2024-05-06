package kz.hxncus.mc.fastpluginconfigurer.listener;

import kz.hxncus.mc.fastpluginconfigurer.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.command.FastPluginConfigurerCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
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
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FastPlayer fastPlayer = FastPlayer.getFastPlayer(player.getUniqueId());
        if (fastPlayer.isChat()) {
            String path = fastPlayer.getPath();
            if (path == null || path.isEmpty()) {
                player.sendMessage("Invalid path.");
                return;
            }
            String key = fastPlayer.getKey();
            if (key == null || key.isEmpty()) {
                player.sendMessage("Invalid key.");
                return;
            }
            File file = new File(path + "/config.yml");
            if (!file.exists()) {
                player.sendMessage("Config with this path is not exists.");
                return;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(key, convert(event.getMessage()));
            try {
                config.save(file);
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                player.sendMessage("Error while trying to save the configuration");
                return;
            }
            fastPlayer.setChat(false);
            String pluginName = fastPlayer.getLastPluginName();
            if (pluginName != null) {
                Bukkit.getScheduler().runTask(FastPluginConfigurer.getInstance(),
                        () -> FastPluginConfigurerCommand.configSubCommand(player, pluginName));
            }
        }
    }

    private Object convert(String message) {
        if (message.equals("null")) {
            return null;
        } else if (message.startsWith("[") && message.endsWith("]")) {
            ArrayList<Object> objects = new ArrayList<>();
            for (String messages : message.substring(1, message.length() - 1).split(", ")) {
                objects.add(convert(messages));
            }
            return objects;
        } else if (message.startsWith("{") && message.endsWith("}")) {
            Map<String, Object> objectMap = new HashMap<>();
            for (String messages : message.substring(1, message.length() - 1).split(", ")) {
                String[] splitted = messages.split(":");
                objectMap.put(splitted[0], convert(splitted[1]));
            }
            return objectMap;
        } else if (message.startsWith("\"") && message.endsWith("\"") || message.startsWith("'") && message.endsWith("'")) {
            return message.substring(1, message.length() - 1);
        } else if (NumberUtils.isNumber(message)) {
            return NumberUtils.createNumber(message);
        } else if (message.equalsIgnoreCase("true") || message.equalsIgnoreCase("false")) {
            return Boolean.valueOf(message);
        } else {
            return message;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FastPlayer.removePlayer(event.getPlayer().getUniqueId());
    }
}
