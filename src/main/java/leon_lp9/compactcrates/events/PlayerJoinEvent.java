package leon_lp9.compactcrates.events;

import leon_lp9.compactcrates.CompactCrates;
import leon_lp9.compactcrates.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent event){
        if (event.getPlayer().hasPermission("compactcrates.notify.update")) {
            Bukkit.getScheduler().runTaskLater(CompactCrates.getInstance(), () -> {
                new UpdateChecker(CompactCrates.getInstance(), 107018).getVersion(version -> {
                    if (!CompactCrates.getInstance().getDescription().getVersion().equals(version)) {
                        event.getPlayer().sendMessage(CompactCrates.getPrefix() + "There is a new update available.");
                        event.getPlayer().sendMessage(CompactCrates.getPrefix() + "Your are using version §e" + CompactCrates.getInstance().getDescription().getVersion());
                        event.getPlayer().sendMessage(CompactCrates.getPrefix() + "The latest version is §e" + version);
                        event.getPlayer().sendMessage(CompactCrates.getPrefix() + "Download it here: https://www.spigotmc.org/resources/compactcrates.107018/");
                    }
                });
            }, 40);

        }
    }
}
