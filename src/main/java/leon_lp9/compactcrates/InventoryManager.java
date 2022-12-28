package leon_lp9.compactcrates;

import leon_lp9.compactcrates.manager.OpenCrate;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class InventoryManager implements Listener {

    public static void openFirstInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, CompactCrates.getInstance().getConfig().getInt("inventorySize"), CompactCrates.getInstance().getLanguageConfig().getString("firstInventoryName"));

        CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
            ItemStack itemStack = new ItemBuilder(Material.getMaterial(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Type"))).setLocalizedName("crate").setDisplayName(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name").replace("&", "§")).setLocalizedName(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID")).build();
            ItemMeta itemMeta = itemStack.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();

            if (!CompactCrates.getInstance().getUserConfig().contains( player.getUniqueId().toString() + "." + CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID") + ".Keys")){
                CompactCrates.getInstance().getUserConfig().set( player.getUniqueId().toString() + "." + CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID") + ".Keys", 0);
                CompactCrates.getInstance().saveUserConfig();
            }

            CompactCrates.getInstance().getLanguageConfig().getStringList("firstInventoryLore").forEach(s1 -> {
                lore.add(s1.replace("%key%", CompactCrates.getInstance().getUserConfig().getString( player.getUniqueId().toString() + "." + CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID") + ".Keys") + "").replace("&", "§"));
            });

            lore.add("§7");
            lore.add("§e§oLeft click§e to open the crate");
            lore.add("§e§oRight click§e to see the rewards");
            if (player.hasPermission("compactcrates.admin")) {
                lore.add("§e§oMiddle click§e to edit the crate (Admin)");
            }
            lore.add("§7");


            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(Integer.parseInt(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Slot")) ,itemStack);
        });

        player.openInventory(inventory);
    }

    public void openSecondInventory(Player player, String crateID, String crateName) {
        Inventory inventory = Bukkit.createInventory(null, 54, CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", crateName).replace("&", "§"));

        for (int i = 0; i < 45; i++) {

            if (CompactCrates.getInstance().getChestConfig().contains("chestContents" + "." + crateID + "." + i)) {
                ItemStack is = CompactCrates.getInstance().getChestConfig().getItemStack("chestContents" + "." + crateID + "." + i);
                inventory.setItem(i, is);
            }else {
                CompactCrates.getInstance().getChestConfig().set("chestContents" + "." + crateID + "." + i, new ItemStack(Material.AIR));
                CompactCrates.getInstance().saveChestsConfig();
            }
        }

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i + 45, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§7").build());
        }

        inventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("§c§lBack").setLocalizedName("back").build());

        player.openInventory(inventory);
    }

    public void openSecondAdminInventory(Player player, String crateID, String crateName) {
        Inventory inventory = Bukkit.createInventory(null, 54, CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", crateName).replace("&", "§") + " §4(Admin)");

        for (int i = 0; i < 45; i++) {

            if (CompactCrates.getInstance().getChestConfig().contains("chestContents" + "." + crateID + "." + i)) {
                ItemStack is = CompactCrates.getInstance().getChestConfig().getItemStack("chestContents" + "." + crateID + "." + i);
                inventory.setItem(i, is);
            }else {
                CompactCrates.getInstance().getChestConfig().set("chestContents" + "." + crateID + "." + i, new ItemStack(Material.AIR));
                CompactCrates.getInstance().saveChestsConfig();
            }
        }

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i + 45, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§7").build());
        }

        //back
        inventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("§c§lBack").setLocalizedName("back").build());

        //rename
        inventory.setItem(47, new ItemBuilder(Material.NAME_TAG).setDisplayName("§a§lRename Crate").setLocalizedName("Rename").build());

        //changeType
        inventory.setItem(45, new ItemBuilder(Material.CONDUIT).setDisplayName("§a§lChangeMaterial").setLocalizedName("ChangeType").build());

        //setSlot
        inventory.setItem(51, new ItemBuilder(Material.CHEST).setDisplayName("§a§lSet Slot").setLocalizedName("SetSlot").build());

        //delete crate
        inventory.setItem(53, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("§c§lDelete Crate").setLocalizedName("DeleteCrate").build());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(CompactCrates.getInstance().getLanguageConfig().getString("firstInventoryName"))) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

            if (event.getClick().isRightClick()){

                openSecondInventory(player, event.getCurrentItem().getItemMeta().getLocalizedName(), event.getCurrentItem().getItemMeta().getDisplayName());

            }else if (event.getClick().isLeftClick()){

                if (OpenCrate.getRunnable.containsKey(player)){
                    player.sendMessage(CompactCrates.getPrefix() + "§cYou are already opening a crate!");
                    player.closeInventory();
                    return;
                }
                //get players amount of keys
                int keys = CompactCrates.getInstance().getUserConfig().getInt(player.getUniqueId().toString() + "." + event.getCurrentItem().getItemMeta().getLocalizedName() + ".Keys");

                if (keys > 0) {
                    //remove 1 key
                    CompactCrates.getInstance().getUserConfig().set(player.getUniqueId().toString() + "." + event.getCurrentItem().getItemMeta().getLocalizedName() + ".Keys", keys - 1);
                    CompactCrates.getInstance().saveUserConfig();
                    player.closeInventory();
                    player.sendMessage(CompactCrates.getPrefix()+ CompactCrates.getInstance().getLanguageConfig().getString("crateOpened").replace("%crate%", event.getCurrentItem().getItemMeta().getDisplayName()).replace("&", "§"));
                    //open crate
                    OpenCrate.openCrate(player, event.getCurrentItem().getItemMeta().getLocalizedName(), event.getCurrentItem().getItemMeta().getDisplayName());
                }else {
                    player.closeInventory();
                    player.sendMessage(CompactCrates.getPrefix() + CompactCrates.getInstance().getLanguageConfig().getString("noKeys").replace("&", "§"));
                }

            }else if (event.getClick().isCreativeAction()){
                if (player.hasPermission("compactcrates.admin")){
                    openSecondAdminInventory(player, event.getCurrentItem().getItemMeta().getLocalizedName(), event.getCurrentItem().getItemMeta().getDisplayName());
                }
            }
        }else if (event.getView().getTitle().startsWith(CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", "").replace("&", "§"))) {

            if (event.getView().getTitle().endsWith("§4(Admin)")) {
                if (player.hasPermission("compactcrates.admin")) {

                    if (event.getCurrentItem() == null) return;

                    if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

                    if (event.getSlot() >= 45 && event.getSlot() <= 53) event.setCancelled(true);

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("back")) {
                        event.setCancelled(true);
                        openFirstInventory(player);
                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("Rename")) {
                        event.setCancelled(true);
                        player.closeInventory();

                        player.sendMessage(CompactCrates.getPrefix() + "§7Command: §a/compactcrates admin renamecrate <CrateID> <newName>");
                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("ChangeType")) {
                        event.setCancelled(true);
                        player.closeInventory();

                        player.sendMessage(CompactCrates.getPrefix() + "§7Command: §a/compactcrates admin changetype <CrateID> <newType>");
                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("SetSlot")) {
                        event.setCancelled(true);
                        player.closeInventory();

                        player.sendMessage(CompactCrates.getPrefix() + "§7Command: §a/compactcrates admin setslot <CrateID> <Slot>");
                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("DeleteCrate")) {
                        event.setCancelled(true);
                        player.closeInventory();

                        player.sendMessage(CompactCrates.getPrefix() + "§7Command: §a/compactcrates admin deletecrate <CrateID>");
                    }
                }else {
                    event.setCancelled(true);
                }
            }else {
                event.setCancelled(true);
                if (event.getCurrentItem() == null) return;
                if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

                if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("back")) {
                    openFirstInventory(player);
                }
            }

        }else if (event.getView().getTitle().startsWith("§7Open.. ")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().startsWith(CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", "").replace("&", "§"))) {
            if (event.getView().getTitle().endsWith("§4(Admin)")) {
                if (player.hasPermission("compactcrates.admin")) {

                    String crateName = event.getView().getTitle().replace(CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", "").replace("&", "§"), "").replace(" §4(Admin)", "");

                    final String[] crateID = {"null"};
                    //Get the crateID
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name").equals(crateName.replace("§", "&"))) {
                            crateID[0] = CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID");
                        }
                    });

                    for (int i = 0; i < 45; i++) {
                        CompactCrates.getInstance().getChestConfig().set("chestContents" + "." + crateID[0] + "." + i, event.getInventory().getItem(i));
                    }
                    CompactCrates.getInstance().saveChestsConfig();

                    player.sendMessage(CompactCrates.getPrefix() + "§aSaved crate " + crateName);
                }
            }
        }else if (event.getView().getTitle().startsWith("§7Open.. ")) {
            if (OpenCrate.getRunnable.containsKey(player)){

                //Ep level up sound
                player.playSound(player.getLocation(), "entity.player.levelup", 1, 1);

                //give item
                player.getInventory().addItem(OpenCrate.getRandomCrateItem(OpenCrate.getCrateID.get(player)));

                OpenCrate.getRunnable.get(player).cancel();
                OpenCrate.getRunnable.remove(player);
            }
        }
    }

}
