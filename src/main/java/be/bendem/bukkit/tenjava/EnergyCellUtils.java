package be.bendem.bukkit.tenjava;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author bendem
 */
public class EnergyCellUtils {

    public static final String BLOCK_DATA = "energy-block";
    private final TenJava plugin;

    public EnergyCellUtils(TenJava plugin) {
        this.plugin = plugin;
    }

    public boolean isCell(Block block) {
        return Config.CONTAINERS.containsKey(block.getType())
                && block.getMetadata(BLOCK_DATA) != null
                && block.getMetadata(BLOCK_DATA).size() > 0;
    }

    public boolean isFull(Block cell) {
        return cell.getMetadata(BLOCK_DATA).get(0).asInt() >= Config.CONTAINERS.get(cell.getType());
    }

    public int addPower(Block cell, int power) {
        int max = Config.CONTAINERS.get(cell.getType());
        int prev = cell.getMetadata(BLOCK_DATA).get(0).asInt();
        if(prev + power > max) {
            setPower(cell, max);
            return prev + power - max;
        }
        setPower(cell, prev + power);
        return 0;
    }

    private void setPower(Block cell, int power) {
        cell.removeMetadata(BLOCK_DATA, plugin);
        cell.setMetadata(BLOCK_DATA, new FixedMetadataValue(plugin, power));
    }

}
