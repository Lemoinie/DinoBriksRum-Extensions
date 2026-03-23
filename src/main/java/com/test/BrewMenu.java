package com.test;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BrewMenu implements Listener {

    private final BrewManager manager;

    public BrewMenu(BrewManager manager) {
        this.manager = manager;
    }

    public void openMenu(Player player, int page) {
        if (manager.unagedBrews.isEmpty()) {
            manager.initializeCache(player);
        }

        List<ItemStack> allItems = new ArrayList<>();
        allItems.addAll(manager.unagedBrews.values());
        allItems.addAll(manager.agedBrews.values());

        int maxPages = (int) Math.ceil((double) allItems.size() / 45);
        if (maxPages <= 0) maxPages = 1; // Prevent page becoming -1 when size is 0
        if (page < 0) page = 0;
        if (page >= maxPages) page = maxPages - 1;

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "DinoBriks Brews - Page " + (page + 1));
        
        int startIndex = page * 45;
        for (int i = 0; i < 45; i++) {
            if (startIndex + i < allItems.size()) {
                inv.setItem(i, allItems.get(startIndex + i).clone());
            }
        }

        // Pagination buttons
        if (page > 0) {
            ItemStack prev = new ItemStack(org.bukkit.Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            prev.setItemMeta(meta);
            inv.setItem(45, prev);
        }

        if (page < maxPages - 1) {
            ItemStack next = new ItemStack(org.bukkit.Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "Next Page");
            next.setItemMeta(meta);
            inv.setItem(53, next);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith(ChatColor.GOLD + "DinoBriks Brews - Page ")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int page = Integer.parseInt(title.split("Page ")[1]) - 1;

            if (event.getCurrentItem() == null) return;

            if (event.getSlot() == 45 && event.getCurrentItem().getType() == org.bukkit.Material.ARROW) {
                openMenu(player, page - 1);
            } else if (event.getSlot() == 53 && event.getCurrentItem().getType() == org.bukkit.Material.ARROW) {
                openMenu(player, page + 1);
            } else if (event.getSlot() < 45) {
                ItemStack clicked = event.getCurrentItem();
                if (clicked.getType() != org.bukkit.Material.AIR) {
                    player.getInventory().addItem(clicked.clone());
                    player.sendMessage(ChatColor.GREEN + "Given " + clicked.getItemMeta().getDisplayName());
                }
            }
        }
    }
}
