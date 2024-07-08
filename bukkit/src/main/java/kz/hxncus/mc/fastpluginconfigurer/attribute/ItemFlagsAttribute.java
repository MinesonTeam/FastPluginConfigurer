package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemFlagsAttribute implements Attribute {
    @Override
    public List<String> apply(ConfigItem item) {
        Set<ItemFlag> itemFlags = item.getItemFlags();
        return itemFlags.isEmpty() ? null : itemFlags.stream().map(ItemFlag::name).collect(Collectors.toList());
    }
}
