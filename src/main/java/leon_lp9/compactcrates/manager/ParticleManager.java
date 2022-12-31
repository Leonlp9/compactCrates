package leon_lp9.compactcrates.manager;

import leon_lp9.compactcrates.CompactCrates;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ParticleManager implements Listener {

    public static BukkitTask task;

    static double i = 0;
    public static void start() {

        task = Bukkit.getScheduler().runTaskTimer(CompactCrates.getInstance(), () -> {
            if (CompactCrates.getInstance().getChestConfig().contains("chestsPositions")) {
                CompactCrates.getInstance().getChestConfig().getConfigurationSection("chestsPositions").getKeys(false).forEach(s -> {
                    Location location = new Location(Bukkit.getWorld(CompactCrates.getInstance().getChestConfig().getString("chestsPositions." + s + ".world")), CompactCrates.getInstance().getChestConfig().getDouble("chestsPositions." + s + ".x"), CompactCrates.getInstance().getChestConfig().getDouble("chestsPositions." + s + ".y"), CompactCrates.getInstance().getChestConfig().getDouble("chestsPositions." + s + ".z"));

                    try {
                        location.getWorld().spawnParticle(Particle.valueOf(CompactCrates.getInstance().getConfig().getString("Particle")), location.clone().add(0.5, 0.2, 0.5).add(Math.sin(i), Math.sin(i / 3), Math.cos(i)), 1, 0, 0, 0, 0);
                        location.getWorld().spawnParticle(Particle.valueOf(CompactCrates.getInstance().getConfig().getString("Particle")), location.clone().add(0.5, 0.2, 0.5).add(-Math.sin(i), -Math.sin(i / 3), -Math.cos(i)), 1, 0, 0, 0, 0);
                    } catch (Exception e) {
                        CompactCrates.getInstance().getLogger().warning(CompactCrates.getPrefix() + "The particle is not valid!");
                        CompactCrates.getInstance().getConfig().set("Particle", "VILLAGER_ANGRY");
                        CompactCrates.getInstance().saveConfig();
                    }
                });

                if (i < 360) {
                    i = i + 0.2;
                } else {
                    i = 0;
                }
            }
        }, 0, 1);
    }

    public static void stop() {
        task.cancel();
    }

    static ArrayList<String> uuids = new ArrayList<>();
    public static void spawnFallingBlocksAboveNearbyChest(Location location) {
        Location loc = SpawnCratesManager.getNearbyCrate(location);
        if (loc != null) {
            Random r = new Random();

            List<Material> mats = List.of(Material.DIAMOND_BLOCK, Material.GOLD_BLOCK, Material.NETHERITE_BLOCK, Material.EMERALD_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.REDSTONE_BLOCK);

            for (int j = 0; j < 10; j++) {
                FallingBlock fb = loc.getWorld().spawnFallingBlock(loc.clone().add(0.5, 1, 0.5), mats.get(r.nextInt(mats.size())).createBlockData());
                fb.setDropItem(false);
                fb.setHurtEntities(false);

                fb.setVelocity(new Location(loc.getWorld(), 0, 0.5, 0, r.nextInt(360), 0).getDirection().multiply(r.nextDouble() / 2).setY(r.nextDouble() / 2 + 0.2));
                uuids.add(fb.getUniqueId().toString());
            }

        }
    }

    public static void spawnFireworkAboveNearbyChest(Location location) {
        Location loc = SpawnCratesManager.getNearbyCrate(location);
        if (loc != null) {
            Firework fw = (Firework) loc.getWorld().spawnEntity(loc.clone().add(0.5, 1, 0.5), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(0);
            fwm.addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.GREEN).withColor(Color.BLUE).withColor(Color.YELLOW).withColor(Color.ORANGE).withColor(Color.PURPLE).withColor(Color.WHITE).withColor(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).build());

            fw.setFireworkMeta(fwm);

            uuids.add(fw.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(CompactCrates.getInstance(), fw::detonate, 10);
        }
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if ((event.getEntityType() == EntityType.FALLING_BLOCK)) {
            if (uuids.contains(event.getEntity().getUniqueId().toString())) {
                event.setCancelled(true);
                uuids.remove(event.getEntity().getUniqueId().toString());
            }
        }
    }


}
