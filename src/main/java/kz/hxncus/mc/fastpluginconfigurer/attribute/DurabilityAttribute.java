package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

public class DurabilityAttribute implements Attribute {
    @Override
    public Integer apply(ConfigItem item) {
        return item.getDurability() == 0 ? null : item.getDurability();
    }
}
