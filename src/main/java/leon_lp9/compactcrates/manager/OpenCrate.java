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
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class OpenCrate {

    static Double defaultProbability = 100.0;

    public static void openCrate(Player player, String crateID, String crateName) {

        defaultProbability = CompactCrates.getInstance().getConfig().getDouble("defaultProbability");

        SpinType st = SpinType.getSpinTypeOfCrateID(crateID);

        Inventory inv = Bukkit.createInventory(null, (st == SpinType.WHEEL_OF_FORTUNE ? 54 : (st == SpinType.FALLING ? 45 : 27)), "§7Open.. §6§l" + crateName);
//        for (int i = 0; i < 9; i++) {
//            inv.setItem(i + 9, getRandomCrateItem(crateID));
//        }
        if (st == SpinType.CSGO || st ==  SpinType.WHEEL_OF_FORTUNE) {
            inv.setItem(4, new ItemBuilder(Material.END_ROD).setDisplayName("§6§l↓").build());
            inv.setItem(4 + 9 + 9, new ItemBuilder(Material.END_ROD).setDisplayName("§6§l↑").build());
        }if (st == SpinType.FALLING){
            inv.setItem(4 + 9 + 8, new ItemBuilder(Material.CONDUIT).setDisplayName("§6§l→").build());
            inv.setItem(4 + 9 + 10, new ItemBuilder(Material.CONDUIT).setDisplayName("§6§l←").build());
        }
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
            if (item != null && item.getType() != Material.AIR) {
                items.add(item);

            }
        });

        if (items.size() == 0) {
            return new ItemBuilder(Material.BARRIER).setDisplayName("§c§lERROR").build();
        }

        double cumulativeProbability = 0.0;

        for (ItemStack item : items) {

            ItemChecker itemChecker = new ItemChecker(item);

            if (itemChecker.hasCustomTag("probability", ItemTagType.DOUBLE)) {
                cumulativeProbability += Double.parseDouble(String.valueOf(itemChecker.getCustomTag("probability", ItemTagType.DOUBLE)));
            } else {
                cumulativeProbability += defaultProbability;
            }
        }

        double randomProbability = new Random().nextDouble() * cumulativeProbability;

        double TempCumulativeProbability = 0.0;
        ItemStack randomItem = new ItemBuilder(Material.BARRIER).setDisplayName("§c§lERROR").build();
        for (ItemStack item : items) {
            ItemChecker itemChecker = new ItemChecker(item);

            if (itemChecker.hasCustomTag("probability", ItemTagType.DOUBLE)) {
                TempCumulativeProbability += Double.parseDouble(String.valueOf(itemChecker.getCustomTag("probability", ItemTagType.DOUBLE)));
            } else {
                TempCumulativeProbability += defaultProbability;
            }

            if (randomProbability <= TempCumulativeProbability) {
                randomItem = item;
                break;
            }
        }

        if (new ItemChecker(randomItem).hasCustomTag("commands", ItemTagType.STRING)) {

            ItemBuilder itemBuilder = new ItemBuilder(randomItem);

            if (CompactCrates.getInstance().getConfig().contains("CommandRewardPreview") && CompactCrates.getInstance().getConfig().getBoolean("CommandRewardPreview")) {
                ArrayList<String> commands = (ArrayList<String>) CompactCrates.getInstance().getConfig().getStringList("CommandRewardLores");
                for (String command : commands) {
                    itemBuilder.addLineLore(command.replace("&", "§"));
                }
            }

            return itemBuilder.build();
        }

        return randomItem;
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

                nextSpinStep(player, crateID, SpinType.getSpinTypeOfCrateID(crateID));

                player.playSound(player.getLocation(), CompactCrates.getInstance().getConfig().getString("tickSound"), 1, 1);
            }

            if (time[0] == Integer.parseInt(CompactCrates.getInstance().getConfig().getString("SpinTime"))) {

                nextSpinStep(player, crateID, SpinType.getSpinTypeOfCrateID(crateID));

                giveItem(player, getInventory.get(player).getItem(getPickSlot(SpinType.getSpinTypeOfCrateID(crateID))));


            }
        }, 0, 1);
        getRunnable.put(player, myTask);
    }

    public enum SpinType {
        CSGO,
        SINGLE,
        WHEEL_OF_FORTUNE,
        FALLING;

        public static SpinType getSpinType(String type) {
            for (SpinType spinType : SpinType.values()) {
                if (spinType.name().equalsIgnoreCase(type)) {
                    return spinType;
                }
            }
            return null;
        }

        public static SpinType getSpinTypeOfCrateID(String crateID) {
            AtomicReference<SpinType> spinType = new AtomicReference<>(CSGO);
            CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(key -> {
                if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + key + ".ID").equalsIgnoreCase(crateID)) {
                    if (CompactCrates.getInstance().getChestConfig().contains("cratesTypes." + key + ".SpinType")) {
                        spinType.set(SpinType.getSpinType(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + key + ".SpinType")));
                    }
                }
            });
            return spinType.get();
        }
    }

    public static void nextSpinStep(Player player, String crateID, SpinType spinType) {
        if (spinType == SpinType.CSGO) {
            setBGItemInInv(player, crateID, 0);
            setBGItemInInv(player, crateID, 1);
            setBGItemInInv(player, crateID, 2);
            setBGItemInInv(player, crateID, 3);
            setBGItemInInv(player, crateID, 5);
            setBGItemInInv(player, crateID, 6);
            setBGItemInInv(player, crateID, 7);
            setBGItemInInv(player, crateID, 8);

            getInventory.get(player).setItem(9, getInventory.get(player).getItem(10));
            getInventory.get(player).setItem(10, getInventory.get(player).getItem(11));
            getInventory.get(player).setItem(11, getInventory.get(player).getItem(12));
            getInventory.get(player).setItem(12, getInventory.get(player).getItem(13));
            getInventory.get(player).setItem(13, getInventory.get(player).getItem(14));
            getInventory.get(player).setItem(14, getInventory.get(player).getItem(15));
            getInventory.get(player).setItem(15, getInventory.get(player).getItem(16));
            getInventory.get(player).setItem(16, getInventory.get(player).getItem(17));

            getInventory.get(player).setItem(17, getRandomCrateItem(crateID));


            setBGItemInInv(player, crateID, 18);
            setBGItemInInv(player, crateID, 19);
            setBGItemInInv(player, crateID, 20);
            setBGItemInInv(player, crateID, 21);
            setBGItemInInv(player, crateID, 23);
            setBGItemInInv(player, crateID, 24);
            setBGItemInInv(player, crateID, 25);
            setBGItemInInv(player, crateID, 26);
        } else if (spinType == SpinType.SINGLE) {
            for (int i = 0; i < 27; i++) {
                setBGItemInInv(player, crateID, i);
            }
            getInventory.get(player).setItem(13, getRandomCrateItem(crateID));
        } else if (spinType == SpinType.WHEEL_OF_FORTUNE) {
            setBGItemInInv(player, crateID, 0);
            setBGItemInInv(player, crateID, 1);
            setBGItemInInv(player, crateID, 2);
            setBGItemInInv(player, crateID, 3);
            setBGItemInInv(player, crateID, 5);
            setBGItemInInv(player, crateID, 6);
            setBGItemInInv(player, crateID, 7);
            setBGItemInInv(player, crateID, 8);
            setBGItemInInv(player, crateID, 9);
            setBGItemInInv(player, crateID, 10);
            setBGItemInInv(player, crateID, 11);
            setBGItemInInv(player, crateID, 15);
            setBGItemInInv(player, crateID, 16);
            setBGItemInInv(player, crateID, 17);
            setBGItemInInv(player, crateID, 18);
            setBGItemInInv(player, crateID, 19);
            setBGItemInInv(player, crateID, 21);
            setBGItemInInv(player, crateID, 23);
            setBGItemInInv(player, crateID, 25);
            setBGItemInInv(player, crateID, 26);
            setBGItemInInv(player, crateID, 27);
            setBGItemInInv(player, crateID, 28);
            setBGItemInInv(player, crateID, 30);
            setBGItemInInv(player, crateID, 31);
            setBGItemInInv(player, crateID, 32);
            setBGItemInInv(player, crateID, 34);
            setBGItemInInv(player, crateID, 35);
            setBGItemInInv(player, crateID, 36);
            setBGItemInInv(player, crateID, 37);
            setBGItemInInv(player, crateID, 39);
            setBGItemInInv(player, crateID, 40);
            setBGItemInInv(player, crateID, 41);
            setBGItemInInv(player, crateID, 43);
            setBGItemInInv(player, crateID, 44);
            setBGItemInInv(player, crateID, 45);
            setBGItemInInv(player, crateID, 46);
            setBGItemInInv(player, crateID, 47);
            setBGItemInInv(player, crateID, 51);
            setBGItemInInv(player, crateID, 52);
            setBGItemInInv(player, crateID, 53);


            ItemStack item = getInventory.get(player).getItem(12);
            getInventory.get(player).setItem(12, getInventory.get(player).getItem(20));
            getInventory.get(player).setItem(20, getInventory.get(player).getItem(29));
            getInventory.get(player).setItem(29, getInventory.get(player).getItem(38));
            getInventory.get(player).setItem(38, getInventory.get(player).getItem(48));
            getInventory.get(player).setItem(48, getInventory.get(player).getItem(49));
            getInventory.get(player).setItem(49, getInventory.get(player).getItem(50));
            getInventory.get(player).setItem(50, getInventory.get(player).getItem(42));
            getInventory.get(player).setItem(42, getInventory.get(player).getItem(33));
            getInventory.get(player).setItem(33, getInventory.get(player).getItem(24));
            getInventory.get(player).setItem(24, getInventory.get(player).getItem(14));
            getInventory.get(player).setItem(14, getInventory.get(player).getItem(13));

            if (item == null) {
                getInventory.get(player).setItem(13, getRandomCrateItem(crateID));
            }else {
                getInventory.get(player).setItem(13, item);
            }


        }else if (spinType == SpinType.FALLING){
            setBGItemInInv(player, crateID, 0);
            setBGItemInInv(player, crateID, 1);
            setBGItemInInv(player, crateID, 2);
            setBGItemInInv(player, crateID, 3);
            setBGItemInInv(player, crateID, 5);
            setBGItemInInv(player, crateID, 6);
            setBGItemInInv(player, crateID, 7);
            setBGItemInInv(player, crateID, 8);
            setBGItemInInv(player, crateID, 9);
            setBGItemInInv(player, crateID, 10);
            setBGItemInInv(player, crateID, 11);
            setBGItemInInv(player, crateID, 12);
            setBGItemInInv(player, crateID, 14);
            setBGItemInInv(player, crateID, 15);
            setBGItemInInv(player, crateID, 16);
            setBGItemInInv(player, crateID, 17);
            setBGItemInInv(player, crateID, 18);
            setBGItemInInv(player, crateID, 19);
            setBGItemInInv(player, crateID, 20);
            setBGItemInInv(player, crateID, 24);
            setBGItemInInv(player, crateID, 25);
            setBGItemInInv(player, crateID, 26);
            setBGItemInInv(player, crateID, 27);
            setBGItemInInv(player, crateID, 28);
            setBGItemInInv(player, crateID, 29);
            setBGItemInInv(player, crateID, 30);
            setBGItemInInv(player, crateID, 32);
            setBGItemInInv(player, crateID, 33);
            setBGItemInInv(player, crateID, 34);
            setBGItemInInv(player, crateID, 35);
            setBGItemInInv(player, crateID, 36);
            setBGItemInInv(player, crateID, 37);
            setBGItemInInv(player, crateID, 38);
            setBGItemInInv(player, crateID, 39);
            setBGItemInInv(player, crateID, 41);
            setBGItemInInv(player, crateID, 42);
            setBGItemInInv(player, crateID, 43);
            setBGItemInInv(player, crateID, 44);

            getInventory.get(player).setItem(40, getInventory.get(player).getItem(31));
            getInventory.get(player).setItem(31, getInventory.get(player).getItem(22));
            getInventory.get(player).setItem(22, getInventory.get(player).getItem(13));
            getInventory.get(player).setItem(13, getInventory.get(player).getItem(4));

            getInventory.get(player).setItem(4, getRandomCrateItem(crateID));
        }
    }

    public static void setBGItemInInv(Player player, String crateID, Integer slot) {
        List<String> bgItem = CompactCrates.getInstance().getChestConfig().getStringList("cratesTypes." + crateID + ".FillWith");

        if (bgItem.size() == 0) {
            return;
        }

        Random r = new Random();
        Material mat = Material.valueOf(bgItem.get(r.nextInt(bgItem.size())));

        if (mat ==  Material.AIR) {
            return;
        }

        getInventory.get(player).setItem(slot, new ItemBuilder(mat).setDisplayName("§7").build());
    }

    public static Integer getPickSlot(SpinType spinType) {
        if (spinType == SpinType.CSGO) {
            return 13;
        } else if (spinType == SpinType.SINGLE) {
            return 13;
        }else if (spinType == SpinType.WHEEL_OF_FORTUNE) {
            return 13;
        }else if (spinType == SpinType.FALLING) {
            return 4 + 9 + 9;
        }
        return 0;
    }

    public static void giveItem(Player player, ItemStack itemStack) {
        if (new ItemChecker(itemStack).hasCustomTag("winparticle", ItemTagType.STRING)) {
            if (new ItemChecker(itemStack).getCustomTag("winparticle", ItemTagType.STRING).equals("fallingBlocks".toUpperCase())) {
                ParticleManager.spawnFallingBlocksAboveNearbyChest(player.getLocation());
            } else if (new ItemChecker(itemStack).getCustomTag("winparticle", ItemTagType.STRING).equals("fireworks".toUpperCase())) {
                ParticleManager.spawnFireworkAboveNearbyChest(player.getLocation());
            }
        }

        //Ep level up sound
        player.playSound(player.getLocation(), CompactCrates.getInstance().getConfig().getString("giveSound"), 1, 1);

        if (new ItemChecker(itemStack).hasCustomTag("commands", ItemTagType.STRING)) {

            ItemBuilder itemBuilder = new ItemBuilder(itemStack);

            String[] commands = new ItemChecker(itemStack).getCustomTag("commands", ItemTagType.STRING).toString().split("/");

            for (int i = 0; i < commands.length; i++) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands[i].replace("%player%", player.getName()).replace("&", "§"));
            }

        } else {

            //give item
            player.getInventory().addItem(itemStack);
        }

        getRunnable.get(player).cancel();
        getRunnable.remove(player);
        getInventory.remove(player);
        getCrateID.remove(player);
    }
}
