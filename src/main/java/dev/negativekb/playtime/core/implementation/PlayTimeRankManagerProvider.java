package dev.negativekb.playtime.core.implementation;

import dev.negativekb.playtime.api.RankManager;
import dev.negativekb.playtime.core.structure.PlayTimeRank;
import net.luckperms.api.model.group.Group;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayTimeRankManagerProvider implements RankManager {

    private final HashMap<PlayTimeRank, Group> registeredRanks = new HashMap<>();

    /**
     * Registers a {@link PlayTimeRank} object and a LuckPerms API {@link Group}
     * to the local memory
     *
     * @param rank  {@link PlayTimeRank} object
     * @param group LuckPerms API {@link Group} object
     */
    @Override
    public void registerRank(PlayTimeRank rank, Group group) {
        registeredRanks.putIfAbsent(rank, group);
    }

    /**
     * Removes the provided {@link PlayTimeRank} object from the local memory
     *
     * @param rank Rank
     */
    @Override
    public void unRegisterRank(PlayTimeRank rank) {
        registeredRanks.remove(rank);
    }

    /**
     * Gets a {@link PlayTimeRank} from the provided {@link String}
     *
     * @param name Name of the {@link PlayTimeRank} object
     * @return If the {@link PlayTimeRank} is registered, return the object. If not, return empty.
     */
    @Override
    public Optional<PlayTimeRank> getRank(String name) {
        return registeredRanks.keySet().stream()
                .filter(group -> group.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Gets a {@link PlayTimeRank} from the provided {@link Group} object
     *
     * @param group {@link Group} object from LuckPerms API
     * @return If the {@link PlayTimeRank} is registered, return the object. If not, return empty.
     */
    @Override
    public Optional<PlayTimeRank> getRank(Group group) {
        return registeredRanks.entrySet().stream()
                .filter(e -> e.getValue().getName().equalsIgnoreCase(group.getName()))
                .map(Map.Entry::getKey).findFirst();
    }

    /**
     * Gets a {@link PlayTimeRank} from the provided {@link Integer}
     *
     * @param priority Priority of the {@link PlayTimeRank}
     * @return If the {@link PlayTimeRank} object with the provided priority is registered
     * it will return the {@link PlayTimeRank} object. If not, return empty.
     */
    @Override
    public Optional<PlayTimeRank> getRank(int priority) {
        return registeredRanks.keySet().stream()
                .filter(rank -> rank.getPriority() == priority)
                .findFirst();
    }

    /**
     * Gets the previous {@link PlayTimeRank} in the rank hierarchy from the provided {@link PlayTimeRank}
     *
     * @param rank {@link PlayTimeRank} object
     * @return If there is a {@link PlayTimeRank} with a priority one lower than {@param rank}, return.
     * If not, return empty.
     */
    @Override
    public Optional<PlayTimeRank> getPreviousRank(PlayTimeRank rank) {
        return getRank(rank.getPriority() - 1);
    }

    /**
     * Gets the next {@link PlayTimeRank} in the rank hierarchy from the provided {@link PlayTimeRank}
     *
     * @param rank {@link PlayTimeRank} object
     * @return If there is a {@link PlayTimeRank} with a priority one higher than {@param rank}, return.
     * If not, return empty.
     */
    @Override
    public Optional<PlayTimeRank> getNextRank(PlayTimeRank rank) {
        return getRank(rank.getPriority() + 1);
    }

    /**
     * Gets the LuckPerms API {@link Group} of the provided {@link PlayTimeRank}
     *
     * @param rank {@link PlayTimeRank} object
     * @return If there is a LuckPerms API {@link Group} object linked to {@param rank}, return.
     * If not, return empty.
     */
    @Override
    public Optional<Group> getGroup(PlayTimeRank rank) {
        return Optional.ofNullable(registeredRanks.getOrDefault(rank, null));
    }

    /**
     * Gets the LuckPerms API {@link Group} of the provided {@link String}
     *
     * @param rank Name of the {@link PlayTimeRank}
     * @return If there is a {@link PlayTimeRank} with the LuckPerms API {@link Group} object
     * registered in local memory, return. If not, return empty.
     */
    @Override
    public Optional<Group> getGroup(String rank) {
        Optional<PlayTimeRank> rankObject = getRank(rank);
        if (!rankObject.isPresent())
            return Optional.empty();

        PlayTimeRank playTimeRank = rankObject.get();
        return getGroup(playTimeRank);
    }

    /**
     * Gets the default {@link PlayTimeRank}
     *
     * @return If the default {@link PlayTimeRank} exists, return. If not, return empty.
     * Although, there should ALWAYS be a default rank.
     */
    @Override
    public Optional<PlayTimeRank> getDefaultRank() {
        return registeredRanks.keySet()
                .stream().filter(PlayTimeRank::isDefaultRank)
                .findFirst();
    }

}
