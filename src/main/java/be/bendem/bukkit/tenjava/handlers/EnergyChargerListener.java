package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.Config;
import be.bendem.bukkit.tenjava.EnergyCellUtils;
import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import java.util.EnumSet;

/**
 * @author bendem
 */
public class EnergyChargerListener extends BaseListener {

    private final TenJava plugin;

    public EnergyChargerListener(TenJava plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemSmelt(FurnaceSmeltEvent e) {
        // TODO Checks
        Integer energy = plugin.getPluginConfig().FUELS.get(e.getSource().getType());
        if(energy == null) {
            return;
        }
        addAdjacentCell(e.getBlock(), energy);
        e.setResult(null);
    }

    private void addAdjacentCell(Block block, int power) {
        for(BlockFace face : EnumSet.range(BlockFace.NORTH, BlockFace.DOWN)) {
            Block relative = block.getRelative(face);
            if(plugin.getCellUtils().isCell(relative)) {
                power = plugin.getCellUtils().addPower(relative, power);
                if(power == 0) {
                    break;
                }
            }
        }
    }

}
