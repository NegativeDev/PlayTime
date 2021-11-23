package dev.negativekb.playtime.core.implementation;

import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.ProfileManager;
import dev.negativekb.playtime.api.RankManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayTimeAPIProvider extends PlayTimeAPI {

    private final ProfileManager profileManager;
    private final RankManager rankManager;
    public PlayTimeAPIProvider(JavaPlugin plugin) {
        setInstance(this);

        profileManager = new PlayTimeProfileManagerProvider(plugin);
        rankManager = new PlayTimeRankManagerProvider();
    }

    @Override
    public void onDisable() {
        profileManager.onDisable();
    }

    @Override
    public ProfileManager getProfileManager() {
        return profileManager;
    }

    @Override
    public RankManager getRankManager() {
        return rankManager;
    }
}
