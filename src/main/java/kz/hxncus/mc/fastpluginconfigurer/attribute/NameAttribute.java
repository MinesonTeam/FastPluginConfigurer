package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.util.StringUtils;

public class NameAttribute implements Attribute {
    @Override
    public String apply(ConfigItem item) {
        return StringUtils.isEmpty(item.getName()) ? null : item.getName();
    }
}
