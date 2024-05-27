package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

public class SkullOwnerAttribute implements Attribute {

    @Override
    public String apply(ConfigItem item) {
        return item.getSkullOwner();
    }
}
