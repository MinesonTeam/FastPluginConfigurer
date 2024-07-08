package kz.hxncus.mc.fastpluginconfigurer.config;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public enum Messages {
    CONFIG_SUCCESSFULLY_RELOADED("general.successfully_reloaded"), CHEST_SUCCESSFULLY_STORED_INTO_FILE("general.chest_successfully_stored_into_file"),
    CLICK_MESSAGE_TO_COPY_VALUE("general.click_message_to_copy_value"), CLICK_TO_ADD_NEW_KEY("general.click_to_add_new_key"),
    CLICK_TO_CHANGE_CURRENT_VALUE("general.click_to_change_current_value"), CLICK_TO_OPEN_SECTION("general.click_to_open_section"),
    CONVERTER_TYPE_DOES_NOT_EXIST("general.converter_type_does_not_exist"), CURRENT_VALUE("general.current_value"), EMPTY_VALUE("general.empty_value"),
    FILE_ALREADY_EXISTS("general.file_already_exists"), FILE_DOES_NOT_EXIST("general.file_does_not_exist"), HELP("general.help"), HELP_CONFIG("general.help_config"),
    HELP_FILETOINVENTORY("general.help_filetoinventory"), HELP_INVENTORYTOFILE("general.help_inventorytofile"), INVALID_PATH("general.invalid_path"),
    KEY("general.key"), MENU_NOT_FOUND("general.menu_not_found"), MUST_BE_PLAYER("general.must_be_player"), MUST_LOOKING_AT_DOUBLE_CHEST("general.must_looking_at_double_chest"),
    NEXT_PAGE("general.next_page"), PATH("general.path"), PLAYER_LOGGED_WITH_CUSTOM_ITEM("general.player_logged_with_custom_item"),
    PLUGIN_DOES_NOT_EXIST("general.plugin_does_not_exist"), PREFIX("general.prefix"), PREVIOUS_PAGE("general.previous_page"), SECTION("general.section"),
    SHIFT_CLICK_TO_COPY_CURRENT_VALUE("general.shift_click_to_copy_current_value"), SHIFT_CLICK_TO_EDIT_SECTION("general.shift_click_to_edit_section"),
    SOMEONE_DROPPED_CUSTOM_ITEM("general.someone_dropped_custom_item"), SOMEONE_PICKED_CUSTOM_ITEM("general.someone_picked_custom_item"),
    SUCCESSFULLY_STORED_ITEMS_TO_CHEST("general.successfully_stored_items_to_chest"), VALUE("general.value"), VALUE_TOO_LONG("general.VALUE_TOO_LONG"),
    WRITE_NEW_KEY_IN_CHAT("general.write_new_key_in_chat"), WRITE_VALUE_IN_CHAT("general.write_value_in_chat"), UPDATING_CONFIG_KEY("general.updating_config_key"),
    REMOVING_CONFIG_KEY("general.removing_config_key");

    private final String path;

    Messages(final String path) {
        this.path = path;
    }

    public String toPath() {
        return path;
    }

    @Override
    public String toString() {
        return FastPluginConfigurer.get().getConfigManager().getLanguages().getString(path);
    }

    public String format(String message, final Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i].toString());
        }
        return message.replace("{PREFIX}", PREFIX.toString());
    }

    public String toString(final Object... args) {
        return format(FastPluginConfigurer.get().getConfigManager().getLanguages().getString(path), args);
    }

    public void send(@NonNull final CommandSender sender, final Object... args) {
        String message = FastPluginConfigurer.get().getConfigManager().getLanguages().getString(path, "Message with path " + path + " not found");
        sender.sendMessage(format(message, args));
    }

    public void log(final Object... args) {
        send(Bukkit.getConsoleSender(), args);
    }
}
