package be.bendem.bukkit.tenjava;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bendem
 */
public class Config {

    public static final Map<Material, Integer> FUELS;
    static {
        FUELS = new HashMap<>();
        FUELS.put(Material.REDSTONE, 5);
    }

    public static final Map<Material, Integer> CONTAINERS;
    static {
        CONTAINERS = new HashMap<>();
        CONTAINERS.put(Material.REDSTONE_BLOCK, 100);
        CONTAINERS.put(Material.IRON_BLOCK, 200);
        CONTAINERS.put(Material.GOLD_BLOCK, 500);
        CONTAINERS.put(Material.DIAMOND_BLOCK, 1_000);
        CONTAINERS.put(Material.EMERALD_BLOCK, 10_000);
    }

    public static final boolean KEEP_ENERGY_WHEN_PICKUP = true;
    public static final int POWER_PER_LEVEL = 25;

}
