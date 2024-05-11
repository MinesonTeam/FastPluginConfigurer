package kz.hxncus.mc.fastpluginconfigurer;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Data
public class FastPlayer {
    private final UUID uuid;
    private BukkitTask chatTask;
    private boolean chatSetKey;
    private boolean chatAddKey;
    private String path;
    private String dataFolderPath;
}
