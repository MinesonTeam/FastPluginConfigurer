package kz.hxncus.mc.fastpluginconfigurer.command;

import com.extendedclip.deluxemenus.menu.Menu;
import com.google.common.collect.Lists;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.InventoryConverter;
import me.filoghost.chestcommands.fcommons.collection.CaseInsensitiveString;
import me.filoghost.chestcommands.menu.MenuManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FastPluginConfigurerCommand extends DefaultCommand {
    public FastPluginConfigurerCommand() {
        super("fastpluginconfigurer");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return;
        }
        if (args.length == 0) {
            sender.sendMessage("Help:");
            sender.sendMessage("/fastpluginconfigurer DeluxeMenus inventorytofile");
            sender.sendMessage("/fastpluginconfigurer ChestCommands filetoinventory");
            return;
        }
        String args0 = args[0].toLowerCase();
        if (args.length == 1 && (args0.equals("cc") || args0.equals("chestcommands") || args0.equals("dm") || args0.equals("deluxemenus"))) {
            sender.sendMessage("/fastpluginconfigurer " + args0 + " inventorytofile");
            sender.sendMessage("/fastpluginconfigurer " + args0 + " filetoinventory");
            return;
        } else if (args.length == 1) {
            sender.sendMessage("/fastpluginconfigurer chestcommands");
            sender.sendMessage("/fastpluginconfigurer cc");
            sender.sendMessage("/fastpluginconfigurer deluxemenus");
            sender.sendMessage("/fastpluginconfigurer dm");
            return;
        }
        String args1 = args[1].toLowerCase();
        if (args.length == 2 && (args1.equals("inventorytofile") || args1.equals("filetoinventory"))) {
            sender.sendMessage("/fastpluginconfigurer " + args0 + " " + args1 + " fileName");
            return;
        } else if (args.length == 2) {
            sender.sendMessage("/fastpluginconfigurer " + args0 + " inventorytofile");
            sender.sendMessage("/fastpluginconfigurer " + args0 + " filetoinventory");
            return;
        }
        String args2 = args[2];
        InventoryConverter converter;
        switch (args0) {
            case "dm":
            case "deluxemenus":
                converter = FastPluginConfigurer.getInstance().getDeluxeMenusConverter();
                break;
            case "cc":
            case "chestcommands":
                converter = FastPluginConfigurer.getInstance().getChestCommandsConverter();
                break;
            default:
                converter = null;
                break;
        }
        if (converter == null) {
            sender.sendMessage("This converter type is not exists.");
            return;
        }
        Player player = (Player) sender;
        if (args1.equals("inventorytofile")) {
            converter.inventoryToFile(player, args2);
        } else if (args1.equals("filetoinventory")) {
            converter.fileToInventory(player, args2);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("dm", "deluxemenus", "cc", "chestcommands");
        }
        String converter = args[0].toLowerCase();
        if (args.length == 2 && (converter.equals("deluxemenus") || converter.equals("chestcommands") || converter.equals("dm") || converter.equals("cc"))) {
            return Lists.newArrayList("inventorytofile", "filetoinventory");
        }
        String method = args[1].toLowerCase();
        if (args.length == 3 && (converter.equals("deluxemenus") || converter.equals("dm")) && method.equals("filetoinventory")) {
            return Menu.getAllMenus().stream().map(Menu::getMenuName).collect(Collectors.toList());
        } else if (args.length == 3 && (converter.equals("chestcommands") || converter.equals("cc")) && method.equals("filetoinventory")) {
            return MenuManager.getMenuFileNames().stream().map(CaseInsensitiveString::toString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
