package kz.hxncus.mc.fastpluginconfigurer.material;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum MaterialValues {
    BOOKSHELF(Iterable.class), ITEM_FRAME(Enum.class), MAGMA_CREAM(Boolean.class),
    GOLD_NUGGET(Integer.class), PAPER(String.class), SLIME_BALL(Boolean.class);
    final Material material;
    final Class<?> clazz;

    MaterialValues(Class<?> clazz) {
        this.material = Material.valueOf(name());
        this.clazz = clazz;
    }
}
