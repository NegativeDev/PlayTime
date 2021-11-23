package dev.negativekb.playtime.core;

import dev.negativekb.api.plugin.message.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum Locale {

    INVALID_PLAYER("invalid-player", Collections.singletonList(
            "&4&l(!) &7The player &c%name% &7does not exsit!"
    )),

    COOLDOWN_ACTIVE("cooldown-active", Collections.singletonList(
            "&4&l(!) &7Please wait a bit before doing this!"
    )),

    PLAYTIME_SELF("playtime-self", Collections.singletonList(
            "&eYou have &6%time%&eof playtime"
    )),

    PLAYTIME_OTHER("playtime-other", Collections.singletonList(
            "&6%player% &ehas &6%time%&eof playtime"
    )),

    RANK_DOESNT_EXIST("rank-doesnt-exist", Collections.singletonList(
            "&4&l(!) &7An error has occurred! The provided user's rank no longer exists. " +
                    "The next time they log on they will automatically be set to the default rank."
    )),

    RANK_LOWEST_RANK("rank-lowest-rank", Collections.singletonList(
            "&4&l(!) &7The provided user is already at the lowest rank possible."
    )),
    RANK_HIGHEST_RANK("rank-highest-rank", Collections.singletonList(
            "&4&l(!) &7The provided user is already at the highest rank possible."
    )),

    DEMOTED("demoted", Collections.singletonList(
            "&2&l(!) &aYou have successfully demoted &e%player%&a!"
    )),

    PROMOTED("promoted", Collections.singletonList(
            "&2&l(!) &aYou have successfully promoted &e%player%&a!"
    )),
    SET_TIME("set-time", Collections.singletonList(
            "&2&l(!) &aYou have successfully set &e%player%&a's playtime to &e%time%&a."
    ))
    ;
    private final String id;
    private final List<String> defaultMessage;
    private Message message;

    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "messages.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            Arrays.stream(values()).forEach(locale -> {
                String id = locale.getId();
                List<String> defaultMessage = locale.getDefaultMessage();

                config.set(id, defaultMessage);
            });

        } else {
            Arrays.stream(values()).filter(locale -> {
                String id = locale.getId();
                return (config.get(id, null) == null);
            }).forEach(locale -> config.set(locale.getId(), locale.getDefaultMessage()));

        }
        config.save(configFile);

        // Creates the message objects
        Arrays.stream(values()).forEach(locale ->
                locale.message = new Message(config.getStringList(locale.getId())
                        .toArray(new String[0])));
    }

    public void send(CommandSender sender) {
        message.send(sender);
    }

    public void send(List<Player> players) {
        message.send(players);
    }

    public void broadcast() {
        message.broadcast();
    }

    public Message replace(Object o1, Object o2) {
        return message.replace(o1, o2);
    }
}
