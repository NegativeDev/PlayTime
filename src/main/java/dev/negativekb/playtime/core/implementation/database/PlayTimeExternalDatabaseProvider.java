package dev.negativekb.playtime.core.implementation.database;

import dev.negativekb.playtime.api.Database;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class PlayTimeExternalDatabaseProvider extends Database {

    private Connection connection;
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    public PlayTimeExternalDatabaseProvider(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        setInstance(this);

        connect();
    }

    @SneakyThrows
    @Override
    public void connect() {
        if (connection != null)
            return;

        connection = DriverManager.getConnection("jdbc:mysql://" +
                        host + ":" + port + "/" + database + "?useSSL=false",
                username, password);

        getConnection().ifPresent(connection1 ->
                System.out.println("[PlayTime Database] Successfully connected to the external database!"));
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
