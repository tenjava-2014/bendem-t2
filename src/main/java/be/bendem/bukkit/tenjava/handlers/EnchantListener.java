package be.bendem.bukkit.tenjava.handlers;

import be.bendem.bukkit.tenjava.Config;
import be.bendem.bukkit.tenjava.TenJava;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author bendem
 */
public class EnchantListener extends BaseListener {

    private final TenJava            plugin;
    private final Map<UUID, Integer> openInventories;

    public EnchantListener(TenJava plugin) {
        super(plugin);
        this.plugin = plugin;
        this.openInventories = new HashMap<>();
    }

    @EventHandler
    public void onEnchantPrepare(PrepareItemEnchantEvent e) {
        Integer previousLevel = openInventories.get(e.getEnchanter().getUniqueId());
        if(previousLevel == null) {
            previousLevel = e.getEnchanter().getLevel();
            openInventories.put(e.getEnchanter().getUniqueId(), previousLevel);
        }

        int availablePower = plugin.getCellUtils().getAdjacentPower(e.getEnchantBlock());
        // Enchant from power available
        int maxEnchantLvl = availablePower / Config.POWER_PER_LEVEL;
        maxEnchantLvl += previousLevel;

        //e.getEnchanter().sendMessage("Set lvl to " +  e.getEnchanter().getLevel());
        e.getEnchanter().setLevel(maxEnchantLvl);
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent e) {
        int remaining = plugin.getCellUtils().removeAdjacentPower(e.getEnchantBlock(), e.getExpLevelCost() * Config.POWER_PER_LEVEL);
        //e.getEnchanter().sendMessage("remaining: " + remaining);
        if(remaining > 0) {
            int lvlToRemove = openInventories.get(e.getEnchanter().getUniqueId()) - remaining / Config.POWER_PER_LEVEL;
            //e.getEnchanter().sendMessage("to remove: " + lvlToRemove);
            openInventories.put(e.getEnchanter().getUniqueId(), lvlToRemove);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Integer previousLevel = openInventories.get(player.getUniqueId());
        if(previousLevel != null) {
            //player.sendMessage("Setting level back to " + previousLevel);
            player.setLevel(previousLevel);
            openInventories.remove(e.getPlayer().getUniqueId());
        }
    }

}
