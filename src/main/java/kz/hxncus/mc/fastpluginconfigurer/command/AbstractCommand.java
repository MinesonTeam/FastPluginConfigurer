package kz.hxncus.mc.fastpluginconfigurer.command;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public abstract class AbstractCommand implements ICommand {
    public final FastPluginConfigurer plugin;

    protected AbstractCommand(FastPluginConfigurer plugin, String command) {
        this.plugin = plugin;
        PluginCommand pluginCommand = this.plugin.getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull... args) {
        execute(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String @NonNull... args) {
        return filter(complete(sender, command, args), args);
    }

    private List<String> filter(List<String> list, String... args) {
        if (list == null) {
            return Collections.emptyList();
        }
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (final String arg : list) {
            if (arg.toLowerCase(Locale.ENGLISH).startsWith(last.toLowerCase(Locale.ENGLISH))) {
                result.add(arg);
            }
        }
        return result;
    }
}
