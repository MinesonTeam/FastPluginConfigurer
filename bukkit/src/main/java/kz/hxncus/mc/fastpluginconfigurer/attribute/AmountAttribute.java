package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

public class AmountAttribute implements Attribute {
    @Override
    public Integer apply(ConfigItem item) {
        return item.getAmount();
    }
}
