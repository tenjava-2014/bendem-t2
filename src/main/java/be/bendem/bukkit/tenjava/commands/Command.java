package be.bendem.bukkit.tenjava.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author bendem
 */
public interface Command {

    public void onCommand(CommandSender commandSender, List<String> args);
    public String getName();
    public boolean hasPermission(CommandSender commandSender);

}
