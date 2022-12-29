package leon_lp9.compactcrates.events;

import leon_lp9.compactcrates.CompactCrates;
import leon_lp9.compactcrates.InventoryManager;
import leon_lp9.compactcrates.builder.ItemChecker;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CratePlaceBreakEvent implements Listener {

    @EventHandler
    public void onCratePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (new ItemChecker(event.getItemInHand()).isLocalizedName("crate")) {
            if (player.hasPermission("compactcrates.place")) {
                event.getPlayer().sendMessage(CompactCrates.getPrefix());
                event.getPlayer().sendMessage(CompactCrates.getPrefix() + "§aYou have placed a crate!");
                event.getPlayer().sendMessage(CompactCrates.getPrefix() + "§cRemove§7 it by shift left clicking!");
                event.getPlayer().sendMessage(CompactCrates.getPrefix() + "§7Right click the crate to open it!");
                event.getPlayer().sendMessage(CompactCrates.getPrefix());

                Block block = event.getBlockPlaced();

                int getChests = 0;
                try {
                    getChests = CompactCrates.getInstance().getChestConfig().getConfigurationSection("chestsPositions").getKeys(false).size();
                } catch (Exception e) {
                    System.out.println(CompactCrates.getPrefix() + "First chest placed!");
                }

                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + getChests + ".world", block.getLocation().getWorld().getName());
                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + getChests + ".x", block.getLocation().getBlockX());
                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + getChests + ".y", block.getLocation().getBlockY());
                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + getChests + ".z", block.getLocation().getBlockZ());
                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + getChests + ".type", Material.DRIPSTONE_BLOCK.toString());
                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + getChests + ".name", "&6Default &eCrate &6Name");

                CompactCrates.getInstance().saveChestsConfig();

                SpawnCratesManager.beforCrates.put(event.getBlock().getLocation(), event.getBlock().getType());

                SpawnCratesManager.spawnCrates();
            }

        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
                if (player.hasPermission("compactcrates.break")) {
                    event.getPlayer().sendMessage(CompactCrates.getPrefix());
                    event.getPlayer().sendMessage(CompactCrates.getPrefix() + "§aYou have removed a crate!");
                    event.getPlayer().sendMessage(CompactCrates.getPrefix());

                    SpawnCratesManager.removeCrate(event.getBlock().getLocation());

                    event.setDropItems(false);
                }else {
                    event.getPlayer().sendMessage(CompactCrates.getPrefix() + "§cYou don't have permission to break crates!");
                    event.setCancelled(true);
                }
            }
        }else {
            if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
                event.setCancelled(true);
                if (player.hasPermission("compactcrates.open")) {
                    InventoryManager.openFirstInventory(player);
                }else {
                    player.sendMessage(CompactCrates.getPrefix() + "§cYou don't have permission to open crates!");
                }
            }
        }
    }

    @EventHandler
    public void onCrateOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (SpawnCratesManager.isCrate(block.getLocation())) {
                event.setCancelled(true);
                if (player.hasPermission("compactcrates.open")) {
                    InventoryManager.openFirstInventory(player);
                }else {
                    player.sendMessage(CompactCrates.getPrefix() + "§cYou don't have permission to open crates!");
                }
            }
        }
    }

    //Piston event
    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (SpawnCratesManager.isCrate(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (SpawnCratesManager.isCrate(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    //Fire event
    @EventHandler
    public void onFire(BlockBurnEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockIgniteEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockSpreadEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockFromToEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(EntityChangeBlockEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> SpawnCratesManager.isCrate(block.getLocation()));
    }

    @EventHandler
    public void onFire(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> SpawnCratesManager.isCrate(block.getLocation()));
    }

    @EventHandler
    public void onFire(BlockPhysicsEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockDamageEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockFadeEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockFormEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFire(BlockGrowEvent event) {
        if (SpawnCratesManager.isCrate(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

}
