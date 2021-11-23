package dev.negativekb.playtime.api;

import dev.negativekb.playtime.api.option.Disableable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Negative
 * @since November 18th, 2021
 */
public abstract class PlayTimeAPI implements Disableable {

    @Getter @Setter
    private static PlayTimeAPI instance;

    public abstract ProfileManager getProfileManager();

    public abstract RankManager getRankManager();
}
