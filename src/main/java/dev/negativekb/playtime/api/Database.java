package dev.negativekb.playtime.api;

import lombok.Setter;

import java.sql.Connection;
import java.util.Optional;

public abstract class Database {

    @Setter
    private static Database instance;

    public static Database get() {
        return instance;
    }

    public abstract void connect();

    public abstract void disconnect();

    public abstract Optional<Connection> getConnection();
}
