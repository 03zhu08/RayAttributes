package cn.earthsky.rayattributes.set;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.attribute.PlayerAttributes;
import cn.earthsky.rayattributes.equipment.EquipmentData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SetManager {

    private final RayAttributes plugin;
    private final Map<String, SetBonus> setBonuses = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> playerSetCounts = new ConcurrentHashMap<>();
    private final Map<UUID, SetEffect> activeEffects = new ConcurrentHashMap<>();

    public SetManager(RayAttributes plugin) {
        this.plugin = plugin;
        loadSets();
    }

    private void loadSets() {
        YamlConfiguration cfg = plugin.getConfigManager().getSetsConfig();
        ConfigurationSection sets = cfg.getConfigurationSection("sets");
        if (sets == null) return;

        for (String id : sets.getKeys(false)) {
            ConfigurationSection sec = sets.getConfigurationSection(id);
            String name = sec.getString("name", id);
            SetBonus bonus = new SetBonus(id, name);

            ConfigurationSection twoPiece = sec.getConfigurationSection("2-piece");
            if (twoPiece != null) {
                for (String key : twoPiece.getKeys(false)) {
                    bonus.addTwoPieceBonus(AttributeType.valueOf(key), twoPiece.getDouble(key));
                }
            }

            ConfigurationSection fourPiece = sec.getConfigurationSection("4-piece");
            if (fourPiece != null) {
                bonus.setFourPieceTrigger(fourPiece.getString("trigger", ""));
                ConfigurationSection effects = fourPiece.getConfigurationSection("effects");
                if (effects != null) {
                    for (String key : effects.getKeys(false)) {
                        if (effects.isConfigurationSection(key)) {
                            ConfigurationSection nested = effects.getConfigurationSection(key);
                            bonus.addFourPieceEffect(key + ".value", nested.getDouble("value"));
                            bonus.addFourPieceEffect(key + ".duration", nested.getDouble("duration"));
                        } else {
                            bonus.addFourPieceEffect(key, effects.getDouble(key));
                        }
                    }
                }
            }

            setBonuses.put(id, bonus);
        }
    }

    public void applySetBonuses(Player player, List<EquipmentData> equipped, PlayerAttributes attrs) {
        Map<String, Integer> counts = new HashMap<>();
        for (EquipmentData eq : equipped) {
            if (eq.getSetId() != null) {
                counts.merge(eq.getSetId(), 1, Integer::sum);
            }
        }
        playerSetCounts.put(player.getUniqueId(), counts);

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            SetBonus bonus = setBonuses.get(entry.getKey());
            if (bonus == null) continue;

            if (entry.getValue() >= 2) {
                for (Map.Entry<AttributeType, Double> stat : bonus.getTwoPieceBonuses().entrySet()) {
                    attrs.addPercent(stat.getKey(), stat.getValue());
                }
            }
        }
    }

    public boolean hasFourPiece(Player player, String setId) {
        Map<String, Integer> counts = playerSetCounts.get(player.getUniqueId());
        return counts != null && counts.getOrDefault(setId, 0) >= 4;
    }

    public SetBonus getSetBonus(String id) {
        return setBonuses.get(id);
    }

    public SetEffect getActiveEffect(Player player) {
        return activeEffects.get(player.getUniqueId());
    }

    public void setActiveEffect(Player player, SetEffect effect) {
        activeEffects.put(player.getUniqueId(), effect);
    }

    public void removePlayer(UUID uuid) {
        playerSetCounts.remove(uuid);
        activeEffects.remove(uuid);
    }

    public Map<String, SetBonus> getAllSets() {
        return setBonuses;
    }
}
