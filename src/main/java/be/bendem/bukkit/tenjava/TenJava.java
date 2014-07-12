package be.bendem.bukkit.tenjava;

import be.bendem.bukkit.tenjava.commands.BaseCommand;
import be.bendem.bukkit.tenjava.commands.CommandHandler;
import be.bendem.bukkit.tenjava.handlers.BlockBreaker;
import be.bendem.bukkit.tenjava.handlers.EnchantListener;
import be.bendem.bukkit.tenjava.handlers.EnergyCellListener;
import be.bendem.bukkit.tenjava.handlers.EnergyChargerListener;
import be.bendem.bukkit.tenjava.handlers.FurnaceListener;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

        new EnergyCellListener(this);
        new EnergyChargerListener(this);
        new EnchantListener(this);
        new FurnaceListener(this);
        new BlockBreaker(this);


        commandHandler = new CommandHandler(this, "energy");
        commandHandler.register(new BaseCommand("set") {
            @Override
            public void onCommand(CommandSender commandSender, List<String> args) {
                if(args.size() < 1) {
                    commandSender.sendMessage("You miss a parameter :(");
                }
                if(!(commandSender instanceof Player)) {
                    commandSender.sendMessage("You're not a player :(");
                    return;
                }

                Player pl = (Player) commandSender;
                if(pl.getItemInHand() == null || !Config.CONTAINERS.containsKey(pl.getItemInHand().getType())) {
                    pl.sendMessage("You don't have an cell in your hand");
                    return;
                }

                cellUtils.setPower(pl.getItemInHand(), Integer.parseInt(args.get(0)));
                pl.sendMessage("Your cell has " + cellUtils.getPower(pl.getItemInHand()) + " in it");
            }
        });
    }

    public EnergyCellUtils getCellUtils() {
        return cellUtils;
    }

}
