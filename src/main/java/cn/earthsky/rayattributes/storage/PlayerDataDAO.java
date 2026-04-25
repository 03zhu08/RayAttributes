package cn.earthsky.rayattributes.storage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataDAO {

    private final DatabaseManager db;
    private final Plugin plugin;

    public PlayerDataDAO(DatabaseManager db, Plugin plugin) {
        this.db = db;
        this.plugin = plugin;
    }

    public PlayerRecord load(UUID uuid) {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_data WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PlayerRecord(
                    uuid,
                    rs.getString("name"),
                    rs.getInt("level"),
                    rs.getLong("experience"),
                    rs.getDouble("energy")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(PlayerRecord record) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO player_data (uuid, name, level, experience, energy) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name=?, level=?, experience=?, energy=?")) {
                ps.setString(1, record.getUuid().toString());
                ps.setString(2, record.getName());
                ps.setInt(3, record.getLevel());
                ps.setLong(4, record.getExperience());
                ps.setDouble(5, record.getEnergy());
                ps.setString(6, record.getName());
                ps.setInt(7, record.getLevel());
                ps.setLong(8, record.getExperience());
                ps.setDouble(9, record.getEnergy());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static class PlayerRecord {
        private final UUID uuid;
        private String name;
        private int level;
        private long experience;
        private double energy;

        public PlayerRecord(UUID uuid, String name, int level, long experience, double energy) {
            this.uuid = uuid;
            this.name = name;
            this.level = level;
            this.experience = experience;
            this.energy = energy;
        }

        public UUID getUuid() { return uuid; }
        public String getName() { return name; }
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
        public long getExperience() { return experience; }
        public void setExperience(long experience) { this.experience = experience; }
        public double getEnergy() { return energy; }
        public void setEnergy(double energy) { this.energy = energy; }
    }
}
