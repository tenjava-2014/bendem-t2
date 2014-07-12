package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.Config;
import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bendem
 */
public class BlockBreaker extends BaseListener {

    private final TenJava         plugin;

    public BlockBreaker(TenJava plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemHang(PlayerInteractEntityEvent e) {
        if(e.getRightClicked().getType() != EntityType.ITEM_FRAME || e.getPlayer().getItemInHand().getType() != Material.DIAMOND_PICKAXE) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) e.getRightClicked();
        BlockFace attachedFace = itemFrame.getAttachedFace();

        Block atachedTo = e.getPlayer().getWorld().getBlockAt(
                e.getRightClicked().getLocation()).getRelative(attachedFace);

        if(!plugin.getCellUtils().isCell(atachedTo)) {
            return;
        }
        if(atachedTo.isBlockPowered()) {
            startDigging(itemFrame, atachedTo, getDirection(itemFrame));
        }
    }

    

    private void startDigging(ItemFrame frame, Block cell, BlockFace direction) {
        new DiggerRunnable(frame, cell, direction).runTaskTimer(plugin, 20, 20);
    }

    private BlockFace getDirection(ItemFrame frame) {
        BlockFace face = frame.getAttachedFace();
        switch(frame.getRotation()) {
            // Digging right
            case NONE:
                return BlockFace.values()[face == BlockFace.WEST ? 0 : face.ordinal() + 1];
            // Digging bottom
            case COUNTER_CLOCKWISE:
            case CLOCKWISE:
                return BlockFace.DOWN;
            // Digging left
            case FLIPPED:
                return BlockFace.values()[face == BlockFace.NORTH ? 3 : face.ordinal() - 1];
            default:
                throw new AssertionError("Impossible to get another value from the Rotation enum");
        }
    }

    private class DiggerRunnable extends BukkitRunnable {

        private final ItemFrame frame;
        private final Block     cell;
        private final BlockFace direction;
        private Block current;
        private int numberDigged;

        public DiggerRunnable(ItemFrame frame, Block cell, BlockFace direction) {
            this.frame = frame;
            this.cell = cell;
            this.direction = direction;
            this.current = frame.getWorld().getBlockAt(frame.getLocation()).getRelative(direction);
            this.numberDigged = 0;
        }

        @Override
        public void run() {
            plugin.getLogger().info("Running");
            if(!frame.isValid() || frame.getItem().getType() != Material.DIAMOND_PICKAXE || plugin.getCellUtils().getPower(cell) < Config.DIG_COST) {
                plugin.getLogger().info("Invalid action");
                cancel();
                return;
            }

            if(plugin.getCellUtils().removePower(cell, Config.DIG_COST) == Config.DIG_COST) {
                if(current.getType() != Material.AIR) {
                    current.breakNaturally(frame.getItem());
                }
                current = current.getRelative(direction);
                numberDigged++;
                if(numberDigged > Config.MAX_DIG_DISTANCE) {
                    cancel();
                }
            } else {
                plugin.getLogger().info("No more power");
                cancel();
            }
        }

    }

}
