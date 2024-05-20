package kz.hxncus.mc.fastpluginconfigurer.command;

import kz.hxncus.mc.fastpluginconfigurer.Constants;
import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import kz.hxncus.mc.fastpluginconfigurer.converter.Converters;
import kz.hxncus.mc.fastpluginconfigurer.converter.Convertible;
import kz.hxncus.mc.fastpluginconfigurer.fast.FastPlayer;
import kz.hxncus.mc.fastpluginconfigurer.inventory.BasicFastInventory;
import kz.hxncus.mc.fastpluginconfigurer.inventory.FastInventory;
import kz.hxncus.mc.fastpluginconfigurer.language.Messages;
import kz.hxncus.mc.fastpluginconfigurer.material.MaterialValues;
import kz.hxncus.mc.fastpluginconfigurer.util.BytesUtil;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FPCCommand extends AbstractCommand {
    public FPCCommand(FastPluginConfigurer plugin) {
        super(plugin, "fastpluginconfigurer");
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(sender instanceof Player)) {
            Messages.MUST_BE_PLAYER.sendMessage(sender);
            return;
        } else if (args.length < 1) {
            sendHelpMessage(sender, label);
            return;
        }
        if (Constants.INVENTORY_TO_FILE.equalsIgnoreCase(args[0]) || Constants.FILE_TO_INVENTORY.equalsIgnoreCase(args[0])) {
            inventorySubCommand(sender, label, args);
        } else if (Constants.CONFIG.equalsIgnoreCase(args[0])) {
            configSubCommand((HumanEntity) sender, label, args);
        } else if (Constants.RELOAD.equalsIgnoreCase(args[0])) {
            plugin.reloadConfig();
            plugin.registerStaff();
            for (Messages messages : Messages.values()) {
                messages.updateMessage();
            }
        } else {
            sendHelpMessage(sender, label);
        }
    }

    private void inventorySubCommand(CommandSender sender, String label, String... args) {
        if (args.length < 3) {
            sendHelpMessage(sender, label);
            return;
        }
        Convertible converter = null;
        for (Converters converters : Converters.values()) {
            Convertible convertible = converters.getConverter();
            if (convertible != null && args[1].equalsIgnoreCase(converters.getName())) {
                converter = convertible;
                break;
            }
        }
        if (converter == null) {
            Messages.CONVERTER_TYPE_DOES_NOT_EXIST.sendMessage(sender);

        } else {
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase(Constants.INVENTORY_TO_FILE)) {
                converter.inventoryToFile(player, args[2]);
            } else if (args[0].equalsIgnoreCase(Constants.FILE_TO_INVENTORY)) {
                converter.fileToInventory(player, args[2]);
            }
        }
    }

    public void configSubCommand(HumanEntity humanEntity, String label, String... args) {
        if (args.length < 2) {
            sendHelpMessage(humanEntity, label);
            return;
        }
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(args[1]);
        if (targetPlugin == null) {
            Messages.PLUGIN_DOES_NOT_EXIST.sendMessage(humanEntity);
        } else {
            targetPlugin.reloadConfig();
            FastInventory fastInventory = new BasicFastInventory(plugin, 54, args[1]);
            fastInventory.addClickHandler(event -> event.setCancelled(true));

            String path = targetPlugin.getDataFolder()
                                      .getPath() + File.separator;
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
            humanEntity.openInventory(fastInventory.getInventory());
        }
    }

    private void setupConfigInventories(Iterator<String> iterator, FastInventory fastInventory, ConfigurationSection section, String lastPluginName) {
        Inventory inventory = fastInventory.getInventory();
        while (iterator.hasNext()) {
            if (inventory.firstEmpty() > 44) {
                FastInventory basicFastInventory = createFastInventory(lastPluginName);
                fastInventory.setItem(53, Constants.ARROW_ITEM.setDisplayName(Messages.NEXT_PAGE.getMessage()).build(), event -> event.getWhoClicked().openInventory(basicFastInventory.getInventory()));
                basicFastInventory.setItem(45, Constants.ARROW_ITEM.setDisplayName(Messages.PREVIOUS_PAGE.getMessage()).build(), event -> event.getWhoClicked().openInventory(inventory));
                setupConfigInventories(iterator, basicFastInventory, section, lastPluginName);
                return;
            }
            setupKey(lastPluginName, fastInventory, section, iterator.next());
        }
        if (inventory.firstEmpty() < 45) {
            addNewKeyItem(fastInventory, section.getCurrentPath());
        }
    }

    private FastInventory createFastInventory(String title) {
        FastInventory fastInventory = new BasicFastInventory(plugin, 54, title);
        fastInventory.addClickHandler(event -> event.setCancelled(true));
        return fastInventory;
    }
    
    private void addNewKeyItem(FastInventory inventory, String currentPath) {
        inventory.addItem(Constants.NETHER_STAR.setDisplayName(Messages.CLICK_TO_ADD_NEW_KEY.getMessage()).build(), event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            humanEntity.closeInventory();

            FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
            player.setChatAddKey(true, plugin);
            player.setPath(currentPath);
            Messages.WRITE_NEW_KEY_IN_CHAT.sendMessage(humanEntity, StringUtils.isEmpty(currentPath) ? "" : Messages.PATH.getFormattedMessage(currentPath));
        });
    }

    private void setupKey(String lastPluginName, FastInventory inventory, ConfigurationSection section, String path) {
        ConfigurationSection sections = section.getConfigurationSection(path);
        String currentPath = section.getCurrentPath();
        String fullPath;
        if (StringUtils.isEmpty(currentPath)) {
            fullPath = path;
        } else {
            fullPath = currentPath + "." + path;
        }
        if (sections != null) {
            FastInventory fastInventory = createFastInventory(lastPluginName);
            inventory.addItem(new ItemBuilder(Material.OAK_SIGN).setDisplayName(Messages.SECTION.getFormattedMessage(path))
                    .addLore("", Messages.CLICK_TO_OPEN_SECTION.getMessage(), Messages.SHIFT_CLICK_TO_EDIT_SECTION.getMessage())
                    .build(), event -> onSectionClick(event, fastInventory, fullPath));
            fastInventory.setItem(45, Constants.ARROW_ITEM.setDisplayName(Messages.PREVIOUS_PAGE.getMessage()).build(),
                    event -> event.getWhoClicked().openInventory(inventory.getInventory()));
            setupConfigInventories(sections.getKeys(false).iterator(), fastInventory, sections, lastPluginName);
            return;
        }
        Object value = section.get(path, "");
        String pathLowerCase = path.toLowerCase(java.util.Locale.ROOT);
        if (value instanceof String && (pathLowerCase.endsWith("password") || pathLowerCase.endsWith("pass"))) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedHash = digest.digest(((String) value).getBytes(StandardCharsets.UTF_8));
                value = BytesUtil.bytesToHex(encodedHash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        addKeyItemToInventory(inventory, value, fullPath);
    }

    private void addKeyItemToInventory(FastInventory inventory, Object value, String fullPath) {
        inventory.addItem(new ItemBuilder(getMaterialFromValue(value))
            .setDisplayName(Messages.KEY.getFormattedMessage(fullPath))
            .addLore(Messages.CURRENT_VALUE.getMessage(), Messages.VALUE.getFormattedMessage(StringUtils.isEmpty(value.toString()) ? Messages.EMPTY_VALUE : value))
            .addLore("", Messages.CLICK_TO_CHANGE_CURRENT_VALUE.getMessage(), Messages.SHIFT_CLICK_TO_COPY_CURRENT_VALUE.getMessage())
            .setAmount(value instanceof Integer ? (Integer) value : 1)
            .build(), event -> onItemClick(event, fullPath, value.toString()));
    }

    private void onSectionClick(InventoryClickEvent event, FastInventory fastInventory, String fullPath) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!event.getClick().isShiftClick()) {
            humanEntity.openInventory(fastInventory.getInventory());
            return;
        }
        handlePlayerPathSetting(fullPath, humanEntity);
    }

    private void handlePlayerPathSetting(String fullPath, HumanEntity humanEntity) {
        humanEntity.closeInventory();
        FastPlayer player = FastPluginConfigurer.getFastPlayer(humanEntity.getUniqueId());
        player.setChatSetKey(true, plugin);
        player.setPath(fullPath);
        Messages.WRITE_VALUE_IN_CHAT.sendMessage(humanEntity, fullPath);
    }

    private void onItemClick(InventoryClickEvent event, String fullPath, String valueString) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (event.getClick().isShiftClick()) {
            if (valueString.length() > 256) {
                Messages.VALUE_TOO_LONG.sendMessage(humanEntity);
                return;
            }
            TextComponent textComponent = new TextComponent(Messages.CLICK_MESSAGE_TO_COPY_VALUE.getFormattedMessage(valueString));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, valueString));
            humanEntity.spigot().sendMessage(textComponent);
            return;
        }
        handlePlayerPathSetting(fullPath, humanEntity);
    }

    private static Material getMaterialFromValue(Object value) {
        for (MaterialValues values : MaterialValues.values()) {
            if (values.getClazz().isInstance(value)) {
                if (value instanceof Boolean) {
                    return ((boolean) value) ? Material.SLIME_BALL : Material.MAGMA_CREAM;
                }
                return values.getMaterial();
            }
        }
        return Material.BEDROCK;
    }

    private void sendHelpMessage(CommandSender sender, String label) {
        Messages.HELP.sendMessage(sender);
        Messages.HELP_CONFIG.sendMessage(sender, label);
        Messages.HELP_INVENTORYTOFILE.sendMessage(sender, label);
        Messages.HELP_FILETOINVENTORY.sendMessage(sender, label);
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        int length = args.length;
        if (length == 1) {
            return List.of(Constants.RELOAD, Constants.CONFIG, Constants.INVENTORY_TO_FILE, Constants.FILE_TO_INVENTORY);
        }
        String args0 = args[0];
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
        if (args0.equalsIgnoreCase(Constants.INVENTORY_TO_FILE) || args0.equalsIgnoreCase(Constants.FILE_TO_INVENTORY)) {
            return List.of("deluxemenus", "chestcommands", "bettergui", "zmenu");
        } else if (args0.equalsIgnoreCase(Constants.CONFIG)) {
            return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                         .map(Plugin::getName)
                         .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> getListAtLength3(String args0, String args1) {
        if (args0.equalsIgnoreCase(Constants.FILE_TO_INVENTORY)) {
            for (Converters converters : Converters.values()) {
                if (converters.getName().equalsIgnoreCase(args1)) {
                    return converters.getConverter().getAllFileNames();
                }
            }
        } else if (args0.equalsIgnoreCase(Constants.CONFIG)) {
            return getPluginConfigFiles(args1);
        }
        return Collections.emptyList();
    }

    private static List<String> getPluginConfigFiles(String args1) {
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(args1);
        if (targetPlugin != null) {
            String[] array = targetPlugin.getDataFolder().list((dir, name) -> name.endsWith(Constants.YML_EXPANSION));
            if (array != null) {
                return Arrays.asList(array);
            }
        }
        return Collections.emptyList();
    }
}
