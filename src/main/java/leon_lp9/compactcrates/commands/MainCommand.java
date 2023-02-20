package leon_lp9.compactcrates.commands;

import leon_lp9.compactcrates.CompactCrates;
import leon_lp9.compactcrates.InventoryManager;
import leon_lp9.compactcrates.builder.ItemBuilder;
import leon_lp9.compactcrates.UpdateChecker;
import leon_lp9.compactcrates.builder.ItemChecker;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            new UpdateChecker(CompactCrates.getInstance(), 107018).getVersion(version -> {
                sender.sendMessage("§e§lCompactCrates §7by §eleon_lp9");
                sender.sendMessage("§7Version: §e" + CompactCrates.getInstance().getDescription().getVersion());
                sender.sendMessage("§7The latest version is §e" + version);
                sender.sendMessage("§7Commands:");
                sender.sendMessage("§e/compactcrates help §7- §eShows this help message");
                sender.sendMessage("§e/compactcrates reload §7- §eReloads the plugin");
                sender.sendMessage("§e/compactcrates placechest §7- §eGives you a crate to place");
                sender.sendMessage("§e/compactcrates admin §7- §eAdmin settings");
                sender.sendMessage("§e/compactcrates item §7- §eEdit item settings");
                sender.sendMessage("§e/testvote <PlayerName> §7- §eVotifer test vote");
            });
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
                sender.sendMessage("§e/compactcrates admin §7- §eAdmin settings");
                sender.sendMessage("§e/compactcrates item §7- §eEdit item settings");
                sender.sendMessage("§e/testvote <PlayerName> §7- §eVotifer test vote");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("compactcrates.reload")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                CompactCrates.getInstance().saveDefaultConfig();
                CompactCrates.getInstance().getConfig().options().copyDefaults(true);

                //safe defaults of language file
                if (!new File(CompactCrates.getInstance().getDataFolder(), "language.yml").exists()) {
                    CompactCrates.getInstance().saveResource("language.yml", false);
                }
                if (!new File(CompactCrates.getInstance().getDataFolder(), "chests.yml").exists()) {
                    CompactCrates.getInstance().saveResource("chests.yml", false);
                }

                CompactCrates.getInstance().reloadConfig();
                CompactCrates.getInstance().createNewConfig();


                CompactCrates.getInstance().getLanguageConfig().options().copyDefaults(true);
                CompactCrates.getInstance().getChestConfig().options().copyDefaults(true);

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

                sender.sendMessage("§7Admin Commands:");
                sender.sendMessage("§e/cc admin gui §7- §eOpen the admin gui");
                sender.sendMessage("§e/cc admin keys [do] [player] [type] [amount] §7- §eManage keys");
                sender.sendMessage("§e/cc admin setParticle [particle] §7- §eSet the particle of the crate");
                sender.sendMessage("§e/cc admin create <ID> <SLOT> <MATERIAL> <NAME> §7- §eCreate a new crate");
                sender.sendMessage("§e/cc admin deletecrate <ID> §7- §eRemove a crate");
                sender.sendMessage("§e/cc admin renamecrate <ID> <NewNAME> §7- §eRename a crate");
                sender.sendMessage("§e/cc admin setslot <ID> <NewSLOW> §7- §eSet the slot of a crate");
                sender.sendMessage("§e/cc admin changetype <ID> <NewMATERIAL> §7- §eSet the material of a crate");
                sender.sendMessage("§e/cc admin setsize <Size> §7- §eSet the size of the Menu");

                return true;
            }

            if (args[0].equalsIgnoreCase("setParticle")) {
                if (!sender.hasPermission("compactcrates.admin.setparticle")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("item")){
                if (!sender.hasPermission("compactcrates.item")){
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                sender.sendMessage("§7Item Commands:");
                sender.sendMessage("§e/cc item addCommand <Comannds> §7- §eAdd a command to the item");
                sender.sendMessage("§e/cc item addProbability <Probability> §7- §eAdd a probability to the item");

            }

        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("setParticle")) {
                    if (!sender.hasPermission("compactcrates.admin.setparticle")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }

                }else if (args[1].equalsIgnoreCase("gui")){
                    if (!sender.hasPermission("compactcrates.admin.gui")){
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }

                    Player player = (Player) sender;

                    player.sendMessage("§aOpening GUI...");

                    Inventory inventory = Bukkit.createInventory(null, 45, "§6CompactCrates Admin GUI");

                    ArrayList<ItemStack> items = new ArrayList<>();

                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        ItemStack itemStack = new ItemBuilder(Material.getMaterial(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Type"))).setLocalizedName("crate").setDisplayName(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name").replace("&", "§")).setLocalizedName(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID")).build();
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        ArrayList<String> lore = new ArrayList<>();

                        if (!CompactCrates.useMysql) {
                            if (!CompactCrates.getInstance().getUserConfig().contains(player.getUniqueId().toString() + "." + CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID") + ".Keys")) {
                                CompactCrates.getInstance().getUserConfig().set(player.getUniqueId().toString() + "." + CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID") + ".Keys", 0);
                                CompactCrates.getInstance().saveUserConfig();
                            }
                        }

                        lore.add("§7");
                        lore.add(CompactCrates.getInstance().getLanguageConfig().getString("leftClick").replace("&", "§"));
                        lore.add(CompactCrates.getInstance().getLanguageConfig().getString("rightClick").replace("&", "§"));
                        if (player.hasPermission("compactcrates.admin") && player.getGameMode().equals(GameMode.CREATIVE)) {
                            lore.add(CompactCrates.getInstance().getLanguageConfig().getString("middleClick").replace("&", "§"));
                        }
                        lore.add("§7");


                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);

                        items.add(itemStack);
                    });

                    for (int i = 0; i < items.size(); i++) {
                        inventory.setItem(i, items.get(i));
                    }

                    player.openInventory(inventory);

                }
                return true;
            }else if (args[0].equalsIgnoreCase("setmaterial")){
                if (!sender.hasPermission("compactcrates.admin.setmaterial")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                ArrayList<String> mats = new ArrayList<>();
                for (int i = 0; i < Material.values().length; i++) {
                    mats.add(Material.values()[i].name());
                }

                if (mats.contains(args[1].toUpperCase())) {

                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("chestsPositions").getKeys(false).forEach(s -> {
                        Location loc = new Location(Bukkit.getWorld(CompactCrates.getInstance().getChestConfig().getString("chestsPositions." + s + ".world")),
                                CompactCrates.getInstance().getChestConfig().getDouble("chestsPositions." + s + ".x"),
                                CompactCrates.getInstance().getChestConfig().getDouble("chestsPositions." + s + ".y"),
                                CompactCrates.getInstance().getChestConfig().getDouble("chestsPositions." + s + ".z"));

                        if (sender instanceof Player) {
                            Player player = (Player) sender;

                            if (player.getWorld().equals(loc.getWorld())) {
                                if (player.getLocation().distance(loc) <= 5) {
                                    CompactCrates.getInstance().getChestConfig().set("chestsPositions." + s + ".type", args[1].toUpperCase());
                                    CompactCrates.getInstance().saveChestsConfig();
                                    SpawnCratesManager.spawnCrates();
                                    sender.sendMessage(CompactCrates.getPrefix() + "§aYou have changed the material of the crate!");
                                }
                            }

                        } else {
                            sender.sendMessage(CompactCrates.getPrefix() + "You must be a player to use this command!");
                        }
                    });

                    CompactCrates.getInstance().saveConfig();
                    sender.sendMessage(CompactCrates.getPrefix() + "§aYou have set the crate material to " + args[1].toUpperCase());
                }else{
                    sender.sendMessage(CompactCrates.getPrefix() + "§cThat is not a valid material!");
                }
            }else if (args[0].equalsIgnoreCase("item")){
                if (!sender.hasPermission("compactcrates.item")){
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                if (args[1].equalsIgnoreCase("addCommand")){
                    sender.sendMessage(CompactCrates.getPrefix() + "§cPlease use /cc item addCommand <Commands>");
                    sender.sendMessage(CompactCrates.getPrefix() + "§cSplit the commands with a '/'!");
                }else if (args[1].equalsIgnoreCase("addProbability")){
                    sender.sendMessage(CompactCrates.getPrefix() + "§cPlease use /cc item addProbability <Probability>");
                }else if (args[1].equalsIgnoreCase("setPreviewLore")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "§cPlease use /cc item setPreviewLore <Lore>");
                    sender.sendMessage(CompactCrates.getPrefix() + "§cNext line lore with a '/n'!");
                }else if (args[1].equalsIgnoreCase("preview")){
                    Player player = (Player) sender;

                    Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "§6CompactCrates Admin GUI Preview");

                    ItemStack is = player.getInventory().getItemInMainHand();

                    ItemStack stackwithlore = null;

                    inventory.setItem(2, is);


                    if (new ItemChecker(is).hasCustomTag("previewlore", ItemTagType.STRING)) {
                        ItemBuilder itemBuilder = new ItemBuilder(is);

                        String[] lore = new ItemChecker(is).getCustomTag("previewlore", ItemTagType.STRING).toString().split("/n");

                        for (String s : lore) {
                            itemBuilder.addLineLore(s.replace("&", "§"));
                        }

                        inventory.setItem(2, itemBuilder.build());

                        stackwithlore = itemBuilder.build();
                    }

                    if (new ItemChecker((stackwithlore == null ? is : stackwithlore)).hasCustomTag("commands", ItemTagType.STRING)){


                        ItemBuilder itemBuilder2 = new ItemBuilder(Material.PAPER);
                        itemBuilder2.setDisplayName("§6Commands");

                        String[] command2s = new ItemChecker((stackwithlore == null ? is : stackwithlore)).getCustomTag("commands", ItemTagType.STRING).toString().split("/");

                        for (String s : command2s) {
                            itemBuilder2.addLineLore("§7- /" + s.replace("&", "§"));
                        }

                        inventory.setItem(4, itemBuilder2.build());


                        ItemBuilder itemBuilder = new ItemBuilder((stackwithlore == null ? is : stackwithlore));

                        if (CompactCrates.getInstance().getConfig().contains("CommandRewardPreview") && CompactCrates.getInstance().getConfig().getBoolean("CommandRewardPreview")) {
                            ArrayList<String> commands = (ArrayList<String>) CompactCrates.getInstance().getConfig().getStringList("CommandRewardLores");
                            for (String command2 : commands) {
                                itemBuilder.addLineLore(command2.replace("&", "§"));
                            }
                        }

                        inventory.setItem(2, itemBuilder.build());

                    }
                    player.openInventory(inventory);


                }
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
                }else if (args[1].equalsIgnoreCase("setSize")) {
                    //checkPerms
                    if (!sender.hasPermission("compactcrates.admin.setsize")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }

                    CompactCrates.getInstance().getConfig().set("inventorySize", Integer.parseInt(args[2]));
                }else if (args[1].equalsIgnoreCase("deletecrate")){
                    //checkPerms
                    if (!sender.hasPermission("compactcrates.admin.deletecrate")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }

                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });

                    if (types.contains(args[2])) {

                        CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                            if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID").equalsIgnoreCase(args[2])) {
                                CompactCrates.getInstance().getChestConfig().set("cratesTypes." + s, null);
                                CompactCrates.getInstance().saveChestsConfig();
                                sender.sendMessage(CompactCrates.getPrefix() + "§aCrate deleted!");
                            }
                        });
                    }else {
                        sender.sendMessage(CompactCrates.getPrefix() + "§cThis crate does not exist!");
                    }
                }
            }
        }

        if (args.length >= 3){
            if (args[0].equalsIgnoreCase("item")){

                if (!sender.hasPermission("compactcrates.admin.item")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

                if (sender instanceof Player){
                    Player player = (Player) sender;

                    if (args[1].equalsIgnoreCase("addCommand")){
                        StringBuilder commands = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            commands.append(args[i]).append(" ");
                        }

                        if (commands.toString().startsWith("/") || commands.toString().endsWith("/")){
                            sender.sendMessage("§cYou do not have to start the command with /. It will be added automatically! Only use to Split the commands!");
                            sender.sendMessage("§cExample: /cc item addCommand give %player% diamond 1/say %player% Hello!/say %player% Hi!");
                            return true;
                        }

                        if (player.getInventory().getItemInHand().getType() == Material.AIR){
                            sender.sendMessage(CompactCrates.getPrefix() + "§cYou must hold an item in your hand!");
                            return true;
                        }

                        ItemBuilder itemBuilder = new ItemBuilder(player.getInventory().getItemInHand());
                        itemBuilder.addCustomTag("commands", ItemTagType.STRING, commands.toString());
                        player.getInventory().setItemInHand(itemBuilder.build());

                        String[] commandsArray = commands.toString().split("/");

                        player.sendMessage("§aYou have added the following commands to the item!");
                        for (int i = 0; i < commandsArray.length; i++) {
                            player.sendMessage("§7/" + commandsArray[i]);
                        }

                        return true;

                    }else if (args[1].equalsIgnoreCase("addProbability")) {
                        if (player.getInventory().getItemInHand().getType() == Material.AIR) {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cYou must hold an item in your hand!");
                            return true;
                        }

                        try{
                            ItemBuilder itemBuilder = new ItemBuilder(player.getInventory().getItemInHand());
                            itemBuilder.addCustomTag("probability", ItemTagType.DOUBLE, Double.parseDouble(args[2]));
                            player.getInventory().setItemInHand(itemBuilder.build());

                            player.sendMessage(CompactCrates.getPrefix() + "§aYou have added the " + args[2] + " probability to the item!");
                        }catch (NumberFormatException e){
                            sender.sendMessage(CompactCrates.getPrefix() + "§cYou must enter a number!");
                            return true;
                        }
                    }else if (args[1].equalsIgnoreCase("addWinParticle")){
                        if (player.getInventory().getItemInHand().getType() == Material.AIR) {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cYou must hold an item in your hand!");
                            return true;
                        }

                        //ArrayListWithAllParticles
                        List<String> particles = List.of("fallingBlocks".toUpperCase(), "fireworks".toUpperCase());

                        if (particles.contains(args[2].toUpperCase())){
                            ItemBuilder itemBuilder = new ItemBuilder(player.getInventory().getItemInHand());
                            itemBuilder.addCustomTag("winparticle", ItemTagType.STRING, args[2].toUpperCase());
                            player.getInventory().setItemInHand(itemBuilder.build());

                            player.sendMessage(CompactCrates.getPrefix() + "§aYou have added the " + args[2].toUpperCase() + " particle to the item!");
                        }else {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cThis particle does not exist!");
                        }
                    }else if (args[1].equalsIgnoreCase("setPreviewLore")){
                        if (player.getInventory().getItemInHand().getType() == Material.AIR) {
                            sender.sendMessage(CompactCrates.getPrefix() + "§cYou must hold an item in your hand!");
                            return true;
                        }

                        StringBuilder lore = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            lore.append(args[i]).append(" ");
                        }

                        ItemBuilder itemBuilder = new ItemBuilder(player.getInventory().getItemInHand());
                        itemBuilder.addCustomTag("previewlore", ItemTagType.STRING, lore.toString());
                        player.getInventory().setItemInHand(itemBuilder.build());

                        player.sendMessage(CompactCrates.getPrefix() + "§aYou have added the " + lore.toString() + " lore to the item!");
                    }
                }
            }
        }

        if (args.length >= 4){
            if (args[2].equalsIgnoreCase("show")) {

                if (!sender.hasPermission("compactcrates.admin.show")) {
                    sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                    return true;
                }

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
                        sender.sendMessage("§7- §a" + s + ": §e" + InventoryManager.getCrateAmount(target.getUniqueId(), s));
                    } else {
                        sender.sendMessage("§7- §a" + s + ": §e0");
                    }
                });
                return true;
            }
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("changetype")) {
                    if (!sender.hasPermission("compactcrates.admin.changetype")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });

                    if (types.contains(args[2])){

                        CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                            if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID").equalsIgnoreCase(args[2])) {

                                ArrayList<String> mats = new ArrayList<>();
                                for (int i = 0; i < Material.values().length; i++) {
                                    mats.add(Material.values()[i].name());
                                }

                                if (mats.contains(args[3].toUpperCase())) {
                                    CompactCrates.getInstance().getChestConfig().set("cratesTypes." + s + ".Type", args[3].toUpperCase());
                                    CompactCrates.getInstance().saveChestsConfig();
                                    sender.sendMessage(CompactCrates.getPrefix() + "§aYou have changed the type of the crate to " + args[2] + "!");
                                }else {
                                    sender.sendMessage(CompactCrates.getPrefix() + "§cThis material does not exist!");
                                }
                            }
                        });
                    }

                }else if (args[1].equalsIgnoreCase("setslot")) {
                    if (!sender.hasPermission("compactcrates.admin.setslot")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });

                    if (types.contains(args[2])){

                        CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                            if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID").equalsIgnoreCase(args[2])) {

                                if (Integer.parseInt(args[3]) <= Integer.parseInt(CompactCrates.getInstance().getConfig().getString("inventorySize")) && Integer.parseInt(args[3]) >= 0) {
                                    CompactCrates.getInstance().getChestConfig().set("cratesTypes." + s + ".Slot", args[3].toUpperCase());
                                    CompactCrates.getInstance().saveChestsConfig();
                                    sender.sendMessage(CompactCrates.getPrefix() + "§aYou have changed the type of the crate to " + args[2] + "!");
                                }else {
                                    sender.sendMessage(CompactCrates.getPrefix() + "§cThis slot does not exist!");
                                }
                            }
                        });
                    }

                }else if (args[1].equalsIgnoreCase("renamecrate")) {
                    if (!sender.hasPermission("compactcrates.admin.renamecrate")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });

                    if (types.contains(args[2])) {

                        CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                            if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID").equalsIgnoreCase(args[2])) {

                                StringBuilder name = new StringBuilder();
                                for (int i = 3; i < args.length; i++) {
                                    name.append(args[i]).append(" ");
                                }

                                CompactCrates.getInstance().getChestConfig().set("cratesTypes." + s + ".Name", name.toString());
                                CompactCrates.getInstance().saveChestsConfig();
                                sender.sendMessage(CompactCrates.getPrefix() + "§aYou have changed the Name of the crate to " + name.toString() + "!");
                            }
                        });
                    }
                }
            }
        }

        if (args.length >= 6){
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

                            if (!CompactCrates.useMysql) {
                                if (!CompactCrates.getInstance().getUserConfig().contains(target.getUniqueId() + "." + args[4] + ".Keys")) {
                                    CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", 0);
                                }

                                CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", Integer.parseInt(CompactCrates.getInstance().getUserConfig().getString(target.getUniqueId() + "." + args[4] + ".Keys")) + Integer.parseInt(args[5]));
                                CompactCrates.getInstance().saveUserConfig();
                            }else {
                                CompactCrates.getInstance().getMySql().addCrateAmount(target.getUniqueId().toString(), args[4], Integer.parseInt(args[5]));
                            }

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

                            if (!CompactCrates.useMysql) {
                                CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", Integer.parseInt(args[5]));
                                CompactCrates.getInstance().saveUserConfig();
                            }else {
                                CompactCrates.getInstance().getMySql().setCrateAmount(target.getUniqueId().toString(), args[4], Integer.parseInt(args[5]));
                            }
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

                            if (!CompactCrates.useMysql) {
                                CompactCrates.getInstance().getUserConfig().set(target.getUniqueId() + "." + args[4] + ".Keys", Integer.parseInt(CompactCrates.getInstance().getUserConfig().getString(target.getUniqueId() + "." + args[4] + ".Keys")) - Integer.parseInt(args[5]));
                                CompactCrates.getInstance().saveUserConfig();
                            }else {
                                CompactCrates.getInstance().getMySql().addCrateAmount(target.getUniqueId().toString(), args[4], -Integer.parseInt(args[5]));
                            }
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

                if (args[1].equalsIgnoreCase("create")){
                    if (!sender.hasPermission("compactcrates.admin.create")) {
                        sender.sendMessage(CompactCrates.getPrefix() + "You don't have permission to use this command!");
                        return true;
                    }

                    ArrayList<String> mats = new ArrayList<>();
                    for (int i = 0; i < Material.values().length; i++) {
                        mats.add(Material.values()[i].name());
                    }

                    if (mats.contains(args[4].toUpperCase())){
                        Integer slot = Integer.parseInt(args[3]);

                        CompactCrates.getInstance().getChestConfig().set("cratesTypes." + args[2] + ".ID", args[2]);
                        CompactCrates.getInstance().getChestConfig().set("cratesTypes." + args[2] + ".Name", args[5]);
                        CompactCrates.getInstance().getChestConfig().set("cratesTypes." + args[2] + ".Slot", slot);
                        CompactCrates.getInstance().getChestConfig().set("cratesTypes." + args[2] + ".Type", args[4].toUpperCase());
                        CompactCrates.getInstance().getChestConfig().set("cratesTypes." + args[2] + ".SpinType", "csgo");
                        CompactCrates.getInstance().getChestConfig().set("cratesTypes." + args[2] + ".FillWith", List.of("AIR"));

                        CompactCrates.getInstance().saveChestsConfig();
                    }

                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return List.of("reload", "placechest", "help", "admin", "setmaterial", "item");
        }else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("admin")) {
                return List.of("gui", "keys", "setParticle", "deletecrate", "setslot", "changetype", "renamecrate", "setsize", "create");
            }else if (args[0].equalsIgnoreCase("setmaterial")){
                ArrayList<String> types = new ArrayList<>();
                for (int i = 0; i < Material.values().length; i++) {
                    types.add(Material.values()[i].name());
                }
                return types;
            }else if (args[0].equalsIgnoreCase("item")){

                return List.of("addCommand", "addProbability", "addWinParticle", "setPreviewLore", "preview");

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
                }else if (args[1].equalsIgnoreCase("deletecrate")) {
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });
                    return types;
                }else if (args[1].equalsIgnoreCase("setslot")) {
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });
                    return types;
                }else if (args[1].equalsIgnoreCase("changetype")) {
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });
                    return types;
                }else if (args[1].equalsIgnoreCase("renamecrate")) {
                    ArrayList<String> types = new ArrayList<>();
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        types.add(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID"));
                    });
                    return types;
                }else if (args[1].equalsIgnoreCase("setsize")) {

                    return List.of("9", "18", "27", "36", "45", "54");
                }else if (args[1].equalsIgnoreCase("create")) {
                    return List.of("<ID>");
                }
            }else if (args[0].equalsIgnoreCase("item") && args[1].equalsIgnoreCase("addProbability")) {
                return List.of("0.10", "100.0", "0.5", "50.0", "10.0", "1.0", "75.5", "0.001");
            }else if (args[0].equalsIgnoreCase("item") && args[1].equalsIgnoreCase("addWinParticle")) {
                return List.of("fallingBlocks", "fireworks");
            }else if (args[0].equalsIgnoreCase("item") && args[1].equalsIgnoreCase("setPreviewLore")) {
                Player player = (Player) sender;
                ItemStack item = player.getInventory().getItemInMainHand();

                if (item != null && item.getType() != Material.AIR) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        if (new ItemChecker(item).hasCustomTag("previewLore", ItemTagType.STRING)){
                            return List.of((String) new ItemChecker(item).getCustomTag("previewLore", ItemTagType.STRING));
                        }
                    }
                }

                return List.of();
            }
        }else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        players.add(player.getName());
                    }
                    return players;
                }else if (args[1].equalsIgnoreCase("setslot") || args[1].equalsIgnoreCase("create")) {
                    ArrayList<String> types = new ArrayList<>();
                    for (int i = 0; i < 45; i++) {
                        types.add(String.valueOf(i));
                    }
                    return types;
                }else if (args[1].equalsIgnoreCase("changetype")) {
                    ArrayList<String> types = new ArrayList<>();
                    for (int i = 0; i < Material.values().length; i++) {
                        types.add(Material.values()[i].name());
                    }
                    return types;
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
                }else if (args[1].equalsIgnoreCase("create")) {
                    ArrayList<String> types = new ArrayList<>();
                    for (int i = 0; i < Material.values().length; i++) {
                        types.add(Material.values()[i].name());
                    }
                    return types;
                }
            }
        }else if (args.length == 6) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("keys") && !args[2].equalsIgnoreCase("show")) {
                    return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
                }else if (args[1].equalsIgnoreCase("create")) {
                    return List.of("&e&lCrateName");
                }
            }
        }

        return null;
    }
}
