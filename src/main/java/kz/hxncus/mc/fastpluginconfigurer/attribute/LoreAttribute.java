package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

import java.util.List;

public class LoreAttribute implements Attribute {

    @Override
    public List<String> apply(ConfigItem item) {
        return item.getLore();
    }
}
