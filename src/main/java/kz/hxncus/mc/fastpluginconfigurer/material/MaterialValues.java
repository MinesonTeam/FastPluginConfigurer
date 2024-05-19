package kz.hxncus.mc.fastpluginconfigurer.material;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum MaterialValues {
    STRING(Material.STRING, String.class), ENDER_PEARL(Material.ENDER_PEARL, int.class), ITEM_FRAME(Material.ITEM_FRAME, Enum.class),
    SLIME_BALL(Material.SLIME_BALL, boolean.class), MAGMA_CREAM(Material.MAGMA_CREAM, boolean.class), BOOKSHELF(Material.BOOKSHELF, Iterable.class);
    final Material material;
    final Class<?> clazz;
    MaterialValues(Material material, Class<?> clazz) {
        this.material = material;
        this.clazz = clazz;
    }
}
