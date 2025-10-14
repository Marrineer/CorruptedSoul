package org.marrineer.corruptedSoul.managers;

import org.marrineer.corruptedSoul.CorruptedSoul;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class databaseManager {
    private Connection connection;
    private String dbType;
    private String sqliteFile;
    private String host, dbName, user, password;
    private int port;
    private final CorruptedSoul plugin;
    private boolean cacheLoaded = false;
    private final Map<UUID, Integer> usageCache = new ConcurrentHashMap<>();

    public databaseManager(CorruptedSoul plugin) {
        this.plugin = plugin;
        String type = plugin.getConfig().getString("database.type", "SQLITE");
        dbType = type.toUpperCase();
        if(dbType.equals("MYSQL")) {
            host = plugin.getConfig().getString("database.host", "localhost");
            port = plugin.getConfig().getInt("database.port", 3306);
            dbName = plugin.getConfig().getString("database.name", "soul_db");
            user = plugin.getConfig().getString("database.user", "root");
            password = plugin.getConfig().getString("database.password", "password");
        } else {
            sqliteFile = plugin.getConfig().getString("database.dbfile", "dbFile.db");
        }
    }

    public void connect() throws SQLException {
        if (dbType.equals("SQLITE")) {
            String url = "jdbc:sqlite:plugins/CorruptedSoul/" + sqliteFile;
            connection = DriverManager.getConnection(url);
        } else if (dbType.equals("MYSQL")) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&autoReconnect=true";
            connection = DriverManager.getConnection(url, user, password);
        }
        String createTable = "CREATE TABLE IF NOT EXISTS soulCounter (" +
                "player_uuid TEXT PRIMARY KEY, " +
                "soul_count INTEGER DEFAULT 0" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTable);
        }
        loadCache();
    }
    public void disconnect() {
        flushCache();
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while disconnecting from the database");
        }
    }
    private void loadCache() {
        String sql = "SELECT player_uuid, soul_count FROM soulCounter";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                int usageCount = rs.getInt("soul_count");
                usageCache.put(playerUUID, usageCount);
            }
            cacheLoaded = true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading data to cache", e);
        }
    }
    public int getUsage(UUID playerUUID) {
        if (!cacheLoaded) {
            plugin.getLogger().warning("Cache not loaded yet!");
            return 0;
        }
        return usageCache.getOrDefault(playerUUID, 0);
    }
    public void incrementUsage(UUID playerUUID) {
        if (!cacheLoaded) {
            plugin.getLogger().warning("Cache not loaded yet!");
            return;
        }
        int current = getUsage(playerUUID);
        usageCache.put(playerUUID, current + 1);
    }
    public void flushCache() {
        if (!cacheLoaded || usageCache.isEmpty()) return;
        String sql = "INSERT OR REPLACE INTO playerUsage (player_uuid, usage_count) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Map.Entry<UUID, Integer> entry : usageCache.entrySet()) {
                pstmt.setString(1, entry.getKey().toString());
                pstmt.setInt(2, entry.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error during rollback", ex);
            }
            plugin.getLogger().log(Level.SEVERE, "Error flushing cache to database", e);
        }
    }
    public void startAutoFlush(double intervalSecond) {
        if(!plugin.getConfig().getBoolean("autoSave.enable")) {
            long intervalTicks = (long) (intervalSecond * 20L);
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                    plugin, this::flushCache, intervalTicks, intervalTicks
            );
        }
    }
    public boolean isCacheLoaded() { return cacheLoaded; }
    public int getCacheSize() { return usageCache.size(); }

}
