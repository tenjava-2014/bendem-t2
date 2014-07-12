package be.bendem.bukkit.tenjava;

import be.bendem.bukkit.tenjava.commands.BaseCommand;
import be.bendem.bukkit.tenjava.commands.CommandHandler;
import be.bendem.bukkit.tenjava.handlers.EnergyChargerListener;
import be.bendem.bukkit.tenjava.handlers.EnergyContainerListener;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
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
    private EnergyCellUtils cellUtils;

    @Override
    public void onEnable() {
        cellUtils = new EnergyCellUtils(this);

        getServer().addRecipe(new FurnaceRecipe(new ItemStack(Material.BRICK), Material.REDSTONE));

        new EnergyContainerListener(this);
        new EnergyChargerListener(this);


        commandHandler = new CommandHandler(this, "energy");
        commandHandler.register(new BaseCommand("test") {
            @Override
            public void onCommand(CommandSender commandSender, List<String> args) {
                commandSender.sendMessage(StringUtils.join(args, " "));
            }
        });
    }

    public EnergyCellUtils getCellUtils() {
        return cellUtils;
    }

}
