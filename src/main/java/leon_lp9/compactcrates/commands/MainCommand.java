package leon_lp9.compactcrates.commands;

import leon_lp9.compactcrates.CompactCrates;
import leon_lp9.compactcrates.ItemBuilder;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
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
                sender.sendMessage("§e/cc admin setParticle [particle] §7- §eSet the particle of the crate");

                return true;
            }

            if (args[0].equalsIgnoreCase("setParticle")) {
                if (!sender.hasPermission("compactcrates.admin.setparticle")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }



                return true;
            }

        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("gui")) {
                    if (!sender.hasPermission("compactcrates.admin.gui")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        //player.openInventory(CompactCrates.getInstance().getAdminGUI().getInventory());
                        return true;
                    } else {
                        sender.sendMessage(CompactCrates.getPrefix() + "You must be a player to use this command!");
                    }
                    return true;

                } else if (args[1].equalsIgnoreCase("setParticle")) {
                    if (!sender.hasPermission("compactcrates.admin.setparticle")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }

                }
                return true;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("setParticle")) {
                    if (!sender.hasPermission("compactcrates.admin.setparticle")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }
                    //ArrayListWithAllParticles
                    ArrayList<String> particles = new ArrayList<>();
                    for (Particle particle : Particle.values()) {
                        particles.add(particle.name());
                    }

                    if (particles.contains(args[2].toUpperCase())){
                        CompactCrates.getInstance().getConfig().set("Particle", args[2].toUpperCase());
                        CompactCrates.getInstance().saveConfig();
                    }else {
                        sender.sendMessage(CompactCrates.getPrefix() + "§cThis particle does not exist!");
                    }
                    return true;
                }
            }
        }

        if (args.length == 4){
            if (args[2].equalsIgnoreCase("show")) {
                ArrayList<String> types = new ArrayList<>();
                CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                    types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                });

                //Show keys
                Player target = Bukkit.getPlayer(args[3]);
                if (target == null) {
                    sender.sendMessage(CompactCrates.getPrefix() + "§cThis player is not online!");
                    return true;
                }
                sender.sendMessage(CompactCrates.getPrefix() + "§a" + target.getName() + "'s keys:");
                types.forEach(s -> {
                    if (CompactCrates.getInstance().getUserConfig().contains(target.getUniqueId() + "." + s + ".Keys")) {
                        sender.sendMessage("§7- §a" + s + ": §e" + CompactCrates.getInstance().getUserConfig().getString(target.getUniqueId() + "." + s + ".Keys"));
                    } else {
                        sender.sendMessage("§7- §a" + s + ": §e0");
                    }
                });
                return true;
            }
        }

        if (args.length == 6){
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys")) {
                    if (!sender.hasPermission("compactcrates.admin.keys")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });
                    if (args[2].equalsIgnoreCase("give")){
                        Player target = Bukkit.getPlayer(args[3]);
                        if (target == null){
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis player is not online!");
                            return true;
                        }
                        if (types.contains(args[4])){

                            if (!CompactCrates.getInstance().getUserConfig().contains(target.getUniqueId() + "." + args[4] + ".Keys")){
                                CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", 0);
                            }

                            CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", Integer.parseInt(CompactCrates.getInstance().getUserConfig().getString(target.getUniqueId() + "." + args[4] + ".Keys")) + Integer.parseInt(args[5]));
                            CompactCrates.getInstance().saveUserConfig();
                            sender.sendMessage(CompactCrates.getPrefix() + "§aYou have given " + target.getName() + " " + args[5] + " " + args[4] + " keys!");
                           return true;
                        }else {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis crate does not exist!");
                            return true;
                        }
                    }else if (args[2].equalsIgnoreCase("set")){
                        Player target = Bukkit.getPlayer(args[3]);
                        if (target == null){
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis player is not online!");
                            return true;
                        }
                        if (types.contains(args[4])){

                            CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", Integer.parseInt(args[5]));
                            CompactCrates.getInstance().saveUserConfig();
                            sender.sendMessage(CompactCrates.getPrefix() + "§aYou have set " + target.getName() + "'s " + args[4] + " keys to " + args[5] + "!");
                            return true;
                        }else {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis crate does not exist!");
                            return true;
                        }
                    }else if (args[2].equalsIgnoreCase("take")){
                        Player target = Bukkit.getPlayer(args[3]);
                        if (target == null){
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis player is not online!");
                            return true;
                        }
                        if (types.contains(args[4])){

                            if (!CompactCrates.getInstance().getUserConfig().contains(target.getUniqueId() + "." + args[4] + ".Keys")){
                                CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", 0);
                            }

                            CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", Integer.parseInt(CompactCrates.getInstance().getUserConfig().getString(target.getUniqueId() + "." + args[4] + ".Keys")) - Integer.parseInt(args[5]));
                            CompactCrates.getInstance().saveUserConfig();
                            sender.sendMessage(CompactCrates.getPrefix() + "§aYou have taken " + args[5] + " " + args[4] + " keys from " + target.getName() + "!");
                            return true;
                        }else {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis crate does not exist!");
                            return true;
                        }
                    }else {
                        sender.sendMessage(CompactCrates.getPrefix() + "§cThis subcommand does not exist!");
                        return true;
                    }
                }
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
                return List.of("gui", "keys", "setParticle");
            }
        }else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys")) {
                    return List.of("give", "remove", "set", "show");
                }else if (args[1].equalsIgnoreCase("setParticle")) {
                    Particle[] particles = Particle.values();
                    List<String> particleNames = new ArrayList<>();
                    for (Particle particle : particles) {
                        particleNames.add(particle.name());
                    }
                    return particleNames;
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
                if (args[1].equalsIgnoreCase("keys") && !args[2].equalsIgnoreCase("show")) {
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });
                    return types;
                }
            }
        }else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys") && !args[2].equalsIgnoreCase("show")) {
                    return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
                }
            }
        }

        return null;
    }
}
