package dev.negativekb.playtime.core.structure;

import lombok.Data;

@Data
public class PlayTimeRank {

    private final String name;
    private final long requiredPlayTime;
    private final String luckPermsGroup;
    private final int priority;
    private final boolean defaultRank;

}
