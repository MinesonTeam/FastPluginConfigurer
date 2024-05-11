package kz.hxncus.mc.fastpluginconfigurer.command;

import com.extendedclip.deluxemenus.menu.Menu;
import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.hook.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.inventory.BasicFastInventory;
import kz.hxncus.mc.fastpluginconfigurer.inventory.FastInventory;
import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import me.filoghost.chestcommands.fcommons.collection.CaseInsensitiveString;
import me.filoghost.chestcommands.menu.MenuManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FastPluginConfigurerCommand extends DefaultCommand {
    public FastPluginConfigurerCommand(FastPluginConfigurer plugin) {
        super(plugin, "fastpluginconfigurer");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return;
        }
        if (args.length == 0) {
            sendHelpMessage(sender, label);
            return;
        }
        String args0 = args[0].toLowerCase();
        if (args.length == 1) {
            sendHelpMessage(sender, label);
            return;
        }
        switch (args0) {
            case "inventorytofile":
            case "filetoinventory":
                if (args.length == 2) {
                    sendHelpMessage(sender, label);
                    return;
                }
                inventorySubCommand(sender, args, args0);
                break;
            case "config":
                configSubCommand((HumanEntity) sender, args);
                break;
            default:
                sendHelpMessage(sender, label);
                break;
        }
    }

    private void inventorySubCommand(CommandSender sender, String[] args, String args0) {
        String args2 = args[2];
        Convertible converter;
        switch (args[1]) {
            case "deluxemenus":
                converter = plugin.getDeluxeMenusHook();
                break;
            case "chestcommands":
                converter = plugin.getChestCommandsHook();
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

    public void configSubCommand(HumanEntity humanEntity, String[] args) {
        Plugin targetPlugin = plugin.getPluginManager().getPlugin(args[1]);
        if (targetPlugin == null) {
            humanEntity.sendMessage("This plugin doesn't exist.");
            return;
        }
        targetPlugin.reloadConfig();

        FastInventory fastInventory = new BasicFastInventory(plugin, 54, args[1]);
        fastInventory.addClickHandler(event -> event.setCancelled(true));

        FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId()).setDataFolderPath(targetPlugin.getDataFolder().getPath());

        FileConfiguration config = targetPlugin.getConfig();
        setupConfigInventories(config.getKeys(false).iterator(), fastInventory, config, targetPlugin.getName());
        fastInventory.open(humanEntity);
    }

    private void setupConfigInventories(Iterator<String> iterator, FastInventory inventory, ConfigurationSection section, String lastPluginName) {
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (inventory.firstEmpty() > 44) {
                FastInventory fastInventory = new BasicFastInventory(plugin, 54, lastPluginName);
                fastInventory.addClickHandler(event -> event.setCancelled(true));

                setNextAndPreviousPages(inventory, fastInventory);

                setupConfigInventories(iterator, fastInventory, section, lastPluginName);
                break;
            }
            setupKey(lastPluginName, inventory, section, path);
        }
        addNewKeyItem(inventory, section.getCurrentPath());
    }

    private void setNextAndPreviousPages(FastInventory currentInv, FastInventory nextInv) {
        currentInv.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("Next page").build(), event -> nextInv.open(event.getWhoClicked()));
        setPreviousPage(nextInv, currentInv);
    }
    
    private void setPreviousPage(FastInventory currentInv, FastInventory previousInv) {
        currentInv.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build(), event -> previousInv.open(event.getWhoClicked()));
    }
    
    private void addNewKeyItem(FastInventory inventory, String currentPath) {
        if (inventory.firstEmpty() <= 44) {
            inventory.addItem(new ItemBuilder(Material.NETHER_STAR).setDisplayName("§fClick to add a new key")
                                                                   .build(), event -> {
                HumanEntity humanEntity = event.getWhoClicked();
                humanEntity.closeInventory();

                FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
                player.setChatAddKey(true);
                player.setPath(currentPath);
                player.setChatTask(Bukkit.getScheduler()
                                         .runTaskLater(plugin, () -> player.setChatAddKey(false), 600L));
                humanEntity.sendMessage("Write down the new key in the chat." + (StringUtils.isEmpty(currentPath) ? "" : " Path: " + currentPath));
            });
        }
    }

    private void setupKey(String lastPluginName, FastInventory inventory, ConfigurationSection section, String path) {
        ConfigurationSection sections = section.getConfigurationSection(path);
        String currentPath = section.getCurrentPath();
        String fullPath = StringUtils.isEmpty(currentPath) ? path : currentPath + "." + path;
        if (sections != null) {
            FastInventory fastInventory = new BasicFastInventory(plugin, 54, lastPluginName);
            fastInventory.addClickHandler(event -> event.setCancelled(true));
            inventory.addItem(new ItemBuilder(Material.OAK_SIGN)
                    .setDisplayName("§fSection: §e" + path)
                    .addLore(Constants.ADDITIONAL_SECTION_LORE)
                    .build(), event -> onSectionClick(event, fastInventory, fullPath));
            setPreviousPage(fastInventory, inventory);
            setupConfigInventories(sections.getKeys(false).iterator(), fastInventory, sections, lastPluginName);
            return;
        }
        Object value = section.get(path, "");
        String valueString = value == null ? "" : value.toString();
        inventory.addItem(new ItemBuilder(getMaterialFromValue(value))
                              .setDisplayName("§fKey: §e" + fullPath)
                              .addLore("§7Current value:", " §8▪ §e" + (StringUtils.isEmpty(valueString) ? "§o§mempty value" : value))
                              .addLore(Constants.ADDITIONAL_ITEM_LORE).setAmount((value instanceof Number) ? ((Number) value).intValue() : 1)
                              .build(), event -> onItemClick(event, fullPath, valueString));
    }

    private void onSectionClick(InventoryClickEvent event, FastInventory fastInventory, String fullPath) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.closeInventory();
        if (!event.getClick().isShiftClick()) {
            fastInventory.open(humanEntity);
            return;
        }
        FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
        player.setChatSetKey(true);
        player.setPath(fullPath);
        player.setChatTask(Bukkit.getScheduler().runTaskLater(plugin,
                () -> player.setChatSetKey(false), 600L));
        humanEntity.sendMessage("Write a value for path §e" + fullPath + "§r in the chat or write \"cancel\" to cancel.");
    }

    private void onItemClick(InventoryClickEvent event, String fullPath, String valueString) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!event.getClick().isShiftClick()) {
            humanEntity.closeInventory();

            FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
            player.setChatSetKey(true);
            player.setPath(fullPath);
            player.setChatTask(Bukkit.getScheduler().runTaskLater(plugin, () -> player.setChatSetKey(false), 600L));
            humanEntity.sendMessage("Write a value for path §e" + fullPath + "§r in the chat");
            return;
        }
        if (valueString.length() > 256) {
            humanEntity.sendMessage("The value is too long to be copied. Please change the value in the config.");
            return;
        }
        TextComponent textComponent = new TextComponent("Click this message to copy the value " + valueString);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, valueString));
        humanEntity.spigot().sendMessage(textComponent);
    }

    private static Material getMaterialFromValue(Object value) {
        Material material;
        if (value instanceof String) {
            material = Material.STRING;
        } else if (value instanceof Number) {
            material = Material.ENDER_PEARL;
        } else if (value instanceof Enum) {
            material = Material.ITEM_FRAME;
        } else if (value instanceof Boolean) {
            material = (boolean) value ? Material.SLIME_BALL : Material.MAGMA_CREAM;
        } else if (value instanceof Iterable<?>) {
            material = Material.BOOKSHELF;
        } else {
            material = Material.BEDROCK;
        }
        return material;
    }

    private void sendHelpMessage(CommandSender sender, String label) {
        sender.sendMessage("FastPluginConfigurer help:");
        sender.sendMessage("/" + label + " config <plugin> [file_name]");
        sender.sendMessage("/" + label + " inventorytofile <converter> <file_name>");
        sender.sendMessage("/" + label + " filetoinventory <converter> <file_name>");
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String[] args) {
        if (args.length == 1) {
            return List.of("config", "inventorytofile", "filetoinventory");
        }
        PluginManager pluginManager = plugin.getPluginManager();
        String method = args[0].toLowerCase();
        if (args.length == 2 && (method.equals("inventorytofile") || method.equals("filetoinventory"))) {
            return List.of("deluxemenus", "chestcommands");
        } else if (args.length == 2 && method.equals("config")) {
            return Arrays.stream(pluginManager.getPlugins()).map(Plugin::getName).collect(Collectors.toList());
        }
        String converter = args[1].toLowerCase();
        if (converter.equals("deluxemenus") && pluginManager.getPlugin(converter) != null
                && args.length == 3 && method.equals("filetoinventory")) {
            return Menu.getAllMenus().stream().map(Menu::getMenuName).collect(Collectors.toList());
        } else if (converter.equals("chestcommands") && pluginManager.getPlugin(converter) != null
                && args.length == 3 && method.equals("filetoinventory")) {
            return MenuManager.getMenuFileNames().stream().map(CaseInsensitiveString::toString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
}
