package dev.negativekb.playtime.commands.subcommands;

import dev.negativekb.api.plugin.command.SubCommand;
import dev.negativekb.api.plugin.command.annotation.CommandInfo;
import dev.negativekb.api.plugin.util.TimeUtil;
import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.ProfileManager;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static dev.negativekb.playtime.core.Locale.INVALID_PLAYER;
import static dev.negativekb.playtime.core.Locale.SET_TIME;

@CommandInfo(name = "settime", permission = "playtime.settime", args = {"player", "time"})
public class CommandSetPlayTime extends SubCommand {

    private final ProfileManager profileManager;

    public CommandSetPlayTime() {
        PlayTimeAPI api = PlayTimeAPI.getInstance();
        profileManager = api.getProfileManager();
    }

    @Override
    public void runCommand(CommandSender sender, String[] args) {
        String target = args[0];
        UUID uuid = profileManager.getUUIDFromName(target);
        if (uuid == null) {
            INVALID_PLAYER.replace("%name%", target).send(sender);
            return;
        }

        String time = args[1];
        Long timeInMills = TimeUtil.longFromString(time);

        profileManager.database().updateTime(uuid, timeInMills);
        profileManager.getProfile(uuid).ifPresent(profile -> profile.setPlayTime(timeInMills));
        SET_TIME.replace("%player%", target).replace("%time%", time).send(sender);
    }
}
