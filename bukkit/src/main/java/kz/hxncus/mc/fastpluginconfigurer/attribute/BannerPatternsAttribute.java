package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import org.bukkit.block.banner.Pattern;

import java.util.List;
import java.util.function.Function;

public class BannerPatternsAttribute implements Attribute {
    private final Function<List<Pattern>, List<String>> function;

    public BannerPatternsAttribute(Function<List<Pattern>, List<String>> function) {
        this.function = function;
    }

    @Override
    public List<String> apply(ConfigItem item) {
        List<String> applied = function.apply(item.getBannerPatterns());
        return applied.isEmpty() ? null : applied;
    }
}
