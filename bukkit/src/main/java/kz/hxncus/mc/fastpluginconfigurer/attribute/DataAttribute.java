package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

public class DataAttribute implements Attribute {
    @Override
    public Byte apply(ConfigItem item) {
        return item.getData() == 0 ? null : item.getData();
    }
}
