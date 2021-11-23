package dev.negativekb.playtime.events;

import dev.negativekb.api.plugin.event.PluginEvent;
import dev.negativekb.playtime.core.structure.PlayTimeProfile;
import dev.negativekb.playtime.core.structure.PlayTimeRank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class PlayTimeRankupEvent extends PluginEvent {

    private final Player player;
    private final PlayTimeRank nextRank;
    private final PlayTimeProfile profile;
    private boolean cancelled;

}
