package dev.negativekb.playtime.api;

import dev.negativekb.playtime.api.option.Disableable;
import dev.negativekb.playtime.core.implementation.database.tables.PlayTimeUserTable;
import dev.negativekb.playtime.core.structure.PlayTimeProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Negative
 * @since November 18th, 2021
 */
public interface ProfileManager extends Disableable {

    /**
     * Get a Profile from a UUID
     * @param uuid UUID
     * @return If the profile exists
     */
    Optional<PlayTimeProfile> getProfile(UUID uuid);

    /**
     * Get a Profile from a Player
     * @param player Player
     * @return If the profile exists
     */
    Optional<PlayTimeProfile> getProfile(Player player);

    /**
     * Get a Profile from an Offline Player
     * @param player Offline Player
     * @return If the profile exists
     */
    Optional<PlayTimeProfile> getProfile(OfflinePlayer player);

    /**
     * Load the UUID into a {@link PlayTimeProfile} object from the database
     * @param uuid UUID
     */
    void load(String name, UUID uuid);

    /**
     * Saves and unloads the {@link PlayTimeProfile} object from local memory into
     * the database.
     *
     * @param uuid UUID
     */
    void unLoad(UUID uuid);

    /**
     * Gets the {@link UUID} from the provided {@link String}
     * @param name Name of the Player to lookup
     * @return UUID from the database
     */
    UUID getUUIDFromName(String name);

    /**
     * @return The current instance of the user table
     */
    PlayTimeUserTable database();

    /**
     * Returns all cached profiles
     */
    ArrayList<PlayTimeProfile> getProfiles();
}
