package leon_lp9.compactcrates.manager;

import leon_lp9.compactcrates.CompactCrates;
import leon_lp9.compactcrates.builder.ItemBuilder;
import leon_lp9.compactcrates.builder.ItemChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class OpenCrate {

    public static void openCrate(Player player, String crateID, String crateName) {
        Inventory inv = Bukkit.createInventory(null, 27, "§7Open.. §6§l" + crateName);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i + 9, getRandomCrateItem(crateID));
        }
        inv.setItem(4, new ItemBuilder(Material.END_ROD).setDisplayName("§6§l↓").build());
        inv.setItem(4 + 9 + 9, new ItemBuilder(Material.END_ROD).setDisplayName("§6§l↑").build());
        player.openInventory(inv);

        getInventory.put(player, inv);
        getCrateID.put(player, crateID);
        startRunnableForSinglePlayer(player, crateID);
    }

    public static ItemStack getRandomCrateItem(String crateID) {

        ArrayList<ItemStack> items = new ArrayList<>();

        if (!CompactCrates.getInstance().getChestConfig().contains("chestContents." + crateID)) {
            return new ItemBuilder(Material.BARRIER).setDisplayName("§c§lERROR").build();
        }

        CompactCrates.getInstance().getChestConfig().getConfigurationSection("chestContents." + crateID).getKeys(false).forEach(key -> {
            ItemStack item = CompactCrates.getInstance().getChestConfig().getItemStack("chestContents." + crateID + "." + key);
            if (item != null && item.getType() !=  Material.AIR) {
                items.add(item);
            }
        });

        if (items.size() == 0) {
            return new ItemBuilder(Material.BARRIER).setDisplayName("§c§lERROR").build();
        }

        Random random = new Random();
        int randomItem = random.nextInt(items.size());

        if (new ItemChecker(items.get(randomItem)).hasCustomTag("commands", ItemTagType.STRING)){

            ItemBuilder itemBuilder = new ItemBuilder(items.get(randomItem));

            if (CompactCrates.getInstance().getConfig().contains("CommandRewardPreview") && CompactCrates.getInstance().getConfig().getBoolean("CommandRewardPreview")) {
                ArrayList<String> commands = (ArrayList<String>) CompactCrates.getInstance().getConfig().getStringList("CommandRewardLores");
                for (String command : commands) {
                    itemBuilder.addLineLore(command.replace("&", "§"));
                }
            }

            return itemBuilder.build();
        }

        return items.get(randomItem);
    }

    public static HashMap<Player, Inventory> getInventory = new HashMap<>();
    public static HashMap<Player, BukkitTask> getRunnable = new HashMap<>();
    public static HashMap<Player, String> getCrateID = new HashMap<>();
    public static void startRunnableForSinglePlayer(Player player, String crateID) {
        Integer[] time = {0};
        Double[] nextTime = {-10.0};
        Integer[] delay = {0};
        BukkitTask myTask = Bukkit.getScheduler().runTaskTimer(CompactCrates.getInstance(), () -> {
            time[0]++;
            delay[0]++;

            if (delay[0] >= nextTime[0]) {
                nextTime[0] = nextTime[0] + Double.parseDouble(CompactCrates.getInstance().getConfig().getString("DecelerateSpeed"));
                delay[0] = 0;

                getInventory.get(player).setItem(9, getInventory.get(player).getItem(10));
                getInventory.get(player).setItem(10, getInventory.get(player).getItem(11));
                getInventory.get(player).setItem(11, getInventory.get(player).getItem(12));
                getInventory.get(player).setItem(12, getInventory.get(player).getItem(13));
                getInventory.get(player).setItem(13, getInventory.get(player).getItem(14));
                getInventory.get(player).setItem(14, getInventory.get(player).getItem(15));
                getInventory.get(player).setItem(15, getInventory.get(player).getItem(16));
                getInventory.get(player).setItem(16, getInventory.get(player).getItem(17));

                getInventory.get(player).setItem(17, getRandomCrateItem(crateID));

                player.playSound(player.getLocation(), "block.note_block.pling", 1, 1);
            }

            if (time[0] ==  Integer.parseInt(CompactCrates.getInstance().getConfig().getString("SpinTime"))){
                getInventory.get(player).setItem(9, getInventory.get(player).getItem(10));
                getInventory.get(player).setItem(10, getInventory.get(player).getItem(11));
                getInventory.get(player).setItem(11, getInventory.get(player).getItem(12));
                getInventory.get(player).setItem(12, getInventory.get(player).getItem(13));
                getInventory.get(player).setItem(13, getInventory.get(player).getItem(14));
                getInventory.get(player).setItem(14, getInventory.get(player).getItem(15));
                getInventory.get(player).setItem(15, getInventory.get(player).getItem(16));
                getInventory.get(player).setItem(16, getInventory.get(player).getItem(17));

                getInventory.get(player).setItem(17, getRandomCrateItem(crateID));

                //Ep level up sound
                player.playSound(player.getLocation(), "entity.player.levelup", 1, 1);

                if (new ItemChecker(getInventory.get(player).getItem(13)).hasCustomTag("commands", ItemTagType.STRING)){

                    ItemBuilder itemBuilder = new ItemBuilder(getInventory.get(player).getItem(13));

                    String[] commands = new ItemChecker(getInventory.get(player).getItem(13)).getCustomTag("commands", ItemTagType.STRING).toString().split("/");

                    for (int i = 0; i < commands.length; i++) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands[i].replace("%player%", player.getName()).replace("&", "§"));
                    }

                }else {

                    //give item
                    player.getInventory().addItem(getInventory.get(player).getItem(13));
                }

                getRunnable.get(player).cancel();
                getRunnable.remove(player);
                getInventory.remove(player);
                getCrateID.remove(player);
            }
        }, 0, 1);
        getRunnable.put(player, myTask);
    }

}
