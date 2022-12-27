package leon_lp9.compactcrates;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager implements Listener {

    public static void openFirstInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, CompactCrates.getInstance().getConfig().getInt("inventorySize"), CompactCrates.getInstance().getLanguageConfig().getString("firstInventoryName"));

        CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(s -> {
            ItemStack itemStack = new ItemBuilder(Material.getMaterial(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Type"))).setLocalizedName("crate").setDisplayName(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Name")).build();
            inventory.setItem(Integer.parseInt(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + s + ".Slot")) ,itemStack);
        });

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(CompactCrates.getInstance().getLanguageConfig().getString("firstInventoryName"))) {
            event.setCancelled(true);
        }
    }

}
