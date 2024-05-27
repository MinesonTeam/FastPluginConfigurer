package kz.hxncus.mc.fastpluginconfigurer.config;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.UUID;

@Data
public class ConfigSession {
    private final UUID uuid;
    private String keyPath;
    private String pluginName;
    private Chat chat;
    private BukkitTask chatTask;
    private File file;

    public void cancelTask() {
        chatTask.cancel();
    }

    public void setChat(Chat chat) {
        if (chat == Chat.NOTHING) {
            cancelTask();
        } else {
            setChatTask(Bukkit.getScheduler().runTaskLater(FastPluginConfigurer.getInstance(),
                                           () -> this.chat = Chat.NOTHING, 1200L));
        }
        this.chat = chat;
    }

    public enum Chat {
        NOTHING, ADDING_NEW_KEY, SETTING_KEY_VALUE
    }
}
