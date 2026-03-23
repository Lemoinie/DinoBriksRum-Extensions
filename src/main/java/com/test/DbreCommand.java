package com.test;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DbreCommand implements CommandExecutor, TabCompleter {

    private final NamespacedKey ageKey;
    private final BrewManager manager;
    private final BrewMenu menu;

    public DbreCommand(MyPlugin plugin, BrewManager manager, BrewMenu menu) {
        this.ageKey = NamespacedKey.fromString("aged_days"); // Resolves to minecraft:aged_days
        this.manager = manager;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /dbre <age|menu|give>");
            return true;
        }

        if (args[0].equalsIgnoreCase("menu")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can open menus.");
                return true;
            }
            menu.openMenu((Player) sender, 0);
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("dbre.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /dbre give <player> <brew_modifier>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            String brewName = args[2];
            Player senderPlayer = (sender instanceof Player) ? (Player) sender : target;
            ItemStack item = manager.getBrew(brewName, senderPlayer);
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Unknown brew format: " + brewName);
                return true;
            }

            target.getInventory().addItem(item.clone());
            sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " a " + brewName);
            return true;
        }

        if (args[0].equalsIgnoreCase("age")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this.");
                return true;
            }
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "You must hold an item.");
                return true;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage(ChatColor.RED + "This item has no data.");
                return true;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (args.length == 1) {
                // Check age
                if (container.has(ageKey, PersistentDataType.BYTE)) {
                    byte age = container.get(ageKey, PersistentDataType.BYTE);
                    player.sendMessage(ChatColor.GREEN + "This rum has aged for: " + ChatColor.YELLOW + age + " days.");
                } else if (container.has(ageKey, PersistentDataType.INTEGER)) {
                    int age = container.get(ageKey, PersistentDataType.INTEGER);
                    player.sendMessage(ChatColor.GREEN + "This rum has aged for: " + ChatColor.YELLOW + age + " days.");
                } else {
                    player.sendMessage(ChatColor.RED + "This item does not have any 'aged_days' data.");
                }
                return true;
            } else if (args.length == 2) {
                // Set age
                try {
                    byte newAge = Byte.parseByte(args[1]);
                    container.set(ageKey, PersistentDataType.BYTE, newAge);
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "Successfully set the aged days to " + newAge + ".");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid number format. Please provide a valid byte value (0-127).");
                }
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /dbre age [value]");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Unknown argument. Usage: /dbre <age|menu|give>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("age");
            completions.add("menu");
            completions.add("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(manager.getAllBrewNames());
        }
        
        // Filter completions
        if (!completions.isEmpty()) {
            String current = args[args.length - 1].toLowerCase();
            completions.removeIf(s -> !s.toLowerCase().startsWith(current));
        }

        return completions;
    }
}
