package kz.hxncus.mc.fastpluginconfigurer.fast;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.UUID;

@Data
public class FastPlayer {
    private final UUID uuid;
    private String path;
    private String lastPluginName;
    private boolean chatSetKey;
    private boolean chatAddKey;
    private BukkitTask chatTask;
    private File file;

    public void setChatSetKey(boolean chatSetKey, Plugin plugin) {
        this.chatSetKey = chatSetKey;
        if (chatSetKey) {
            setChatTask(Bukkit.getScheduler().runTaskLater(plugin, () -> setChatSetKey(false), 1200L));
        } else {
            getChatTask().cancel();
        }
    }

    public void setChatSetKey(boolean chatSetKey) {
        this.chatSetKey = chatSetKey;
        if (!chatSetKey) {
            getChatTask().cancel();
        }
    }

    public void setChatAddKey(boolean chatAddKey, Plugin plugin) {
        this.chatSetKey = chatAddKey;
        if (chatAddKey) {
            setChatTask(Bukkit.getScheduler().runTaskLater(plugin, () -> setChatSetKey(false), 1200L));
        } else {
            getChatTask().cancel();
        }
    }

    public void setChatAddKey(boolean chatAddKey) {
        this.chatSetKey = chatAddKey;
        if (!chatAddKey) {
            getChatTask().cancel();
        }
    }
}
