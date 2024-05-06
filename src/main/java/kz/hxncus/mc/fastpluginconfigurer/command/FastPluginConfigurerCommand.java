package kz.hxncus.mc.fastpluginconfigurer.command;

import com.extendedclip.deluxemenus.menu.Menu;
import com.google.common.collect.Lists;
import kz.hxncus.mc.fastpluginconfigurer.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.InventoryConverter;
import kz.hxncus.mc.fastpluginconfigurer.inventory.FastInventory;
import kz.hxncus.mc.fastpluginconfigurer.inventory.UsualFastInventory;
import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.NonNull;
import me.filoghost.chestcommands.fcommons.collection.CaseInsensitiveString;
import me.filoghost.chestcommands.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FastPluginConfigurerCommand extends DefaultCommand {
    private final FastPluginConfigurer instance = FastPluginConfigurer.getInstance();
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
            sendHelpMessage(sender);
            return;
        }
        String args0 = args[0].toLowerCase();
        if (args.length == 1) {
            sendHelpMessage(sender);
            return;
        }
        String args1 = args[1].toLowerCase();
        switch (args0) {
            case "inventorytofile":
            case "filetoinventory":
                inventorySubCommand(sender, args, args0, args1);
                break;
            case "config":
                configSubCommand(sender, args1);
                break;
            default:
                sendHelpMessage(sender);
                break;
        }
    }

    private void inventorySubCommand(CommandSender sender, String[] args, String args0, String args1) {
        if (args.length == 2) {
            sendHelpMessage(sender);
            return;
        }
        String args2 = args[2];
        InventoryConverter converter;
        switch (args1) {
            case "dm":
            case "deluxemenus":
                converter = instance.getDeluxeMenusConverter();
                break;
            case "cc":
            case "chestcommands":
                converter = instance.getChestCommandsConverter();
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
        if (args0.equals("inventorytofile")) {
            converter.inventoryToFile(player, args2);
        } else if (args0.equals("filetoinventory")) {
            converter.fileToInventory(player, args2);
        }
    }

    public static void configSubCommand(CommandSender sender, String args1) {
        Plugin plugin = FastPluginConfigurer.getPlugins().get(args1);
        if (plugin == null) {
            sender.sendMessage("This plugin is not exists.");
            return;
        }
        plugin.reloadConfig();

        FastInventory fastInventory = new UsualFastInventory(54, args1);
        fastInventory.addClickHandler(event -> event.setCancelled(true));

        HumanEntity humanEntity = (HumanEntity) sender;
        FastPlayer fastPlayer = FastPlayer.getFastPlayer(humanEntity.getUniqueId());
        fastPlayer.setPath(plugin.getDataFolder().getPath());
        fastPlayer.setLastPluginName(plugin.getName().toLowerCase());

        FileConfiguration config = plugin.getConfig();
        setupConfigInventories(fastPlayer, fastInventory, config, config.getKeys(false).iterator());
        fastInventory.open(humanEntity);
    }

    private static void setupConfigInventories(FastPlayer fastPlayer, FastInventory inventory, @NonNull ConfigurationSection section, Iterator<String> iterator) {
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (inventory.firstEmpty() > 44) {
                FastInventory fastInventory = new UsualFastInventory(54, fastPlayer.getLastPluginName());
                fastInventory.addClickHandler(event -> event.setCancelled(true));
                inventory.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("Next page").build(), event -> fastInventory.open(event.getWhoClicked()));
                fastInventory.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build(), event -> inventory.open(event.getWhoClicked()));
                setupConfigInventories(fastPlayer, fastInventory, section, iterator);
                break;
            }
            setupKey(fastPlayer, inventory, section, key);
        }
        if (inventory.firstEmpty() <= 44) {
            inventory.addItem(new ItemBuilder(Material.NETHER_STAR).setDisplayName("§aAdd a new key").build(), event -> {

            });
        }
    }

    private static void setupKey(FastPlayer fastPlayer, FastInventory inventory, ConfigurationSection section, String key) {
        ConfigurationSection sections = section.getConfigurationSection(key);
        if (sections != null) {
            FastInventory fastInventory = new UsualFastInventory(54, fastPlayer.getLastPluginName());
            fastInventory.addClickHandler(event -> event.setCancelled(true));
            inventory.addItem(new ItemBuilder(Material.OAK_SIGN).setDisplayName("§f" + key).build(), event -> fastInventory.open(event.getWhoClicked()));
            fastInventory.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build(), event -> inventory.open(event.getWhoClicked()));
            setupConfigInventories(fastPlayer, fastInventory, sections, sections.getKeys(false).iterator());
        } else {
            Object value = section.get(key);
            int amount = 1;
            Material material;
            if (value instanceof String) {
                material = Material.STRING;
            } else if (value instanceof Number) {
                amount = ((Number) value).intValue();
                material = Material.ENDER_PEARL;
            } else if (value instanceof Enum) {
                material = Material.ITEM_FRAME;
            } else if (value instanceof Boolean) {
                boolean bool = (boolean) value;
                material = bool ? Material.SLIME_BALL : Material.MAGMA_CREAM;
            } else if (value instanceof Iterable<?>) {
                material = Material.BOOKSHELF;
            } else {
                material = Material.BEDROCK;
            }
            inventory.addItem(new ItemBuilder(material).setDisplayName("§f" + key).addLore("§f" + section.get(key)).setAmount(amount).build(), event -> {
                HumanEntity humanEntity = event.getWhoClicked();
                humanEntity.closeInventory();

                String fullKey = section.getCurrentPath() + "." + key;

                FastPlayer player = FastPlayer.getFastPlayer(humanEntity.getUniqueId());
                player.setChat(true);
                player.setKey(fullKey);
                Bukkit.getScheduler().runTaskLater(FastPluginConfigurer.getInstance(), () -> player.setChat(false), 600L);
                humanEntity.sendMessage("Write a value for key §e" + fullKey + "§r in the chat");
            });
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("Help:");
        sender.sendMessage("/fastpluginconfigurer ");
        sender.sendMessage("/fastpluginconfigurer inventorytofile <converter> <filename>");
        sender.sendMessage("/fastpluginconfigurer filetoinventory <converter> <filename>");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("config", "inventorytofile", "filetoinventory");
        }
        String method = args[0].toLowerCase();
        if (args.length == 2 && (method.equals("inventorytofile") || method.equals("filetoinventory"))) {
            return Lists.newArrayList("deluxemenus", "chestcommands");
        } else if (args.length == 2 && (method.equals("config"))) {
            return new ArrayList<>(FastPluginConfigurer.getPlugins().keySet());
        }
        String converter = args[1].toLowerCase();
        if (FastPluginConfigurer.getPlugins().containsKey("deluxemenus") && args.length == 3
                && (converter.equals("deluxemenus") || converter.equals("dm")) && method.equals("filetoinventory")) {
            return Menu.getAllMenus().stream().map(Menu::getMenuName).collect(Collectors.toList());
        } else if (FastPluginConfigurer.getPlugins().containsKey("chestcommands") && args.length == 3
                && (converter.equals("chestcommands") || converter.equals("cc")) && method.equals("filetoinventory")) {
            return MenuManager.getMenuFileNames().stream().map(CaseInsensitiveString::toString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
