package dev.negativekb.playtime.core.structure;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Data
public class PlayTimeProfile {

    private final UUID uuid;
    private long playTime;
    private String rank;
    private String name;

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void addTime(long time) {
        setPlayTime(getPlayTime() + time);
    }
}
