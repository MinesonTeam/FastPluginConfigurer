package kz.hxncus.mc.fastpluginconfigurer.command;

import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Converters;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.BasicFastInventory;
import kz.hxncus.mc.fastpluginconfigurer.inventory.FastInventory;
import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FPCCommand extends DefaultCommand {
    public FPCCommand(FastPluginConfigurer plugin) {
        super(plugin, "fastpluginconfigurer");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return;
        }
        switch (args[0].toLowerCase(Locale.US)) {
            case Constants.INVENTORY_TO_FILE:
            case Constants.FILE_TO_INVENTORY:
                inventorySubCommand(sender, label, args);
                break;
            case Constants.CONFIG:
                configSubCommand((HumanEntity) sender, label, args);
                break;
            default:
                sendHelpMessage(sender, label);
                break;
        }
    }

    private void inventorySubCommand(CommandSender sender, String label, String... args) {
        if (args.length < 2) {
            sendHelpMessage(sender, label);
            return;
        }
        Convertible converter = null;
        for (Converters converters : Converters.values()) {
            Convertible convertible = converters.getConverter();
            if (convertible != null && args[1].equals(converters.getName())) {
                converter = convertible;
                break;
            }
        }
        if (converter == null) {
            sender.sendMessage("This converter type is not exists.");
            return;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase(Constants.INVENTORY_TO_FILE)) {
            converter.inventoryToFile(player, args[2]);
        } else if (args[0].equalsIgnoreCase(Constants.FILE_TO_INVENTORY)) {
            converter.fileToInventory(player, args[2]);
        }
    }

    public void configSubCommand(HumanEntity humanEntity, String label, String... args) {
        if (args.length < 2) {
            sendHelpMessage(humanEntity, label);
            return;
        }
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(args[1]);
        if (targetPlugin == null) {
            humanEntity.sendMessage("This plugin doesn't exist.");
            return;
        }
        targetPlugin.reloadConfig();
        FastInventory fastInventory = new BasicFastInventory(plugin, 54, args[1]);
        fastInventory.addClickHandler(event -> event.setCancelled(true));

        String path = targetPlugin.getDataFolder().getPath() + File.separator;
        File file;
        if (args.length < 3) {
            file = new File(path + "config.yml");
        } else {
            file = new File(path + args[2]);
        }
        FastPlayer fastPlayer = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
        fastPlayer.setFile(file);
        fastPlayer.setLastPluginName(targetPlugin.getName());

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        setupConfigInventories(config.getKeys(false).iterator(), fastInventory, config, targetPlugin.getName());
        fastInventory.open(humanEntity);
    }

    private void setupConfigInventories(Iterator<String> iterator, FastInventory inventory, ConfigurationSection section, String lastPluginName) {
        while (iterator.hasNext()) {
            if (inventory.firstEmpty() > 44) {
                FastInventory fastInventory = createFastInventory(lastPluginName);
                inventory.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("Next page").build(), event -> fastInventory.open(event.getWhoClicked()));
                fastInventory.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build(), event -> inventory.open(event.getWhoClicked()));
                setupConfigInventories(iterator, fastInventory, section, lastPluginName);
                return;
            }
            setupKey(lastPluginName, inventory, section, iterator.next());
        }
        if (inventory.firstEmpty() < 45) {
            addNewKeyItem(inventory, section.getCurrentPath());
        }
    }

    private FastInventory createFastInventory(String title) {
        FastInventory fastInventory = new BasicFastInventory(plugin, 54, title);
        fastInventory.addClickHandler(event -> event.setCancelled(true));
        return fastInventory;
    }
    
    private void addNewKeyItem(FastInventory inventory, String currentPath) {
        inventory.addItem(new ItemBuilder(Material.NETHER_STAR).setDisplayName("§fClick to add a new key").build(), event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            humanEntity.closeInventory();

            FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
            player.setChatAddKey(true, plugin);
            player.setPath(currentPath);
            humanEntity.sendMessage("Write down the new key in the chat." + (StringUtils.isEmpty(currentPath) ? "" : " Path: " + currentPath));
        });
    }

    private void setupKey(String lastPluginName, FastInventory inventory, ConfigurationSection section, String path) {
        ConfigurationSection sections = section.getConfigurationSection(path);
        String currentPath = section.getCurrentPath();
        String fullPath = StringUtils.isEmpty(currentPath) ? path : currentPath + "." + path;
        if (sections != null) {
            FastInventory fastInventory = createFastInventory(lastPluginName);
            inventory.addItem(new ItemBuilder(Material.OAK_SIGN).setDisplayName("§fSection: §e" + path)
                    .addLore(Constants.SECTION_LORE)
                    .build(), event -> onSectionClick(event, fastInventory, fullPath));
            fastInventory.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build(), event -> inventory.open(event.getWhoClicked()));
            setupConfigInventories(sections.getKeys(false).iterator(), fastInventory, sections, lastPluginName);
            return;
        }
        addKeyItemToInventory(inventory, section.get(path, ""), fullPath);
    }

    private void addKeyItemToInventory(FastInventory inventory, Object value, String fullPath) {
        inventory.addItem(new ItemBuilder(getMaterialFromValue(value)).setDisplayName("§fKey: §e" + fullPath)
                                                                      .addLore("§7Current value:", " §8▪ §e" + (StringUtils.isEmpty(value.toString()) ? "§o§mempty value" : value))
                                                                      .addLore(Constants.ITEM_LORE).setAmount((value instanceof Number) ? ((Number) value).intValue() : 1)
                                                                      .build(), event -> onItemClick(event, fullPath, value.toString()));
    }

    private void onSectionClick(InventoryClickEvent event, FastInventory fastInventory, String fullPath) {
        HumanEntity humanEntity = event.getWhoClicked();
        humanEntity.closeInventory();
        if (!event.getClick().isShiftClick()) {
            fastInventory.open(humanEntity);
            return;
        }
        FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
        player.setChatSetKey(true, plugin);
        player.setPath(fullPath);
        humanEntity.sendMessage("Write a value for path §e" + fullPath + "§r in the chat or write \"cancel\" to cancel.");
    }

    private void onItemClick(InventoryClickEvent event, String fullPath, String valueString) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!event.getClick().isShiftClick()) {
            humanEntity.closeInventory();

            FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
            player.setChatSetKey(true, plugin);
            player.setPath(fullPath);
            humanEntity.sendMessage("Write a value for path §e" + fullPath + "§r in the chat or write \"cancel\" to cancel.");
            return;
        } else if (valueString.length() > 256) {
            humanEntity.sendMessage("The value is too long to be copied. Please change the value in the config.");
            return;
        }
        TextComponent textComponent = new TextComponent("Open chat then click this message to copy the value: " + valueString);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, valueString));
        humanEntity.spigot().sendMessage(textComponent);
    }

    private static Material getMaterialFromValue(Object value) {
        if (value instanceof String) {
            return Material.STRING;
        } else if (value instanceof Number) {
            return Material.ENDER_PEARL;
        } else if (value instanceof Enum) {
            return Material.ITEM_FRAME;
        } else if (value instanceof Boolean) {
            return (boolean) value ? Material.SLIME_BALL : Material.MAGMA_CREAM;
        } else if (value instanceof Iterable<?>) {
            return Material.BOOKSHELF;
        }
        return Material.BEDROCK;
    }

    private void sendHelpMessage(CommandSender sender, String label) {
        sender.sendMessage("FastPluginConfigurer help:");
        sender.sendMessage("/" + label + " config <plugin> [file_name]");
        sender.sendMessage("/" + label + " inventorytofile <converter> <file_name>");
        sender.sendMessage("/" + label + " filetoinventory <converter> <file_name>");
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        int length = args.length;
        if (length == 1) {
            return List.of(Constants.CONFIG, Constants.INVENTORY_TO_FILE, Constants.FILE_TO_INVENTORY);
        }
        String args0 = args[0].toLowerCase();
        if (length == 2) {
            return getListAtLength2(args0);
        }
        String args1 = args[1];
        if (length == 3) {
            return getListAtLength3(args0, args1);
        }
        return Collections.emptyList();
    }

    private List<String> getListAtLength2(String args0) {
        if ((args0.equals(Constants.INVENTORY_TO_FILE) || args0.equals(Constants.FILE_TO_INVENTORY))) {
            return List.of("deluxemenus", "chestcommands", "bettergui", "zmenu");
        } else if (args0.equals(Constants.CONFIG)) {
            return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                         .map(Plugin::getName)
                         .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> getListAtLength3(String args0, String args1) {
        if (args0.equals(Constants.FILE_TO_INVENTORY)) {
            for (Converters converters : Converters.values()) {
                if (converters.getName().equalsIgnoreCase(args1)) {
                    return converters.getConverter().getAllFileNames();
                }
            }
        } else if (args0.equals(Constants.CONFIG)) {
            Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(args1);
            if (targetPlugin == null) {
                return Collections.emptyList();
            }
            String[] array = targetPlugin.getDataFolder().list((dir, name) -> name.endsWith(".yml"));
            if (array == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(array);
        }
        return Collections.emptyList();
    }
}
