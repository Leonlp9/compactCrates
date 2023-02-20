package leon_lp9.compactcrates;

import leon_lp9.compactcrates.builder.ItemBuilder;
import leon_lp9.compactcrates.builder.ItemChecker;
import leon_lp9.compactcrates.manager.OpenCrate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryManager implements Listener {

    public static int getCrateAmount(UUID uuid, String crateID){
        if (CompactCrates.useMysql){
            return CompactCrates.getInstance().getMySql().getCrateAmount(uuid.toString(), crateID);
        }else {
            return CompactCrates.getInstance().getUserConfig().getInt(uuid.toString() + "." + crateID + ".Keys");
        }
    }

    public static void setCrateAmount(UUID uuid, String crateID, int amount){
        if (CompactCrates.useMysql){
            CompactCrates.getInstance().getMySql().setCrateAmount(uuid.toString(), crateID, amount);
        }else {
            CompactCrates.getInstance().getUserConfig().set(uuid.toString() + "." + crateID + ".Keys", amount);
            CompactCrates.getInstance().saveUserConfig();
        }
    }

    public static void openFirstInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, CompactCrates.getInstance().getConfig().getInt("inventorySize"), CompactCrates.getInstance().getLanguageConfig().getString("firstInventoryName"));

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

            CompactCrates.getInstance().getLanguageConfig().getStringList("firstInventoryLore").forEach(s1 -> {
                lore.add(s1.replace("%key%",  getCrateAmount(player.getUniqueId(), CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID")) + "").replace("&", "§"));
            });

            if (!CompactCrates.getInstance().getLanguageConfig().contains("leftClick")){
                CompactCrates.getInstance().getLanguageConfig().set("leftClick", "LeftClick OPEN");
                CompactCrates.getInstance().getLanguageConfig().set("rightClick", "RightClick PREVIEW");
                CompactCrates.getInstance().getLanguageConfig().set("middleClick", "MiddleClick Admin");
                CompactCrates.getInstance().saveLanguageConfig();
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

            inventory.setItem(Integer.parseInt(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Slot")) ,itemStack);
        });

        player.openInventory(inventory);
    }

    public void openSecondInventory(Player player, String crateID, String crateName) {
        Inventory inventory = Bukkit.createInventory(null, 54, CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", crateName).replace("&", "§"));

        for (int i = 0; i < 45; i++) {

            if (CompactCrates.getInstance().getChestConfig().contains("chestContents" + "." + crateID + "." + i)) {
                final ItemStack is = CompactCrates.getInstance().getChestConfig().getItemStack("chestContents" + "." + crateID + "." + i);

                ItemStack stackwithlore = null;

                inventory.setItem(i, is);


                if (new ItemChecker(is).hasCustomTag("previewlore", ItemTagType.STRING)) {
                    ItemBuilder itemBuilder = new ItemBuilder(is);

                    String[] lore = new ItemChecker(is).getCustomTag("previewlore", ItemTagType.STRING).toString().split("/n");

                    for (String s : lore) {
                        itemBuilder.addLineLore(s.replace("&", "§"));
                    }

                    inventory.setItem(i, itemBuilder.build());

                    stackwithlore = itemBuilder.build();
                }

                if (new ItemChecker((stackwithlore == null ? is : stackwithlore)).hasCustomTag("commands", ItemTagType.STRING)){

                    ItemBuilder itemBuilder = new ItemBuilder((stackwithlore == null ? is : stackwithlore));

                    if (CompactCrates.getInstance().getConfig().contains("CommandRewardPreview") && CompactCrates.getInstance().getConfig().getBoolean("CommandRewardPreview")) {
                        ArrayList<String> commands = (ArrayList<String>) CompactCrates.getInstance().getConfig().getStringList("CommandRewardLores");
                        for (String command : commands) {
                            itemBuilder.addLineLore(command.replace("&", "§"));
                        }
                    }

                    inventory.setItem(i, itemBuilder.build());
                }

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

        if (!CompactCrates.getInstance().getLanguageConfig().contains("deleteCrate")){

            CompactCrates.getInstance().getLanguageConfig().set("changeMaterial", "&e&o&lChange the Material");
            CompactCrates.getInstance().getLanguageConfig().set("changeAnimation", "&e&o&lChange the Animation");
            CompactCrates.getInstance().getLanguageConfig().set("changeName", "&e&o&lChange the Name");
            CompactCrates.getInstance().getLanguageConfig().set("setSlot", "&e&o&lSet the Slot");
            CompactCrates.getInstance().getLanguageConfig().set("setBackgroud", "&e&o&lSet the Background");
            CompactCrates.getInstance().getLanguageConfig().set("deleteCrate", "&c&o&lDelete the Crate");
            CompactCrates.getInstance().getLanguageConfig().set("back", "&c&o&lBack");
            CompactCrates.getInstance().saveLanguageConfig();
        }

        //back
        inventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("back").replace("&", "§")).setLocalizedName("back").build());

        //rename
        inventory.setItem(47, new ItemBuilder(Material.NAME_TAG).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("changeName").replace("&", "§")).setLocalizedName("Rename").build());

        //changeType
        inventory.setItem(45, new ItemBuilder(Material.CONDUIT).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("changeMaterial").replace("&", "§")).setLocalizedName("ChangeType").build());

        //setAnimation
        inventory.setItem(46, new ItemBuilder(Material.CLOCK).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("changeAnimation").replace("&", "§")).setLocalizedName("SetAnimation").build());

        //setSlot
        inventory.setItem(51, new ItemBuilder(Material.CHEST).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("setSlot").replace("&", "§")).setLocalizedName("SetSlot").build());

        //setBackgroundItems
        inventory.setItem(52, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("setBackgroud").replace("&", "§")).setLocalizedName("SetBackgroundItems").build());

        //delete crate
        inventory.setItem(53, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName(CompactCrates.getInstance().getLanguageConfig().getString("deleteCrate").replace("&", "§")).setLocalizedName("DeleteCrate").build());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(CompactCrates.getInstance().getLanguageConfig().getString("firstInventoryName")) || event.getView().getTitle().equals("§6CompactCrates Admin GUI")) {
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
                int keys = getCrateAmount(player.getUniqueId(), event.getCurrentItem().getItemMeta().getLocalizedName());

                if (keys > 0) {
                    //remove 1 key
                    if (CompactCrates.useMysql){
                        CompactCrates.getMySql().setCrateAmount(player.getUniqueId().toString(), event.getCurrentItem().getItemMeta().getLocalizedName(), keys - 1);
                    }else {
                        CompactCrates.getInstance().getUserConfig().set(player.getUniqueId().toString() + "." + event.getCurrentItem().getItemMeta().getLocalizedName() + ".Keys", keys - 1);
                    }
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

                    String crateName = event.getView().getTitle().replace(CompactCrates.getInstance().getLanguageConfig().getString("secondInventoryName").replace("%crate%", "").replace("&", "§"), "").replace(" §4(Admin)", "");

                    final String[] crateID = {"null"};
                    //Get the crateID
                    CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                        if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name").equals(crateName.replace("§", "&"))) {
                            crateID[0] = CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID");
                        }
                    });

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("Rename")) {

                        event.setCancelled(true);
                        player.closeInventory();

                        player.sendMessage(CompactCrates.getPrefix() + "§7Command: §a/compactcrates admin renamecrate " + crateID[0] + " <newName>");

                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("ChangeType")) {
                        event.setCancelled(true);

                        player.sendMessage(CompactCrates.getPrefix() + "§7Command: §a/compactcrates admin changetype " + crateID[0] + " <newType>");

                        openTypesInventory(player, crateID[0], 0);

                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("SetSlot")) {

                        Inventory inventory = Bukkit.createInventory(null, 45, "§a§lSet Slot §7(§c" + crateName + "§7)");

                        for (int i = 0; i < 45; i++) {
                            inventory.setItem(i, new ItemBuilder(Material.CONDUIT).setDisplayName("§eSet here §7(Slot " + i + ")").setLocalizedName(crateID[0] + " " + i).build());
                        }

                        player.openInventory(inventory);

                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("DeleteCrate")) {

                        Inventory inventory = Bukkit.createInventory(null, 27, "§c§lDelete Crate §7(§c" + crateName + "§7)");

                        inventory.setItem(11, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§lYes").setLocalizedName(crateID[0]).build());

                        inventory.setItem(15, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§lBack").setLocalizedName("back").build());

                        player.openInventory(inventory);

                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("SetAnimation")) {
                        event.setCancelled(true);

                        Inventory inventory = Bukkit.createInventory(null, 9, "§8Set Animation §7(§c" + crateName + "§7)");

                        //single
                        inventory.setItem(0, new ItemBuilder(Material.SNOWBALL).setDisplayName("§aSingle").setLocalizedName(crateID[0] + " single").build());

                        //csgo
                        inventory.setItem(1, new ItemBuilder(Material.FIREWORK_STAR).setDisplayName("§aCsgo").setLocalizedName(crateID[0] + " csgo").build());

                        //wheel_of_fortune
                        inventory.setItem(2, new ItemBuilder(Material.GOLD_NUGGET).setDisplayName("§aWheel of Fortune").setLocalizedName(crateID[0] + " wheel_of_fortune").build());

                        //falling
                        inventory.setItem(3, new ItemBuilder(Material.SAND).setDisplayName("§aFalling").setLocalizedName(crateID[0] + " falling").build());

                        player.openInventory(inventory);
                    }

                    if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("SetBackgroundItems")){
                        event.setCancelled(true);

                        Inventory inventory = Bukkit.createInventory(null, 9, "§8Set Background Items §7(§c" + crateName + "§7)");

                        List<String> mats = CompactCrates.getInstance().getChestConfig().getStringList("cratesTypes." + crateID[0] + ".FillWith");

                        mats.forEach(s -> {
                            inventory.addItem(new ItemBuilder(Material.valueOf(s)).build());
                        });

                        player.openInventory(inventory);
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
        }else if (event.getView().getTitle().startsWith("§8Set Animation §7(§c")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

            if (event.getCurrentItem().getItemMeta().hasLocalizedName()){
                String crateID = event.getCurrentItem().getItemMeta().getLocalizedName().split(" ")[0];
                String animation = event.getCurrentItem().getItemMeta().getLocalizedName().split(" ")[1];

                CompactCrates.getInstance().getChestConfig().set("cratesTypes." + crateID + ".SpinType", animation);
                CompactCrates.getInstance().saveChestsConfig();

                player.closeInventory();
                player.sendMessage(CompactCrates.getPrefix() + "§7You have set the animation of the crate to §a" + animation);
            }
        }else if (event.getView().getTitle().startsWith("§a§lSet Slot §7(§c")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

            if (event.getCurrentItem().getItemMeta().hasLocalizedName()){
                String crateID = event.getCurrentItem().getItemMeta().getLocalizedName().split(" ")[0];
                int slot = Integer.parseInt(event.getCurrentItem().getItemMeta().getLocalizedName().split(" ")[1]);

                CompactCrates.getInstance().getChestConfig().set("cratesTypes." + crateID + ".Slot", slot);
                CompactCrates.getInstance().saveChestsConfig();

                openFirstInventory(player);
                player.sendMessage(CompactCrates.getPrefix() + "§7You have set the slot of the crate to §a" + slot);
            }
        }else if (event.getView().getTitle().startsWith("§c§lDelete Crate §7(§c")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

            if (event.getCurrentItem().getItemMeta().hasLocalizedName()){
                if (event.getCurrentItem().getItemMeta().getLocalizedName().equals("back")) {
                    openFirstInventory(player);
                }else {
                    String crateID = event.getCurrentItem().getItemMeta().getLocalizedName();

                    CompactCrates.getInstance().getChestConfig().set("cratesTypes." + crateID, null);
                    CompactCrates.getInstance().saveChestsConfig();

                    openFirstInventory(player);
                    player.sendMessage(CompactCrates.getPrefix() + "§7You have deleted the crate §a" + crateID);
                }
            }
        }else if (event.getView().getTitle().equals("§a§lChange Crate Type")){
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (event.getView().getBottomInventory().equals(event.getClickedInventory())) return;

            if (event.getCurrentItem().getItemMeta().hasLocalizedName()){
                String name = event.getCurrentItem().getItemMeta().getLocalizedName();

                if (name.equals("next")){
                    openTypesInventory(player, event.getInventory().getItem(0).getItemMeta().getLocalizedName(), Integer.parseInt(event.getInventory().getItem(49).getItemMeta().getLocalizedName()) + 1);
                    return;
                }else if (name.equals("previous")){
                    openTypesInventory(player, event.getInventory().getItem(0).getItemMeta().getLocalizedName(), Integer.parseInt(event.getInventory().getItem(49).getItemMeta().getLocalizedName()) - 1);
                    return;
                }else if (event.getSlot() == 49){
                    return;
                }

                String crateID = event.getInventory().getItem(0).getItemMeta().getLocalizedName();

                CompactCrates.getInstance().getChestConfig().set("cratesTypes." + crateID + ".Type", event.getCurrentItem().getType().name());

                openFirstInventory(player);


            }
        }else if (event.getView().getTitle().equals("§6CompactCrates Admin GUI Preview")){
            event.setCancelled(true);
        }

    }

    public void openTypesInventory(Player player, String crateID, Integer page){
        Inventory inventory = Bukkit.createInventory(null, 54, "§a§lChange Crate Type");

        ArrayList<Material> mats = new ArrayList<>(Arrays.asList(Material.values()));

        for (int i = 0; i < 45; i++) {

            inventory.setItem(i, new ItemBuilder((Material) mats.get(i+1 + (45 * page))).setDisplayName("§a§l" + ((Material) mats.get(i+1 + (45 * page))).name()).setLocalizedName(crateID).build());
        }

        if (page != 0){
            inventory.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("§a§lPrevious Page").setLocalizedName("previous").build());
        }
        if (page != 25){
            inventory.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("§a§lNext Page").setLocalizedName("next").build());
        }
        inventory.setItem(49, new ItemBuilder(Material.PAPER).setDisplayName("§7§lPage " + (page +1)).setLocalizedName(page + "").build());

        player.openInventory(inventory);
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

                ItemStack item = OpenCrate.getRandomCrateItem(OpenCrate.getCrateID.get(player));
                OpenCrate.giveItem(player, item);

            }
        }else if (event.getView().getTitle().startsWith("§8Set Background Items §7(§c")){
            if (player.hasPermission("compactcrates.admin")) {

                String crateName = event.getView().getTitle().replace("§8Set Background Items §7(§c", "").replace("§7)", "");

                final String[] crateID = {"null"};
                //Get the crateID
                CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
                    if (CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name").equals(crateName.replace("§", "&"))) {
                        crateID[0] = CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".ID");
                    }
                });

                ArrayList<String> items = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    if (event.getInventory().getItem(i) != null && event.getInventory().getItem(i).getType() != Material.AIR){
                        items.add(event.getInventory().getItem(i).getType().name());
                    }
                }
                CompactCrates.getInstance().getChestConfig().set("cratesTypes." + crateID[0] + ".FillWith", items);

                CompactCrates.getInstance().saveChestsConfig();

                player.sendMessage(CompactCrates.getPrefix() + "§aSaved background items for crate " + crateName);
            }
        }
    }

}
