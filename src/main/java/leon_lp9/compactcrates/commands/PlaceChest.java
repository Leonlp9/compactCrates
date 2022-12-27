package leon_lp9.compactcrates.commands;

import leon_lp9.compactcrates.CompactCrates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PlaceChest implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("compactcrates.placechest")) {
            sender.sendMessage(CompactCrates.getInstance().getLanguageConfig().getString("prefix") + "You don't have permission to use this command!");
            return true;
        }




        return false;
    }
}
