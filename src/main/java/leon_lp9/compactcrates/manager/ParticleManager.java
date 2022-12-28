package leon_lp9.compactcrates.manager;

import leon_lp9.compactcrates.CompactCrates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleManager {

    static double i = 0;
    public static void start() {

        Bukkit.getScheduler().runTaskTimer(CompactCrates.getInstance(), () -> {
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
}
