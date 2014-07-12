package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bendem
 */
public class BlockBreaker extends BaseListener {

    private final TenJava plugin;
    private final Map<Block, DiggerRunnable> diggingTasks;

    public BlockBreaker(TenJava plugin) {
        super(plugin);
        this.plugin = plugin;
        diggingTasks = new HashMap<>();
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

        registerTask(itemFrame, atachedTo, getDirection(itemFrame), atachedTo.isBlockPowered());
    }

    @EventHandler
    public void onCellPower(BlockPhysicsEvent e) {
        final Block block = e.getBlock();
        DiggerRunnable task = diggingTasks.get(block);
        if(task == null) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(block.isBlockPowered()) {
                    plugin.getLogger().info("Starting");
                    if(task.isStarted()) {
                        restartDigging(block, task);
                    } else {
                        task.start(plugin);
                    }
                } else {
                    plugin.getLogger().info("Stopping");
                    stopTask(block);
                }
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void onCellBreak(BlockBreakEvent e) {
        stopTask(e.getBlock());
    }

    private void registerTask(ItemFrame frame, Block cell, BlockFace direction, boolean start) {
        stopTask(cell);
        DiggerRunnable diggerRunnable = new DiggerRunnable(frame, cell, direction);
        diggingTasks.put(cell, diggerRunnable);
        plugin.getLogger().info("Cell registered: " + cell.getLocation());
        if(start) {
            diggerRunnable.start(plugin);
        }
    }

    private void stopTask(Block cell) {
        DiggerRunnable runnable = diggingTasks.get(cell);
        if(runnable != null) {
            runnable.stop();
            diggingTasks.remove(cell);
        }
    }

    private void restartDigging(Block cell, DiggerRunnable task) {
        stopTask(cell);
        DiggerRunnable diggerRunnable = new DiggerRunnable(task);
        diggingTasks.put(cell, task);
        diggerRunnable.start(plugin);
    }

    private BlockFace getDirection(ItemFrame frame) {
        BlockFace face = frame.getAttachedFace();
        switch(frame.getRotation()) {
            // Digging right
            case NONE:
                return BlockFace.values()[face == BlockFace.WEST ? 0 : face.ordinal() + 1];
            // Digging bottom
            case CLOCKWISE:
            case FLIPPED:
                return BlockFace.DOWN;
            // Digging left
            case COUNTER_CLOCKWISE:
                return BlockFace.values()[face == BlockFace.NORTH ? 3 : face.ordinal() - 1];
            default:
                throw new AssertionError("Impossible to get another value from the Rotation enum");
        }
    }

    private class DiggerRunnable extends BukkitRunnable {

        private final ItemFrame frame;
        private final Block cell;
        private BlockFace direction;
        private Block current;
        private int numberDigged;
        private boolean started = false;
        private boolean stopped = false;

        public DiggerRunnable(DiggerRunnable task) {
            this.frame = task.frame;
            this.cell = task.cell;
            this.current = task.current;
            this.numberDigged = task.numberDigged;
            this.direction = task.direction;
        }

        public DiggerRunnable(ItemFrame frame, Block cell, BlockFace direction) {
            this.frame = frame;
            this.cell = cell;
            this.direction = direction;
            this.current = frame.getWorld().getBlockAt(frame.getLocation()).getRelative(direction);
            this.numberDigged = 0;
        }

        public BukkitTask start(JavaPlugin plugin) {
            checkDirectionChanged(getDirection(frame));
            started = true;
            return runTaskTimer(plugin, 0, 20);
        }

        public void stop() {
            if(started && !stopped) {
                stopped = true;
                cancel();
            }
        }

        private void checkDirectionChanged(BlockFace newDirection) {
            if(direction != newDirection) {
                this.direction = newDirection;
                this.current = frame.getWorld().getBlockAt(frame.getLocation()).getRelative(newDirection);
                this.numberDigged = 0;
            }
        }

        @Override
        public void run() {
            plugin.getLogger().info("Running");
            if(!frame.isValid() || frame.getItem().getType() != Material.DIAMOND_PICKAXE || plugin.getCellUtils().getPower(cell) < plugin.getPluginConfig().DIG_COST) {
                plugin.getLogger().info("Invalid action");
                stop();
                return;
            }
/*
            // Where does these tasks disapear? :O hegfkze lfglugf lreglor
            if(!diggingTasks.containsValue(this)) {
                stop();
                return;
            }
*/
            if(plugin.getCellUtils().removePower(cell, plugin.getPluginConfig().DIG_COST) == plugin.getPluginConfig().DIG_COST) {
                if(current.getType() != Material.AIR) {
                    current.breakNaturally(frame.getItem());
                }
                current = current.getRelative(direction);
                numberDigged++;
                if(numberDigged > plugin.getPluginConfig().MAX_DIG_DISTANCE) {
                    stop();
                }
            } else {
                plugin.getLogger().info("No more power");
                stop();
            }
        }

        public boolean isStarted() {
            return started;
        }

    }

}
