package cn.earthsky.rayattributes.energy;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.attribute.PlayerAttributes;
import cn.earthsky.rayattributes.storage.PlayerDataDAO;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EnergyManager {

    private final RayAttributes plugin;
    private final Map<UUID, Double> energyMap = new ConcurrentHashMap<>();
    private BukkitTask displayTask;

    public EnergyManager(RayAttributes plugin) {
        this.plugin = plugin;
        startDisplayTask();
    }

    public void loadPlayer(Player player) {
        PlayerDataDAO.PlayerRecord record = plugin.getAttributeManager().getRecord(player);
        double energy = record != null ? record.getEnergy() : plugin.getConfigManager().getMaxEnergy();
        energyMap.put(player.getUniqueId(), energy);
    }

    public void unloadPlayer(Player player) {
        Double energy = energyMap.remove(player.getUniqueId());
        if (energy != null) {
            PlayerDataDAO.PlayerRecord record = plugin.getAttributeManager().getRecord(player);
            if (record != null) {
                record.setEnergy(energy);
            }
        }
    }

    public void addEnergy(Player player, int baseAmount) {
        PlayerAttributes attrs = plugin.getAttributeManager().getAttributes(player);
        double regenBonus = attrs != null ? attrs.getFinal(AttributeType.ENERGY_REGEN) : 0;
        double actual = baseAmount * (1 + regenBonus);
        double current = getEnergy(player);
        double max = plugin.getConfigManager().getMaxEnergy();
        energyMap.put(player.getUniqueId(), Math.min(current + actual, max));
    }

    public boolean consumeEnergy(Player player, int cost) {
        double current = getEnergy(player);
        if (current < cost) return false;
        energyMap.put(player.getUniqueId(), current - cost);
        return true;
    }

    public double getEnergy(Player player) {
        return energyMap.getOrDefault(player.getUniqueId(), 0.0);
    }

    private void startDisplayTask() {
        displayTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                double energy = getEnergy(player);
                int max = plugin.getConfigManager().getMaxEnergy();
                EnergyBar.display(player, energy, max);
            }
        }, 10L, 10L);
    }

    public void shutdown() {
        if (displayTask != null) {
            displayTask.cancel();
        }
    }
}
