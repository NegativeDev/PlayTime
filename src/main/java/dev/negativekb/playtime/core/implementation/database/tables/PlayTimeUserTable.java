package dev.negativekb.playtime.core.implementation.database.tables;

import dev.negativekb.playtime.api.db.DatabaseTable;
import dev.negativekb.playtime.core.structure.PlayTimeProfile;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class PlayTimeUserTable extends DatabaseTable {

    public PlayTimeUserTable() {
        setInstance(this);
        createTable();
    }

    @Override
    public void createTable() {
        getDatabase().getConnection().ifPresent(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `players` " +
                                "(`uuid` VARCHAR(50) PRIMARY KEY, `name` VARCHAR(16), `time` LONG, `rank` VARCHAR(50)) "
                );
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addOrUpdate(PlayTimeProfile profile) {
        if (exists(profile.getUuid()))
            update(profile);
        else
            add(profile);
    }

    @SneakyThrows
    public ArrayList<PlayTimeProfile> getProfiles() {
        Optional<Connection> connection = getDatabase().getConnection();
        ArrayList<PlayTimeProfile> profiles = new ArrayList<>();
        if (!connection.isPresent())
            return profiles;

        Connection con = connection.get();
        PreparedStatement statement = con.prepareStatement("SELECT `uuid` FROM players");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String uuid = resultSet.getString("uuid");
            UUID parsed = UUID.fromString(uuid);
            long playTime = getPlayTime(parsed);
            String rank = getRank(parsed);

            PlayTimeProfile profile = new PlayTimeProfile(parsed);
            profile.setPlayTime(playTime);
            profile.setRank(rank);

            profiles.add(profile);
        }
        return profiles;
    }

    @SneakyThrows
    public long getPlayTime(UUID uuid) {
        Optional<Connection> connection = getDatabase().getConnection();
        if (!connection.isPresent())
            return 0;

        if (!exists(uuid))
            return 0;

        Connection con = connection.get();
        PreparedStatement statement = con.prepareStatement("SELECT `time` FROM players WHERE uuid=?");
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();
        long time;
        if (resultSet.next()) {
            time = resultSet.getLong("time");
            return time;
        }
        return 0;
    }

    @SneakyThrows
    public String getRank(UUID uuid) {
        Optional<Connection> connection = getDatabase().getConnection();
        if (!connection.isPresent())
            return null;

        if (!exists(uuid))
            return null;

        Connection con = connection.get();
        PreparedStatement statement = con.prepareStatement("SELECT `rank` FROM `players` WHERE `uuid`=?");
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();
        String rank = "No Rank";
        if (resultSet.next()) {
            rank = resultSet.getString("rank");
        }
        return rank;
    }

    @SneakyThrows
    public String getName(UUID uuid) {
        Optional<Connection> connection = getDatabase().getConnection();
        if (!connection.isPresent())
            return null;

        if (!exists(uuid))
            return null;

        Connection con = connection.get();
        PreparedStatement statement = con.prepareStatement("SELECT `name` FROM `players` WHERE `uuid`=?");
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();
        String rank = "No Name";
        if (resultSet.next()) {
            rank = resultSet.getString("name");
        }
        return rank;
    }

    @SneakyThrows
    public UUID getUUID(String name) {
        Optional<Connection> connection = getDatabase().getConnection();
        if (!connection.isPresent())
            return null;

        Connection con = connection.get();
        PreparedStatement statement = con.prepareStatement("SELECT `uuid` FROM `players` WHERE `name`=?");
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        String uuid = null;
        if (resultSet.next()) {
            uuid = resultSet.getString("uuid");
        }
        return (uuid == null ? null : UUID.fromString(uuid));
    }

    public void delete(PlayTimeProfile profile) {
        getDatabase().getConnection().ifPresent(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM `players` WHERE `uuid`=?");
                statement.setString(1, profile.getUuid().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void add(PlayTimeProfile profile) {
        getDatabase().getConnection().ifPresent(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `players` (`uuid`, `name`, `time`, `rank`) VALUES (?, ?, ?, ?)");
                statement.setString(1, profile.getUuid().toString());
                statement.setString(2, profile.getName());
                statement.setLong(3, profile.getPlayTime());
                statement.setString(4, profile.getRank());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void update(PlayTimeProfile profile) {
        updateRank(profile);
        updateTime(profile);
        updateName(profile);
    }

    @SneakyThrows
    private boolean exists(UUID uuid) {
        Optional<Connection> connection = getDatabase().getConnection();
        if (!connection.isPresent())
            return false;

        Connection con = connection.get();
        PreparedStatement statement = con.prepareStatement("SELECT * FROM `players` WHERE uuid=?");
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    private void updateRank(PlayTimeProfile profile) {
        updateRank(profile.getUuid(), profile.getRank());
    }

    public void updateRank(UUID uuid, String rank) {
        getDatabase().getConnection().ifPresent(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE `players` SET `rank`=? WHERE `uuid`=?");
                statement.setString(1, rank);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateName(PlayTimeProfile profile) {
        getDatabase().getConnection().ifPresent(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE `players` SET `name`=? WHERE `uuid`=?");
                statement.setString(1, profile.getName());
                statement.setString(2, profile.getUuid().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateTime(PlayTimeProfile profile) {
        updateTime(profile.getUuid(), profile.getPlayTime());
    }

    public void updateTime(UUID uuid, long time) {
        getDatabase().getConnection().ifPresent(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE `players` SET `time`=? WHERE `uuid`=?");
                statement.setLong(1, time);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
