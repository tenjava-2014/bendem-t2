package be.bendem.bukkit.tenjava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bendem
 */
public class CommandHandler implements CommandExecutor {

    private final Map<String, Command> commands;

    public CommandHandler(JavaPlugin plugin, String name) {
        plugin.getCommand(name.toLowerCase()).setExecutor(this);
        commands = new HashMap<>();
    }

    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return false;
    }
}
