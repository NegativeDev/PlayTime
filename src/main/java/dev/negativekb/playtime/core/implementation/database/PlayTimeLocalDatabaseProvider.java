package dev.negativekb.playtime.core.implementation.database;

import dev.negativekb.playtime.api.Database;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class PlayTimeLocalDatabaseProvider extends Database {

    private Connection connection;
    private final String databaseName;
    private final JavaPlugin plugin;
    public PlayTimeLocalDatabaseProvider(JavaPlugin plugin, String dbName) {
        setInstance(this);
        this.plugin = plugin;
        this.databaseName = dbName;

        connect();
    }

    @SneakyThrows
    @Override
    public void connect() {
        if (connection != null)
            return;

        // Loading local database
        Class.forName("org.sqlite.JDBC");
        String path = plugin.getDataFolder().getPath() + "/" + databaseName + ".db";
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        getConnection().ifPresent(connection1 ->
                System.out.println("[PlayTime Database] Successfully connected to the local database!"));
    }

    @Override
    public void disconnect() {
        getConnection().ifPresent(con -> {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Optional<Connection> getConnection() {
        return Optional.ofNullable(connection);
    }
}
