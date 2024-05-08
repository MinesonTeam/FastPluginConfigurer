package kz.hxncus.mc.fastpluginconfigurer.command;

import com.extendedclip.deluxemenus.menu.Menu;
import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.hook.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.inventory.BasicFastInventory;
import kz.hxncus.mc.fastpluginconfigurer.inventory.FastInventory;
import kz.hxncus.mc.fastpluginconfigurer.util.ItemBuilder;
import lombok.NonNull;
import me.filoghost.chestcommands.fcommons.collection.CaseInsensitiveString;
import me.filoghost.chestcommands.menu.MenuManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
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
            sendHelpMessage(sender, label);
            return;
        }
        String args0 = args[0].toLowerCase();
        if (args.length == 1) {
            sendHelpMessage(sender, label);
            return;
        }
        String args1 = args[1].toLowerCase();
        switch (args0) {
            case "inventorytofile":
            case "filetoinventory":
                inventorySubCommand(sender, label, args, args0, args1);
                break;
            case "config":
                configSubCommand(sender, args1);
                break;
            default:
                sendHelpMessage(sender, label);
                break;
        }
    }

    private void inventorySubCommand(CommandSender sender, String label, String[] args, String args0, String args1) {
        if (args.length == 2) {
            sendHelpMessage(sender, label);
            return;
        }
        String args2 = args[2];
        Convertible converter;
        switch (args1) {
            case "deluxemenus":
                converter = instance.getDeluxeMenusHook();
                break;
            case "chestcommands":
                converter = instance.getChestCommandsHook();
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

        FastInventory fastInventory = new BasicFastInventory(54, args1);
        fastInventory.addClickHandler(event -> event.setCancelled(true));

        HumanEntity humanEntity = (HumanEntity) sender;
        FastPlayer fastPlayer = FastPlayer.getFastPlayer(humanEntity.getUniqueId());
        fastPlayer.setDataFolderPath(plugin.getDataFolder().getPath());
        fastPlayer.setLastPluginName(plugin.getName().toLowerCase());

        FileConfiguration config = plugin.getConfig();
        setupConfigInventories(fastPlayer, fastInventory, config, config.getKeys(false).iterator());
        fastInventory.open(humanEntity);
    }

    private static void setupConfigInventories(FastPlayer fastPlayer, FastInventory inventory, @NonNull ConfigurationSection section, Iterator<String> iterator) {
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (inventory.firstEmpty() > 44) {
                FastInventory fastInventory = new BasicFastInventory(54, fastPlayer.getLastPluginName());
                fastInventory.addClickHandler(event -> event.setCancelled(true));

                setNextAndPreviousPages(inventory, fastInventory);

                setupConfigInventories(fastPlayer, fastInventory, section, iterator);
                break;
            }
            setupKey(fastPlayer, inventory, section, path);
        }
        addNewKeyItem(inventory, section);
    }

    private static void setNextAndPreviousPages(FastInventory currentInv, FastInventory nextInv) {
        currentInv.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("Next page").build(), event -> nextInv.open(event.getWhoClicked()));
        setPreviousPage(nextInv, currentInv);
    }
    
    private static void setPreviousPage(FastInventory currentInv, FastInventory previousInv) {
        currentInv.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("Previous page").build(), event -> previousInv.open(event.getWhoClicked()));
    }
    
    private static void addNewKeyItem(FastInventory inventory, ConfigurationSection section) {
        if (inventory.firstEmpty() <= 44) {
            inventory.addItem(new ItemBuilder(Material.NETHER_STAR).setDisplayName("§fClick to add a new key")
                                                                   .build(), event -> {
                HumanEntity humanEntity = event.getWhoClicked();
                humanEntity.closeInventory();

                String currentPath = section.getCurrentPath();
                FastPlayer player = FastPlayer.getFastPlayer(humanEntity.getUniqueId());
                player.setChatAddKey(true);
                player.setPath(currentPath);
                player.setChatTask(Bukkit.getScheduler()
                                         .runTaskLater(FastPluginConfigurer.getInstance(), () -> player.setChatAddKey(false), 600L));
                humanEntity.sendMessage("Write down the new key in the chat." + (StringUtils.isEmpty(currentPath) ? "" : " Path: " + currentPath));
            });
        }
    }

    private static void setupKey(FastPlayer fastPlayer, FastInventory inventory, ConfigurationSection section, String path) {
        ConfigurationSection sections = section.getConfigurationSection(path);
        String currentPath = section.getCurrentPath();
        String fullPath = StringUtils.isEmpty(currentPath) ? path : currentPath + "." + path;
        if (sections != null) {
            FastInventory fastInventory = new BasicFastInventory(54, fastPlayer.getLastPluginName());
            fastInventory.addClickHandler(event -> event.setCancelled(true));
            
            inventory.addItem(new ItemBuilder(Material.OAK_SIGN).setDisplayName("§fSection: §e" + path).build(), event -> {
                HumanEntity humanEntity = event.getWhoClicked();
                if (!event.getClick().isShiftClick()) {
                    fastInventory.open(humanEntity);
                    return;
                }
                humanEntity.closeInventory();

                FastPlayer player = FastPlayer.getFastPlayer(humanEntity.getUniqueId());
                player.setChatSetKey(true);
                player.setPath(fullPath);
                player.setChatTask(Bukkit.getScheduler().runTaskLater(FastPluginConfigurer.getInstance(),
                        () -> player.setChatSetKey(false), 600L));
                humanEntity.sendMessage("Write a value for path §e" + fullPath + "§r in the chat or write cancel to cancel.");
            });
            setPreviousPage(fastInventory, inventory);
            setupConfigInventories(fastPlayer, fastInventory, sections, sections.getKeys(false).iterator());
            return;
        }
        Object value = section.get(path, "");
        String valueString = value.toString();
        int amount = 1;
        Material material;
        if (value instanceof String) {
            material = Material.STRING;
        } else if (value instanceof Number) {
            material = Material.ENDER_PEARL;
            amount = ((Number) value).intValue();
        } else if (value instanceof Enum) {
            material = Material.ITEM_FRAME;
        } else if (value instanceof Boolean) {
            material = (boolean) value ? Material.SLIME_BALL : Material.MAGMA_CREAM;
        } else if (value instanceof Iterable<?>) {
            material = Material.BOOKSHELF;
        } else {
            material = Material.BEDROCK;
        }
        inventory.addItem(new ItemBuilder(material).setDisplayName("§fKey: §e" + fullPath)
                          .addLore("§7Current value:", " §8▪ §e" + (StringUtils.isEmpty(valueString) ? "§o§mempty value" : value))
                          .addLore(Constants.ADDITIONAL_ITEM_LORE).setAmount(amount).build(), event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (!event.getClick().isShiftClick()) {
                humanEntity.closeInventory();

                FastPlayer player = FastPlayer.getFastPlayer(humanEntity.getUniqueId());
                player.setChatSetKey(true);
                player.setPath(fullPath);
                player.setChatTask(Bukkit.getScheduler().runTaskLater(FastPluginConfigurer.getInstance(), () -> player.setChatSetKey(false), 600L));
                humanEntity.sendMessage("Write a value for path §e" + fullPath + "§r in the chat");
                return;
            }
            if (valueString.length() > 256) {
                humanEntity.sendMessage("The value is too long to be copied. Please change the value in the config.");
                return;
            }
            BaseComponent baseComponent = new TextComponent("Click this message to copy the value " + value);
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, valueString));
            humanEntity.spigot().sendMessage(baseComponent);
        });
    }

    private static class Result {
        public final int amount;
        public final Material material;

        public Result(int amount, Material material) {
            this.amount = amount;
            this.material = material;
        }
    }

    private void sendHelpMessage(CommandSender sender, String label) {
        sender.sendMessage("FastPluginConfigurer help:");
        sender.sendMessage("/" + label + " config <plugin>");
        sender.sendMessage("/" + label + " inventorytofile <converter> <filename>");
        sender.sendMessage("/" + label + " filetoinventory <converter> <filename>");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("config", "inventorytofile", "filetoinventory");
        }
        String method = args[0].toLowerCase();
        if (args.length == 2 && (method.equals("inventorytofile") || method.equals("filetoinventory"))) {
            return List.of("deluxemenus", "chestcommands");
        } else if (args.length == 2 && method.equals("config")) {
            return new ArrayList<>(FastPluginConfigurer.getPlugins().keySet());
        }
        String converter = args[1].toLowerCase();
        if (FastPluginConfigurer.getPlugins().containsKey("deluxemenus") && args.length == 3 &&
                converter.equals("deluxemenus") && method.equals("filetoinventory")) {
            return Menu.getAllMenus().stream().map(Menu::getMenuName).collect(Collectors.toList());
        } else if (FastPluginConfigurer.getPlugins().containsKey("chestcommands") && args.length == 3 &&
                converter.equals("chestcommands") && method.equals("filetoinventory")) {
            return MenuManager.getMenuFileNames().stream().map(CaseInsensitiveString::toString).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
}
