package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import org.bukkit.Color;

import java.util.function.Function;

public class RGBAttribute implements Attribute {
    Function<Color, String> function;

    public RGBAttribute(Function<Color, String> function) {
        this.function = function;
    }

    @Override
    public String apply(ConfigItem item) {
        return item.getRGB() == null ? null : function.apply(item.getRGB());
    }
}
