package dev.negativekb.playtime.core.implementation;

import dev.negativekb.api.plugin.util.Utils;
import dev.negativekb.playtime.api.ProfileManager;
import dev.negativekb.playtime.core.implementation.database.tables.PlayTimeUserTable;
import dev.negativekb.playtime.core.structure.PlayTimeProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class PlayTimeProfileManagerProvider implements ProfileManager {

    private final ArrayList<PlayTimeProfile> profiles = new ArrayList<>();
    private final PlayTimeUserTable table;
    public PlayTimeProfileManagerProvider(JavaPlugin plugin) {
        table = (PlayTimeUserTable) PlayTimeUserTable.getInstance();

//        new ProfileSaveTask().runTaskTimerAsynchronously(plugin, 0, 20 * 120);
        new ProfileSaveTask().runTaskTimerAsynchronously(plugin, 0, 20 * 30);
    }

    /**
     * Get a Profile from a UUID
     *
     * @param uuid UUID
     * @return If the profile exists
     */
    @Override
    public Optional<PlayTimeProfile> getProfile(UUID uuid) {
        return profiles.stream().filter(profile -> profile.getUuid().equals(uuid))
                .findFirst();
    }

    /**
     * Get a Profile from a Player
     *
     * @param player Player
     * @return If the profile exists
     */
    @Override
    public Optional<PlayTimeProfile> getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    /**
     * Get a Profile from an Offline Player
     *
     * @param player Offline Player
     * @return If the profile exists
     */
    @Override
    public Optional<PlayTimeProfile> getProfile(OfflinePlayer player) {
        return getProfile(player.getUniqueId());
    }

    /**
     * Load the UUID into a {@link PlayTimeProfile} object from the database
     *
     * @param uuid UUID
     */
    @Override
    public void load(String name, UUID uuid) {
        long playTime = table.getPlayTime(uuid);
        String rank = table.getRank(uuid);

        PlayTimeProfile cacheProfile = new PlayTimeProfile(uuid);
        cacheProfile.setRank(rank);
        cacheProfile.setName(name);
        cacheProfile.setPlayTime(playTime);

        profiles.add(cacheProfile);
    }

    /**
     * Saves and unloads the {@link PlayTimeProfile} object from local memory into
     * the database.
     *
     * @param uuid UUID
     */
    @Override
    public void unLoad(UUID uuid) {
        getProfile(uuid).ifPresent(profile -> {
            table.addOrUpdate(profile);
            profiles.remove(profile);
        });
    }

    /**
     * Gets the {@link UUID} from the provided {@link String}
     *
     * @param name Name of the Player to lookup
     * @return UUID from the database
     */
    @Override
    public UUID getUUIDFromName(String name) {
        return table.getUUID(name);
    }

    /**
     * @return The current instance of the user table
     */
    @Override
    public PlayTimeUserTable database() {
        return table;
    }

    /**
     * Returns all cached profiles
     */
    @Override
    public ArrayList<PlayTimeProfile> getProfiles() {
        return profiles;
    }

    @Override
    public void onDisable() {
        profiles.forEach(table::addOrUpdate);
    }

    private class ProfileSaveTask extends BukkitRunnable {

        @Override
        public void run() {
            profiles.forEach(table::addOrUpdate);
        }
    }
}
