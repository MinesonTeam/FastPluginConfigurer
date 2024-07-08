package kz.hxncus.mc.fastpluginconfigurer.config;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum ConfigMaterial {
    BOOKSHELF(Iterable.class), ITEM_FRAME(Enum.class), MAGMA_CREAM(Boolean.class),
    GOLD_NUGGET(Integer.class), PAPER(String.class), SLIME_BALL(Boolean.class);
    final Material material;
    final Class<?> clazz;

    ConfigMaterial(Class<?> clazz) {
        this.material = Material.valueOf(name());
        this.clazz = clazz;
    }
}
