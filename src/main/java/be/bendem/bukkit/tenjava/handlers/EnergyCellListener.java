package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.Config;
import be.bendem.bukkit.tenjava.EnergyCellUtils;
import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author bendem
 */
public class EnergyCellListener extends BaseListener {

    private final TenJava plugin;

    public EnergyCellListener(TenJava plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        ItemStack hand = e.getItemInHand();
        Integer maxPower = plugin.getPluginConfig().CONTAINERS.get(block.getType());
        if(maxPower == null || hand == null) {
            return;
        }

        int power = plugin.getCellUtils().getPower(hand);
        block.setMetadata(EnergyCellUtils.BLOCK_DATA, new FixedMetadataValue(plugin, power));
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if(block == null || e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.getPlayer().isSneaking()) {
            return;
        }
        Integer maxPower = plugin.getPluginConfig().CONTAINERS.get(block.getType());
        if(maxPower == null || ! plugin.getCellUtils().isCell(block)) {
            return;
        }

        e.getPlayer().sendMessage(ChatColor.AQUA + "Energy stored: " + ChatColor.BOLD + plugin.getCellUtils().getPower(block));
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Integer maxPower = plugin.getPluginConfig().CONTAINERS.get(block.getType());
        if(maxPower == null
                || !block.hasMetadata(EnergyCellUtils.BLOCK_DATA)
                || !plugin.getPluginConfig().KEEP_ENERGY_WHEN_PICKUP
                || e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ItemStack itemStack = new ItemStack(block.getType());
        plugin.getCellUtils().setPower(itemStack, block.getMetadata(EnergyCellUtils.BLOCK_DATA).get(0).asInt());
        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);

        block.setType(Material.AIR);
        block.removeMetadata(EnergyCellUtils.BLOCK_DATA, plugin);
    }

}
