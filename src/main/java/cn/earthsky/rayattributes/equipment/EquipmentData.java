package cn.earthsky.rayattributes.equipment;

import cn.earthsky.rayattributes.attribute.AttributeType;

import java.util.*;

public class EquipmentData {

    private EquipmentSlot slot;
    private EquipmentTier tier;
    private int level;
    private AttributeType mainStatType;
    private double mainStatValue;
    private final Map<AttributeType, Double> subStats = new LinkedHashMap<>();
    private String setId;

    public EquipmentData(EquipmentSlot slot, EquipmentTier tier, int level,
                         AttributeType mainStatType, double mainStatValue) {
        this.slot = slot;
        this.tier = tier;
        this.level = level;
        this.mainStatType = mainStatType;
        this.mainStatValue = mainStatValue;
    }

    public void addSubStat(AttributeType type, double value) {
        subStats.put(type, value);
    }

    public EquipmentSlot getSlot() { return slot; }
    public EquipmentTier getTier() { return tier; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public AttributeType getMainStatType() { return mainStatType; }
    public double getMainStatValue() { return mainStatValue; }
    public void setMainStatValue(double mainStatValue) { this.mainStatValue = mainStatValue; }
    public Map<AttributeType, Double> getSubStats() { return subStats; }
    public String getSetId() { return setId; }
    public void setSetId(String setId) { this.setId = setId; }
}
