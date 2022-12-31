package leon_lp9.compactcrates.placeholders;

import leon_lp9.compactcrates.CompactCrates;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class UserPlaceHolders extends PlaceholderExpansion {


    @Override
    public @NotNull String getIdentifier() {
        return "compactcrates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Leon_lp9";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.4-SNAPSHOT";
    }

    public String onPlaceholderRequest(Player player, String identifier) {

        if(identifier.startsWith("cratename")) {
            AtomicReference<String> nameOFCrate = new AtomicReference<>("error");
            CompactCrates.getInstance().getChestConfig().getConfigurationSection("cratesTypes").getKeys(false).forEach(key -> {
                if (identifier.endsWith(key)) {
                    nameOFCrate.set(CompactCrates.getInstance().getChestConfig().getString("cratesTypes." + key + ".Name"));
                }
            });
            return nameOFCrate.get();
        }

        /*
        Check if the player is online,
        You should do this before doing anything regarding players
         */
        if(player == null){
            return "";
        }

        /*
        %compactcrates_name%
        Returns the player name
         */

        if (identifier.startsWith("usercratecount")){
            AtomicReference<String> count = new AtomicReference<>("error");
            CompactCrates.getInstance().getUserConfig().getConfigurationSection(player.getUniqueId().toString()).getKeys(false).forEach(key -> {
                if (identifier.endsWith(key)) {
                    count.set(CompactCrates.getInstance().getUserConfig().getString(player.getUniqueId().toString() + "." + key + ".Keys"));
                }
            });
            return count.get();
        }

        return null;
    }
}
