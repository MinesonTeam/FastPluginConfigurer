package kz.hxncus.mc.fastpluginconfigurer;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class FastPlayer {
    private static final Map<UUID, FastPlayer> FAST_PLAYER_MAP = new HashMap<>();

    public static FastPlayer getFastPlayer(UUID uuid) {
        return FAST_PLAYER_MAP.computeIfAbsent(uuid, FastPlayer::new);
    }

    public static FastPlayer removePlayer(UUID uuid) {
        return FAST_PLAYER_MAP.remove(uuid);
    }

    private final UUID uuid;
    private BukkitTask chatTask;
    private String lastPluginName;
    private boolean chatSetKey;
    private boolean chatAddKey;
    private String path;
    private String dataFolderPath;
}
