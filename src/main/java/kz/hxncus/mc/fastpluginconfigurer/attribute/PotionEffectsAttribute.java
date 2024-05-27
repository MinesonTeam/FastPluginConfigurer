package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.function.Function;

public class PotionEffectsAttribute implements Attribute {
    Function<List<PotionEffect>, List<String>> function;

    public PotionEffectsAttribute(Function<List<PotionEffect>, List<String>> function) {
        this.function = function;
    }

    @Override
    public List<String> apply(ConfigItem item) {
        List<String> applied = function.apply(item.getPotionEffects());
        return applied.isEmpty() ? null : applied;
    }
}
