package dev.negativekb.playtime.listeners;

import dev.negativekb.api.plugin.util.Utils;
import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.ProfileManager;
import dev.negativekb.playtime.api.RankManager;
import dev.negativekb.playtime.core.structure.PlayTimeProfile;
import dev.negativekb.playtime.core.structure.PlayTimeRank;
import dev.negativekb.playtime.events.PlayTimeRankupEvent;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayTimeListener implements Listener {

    private final ProfileManager profileManager;
    private final RankManager rankManager;
    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> timeTable = new HashMap<>();

    public PlayTimeListener(JavaPlugin plugin) {
        this.plugin = plugin;

        PlayTimeAPI api = PlayTimeAPI.getInstance();
        profileManager = api.getProfileManager();
        rankManager = api.getRankManager();

        new PlayTimeUpdateTask().runTaskTimerAsynchronously(plugin, 0, 20 * 5);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        profileManager.load(player.getName(), uuid);

        profileManager.getProfile(player).ifPresent(profile -> {
            // TODO: Clean up code
            // TODO: If the user joins with an "invalid rank", set them to default.
            if (profile.getRank() == null || profile.getRank().equals("No Rank")) {
                rankManager.getDefaultRank().ifPresent(rank -> profile.setRank(rank.getName()));
            }

        });
        timeTable.putIfAbsent(uuid, System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        profileManager.getProfile(uuid).ifPresent(profile -> {
            UUID unique = profile.getUuid();
            long current = System.currentTimeMillis();
            Long timeStamp = timeTable.get(unique);
            long playDuration = current - timeStamp;

            profile.addTime(playDuration);
            timeTable.remove(unique);
        });
        profileManager.unLoad(uuid);

    }


    private class PlayTimeUpdateTask extends BukkitRunnable {

        @Override
        public void run() {
            // Gather a list of profiles to unload in case of some random
            // event happening where logging off will not save the data.
            List<PlayTimeProfile> toUnload = profileManager.getProfiles().stream()
                    .filter(profile -> !profile.getPlayer().isPresent())
                    .collect(Collectors.toList());

            toUnload.forEach(profile -> profileManager.unLoad(profile.getUuid()));

            // Loops through all profiles and updates their values
            profileManager.getProfiles().forEach(profile -> {
                UUID unique = profile.getUuid();
                long current = System.currentTimeMillis();
                Long timeStamp = timeTable.get(unique);
                long playDuration = current - timeStamp;

                profile.addTime(playDuration);
                timeTable.replace(unique, System.currentTimeMillis());
            });

            // Check for potential rankups
            profileManager.getProfiles().stream().filter(profile -> {
                if (!profile.getPlayer().isPresent())
                    return false;

                Optional<PlayTimeRank> rank = rankManager.getRank(profile.getRank());
                if (!rank.isPresent())
                    return false;

                PlayTimeRank playTimeRank = rank.get();
                Optional<PlayTimeRank> nextRank = rankManager.getNextRank(playTimeRank);
                if (!nextRank.isPresent())
                    return false;

                PlayTimeRank timeRank = nextRank.get();
                long requiredPlayTime = timeRank.getRequiredPlayTime();
                return (profile.getPlayTime() >= requiredPlayTime);
            }).forEach(profile -> {
                // These players are eligible to rankup
                assert profile.getPlayer().isPresent();
                assert rankManager.getRank(profile.getRank()).isPresent();
                PlayTimeRank playTimeRank = rankManager.getRank(profile.getRank()).get();

                assert rankManager.getNextRank(playTimeRank).isPresent();
                PlayTimeRank next = rankManager.getNextRank(playTimeRank).get();

                PlayTimeRankupEvent event = new PlayTimeRankupEvent(profile.getPlayer().get(), next, profile);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled())
                    return;

                profile.setRank(next.getName());

                assert rankManager.getGroup(next).isPresent();
                Group group = rankManager.getGroup(next).get();

                // I don't think there's an API method to add a group to a user.
                // So I'll go monke style and just run a command.
                Bukkit.getScheduler().runTask(plugin, () ->
                        Utils.executeConsoleCommand("lp user " + profile.getOfflinePlayer().getName() +
                                " parent add " + group.getName()));
            });
        }
    }

}
