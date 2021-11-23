package dev.negativekb.playtime.commands.subcommands;

import dev.negativekb.api.plugin.command.SubCommand;
import dev.negativekb.api.plugin.command.annotation.CommandInfo;
import dev.negativekb.api.plugin.util.Utils;
import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.ProfileManager;
import dev.negativekb.playtime.api.RankManager;
import dev.negativekb.playtime.core.structure.PlayTimeRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

import static dev.negativekb.playtime.core.Locale.*;

@CommandInfo(name = "promote", permission = "playtime.promote", args = {"player"})
public class CommandPromote extends SubCommand {
    private final ProfileManager profileManager;
    private final RankManager rankManager;
    private final JavaPlugin plugin;

    public CommandPromote(JavaPlugin plugin) {
        this.plugin = plugin;

        PlayTimeAPI api = PlayTimeAPI.getInstance();
        profileManager = api.getProfileManager();
        rankManager = api.getRankManager();
    }

    @Override
    public void runCommand(CommandSender sender, String[] args) {
        String target = args[0];

        UUID uuid = profileManager.getUUIDFromName(target);
        if (uuid == null) {
            INVALID_PLAYER.replace("%name%", target).send(sender);
            return;
        }

        String rank = profileManager.database().getRank(uuid);
        Optional<PlayTimeRank> rankObject = rankManager.getRank(rank);
        if (!rankObject.isPresent()) {
            // Rank no longer exists.
            RANK_DOESNT_EXIST.send(sender);
            return;
        }

        PlayTimeRank playTimeRank = rankObject.get();
        Optional<PlayTimeRank> nextRank = rankManager.getNextRank(playTimeRank);
        if (!nextRank.isPresent()) {
            RANK_HIGHEST_RANK.send(sender);
            return;
        }

        String name = nextRank.get().getName();
        profileManager.database().updateRank(uuid, name);
        profileManager.getProfile(uuid).ifPresent(profile -> profile.setRank(name));
        PROMOTED.replace("%player%", target).send(sender);

        // I don't think there's an API method to add a group to a user.
        // So I'll go monke style and just run a command.
        Bukkit.getScheduler().runTask(plugin, () ->
                Utils.executeConsoleCommand("lp user " + target + " parent add " + nextRank.get().getLuckPermsGroup()));
    }
}
