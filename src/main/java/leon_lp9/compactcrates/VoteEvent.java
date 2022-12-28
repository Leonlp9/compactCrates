package leon_lp9.compactcrates;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class VoteEvent implements Listener {

    @EventHandler(priority= EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();

        giveVoteReward(vote.getUsername());
    }

    public static void giveVoteReward(String username){
        if (CompactCrates.getInstance().getConfig().contains("VoteRewards")) {
            ArrayList<String> commands = (ArrayList<String>) CompactCrates.getInstance().getConfig().getStringList("VoteRewards");
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", username));
            }
        }else {
            CompactCrates.getInstance().getLogger().warning("VoteRewards is not set in the config.yml");
            CompactCrates.getInstance().getConfig().set("VoteRewards", new ArrayList<>());
        }
    }
}
