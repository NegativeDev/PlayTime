package dev.negativekb.playtime.core.implementation.registry;

import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.RankManager;
import dev.negativekb.playtime.api.exceptions.InvalidGroupException;
import dev.negativekb.playtime.api.registry.RankRegistry;
import dev.negativekb.playtime.core.structure.PlayTimeRank;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;

import java.util.Arrays;

public class PlayTimeRankRegistryProvider implements RankRegistry {

    private final RankManager rankManager;
    private final LuckPerms api;

    public PlayTimeRankRegistryProvider(LuckPerms api) {
        this.api = api;
        rankManager = PlayTimeAPI.getInstance().getRankManager();
    }

    @Override
    public void register(PlayTimeRank... ranks) {
        Arrays.stream(ranks).forEach(rank -> {
            String luckPermsGroup = rank.getLuckPermsGroup();
            Group group = api.getGroupManager().getGroup(luckPermsGroup);
            // Ensures that all the groups for the ranks are valid.
            if (group == null)
                throw new InvalidGroupException("The LuckPerms Group for rank `" + rank.getName() + "` is invalid!");

            rankManager.registerRank(rank, group);
        });
    }
}
