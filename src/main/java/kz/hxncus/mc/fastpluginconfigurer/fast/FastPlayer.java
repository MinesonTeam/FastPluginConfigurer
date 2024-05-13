package kz.hxncus.mc.fastpluginconfigurer.fast;

import lombok.Data;
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
}
