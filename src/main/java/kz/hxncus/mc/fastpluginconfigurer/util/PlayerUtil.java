package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

@UtilityClass
public class PlayerUtil {
    public Block getBlockPlayerLookingAt(Player player, int distance) {
        BlockIterator iterator = new BlockIterator(player, distance);
        Block lastBlock = iterator.next();
        while (iterator.hasNext()) {
            if (lastBlock.getType() != Material.AIR) {
                break;
            }
            lastBlock = iterator.next();
        }
        return lastBlock;
    }
}
