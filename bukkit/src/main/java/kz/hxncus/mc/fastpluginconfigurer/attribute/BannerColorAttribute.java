package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

public class BannerColorAttribute implements Attribute {
    @Override
    public String apply(ConfigItem item) {
        return item.getDyeColor() == null ? null : item.getDyeColor().name();
    }
}
