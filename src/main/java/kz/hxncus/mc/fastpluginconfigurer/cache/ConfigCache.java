package kz.hxncus.mc.fastpluginconfigurer.cache;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.UUID;

@Data
public class ConfigCache {
    private final UUID uuid;
    private String keyPath;
    private String pluginName;
    private ChatState chatState;
    private BukkitTask chatReturnTask;
    private File file;

    public void cancelTask() {
        chatReturnTask.cancel();
    }
    
    public void setChatState(ChatState chatState) {
        this.chatState = chatState;
        if (chatState == ChatState.NOTHING) {
            cancelTask();
        } else {
            setChatReturnTask(Bukkit.getScheduler().runTaskLater(FastPluginConfigurer.get(), () -> this.chatState = ChatState.NOTHING, 1200L));
        }
    }

    public enum ChatState {
        NOTHING, ADDING_NEW_KEY, SETTING_KEY_VALUE
    }
}
