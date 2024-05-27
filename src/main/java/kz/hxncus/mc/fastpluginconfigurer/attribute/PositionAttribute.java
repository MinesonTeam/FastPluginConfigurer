package kz.hxncus.mc.fastpluginconfigurer.attribute;

import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;

import java.util.function.UnaryOperator;

public class PositionAttribute implements Attribute {
    UnaryOperator<Integer> function;

    public PositionAttribute(UnaryOperator<Integer> function) {
        this.function = function;
    }

    @Override
    public Integer apply(ConfigItem item) {
        return function.apply(item.getIndex());
    }
}
