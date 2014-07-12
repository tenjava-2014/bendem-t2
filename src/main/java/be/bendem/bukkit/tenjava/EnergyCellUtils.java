package be.bendem.bukkit.tenjava;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bendem
 */
public class EnergyCellUtils {


    private static final Pattern PATTERN    = Pattern.compile("^Energy stored ([0-9]+) / [0-9]+$");
    public static final  String  BLOCK_DATA = "energy-block";
    private final TenJava plugin;

    public EnergyCellUtils(TenJava plugin) {
        this.plugin = plugin;
    }

    public boolean isCell(Block block) {
        return plugin.getPluginConfig().CONTAINERS.containsKey(block.getType()) && block.getMetadata(BLOCK_DATA) != null && block.getMetadata(BLOCK_DATA).size() > 0;
    }

    public boolean isFull(Block cell) {
        return cell.getMetadata(BLOCK_DATA).get(0).asInt() >= plugin.getPluginConfig().CONTAINERS.get(cell.getType());
    }

    /**
     * Add power to a cell
     * @param cell the cell to add power to
     * @param power the power to add
     * @return the power which could not be added because the cell was full
     */
    public int addPower(Block cell, int power) {
        int max = plugin.getPluginConfig().CONTAINERS.get(cell.getType());
        int prev = getPower(cell);
        if(prev + power > max) {
            setPower(cell, max);
            return prev + power - max;
        }
        setPower(cell, prev + power);
        return 0;
    }

    /**
     * Remove power from a cell
     * @param cell the cell to remove from
     * @param power the power to remove
     * @return the amount of power that was removed
     */
    public int removePower(Block cell, int power) {
        int current = getPower(cell);
        if(current < power) {
            setPower(cell, 0);
            return current;
        }
        setPower(cell, current - power);
        return power;
    }

    /**
     * Get the contained power of a cell
     * @param cell the cell to get the power from
     * @return the power contained
     */
    public int getPower(Block cell) {
        return cell.getMetadata(BLOCK_DATA).get(0).asInt();
    }

    /**
     * Set the power contained in a cell
     * @param cell the cell to set power to
     * @param power the power to set
     */
    public void setPower(Block cell, int power) {
        cell.removeMetadata(BLOCK_DATA, plugin);
        cell.setMetadata(BLOCK_DATA, new FixedMetadataValue(plugin, power));
    }

    /**
     * Get the contained power of a cell
     * @param cell the cell to get the power from
     * @return the power contained
     */
    public int getPower(ItemStack cell) {
        Integer maxPower = plugin.getPluginConfig().CONTAINERS.get(cell.getType());
        if(maxPower == null) {
            return 0;
        }

        ItemMeta meta = cell.getItemMeta();
        if(meta.hasLore() && meta.getLore().size() > 0) {
            Matcher matcher = PATTERN.matcher(meta.getLore().get(0));
            if(matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return 0;
    }

    /**
     * Set the power contained in a cell
     * @param cell the cell to set power to
     * @param power the power to set
     */
    public void setPower(ItemStack cell, int power) {
        Integer maxPower = plugin.getPluginConfig().CONTAINERS.get(cell.getType());
        ItemMeta meta = cell.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Energy stored " + power + " / " + maxPower);
        meta.setLore(lore);
        meta.setDisplayName("Energy container (" + cell.getType().name().toLowerCase().replace("_block", "") + ")");
        cell.setItemMeta(meta);
    }

    /**
     * Get combined power for all cells adjacents to the block
     * @param block the block around which to check
     * @return the total power available
     */
    public int getAdjacentPower(Block block) {
        int power = 0;
        for(BlockFace face : EnumSet.range(BlockFace.NORTH, BlockFace.DOWN)) {
            Block relative = block.getRelative(face);
            if(isCell(relative)) {
                power += getPower(relative);
            }
        }
        return power;
    }

    /**
     * Remove power from a cell around the block
     * @param block the block around which to check
     * @param power the power to remove
     * @return the power remaining to remove
     */
    public int removeAdjacentPower(Block block, int power) {
        int remaining = power;
        for(BlockFace face : EnumSet.range(BlockFace.NORTH, BlockFace.DOWN)) {
            Block relative = block.getRelative(face);
            if(isCell(relative)) {
                remaining -= removePower(relative, remaining);
            }
        }
        return remaining;
    }
}
