package be.bendem.bukkit.tenjava.commands;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

/**
 * @author bendem
 */
public abstract class BaseCommand implements Command {

    private final String name;
    private final String permission;

    public BaseCommand(String name, String permission) {
        Validate.notNull(name);
        this.name = name;
        this.permission = permission;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return permission == null || commandSender.hasPermission(permission);
    }

    public String getPermission() {
        return permission;
    }

}
