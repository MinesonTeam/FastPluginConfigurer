package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

public class MaterialAttribute implements Attribute {
    @Override
    public String apply(ConfigItem item) {
        return item.getMaterial().name();
    }
}
