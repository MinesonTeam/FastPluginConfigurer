package kz.hxncus.mc.fastpluginconfigurer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class FastPlayer {
    protected static final Map<UUID, FastPlayer> FAST_PLAYER_MAP = new HashMap<>();
    public static FastPlayer getFastPlayer(UUID uuid) {
        return FAST_PLAYER_MAP.computeIfAbsent(uuid, uuids -> new FastPlayer(uuids, null, false, null, null));
    }
    public static FastPlayer removePlayer(UUID uuid) {
        return FAST_PLAYER_MAP.remove(uuid);
    }
    private final UUID uuid;
    private String lastPluginName;
    private boolean chat;
    private String key;
    private String path;
}
