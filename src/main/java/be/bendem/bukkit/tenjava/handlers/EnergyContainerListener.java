package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.Config;
import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

/**
 * @author bendem
 */
public class EnergyContainerListener implements Listener {

    private static final String BLOCK_DATA = "energy-block";

    private final TenJava plugin;

    public EnergyContainerListener(TenJava plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Integer maxPower = Config.CONTAINERS.get(block.getType());
        if(maxPower == null) {
            return;
        }

        block.setMetadata(BLOCK_DATA, new FixedMetadataValue(plugin, 0));
        e.getPlayer().sendMessage("lol " + maxPower);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if(block == null) {
            return;
        }
        Integer maxPower = Config.CONTAINERS.get(block.getType());
        if(maxPower == null) {
            return;
        }

        plugin.getLogger().info("Block: " + block.getType().name());
        plugin.getLogger().info("Metadata: " + block.getMetadata(BLOCK_DATA));
        plugin.getLogger().info("Metadata: " + block.getMetadata(BLOCK_DATA).get(0));
        plugin.getLogger().info("Metadata: " + block.getMetadata(BLOCK_DATA).get(0).asInt());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Integer maxPower = Config.CONTAINERS.get(block.getType());
        if(maxPower == null || !block.hasMetadata(BLOCK_DATA)) {
            return;
        }

        e.getPlayer().sendMessage(String.valueOf(block.getMetadata(BLOCK_DATA).size()));

        ItemStack itemStack = new ItemStack(block.getType());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(Arrays.asList("Energy stored " + block.getMetadata(BLOCK_DATA).get(0).asInt() + " / " + maxPower));
        itemStack.setItemMeta(meta);
        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);

        block.setType(Material.AIR);
    }

}
