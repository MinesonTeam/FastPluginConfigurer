package kz.hxncus.mc.fastpluginconfigurer.listener;

import kz.hxncus.mc.fastpluginconfigurer.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.command.FastPluginConfigurerCommand;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtil;
import org.apache.commons.lang.StringUtils;
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
    @EventHandler
    public void onPlayerWriteConfigValue(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FastPlayer fastPlayer = FastPlayer.getFastPlayer(player.getUniqueId());
        String message = event.getMessage();
        if (!fastPlayer.isChatSetKey()) {
            return;
        }
        if (message.equalsIgnoreCase("cancel")) {
            resetChatSetting(fastPlayer, player);
        }
        String path = fastPlayer.getPath();
        if (StringUtils.isEmpty(path)) {
            player.sendMessage("Invalid path.");
            return;
        }
        String dataFolderPath = fastPlayer.getDataFolderPath();
        if (StringUtils.isEmpty(dataFolderPath)) {
            player.sendMessage("Invalid dataFolderPath.");
            return;
        }
        File file = new File(dataFolderPath + "/config.yml");
        if (!file.exists()) {
            try {
                new File(dataFolderPath).mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, convert(message));
        FileUtil.reload(config, file);

        resetChatSetting(fastPlayer, player);
        event.setCancelled(true);
    }

    private static void resetChatSetting(FastPlayer fastPlayer, Player player) {
        fastPlayer.setChatSetKey(false);
        fastPlayer.getChatTask().cancel();

        String pluginName = fastPlayer.getLastPluginName();
        if (pluginName != null) {
            Bukkit.getScheduler().runTask(FastPluginConfigurer.getInstance(),
                    () -> FastPluginConfigurerCommand.configSubCommand(player, pluginName));
        }
    }

    @EventHandler
    public void onPlayerWriteConfigKey(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FastPlayer fastPlayer = FastPlayer.getFastPlayer(player.getUniqueId());
        String message = event.getMessage();
        if (!fastPlayer.isChatAddKey() || message.equalsIgnoreCase("cancel")) {
            fastPlayer.setChatAddKey(false);
            return;
        }
        String path = fastPlayer.getPath();
        if (StringUtils.isEmpty(path)) {
            player.sendMessage("Invalid path.");
            return;
        }
        String dataFolderPath = fastPlayer.getDataFolderPath();
        if (StringUtils.isEmpty(dataFolderPath)) {
            player.sendMessage("Invalid dataFolderPath.");
            return;
        }
        File file = new File(dataFolderPath + "/config.yml");
        if (!file.exists()) {
            try {
                new File(dataFolderPath).mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path + "." + event.getMessage(), "");
        FileUtil.reload(config, file);

        fastPlayer.setChatAddKey(false);
        fastPlayer.getChatTask().cancel();

        String pluginName = fastPlayer.getLastPluginName();
        if (pluginName != null) {
            Bukkit.getScheduler().runTask(FastPluginConfigurer.getInstance(),
                    () -> FastPluginConfigurerCommand.configSubCommand(player, pluginName));
        }
        event.setCancelled(true);
    }

    private Object convert(String message) {
        if (message.equals("null")) {
            return null;
        }
        String[] split = message.substring(1, message.length() - 1).split(", ");
        if (message.startsWith("{") && message.endsWith("}")) {
            Map<String, Object> objectMap = new HashMap<>();
            for (String messages : split) {
                String[] splitted = messages.split(":");
                objectMap.put(splitted[0], convert(splitted[1]));
            }
            return objectMap;
        }
        if (message.startsWith("[") && message.endsWith("]")) {
            ArrayList<Object> objects = new ArrayList<>();
            for (String messages : split) {
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
        FastPlayer.removePlayer(event.getPlayer().getUniqueId());
    }
}
