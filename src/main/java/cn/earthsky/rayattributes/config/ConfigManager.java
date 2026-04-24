package cn.earthsky.rayattributes.config;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.equipment.EquipmentSlot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ConfigManager {

    private final RayAttributes plugin;
    private YamlConfiguration equipmentConfig;
    private YamlConfiguration setsConfig;

    private int maxEnhanceLevel;
    private final Map<Integer, Integer> subStatUnlock = new HashMap<>();
    private final List<Integer> subStatEnhanceLevels = new ArrayList<>();
    private double subStatInitialRatio;
    private double subStatEnhanceRatio;

    private final Map<AttributeType, Double> mainStatMax = new EnumMap<>(AttributeType.class);
    private final Map<AttributeType, Double> subStatMax = new EnumMap<>(AttributeType.class);
    private final Map<EquipmentSlot, List<AttributeType>> slotMainStats = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, List<AttributeType>> slotSubStats = new EnumMap<>(EquipmentSlot.class);

    private double baseCritRate;
    private double maxCritRate;
    private double baseCritDamage;
    private double maxCritDamage;
    private int maxEnergy;
    private int normalAttackEnergyRecovery;
    private double baseHealth;
    private double healthPerLevel;
    private double baseAttack;
    private double attackPerLevel;
    private double baseDefense;
    private double defensePerLevel;

    public ConfigManager(RayAttributes plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        saveResourceIfAbsent("equipment.yml");
        saveResourceIfAbsent("sets.yml");

        File eqFile = new File(plugin.getDataFolder(), "equipment.yml");
        equipmentConfig = YamlConfiguration.loadConfiguration(eqFile);

        File setsFile = new File(plugin.getDataFolder(), "sets.yml");
        setsConfig = YamlConfiguration.loadConfiguration(setsFile);

        loadEquipmentConfig();
        loadBaseConfig();
    }

    private void loadBaseConfig() {
        baseCritRate = plugin.getConfig().getDouble("crit.base-rate", 0.05);
        maxCritRate = plugin.getConfig().getDouble("crit.max-rate", 0.75);
        baseCritDamage = plugin.getConfig().getDouble("crit.base-damage", 1.5);
        maxCritDamage = plugin.getConfig().getDouble("crit.max-damage", 2.5);
        maxEnergy = plugin.getConfig().getInt("energy.max", 100);
        normalAttackEnergyRecovery = plugin.getConfig().getInt("energy.normal-attack-recovery", 10);
        baseHealth = plugin.getConfig().getDouble("base-growth.health.base", 100);
        healthPerLevel = plugin.getConfig().getDouble("base-growth.health.per-level", 20);
        baseAttack = plugin.getConfig().getDouble("base-growth.attack.base", 10);
        attackPerLevel = plugin.getConfig().getDouble("base-growth.attack.per-level", 5);
        baseDefense = plugin.getConfig().getDouble("base-growth.defense.base", 5);
        defensePerLevel = plugin.getConfig().getDouble("base-growth.defense.per-level", 3);
    }

    private void saveResourceIfAbsent(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
    }

    private void loadEquipmentConfig() {
        maxEnhanceLevel = equipmentConfig.getInt("enhance.max-level", 10);
        subStatInitialRatio = equipmentConfig.getDouble("enhance.sub-stat-initial-ratio", 0.25);
        subStatEnhanceRatio = equipmentConfig.getDouble("enhance.sub-stat-enhance-ratio", 0.25);

        ConfigurationSection unlockSec = equipmentConfig.getConfigurationSection("enhance.sub-stat-unlock");
        if (unlockSec != null) {
            for (String key : unlockSec.getKeys(false)) {
                subStatUnlock.put(Integer.parseInt(key), unlockSec.getInt(key));
            }
        }

        subStatEnhanceLevels.addAll(equipmentConfig.getIntegerList("enhance.sub-stat-enhance-levels"));

        loadStatMap("main-stat-max", mainStatMax);
        loadStatMap("sub-stat-max", subStatMax);

        ConfigurationSection slotsSec = equipmentConfig.getConfigurationSection("slots");
        if (slotsSec != null) {
            for (String slotName : slotsSec.getKeys(false)) {
                EquipmentSlot slot = EquipmentSlot.valueOf(slotName);
                slotMainStats.put(slot, parseAttributeList(slotsSec.getStringList(slotName + ".main-stats")));
                slotSubStats.put(slot, parseAttributeList(slotsSec.getStringList(slotName + ".sub-stats")));
            }
        }
    }

    private void loadStatMap(String path, Map<AttributeType, Double> target) {
        ConfigurationSection sec = equipmentConfig.getConfigurationSection(path);
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                target.put(AttributeType.valueOf(key), sec.getDouble(key));
            }
        }
    }

    private List<AttributeType> parseAttributeList(List<String> names) {
        List<AttributeType> result = new ArrayList<>();
        for (String name : names) {
            result.add(AttributeType.valueOf(name));
        }
        return result;
    }

    public YamlConfiguration getSetsConfig() { return setsConfig; }
    public int getMaxEnhanceLevel() { return maxEnhanceLevel; }
    public Map<Integer, Integer> getSubStatUnlock() { return subStatUnlock; }
    public List<Integer> getSubStatEnhanceLevels() { return subStatEnhanceLevels; }
    public double getSubStatInitialRatio() { return subStatInitialRatio; }
    public double getSubStatEnhanceRatio() { return subStatEnhanceRatio; }
    public Map<AttributeType, Double> getMainStatMax() { return mainStatMax; }
    public Map<AttributeType, Double> getSubStatMax() { return subStatMax; }
    public List<AttributeType> getSlotMainStats(EquipmentSlot slot) { return slotMainStats.getOrDefault(slot, Collections.emptyList()); }
    public List<AttributeType> getSlotSubStats(EquipmentSlot slot) { return slotSubStats.getOrDefault(slot, Collections.emptyList()); }
    public double getBaseCritRate() { return baseCritRate; }
    public double getMaxCritRate() { return maxCritRate; }
    public double getBaseCritDamage() { return baseCritDamage; }
    public double getMaxCritDamage() { return maxCritDamage; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getNormalAttackEnergyRecovery() { return normalAttackEnergyRecovery; }
    public double getBaseHealth() { return baseHealth; }
    public double getHealthPerLevel() { return healthPerLevel; }
    public double getBaseAttack() { return baseAttack; }
    public double getAttackPerLevel() { return attackPerLevel; }
    public double getBaseDefense() { return baseDefense; }
    public double getDefensePerLevel() { return defensePerLevel; }
}