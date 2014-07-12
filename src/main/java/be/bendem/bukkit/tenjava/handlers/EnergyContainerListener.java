package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.Config;
import be.bendem.bukkit.tenjava.EnergyCellUtils;
import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bendem
 */
public class EnergyContainerListener extends BaseListener {

    private static final Pattern PATTERN = Pattern.compile("^Energy stored ([0-9]+) / [0-9]+$");

    private final TenJava plugin;

    public EnergyContainerListener(TenJava plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        ItemStack hand = e.getItemInHand();
        Integer maxPower = Config.CONTAINERS.get(block.getType());
        if(maxPower == null || hand == null) {
            return;
        }

        ItemMeta meta = hand.getItemMeta();
        if(meta.hasLore() && meta.getLore().size() > 0) {
            Matcher matcher = PATTERN.matcher(meta.getLore().get(0));
            if(matcher.matches()) {
                block.setMetadata(EnergyCellUtils.BLOCK_DATA, new FixedMetadataValue(plugin, Integer.parseInt(matcher.group(1))));
                return;
            }
        }

        block.setMetadata(EnergyCellUtils.BLOCK_DATA, new FixedMetadataValue(plugin, 0));
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
        plugin.getLogger().info("Metadata: " + block.getMetadata(EnergyCellUtils.BLOCK_DATA));
        plugin.getLogger().info("Metadata: " + block.getMetadata(EnergyCellUtils.BLOCK_DATA).get(0));
        plugin.getLogger().info("Metadata: " + block.getMetadata(EnergyCellUtils.BLOCK_DATA).get(0).asInt());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Integer maxPower = Config.CONTAINERS.get(block.getType());
        if(maxPower == null || !block.hasMetadata(EnergyCellUtils.BLOCK_DATA) || e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        ItemStack itemStack = new ItemStack(block.getType());
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Energy stored " + block.getMetadata(EnergyCellUtils.BLOCK_DATA).get(0).asInt() + " / " + maxPower);
        meta.setLore(lore);
        meta.setDisplayName("Energy container (" + block.getType().name().toLowerCase().replace("_block", "") + ")");
        itemStack.setItemMeta(meta);
        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);

        block.setType(Material.AIR);
        block.removeMetadata(EnergyCellUtils.BLOCK_DATA, plugin);
    }

}
