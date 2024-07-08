package kz.hxncus.mc.fastpluginconfigurer.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@EqualsAndHashCode
public class CacheManager {
    private final Map<UUID, ConfigCache> configCacheMap = new ConcurrentHashMap<>();

    public ConfigCache getConfigCache(final UUID uuid) {
        return configCacheMap.computeIfAbsent(uuid, ConfigCache::new);
    }
    public ConfigCache removeCache(final UUID uuid) {
        return configCacheMap.remove(uuid);
    }
}
