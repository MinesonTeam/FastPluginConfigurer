package kz.hxncus.mc.fastpluginconfigurer.hook;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.config.ConfigItem;
import kz.hxncus.mc.fastpluginconfigurer.util.Constants;
import kz.hxncus.mc.fastpluginconfigurer.util.FileUtil;
import kz.hxncus.mc.fastpluginconfigurer.util.Messages;
import kz.hxncus.mc.fastpluginconfigurer.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public abstract class AbstractHook implements Convertible {
    public final FastPluginConfigurer plugin;

    protected AbstractHook(FastPluginConfigurer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void convertInventoryToFile(Player player, String fileName) {
        File file = new File(plugin.getDirectoryManager().getConvertedDir(), fileName.endsWith(Constants.YML_EXPANSION) ? fileName : fileName + Constants.YML_EXPANSION);
        if (file.exists()) {
            Messages.FILE_ALREADY_EXISTS.sendMessage(player, fileName);
            return;
        }
        Block targetBlock = VersionUtil.getTargetBlock(player, 5);
        BlockState state = targetBlock == null ? null : targetBlock.getState();
        if (!(state instanceof Chest)) {
            Messages.MUST_LOOKING_AT_DOUBLE_CHEST.sendMessage(player);
            return;
        }
        Inventory chestInventory = ((Chest) state).getInventory();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configureInventory(fileName, config, chestInventory);
        int count = 0;
        for (int i = 0; i < chestInventory.getSize(); i++) {
            ItemStack item = chestInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            storeItemInConfig(config, new ConfigItem(item, i), count++);
        }
        FileUtil.reload(config, file);
        Messages.CHEST_SUCCESSFULLY_STORED_INTO_FILE.sendMessage(player, fileName);
    }
}
