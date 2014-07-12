package be.bendem.bukkit.tenjava;

import be.bendem.bukkit.tenjava.commands.BaseCommand;
import be.bendem.bukkit.tenjava.commands.CommandHandler;
import be.bendem.bukkit.tenjava.handlers.EnergyContainerListener;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * @author bendem
 *
 *
 *
 */
public class TenJava extends JavaPlugin {

    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EnergyContainerListener(this), this);




        commandHandler = new CommandHandler(this, "energy");
        commandHandler.register(new BaseCommand("test") {
            @Override
            public void onCommand(CommandSender commandSender, List<String> args) {
            }
        });
    }

}
