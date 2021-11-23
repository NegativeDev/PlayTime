package dev.negativekb.playtime;

import dev.negativekb.api.plugin.BasePlugin;
import dev.negativekb.api.plugin.util.TimeUtil;
import dev.negativekb.playtime.api.Database;
import dev.negativekb.playtime.api.PlayTimeAPI;
import dev.negativekb.playtime.api.exceptions.LuckPermsNotFoundException;
import dev.negativekb.playtime.api.registry.RankRegistry;
import dev.negativekb.playtime.commands.CommandPlayTime;
import dev.negativekb.playtime.core.Locale;
import dev.negativekb.playtime.core.implementation.PlayTimeAPIProvider;
import dev.negativekb.playtime.core.implementation.database.PlayTimeExternalDatabaseProvider;
import dev.negativekb.playtime.core.implementation.database.PlayTimeLocalDatabaseProvider;
import dev.negativekb.playtime.core.implementation.database.tables.PlayTimeUserTable;
import dev.negativekb.playtime.core.implementation.registry.PlayTimeRankRegistryProvider;
import dev.negativekb.playtime.core.structure.PlayTimeRank;
import dev.negativekb.playtime.core.util.ConfigUtils;
import dev.negativekb.playtime.listeners.PlayTimeListener;
import lombok.Getter;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class PlayTime extends BasePlugin {

    @Getter
    private static PlayTime instance;
    @Getter
    private LuckPerms api;

    @Override
    public void onEnable() {
        super.onEnable();
        Locale.init(this);
        instance = this;

        loadFiles(this, "config.yml", "database.yml");

        // Initialize Database
        initDatabase();
        new PlayTimeAPIProvider(this);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();

            // Will initialize Ranks
            ConfigurationSection ranks = getConfig().getConfigurationSection("ranks");
            if (ranks == null)
                throw new NullPointerException("There seems to be no `ranks` configuration section. Please reset or fix your configuration file.");

            RankRegistry rankRegistry = new PlayTimeRankRegistryProvider(api);
            ranks.getKeys(false).forEach(key -> {
                String path = key + ".";

                int priority = ranks.getInt(path + "priority");
                String group = ranks.getString(path + "luckperms-group");
                String reqTime = ranks.getString(path + "required-time");
                boolean defRank = ranks.getBoolean(path + "default-rank", false);
                Long requiredTime = TimeUtil.longFromString(reqTime);

                PlayTimeRank rank = new PlayTimeRank(key, requiredTime, group, priority, defRank);
                rankRegistry.register(rank);
            });

        } else // Although this probably is not necessary because the plugin is dependent on LuckPerms
            throw new LuckPermsNotFoundException("LuckPerms not found. Please check if you have the plugin installed.");

        registerListeners(
                new PlayTimeListener(this)
        );

        registerCommands(
                new CommandPlayTime(this)
        );

    }

    @SneakyThrows
    private void initDatabase() {
        FileConfiguration config = new ConfigUtils("database").getConfig();
        boolean usingMySQL = config.getBoolean("mysql");
        if (usingMySQL) {
            // Connect to external database
            String host = config.getString("host");
            String port = config.getString("port");
            String database = config.getString("database");
            String username = config.getString("username");
            String password = config.getString("password");
            new PlayTimeExternalDatabaseProvider(host, port, database, username, password);
        } else {
            // Connect to local database
            new PlayTimeLocalDatabaseProvider(this, "database");
        }

        new PlayTimeUserTable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PlayTimeAPI.getInstance().onDisable();
        Database.get().disconnect();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
