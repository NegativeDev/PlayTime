package dev.negativekb.playtime.commands;

import dev.negativekb.api.plugin.command.Command;
import dev.negativekb.api.plugin.command.annotation.CommandInfo;
import dev.negativekb.api.plugin.util.TimeUtil;
import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.ProfileManager;
import dev.negativekb.playtime.commands.subcommands.CommandDemote;
import dev.negativekb.playtime.commands.subcommands.CommandPromote;
import dev.negativekb.playtime.commands.subcommands.CommandSetPlayTime;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static dev.negativekb.playtime.core.Locale.*;

@CommandInfo(name = "playtime", aliases = {"pt"}, playerOnly = true)
public class CommandPlayTime extends Command {

    private final ProfileManager profileManager;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public CommandPlayTime(JavaPlugin plugin) {
        profileManager = PlayTimeAPI.getInstance().getProfileManager();

        addSubCommands(
            new CommandPromote(plugin),
            new CommandDemote(plugin),
            new CommandSetPlayTime()
        );

        new CooldownTask().runTaskTimerAsynchronously(plugin, 0, 20);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (args.length == 0) {
            profileManager.getProfile(uuid).ifPresent(profile -> {
                long playTime = profile.getPlayTime();
                String format = TimeUtil.format((System.currentTimeMillis() + playTime), System.currentTimeMillis());

                PLAYTIME_SELF.replace("%time%", format).send(player);
            });
            return;
        }

        String target = args[0];
        UUID uuidFromName = profileManager.getUUIDFromName(target);
        if (uuidFromName == null) {
            INVALID_PLAYER.replace("%name%", target).send(player);
            return;
        }

        boolean onCooldown = cooldowns.containsKey(uuid);
        if (onCooldown) {
            COOLDOWN_ACTIVE.send(player);
            return;
        }

        // 3 second cooldown to prevent spamming the database.
        cooldowns.put(uuid, (System.currentTimeMillis() + (1000L * 3)));

        long playTime = profileManager.database().getPlayTime(uuidFromName);
        String format = TimeUtil.format((System.currentTimeMillis() + playTime), System.currentTimeMillis());

        PLAYTIME_OTHER.replace("%player%", target).replace("%time%", format).send(player);
    }

    private class CooldownTask extends BukkitRunnable {

        @Override
        public void run() {
            List<UUID> toRemove = cooldowns.entrySet().stream()
                    .filter(entry -> System.currentTimeMillis() >= entry.getValue()).map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            toRemove.forEach(cooldowns::remove);
        }
    }
}