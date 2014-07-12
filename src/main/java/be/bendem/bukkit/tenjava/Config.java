package be.bendem.bukkit.tenjava;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bendem
 */
public class Config {

    public final Map<Material, Integer> FUELS      = new HashMap<>();
    public final Map<Material, Integer> CONTAINERS = new HashMap<>();
    public final boolean KEEP_ENERGY_WHEN_PICKUP;
    public final int     POWER_PER_LEVEL;
    public final int     DIG_COST;
    public final int     MAX_DIG_DISTANCE;

    public Config(FileConfiguration config) {
        boolean added;
        // Load fuels
        added = false;
        ConfigurationSection fuel = config.getConfigurationSection("fuels");
        for(String material : fuel.getKeys(false)) {
            FUELS.put(Material.valueOf(material.toUpperCase()), fuel.getInt(material));
            added = true;
        }
        if(!added) {
            defaultFuel();
        }

        // Load Cells
        added = false;
        ConfigurationSection cells = config.getConfigurationSection("cells");
        for(String material : cells.getKeys(false)) {
            CONTAINERS.put(Material.valueOf(material.toUpperCase()), cells.getInt(material));
            added = true;
        }
        if(!added) {
            defaultContainer();
        }

        KEEP_ENERGY_WHEN_PICKUP = config.getBoolean("keep-energy-when-pickup", true);
        POWER_PER_LEVEL = config.getInt("power-per-enchant-level", 25);
        DIG_COST = config.getInt("dig-cost", 10);
        MAX_DIG_DISTANCE = config.getInt("max-dig-distance", 50);
    }

    private void defaultFuel() {
        FUELS.put(Material.REDSTONE, 5);
        FUELS.put(Material.IRON_INGOT, 10);
        FUELS.put(Material.GOLD_INGOT, 20);
        FUELS.put(Material.DIAMOND, 50);
        FUELS.put(Material.EMERALD, 100);
    }

    private void defaultContainer() {
        CONTAINERS.put(Material.REDSTONE_BLOCK, 100);
        CONTAINERS.put(Material.IRON_BLOCK, 200);
        CONTAINERS.put(Material.GOLD_BLOCK, 500);
        CONTAINERS.put(Material.DIAMOND_BLOCK, 1_000);
        CONTAINERS.put(Material.EMERALD_BLOCK, 10_000);
    }

}
