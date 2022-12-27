package leon_lp9.compactcrates.commands;

import leon_lp9.compactcrates.CompactCrates;
import leon_lp9.compactcrates.ItemBuilder;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§e§lCompactCrates §7by §eleon_lp9");
            sender.sendMessage("§7Version: §e" + CompactCrates.getInstance().getDescription().getVersion());
            sender.sendMessage("§7Commands:");
            sender.sendMessage("§e/compactcrates help §7- §eShows this help message");
            sender.sendMessage("§e/compactcrates reload §7- §eReloads the plugin");
            sender.sendMessage("§e/compactcrates placechest §7- §eGives you a crate to place");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage("§e§lCompactCrates §7by §eleon_lp9");
                sender.sendMessage("§7Version: §e" + CompactCrates.getInstance().getDescription().getVersion());
                sender.sendMessage("§7Commands:");
                sender.sendMessage("§e/compactcrates help §7- §eShows this help message");
                sender.sendMessage("§e/compactcrates reload §7- §eReloads the plugin");
                sender.sendMessage("§e/compactcrates placechest §7- §eGives you a crate to place");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("compactcrates.reload")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }
                CompactCrates.getInstance().reloadConfig();
                CompactCrates.getInstance().createNewConfig();
                SpawnCratesManager.spawnCrates();
                sender.sendMessage(CompactCrates.getPrefix() + "§aConfig reloaded!");
                return true;
            }

            if (args[0].equalsIgnoreCase("placechest")) {
                if (!sender.hasPermission("compactcrates.placechest")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.getInventory().addItem(new ItemBuilder(Material.CHEST).setDisplayName("§e§lCrate").setLore("§7Right click to place!").setLocalizedName("crate").build());
                    player.sendMessage(CompactCrates.getPrefix() + "§aYou have received a crate to place!");

                    return true;
                } else {
                    sender.sendMessage(CompactCrates.getPrefix() + "You must be a player to use this command!");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("admin")) {
                if (!sender.hasPermission("compactcrates.admin")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                sender.sendMessage("§7Commands:");
                sender.sendMessage("§e/cc admin gui §7- §eOpen the admin gui");
                sender.sendMessage("§e/cc admin keys [do] [player] [type] [amount] §7- §eManage keys");

                return true;
            }

        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return List.of("reload", "placechest", "help", "admin");
        }else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("admin")) {
                return List.of("gui", "keys");
            }
        }else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys")) {
                    return List.of("give", "remove", "set", "show");
                }
            }
        }else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        players.add(player.getName());
                    }
                    return players;
                }
            }
        }else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys")) {
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name"));
                    });
                    return types;
                }
            }
        }

        return null;
    }
}
