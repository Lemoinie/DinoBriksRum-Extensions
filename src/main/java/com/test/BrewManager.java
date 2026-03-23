package com.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BrewManager {

    private final JavaPlugin plugin;
    public final Map<String, ItemStack> unagedBrews = new LinkedHashMap<>();
    public final Map<String, ItemStack> agedBrews = new LinkedHashMap<>();
    private boolean isInitialized = false;

    // A comprehensive list of brew modifiers from the datapack
    private final String[] brewModifiers = {
        // Absinthe
        "absinthe/absinthe", "absinthe/ghastly_cry/final", "absinthe/pinewood/unaged",
        // Beer
        "beer/beer", "beer/pumpkin_beer/final", "beer/stavroforos/unaged",
        // Mead
        "mead/mead", "mead/corpse/final", "mead/song/final", "mead/illager/final",
        // Tequila
        "tequila/tequila", "tequila/roscuros/unaged", "tequila/desert/final_beverage",
        // Vodka
        "vodka/vodka", "vodka/blue_comet/final", "vodka/drako_horilka/unaged", "vodka/amber/final", "vodka/jungle/final",
        // Whiskey
        "whiskey/whiskey", "whiskey/chorus_bourbon/unaged", "whiskey/torchflower/final", "whiskey/dwarven/final", "whiskey/fire/final",
        // Wine
        "wine/wine", "wine/crimson/unaged", "wine/kings_strength/unaged", "wine/elven/unaged", "wine/sparkling/unaged",
        // Rum
        "rum/rum", "rum/gnome_rum/final", "rum/creeper_cider/final", "rum/flyneberry_rum/final", "rum/black_midnight/unaged", "rum/bog_grog/final"
    };

    public BrewManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initializeCache(org.bukkit.entity.Player context) {
        if (isInitialized) return;

        try {
            // Spawn the chest exactly above the executing player to guarantee the Chunk is fully loaded and ticking 
            Location loc = context.getLocation().clone();
            loc.setY(loc.getWorld().getMaxHeight() - 1); 
            loc.getBlock().setType(Material.CHEST);
            String worldKey = loc.getWorld().getKey().toString(); 
            
            for (String mod : brewModifiers) {
                // 1. Grab fresh snapshot, inject potion, push to World
                org.bukkit.block.Chest chest = (org.bukkit.block.Chest) loc.getBlock().getState();
                chest.getInventory().setItem(0, new ItemStack(Material.POTION));
                chest.update(); // MUST update to flush item into world coordinates!
                
                // 2. Let Vanilla mutate the World NBT natively
                String cmdGenerate = "minecraft:execute in " + worldKey + " run item modify block " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " container.0 db_drinks:" + mod;
                boolean success = Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdGenerate);
                if (!success) plugin.getLogger().warning("Datapack command format failed: " + cmdGenerate);
                
                // 3. Grab fresh snapshot of the mutated World block to retrieve item
                org.bukkit.block.Chest generatedChest = (org.bukkit.block.Chest) loc.getBlock().getState();
                ItemStack generated = generatedChest.getInventory().getItem(0);
                unagedBrews.put(mod, generated != null ? generated.clone() : new ItemStack(Material.POTION));
                
                if (generated != null) {
                    // 4. Age the existing item natively in the World
                    String cmdAge = "minecraft:execute in " + worldKey + " run item modify block " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " container.0 db_drinks:age/16_days";
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdAge);
                    
                    // 5. Read the final state
                    org.bukkit.block.Chest agedChest = (org.bukkit.block.Chest) loc.getBlock().getState();
                    ItemStack aged = agedChest.getInventory().getItem(0);
                    agedBrews.put(mod, aged != null ? aged.clone() : new ItemStack(Material.POTION));
                }
            }
            loc.getBlock().setType(Material.AIR);
            plugin.getLogger().info("Successfully loaded features and lazily cached items from DBRE Datapack via Chest Method!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize cached items due to an error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Force true so we don't spam exceptions on every click if it fails
            isInitialized = true;
        }
    }
    
    public List<String> getAllBrewNames() {
        List<String> combined = new ArrayList<>();
        combined.addAll(List.of(brewModifiers));
        for (String mod : brewModifiers) {
            combined.add(mod + "_aged");
        }
        return combined;
    }

    public ItemStack getBrew(String modifierName, org.bukkit.entity.Player context) {
        if (!isInitialized && context != null) {
            initializeCache(context);
        }
        
        if (modifierName.endsWith("_aged")) {
            return agedBrews.get(modifierName.replace("_aged", ""));
        }
        return unagedBrews.get(modifierName);
    }
}
