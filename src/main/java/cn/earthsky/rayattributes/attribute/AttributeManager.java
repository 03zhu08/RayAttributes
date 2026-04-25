package cn.earthsky.rayattributes.attribute;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.config.ConfigManager;
import cn.earthsky.rayattributes.equipment.EquipmentData;
import cn.earthsky.rayattributes.set.SetManager;
import cn.earthsky.rayattributes.storage.PlayerDataDAO;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeManager {

    private final RayAttributes plugin;
    private final Map<UUID, PlayerAttributes> cache = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerDataDAO.PlayerRecord> playerRecords = new ConcurrentHashMap<>();

    public AttributeManager(RayAttributes plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(Player player) {
        PlayerDataDAO.PlayerRecord record = plugin.getPlayerDataDAO().load(player.getUniqueId());
        if (record == null) {
            record = new PlayerDataDAO.PlayerRecord(
                player.getUniqueId(), player.getName(), 1, 0, plugin.getConfigManager().getMaxEnergy()
            );
        }
        playerRecords.put(player.getUniqueId(), record);
        recalculate(player);
    }

    public void unloadPlayer(Player player) {
        PlayerDataDAO.PlayerRecord record = playerRecords.remove(player.getUniqueId());
        if (record != null) {
            plugin.getPlayerDataDAO().save(record);
        }
        cache.remove(player.getUniqueId());
    }

    public void recalculate(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        PlayerDataDAO.PlayerRecord record = getRecord(player);
        int level = record != null ? record.getLevel() : 1;

        PlayerAttributes attrs = new PlayerAttributes();
        attrs.reset();

        attrs.addFlat(AttributeType.HEALTH, cfg.getBaseHealth() + cfg.getHealthPerLevel() * level);
        attrs.addFlat(AttributeType.ATTACK, cfg.getBaseAttack() + cfg.getAttackPerLevel() * level);
        attrs.addFlat(AttributeType.DEFENSE, cfg.getBaseDefense() + cfg.getDefensePerLevel() * level);

        List<EquipmentData> equipped = plugin.getEquipmentManager().getEquippedItems(player);
        for (EquipmentData eq : equipped) {
            applyEquipmentStats(attrs, eq);
        }

        plugin.getSetManager().applySetBonuses(player, equipped, attrs);

        computeFinal(attrs, cfg);
        cache.put(player.getUniqueId(), attrs);

        double maxHealth = attrs.getFinal(AttributeType.HEALTH);
        player.setMaxHealth(Math.max(maxHealth, 1));
    }

    private void applyEquipmentStats(PlayerAttributes attrs, EquipmentData eq) {
        AttributeType mainType = eq.getMainStatType();
        if (mainType.isPercentage()) {
            attrs.addPercent(mainType, eq.getMainStatValue());
        } else {
            attrs.addFlat(mainType, eq.getMainStatValue());
        }

        for (Map.Entry<AttributeType, Double> entry : eq.getSubStats().entrySet()) {
            if (entry.getKey().isPercentage()) {
                attrs.addPercent(entry.getKey(), entry.getValue());
            } else {
                attrs.addFlat(entry.getKey(), entry.getValue());
            }
        }
    }

    private void computeFinal(PlayerAttributes attrs, ConfigManager cfg) {
        double attack = attrs.getFlat(AttributeType.ATTACK) * (1 + attrs.getPercent(AttributeType.ATTACK_PERCENT));
        double health = attrs.getFlat(AttributeType.HEALTH) * (1 + attrs.getPercent(AttributeType.HEALTH_PERCENT));
        double defense = attrs.getFlat(AttributeType.DEFENSE) * (1 + attrs.getPercent(AttributeType.DEFENSE_PERCENT));

        attrs.setFinal(AttributeType.ATTACK, attack);
        attrs.setFinal(AttributeType.HEALTH, health);
        attrs.setFinal(AttributeType.DEFENSE, defense);

        double critRate = cfg.getBaseCritRate() + attrs.getPercent(AttributeType.CRIT_RATE);
        attrs.setFinal(AttributeType.CRIT_RATE, Math.min(critRate, cfg.getMaxCritRate()));

        double critDmg = cfg.getBaseCritDamage() + attrs.getPercent(AttributeType.CRIT_DAMAGE);
        attrs.setFinal(AttributeType.CRIT_DAMAGE, Math.min(critDmg, cfg.getMaxCritDamage()));

        attrs.setFinal(AttributeType.SKILL_DAMAGE, attrs.getPercent(AttributeType.SKILL_DAMAGE));
        attrs.setFinal(AttributeType.ENERGY_REGEN, attrs.getPercent(AttributeType.ENERGY_REGEN));
        attrs.setFinal(AttributeType.COOLDOWN_REDUCTION, attrs.getPercent(AttributeType.COOLDOWN_REDUCTION));
        attrs.setFinal(AttributeType.DAMAGE_REDUCTION, attrs.getPercent(AttributeType.DAMAGE_REDUCTION));
    }

    public PlayerAttributes getAttributes(Player player) {
        return cache.get(player.getUniqueId());
    }

    public PlayerDataDAO.PlayerRecord getRecord(Player player) {
        return playerRecords.get(player.getUniqueId());
    }

    public void setLevel(Player player, int level) {
        PlayerDataDAO.PlayerRecord record = getRecord(player);
        if (record != null) {
            record.setLevel(level);
            recalculate(player);
        }
    }

    public void saveAll() {
        for (PlayerDataDAO.PlayerRecord record : playerRecords.values()) {
            plugin.getPlayerDataDAO().save(record);
        }
    }
}
