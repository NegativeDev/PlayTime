package dev.negativekb.playtime.core.util;

import dev.negativekb.playtime.PlayTime;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtils {
    String name;

    public ConfigUtils(String s) {
        this.name = s;
    }

    public FileConfiguration getConfig() {
        File file = new File(PlayTime.getInstance().getDataFolder(), this.name + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }
}


