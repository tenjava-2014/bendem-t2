package be.bendem.bukkit.tenjava.commands;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bendem
 */
public class CommandHandler implements CommandExecutor {

    private final Map<String, Command> commands;
    private final String name;

    public CommandHandler(JavaPlugin plugin, String name) {
        plugin.getCommand(name.toLowerCase()).setExecutor(this);
        this.commands = new HashMap<>();
        this.name = name.toLowerCase();
    }

    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        if(!command.getName().equalsIgnoreCase(name) || args.length < 1) {
            return false;
        }

        Command subCommand = commands.get(args[0].toLowerCase());
        if(subCommand == null) {
            return false;
        }

        if(!subCommand.hasPermission(commandSender)) {
            commandSender.sendMessage(ChatColor.RED + "You don't have the permission to use that command!");
        } else {
            subCommand.onCommand(commandSender, Arrays.asList(args).subList(1, args.length));
        }
        return true;
    }
}
