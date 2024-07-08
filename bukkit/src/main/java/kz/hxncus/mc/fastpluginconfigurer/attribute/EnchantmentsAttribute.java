package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EnchantmentsAttribute implements Attribute {
    private final Function<Map<Enchantment, Integer>, List<String>> function;

    public EnchantmentsAttribute(Function<Map<Enchantment, Integer>, List<String>> function) {
        this.function = function;
    }

    @Override
    public List<String> apply(ConfigItem item) {
        List<String> applied = function.apply(item.getEnchantments());
        return applied.isEmpty() ? null : applied;
    }
}
