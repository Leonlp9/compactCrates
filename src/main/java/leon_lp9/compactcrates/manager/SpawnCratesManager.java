package leon_lp9.compactcrates.manager;

import leon_lp9.compactcrates.CompactCrates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.UUID;

public class SpawnCratesManager {

    public static final HashMap<Location, Material> beforCrates = new HashMap<>();
    public static final HashMap<String, String> getArmorStand = new HashMap<>();
    public static final HashMap<String, String> getArmorStandClick = new HashMap<>();

    public static void spawnCrates() {
        removeCrates();

        if (CompactCrates.getInstance().getChestConfig().contains("chestsPositions")){
            for (String key : CompactCrates.getInstance().getChestConfig().getConfigurationSection("chestsPositions").getKeys(false)) {
                String world = CompactCrates.getInstance().getChestConfig().getString("chestsPositions." + key + ".world");
                int x = CompactCrates.getInstance().getChestConfig().getInt("chestsPositions." + key + ".x");
                int y = CompactCrates.getInstance().getChestConfig().getInt("chestsPositions." + key + ".y");
                int z = CompactCrates.getInstance().getChestConfig().getInt("chestsPositions." + key + ".z");
                String type = CompactCrates.getInstance().getChestConfig().getString("chestsPositions." + key + ".type");
                String name = CompactCrates.getInstance().getChestConfig().getString("chestsPositions." + key + ".name").replace("&", "§");


                if (world != null && type != null && name != null) {
                    Location location = new Location(Bukkit.getWorld(world), x, y, z);

                    if (!beforCrates.containsKey(location)) {
                        beforCrates.put(location, location.getBlock().getType());
                    }

                    try {
                        location.getBlock().setType(Material.valueOf(type));
                    }catch (Exception e) {
                        CompactCrates.getInstance().getLogger().warning(CompactCrates.getPrefix() + "The type of the chest " + name + " is not valid!");
                    }

                    ArmorStand armorStand = location.getWorld().spawn(location.clone().add(0.5, 1.2, 0.5), ArmorStand.class);
                    armorStand.setCustomName(name);
                    armorStand.setCustomNameVisible(true);
                    armorStand.setGravity(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setMarker(true);
                    armorStand.setInvisible(true);
                    armorStand.setSilent(true);
                    armorStand.setPersistent(true);
                    armorStand.setCollidable(false);

                    getArmorStand.put(key, armorStand.getUniqueId().toString());

                    ArmorStand armorStandKlick = location.getWorld().spawn(location.clone().add(0.5, 0.9, 0.5), ArmorStand.class);
                    armorStandKlick.setCustomName(CompactCrates.getInstance().getLanguageConfig().getString("click").replace("&", "§"));
                    armorStandKlick.setCustomNameVisible(true);
                    armorStandKlick.setGravity(false);
                    armorStandKlick.setInvulnerable(true);
                    armorStandKlick.setMarker(true);
                    armorStandKlick.setInvisible(true);
                    armorStandKlick.setSilent(true);
                    armorStandKlick.setPersistent(true);
                    armorStandKlick.setCollidable(false);

                    getArmorStandClick.put(key, armorStandKlick.getUniqueId().toString());
                }
            }
        }
    }

    public static void removeCrates() {
        for (Location location : beforCrates.keySet()) {
            location.getBlock().setType(beforCrates.get(location));
        }
        beforCrates.clear();
        getArmorStand.forEach((id, name) -> {
            ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(UUID.fromString(name));
            if (armorStand != null) {
                armorStand.remove();
            }
        });
        getArmorStand.clear();
        getArmorStandClick.forEach((id, name) -> {
            ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(UUID.fromString(name));
            if (armorStand != null) {
                armorStand.remove();
            }
        });
        getArmorStandClick.clear();

    }

    public static Boolean isCrate(Location location) {
        return beforCrates.containsKey(location);
    }

    public static void removeCrate(Location location) {
        //Loop through all the chests in the config
        for (String key : CompactCrates.getInstance().getChestConfig().getConfigurationSection("chestsPositions").getKeys(false)) {
            //Get the location of the chest
            String world = CompactCrates.getInstance().getChestConfig().getString("chestsPositions." + key + ".world");
            int x = CompactCrates.getInstance().getChestConfig().getInt("chestsPositions." + key + ".x");
            int y = CompactCrates.getInstance().getChestConfig().getInt("chestsPositions." + key + ".y");
            int z = CompactCrates.getInstance().getChestConfig().getInt("chestsPositions." + key + ".z");
            //Check if the location is the same as the location of the chest
            if (location.getWorld().getName().equals(world) && location.getBlockX() == x && location.getBlockY() == y && location.getBlockZ() == z) {
                //Remove the chest from the config
                CompactCrates.getInstance().getChestConfig().set("chestsPositions." + key, null);
                //Save the config
                CompactCrates.getInstance().saveChestsConfig();
                //Spawn the chests
                location.getBlock().setType(beforCrates.get(location));
                beforCrates.remove(location);
                spawnCrates();
                //Stop the loop
                break;
            }
        }
    }

    public static Location getNearbyCrate(Location location) {
        for (Location loc : beforCrates.keySet()) {
            if (loc.getWorld().equals(location.getWorld())) {
                if (loc.distance(location) <= 8.5) {
                    return loc;
                }
            }
        }
        return null;
    }

}
