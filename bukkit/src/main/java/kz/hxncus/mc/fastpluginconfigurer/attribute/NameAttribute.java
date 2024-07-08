package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.util.StringUtil;

public class NameAttribute implements Attribute {
    @Override
    public String apply(ConfigItem item) {
        return StringUtil.isEmpty(item.getName()) ? null : item.getName();
    }
}
