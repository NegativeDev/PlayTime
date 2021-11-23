package dev.negativekb.playtime.api;

import dev.negativekb.playtime.core.structure.PlayTimeRank;
import net.luckperms.api.model.group.Group;

import java.util.Optional;

/**
 * @author Negative
 * @since November 20th, 2021
 */
public interface RankManager {

    /**
     * Registers a {@link PlayTimeRank} object and a LuckPerms API {@link Group}
     * to the local memory
     *
     * @param rank {@link PlayTimeRank} object
     * @param group LuckPerms API {@link Group} object
     */
    void registerRank(PlayTimeRank rank, Group group);

    /**
     * Removes the provided {@link PlayTimeRank} object from the local memory
     * @param rank Rank
     */
    void unRegisterRank(PlayTimeRank rank);

    /**
     * Gets a {@link PlayTimeRank} from the provided {@link String}
     * @param name Name of the {@link PlayTimeRank} object
     * @return If the {@link PlayTimeRank} is registered, return the object. If not, return empty.
     */
    Optional<PlayTimeRank> getRank(String name);

    /**
     * Gets a {@link PlayTimeRank} from the provided {@link Group} object
     * @param group {@link Group} object from LuckPerms API
     * @return If the {@link PlayTimeRank} is registered, return the object. If not, return empty.
     */
    Optional<PlayTimeRank> getRank(Group group);

    /**
     * Gets a {@link PlayTimeRank} from the provided {@link Integer}
     * @param priority Priority of the {@link PlayTimeRank}
     * @return If the {@link PlayTimeRank} object with the provided priority is registered
     * it will return the {@link PlayTimeRank} object. If not, return empty.
     */
    Optional<PlayTimeRank> getRank(int priority);

    /**
     * Gets the previous {@link PlayTimeRank} in the rank hierarchy from the provided {@link PlayTimeRank}
     * @param rank {@link PlayTimeRank} object
     * @return If there is a {@link PlayTimeRank} with a priority one lower than {@param rank}, return.
     * If not, return empty.
     */
    Optional<PlayTimeRank> getPreviousRank(PlayTimeRank rank);

    /**
     * Gets the next {@link PlayTimeRank} in the rank hierarchy from the provided {@link PlayTimeRank}
     * @param rank {@link PlayTimeRank} object
     * @return If there is a {@link PlayTimeRank} with a priority one higher than {@param rank}, return.
     * If not, return empty.
     */
    Optional<PlayTimeRank> getNextRank(PlayTimeRank rank);

    /**
     * Gets the LuckPerms API {@link Group} of the provided {@link PlayTimeRank}
     * @param rank {@link PlayTimeRank} object
     * @return If there is a LuckPerms API {@link Group} object linked to {@param rank}, return.
     * If not, return empty.
     */
    Optional<Group> getGroup(PlayTimeRank rank);

    /**
     * Gets the LuckPerms API {@link Group} of the provided {@link String}
     * @param rank Name of the {@link PlayTimeRank}
     * @return If there is a {@link PlayTimeRank} with the LuckPerms API {@link Group} object
     * registered in local memory, return. If not, return empty.
     */
    Optional<Group> getGroup(String rank);

    /**
     * Gets the default {@link PlayTimeRank}
     * @return If the default {@link PlayTimeRank} exists, return. If not, return empty.
     * Although, there should ALWAYS be a default rank.
     */
    Optional<PlayTimeRank> getDefaultRank();

}
