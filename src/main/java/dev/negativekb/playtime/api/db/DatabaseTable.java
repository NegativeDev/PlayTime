package dev.negativekb.playtime.api.db;

import dev.negativekb.playtime.api.Database;
import lombok.Getter;
import lombok.Setter;

public abstract class DatabaseTable {

    @Getter
    private final Database database;
    @Getter @Setter
    private static DatabaseTable instance;
    public DatabaseTable() {
        database = Database.get();
    }

    public abstract void createTable();

}
