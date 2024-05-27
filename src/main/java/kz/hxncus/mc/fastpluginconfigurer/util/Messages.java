package kz.hxncus.mc.fastpluginconfigurer.util;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public enum Messages {
    CONFIG_SUCCESSFULLY_RELOADED, CHEST_SUCCESSFULLY_STORED_INTO_FILE, CLICK_MESSAGE_TO_COPY_VALUE, CLICK_TO_ADD_NEW_KEY, CLICK_TO_CHANGE_CURRENT_VALUE,
    CLICK_TO_OPEN_SECTION, CONVERTER_TYPE_DOES_NOT_EXIST, CURRENT_VALUE, EMPTY_VALUE, FILE_ALREADY_EXISTS, FILE_DOES_NOT_EXIST, HELP, HELP_CONFIG,
    HELP_FILETOINVENTORY, HELP_INVENTORYTOFILE, INVALID_PATH, KEY, MENU_NOT_FOUND, MUST_BE_PLAYER, MUST_LOOKING_AT_DOUBLE_CHEST, NEXT_PAGE, PATH,
    PLAYER_LOGGED_WITH_CUSTOM_ITEM, PLUGIN_DOES_NOT_EXIST, PREFIX, PREVIOUS_PAGE, SECTION, SHIFT_CLICK_TO_COPY_CURRENT_VALUE, SHIFT_CLICK_TO_EDIT_SECTION,
    SOMEONE_DROPPED_CUSTOM_ITEM, SOMEONE_PICKED_CUSTOM_ITEM, SUCCESSFULLY_STORED_ITEMS_TO_CHEST, UNKNOWN_LANGUAGE, VALUE, VALUE_TOO_LONG, WRITE_NEW_KEY_IN_CHAT,
    WRITE_VALUE_IN_CHAT;

    private String message;

    Messages() {
        updateMessage();
    }

    public void updateMessage() {
        message = FastPluginConfigurer.getInstance()
                                      .getLanguageManager()
                                      .getLangConfig()
                                      .getString(name().toLowerCase(Locale.ROOT), "");
    }

    public String getMessage() {
        return message.replace("{PREFIX}", PREFIX.message);
    }

    public String getFormattedMessage(Object... args) {
        return String.format(getMessage(), args);
    }

    public void sendMessage(CommandSender sender, Object... args) {
        sender.sendMessage(getFormattedMessage(args));
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public static void updateAllMessages() {
        for (Messages messages : values()) {
            messages.updateMessage();
        }
    }
}
